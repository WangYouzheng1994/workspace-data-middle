package com.yqwl.datamiddle.realtime.cdc;

import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/3/3 18:23
 * @Version: V1.0
 */
public class OracleCDCApp {
    public static void main(String[] args) throws Exception {
        SourceFunction<String> sourceFunction = OracleSource.<String>builder()
                .hostname("")
                .port(1521)
                .database("XE") // monitor XE database
                .schemaList("inventory") // monitor inventory schema
                .tableList("inventory.products") // monitor products table
                .username("flinkuser")
                .password("flinkpw")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env
                .addSource(sourceFunction)
                .print().setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }
}
