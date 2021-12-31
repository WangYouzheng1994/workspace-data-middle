package com.yqwl.datamiddle.realtime.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/12/28 9:37
 * @Version: V1.0
 */
public class BaseDBApp {
    //定义用户行为主题信息
    private static final String TOPIC_START ="dwd_start_log";
    private static final String TOPIC_PAGE ="dwd_page_log";
    private static final String TOPIC_DISPLAY ="dwd_display_log";

    public static void main(String[] args) throws Exception {
    //TODO 0.基本环境准备
        //Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        //设置 CK 相关参数
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        env.setStateBackend(new
                FsStateBackend("hdfs://hadoop202:8020/gmall/flink/checkpoint"));
        System.setProperty("HADOOP_USER_NAME", "atguigu");
        //TODO 1.接收 Kafka 数据，过滤空值数据
        //定义消费者组以及指定消费主题
        String topic = "ods_base_db_m";
        String groupId = "ods_base_group";
        //从 Kafka 主题中读取数据
        FlinkKafkaConsumer<String> kafkaSource = KafkaUtil.getKafkaSource(topic,groupId);
        DataStream<String> jsonDstream = env.addSource(kafkaSource);
        //jsonDstream.print("data json:::::::");
        //对数据进行结构的转换 String->JSONObject
        DataStream<JSONObject> jsonStream = jsonDstream.map(jsonStr ->
                JSON.parseObject(jsonStr));
        //DataStream<JSONObject> jsonStream = jsonDstream.map(JSON::parseObject);

        // ETL 数据校验
        //过滤为空或者 长度不足的数据
        SingleOutputStreamOperator<JSONObject> filteredDstream = jsonStream.filter(
                jsonObject -> {
                    boolean flag = jsonObject.getString("table") != null
                            && jsonObject.getJSONObject("data") != null
                            && jsonObject.getString("data").length() > 3;
                    return flag;
                }) ;
        filteredDstream.print("json::::::::");
        env.execute();
    }
}
