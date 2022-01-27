package com.yqwl.datamiddle.realtime.app.demo;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 算子状态管理
 * @Author: WangYouzheng
 * @Date: 2022/1/26 10:17
 * @Version: V1.0
 */
public class OperaterStateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        // env.setParallelism(2);
        // env.setParallelism(1);

        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("192.168.3.85", 7777);
        SingleOutputStreamOperator<Integer> countMap = stringDataStreamSource.map(line -> {
                    System.out.println("我接受到的是：" + line);
                    return line;
                })// 定义一个有状态的map操作，实现count的动作。
                .map(new MyCountState());
        countMap.print("求和结果");
        SingleOutputStreamOperator<Integer> countMap2 = countMap.map(new MyCountState3());
        countMap2.print("求和结果第二个map");

        env.execute();
    }

    public static class MyCountState implements MapFunction<String, Integer>  {
        // 定义一个本地变量 视为算子状态 如果并行度 > 1的时候 那么就有问题了 他是跑在各个jvm的线程的里面的
        private Integer count = 0;
        @Override
        public Integer map(String value) throws Exception {
            return ++count;
        }
    }

    public static class MyCountState2 implements MapFunction<Integer, Integer> {
        // 定义一个本地变量 视为算子状态 如果并行度 > 1的时候 那么就有问题了 他是跑在各个jvm的线程的里面的，并没有共享
        private Integer count2 = 0;
        @Override
        public Integer map(Integer value) throws Exception {
            return ++count2;
        }
    }

    /**
     * operator state demo
     */
    public static class MyCountState3 implements MapFunction<Integer, Integer>, CheckpointedFunction {
        private Integer count2 = 0;
        ListState<Integer> listState;
        @Override
        public Integer map(Integer value) throws Exception {
            listState.add(123+value);
            System.out.println(listState.toString());
            return value;
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            // context.
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            listState = context.getOperatorStateStore().getListState(new ListStateDescriptor<Integer>("operator-state", Integer.class));
        }
    }
}
