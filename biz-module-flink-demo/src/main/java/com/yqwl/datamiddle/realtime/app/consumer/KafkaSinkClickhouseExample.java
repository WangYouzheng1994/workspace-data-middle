package com.yqwl.datamiddle.realtime.app.consumer;

import com.yqwl.datamiddle.realtime.enums.KafkaTopicEnum;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseClusterSettings;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseSinkConst;

import java.util.HashMap;
import java.util.Map;

public class KafkaSinkClickhouseExample {
    private static final Logger logger = LogManager.getLogger(KafkaSinkClickhouseExample.class);

    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Map<String, String> globalParameters = new HashMap<>();

        // ClickHouse cluster properties
        globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_HOSTS, "http://hadoop95:8123/");
        //globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_USER, ...);
        //globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_PASSWORD, ...);
        // sink common
        globalParameters.put(ClickHouseSinkConst.TIMEOUT_SEC, "1");
        globalParameters.put(ClickHouseSinkConst.FAILED_RECORDS_PATH, "d:/");
        globalParameters.put(ClickHouseSinkConst.NUM_WRITERS, "2");
        globalParameters.put(ClickHouseSinkConst.NUM_RETRIES, "2");
        globalParameters.put(ClickHouseSinkConst.QUEUE_MAX_CAPACITY, "2");
        globalParameters.put(ClickHouseSinkConst.IGNORING_CLICKHOUSE_SENDING_EXCEPTION_ENABLED, "false");


        //1.14.3版本 kafka写法
        KafkaSource<String> kafkaBuild = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop95:9092")
                .setTopics(KafkaTopicEnum.MYSQL_TOPIC.name())
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaSource = env.fromSource(kafkaBuild, WatermarkStrategy.noWatermarks(), "kafka-source");

        kafkaSource.print();

        //对数据进行过滤 清洗


















/*        // Split up the lines in pairs (2-tuples) containing: (word,1)
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = kafkaSource.flatMap(new Tokenizer())
                // group by the tuple field "0" and sum up tuple field "1"
                .keyBy(tuple2 -> tuple2.f0)
                .sum(1).uid("map-word-count");

        sum.addSink(JdbcSink.sink(
                "insert into word_count (name, num) values (?, ?)",
                (statement, tuple2) -> {
                    statement.setString(1, tuple2.f0);
                    statement.setInt(2, tuple2.f1);
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://192.168.3.4:3306/test")
                        .withDriverName("com.mysql.cj.jdbc.Driver")
                        .withUsername("fengqiwulian")
                        .withPassword("fengqiwulian")
                        .build()
        )).uid("sink-word-count");*/

        try {
            env.execute("KafkaFlinkWordCount");
        } catch (Exception e) {
            logger.error("An error occurred.", e);
        }
    }

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split(",");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
