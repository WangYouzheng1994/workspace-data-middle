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
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class KafkaSinkClickhouseExample2 {
    private static final Logger LOGGER = LogManager.getLogger(KafkaSinkClickhouseExample2.class);

    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint/kafka20");
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");

        Props props = PropertiesUtil.getProps("cdc.properties");
        //kafka source
        KafkaSource<String> kafkaBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.MYSQL_TOPIC_NAME)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaSource = env.fromSource(kafkaBuild, WatermarkStrategy.noWatermarks(), "kafka-source");



        //kafkaSource.print();
        //对数据中进行过滤，订单表
        SingleOutputStreamOperator<String> filterOrder = kafkaSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("database").equals("datasource_kafka") && jo.getString("tableName").equals("orders")) {
                    return true;
                }
                return false;
            }
        }).uid("filterOrder").name("filterOrder");
        //订单表过滤后进行实体类转换
        SingleOutputStreamOperator<Orders> mapOrder = filterOrder.map(new MapFunction<String, Orders>() {
            @Override
            public Orders map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Orders orders = jo.getObject("after", Orders.class);
                return orders;
            }
        }).uid("mapOrder").name("mapOrder");

        //过滤出订单详情表数据
        SingleOutputStreamOperator<String> filterOrderDetail = kafkaSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("database").equals("datasource_kafka") && jo.getString("tableName").equals("orders_detail")) {
                    return true;
                }
                return false;
            }
        }).uid("filterOrderDetail").name("filterOrderDetail");

        //订单详情过滤后进行实体类转换
        SingleOutputStreamOperator<OrdersDetail> mapOrderDetail = filterOrderDetail.map(new MapFunction<String, OrdersDetail>() {
            @Override
            public OrdersDetail map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                OrdersDetail ordersDetail = jo.getObject("after", OrdersDetail.class);
                return ordersDetail;
            }
        }).uid("mapOrderDetail").name("mapOrderDetail");


        mapOrder.addSink(ClickHouseUtil.<Orders>getSink("insert into orders values (?,?,?,?,?,?,?,?)")).uid("OrderAddSink").name("OrderAddSink");
// orderWideWithUserDS.addSink(ClickHouseUtil.<OrderDetailWide>getSink("insert into order_detail_dwd2 values (?,?,?,?,?,?,?,?,?)"))
        mapOrderDetail.addSink(ClickHouseUtil.<OrdersDetail>getSink("insert into orders_detail values (?,?,?,?,?,?)")).uid("OrderDetailAddSink").name("OrderDetailAddSink");






        LOGGER.info("order表转换成实体类后输出数据");
        LOGGER.info("OrdersDetail明细表转换成实体类后输出数据");
        //4.1 订单指定事件时间字段
     /*   SingleOutputStreamOperator<Orders> orderInfoWithTsDS = mapOrder.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Orders>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Orders>() {
                            @Override
                            public long extractTimestamp(Orders orderInfo, long recordTimestamp) {
                                return orderInfo.getCreateTime();
                            }
                        })).uid("orderInfoWithTsDS").name("orderInfoWithTsDS");
        //4.2 订单详细表指定事件时间字段
        SingleOutputStreamOperator<OrdersDetail> orderDetailWithTsDS = mapOrderDetail.assignTimestampsAndWatermarks(
                WatermarkStrategy.<OrdersDetail>forBoundedOutOfOrderness(Duration.ofMinutes(5))
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
                .between(Time.minutes(-5), Time.minutes(5))
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
        orderWideWithUserDS.addSink(ClickHouseUtil.<OrderDetailWide>getSink("insert into order_detail_dwd2 values (?,?,?,?,?,?,?,?,?)"))
        .uid("sinkClickhouse").name("sinkClickhouse");*/
        //sinkSource.print();
        try {
            env.execute("KafkaSinkClickhouse2");
        } catch (Exception e) {
            LOGGER.error("stream invoke error", e);
        }
    }

}
