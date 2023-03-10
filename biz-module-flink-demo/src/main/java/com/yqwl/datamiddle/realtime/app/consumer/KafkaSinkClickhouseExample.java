package com.yqwl.datamiddle.realtime.app.consumer;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.mysql.OrderDetailWide;
import com.yqwl.datamiddle.realtime.bean.mysql.Orders;
import com.yqwl.datamiddle.realtime.bean.mysql.OrdersDetail;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.ClickHouseUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaSinkClickhouseExample {
    private static final Logger LOGGER = LogManager.getLogger(KafkaSinkClickhouseExample.class);

    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        CheckpointConfig ck = env.getCheckpointConfig();
        //触发保存点的时间间隔, 每隔1000 ms进行启动一个检查点
        ck.setCheckpointInterval(10000);
        //采用精确一次模式
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //检查点保存路径
        //ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint");
        //检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        ck.setCheckpointTimeout(60000);
        //确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        ck.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        ck.setMaxConcurrentCheckpoints(1);
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");

        Props props = PropertiesUtil.getProps("cdc.properties");

        //kafka source
        KafkaSource<String> kafkaOrder = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ORDERS_PREFIX + KafkaTopicConst.MYSQL_TOPIC_NAME)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaStreamOrder = env.fromSource(kafkaOrder, WatermarkStrategy.noWatermarks(), "kafka-source");
        //对数据中进行过滤，订单表
        //订单表过滤后进行实体类转换
        SingleOutputStreamOperator<Orders> mapOrder = kafkaStreamOrder.map(new MapFunction<String, Orders>() {
            @Override
            public Orders map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Orders orders = jo.getObject("after", Orders.class);
                return orders;
            }
        }).uid("mapOrder").name("mapOrder");


        //kafka source
        KafkaSource<String> kafkaOrderDetail = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ORDER_DETAIL_PREFIX + KafkaTopicConst.MYSQL_TOPIC_NAME)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> kafkaStreamOrderDetail = env.fromSource(kafkaOrderDetail, WatermarkStrategy.noWatermarks(), "kafka-source");

        //订单详情过滤后进行实体类转换
        SingleOutputStreamOperator<OrdersDetail> mapOrderDetail = kafkaStreamOrderDetail.map(new MapFunction<String, OrdersDetail>() {
            @Override
            public OrdersDetail map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                OrdersDetail ordersDetail = jo.getObject("after", OrdersDetail.class);
                return ordersDetail;
            }
        }).uid("mapOrderDetail").name("mapOrderDetail");
        LOGGER.info("order表转换成实体类后输出数据");
        LOGGER.info("OrdersDetail明细表转换成实体类后输出数据");
        //4.1 订单指定事件时间字段
        SingleOutputStreamOperator<Orders> orderInfoWithTsDS = mapOrder.assignTimestampsAndWatermarks(
                //水印延迟时间5s
                WatermarkStrategy.<Orders>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Orders>() {
                            @Override
                            public long extractTimestamp(Orders orderInfo, long recordTimestamp) {
                                return orderInfo.getCreateTime();
                            }
                        })).uid("orderInfoWithTsDS").name("orderInfoWithTsDS");
        //4.2 订单详细表指定事件时间字段
        SingleOutputStreamOperator<OrdersDetail> orderDetailWithTsDS = mapOrderDetail.assignTimestampsAndWatermarks(
                //水印延迟时间5s
                WatermarkStrategy.<OrdersDetail>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner(new SerializableTimestampAssigner<OrdersDetail>() {
                                                   @Override
                                                   public long extractTimestamp(OrdersDetail orderDetail, long recordTimestamp) {
                                                       return orderDetail.getCreateTime();
                                                   }
                                               }
                        )
        ).uid("orderDetailWithTsDS").name("orderDetailWithTsDS");

        // 按照订单号进行分组  指定关联的key
        // 订单表根据订单号分组
        KeyedStream<Orders, String> orderInfoKeyedDS = orderInfoWithTsDS.keyBy(Orders::getOrderNo);
        // 明细表根据订单号分组
        KeyedStream<OrdersDetail, String> orderDetailKeyedDS = orderDetailWithTsDS.keyBy(OrdersDetail::getOrderNo);

        // 使用intervalJoin对订单和订单明细进行关联
        // 使用orderInfoKeyedDs join orderDetailKeyedDS
        SingleOutputStreamOperator<OrderDetailWide> orderWideDS = orderInfoKeyedDS
                .intervalJoin(orderDetailKeyedDS) // 双流合并
                //设置时间范围，这个时间的边界需要进行实际情况调整~
                .between(Time.seconds(-2), Time.seconds(2))
                .process(
                        // 流合并后的动作。 每个连接上的数据，会调用这个ProcessElement
                        new ProcessJoinFunction<Orders, OrdersDetail, OrderDetailWide>() {
                            @Override
                            public void processElement(Orders orderInfo, OrdersDetail orderDetail, Context ctx, Collector<OrderDetailWide> out) throws Exception {
                                // 订单和明细拉宽
                                out.collect(new OrderDetailWide(orderInfo, orderDetail));
                            }
                        }
                ).uid("orderWideDS").name("orderWideDS");
        // 测试合并结果
        LOGGER.info("Orders and OrderDetail  Wide 拓宽后数据");
        //关联用户维度 从hbase中获取
        SingleOutputStreamOperator<OrderDetailWide> orderWideWithUserDS = AsyncDataStream.unorderedWait(
                orderWideDS,
                new DimAsyncFunction<OrderDetailWide>("USERS", "USER_ID") {

                    @Override
                    public Object getKey(OrderDetailWide orderWide) {
                        return orderWide.getUserId();
                    }

                    @Override
                    public void join(OrderDetailWide orderWide, JSONObject dimInfoJsonObj) throws Exception {
                        //将维度中 用户表中username 设置给订单宽表中的属性
                        orderWide.setUsername(dimInfoJsonObj.getString("USERNAME"));
                    }
                },
                60, TimeUnit.SECONDS).uid("orderWideWithUserDS").name("orderWideWithUserDS");

        //宽表数据写入clickhouse
        orderWideWithUserDS.addSink(ClickHouseUtil.<OrderDetailWide>getSink("insert into order_detail_dwd values (?,?,?,?,?,?,?,?,?)"))
                .uid("sinkClickhouse").name("sinkClickhouse");
        //orderWideWithUserDS.print();
        try {
            env.execute("KafkaSinkClickhouse");
        } catch (Exception e) {
            LOGGER.error("stream invoke error", e);
        }
    }

}
