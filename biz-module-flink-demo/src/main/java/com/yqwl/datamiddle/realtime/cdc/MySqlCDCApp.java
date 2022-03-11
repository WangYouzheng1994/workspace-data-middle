package com.yqwl.datamiddle.realtime.cdc;

import cn.hutool.setting.dialect.Props;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/3/3 18:28
 * @Version: V1.0
 */
public class MySqlCDCApp {
    public static void main(String[] args) throws Exception {
        Props props = PropertiesUtil.getProps("cdc.properties");
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("mysql.hostname"))
                .port(props.getInt("mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("mysql.database.list"), ",")) // set captured database
                .tableList(StrUtil.getStrList(props.getStr("mysql.table.list"), ",")) // set captured table
                .username(props.getStr("mysql.username"))
                .password(props.getStr("mysql.password"))
                //.deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // enable checkpoint
        env.setStateBackend(new FsStateBackend("hdfs://192.168.3.95:8020/demo/cdc/checkpoint"));
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        System.setProperty("HADOOP_USER_NAME", "root");

        DataStreamSource<String> source = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL-Source");
       // source.print();

        FlinkKafkaProducer<String> productByOrders = KafkaUtil.getKafkaProductBySchema(props.getStr("kafka.hostname"),
                KafkaTopicConst.mysql_topic_name,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.mysql_topic_name));
        source.addSink(productByOrders).uid("mysql-sink");

        env.execute("Print-MySQL-Binlog");
    }
}