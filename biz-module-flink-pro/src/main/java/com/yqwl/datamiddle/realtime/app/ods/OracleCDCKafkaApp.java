package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

/**
 * @Description: 将oracle所有源表数据cdc到kafka的同一个topic中
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OracleCDCKafkaApp {

    public static void main(String[] args) throws Exception {

        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        properties.put("database.serverTimezone", "UTC");
        properties.put("database.serverTimezone", "Asia/Shanghai");

        //读取oracle连接配置属性
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_PROD);
        SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("cdc.oracle.hostname"))
                .port(props.getInt("cdc.oracle.port"))
                .database(props.getStr("cdc.oracle.database"))
                .schemaList(StrUtil.getStrList(props.getStr("cdc.oracle.schema.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("cdc.oracle.table.list"), ","))
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        log.info("stream流环境初始化完成");
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint");
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");
        DataStreamSource<String> source = env.addSource(oracleSource);


        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.CDC_VLMS_UNITE_ORACLE,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.CDC_VLMS_UNITE_ORACLE));

        //输出到kafka
        source.addSink(sinkKafka).setParallelism(1).uid("sinkKafka").name("sinkKafka");

        source.print("结果数据输出:");
        log.info("add sink kafka设置完成");
        env.execute("oracle-cdc-kafka");
        log.info("oracle-cdc-kafka job开始执行");
    }
}
