package com.yqwl.datamiddle.realtime.app.demo;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description: 算子状态管理
 * @Author: WangYouzheng
 * @Date: 2022/1/26 10:17
 * @Version: V1.0
 */
public class StateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        // env.setParallelism(2);
        // env.setParallelism(1);

        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("192.168.3.84", 7777);
        SingleOutputStreamOperator<Integer> countMap = stringDataStreamSource.map(line -> {
                    System.out.println("我接受到的是：" + line);
                    return line;
                })// 定义一个有状态的map操作，实现count的动作。
                .map(new MyCountState());
        countMap.print("求和结果");
        SingleOutputStreamOperator<Integer> countMap2 = countMap.map(new MyCountState2());
        countMap2.print("求和结果第二个map");

        env.execute();
    }

    public static class MyCountState implements MapFunction<String, Integer> {
        // 定义一个本地变量 视为算子状态 如果并行度 > 1的时候 那么就有问题了 他是跑在各个jvm的线程的里面的
        private Integer count = 0;
        @Override
        public Integer map(String value) throws Exception {
            return ++count;
        }
    }

    public static class MyCountState2 implements MapFunction<Integer, Integer> {
        // 定义一个本地变量 视为算子状态 如果并行度 > 1的时候 那么就有问题了 他是跑在各个jvm的线程的里面的
        private Integer count2 = 0;
        @Override
        public Integer map(Integer value) throws Exception {
            return ++count2;
        }
    }
}
