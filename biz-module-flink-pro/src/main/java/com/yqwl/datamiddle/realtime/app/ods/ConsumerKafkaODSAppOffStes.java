package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.yqwl.datamiddle.realtime.app.func.DimBatchSink;
import com.yqwl.datamiddle.realtime.app.func.TableProcessDivideFunctionList;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.TimeConst;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.jetty.util.StringUtil;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 此消费者用于指定Topic的偏移量去消费,消费到指定的偏移量即停止
 *               消费kafka下topic名称为:cdc_vlms_unite_oracle中的数据，定时查询 table_process配置表中数据，将数据分流进kafka或mysql中
 *
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
@Deprecated
public class ConsumerKafkaODSAppOffStes {

    public static void main(String[] args) throws Exception {
        // 从偏移量表中读取指定的偏移量模式
        HashMap<TopicPartition, Long> offsetMapStart = new HashMap<>();
        HashMap<TopicPartition, Long> offsetMapEnd = new HashMap<>();
        TopicPartition topicPartitionStart = new TopicPartition(KafkaTopicConst.ORACLE_CDC1108, 0);
        TopicPartition topicPartitionEnd = new TopicPartition(KafkaTopicConst.ORACLE_CDC1108, 0);
        offsetMapStart.put(topicPartitionStart, 41110L);
        offsetMapEnd.put(topicPartitionEnd, 77630L);

        //====================================stream env配置===============================================//
        // Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // flink程序重启，每次之间间隔10s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        // 设置并行度为1
        env.setParallelism(1);
        // 算子拒绝合并
        env.disableOperatorChaining();
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
                .setTopics(KafkaTopicConst.ORACLE_CDC1108)
                .setGroupId(KafkaTopicConst.ORACLE_CDC1108_GROUP)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.offsets(offsetMapStart)) // 指定起始偏移量 60 6-1
                .setBounded(OffsetsInitializer.offsets(offsetMapEnd))           // 终止 60 6-1
                .build();
        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer")
                .uid("ConsumerKafkaODSAppJsonDataStr").name("ConsumerKafkaODSAppJsonDataStr");


        //====================================flink各自算子处理逻辑===============================================//
        // 从Kafka主题中获取消费端
        // 将json数据转化成JSONObject对象
        DataStream<JSONObject> jsonStream = jsonDataStr.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String json) throws Exception {
                return JSON.parseObject(json);
            }
        }).uid("ConsumerKafkaODSAppJsonStream").name("ConsumerKafkaODSAppJsonStream");
        // jsonStream.print("json转化map输出:");
        // 动态分流事实表放到主流，写回到kafka的DWD层；如果维度表不用处理通过侧输出流，写入到mysql
        // 定义输出到mysql的侧输出流标签
        OutputTag<JSONObject> mysqlTag = new OutputTag<JSONObject>(TableProcess.SINK_TYPE_MYSQL) {
        };

        // 事实流写回到Kafka的数据
        SingleOutputStreamOperator<JSONObject> kafkaDS = jsonStream.process(new TableProcessDivideFunctionList(mysqlTag))
                .setParallelism(1)
                .uid("ConsumerKafkaODSAppKafka-divide-data").name("ConsumerKafkaODSAppKafka-divide-data");
        log.info("事实主流数据处理完成");

        // 获取一个kafka生产者将事实数据写回到kafka的dwd层
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
                        // 将保存的对应表名作为kafka的topic name
                        String sinkTopic = jsonObj.getString("sink_table");
                        // 将之前多余添加的key删除
                        jsonObj.remove("sink_table");
                        jsonObj.remove("sink_pk");
                        // 从数据中获取到要输出的主题名称  以及转换成字节
                        return new ProducerRecord<>(sinkTopic, jsonObj.toString().getBytes(Charset.forName("UTF-8")));
                    }
                }
        );

        // kafkaDS.print("kafka结果数据输出:");
        kafkaDS.addSink(kafkaSink).uid("ConsumerKafkaODSAppOds-sink-kafka").name("ConsumerKafkaODSAppOds-sink-kafka");
        // 获取侧输出流 通过mysqlTag得到需要写到mysql的数据
        DataStream<JSONObject> insertMysqlDS = kafkaDS.getSideOutput(mysqlTag);

        // 定义水位线
        SingleOutputStreamOperator<JSONObject> jsonStreamOperator = insertMysqlDS.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        // 定义开窗
        SingleOutputStreamOperator<Map<String, List<JSONObject>>> mysqlProcess = jsonStreamOperator.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(2))).apply(new AllWindowFunction<JSONObject, Map<String, List<JSONObject>>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<JSONObject> elements, Collector<Map<String, List<JSONObject>>> collector) throws Exception {
                List<JSONObject> listTotal = Lists.newArrayList(elements);
                if (CollectionUtils.isNotEmpty(listTotal)) {
                    Map<String, List<JSONObject>> map = new HashMap<>();
                    for (JSONObject element : elements) {
                        //获取目标表
                        String sinkTable = element.getString("sink_table");
                        if (map.containsKey(sinkTable)) {
                            List<JSONObject> list = map.get(sinkTable);
                            //取出真实数据
                            list.add(JsonPartUtil.getAfterObj(element));
                        } else {
                            List<JSONObject> list = new ArrayList<>();
                            //取出真实数据
                            list.add(JsonPartUtil.getAfterObj(element));
                            map.put(sinkTable, list);
                        }
                    }
                    log.info("map里数据数量: {}", map.size());
                    collector.collect(map);
                }
            }
        }).uid("ConsumerKafkaODSAppMysqlProcess").name("ConsumerKafkaODSAppMysqlProcess");


        //=====================================插入mysql-sink===============================================//
        // mysqlProcess.print("mysql结果数据输出:");
        // 将维度数据保存到mysql对应的维度表中
        mysqlProcess.addSink(new DimBatchSink()).setParallelism(1).uid("ConsumerKafkaODSAppDim-sink-batch-mysql").name("ConsumerKafkaODSAppDim-sink-batch-mysql");
        log.info("维表sink到mysql数据库中");

        log.info("流处理程序开始执行");
        env.execute("consumer-kafka-ods");
    }
}
