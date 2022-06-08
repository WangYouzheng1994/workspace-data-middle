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
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
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
        // 过滤出BASE_STATION_DATA_Epc的表
        DataStream<String> filterBsdEpcDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_middle_flink") && jo.getString("tableName").equals("dwd_vlms_base_station_data_epc")) {
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

        filterBsdEpcDs.print("epcFilter数据输出:");
        //BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = filterBsdEpcDs.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                DwdBaseStationDataEpc dataBsdEpc = jsonObject.getObject("after", DwdBaseStationDataEpc.class);
                Timestamp ts = jsonObject.getTimestamp("ts"); //取ts作为时间戳字段
                dataBsdEpc.setTs(ts);
                return dataBsdEpc;
            }
        }).uid("transitionBASE_STATION_DATA_EPCMap").name("transitionBASE_STATION_DATA_EPCMap");

        mapBsdEpc.print("epc数据输出:");
        mapBsdEpc.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, CP9_OFFLINE_TIME, BASE_NAME, BASE_CODE )\n" +
                        "VALUES\n" +
                        "        (?, ?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "  VIN=?, CP9_OFFLINE_TIME=? ,BASE_NAME=?,\n" +
                        "        BASE_CODE=?",
                (ps, epc) -> {
                    ps.setString(1, epc.getVIN());
                    ps.setLong(2, epc.getCP9_OFFLINE_TIME());
                    ps.setString(3, epc.getBASE_NAME());
                    ps.setString(4, epc.getBASE_CODE());
                    ps.setString(5, epc.getVIN());
                    ps.setLong(6, epc.getCP9_OFFLINE_TIME());
                    ps.setString(7, epc.getBASE_NAME());
                    ps.setString(8, epc.getBASE_CODE());
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

        //==============================================dwm_vlms_sptb02处理====================================================================//
        //2.进行数据过滤
        SingleOutputStreamOperator<String> filterSptb02 = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_middle_flink") && jo.getString("tableName").equals("dwm_vlms_sptb02")) {
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

        //3.进行实体类转换
        //转换sptb02为实体类
        SingleOutputStreamOperator<OotdTransition> mapOotdTransition = filterSptb02.map(new MapFunction<String, OotdTransition>() {
            @Override
            public OotdTransition map(String sptb02Value) throws Exception {
                OotdTransition ootdTransition = new OotdTransition();
                JSONObject jsonObject = JSON.parseObject(sptb02Value);
                DwmSptb02 dwmSptb02 = jsonObject.getObject("after", DwmSptb02.class);
                String cjsdbh = dwmSptb02.getCJSDBH();
                String vehicle_code = dwmSptb02.getVEHICLE_CODE();      //车型
                String vvin = dwmSptb02.getVVIN();                      //底盘号
                Long ddjrq = dwmSptb02.getDDJRQ();                      //整车物流接收STD日期
                String cjhdh = dwmSptb02.getCJHDH();                    //任务单号
                Long dpzrq = dwmSptb02.getDPZRQ();                      //配板日期
                String cpzdbh = dwmSptb02.getCPZDBH();                  //配载单编号
                Long assign_time = dwmSptb02.getASSIGN_TIME();          //指派运输商日期
                String ccysdm = dwmSptb02.getCCYSDM();                  //指派承运商名称
                Long actual_out_time = dwmSptb02.getACTUAL_OUT_TIME();  //出库日期
                Long shipment_time = dwmSptb02.getSHIPMENT_TIME();      //起运日期 公路/铁路
                String vjsydm = dwmSptb02.getVJSYDM();                  //运输车号
                String start_city_name = dwmSptb02.getSTART_CITY_NAME();//始发城市
                String end_city_name = dwmSptb02.getEND_CITY_NAME();    //目的城市
                String dealer_name = dwmSptb02.getDEALER_NAME();        //经销商代码(名称)
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
                    if (dpzrq != null) {
                        ootdTransition.setDPZRQ(dpzrq);
                    }
                    if (StringUtils.isNotBlank(cpzdbh)) {
                        ootdTransition.setCPZDBH(cpzdbh);
                    }
                    if (assign_time != null) {
                        ootdTransition.setASSIGN_TIME(assign_time);
                    }
                    if (StringUtils.isNotBlank(ccysdm)) {
                        ootdTransition.setASSIGN_NAME(ccysdm);
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
                        return "此条sql无vehicle_code";
                    }

                    @Override
                    public void join(OotdTransition ootd, JSONObject dimInfoJsonObj) throws Exception {
                        if (dimInfoJsonObj.getString("CCPDM") != null) {
                            ootd.setVEHICLE_NMAE(dimInfoJsonObj.getString("VCPMC"));
                        }
                    }
                }, 60, TimeUnit.SECONDS).uid("base+VEHICLE_NMAE").name("base+VEHICLE_NMAE");
//        ootdAddCarNameStream.print("合并了车的流:");

        //filterBsdDs.print("bSD:");


//{"database":"data_middle_flink","before":{},
// "after":{"SAMPLE_STATUS":0,"STATION_CODE":"K","IN_WAREHOUSE_NAME":"照宝库","LAST_UPDATE_DATE":1652206207000000,
// "EFFECT_FLAG":"1","IDNUM":9513,"BRAND_CODE":"LFPH","SAMPLE_U_T_C":1652205865000000,
// "BATCH_CODE":"20220510180425313f57d9e75cc1445cda2e847b962d2763d","FILE_STATUS":0,"IS_CORRECT":-1,
// "MOTORCYCLETYPE_NAME":"红旗","MSG_ID":"7fc1dc4d-d18c-413c-ac27-889e4b271472","CREATE_TIMESTAMP":1652206207578087,
// "IN_WAREHOUSE_CODE":"K","SHOP_NO":"K","OPERATE_TYPE":"InStock","OPERATOR_ID":"805","BRAND_NAME":"红旗",
// "VIN":"LFPHC7CE4N2A04481","FILE_TYPE":0,"MOTORCYCLETYPE_CODE":"EQM5","STATION_TYPE":"TypeD","PUSH_BATCH":"9f62fb8f-352b-4768-8798-b83bd85c3074"},
// "type":"insert","tableName":"dwd_vlms_base_station_data","ts":1654614547474}


        //==============================================dwd_base_station_data处理====================================================================//
        // 过滤出BASE_STATION_DATA的表
        DataStream<String> filterBsdDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_middle_flink") && jo.getString("tableName").equals("dwd_vlms_base_station_data")) {
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

        //转换BASE_STATION_DATA为实体类
        SingleOutputStreamOperator<DwdBaseStationData> mapBsd = filterBsdDs.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String kafkaBsdValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdValue);
                DwdBaseStationData dataBsd = jsonObject.getObject("after", DwdBaseStationData.class);
                Timestamp ts = jsonObject.getTimestamp("ts");
                String vin = dataBsd.getVIN();              //vin码
//                dataBsd.get
                dataBsd.setTs(ts);
                return dataBsd;
            }
        }).uid("transitionBASE_STATION_DATA").name("transitionBASE_STATION_DATA");




        env.execute("合表开始");
        log.info("base_station_data job任务开始执行");

    }
}
