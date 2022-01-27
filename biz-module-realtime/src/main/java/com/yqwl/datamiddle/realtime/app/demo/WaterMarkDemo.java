package com.yqwl.datamiddle.realtime.app.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/1/24 13:21
 * @Version: V1.0
 */
@Slf4j
public class WaterMarkDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // env.setParallelism(4); // 并行度 感受多线程执行
        env.setParallelism(1); // 并行度 感受多线程执行
        DataStream<String> lines = env.readTextFile("file:///C://1.txt");
        lines.print("啦啦啦");
        log.info("test log");
        SingleOutputStreamOperator<String> map = lines.map(new RichMapFunction<String, String>() {
            @Override
            public void open(Configuration parameters) throws Exception {
            }

            @Override
            public String map(String value) throws Exception {
                return value + "你好 我是map";
            }
        });
        map.print("啦啦啦2");

/*        map.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<String>() {
            @Override
            public long extractTimestamp(String element) {
                return Long.valueOf(element);
            }
        });*/
        map.keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String value) throws Exception {
                return null;
            }
        });
        env.execute("aaaaaaaaaa");
        //
        // DataStream<Transaction> transactions = env
        //         .addSource(new TransactionSource())
        //         .name("transactions");
        //
        // DataStream<Alert> alerts = transactions
        //         .keyBy(Transaction::getAccountId)
        //         .process(new FraudDetector())
        //         .name("fraud-detector");
        //
        // alerts
        //         .addSink(new AlertSink())
        //         .name("send-alerts");
        //
        // env.execute("Fraud Detection");
    }
}
