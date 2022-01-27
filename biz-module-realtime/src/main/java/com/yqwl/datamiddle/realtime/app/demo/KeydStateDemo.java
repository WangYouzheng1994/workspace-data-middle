package com.yqwl.datamiddle.realtime.app.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Description: https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/dev/datastream/fault-tolerance/state/
 * @Author: WangYouzheng
 * @Date: 2022/1/26 11:59
 * @Version: V1.0
 */
public class KeydStateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // env.setParallelism(2);
        // env.setParallelism(1);

        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("192.168.3.84", 7777);

        // 先转成个对象 为了后面的分组
        SingleOutputStreamOperator<TestKeyd> countMap = stringDataStreamSource.map(line -> {
                    System.out.println("我接受到的是：" + line);
                    return new TestKeyd(line, line);
                });
        SingleOutputStreamOperator<Integer> map = countMap.keyBy(new KeySelector<TestKeyd, String>() {
                    @Override
                    public String getKey(TestKeyd value) throws Exception {
                        return value.getKeyName();
                    }
                })
                // .map(new MyCounter()); 错误示例。
                .map(new MyCounterTwo());

        map.print("keyed resutl:");
        System.out.println();
        env.execute();
    }

    /**
     * 计数器实体类
     */
    @Data
    @AllArgsConstructor
    static class TestKeyd {
        private String keyName;
        private String value;
    }

    /**
     * 设置自定义的计数器:错误示例。 了解Rich系列api中 关于open生命周期的问题。
     * 因为getRunTimeContext是需要代码在任务提交给flink 拿到slot以后 才可以有上下文。
     * 因此初始化中的获取上下文需要在open中初始化。
     */
    public static class MyCounter extends RichMapFunction<TestKeyd, Integer> {
        // 初始化 分组的状态缓存集合
        private ValueState<Integer> keyCountState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("key-count", Integer.class));

        @Override
        public Integer map(TestKeyd row) throws Exception {
            Integer value = this.keyCountState.value();
            value++;
            keyCountState.update(value);
            return value;
        }
    }

    /**
     * 设置自定义的计数器
     */
    public static class MyCounterTwo extends RichMapFunction<TestKeyd, Integer> {
        // 初始化 分组的状态缓存集合
        private ValueState<Integer> keyCountState;
        private ListState<Integer> listCountState;
        private MapState<String, Double> myMapState;
        private ReducingState<TestKeyd> reducingState;


        /**
         * 初始化。
         * @param parameters
         * @throws Exception
         */
        @Override
        public void open(Configuration parameters) throws Exception {
            // 这里设置初始值的api即将弃用： 需要在用state的代码中做 非null再进行初始化判定
            keyCountState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("key-count", Integer.class, 0));

            listCountState = getRuntimeContext().getListState(new ListStateDescriptor<Integer>("list-key-count", Integer.class));

            myMapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Double>("map-count", String.class, Double.class));

            reducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<TestKeyd>("", new ReduceFunction<TestKeyd>() {
                @Override
                public TestKeyd reduce(TestKeyd value1, TestKeyd value2) throws Exception {
                    // 计算逻辑
                    return null;
                }
            }, TestKeyd.class));
            // super.open(parameters);
        }

        @Override
        public Integer map(TestKeyd row) throws Exception {
            Integer value = this.keyCountState.value();
            value++;
            keyCountState.update(value);
            keyCountState.clear();// 清空

            // list state api
            Iterable<Integer> listStateIter = listCountState.get();
            listCountState.add(value); // 插入
            listCountState.addAll(Arrays.asList(1, 2, 3, 4)); // 插入所有

            // map state api
            myMapState.put("lala", 123d);
            myMapState.get("lala");
            myMapState.remove("lala");

            // reduce State api
            reducingState.add(row);
            reducingState.get();
            return value;
        }
    }
}
