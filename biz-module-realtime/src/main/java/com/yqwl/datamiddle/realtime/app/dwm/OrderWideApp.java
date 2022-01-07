package com.yqwl.datamiddle.realtime.app.dwm;

import com.alibaba.fastjson.JSON;
import com.yqwl.datamiddle.realtime.bean.OrderDetail;
import com.yqwl.datamiddle.realtime.bean.OrderInfo;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.phoenix.shaded.com.ibm.icu.text.SimpleDateFormat;

/**
 * @Description: 订单拉宽 dwm
 * @Author: WangYouzheng
 * @Date: 2022/1/7 9:40
 * @Version: V1.0
 */
public class OrderWideApp {
    public static void main(String[] args) throws Exception {
        //TODO 0.基本环境准备
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度读取 kafka 分区数据
        env.setParallelism(4);
         //设置 CK 相关配置
         env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
         env.getCheckpointConfig().setCheckpointTimeout(60000);
         StateBackend fsStateBackend = new
                 FsStateBackend("hdfs://hadoop100:8020/gmall/flink/checkpoint/OrderWideApp");
         env.setStateBackend(fsStateBackend);
         System.setProperty("HADOOP_USER_NAME", "root");
        //TODO 1.从 Kafka 的 dwd 层接收订单和订单明细数据
        String orderInfoSourceTopic = "dwd_order_info";
        String orderDetailSourceTopic = "dwd_order_detail";
        String orderWideSinkTopic = "dwm_order_wide";
        String groupId = "order_wide_group";
        //从 Kafka 中读取数据
        FlinkKafkaConsumer<String> sourceOrderInfo =
                KafkaUtil.getKafkaSource(orderInfoSourceTopic, groupId);
        FlinkKafkaConsumer<String> sourceOrderDetail =
                KafkaUtil.getKafkaSource(orderDetailSourceTopic, groupId);
        DataStream<String> orderInfojsonDStream = env.addSource(sourceOrderInfo);
        DataStream<String> orderDetailJsonDStream = env.addSource(sourceOrderDetail);
        //对读取的数据进行结构的转换
        DataStream<OrderInfo> orderInfoDStream = orderInfojsonDStream.map(
            new RichMapFunction<String, OrderInfo>() {
                SimpleDateFormat simpleDateFormat = null;

                @Override
                public void open(Configuration parameters) throws Exception {
                    super.open(parameters);
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }

                @Override
                public OrderInfo map(String jsonString) throws Exception {
                    OrderInfo orderInfo = JSON.parseObject(jsonString, OrderInfo.class);

                    orderInfo.setCreate_ts(simpleDateFormat.parse(orderInfo.getCreate_time()).getTime());
                    return orderInfo;
                }
            }
        );
        DataStream<OrderDetail> orderDetailDStream = orderDetailJsonDStream.map(new RichMapFunction<String, OrderDetail>() {
            SimpleDateFormat simpleDateFormat = null;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }

            @Override
            public OrderDetail map(String jsonString) throws Exception {
                OrderDetail orderDetail = JSON.parseObject(jsonString, OrderDetail.class);
                orderDetail.setCreate_ts(simpleDateFormat.parse(orderDetail.getCreate_time()).getTime());
                return orderDetail;
            }
        });
        orderInfoDStream.print("orderInfo::::");
        orderDetailDStream.print("orderDetail::::");
        env.execute();
    }
}
