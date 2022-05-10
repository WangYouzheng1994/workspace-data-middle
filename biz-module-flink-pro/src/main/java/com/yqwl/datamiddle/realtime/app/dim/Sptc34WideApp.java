package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.setting.dialect.Props;
import com.yqwl.datamiddle.realtime.bean.Sptc34Wide;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.text.SimpleDateFormat;


/**
 * 从kafka读取sptc34 的topic   并将 sptc34表拓宽字段生成新的维度表   并将生成的维度宽表传到 mysql中
 */
public class Sptc34WideApp {
    public static void main(String[] args) throws Exception{
        //获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度
        env.setParallelism(1);
        //设置checkpoint
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

        Props props = PropertiesUtil.getProps("cdc.properties");

        //从kafka消费数据
        KafkaSource<String> kafkasource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SPTC34)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SPTC34_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();


        System.err.println(kafkasource);
        DataStreamSource<String> kafkaStream = env.fromSource(kafkasource, WatermarkStrategy.noWatermarks(), "odssptc34kafka-source");
//        kafkaStream.print();
        //DataStreamSource<String> kafkaSource = env.addSource(KafkaUtil.getKafkaSource(KafkaTopicConst.ODS_VLMS_SPTC34,KafkaTopicConst.ODS_VLMS_SPTC34_GROUP));


//        System.err.println("1111111-------kafkaSource");
        //sptc34表转换成实体类    sptc34表里面有176条数据匹配不上  已经使用空串代替
        SingleOutputStreamOperator<Sptc34Wide> Sptc34MapSteeam = kafkaStream.map(new MapFunction<String, Sptc34Wide>() {
            @Override
            public Sptc34Wide map(String value) throws Exception {
                Sptc34Wide sptc34Wide = JsonPartUtil.getAfterObj(value, Sptc34Wide.class);
                String vsqsxdm = StringUtils.join(sptc34Wide.getVsqdm(), sptc34Wide.getVsxdm());
                sptc34Wide.setVsqsxdm(vsqsxdm);
//                System.out.println(vsqsxdm);
                System.out.println(sptc34Wide.toString());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                return sptc34Wide;
            }
        }).uid("odsSptc34").name("odsSptc34");





//        kafkaStream.print();
//        //连接mysql数据库,将数据存到mysql中
//        kafkaStream.addSink(JDBCSink.getSink("insert into dim_vlms_sptc34 values (?,?,?,?,?,?,?,?,?,?," +
//                        "?,?,?,?,?,?,?,?,?,?," +
//                        "?,?,?,?,?,?,?,?,?,?," +
//                        "?,?,?,?,?,?,?,?,?,?," +
//                        "?,?,?,?,?,?)"))
//                .uid("sptc34sinkMysql").name("sptc34sinkMysql");


        env.execute("Sptc34WideApp");
    }


}
