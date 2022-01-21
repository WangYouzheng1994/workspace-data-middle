package com.yqwl.datamiddle.realtime.app.example;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

    @Override
    public void flatMap(String line, Collector<Tuple2<String, Integer>> collector) throws Exception {
        for (String word : line.split(" ")) {
            collector.collect(new Tuple2<>(word, 1));
        }
    }
}
