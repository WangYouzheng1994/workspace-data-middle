package com.yqwl.datamiddle.realtime.util;

import com.alibaba.fastjson.JSONArray;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.List;

/**
 * @Description: 抓上游flink 算子推送的数据
 * @Author: WangYouzheng
 * @Date: 2022/5/12 11:45
 * @Version: V1.0
 */
public class TestSink<T> extends RichSinkFunction<List<T>> {
    @Override
    public void open(Configuration parameters) {
        try {
            System.out.println("我是监控sink算子");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invoke(List<T> value, Context context) throws Exception {
        System.out.println("我接收到了数据：" + JSONArray.toJSONString(value));
    }
}
