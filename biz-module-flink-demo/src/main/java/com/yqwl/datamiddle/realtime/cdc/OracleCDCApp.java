package com.yqwl.datamiddle.realtime.cdc;

import cn.hutool.setting.dialect.Props;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
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
        Props props = PropertiesUtil.getProps("cdc.properties");
        SourceFunction<String> sourceFunction = OracleSource.<String>builder()
                .hostname(props.getStr("oracle.hostname"))
                .port(props.getInt("oracle.port"))
                .database(props.getStr("oracle.database")) // monitor XE database
                .schemaList(StrUtil.getStrList(props.getStr("oracle.schema.list"), ",")) // monitor inventory schema
                .tableList(StrUtil.getStrList(props.getStr("oracle.table.list"), ",")) // monitor products table
                .username(props.getStr("oracle.username"))
                .password(props.getStr("oracle.password"))
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.addSource(sourceFunction).print(); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }
}
