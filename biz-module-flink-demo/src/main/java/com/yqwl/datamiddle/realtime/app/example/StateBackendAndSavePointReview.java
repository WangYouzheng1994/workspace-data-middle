package com.yqwl.datamiddle.realtime.app.example;

import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
 
import java.util.Properties;
 
public class StateBackendAndSavePointReview {
    public static void main(String[] args) throws Exception{
        System.setProperty("HADOOP_USER_NAME","root");   //以root的身份访问hdfs
        // 1.获取flink流计算的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
 
        // 2. 容错相关配置
        // 2.1 开启CheckPointing
        env.enableCheckpointing(10000);
        // 2.2 设置重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, Time.seconds(2)));
        //2.3 设置Checkpoint模式（与Kafka整合，一定要设置Checkpoint模式为Exactly_Once）
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //2.4 系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //2.5 StateBackend配置：存储在hdfs中并指定checkpoints保存的具体位置
        // 会覆盖flink-conf.yaml中的相关配置
        env.setStateBackend(new FsStateBackend("hdfs://192.168.3.95:8020/demo/cdc/checkpoint"));

        //kafka source
        KafkaSource<String> kafkaBuild = KafkaSource.<String>builder()
                .setBootstrapServers("192.168.3.95:9092")
                .setTopics(KafkaTopicConst.MYSQL_TOPIC_NAME)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaSource = env.fromSource(kafkaBuild, WatermarkStrategy.noWatermarks(), "kafka-source");

        //5 sink输出
        kafkaSource.print();
 
        // 6. 执行程序
        env.execute("StateBackendAndSavePoint");
    }
}