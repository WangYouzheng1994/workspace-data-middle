package com.yqwl.datamiddle.realtime.cdc;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/3/3 18:28
 * @Version: V1.0
 */
public class MySqlCDCApp {
    private static final Logger LOGGER = LogManager.getLogger(MySqlCDCApp.class);
    public static void main(String[] args) throws Exception {

        Props props = PropertiesUtil.getProps("cdc.properties");
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("mysql.hostname"))
                .port(props.getInt("mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("mysql.database.list"), ",")) // set captured database
                .tableList(StrUtil.getStrList(props.getStr("mysql.table.list"), ",")) // set captured table
                .username(props.getStr("mysql.username"))
                .password(props.getStr("mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // enable checkpoint
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint");
        //检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        ck.setCheckpointTimeout(60000);
        //确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        ck.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        ck.setMaxConcurrentCheckpoints(1);
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");

        DataStreamSource<String> source = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL-Source");
        SingleOutputStreamOperator<String> orderFilter = source.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("tableName").equals("orders")) {
                    return true;
                }
                return false;
            }
        }).uid("orderFilter").name("orderFilter");

        SingleOutputStreamOperator<String> orderDetailFilter = source.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("tableName").equals("orders_detail")) {
                    return true;
                }
                return false;
            }
        }).uid("orderDetailFilter").name("orderDetailFilter");

        //order表一个topic
        FlinkKafkaProducer<String> sinkKafkaOrder = KafkaUtil.getKafkaProductBySchema(props.getStr("kafka.hostname"),
                KafkaTopicConst.ORDERS_PREFIX + KafkaTopicConst.MYSQL_TOPIC_NAME,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.ORDERS_PREFIX + KafkaTopicConst.MYSQL_TOPIC_NAME));
        orderFilter.addSink(sinkKafkaOrder).uid("sinkKafkaOrder").name("sinkKafkaOrder");

        //orders_detail表一个topic
        FlinkKafkaProducer<String> sinkKafkaOrderDetail = KafkaUtil.getKafkaProductBySchema(props.getStr("kafka.hostname"),
                KafkaTopicConst.ORDER_DETAIL_PREFIX + KafkaTopicConst.MYSQL_TOPIC_NAME,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.ORDER_DETAIL_PREFIX + KafkaTopicConst.MYSQL_TOPIC_NAME));
        orderDetailFilter.addSink(sinkKafkaOrderDetail).uid("sinkKafkaOrderDetail").name("sinkKafkaOrderDetail");

        env.execute("mysql-cdc-kafka");
        LOGGER.info("mysql-cdc-kafka 正常执行");
    }
}