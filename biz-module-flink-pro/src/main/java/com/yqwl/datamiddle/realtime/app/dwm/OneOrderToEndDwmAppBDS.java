package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmAppBDS {

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(600000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA)
                .setGroupId(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_GROUP_2)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");

        //==============================================dwd_base_station_data处理 START==========================================================================//

        //2.转换BASE_STATION_DATA为实体类
        SingleOutputStreamOperator<DwdBaseStationData> mapBsd = mysqlSource.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationData.class);
            }
        }).uid("transitionBASE_STATION_DATA").name("transitionBASE_STATION_DATA");

        //3.插入mysql
        mapBsd.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end SET IN_WAREHOUSE_NAME= ?, IN_WAREHOUSE_CODE= ?  WHERE VIN = ? ",
                (ps, epc) -> {
                    ps.setString(1, epc.getIN_WAREHOUSE_NAME());
                    ps.setString(2, epc.getIN_WAREHOUSE_CODE());
                    ps.setString(3, epc.getVIN());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink1").name("baseStationDataSink1");

        //3.基地入库时间
        mapBsd.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a SET IN_SITE_TIME = ? " +
                        "WHERE e.VIN = ? " +
                        "AND e.LEAVE_FACTORY_TIME < ? " +
                        "AND a.`WAREHOUSE_TYPE` = 'T1' " +
                        "AND (e.IN_SITE_TIME > ? or e.IN_SITE_TIME = 0)",
                (ps, epc) -> {
                    ps.setLong(1, epc.getSAMPLE_U_T_C());
                    ps.setString(2, epc.getVIN());
                    ps.setLong(3, epc.getSAMPLE_U_T_C());
                    ps.setLong(4, epc.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink2").name("baseStationDataSink2");


        //4.末端配送入库时间
        mapBsd.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a SET IN_DISTRIBUTE_TIME = ? " +
                        "WHERE e.VIN = ? " +
                        "AND e.LEAVE_FACTORY_TIME < ? " +
                        "AND a.`WAREHOUSE_TYPE` = 'T2' " +
                        "AND e.IN_SITE_TIME < ?",
                (ps, epc) -> {
                    ps.setLong(1, epc.getSAMPLE_U_T_C());
                    ps.setString(2, epc.getVIN());
                    ps.setLong(3, epc.getSAMPLE_U_T_C());
                    ps.setLong(4, epc.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink3").name("baseStationDataSink3");


        //过滤出所有出库操作记录
        SingleOutputStreamOperator<DwdBaseStationData> outStockFilter = mapBsd.filter(new FilterFunction<DwdBaseStationData>() {
            @Override
            public boolean filter(DwdBaseStationData data) throws Exception {
                String operateType = data.getOPERATE_TYPE();
                return "OutStock".equals(operateType);
            }
        }).uid("outStockFilter").name("outStockFilter");

        // TODO: @See org.jeecg.yqwl.datamiddle.job.mapper.DataMiddleOdsBaseStationDataAndEpcMapper.updateOOTDLeaveFactoryTime 需要新增where语句 By QingSong 2022年6月17日00:21:04
        //出厂日期
        outStockFilter.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end SET LEAVE_FACTORY_TIME=? WHERE VIN = ? AND CP9_OFFLINE_TIME < ? AND ( LEAVE_FACTORY_TIME = 0 OR LEAVE_FACTORY_TIME > ? )",
                (ps, epc) -> {
                    ps.setLong(1, epc.getSAMPLE_U_T_C());
                    ps.setString(2, epc.getVIN());
                    ps.setLong(3, epc.getSAMPLE_U_T_C());
                    ps.setLong(4, epc.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink3").name("baseStationDataSink3");


        //==============================================dwd_base_station_data处理 END==========================================================================//


        env.execute("dwdBsd更新一单到底表");
        log.info("base_station_data job任务开始执行");

    }
}
