package com.yqwl.datamiddle.realtime.util;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description: kafka读写工具
 * @Author: WangYouzheng
 * @Date: 2021/12/28 10:11
 * @Version: V1.0
 */
public class KafkaUtil {
    //public static final String KAFKA_SERVER = "10.123.175.195:9092,10.123.175.196:9092,10.123.175.197:9092";
    public static final String KAFKA_SERVER = "192.168.3.195:9092,192.168.3.96:9092,192.168.3.97:9092";
    private static final String DEFAULT_TOPIC = "DEFAULT_DATA";

    /**
     * 从指定的topic和consumer group进行消费。
     *
     * @param topic
     * @param groupId
     * @return
     */
    public static FlinkKafkaConsumer<String> getKafkaSource(String topic, String groupId) {
        Properties prop = new Properties();
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        return new FlinkKafkaConsumer<String>(topic, new SimpleStringSchema(), prop);
    }

    /**
     * 获取序列化器
     *
     * @param topic
     * @return
     */
    public static KafkaSerializationSchema<String> getKafkaSerializationSchema(String topic) {
        return new KafkaSerializationSchema<String>() {
            @Override
            public ProducerRecord<byte[], byte[]> serialize(String element, Long timestamp) {
                return new ProducerRecord<>(
                        topic, // target topic
                        element.getBytes(StandardCharsets.UTF_8)); // record contents
            }
        };
    }

    /**
     * 获取指定主题的生产者。。  通过制定序列化方案的方式
     *
     * @param kafkaSerializationSchema
     * @param <T>
     * @return
     */
    public static <T> FlinkKafkaProducer<T> getKafkaProductBySchema(String server, String topic, KafkaSerializationSchema<T> kafkaSerializationSchema) {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.setProperty(ProducerConfig.RETRIES_CONFIG, "5");
        //设置生产数据的超时时间
        props.setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 5 * 60 * 1000 + "");
        return new FlinkKafkaProducer<T>(topic, kafkaSerializationSchema, props, FlinkKafkaProducer.Semantic.EXACTLY_ONCE, 20);
    }

    /**
     * 获取指定主题的生产者。。  通过制定序列化方案的方式
     *
     * @param kafkaSerializationSchema
     * @param <T>
     * @return
     */
    public static <T> FlinkKafkaProducer<T> getKafkaProductBySchema(KafkaSerializationSchema<T> kafkaSerializationSchema) {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        props.setProperty(ProducerConfig.RETRIES_CONFIG, "5");
        //设置生产数据的超时时间
        props.setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 5 * 60 * 1000 + "");
        return new FlinkKafkaProducer<T>(DEFAULT_TOPIC, kafkaSerializationSchema, props, FlinkKafkaProducer.Semantic.EXACTLY_ONCE, 20);
    }

    /**
     * 通过主题，封装FlinkKafkaProducer
     */
    public static FlinkKafkaProducer<String> getKafkaSink(String server, String topic) {
        return new FlinkKafkaProducer<String>(server, topic, new SimpleStringSchema());
    }

    /**
     * 通过主题，封装FlinkKafkaProducer
     */
    public static FlinkKafkaProducer<String> getKafkaSink(String topic) {
        return new FlinkKafkaProducer<String>(DEFAULT_TOPIC, topic, new SimpleStringSchema());
    }

    public static String getKafkaDDL(String topic, String groupId) {
        String ddl = "'connector' = 'kafka', " +
                " 'topic' = '" + topic + "'," +
                " 'properties.bootstrap.servers' = '" + KAFKA_SERVER + "', " +
                " 'properties.group.id' = '" + groupId + "', " +
                "  'format' = 'json', " +
                "  'scan.startup.mode' = 'latest-offset'  ";
        return ddl;
    }
}
