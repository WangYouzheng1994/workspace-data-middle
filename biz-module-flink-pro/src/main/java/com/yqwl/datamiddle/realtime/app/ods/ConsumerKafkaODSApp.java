package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimMysqlSink;
import com.yqwl.datamiddle.realtime.app.func.TableProcessDivideFunction;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

/**
 * @Description: 消费kafka下同一个topic，将表进行分流
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class ConsumerKafkaODSApp {

    public static void main(String[] args) throws Exception {
        //Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSourceBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.CDC_VLMS_UNITE_ORACLE)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //将kafka中源数据转化成DataStream
        DataStreamSource<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer");
        //从Kafka主题中获取消费端
        log.info("从kafka的主题:" + KafkaTopicConst.CDC_VLMS_UNITE_ORACLE + "中获取的要处理的数据");
        //将json数据转化成JSONObject对象
        DataStream<JSONObject> jsonStream = jsonDataStr.map(JSON::parseObject);
        //动态分流事实表放到主流，写回到kafka的DWD层；如果维度表不用处理通过侧输出流，写入到mysql
        //定义输出到mysql的侧输出流标签
        OutputTag<JSONObject> mysqlTag = new OutputTag<JSONObject>(TableProcess.SINK_TYPE_MYSQL) {
        };
        //事实流写回到Kafka的数据
        SingleOutputStreamOperator<JSONObject> kafkaDS = jsonStream.process(new TableProcessDivideFunction(mysqlTag)).uid("kafka-divide-data").name("kafka-divide-data");
        log.info("事实主流数据处理完成");
        //获取侧输出流 通过mysqlTag得到需要写到mysql的数据
        DataStream<JSONObject> hbaseDS = kafkaDS.getSideOutput(mysqlTag);
        //将维度数据保存到mysql对应的维度表中
        hbaseDS.addSink(new DimMysqlSink()).uid("dim-sink-mysql").name("dim-sink-mysql");
        log.info("维表sink到mysql数据库中");

        //获取一个kafka生产者将事实数据写回到kafka的dwd层
        FlinkKafkaProducer<JSONObject> kafkaSink = KafkaUtil.getKafkaProductBySchema(

                new KafkaSerializationSchema<JSONObject>() {
                    @Override
                    public void open(SerializationSchema.InitializationContext context) throws Exception {
                        log.info("kafka序列化");
                    }
                    /**
                     * @param jsonObj 传递给这个生产者的源数据 即flink的流数据
                     * @param timestamp
                     * @return
                     */
                    @Override
                    public ProducerRecord<byte[], byte[]> serialize(JSONObject jsonObj, @Nullable Long timestamp) {
                        //将保存的对应表名作为kafka的topic name
                        String sinkTopic = jsonObj.getString("sink_table");
                        //将之前多余添加的key删除
                        jsonObj.remove("sink_table");
                        jsonObj.remove("sink_pk");
                        // 从数据中获取到要输出的主题名称  以及转换成字节
                        return new ProducerRecord<>(sinkTopic, jsonObj.toString().getBytes());
                    }
                }
        );

        kafkaDS.addSink(kafkaSink).uid("ods-sink-kafka").name("ods-sink-kafka");
        ;
        log.info("事实表sink到流处理环境");
        env.execute("consumer-kafka-ods");
        log.info("事实表sink到流处理环境");
    }
}
