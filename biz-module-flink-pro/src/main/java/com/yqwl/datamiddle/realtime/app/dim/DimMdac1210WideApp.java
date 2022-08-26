package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.bean.DimMdac1210;
import com.yqwl.datamiddle.realtime.bean.Mdac10;
import com.yqwl.datamiddle.realtime.bean.Mdac12;
import com.yqwl.datamiddle.realtime.bean.SiteWarehouse;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.reflections.Reflections.log;

/**
 * @Description: 用于合并Mdac12与Mdac10的flink任务
 * @Author: XiaoFeng
 * @Date: 2022/8/25 16:21
 * @Version: V1.0
 */
@Slf4j
public class DimMdac1210WideApp {
    public static void main(String[] args) {
        // Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        // 设置并行度为1
        env.setParallelism(1);
        log.info("初始化流处理环境完成");

        // ====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");
        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("waybill_dwd_sptb02_simple"));
        Props props = PropertiesUtil.getProps();

        // kafka source1 mdac12 (主表)
        KafkaSource<String> mdac12KafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_MDAC12)
                .setGroupId(KafkaTopicConst.ODS_VLMS_MDAC12_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // kafka source1 mdac10
        KafkaSource<String> mdac10KafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_MDAC10)
                .setGroupId(KafkaTopicConst.ODS_VLMS_MDAC10_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        // 1.将kafka中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mdac12Stream = env.fromSource(mdac12KafkaSource, WatermarkStrategy.noWatermarks(), "mdac12-kafka").uid("DimMdac1210WideAppMdac12Source").name("DimProvinceWideAppMdac12Source");
        SingleOutputStreamOperator<String> mdac10Stream = env.fromSource(mdac10KafkaSource, WatermarkStrategy.noWatermarks(), "mdac10-kafka").uid("DimMdac1210WideAppMdac10Source").name("DimProvinceWideAppMdac10Source");

        // 2.1过滤Mdac12的表
        SingleOutputStreamOperator<String> mdac12Filter = mdac12Stream.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mdac12StreamValue) throws Exception {
                JSONObject mdac12Json = JSON.parseObject(mdac12StreamValue);
                if (StringUtils.equals(mdac12Json.getString("database"), "TDS_LJ") && StringUtils.equals(mdac12Json.getString("tableName"), "MDAC12")) {
                    Mdac12 mdac12Bean = mdac12Json.getObject("after", Mdac12.class);
                    return true;
                }
                return false;
            }
        }).uid("DimMdac1210WideAppFilterMdac12").name("DimMdac1210WideAppFilterMdac12");
        // 2.2过滤Mdac10的表
        SingleOutputStreamOperator<String> mdac10Filter = mdac10Stream.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mdac10StreamValue) throws Exception {
                JSONObject mdac10Json = JSON.parseObject(mdac10StreamValue);
                if (StringUtils.equals(mdac10Json.getString("database"), "TDS_LJ") && StringUtils.equals(mdac10Json.getString("tableName"), "MDAC10")) {
                    return true;
                }
                return false;
            }
        }).uid("DimMdac1210WideAppFilterMdac10").name("DimMdac1210WideAppFilterMdac10");

        // 3.1 Mdac12转实体类
        SingleOutputStreamOperator<Mdac12> mdac12MapBean = mdac12Filter.map(new MapFunction<String, Mdac12>() {
            @Override
            public Mdac12 map(String madc12FilterValue) throws Exception {
                JSONObject mdac12JsonObject = JSON.parseObject(madc12FilterValue);
                Mdac12 mdac12After = mdac12JsonObject.getObject("after", Mdac12.class);
                Long ts = mdac12JsonObject.getLong("ts");
                mdac12After.setTs(ts);
                return mdac12After;
            }
        }).uid("DimMdac1210WideAppFilterMdac12Map").name("DimMdac1210WideAppFilterMdac12Map");

        // 3.2 Mdac10转实体类
        SingleOutputStreamOperator<Mdac10> mdac10MapBean = mdac12Filter.map(new MapFunction<String, Mdac10>() {
            @Override
            public Mdac10 map(String madc10FilterValue) throws Exception {
                JSONObject mdac10JsonObject = JSON.parseObject(madc10FilterValue);
                Mdac10 mdac10After = mdac10JsonObject.getObject("after", Mdac10.class);
                Long ts = mdac10JsonObject.getLong("ts");
                mdac10After.setTs(ts);
                return mdac10After;
            }
        }).uid("DimMdac1210WideAppFilterMdac10Map").name("DimMdac1210WideAppFilterMdac10Map");

        // 4.1指定Mdac12的事件时间字段
        SingleOutputStreamOperator<Mdac12> mdac12WithTs = mdac12MapBean.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Mdac12>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Mdac12>() {
                            @Override
                            public long extractTimestamp(Mdac12 mdac12, long recordTimestamp) {
                                return mdac12.getTs();
                            }
                        })
        ).uid("DimMdac1210WideAppFilterMdac12TsDs").name("DimMdac1210WideAppFilterMdac12TsDs");
        // 4.2指定Mdac12的事件时间字段
        SingleOutputStreamOperator<Mdac10> mdac10WithTs = mdac10MapBean.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Mdac10>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Mdac10>() {
                            @Override
                            public long extractTimestamp(Mdac10 mdac10, long recordTimestamp) {
                                return mdac10.getTs();
                            }
                        })
        ).uid("DimMdac1210WideAppFilterMdac10TsDs").name("DimMdac1210WideAppFilterMdac10TsDs");

        // 5.分组指定Key
        KeyedStream<Mdac12, Object> mdac12ObjectKeyedStream = mdac12WithTs.keyBy(Mdac12::getCPP);
        KeyedStream<Mdac10, Object> mdac10ObjectKeyedStream = mdac10WithTs.keyBy(Mdac10::getCPP);

        // 6.进行表拓宽 Mdac12与Mdac10用CPP字段进行关联
        mdac12ObjectKeyedStream
                .intervalJoin(mdac10ObjectKeyedStream)
                .between(Time.minutes(-10),Time.minutes(10))
                .process(new ProcessJoinFunction<Mdac12, Mdac10, DimMdac1210>() {
                    @Override
                    public void processElement(Mdac12 mdac12, Mdac10 mdac10, ProcessJoinFunction<Mdac12, Mdac10, DimMdac1210>.Context ctx, Collector<DimMdac1210> out) throws Exception {
                        out.collect(new DimMdac1210(mdac12, mdac10));
                    }
                });
    }
}
