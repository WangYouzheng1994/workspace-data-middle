package com.yqwl.datamiddle.realtime.app.dim;


import cn.hutool.setting.dialect.Props;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Test {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString(RestOptions.BIND_PORT, "50100-50200"); // 指定访问端口
        //获取执行环境
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        //设置并行度
        env.setParallelism(1);
        // env.disableOperatorChaining();
        //设置checkpoint`
        CheckpointConfig ck = env.getCheckpointConfig();
        //触发保存点的时间间隔, 每隔1000 ms进行启动一个检查点
        ck.setCheckpointInterval(10000);
        //采用精确一次模式
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //检查点保存路径
        ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint");
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");


        Props props = PropertiesUtil.getProps();

        //从kafka消费数据
        KafkaSource<String> kafkasource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SPTC34)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SPTC34_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaStream = env.fromSource(kafkasource, WatermarkStrategy.noWatermarks(), "odssptc34kafka-source");


        env.execute("Test");
    }

}
