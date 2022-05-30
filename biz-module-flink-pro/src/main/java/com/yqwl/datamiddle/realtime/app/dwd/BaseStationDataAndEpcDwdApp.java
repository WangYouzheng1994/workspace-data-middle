package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.*;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
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
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
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
public class BaseStationDataAndEpcDwdApp {

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

        // kafka source2 base_station_data
        KafkaSource<String> bsdSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA)
                //.setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        // kafka source1 base_station_data_epc
        KafkaSource<String> bsdEpcSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC)
                //.setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();



        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> bsdDs = env.fromSource(bsdSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-bsd").uid("bsdDs").name("bsdDs");
        SingleOutputStreamOperator<String> bsdEpcDs = env.fromSource(bsdEpcSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-epc").uid("bsdEpcDs").name("bsdEpcDs");


        //2.进行数据过滤
        // 过滤出BASE_STATION_DATA的表
        DataStream<String> filterBsdDs = bsdDs.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("BASE_STATION_DATA")) {
                    DwdBaseStationData after = jo.getObject("after", DwdBaseStationData.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterBASE_STATION_DATA").name("filterBASE_STATION_DATA");

        // 过滤出BASE_STATION_DATA的表
        DataStream<String> filterBsdEpcDs = bsdEpcDs.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("BASE_STATION_DATA_EPC")) {
                    DwdBaseStationDataEpc after = jo.getObject("after", DwdBaseStationDataEpc.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterBASE_STATION_DATA_EPC").name("filterBASE_STATION_DATA_EPC");

        //3. 进行实体类的转换
        //BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = filterBsdEpcDs.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                DwdBaseStationDataEpc dataBsdEpc = jsonObject.getObject("after", DwdBaseStationDataEpc.class);
                Timestamp ts = jsonObject.getTimestamp("ts");
                dataBsdEpc.setTs(ts);
                return dataBsdEpc;
            }
        }).uid("transitionBASE_STATION_DATA_EPCMap").name("transitionBASE_STATION_DATA_EPCMap");
        //BASE_STATION_DATA
        SingleOutputStreamOperator<DwdBaseStationData> mapBsd = filterBsdDs.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String kafkaBsdValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdValue);
                DwdBaseStationData dataBsd = jsonObject.getObject("after", DwdBaseStationData.class);
                Timestamp ts = jsonObject.getTimestamp("ts");
                dataBsd.setTs(ts);
                return dataBsd;
            }
        }).uid("transitionBASE_STATION_DATA").name("transitionBASE_STATION_DATA");

        // 4.指定事件时间字段
        //DwdBaseStationDataEpc指定事件时间
        SingleOutputStreamOperator<DwdBaseStationDataEpc> dwdBaseStationDataEpcWithTS = mapBsdEpc.assignTimestampsAndWatermarks(
                WatermarkStrategy.<DwdBaseStationDataEpc>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<DwdBaseStationDataEpc>() {
                            @Override
                            public long extractTimestamp(DwdBaseStationDataEpc dwdBaseStationDataEpc, long l) {
                                Timestamp ts = dwdBaseStationDataEpc.getTs();
                                return ts.getTime();
                            }
                        })).uid("assIgnDwdBaseStationDataEpcEventTime");
        //DwdBaseStationData 指定事件时间
        SingleOutputStreamOperator<DwdBaseStationData> dwdBaseStationDataWithTS = mapBsd.assignTimestampsAndWatermarks(
                WatermarkStrategy.<DwdBaseStationData>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<DwdBaseStationData>() {
                            @Override
                            public long extractTimestamp(DwdBaseStationData dwdBaseStationData, long l) {
                                Timestamp ts = dwdBaseStationData.getTs();
                                return ts.getTime();
                            }
                        })).uid("assIgnDwdBaseStationDataEventTime");
//        dwdBaseStationDataWithTS.print("输出指定事件时间的:");
        //5.分组指定关联key
//        KeyedStream<DwdBaseStationDataEpc, String> dwdBdsEpcKeyedStream = dwdBaseStationDataEpcWithTS.keyBy(DwdBaseStationDataEpc::getVIN);
//        KeyedStream<DwdBaseStationData, String> dwdBdsKeyedStream = dwdBaseStationDataWithTS.keyBy(DwdBaseStationData::getVIN);

        //6.处理字段
        //todo: 1.base_station_data_epc 处理CP9下线接车日期


        //todo: 2.base_station_data 和rfid_warehouse关联添加入库仓库的字段
        //provincesWideWithSysc09.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        SingleOutputStreamOperator<DwdBaseStationData> outSingleOutputStreamOperator = AsyncDataStream.unorderedWait(
                dwdBaseStationDataWithTS,
                new DimAsyncFunction<DwdBaseStationData>(DimUtil.MYSQL_DB_TYPE, "ods_vlms_rfid_warehouse", "WAREHOUSE_CODE") {
                    @Override
                    public Object getKey(DwdBaseStationData dwdBsd) {
                        if (StringUtils.isNotEmpty(dwdBsd.getSHOP_NO())){
                            String shop_no = dwdBsd.getSHOP_NO();
                            log.info("sql查询:"+shop_no);
                            return shop_no;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwdBaseStationData dBsd, JSONObject dimInfoJsonObj) {
                        if (dimInfoJsonObj.getString("WAREHOUSE_CODE") !=null){
                            log.info("dim WAREHOUSE_CODE");
                            dBsd.setIN_WAREHOUSE_CODE(dimInfoJsonObj.getString("WAREHOUSE_CODE"));
                            dBsd.setIN_WAREHOUSE_NAME(dimInfoJsonObj.getString("WAREHOUSE_NAME"));
                        }
                    }
                }, 60, TimeUnit.SECONDS);

        //7.开窗,按照时间窗口存储到mysql
        outSingleOutputStreamOperator.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        outSingleOutputStreamOperator.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<DwdBaseStationData, List<DwdBaseStationData>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<DwdBaseStationData> iterable, Collector<List<DwdBaseStationData>> collector) throws Exception {
                ArrayList<DwdBaseStationData> es = Lists.newArrayList(iterable);
                if (es.size() > 0) {
                    collector.collect(es);
                }
            }
        }).addSink(JdbcSink.<DwdBaseStationData>getBatchSink()).uid("sink-mysql").name("sink-mysql");

        env.execute("合表开始");
        log.info("base_station_data job任务开始执行");

    }
}
