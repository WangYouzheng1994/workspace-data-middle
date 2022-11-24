package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.yqwl.datamiddle.realtime.app.func.SimpleHourOfDayUpdateTotalSink;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.reflections.Reflections.log;

/**
 * @Description: 此类用于过滤Kafka的数据分为时间
 * @Author: XiaoFeng
 * @Date: 2022/9/7 19:48
 * @Version: V1.0
 */
public class FilterConsumerKafka {
    public static void main(String[] args) throws Exception {
        // 1661788800000 2022.8.30 -
        // 1659283200000 2022.8.01
        // 1654012800000 2022.6.01
        // 1656604800000 2022.7.01
        // =  2505600000 30天的毫秒数
        // =  7776000000 90天的毫秒数
        Long before30daysTime = System.currentTimeMillis()-2505600000L;
        Long before90daysTime = System.currentTimeMillis()-7776000000L;
        Long before60daysTime = System.currentTimeMillis()-5184000000L;
        // 从偏移量表中读取指定的偏移量模式
        HashMap<TopicPartition, Long> offsetMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(KafkaTopicConst.ORACLE_CDC1110, 0);
        offsetMap.put(topicPartition, 500L);


        //====================================stream env配置===============================================//
        // Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // flink程序重启，每次之间间隔10s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        // 设置并行度为1
        env.setParallelism(1);
        log.info("初始化流处理环境完成");

        //====================================checkpoint配置===============================================//
        // 设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        // ck.setCheckpointTimeout(60000);
        // 确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        // ck.setMinPauseBetweenCheckpoints(500);
        // 同一时间只允许进行一个检查点
        // ck.setMaxConcurrentCheckpoints(1);
        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("consumer_kafka_ods_app"));
        // 设置savepoint点二级目录位置
        // env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("consumer_kafka_ods_app"));
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");

        //====================================kafka 消费端配置===============================================//
        // kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSourceBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ORACLE_CDC1110)
                .setGroupId(KafkaTopicConst.ORACLE_CDC1110)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                // .setStartingOffsets(OffsetsInitializer.offsets(offsetMap)) // 指定起始偏移量 60 6-1
                // .setBounded(OffsetsInitializer.offsets(offsetMap)) // 终止 60 6-1
                .build();
        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer")
                .uid("FilterConsumerKafkaJsonDataStr").name("FilterConsumerKafkaJsonDataStr");
        jsonDataStr.addSink(new SimpleHourOfDayUpdateTotalSink<String>()).uid("FilterConsumerKafkaUpdateMysql").name("FilterConsumerKafkaUpdateMysql");
        env.execute();


    }
}
