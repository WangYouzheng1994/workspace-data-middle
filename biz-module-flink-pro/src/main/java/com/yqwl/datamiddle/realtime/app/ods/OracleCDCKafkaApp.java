package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import io.debezium.connector.oracle.OracleConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 将oracle中其中相关的源表数据cdc同步到kafka的同一个topic中, 名称为:cdc_vlms_unite_oracle
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OracleCDCKafkaApp {

    /**
     * 获取当前上下文环境下 数仓中的分流到clickhouse的表名，用以MySqlCDC抽取。
     * @return
     * @throws Exception
     */
    public static List<String> getSourceTableList() throws Exception {
        Props props = PropertiesUtil.getProps();
        List<Map<String, Object>> sourceTableList = DbUtil.executeQuery("select distinct(source_table) as source_table from table_process where  level_name='ods'" );
        List<String> sourceTable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceTableList)) {
            // sourceTableList
            for (Map<String, Object> sourceTableKV : sourceTableList) {
                String sourceTableName = GetterUtil.getString(sourceTableKV.get("source_table"));
                sourceTable.add(props.getStr("cdc.oracle.schema.list") + "." + sourceTableName);
            }
        }
        return sourceTable;
    }

    public static void main(String[] args) throws Exception {
        // 获取抽取的数据源
        List<String> sourceTableList = getSourceTableList();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //flink程序重启，每次之间间隔10s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, Time.of(30, TimeUnit.SECONDS)));
        // 设置并行度为1
        env.setParallelism(1);

        //====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        // ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        ck.setCheckpointTimeout(60000);
        //确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        ck.setMinPauseBetweenCheckpoints(5000);
        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("oracle_cdc_kafka_app"));
        // 设置savepoint点二级目录位置
        // env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("oracle_cdc_kafka_app"));
        //同一时间只允许进行一个检查点
        //ck.setMaxConcurrentCheckpoints(1);
        System.setProperty("HADOOP_USER_NAME", "yunding");

        log.info("stream流环境初始化完成");

        Props props = PropertiesUtil.getProps();
        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); // 解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   // 解决归档日志数据延迟
        // properties.put("log.mining.archive.log.only.mode", "true");   // 只读archive log
        properties.put("log.mining.batch.size.default", "2000");
        // properties.put("log.mining.batch.size.max", "400000");
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        properties.put("event.processing.failure.handling.mode", "warn");
        properties.put("rac.nodes","10.123.175.197:1250,10.123.175.197:1251");
        properties.put("converters", "aaa");
        properties.put("aaa.type", "com.yqwl.datamiddle.realtime.util.TimestampConverter");
        //properties.put("database.serverTimezone", "UTC");
        //properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(FAILOVER=OFF)(ADDRESS=(PROTOCOL=tcp)(HOST=" +
                props.getStr("cdc.oracle.hostname") + ")(PORT=" +
                props.getInt("cdc.oracle.port") + ")))(CONNECT_DATA=(SID=" +
                props.getStr("cdc.oracle.database") + ")))");

        // 读取oracle连接配置属性
        SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("cdc.oracle.hostname"))
                .port(props.getInt("cdc.oracle.port"))
                .database(props.getStr("cdc.oracle.database"))
                .schemaList(StrUtil.getStrList(props.getStr("cdc.oracle.schema.list"), ","))
                .tableList(sourceTableList.toArray(new String[sourceTableList.size()]))
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();


        log.info("checkpoint设置完成");
        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("OracleCDCKafkaApporacleSourceStream").name("OracleCDCKafkaApporacleSourceStream");

        //获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.CDC_VLMS_UNITE_ORACLE_Latest_0804,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.CDC_VLMS_UNITE_ORACLE_Latest_0804));

        // 输出到kafka
        oracleSourceStream.addSink(sinkKafka).uid("OracleCDCKafkaAppSink-Kafka-cdc_vlms_unite_oracle").name("OracleCDCKafkaAppSink-Kafka-cdc_vlms_unite_oracle");
        log.info("add sink kafka设置完成");
        env.execute("oracle-cdc-kafka");
        log.info("oracle-cdc-kafka job开始执行");
    }
}
