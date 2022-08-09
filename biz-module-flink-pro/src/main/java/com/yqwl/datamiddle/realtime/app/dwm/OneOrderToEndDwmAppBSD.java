package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.yqwl.datamiddle.realtime.app.func.SimpleBsdSinkOOTD;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmAppBSD {
    // 2021-06-01 00:00:00  设置此时间的原因为sptb02的ddjrq为7月1日与base_station_data表的时间有出入，故选取半年前的时间来兜底
    private static final long START = 1622476800000L;
    // 2022-12-31 23:59:59
    private static final long END = 1672502399000L;
    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA)
                .setGroupId(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_GROUP_2)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        // 1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "OneOrderToEndDwmAppBSDMysqlSource").uid("OneOrderToEndDwmAppBSDMysqlSourceStream").name("OneOrderToEndDwmAppBSDMysqlSourceStream");
        //==============================================dwd_base_station_data处理 START==========================================================================//

        // 2.转换BASE_STATION_DATA为实体类
        SingleOutputStreamOperator<DwdBaseStationData> mapBsd = mysqlSource.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationData.class);
            }
        }).uid("OneOrderToEndDwmAppBSDTransitionBASE_STATION_DATA").name("OneOrderToEndDwmAppBSDTransitionBASE_STATION_DATA");
        // 新增过滤时间的操作
        SingleOutputStreamOperator<DwdBaseStationData> mapBsdFilterTime = mapBsd.process(new ProcessFunction<DwdBaseStationData, DwdBaseStationData>() {
            @Override
            public void processElement(DwdBaseStationData value, ProcessFunction<DwdBaseStationData, DwdBaseStationData>.Context ctx, Collector<DwdBaseStationData> out) throws Exception {
                if (value.getSAMPLE_U_T_C() >= START && value.getSAMPLE_U_T_C() <= END) {
                    out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppFilter2022Time").name("OneOrderToEndDwmAppFilter2022Time");
        // 3.更新 dwdBds->dwmOOTD 一单到底表
        mapBsdFilterTime.addSink(new SimpleBsdSinkOOTD<DwdBaseStationData>()).uid("OneOrderToEndDwmAppBSDBsdSinkOOTD").name("OneOrderToEndDwmAppBSDBsdSinkOOTD");
        //==============================================dwd_base_station_data处理 END==========================================================================//
        env.execute("dwdBsd更新一单到底表");
        log.info("base_station_data job任务开始执行");

    }
}
