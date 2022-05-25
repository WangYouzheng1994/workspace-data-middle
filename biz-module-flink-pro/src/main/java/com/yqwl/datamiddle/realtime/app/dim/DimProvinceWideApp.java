package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.Mdac01;
import com.yqwl.datamiddle.realtime.bean.ProvincesWide;
import com.yqwl.datamiddle.realtime.bean.Sysc07;
import com.yqwl.datamiddle.realtime.bean.Sysc08;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.DimUtil;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/5/12 13:30
 * @Version: V1.2
 */
public class DimProvinceWideApp {
    private static final Logger LOGGER = LogManager.getLogger(DimProvinceWideApp.class);

    public static void main(String[] args) {
        /*1. 创建环境*/
        //Configuration conf = new Configuration();
        //conf.setString(RestOptions.BIND_PORT, "8081"); // 指定访问端口

        // 获取执行环境:
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

      /*  CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint/kafka20");
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);*/
        System.setProperty("HADOOP_USER_NAME", "root");

        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);

        // kafka source1 sysc07
        KafkaSource<String> sysc07 = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SYSC07)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SYSC07_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // kafka source1 sysc08
        KafkaSource<String> sysc08 = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SYSC08)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SYSC07_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // kafka source1 mdac01
        KafkaSource<String> mdac01 = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_MDAC01)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SYSC07_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        SingleOutputStreamOperator<String> sysc07Source = env.fromSource(sysc07, WatermarkStrategy.noWatermarks(), "sysc07-kafka").uid("sysc07Source").name("sysc07Source");
        SingleOutputStreamOperator<String> sysc08Source = env.fromSource(sysc08, WatermarkStrategy.noWatermarks(), "sysc08-kafka").uid("sysc08Source").name("sysc08Source");
        SingleOutputStreamOperator<String> mdac01Source = env.fromSource(mdac01, WatermarkStrategy.noWatermarks(), "mdac01-kafka").uid("mdac01Source").name("mdac01Source");
        ;

        LOGGER.info("1.kafka数据源收入");

        /*2. 进行数据过滤:*/
        // 过滤出sysc07的表
        DataStream<String> filterSysc07 = sysc07Source.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("SYSC07")) {
                    Sysc07 after = jo.getObject("after", Sysc07.class);
                    String cdqdm = after.getCDQDM();
                    if (cdqdm != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterSysc07").name("filterSysc07");
        // 过滤出sysc08的表
        SingleOutputStreamOperator<String> filterSysc08 = sysc08Source.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("SYSC08")) {
                    return true;
                }
                return false;
            }
        }).uid("filterSysc08").name("filterSysc08");

        // 过滤出mdac01的表
        SingleOutputStreamOperator<String> filterMdac01 = mdac01Source.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("MDAC01")) {
                    return true;
                }
                return false;
            }
        }).uid("filterMdac01").name("filterMdac01");

        LOGGER.info("2.过滤后的数据");

        /*3.进行实体类转换 */
        //  转换Sysc07的表
        SingleOutputStreamOperator<Sysc07> mapSysc07 = filterSysc07.map(new MapFunction<String, Sysc07>() {
            @Override
            public Sysc07 map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Sysc07 sysc07 = jo.getObject("after", Sysc07.class);
                Timestamp ts = jo.getTimestamp("ts");
                sysc07.setTs(ts);
                return sysc07;
            }
        }).uid("mapSysc07").name("mapSysc07");

        //  转换  Sysc08的表
        SingleOutputStreamOperator<Sysc08> mapSysc08 = filterSysc08.map(new MapFunction<String, Sysc08>() {
            @Override
            public Sysc08 map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Sysc08 sysc08 = jo.getObject("after", Sysc08.class);
                Timestamp ts = jo.getTimestamp("ts");
                sysc08.setTs(ts);
                return sysc08;
            }
        }).uid("mapSysc08").name("mapSysc08");

        //  转换  Mdac01的表
        SingleOutputStreamOperator<Mdac01> mapMdac01 = filterMdac01.map(new MapFunction<String, Mdac01>() {
            @Override
            public Mdac01 map(String data) throws Exception {
                JSONObject jo = JSON.parseObject(data);
                Mdac01 mdac01 = jo.getObject("after", Mdac01.class);
                Timestamp ts = jo.getTimestamp("ts");
                mdac01.setTs(ts);
                return mdac01;
            }
        }).uid("mapMdac01").name("mapMdac01");

        /*4.指定事件时间字段 */
        //  sysc07指定事件时间
        SingleOutputStreamOperator<Sysc07> sysc07WithTs = mapSysc07.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Sysc07>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Sysc07>() {
                            @Override
                            public long extractTimestamp(Sysc07 sysc07, long recordTimestamp) {
                                Timestamp ts = sysc07.getTs();
                                Long time = ts.getTime();
                                return time;
                            }
                        })
        ).uid("Sysc07WithTsDS").name("Sysc07WithTsDS");

        //  sysc08指定事件时间
        SingleOutputStreamOperator<Sysc08> sysc08WithTs = mapSysc08.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Sysc08>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Sysc08>() {
                            @Override
                            public long extractTimestamp(Sysc08 sysc08, long recordTimestamp) {
                                Timestamp ts = sysc08.getTs();
                                Long time = ts.getTime();
                                return time;
                            }
                        })
        ).uid("Sysc08WithTsDS").name("Sysc08WithTsDS");

        //  mdac01指定事件时间
        SingleOutputStreamOperator<Mdac01> mdac01WithTs = mapMdac01.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Mdac01>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Mdac01>() {
                            @Override
                            public long extractTimestamp(Mdac01 mdac01, long recordTimestamp) {
                                Timestamp ts = mdac01.getTs();
                                Long time = ts.getTime();
                                return time;
                            }
                        })
        ).uid("Mdac01WithTsDS").name("Mdac01WithTsDS");

        /* 5. 分组指定关联key */
        //  sysc07,08,09按照省区代码(CSQDM)分组
        KeyedStream<Sysc07, String> sysc07StringKeyedStream = sysc07WithTs.keyBy(Sysc07::getCSQDM);
        KeyedStream<Sysc08, String> sysc08StringKeyedStream = sysc08WithTs.keyBy(Sysc08::getCSQDM);
        //  Mdac01按照大区代码(CDQDM)分组
        KeyedStream<Mdac01, String> mdac01StringKeyedStream = mdac01WithTs.keyBy(Mdac01::getCDQDM);

        /* 6.进行表拓宽 */
        //6.1 先使用省区代码(CSQDM)对sysc07,08进行关联
        DataStream<ProvincesWide> wide0708 = sysc08StringKeyedStream
                .intervalJoin(sysc07StringKeyedStream)
                .between(Time.minutes(-10), Time.minutes(10))
                .process(
                        new ProcessJoinFunction<Sysc08, Sysc07, ProvincesWide>() {
                            @Override
                            public void processElement(Sysc08 left, Sysc07 right, Context ctx, Collector<ProvincesWide> out) {
                                out.collect(new ProvincesWide(right, left));
                            }
                        }
                ).uid("mergeSysc0708").name("mergeSysc0708");
        KeyedStream<ProvincesWide, String> provincesWide0708KeyedStream = wide0708.keyBy(ProvincesWide::getCdqdm);


        //  6.2 再用wide070809宽表和Mdac01进行关联
        DataStream<ProvincesWide> provincesWide = provincesWide0708KeyedStream
                .intervalJoin(mdac01StringKeyedStream)
                .between(Time.minutes(-10), Time.minutes(10))
                .process(new ProcessJoinFunction<ProvincesWide, Mdac01, ProvincesWide>() {
                    @Override
                    public void processElement(ProvincesWide left, Mdac01 right, Context ctx, Collector<ProvincesWide> out) {
                        out.collect(new ProvincesWide(left, right));
                    }
                }).uid("mergeWide07mdac01").name("mergeWide07mdac01");

        SingleOutputStreamOperator<ProvincesWide> provincesWideWithSysc09 = AsyncDataStream.unorderedWait(
                provincesWide,
                new DimAsyncFunction<ProvincesWide>(DimUtil.MYSQL_DB_TYPE, "ods_vlms_sysc09", "cdsdm,csqdm") {
                    @Override
                    public Object getKey(ProvincesWide wide) {
                        if (StringUtils.isNotEmpty(wide.getCdsdm08()) && StringUtils.isNotEmpty(wide.getCsqdm())) {
                            return Arrays.asList(wide.getCdsdm08(), wide.getCsqdm());
                        }
                        return null;
                    }

                    @Override
                    public void join(ProvincesWide wide, JSONObject dimInfoJsonObj) throws Exception {
                        if (dimInfoJsonObj.getString("CDSDM") != null) {
                            LOGGER.info("dim cdsdm");
                            System.out.println("dim cdsdm");
                            wide.setCdsdm(dimInfoJsonObj.getString("CDSDM"));
                        } else {
                            LOGGER.info("wide csxdm");
                            System.out.println("wide csxdm");
                            wide.setCdsdm(wide.getCsxdm());
                        }
                        if (dimInfoJsonObj.getString("CDSDM").equals(wide.getCdsdm08()) && dimInfoJsonObj.getString("CSQDM").equals(wide.getCsqdm())) {
                            wide.setCdsdm(dimInfoJsonObj.getString("CDSDM"));
                            wide.setVdsmc(dimInfoJsonObj.getString("VDSMC"));
                        }
                    }
                }, 60, TimeUnit.SECONDS).uid("provincesWideWithSysc09").name("provincesWideWithSysc09");

        /* 7.开窗,按照数量(后续改为按照时间窗口)*/
        provincesWideWithSysc09.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        provincesWideWithSysc09.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<ProvincesWide, List<ProvincesWide>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<ProvincesWide> iterable, Collector<List<ProvincesWide>> collector) throws Exception {
                ArrayList<ProvincesWide> es = Lists.newArrayList(iterable);
                if (es.size() > 0) {
                    collector.collect(es);
                }
            }
        }).addSink(JdbcSink.<ProvincesWide>getBatchSink()).uid("sink-mysql").name("sink-mysql");

        try {
            env.execute("KafkaSinkMysql");
        } catch (Exception e) {
            LOGGER.error("stream invoke error", e);
        }

    }
}















