package com.yqwl.datamiddle.realtime.app.demo;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/1/26 14:55
 * @Version: V1.0
 */
public class FlatMapDemo {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("192.168.3.84", 7777);
        stringDataStreamSource.flatMap(new FlatMapFunction<String, Object>() {
            @Override
            public void flatMap(String value, Collector<Object> out) throws Exception {

            }
        });
        ArrayList<String> list = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));
        List<String> collect = list.stream().map(i -> i + "123").flatMap(s -> {
            return Stream.of(s.split("").toString());
        }).collect(Collectors.toList());
        System.out.println(collect);
    }
}
