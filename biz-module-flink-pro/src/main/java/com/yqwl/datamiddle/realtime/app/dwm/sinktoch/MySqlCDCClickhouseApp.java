package com.yqwl.datamiddle.realtime.app.dwm.sinktoch;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.DimBatchSink;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
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
 * @Description: cdc 数仓自身的mysql到clickhouse
 * @Author: WangYouzheng
 * @Date: 2022/7/20 13:41
 * @Version: V1.0
 */
@Slf4j
public class MySqlCDCClickhouseApp {

    /**
     * 获取当前上下文环境下 数仓中的分流到clickhouse的表名，用以MySqlCDC抽取。
     * @return
     * @throws Exception
     */
    public static List<String> getSourceTableList() throws Exception {
        Props props = PropertiesUtil.getProps();
        List<Map<String, Object>> sourceTableList = DbUtil.executeQuery("select distinct(source_table) as source_table from table_process where sink_type = 'clickhouse' and level_name='dwm'" );
        List<String> sourceTable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceTableList)) {
            // sourceTableList
            for (Map<String, Object> sourceTableKV : sourceTableList) {
                String sourceTableName = GetterUtil.getString(sourceTableKV.get("source_table"));
                sourceTable.add(props.getStr("cdc.mysql.database.list") + "." + sourceTableName);
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
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("mysql_cdc_clichouse_app"));
        // 设置savepoint点二级目录位置
        // env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("oracle_cdc_kafka_app"));
        //同一时间只允许进行一个检查点
        //ck.setMaxConcurrentCheckpoints(1);
        System.setProperty("HADOOP_USER_NAME", "root");

        log.info("stream流环境初始化完成");

        Properties properties = new Properties();
        // 遇到错误跳过
        properties.setProperty("debezium.inconsistent.schema.handing.mode","warn");
        properties.setProperty("debezium.event.deserialization.failure.handling.mode","warn");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        //读取oracle连接配置属性
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                // .tableList("data_flink.dwm_vlms_sptb02")
                // .tableList("data_middle_flink.dwm_vlms_sptb02")
                .tableList(sourceTableList.toArray(new String[sourceTableList.size()]))
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .debeziumProperties(properties)
                .distributionFactorUpper(10.0d)  // 针对cdc的错误算法的更改
                .build();

        log.info("checkpoint设置完成");
        SingleOutputStreamOperator<String> mysqlSourceStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySqlCDCClickhouseApp").uid("MySqlCDCClickhouseApp").name("MySqlCDCClickhouseApp");


        //定义水位线
        // SingleOutputStreamOperator<String> jsonStreamOperator = mysqlSourceStream.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        mysqlSourceStream.print();
        //输出到clickhouse
        // mysqlSourceStream.addSink(new DimBatchSink()).uid("OracleCDCKafkaAppSink-Kafka-cdc_vlms_unite_oracle").name("OracleCDCKafkaAppSink-Kafka-cdc_vlms_unite_oracle");
        log.info("add sink kafka设置完成");
        env.execute("oracle-cdc-kafka");
        log.info("oracle-cdc-kafka job开始执行");
    }
}
