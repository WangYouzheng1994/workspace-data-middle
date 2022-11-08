package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
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
public class OneOrderToEndDwmAppEPC {
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

        //====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");

        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("oote_dwm_epc"));
        // 设置savepoint点二级目录位置
        //env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("oote_dwm_epc"));
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC)
                .setGroupId(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "OneOrderToEndDwmAppEPCMysqlSource").uid("OneOrderToEndDwmAppEPCMysqlSourceStream").name("OneOrderToEndDwmAppEPCMysqlSourceStream");

        //==============================================dwd_base_station_data_epc处理 START====================================================================//

        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = mysqlSource.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationDataEpc.class);
            }
        }).uid("OneOrderToEndDwmAppEPCTransitionBASE_STATION_DATA_EPCMap").name("OneOrderToEndDwmAppEPCTransitionBASE_STATION_DATA_EPCMap");
        // 新增过滤时间的操作
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpcFilterTime = mapBsdEpc.process(new ProcessFunction<DwdBaseStationDataEpc, DwdBaseStationDataEpc>() {
            @Override
            public void processElement(DwdBaseStationDataEpc value, ProcessFunction<DwdBaseStationDataEpc, DwdBaseStationDataEpc>.Context ctx, Collector<DwdBaseStationDataEpc> out) throws Exception {
                Long cp9_offline_time = value.getCP9_OFFLINE_TIME();
                if (cp9_offline_time !=null && value.getCP9_OFFLINE_TIME() >= START && value.getCP9_OFFLINE_TIME() <= END){
                        out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppEpcFilter2022Time").name("OneOrderToEndDwmAppEpcFilter2022Time");

        //4.插入mysql
        mapBsdEpcFilterTime.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, CP9_OFFLINE_TIME, WAREHOUSE_CREATETIME, WAREHOUSE_UPDATETIME )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,  ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "   CP9_OFFLINE_TIME = ? , " +
                        "         WAREHOUSE_CREATETIME = ? , WAREHOUSE_UPDATETIME = ?",
                (ps, epc) -> {
                    Long nowTime = System.currentTimeMillis();
                    ps.setString(1, epc.getVIN());
                    ps.setLong(2, epc.getCP9_OFFLINE_TIME());
                    ps.setLong(3, nowTime);
                    ps.setLong(4, nowTime);
                    ps.setLong(5, epc.getCP9_OFFLINE_TIME());
                    ps.setLong(6, nowTime);
                    ps.setLong(7, nowTime);
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(2)
                        .build(),
                PropertiesUtil.getMysqlJDBCConnection())).uid("OneOrderToEndDwmAppEPCBaseStationDataEpcSink").name("OneOrderToEndDwmAppEPCBaseStationDataEpcSink");
        //==============================================dwd_base_station_data_epc处理 END====================================================================//

        env.execute("bsdEpc更新一单到底表");
        log.info("base_station_data job任务开始执行");

    }
}
