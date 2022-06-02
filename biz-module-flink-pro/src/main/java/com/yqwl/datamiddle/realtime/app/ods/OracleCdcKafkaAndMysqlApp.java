package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.DimBatchSink;
import com.yqwl.datamiddle.realtime.app.func.TableProcessDivideFunctionList;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
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
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消费kafka下同一个topic，将表进行分流
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OracleCdcKafkaAndMysqlApp {

    public static void main(String[] args) throws Exception {
        //Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //flink程序重启10次，每次之间间隔10s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(2);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
      /*  CheckpointConfig ck = env.getCheckpointConfig();
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
        */
        //System.setProperty("HADOOP_USER_NAME", "root");
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);

        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        //properties.put("database.serverTimezone", "UTC");
        properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=YES)(FAILOVER=YES)(ADDRESS=(PROTOCOL=tcp)(HOST=" + props.getStr("cdc.oracle.hostname") + ")(PORT=1521)))(CONNECT_DATA=(SID=" + props.getStr("cdc.oracle.database") + ")))");

        //读取oracle连接配置属性
        SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("cdc.oracle.hostname"))
                .port(props.getInt("cdc.oracle.port"))
                .database(props.getStr("cdc.oracle.database"))
                .schemaList(StrUtil.getStrList(props.getStr("cdc.oracle.schema.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("cdc.oracle.table.list"), ","))
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("oracleSourceStream").name("oracleSourceStream");

        //将json数据转化成JSONObject对象
        DataStream<JSONObject> jsonStream = oracleSourceStream.map(new MapFunction<String, JSONObject>() {
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
        }).slotSharingGroup("otherGroup").uid("jsonStream").name("jsonStream");
        //jsonStream.print("json转化map输出:");
        //动态分流事实表放到主流，写回到kafka的DWD层；如果维度表不用处理通过侧输出流，写入到mysql
        //定义输出到mysql的侧输出流标签
        OutputTag<JSONObject> mysqlTag = new OutputTag<JSONObject>(TableProcess.SINK_TYPE_MYSQL) {
        };

        //事实流写回到Kafka的数据
        SingleOutputStreamOperator<JSONObject> kafkaDS = jsonStream.process(new TableProcessDivideFunctionList(mysqlTag))
                .slotSharingGroup("kafkaGroup")
                .setParallelism(2)
                .uid("kafka-divide-data").name("kafka-divide-data");
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

        //kafkaDS.print("kafka结果数据输出:");
        kafkaDS.addSink(kafkaSink).uid("ods-sink-kafka").name("ods-sink-kafka");
        //获取侧输出流 通过mysqlTag得到需要写到mysql的数据
        DataStream<JSONObject> insertMysqlDS = kafkaDS.getSideOutput(mysqlTag);

        //定义水位线
        SingleOutputStreamOperator<JSONObject> jsonStreamOperator = insertMysqlDS.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        //定义开窗
        SingleOutputStreamOperator<Map<String, List<JSONObject>>> mysqlProcess = jsonStreamOperator.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10))).apply(new AllWindowFunction<JSONObject, Map<String, List<JSONObject>>, TimeWindow>() {
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
                    //System.err.println("map里数据数量:" + map.size());
                    log.info("map里数据数量:" + map.size());
                    collector.collect(map);
                }
            }
        }).slotSharingGroup("mysqlProcessGroup").uid("mysqlProcess").name("mysqlProcess");
        ;

        // mysqlProcess.print("mysql结果数据输出:");
        //将维度数据保存到mysql对应的维度表中
        mysqlProcess.addSink(new DimBatchSink()).setParallelism(1).uid("dim-sink-batch-mysql").name("dim-sink-batch-mysql");
        log.info("维表sink到mysql数据库中");

        log.info("流处理程序开始执行");
        env.execute("consumer-kafka-ods");
    }
}
