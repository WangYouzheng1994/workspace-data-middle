package com.yqwl.datamiddle.realtime.app.example;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WordCount {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        DataStream<String> text = env.fromElements(WordCountData.WORDS);
        DataStream<Tuple2<String, Integer>> counts = ((DataStream) text).flatMap(new LineSplitter()).keyBy(0).sum(1);

        System.out.println("Printing result to stdout. ");
        counts.print();

        env.execute("Streaming WordCount");
    }


}
