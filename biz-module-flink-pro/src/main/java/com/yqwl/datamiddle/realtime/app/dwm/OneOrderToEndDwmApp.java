package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.*;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmApp {

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
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
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("cdc.mysql.table.list"), ","))
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");

        //==============================================dwd_base_station_data_epc处理 START====================================================================//
        //2.过滤出BASE_STATION_DATA_Epc的表
        DataStream<String> filterBsdEpcDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_flink") && jo.getString("tableName").equals("dwd_vlms_base_station_data_epc")) {
                    DwdBaseStationDataEpc after = jo.getObject("after", DwdBaseStationDataEpc.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterDwd_vlms_base_station_data_epc").name("filterDwd_vlms_base_station_data_epc");

        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = filterBsdEpcDs.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                DwdBaseStationDataEpc dataBsdEpc = jsonObject.getObject("after", DwdBaseStationDataEpc.class);
                Timestamp ts = jsonObject.getTimestamp("ts"); //取ts作为时间戳字段
                dataBsdEpc.setTs(ts);
                String vin = dataBsdEpc.getVIN();
                return dataBsdEpc;
            }
        }).uid("transitionBASE_STATION_DATA_EPCMap").name("transitionBASE_STATION_DATA_EPCMap");
        //4.插入mysql
        mapBsdEpc.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, CP9_OFFLINE_TIME, BASE_NAME, BASE_CODE )\n" +
                        "VALUES\n" +
                        "        ( ?, ?, ? ,?) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "   CP9_OFFLINE_TIME=? ,BASE_NAME=?,\n" +
                        "        BASE_CODE=?",
                (ps, epc) -> {
                    ps.setString(1, epc.getVIN());
                    ps.setLong(2, epc.getCP9_OFFLINE_TIME());
                    ps.setString(3, epc.getBASE_NAME());
                    ps.setString(4, epc.getBASE_CODE());
                    ps.setLong(5, epc.getCP9_OFFLINE_TIME());
                    ps.setString(6, epc.getBASE_NAME());
                    ps.setString(7, epc.getBASE_CODE());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(2)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataEpcSink").name("baseStationDataEpcSink");
        //==============================================dwd_base_station_data_epc处理 END====================================================================//

        //==============================================dwd_base_station_data处理 START==========================================================================//
        // 1.过滤出BASE_STATION_DATA的表
        DataStream<String> filterBsdDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_flink") && jo.getString("tableName").equals("dwd_vlms_base_station_data_copy1")) {
                    DwdBaseStationData after = jo.getObject("after", DwdBaseStationData.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterDwd_vlms_base_station_data").name("filterDwd_vlms_base_station_data");

        //2.转换BASE_STATION_DATA为实体类
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
        //3.插入mysql
        mapBsd.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end SET IN_WAREHOUSE_NAME= ?, IN_WAREHOUSE_CODE= ?  WHERE VIN = ? ",
                (ps, epc) -> {
                    ps.setString(1, epc.getIN_WAREHOUSE_NAME());
                    ps.setString(2, epc.getIN_WAREHOUSE_CODE());
                    ps.setString(3, epc.getVIN());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(2)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink1").name("baseStationDataSink1");

        //3.入库时间
        mapBsd.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a SET IN_SITE_TIME = ? WHERE e.VIN = ? AND e.LEAVE_FACTORY_TIME < ? AND a.`WAREHOUSE_TYPE` = 'T1'",
                (ps, epc) -> {
                    ps.setLong(1, epc.getSAMPLE_U_T_C());
                    ps.setString(2, epc.getVIN());
                    ps.setLong(3, epc.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(2)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink2").name("baseStationDataSink2");

        SingleOutputStreamOperator<DwdBaseStationData> outStockFilter = mapBsd.filter(new FilterFunction<DwdBaseStationData>() {
            @Override
            public boolean filter(DwdBaseStationData data) throws Exception {
                String operateType = data.getOPERATE_TYPE();
                return "OutStock".equals(operateType);
            }
        }).uid("outStockFilter").name("outStockFilter");

        //出厂日期
        outStockFilter.addSink(JdbcSink.sink(
                "UPDATE dwm_vlms_one_order_to_end SET LEAVE_FACTORY_TIME=? WHERE VIN = ? AND CP9_OFFLINE_TIME < ? AND ( LEAVE_FACTORY_TIME == 0 OR LEAVE_FACTORY_TIME > ? )",
                (ps, epc) -> {
                    ps.setLong(1, epc.getSAMPLE_U_T_C());
                    ps.setString(2, epc.getVIN());
                    ps.setLong(3, epc.getSAMPLE_U_T_C());
                    ps.setLong(4, epc.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(2)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataSink3").name("baseStationDataSink3");


        //==============================================dwd_base_station_data处理 END==========================================================================//


        //==============================================dwm_vlms_sptb02处理START=============================================================================//
        //2.进行数据过滤
        SingleOutputStreamOperator<String> filterSptb02 = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_flink") && jo.getString("tableName").equals("dwm_vlms_sptb02")) {
                    DwmSptb02 after = jo.getObject("after", DwmSptb02.class);
                    String cjsdbh = after.getCJSDBH();
                    if (cjsdbh != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        filterSptb02.print("filterSptb02:");
        //3.进行实体类转换
        //转换sptb02为实体类
        SingleOutputStreamOperator<OotdTransition> mapOotdTransition = filterSptb02.map(new MapFunction<String, OotdTransition>() {
            @Override
            public OotdTransition map(String sptb02Value) throws Exception {
                OotdTransition ootdTransition = new OotdTransition();
                JSONObject jsonObject = JSON.parseObject(sptb02Value);
                DwmSptb02 dwmSptb02 = jsonObject.getObject("after", DwmSptb02.class);
                String cjsdbh = dwmSptb02.getCJSDBH();                                  //结算单编号
                String vvin = dwmSptb02.getVVIN();                                      //底盘号
                String vehicle_code = dwmSptb02.getVEHICLE_CODE();                      //车型
                Long ddjrq = dwmSptb02.getDDJRQ();                                      //整车物流接收STD日期
                String cjhdh = dwmSptb02.getCJHDH();                                    //任务单号
                Long dphscsj = dwmSptb02.getDPHSCSJ();                                  //配板日期
                String vph = dwmSptb02.getVPH();                                        //配载单编号
                Long assign_time = dwmSptb02.getASSIGN_TIME();                          //指派运输商日期
                String transportName = dwmSptb02.getTRANSPORT_NAME();                   //指派承运商名称
                Long actual_out_time = dwmSptb02.getACTUAL_OUT_TIME();                  //出库日期
                Long shipment_time = dwmSptb02.getSHIPMENT_TIME();                      //起运日期 公路/铁路
                String vjsydm = dwmSptb02.getVJSYDM();                                  //运输车号
                String start_city_name = dwmSptb02.getSTART_CITY_NAME();                //始发城市
                String end_city_name = dwmSptb02.getEND_CITY_NAME();                    //目的城市
                String dealer_name = dwmSptb02.getDEALER_NAME();                        //经销商代码(名称)
                String vysfs = dwmSptb02.getVYSFS();                                    //运输方式
                String start_warehouse_name = dwmSptb02.getSTART_WAREHOUSE_NAME();      //开始站台/港口仓库名称
                String end_warehouse_name = dwmSptb02.getEND_WAREHOUSE_NAME();          //到达站台/港口仓库名称
                Long in_start_platform_time = dwmSptb02.getIN_START_PLATFORM_TIME();    //铁路的入开始站台时间
                Long out_start_platform_time = dwmSptb02.getOUT_START_PLATFORM_TIME();  //铁路的出开始站台时间
                Long in_end_platform_time = dwmSptb02.getIN_END_PLATFORM_TIME();        //铁路的入目的站台时间
                Long unload_railway_time = dwmSptb02.getUNLOAD_RAILWAY_TIME();          //铁路的卸车时间
                Long in_start_waterway_time = dwmSptb02.getIN_START_WATERWAY_TIME();    //水路的入开始港口时间
                Long end_start_waterway_time = dwmSptb02.getEND_START_WATERWAY_TIME();  //水路的出开始港口时间
                Long in_end_waterway_time = dwmSptb02.getIN_END_WATERWAY_TIME();        //水路的入目的港口时间
                Long unload_ship_time = dwmSptb02.getUNLOAD_SHIP_TIME();                //水路的卸船时间
                String highwayWarehouseType = dwmSptb02.getHIGHWAY_WAREHOUSE_TYPE();    //公路运单物理仓库对应的仓库类型
                if (StringUtils.isNotBlank(cjsdbh)) {
                    ootdTransition.setCJSDBH(cjsdbh);
                    if (StringUtils.isNotBlank(vehicle_code)) {
                        ootdTransition.setVEHICLE_CODE(vehicle_code);
                    }
                    if (StringUtils.isNotBlank(vvin)) {
                        ootdTransition.setVVIN(vvin);
                    }
                    if (ddjrq != null) {
                        ootdTransition.setDDJRQ(ddjrq);
                    }
                    if (StringUtils.isNotBlank(cjhdh)) {
                        ootdTransition.setCJHDH(cjhdh);
                    }
                    if (dphscsj != null) {
                        ootdTransition.setDPZRQ(dphscsj);
                    }
                    if (StringUtils.isNotBlank(vph)) {
                        ootdTransition.setCPZDBH(vph);
                    }
                    if (assign_time != null) {
                        ootdTransition.setASSIGN_TIME(assign_time);
                    }
                    if (StringUtils.isNotBlank(transportName)) {
                        ootdTransition.setASSIGN_NAME(transportName);
                    }
                    if (actual_out_time != null) {
                        ootdTransition.setACTUAL_OUT_TIME(actual_out_time);
                    }
                    if (shipment_time != null) {
                        ootdTransition.setSHIPMENT_TIME(shipment_time);
                    }
                    if (StringUtils.isNotBlank(vjsydm)) {
                        ootdTransition.setVJSYDM(vjsydm);
                    }
                    if (StringUtils.isNotBlank(start_city_name)) {
                        ootdTransition.setSTART_CITY_NAME(start_city_name);
                    }
                    if (StringUtils.isNotBlank(end_city_name)) {
                        ootdTransition.setEND_CITY_NAME(end_city_name);
                    }
                    if (StringUtils.isNotBlank(dealer_name)) {
                        ootdTransition.setDEALER_NAME(dealer_name);
                    }
                    //=====================================铁水运单处理=====================================================//
                    if (StringUtils.isNotBlank(vysfs)) {
                        //铁路运输方式
                        if ("T".equals(vysfs) || "L1".equals(vysfs)) {
                            if (StringUtils.isNotBlank(start_warehouse_name)) {
                                ootdTransition.setSTART_PLATFORM_NAME(start_city_name);              //开始站台仓库名称
                            }
                            if (StringUtils.isNotBlank(end_warehouse_name)) {
                                ootdTransition.setEND_PLATFORM_NAME(end_warehouse_name);             //到达站台仓库名称
                            }
                            if (in_start_platform_time != null) {
                                ootdTransition.setIN_START_PLATFORM_TIME(in_start_platform_time);    //铁路的入开始站台时间
                            }
                            if (out_start_platform_time != null) {
                                ootdTransition.setOUT_START_PLATFORM_TIME(out_start_platform_time);  //铁路的出开始站台时间
                            }
                            if (in_end_platform_time != null) {
                                ootdTransition.setIN_END_PLATFORM_TIME(in_end_platform_time);        //铁路的入目的站台时间
                            }
                            if (unload_railway_time != null) {
                                ootdTransition.setUNLOAD_RAILWAY_TIME(unload_railway_time);          //铁路的卸车时间
                            }
                        }
                        //水路运输方式
                        if ("S".equals(vysfs)) {
                            if (StringUtils.isNotBlank(start_warehouse_name)) {
                                ootdTransition.setSTART_PLATFORM_NAME(start_city_name);              //开始站台仓库名称
                            }
                            if (StringUtils.isNotBlank(end_warehouse_name)) {
                                ootdTransition.setEND_PLATFORM_NAME(end_warehouse_name);             //到达站台仓库名称
                            }
                            if (in_start_waterway_time != null) {
                                ootdTransition.setIN_START_WATERWAY_TIME(in_start_waterway_time);    //水路的入开始港口时间
                            }
                            if (end_start_waterway_time != null) {
                                ootdTransition.setEND_START_WATERWAY_TIME(end_start_waterway_time);  //水路的出开始港口时间
                            }
                            if (in_end_waterway_time != null) {
                                ootdTransition.setIN_END_WATERWAY_TIME(in_end_waterway_time);        //水路的入目的港口时间
                            }
                            if (unload_ship_time != null) {
                                ootdTransition.setUNLOAD_RAILWAY_TIME(unload_ship_time);             //水路的卸船时间
                            }
                        }

                        //=====================================末端配送处理=====================================================//
                        //运单为公路运单且物理仓库类型为分拨中心的为末端配送
                        if ("G".equals(vysfs) && "T2".equals(highwayWarehouseType)) {

                        }



                    }
                }

                return ootdTransition;
            }
        });

        //4.根据车型代码,查出车辆名称
        SingleOutputStreamOperator<OotdTransition> ootdAddCarNameStream = AsyncDataStream.unorderedWait(mapOotdTransition,
                new DimAsyncFunction<OotdTransition>(DimUtil.MYSQL_DB_TYPE, "ods_vlms_mdac12", "CCPDM") {
                    @Override
                    public Object getKey(OotdTransition ootd) {
                        if (StringUtils.isNotBlank(ootd.getVEHICLE_CODE())) {
                            String vehicle_code = ootd.getVEHICLE_CODE();
                            return vehicle_code;
                        }
                        return null;
                    }

                    @Override
                    public void join(OotdTransition ootd, JSONObject dimInfoJsonObj) throws Exception {
                        if (dimInfoJsonObj.getString("CCPDM") != null) {
                            ootd.setVEHICLE_NMAE(dimInfoJsonObj.getString("VCPMC"));
                        }
                    }
                }, 60, TimeUnit.SECONDS).uid("base+VEHICLE_NMAE").name("base+VEHICLE_NMAE");

        //5.sptb02与一单到底对应的字段插入mysql
        ootdAddCarNameStream.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, VEHICLE_CODE, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME  )\n" +
                        "VALUES\n" +
                        "        ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=?, VEHICLE_RECEIVING_TIME=?, TASK_NO=?, PLAN_RELEASE_TIME=?, \n " +
                        "STOWAGE_NOTE_NO=?, ASSIGN_TIME=?, CARRIER_NAME=?, ACTUAL_OUT_TIME=?, SHIPMENT_TIME=? ,TRANSPORT_VEHICLE_NO=?, START_CITY_NAME=?, END_CITY_NAME=?, DEALER_NAME=?, \n" +
                        "SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or ? < SETTLEMENT_Y1, ?, SETTLEMENT_Y1)," +
                        "START_PLATFORM_NAME = ?, END_PLATFORM_NAME = ?, IN_START_PLATFORM_TIME = ?, OUT_START_PLATFORM_TIME = ?, IN_END_PLATFORM_TIME = ?, UNLOAD_RAILWAY_TIME = ?, START_WATERWAY_NAME = ?, END_WATERWAY_NAME = ?, IN_START_WATERWAY_TIME = ?, END_START_WATERWAY_TIME = ?, IN_END_WATERWAY_TIME = ?, UNLOAD_SHIP_TIME = ?  ",
                (ps, epc) -> {
                    String vvin = epc.getVVIN();                                        //底盘号
                    String vehicle_code = epc.getVEHICLE_CODE();                        //车型
                    Long ddjrq = epc.getDDJRQ();                                        //整车物流接收STD日
                    String cjhdh = epc.getCJHDH();                                      //任务单号
                    Long dpzrq = epc.getDPZRQ();                                        //配板日期
                    String cpzdbh = epc.getCPZDBH();                                    //配载单编号
                    Long assign_time = epc.getASSIGN_TIME();                            //指派运输商日期
                    String assign_name = epc.getASSIGN_NAME();                          //指派承运商名称
                    Long actual_out_time = epc.getACTUAL_OUT_TIME();                    //出库日期
                    Long shipment_time = epc.getSHIPMENT_TIME();                        //起运日期 公路/铁路
                    String vjsydm = epc.getVJSYDM();                                    //运输车号
                    String start_city_name = epc.getSTART_CITY_NAME();                  //始发城市
                    String end_city_name = epc.getEND_CITY_NAME();                      //目的城市
                    String dealer_name = epc.getDEALER_NAME();                          //经销商代码(名称)
                    String cjsdbh = epc.getCJSDBH();                                    //结算单编号

                    //新添加铁水出入站台/港口的十二个字段
                    String start_platform_name = epc.getSTART_PLATFORM_NAME();          //铁路开始站台
                    String end_platform_name = epc.getEND_PLATFORM_NAME();              //铁路目的站台
                    Long in_start_platform_time = epc.getIN_START_PLATFORM_TIME();      //铁路入开始站台时间
                    Long out_start_platform_time = epc.getOUT_START_PLATFORM_TIME();    //铁路出开始站台时间
                    Long in_end_platform_time = epc.getIN_END_PLATFORM_TIME();          //铁路入目的站台时间
                    Long unload_railway_time = epc.getUNLOAD_RAILWAY_TIME();            //铁路卸车时间
                    String start_waterway_name = epc.getSTART_WATERWAY_NAME();          //水路开始港口名称
                    String end_waterway_name = epc.getEND_WATERWAY_NAME();              //水路目的港口名称
                    Long in_start_waterway_time = epc.getIN_START_WATERWAY_TIME();      //水路入开始港口时间
                    Long end_start_waterway_time = epc.getEND_START_WATERWAY_TIME();    //水路出开始港口时间
                    Long in_end_waterway_time = epc.getIN_END_WATERWAY_TIME();          //水路入目的港口时间
                    Long unload_ship_time = epc.getUNLOAD_SHIP_TIME();                  //水路卸船时间

                    if (StringUtils.isNotBlank(vvin)){
                        ps.setString(1, vvin);                        //底盘号
                    }else {
                        ps.setString(1, "0");                      //底盘号
                    }
                    if (StringUtils.isNotBlank(vehicle_code)){
                        ps.setString(2,  vehicle_code);               //车型
                    }else {
                        ps.setString(2,  "0");                     //车型
                    }
                    if (ddjrq!=null){
                        ps.setLong  (3,  epc.getDDJRQ());             //整车物流接收STD日
                    }else {
                        ps.setLong  (3,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cjhdh)){
                        ps.setString(4,  cjhdh);                      //任务单号
                    }else {
                        ps.setString(4,  "0");                     //任务单号
                    }
                    if (dpzrq!=null){
                        ps.setLong  (5,  dpzrq);                      //配板日期
                    }else {
                        ps.setLong  (5,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cpzdbh)){
                        ps.setString(6,  cpzdbh);                     //配载单编号
                    }else {
                        ps.setString(6,  "0");                     //配载单编号
                    }
                    if (assign_time!=null){
                        ps.setLong  (7,  assign_time);                //指派运输商日期
                    }else {
                        ps.setLong  (7,  0L);                      //指派运输商日期
                    }
                    if (StringUtils.isNotBlank(assign_name)){
                        ps.setString(8,  assign_name);                //指派承运商名称
                    }else {
                        ps.setString(8,  "0");                     //指派承运商名称
                    }
                    if (actual_out_time!=null){
                        ps.setLong  (9,  actual_out_time);            //出库日期
                    }else {
                        ps.setLong  (9,  0L);                      //出库日期
                    }
                    if (shipment_time!=null){
                        ps.setLong  (10, shipment_time);              //起运日期 公路/铁路
                    }else {
                        ps.setLong  (10, 0L);                      //起运日期 公路/铁路
                    }
                    if (StringUtils.isNotBlank(vjsydm)){
                        ps.setString(11, vjsydm);                     //运输车号
                    }else {
                        ps.setString(11, "0");                     //运输车号
                    }
                    if (StringUtils.isNotBlank(start_city_name)){
                        ps.setString(12, start_city_name);            //始发城市
                    }else {
                        ps.setString(12, "0");                     //始发城市
                    }
                    if (StringUtils.isNotBlank(end_city_name)){
                        ps.setString(13, end_city_name);              //目的城市
                    }else {
                        ps.setString(13, "0");                     //目的城市
                    }
                    if (StringUtils.isNotBlank(dealer_name)){
                        ps.setString(14, dealer_name);                //经销商代码(名称)
                    }else {
                        ps.setString(14, "0");                     //经销商代码(名称)
                    }
                    if (StringUtils.isNotBlank(cjsdbh)){
                        ps.setString(15, epc.getCJSDBH());            //结算单编号
                    }else {
                        ps.setString(15, "0");                     //结算单编号
                    }

                    //新添加铁水出入站台/港口的十二个字段
                    if (StringUtils.isNotBlank(start_platform_name)){
                        ps.setString(16, start_platform_name);        //铁路开始站台
                    }else {
                        ps.setString(16, "0");                     //铁路开始站台
                    }
                    if (StringUtils.isNotBlank(end_platform_name)){
                        ps.setString(17, end_platform_name);          //铁路目的站台
                    }else {
                        ps.setString(17, "0");                     //铁路目的站台
                    }
                    if (in_start_platform_time!=null){
                        ps.setLong  (18, in_start_platform_time);     //铁路入开始站台时间
                    }else {
                        ps.setLong  (18, 0L);                      //铁路入开始站台时间
                    }
                    if (out_start_platform_time!=null){
                        ps.setLong  (19, out_start_platform_time);    //铁路出开始站台时间
                    }else {
                        ps.setLong  (19, 0L);                      //铁路出开始站台时间
                    }
                    if (in_end_platform_time!=null){
                        ps.setLong  (20, in_end_platform_time);       //铁路入目的站台时间
                    }else {
                        ps.setLong  (20, 0L);                      //铁路入目的站台时间
                    }
                    if (unload_railway_time!=null){
                        ps.setLong  (21, unload_railway_time);        //铁路卸车时间
                    }else {
                        ps.setLong  (21, 0L);                      //铁路卸车时间
                    }
                    if (StringUtils.isNotBlank(start_waterway_name)){
                        ps.setString(22, start_waterway_name);       //水路开始港口名称
                    }else {
                        ps.setString(22, "0");                    //水路开始港口名称
                    }
                    if (StringUtils.isNotBlank(end_waterway_name)){
                        ps.setString(23, end_waterway_name);        //水路目的港口名称
                    }else {
                        ps.setString(23, "0");                   //水路目的港口名称
                    }
                    if (in_start_waterway_time!=null){
                        ps.setLong  (24, in_start_waterway_time);   //水路入开始港口时间
                    }else {
                        ps.setLong  (24, 0L);                    //水路入开始港口时间
                    }
                    if (end_start_waterway_time!=null){
                        ps.setLong  (25, end_start_waterway_time);  //水路出开始港口时间
                    }else {
                        ps.setLong  (25, 0L);                    //水路出开始港口时间
                    }
                    if (in_end_waterway_time!=null){
                        ps.setLong  (26, in_end_waterway_time);     //水路入目的港口时间
                    }else {
                        ps.setLong  (26, 0L);                    //水路入目的港口时间
                    }
                    if (unload_ship_time!=null){
                        ps.setLong  (27, unload_ship_time);         //水路卸船时间
                    }else {
                        ps.setLong  (27, 0L);                    //水路卸船时间
                    }

                    //on duplicate key
                    if (StringUtils.isNotBlank(vehicle_code)){
                        ps.setString(28,  vehicle_code);               //车型
                    }else {
                        ps.setString(28,  "0");                     //车型
                    }
                    if (ddjrq!=null){
                        ps.setLong  (29,  epc.getDDJRQ());             //整车物流接收STD日
                    }else {
                        ps.setLong  (29,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cjhdh)){
                        ps.setString(30,  cjhdh);                      //任务单号
                    }else {
                        ps.setString(30,  "0");                     //任务单号
                    }
                    if (dpzrq!=null){
                        ps.setLong  (31,  dpzrq);                      //配板日期
                    }else {
                        ps.setLong  (31,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cpzdbh)){
                        ps.setString(32,  cpzdbh);                     //配载单编号
                    }else {
                        ps.setString(32,  "0");                     //配载单编号
                    }
                    if (assign_time!=null){
                        ps.setLong  (33,  assign_time);                //指派运输商日期
                    }else {
                        ps.setLong  (33,  0L);                      //指派运输商日期
                    }
                    if (StringUtils.isNotBlank(assign_name)){
                        ps.setString(34,  assign_name);                //指派承运商名称
                    }else {
                        ps.setString(34,  "0");                     //指派承运商名称
                    }
                    if (actual_out_time!=null){
                        ps.setLong  (35,  actual_out_time);            //出库日期
                    }else {
                        ps.setLong  (35,  0L);                      //出库日期
                    }
                    if (shipment_time!=null){
                        ps.setLong  (36, shipment_time);              //起运日期 公路/铁路
                    }else {
                        ps.setLong  (36, 0L);                      //起运日期 公路/铁路
                    }
                    if (StringUtils.isNotBlank(vjsydm)){
                        ps.setString(37, vjsydm);                     //运输车号
                    }else {
                        ps.setString(37, "0");                     //运输车号
                    }
                    if (StringUtils.isNotBlank(start_city_name)){
                        ps.setString(38, start_city_name);            //始发城市
                    }else {
                        ps.setString(38, "0");                     //始发城市
                    }
                    if (StringUtils.isNotBlank(end_city_name)){
                        ps.setString(39, end_city_name);              //目的城市
                    }else {
                        ps.setString(39, "0");                     //目的城市
                    }
                    if (StringUtils.isNotBlank(dealer_name)){
                        ps.setString(40, dealer_name);                //经销商代码(名称)
                    }else {
                        ps.setString(40, "0");                     //经销商代码(名称)
                    }
                    if (StringUtils.isNotBlank(cjsdbh)){
                        ps.setString(41, epc.getCJSDBH());            //结算单编号
                    }else {
                        ps.setString(41, "0");                     //结算单编号
                    }
                    if (StringUtils.isNotBlank(cjsdbh)){
                        ps.setString(42, epc.getCJSDBH());            //结算单编号
                    }else {
                        ps.setString(42, "0");                     //结算单编号
                    }

                    //新添加铁水出入站台/港口的十二个字段
                    if (StringUtils.isNotBlank(start_platform_name)){
                        ps.setString(43, start_platform_name);        //铁路开始站台
                    }else {
                        ps.setString(43, "0");                     //铁路开始站台
                    }
                    if (StringUtils.isNotBlank(end_platform_name)){
                        ps.setString(44, end_platform_name);          //铁路目的站台
                    }else {
                        ps.setString(44, "0");                     //铁路目的站台
                    }
                    if (in_start_platform_time!=null){
                        ps.setLong  (45, in_start_platform_time);     //铁路入开始站台时间
                    }else {
                        ps.setLong  (45, 0L);                      //铁路入开始站台时间
                    }
                    if (out_start_platform_time!=null){
                        ps.setLong  (46, out_start_platform_time);    //铁路出开始站台时间
                    }else {
                        ps.setLong  (46, 0L);                      //铁路出开始站台时间
                    }
                    if (in_end_platform_time!=null){
                        ps.setLong  (47, in_end_platform_time);       //铁路入目的站台时间
                    }else {
                        ps.setLong  (47, 0L);                      //铁路入目的站台时间
                    }
                    if (unload_railway_time!=null){
                        ps.setLong  (48, unload_railway_time);        //铁路卸车时间
                    }else {
                        ps.setLong  (48, 0L);                      //铁路卸车时间
                    }
                    if (StringUtils.isNotBlank(start_waterway_name)){
                        ps.setString(49, start_waterway_name);       //水路开始港口名称
                    }else {
                        ps.setString(49, "0");                    //水路开始港口名称
                    }
                    if (StringUtils.isNotBlank(end_waterway_name)){
                        ps.setString(50, end_waterway_name);        //水路目的港口名称
                    }else {
                        ps.setString(50, "0");                   //水路目的港口名称
                    }
                    if (in_start_waterway_time!=null){
                        ps.setLong  (51, in_start_waterway_time);   //水路入开始港口时间
                    }else {
                        ps.setLong  (51, 0L);                    //水路入开始港口时间
                    }
                    if (end_start_waterway_time!=null){
                        ps.setLong  (52, end_start_waterway_time);  //水路出开始港口时间
                    }else {
                        ps.setLong  (52, 0L);                    //水路出开始港口时间
                    }
                    if (in_end_waterway_time!=null){
                        ps.setLong  (53, in_end_waterway_time);     //水路入目的港口时间
                    }else {
                        ps.setLong  (53, 0L);                    //水路入目的港口时间
                    }
                    if (unload_ship_time!=null){
                        ps.setLong  (54, unload_ship_time);         //水路卸船时间
                    }else {
                        ps.setLong  (54, 0L);                    //水路卸船时间
                    }

                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(2)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build()));
        //==============================================dwm_vlms_sptb02处理END=============================================================================//


        env.execute("一单到底合表开始");
        log.info("base_station_data job任务开始执行");

    }
}
