package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/3/3 18:23
 * @Version: V1.0
 */
public class OracleCDCApp {
    private static final Logger LOGGER = LogManager.getLogger(OracleCDCApp.class);
    public static void main(String[] args) throws Exception {
//        'debezium.log.mining.strategy'='online_catalog',
//                'debezium.log.mining.continuous.mine'='true'
        Props props = PropertiesUtil.getProps("cdc.properties");
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        properties.put("database.serverTimezone", "UTC");
        properties.put("database.serverTimezone", "Asia/Shanghai");


        SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("oracle.hostname"))
                .port(props.getInt("oracle.port"))
                .database(props.getStr("oracle.database")) // monitor XE database
                .schemaList(StrUtil.getStrList(props.getStr("oracle.schema.list"), ",")) // monitor inventory schema
                .tableList(StrUtil.getStrList(props.getStr("oracle.table.list"), ",")) // monitor products table
                .username(props.getStr("oracle.username"))
                .password(props.getStr("oracle.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint");
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");

        DataStreamSource<String> source = env.addSource(oracleSource);// use parallelism 1 for sink to keep message ordering
        source.print();

        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(props.getStr("kafka.hostname"),
                KafkaTopicConst.ORACLE_TOPIC_NAME,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.ORACLE_TOPIC_NAME));
        source.addSink(sinkKafka).uid("sinkKafka").name("sinkKafka");
        env.execute("oracle-cdc-kafka");
//        LOGGER.info("出现了~");
    }
}
