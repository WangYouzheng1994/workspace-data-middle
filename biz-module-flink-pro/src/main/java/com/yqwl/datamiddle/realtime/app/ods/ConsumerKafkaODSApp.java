package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yqwl.datamiddle.realtime.app.func.DimBatchSink;
import com.yqwl.datamiddle.realtime.app.func.TableProcessDivideFunction;
import com.yqwl.datamiddle.realtime.app.func.TableProcessDivideFunctionList;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
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
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.*;

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
        env.setParallelism(2);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        ck.setCheckpointTimeout(60000);
        //确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        ck.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        ck.setMaxConcurrentCheckpoints(1);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        //System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);

        KafkaSource<String> kafkaSourceBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.CDC_VLMS_UNITE_ORACLE)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer")
                .uid("jsonDataStr").name("jsonDataStr");

        //从Kafka主题中获取消费端
        log.info("从kafka的主题:" + KafkaTopicConst.CDC_VLMS_UNITE_ORACLE + "中获取的要处理的数据");
        SingleOutputStreamOperator<String> filter = jsonDataStr.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String json) throws Exception {
                JSONObject jsonObj = JSON.parseObject(json);
                String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);
                if ("sptb02".equalsIgnoreCase(tableNameStr)) {
                    return true;
                }
                return false;
            }
        });
        //将json数据转化成JSONObject对象
        DataStream<JSONObject> jsonStream = jsonDataStr.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String json) throws Exception {
                JSONObject jsonObj = JSON.parseObject(json);
                //获取cdc进入kafka的时间
                String tsStr = JsonPartUtil.getTsStr(jsonObj);
                //获取after数据
                JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                afterObj.put("WAREHOUSE_CREATETIME", tsStr);
                afterObj.put("WAREHOUSE_UPDATETIME", tsStr);
                jsonObj.put("after", afterObj);
                return jsonObj;
            }
        });
        //jsonStream.print("json转化map输出:");
        //动态分流事实表放到主流，写回到kafka的DWD层；如果维度表不用处理通过侧输出流，写入到mysql
        //定义输出到mysql的侧输出流标签
        OutputTag<JSONObject> mysqlTag = new OutputTag<JSONObject>(TableProcess.SINK_TYPE_MYSQL) {
        };

        //事实流写回到Kafka的数据
        SingleOutputStreamOperator<JSONObject> kafkaDS = jsonStream.process(new TableProcessDivideFunctionList(mysqlTag)).uid("kafka-divide-data").name("kafka-divide-data");
        log.info("事实主流数据处理完成");

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

        kafkaDS.print("kafka结果数据输出:");
        kafkaDS.addSink(kafkaSink).setParallelism(1).uid("ods-sink-kafka").name("ods-sink-kafka");
        //获取侧输出流 通过mysqlTag得到需要写到mysql的数据
        DataStream<JSONObject> insertMysqlDS = kafkaDS.getSideOutput(mysqlTag);

        //定义水位线
        SingleOutputStreamOperator<JSONObject> jsonStreamOperator = insertMysqlDS.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        //定义开窗
        SingleOutputStreamOperator<Map<String, List<JSONObject>>> mysqlProcess = jsonStreamOperator.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<JSONObject, Map<String, List<JSONObject>>, TimeWindow>() {
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
                    System.err.println("map里数据数量:" + map.size());
                    log.info("map里数据数量:" + map.size());
                    collector.collect(map);
                }
            }
        });

        mysqlProcess.print("mysql结果数据输出:");
        //将维度数据保存到mysql对应的维度表中
        mysqlProcess.addSink(new DimBatchSink()).setParallelism(1).uid("dim-sink-batch-mysql").name("dim-sink-batch-mysql");
        log.info("维表sink到mysql数据库中");

        log.info("流处理程序开始执行");
        env.execute("consumer-kafka-ods");
    }
}
