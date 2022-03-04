package com.yqwl.datamiddle.realtime.cdc;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/3/3 18:28
 * @Version: V1.0
 */
public class MySqlCDCApp {
    public static void main(String[] args) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("192.168.3.82")
                .port(3306)
                .databaseList("datasource_kafka") // set captured database
                .tableList("datasource_kafka.orders", "datasource_kafka.orders_detail", "datasource_kafka.products") // set captured table
                .username("root")
                .password("joker")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // enable checkpoint
        env.enableCheckpointing(3000);

        DataStreamSource<String> source = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source");

        source.print();
        env.execute("Print MySQL Snapshot + Binlog");
    }
}