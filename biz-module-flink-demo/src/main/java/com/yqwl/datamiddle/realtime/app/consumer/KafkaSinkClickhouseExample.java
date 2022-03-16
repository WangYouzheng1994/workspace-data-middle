package com.yqwl.datamiddle.realtime.app.consumer;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.OrderDetail;
import com.yqwl.datamiddle.realtime.bean.OrderInfo;
import com.yqwl.datamiddle.realtime.bean.OrderWide;
import com.yqwl.datamiddle.realtime.bean.mysql.*;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.BeanUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ivi.opensource.flinkclickhousesink.ClickHouseSink;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseClusterSettings;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseSinkConst;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaSinkClickhouseExample {
    private static final Logger logger = LogManager.getLogger(KafkaSinkClickhouseExample.class);

    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //2.4 系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.enableCheckpointing(10000, CheckpointingMode.EXACTLY_ONCE);
        //设置clickhouse全局属性
        Props props = PropertiesUtil.getProps("cdc.properties");
        Map<String, String> globalParameters = new HashMap<>();
        // ClickHouse cluster properties
        globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_HOSTS, props.getStr("clickhouse.hostname"));
        //globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_USER, ...);
        //globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_PASSWORD, ...);
        // sink common
        globalParameters.put(ClickHouseSinkConst.TIMEOUT_SEC, "1");
        globalParameters.put(ClickHouseSinkConst.FAILED_RECORDS_PATH, "/home/clickhouse/failed");
        globalParameters.put(ClickHouseSinkConst.NUM_WRITERS, "2");
        globalParameters.put(ClickHouseSinkConst.NUM_RETRIES, "2");
        globalParameters.put(ClickHouseSinkConst.QUEUE_MAX_CAPACITY, "2");
        globalParameters.put(ClickHouseSinkConst.IGNORING_CLICKHOUSE_SENDING_EXCEPTION_ENABLED, "false");

        ParameterTool parameters = ParameterTool.fromMap(globalParameters);
        env.getConfig().setGlobalJobParameters(parameters);

        //kafka source
        KafkaSource<String> kafkaBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.MYSQL_TOPIC_NAME)
                .setStartingOffsets(OffsetsInitializer.earliest())
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

        //对数据中进行过滤，产品表
        SingleOutputStreamOperator<String> filterProducts = kafkaSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("database").equals("datasource_kafka") && jo.getString("tableName").equals("products")) {
                    return true;
                }
                return false;
            }
        }).uid("filterProducts").name("filterProducts");
        //产品表过滤后进行实体类转换
        SingleOutputStreamOperator<Products> mapProducts = filterProducts.map(new MapFunction<String, Products>() {
            @Override
            public Products map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Products products = jo.getObject("after", Products.class);
                return products;
            }
        }).uid("mapProducts").name("mapProducts");

        //对数据中进行过滤，用户表
        SingleOutputStreamOperator<String> filterUser = kafkaSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                if (jo.getString("database").equals("datasource_kafka") && jo.getString("tableName").equals("users")) {
                    return true;
                }
                return false;
            }
        }).uid("filterUser").name("filterUser");
        //用户表过滤后进行实体类转换
        SingleOutputStreamOperator<Users> mapUsers = filterUser.map(new MapFunction<String, Users>() {
            @Override
            public Users map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Users users = jo.getObject("after", Users.class);
                return users;
            }
        }).uid("mapUsers").name("mapUsers");

        mapOrder.print("order表转换成实体类后输出数据");
        mapOrderDetail.print("OrdersDetail明细表转换成实体类后输出数据");
        mapProducts.print("products表转换成实体类后输出数据");
        mapUsers.print("users表转换成实体类后输出数据");
        //4.1 订单指定事件时间字段
        SingleOutputStreamOperator<Orders> orderInfoWithTsDS = mapOrder.assignTimestampsAndWatermarks(
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
        orderWideDS.print("Orders and OrderDetail  Wide 拓宽后数据");

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

        SingleOutputStreamOperator<String> sinkSource = orderWideWithUserDS.map(new MapFunction<OrderDetailWide, String>() {
            @Override
            public String map(OrderDetailWide orderDetailWide) throws Exception {
                return BeanUtil.transferFieldToCsv(orderDetailWide);
            }
        }).uid("sinkSource").name("sinkSource");
        // create props for sink
        Properties propSink = new Properties();
        propSink.put(ClickHouseSinkConst.TARGET_TABLE_NAME, "default.order_detail_dwd");
        propSink.put(ClickHouseSinkConst.MAX_BUFFER_SIZE, "10000");
        ClickHouseSink sink = new ClickHouseSink(propSink);
        sinkSource.addSink(sink);
        sinkSource.print();
        try {
            env.execute("KafkaSinkClickhouse");
        } catch (Exception e) {
            logger.error("An error occurred.", e);
        }
    }

}
