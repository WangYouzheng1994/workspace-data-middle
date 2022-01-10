package com.yqwl.datamiddle.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimSink;
import com.yqwl.datamiddle.realtime.app.func.TableProcessFunction;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/12/28 9:37
 * @Version: V1.0
 */
public class BaseDBApp {

    public static void main(String[] args) throws Exception {
        //TODO 0.基本环境准备
        //Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        //设置 CK 相关参数
/*        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        env.setStateBackend(new FsStateBackend("hdfs://192.168.74.100:8020/gmall/flink/checkpoint"));
        System.setProperty("HADOOP_USER_NAME", "root");*/
        //重启策略
        // 如果说没有开启重启Checkpoint，那么重启策略就是noRestart
        // 如果说没有开Checkpoint，那么重启策略会尝试自动帮你进行重启   重启Integer.MaxValue
        //env.setRestartStrategy(RestartStrategies.noRestart());

        //TODO 1.接收 Kafka 数据，过滤空值数据
        //定义消费者组以及指定消费主题
        String topic = "ods_base_db_m";
        String groupId = "ods_base_group";

        //从 Kafka 主题中读取数据
        FlinkKafkaConsumer<String> kafkaSource = KafkaUtil.getKafkaSource(topic, groupId);
        DataStream<String> jsonDstream = env.addSource(kafkaSource);

        //jsonDstream.print("data json:::::::");

        // 对ODS数据进行结构的转换 String->JSONObject
        DataStream<JSONObject> jsonStream = jsonDstream.map(jsonStr -> JSON.parseObject(jsonStr));
        //DataStream<JSONObject> jsonStream = jsonDstream.map(JSON::parseObject);

        // ETL 数据校验
        //过滤为空或者 长度不足的数据
        SingleOutputStreamOperator<JSONObject> filteredDS = jsonStream.filter(
                jsonObject -> {
                    boolean flag = jsonObject.getString("table") != null
                            && jsonObject.getJSONObject("data") != null
                            && jsonObject.getString("data").length() > 3;
                    return flag;
                });
        filteredDS.print("json::::::::");

        //TODO 5. 动态分流  事实表放到主流，写回到kafka的DWD层；如果维度表，通过侧输出流，写入到Hbase
        //5.1定义输出到Hbase的侧输出流标签
        OutputTag<JSONObject> hbaseTag = new OutputTag<JSONObject>(TableProcess.SINK_TYPE_HBASE) {
        };

        //5.2 主流 写回到Kafka的数据
        SingleOutputStreamOperator<JSONObject> kafkaDS = filteredDS.process(
                new TableProcessFunction(hbaseTag)
        );
        //5.3获取侧输出流    写到Hbase的数据
        DataStream<JSONObject> hbaseDS = kafkaDS.getSideOutput(hbaseTag);

        kafkaDS.print("事实>>>>");
        hbaseDS.print("维度>>>>");

        //TODO 6.将维度数据保存到Phoenix对应的维度表中
        hbaseDS.addSink(new DimSink());

        //TODO 7.将事实数据写回到kafka的dwd层
        FlinkKafkaProducer<JSONObject> kafkaSink = KafkaUtil.getKafkaSinkBySchema(
                new KafkaSerializationSchema<JSONObject>() {
                    @Override
                    public void open(SerializationSchema.InitializationContext context) throws Exception {
                        System.out.println("kafka序列化");
                    }

                    @Override
                    public ProducerRecord<byte[], byte[]> serialize(JSONObject jsonObj, @Nullable Long timestamp) {
                        String sinkTopic = jsonObj.getString("sink_table");
                        JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                        return new ProducerRecord<>(sinkTopic, dataJsonObj.toString().getBytes());
                    }
                }
        );

        kafkaDS.addSink(kafkaSink);

        env.execute();
    }
}
