package com.yqwl.datamiddle.realtime.app.consumer;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.bean.mysql.Products;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ivi.opensource.flinkclickhousesink.ClickHouseSink;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseClusterSettings;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseSinkConst;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaSinkClickhouseExample {
    private static final Logger logger = LogManager.getLogger(KafkaSinkClickhouseExample.class);

    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //设置clickhouse全局属性
        Props props = PropertiesUtil.getProps("cdc.properties");
        Map<String, String> globalParameters = new HashMap<>();
        // ClickHouse cluster properties
        globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_HOSTS, props.getStr("clickhouse.hostname"));
        //globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_USER, ...);
        //globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_PASSWORD, ...);
        // sink common
        globalParameters.put(ClickHouseSinkConst.TIMEOUT_SEC, "1");
        globalParameters.put(ClickHouseSinkConst.FAILED_RECORDS_PATH, "/home/clickhouse/failed");
        globalParameters.put(ClickHouseSinkConst.NUM_WRITERS, "2");
        globalParameters.put(ClickHouseSinkConst.NUM_RETRIES, "2");
        globalParameters.put(ClickHouseSinkConst.QUEUE_MAX_CAPACITY, "2");
        globalParameters.put(ClickHouseSinkConst.IGNORING_CLICKHOUSE_SENDING_EXCEPTION_ENABLED, "false");

        ParameterTool parameters = ParameterTool.fromMap(globalParameters);
        env.getConfig().setGlobalJobParameters(parameters);

        //kafka source
        KafkaSource<String> kafkaBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.mysql_topic_name)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaSource = env.fromSource(kafkaBuild, WatermarkStrategy.noWatermarks(), "kafka-source");

        //kafkaSource.print();
        SingleOutputStreamOperator<String> filter = kafkaSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("database").equals("datasource_kafka") && jo.getString("tableName").equals("products")) {
                    return true;
                }
                return false;
            }
        });


        SingleOutputStreamOperator<String> map = filter.map(new MapFunction<String, String>() {
            @Override
            public String map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Products after = jo.getObject("after", Products.class);
                return Products.convertToCsv(after);
            }
        });

        // create props for sink
        Properties propSink = new Properties();
        propSink.put(ClickHouseSinkConst.TARGET_TABLE_NAME, "default.products");
        propSink.put(ClickHouseSinkConst.MAX_BUFFER_SIZE, "10000");
        ClickHouseSink sink = new ClickHouseSink(propSink);
        map.addSink(sink);
        try {
            env.execute("KafkaSinkClickhouse");
        } catch (Exception e) {
            logger.error("An error occurred.", e);
        }
    }

}
