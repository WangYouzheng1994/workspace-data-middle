package com.yqwl.datamiddle.realtime.app.demo;

import javafx.scene.control.Alert;
import org.apache.flink.shaded.zookeeper3.org.apache.zookeeper.Transaction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/1/21 14:22
 * @Version: V1.0
 */
public class TestDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // env.setParallelism(4); // 并行度 感受多线程执行
        env.setParallelism(1); // 并行度 感受多线程执行
        DataStream<String> lines = env.readTextFile("file:///C://1.txt");
        lines.print("啦啦啦");
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
