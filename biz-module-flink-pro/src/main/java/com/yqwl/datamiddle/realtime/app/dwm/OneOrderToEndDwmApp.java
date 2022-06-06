package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.DimUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmApp {

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        /*CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);*/
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("mysql.hostname"))
                .port(props.getInt("mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("mysql.database.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("mysql.table.list"), ","))
                .username(props.getStr("mysql.username"))
                .password(props.getStr("mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();

        //env.addSource(mySqlSource).uid("oracleSourceStream").name("oracleSourceStream");
        // 将kafka中源数据转化成DataStream
        //SingleOutputStreamOperator<String> bsdDs = env.fromSource(bsdSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-bsd").uid("bsdDs").name("bsdDs");
        //SingleOutputStreamOperator<String> bsdEpcDs = env.fromSource(bsdEpcSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-epc").uid("bsdEpcDs").name("bsdEpcDs");



        env.execute("合表开始");
        log.info("base_station_data job任务开始执行");

    }
}
