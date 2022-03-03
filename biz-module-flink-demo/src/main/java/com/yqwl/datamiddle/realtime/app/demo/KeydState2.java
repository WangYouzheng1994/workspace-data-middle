package com.yqwl.datamiddle.realtime.app.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/1/26 21:24
 * @Version: V1.0
 */
public class KeydState2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("192.168.3.85", 7777);

        // 先转成个对象 为了后面的分组
        SingleOutputStreamOperator<TestKeyd> countMap = stringDataStreamSource.map(line -> {
            System.out.println("我接受到的是：" + line);
            return new TestKeyd(line, line);
        });

        SingleOutputStreamOperator<TestKeyd> flat = countMap.keyBy(new KeySelector<TestKeyd, String>() {
            @Override
            public String getKey(TestKeyd value) throws Exception {
                return value.getKeyName();
            }
        }).flatMap(new RichFlatMapFunction<TestKeyd, TestKeyd>() {
            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
            }

            @Override
            public void flatMap(TestKeyd value, Collector<TestKeyd> out) throws Exception {
                if (StringUtils.equals(value.value, "")) {
                    out.collect(value);
                }
            }

            @Override
            public void close() throws Exception {
                super.close();
            }
        });
        flat.print();
        env.execute();
    }

    @Data
    @AllArgsConstructor
    static
    class TestKeyd {
        private String keyName;
        private String value;
    }
}
