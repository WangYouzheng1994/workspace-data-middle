package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.*;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmAppSPTB02 {

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
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwm_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");



        //==============================================dwm_vlms_sptb02处理START=============================================================================//
        //3.进行实体类转换
        //转换sptb02为实体类
        SingleOutputStreamOperator<OotdTransition> mapOotdTransition = mysqlSource.map(new MapFunction<String, OotdTransition>() {
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
                Long warehouse_createtime = dwmSptb02.getWAREHOUSE_CREATETIME();        //记录创建时间
                Long warehouse_updatetime = dwmSptb02.getWAREHOUSE_UPDATETIME();        //记录更新时间
                ootdTransition.setWAREHOUSE_CREATETIME(warehouse_createtime);
                ootdTransition.setWAREHOUSE_UPDATETIME(warehouse_updatetime);

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

        //ootdAddCarNameStream.print("结果数据输出：");
        //5.sptb02与一单到底对应的字段插入mysql
        ootdAddCarNameStream.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME, WAREHOUSE_CREATETIME, WAREHOUSE_UPDATETIME  )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=?,VEHICLE_NAME=?, VEHICLE_RECEIVING_TIME=?, TASK_NO=?, PLAN_RELEASE_TIME=?, \n " +
                        "STOWAGE_NOTE_NO=?, ASSIGN_TIME=?, CARRIER_NAME=?, ACTUAL_OUT_TIME=?, SHIPMENT_TIME=? ,TRANSPORT_VEHICLE_NO=?, START_CITY_NAME=?, END_CITY_NAME=?, DEALER_NAME=?, \n" +
                        "SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or ? < SETTLEMENT_Y1, ?, SETTLEMENT_Y1)," +
                        "START_PLATFORM_NAME = ?, END_PLATFORM_NAME = ?, IN_START_PLATFORM_TIME = ?, OUT_START_PLATFORM_TIME = ?, IN_END_PLATFORM_TIME = ?, UNLOAD_RAILWAY_TIME = ?, START_WATERWAY_NAME = ?, END_WATERWAY_NAME = ?, IN_START_WATERWAY_TIME = ?, " +
                        "END_START_WATERWAY_TIME = ?, IN_END_WATERWAY_TIME = ?, UNLOAD_SHIP_TIME = ? , WAREHOUSE_CREATETIME = ?, WAREHOUSE_UPDATETIME = ? ",
                (ps, epc) -> {
                    String vvin = epc.getVVIN();                                        //底盘号
                    String vehicle_code = epc.getVEHICLE_CODE();                        //车型
                    String vehicle_nmae = epc.getVEHICLE_NMAE();                        //车型名称
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
                    if (StringUtils.isNotBlank(vehicle_code)){
                        ps.setString(3,  vehicle_nmae);               //车型名称
                    }else {
                        ps.setString(3,  "0");                     //车型名称
                    }
                    if (ddjrq!=null){
                        ps.setLong  (4,  epc.getDDJRQ());             //整车物流接收STD日
                    }else {
                        ps.setLong  (4,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cjhdh)){
                        ps.setString(5,  cjhdh);                      //任务单号
                    }else {
                        ps.setString(5,  "0");                     //任务单号
                    }
                    if (dpzrq!=null){
                        ps.setLong  (6,  dpzrq);                      //配板日期
                    }else {
                        ps.setLong  (6,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cpzdbh)){
                        ps.setString(7,  cpzdbh);                     //配载单编号
                    }else {
                        ps.setString(7,  "0");                     //配载单编号
                    }
                    if (assign_time!=null){
                        ps.setLong  (8,  assign_time);                //指派运输商日期
                    }else {
                        ps.setLong  (8,  0L);                      //指派运输商日期
                    }
                    if (StringUtils.isNotBlank(assign_name)){
                        ps.setString(9,  assign_name);                //指派承运商名称
                    }else {
                        ps.setString(9,  "0");                     //指派承运商名称
                    }
                    if (actual_out_time!=null){
                        ps.setLong  (10,  actual_out_time);            //出库日期
                    }else {
                        ps.setLong  (10,  0L);                      //出库日期
                    }
                    if (shipment_time!=null){
                        ps.setLong  (11, shipment_time);              //起运日期 公路/铁路
                    }else {
                        ps.setLong  (11, 0L);                      //起运日期 公路/铁路
                    }
                    if (StringUtils.isNotBlank(vjsydm)){
                        ps.setString(12, vjsydm);                     //运输车号
                    }else {
                        ps.setString(12, "0");                     //运输车号
                    }
                    if (StringUtils.isNotBlank(start_city_name)){
                        ps.setString(13, start_city_name);            //始发城市
                    }else {
                        ps.setString(13, "0");                     //始发城市
                    }
                    if (StringUtils.isNotBlank(end_city_name)){
                        ps.setString(14, end_city_name);              //目的城市
                    }else {
                        ps.setString(14, "0");                     //目的城市
                    }
                    if (StringUtils.isNotBlank(dealer_name)){
                        ps.setString(15, dealer_name);                //经销商代码(名称)
                    }else {
                        ps.setString(15, "0");                     //经销商代码(名称)
                    }
                    if (StringUtils.isNotBlank(cjsdbh)){
                        ps.setString(16, epc.getCJSDBH());            //结算单编号
                    }else {
                        ps.setString(16, "0");                     //结算单编号
                    }

                    //新添加铁水出入站台/港口的十二个字段
                    if (StringUtils.isNotBlank(start_platform_name)){
                        ps.setString(17, start_platform_name);        //铁路开始站台
                    }else {
                        ps.setString(17, "0");                     //铁路开始站台
                    }
                    if (StringUtils.isNotBlank(end_platform_name)){
                        ps.setString(18, end_platform_name);          //铁路目的站台
                    }else {
                        ps.setString(18, "0");                     //铁路目的站台
                    }
                    if (in_start_platform_time!=null){
                        ps.setLong  (19, in_start_platform_time);     //铁路入开始站台时间
                    }else {
                        ps.setLong  (19, 0L);                      //铁路入开始站台时间
                    }
                    if (out_start_platform_time!=null){
                        ps.setLong  (20, out_start_platform_time);    //铁路出开始站台时间
                    }else {
                        ps.setLong  (20, 0L);                      //铁路出开始站台时间
                    }
                    if (in_end_platform_time!=null){
                        ps.setLong  (21, in_end_platform_time);       //铁路入目的站台时间
                    }else {
                        ps.setLong  (21, 0L);                      //铁路入目的站台时间
                    }
                    if (unload_railway_time!=null){
                        ps.setLong  (22, unload_railway_time);        //铁路卸车时间
                    }else {
                        ps.setLong  (22, 0L);                      //铁路卸车时间
                    }
                    if (StringUtils.isNotBlank(start_waterway_name)){
                        ps.setString(23, start_waterway_name);       //水路开始港口名称
                    }else {
                        ps.setString(23, "0");                    //水路开始港口名称
                    }
                    if (StringUtils.isNotBlank(end_waterway_name)){
                        ps.setString(24, end_waterway_name);        //水路目的港口名称
                    }else {
                        ps.setString(24, "0");                   //水路目的港口名称
                    }
                    if (in_start_waterway_time!=null){
                        ps.setLong  (25, in_start_waterway_time);   //水路入开始港口时间
                    }else {
                        ps.setLong  (25, 0L);                    //水路入开始港口时间
                    }
                    if (end_start_waterway_time!=null){
                        ps.setLong  (26, end_start_waterway_time);  //水路出开始港口时间
                    }else {
                        ps.setLong  (26, 0L);                    //水路出开始港口时间
                    }
                    if (in_end_waterway_time!=null){
                        ps.setLong  (27, in_end_waterway_time);     //水路入目的港口时间
                    }else {
                        ps.setLong  (27, 0L);                    //水路入目的港口时间
                    }
                    if (unload_ship_time!=null){
                        ps.setLong  (28, unload_ship_time);         //水路卸船时间
                    }else {
                        ps.setLong  (28, 0L);                    //水路卸船时间
                    }

                    //on duplicate key
                    if (StringUtils.isNotBlank(vehicle_code)){
                        ps.setString(29,  vehicle_code);               //车型
                    }else {
                        ps.setString(29,  "0");                     //车型
                    }
                    if (StringUtils.isNotBlank(vehicle_code)){
                        ps.setString(30,  vehicle_nmae);               //车型名称
                    }else {
                        ps.setString(30,  "0");                     //车型名称
                    }
                    if (ddjrq!=null){
                        ps.setLong  (31,  epc.getDDJRQ());             //整车物流接收STD日
                    }else {
                        ps.setLong  (31,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cjhdh)){
                        ps.setString(32,  cjhdh);                      //任务单号
                    }else {
                        ps.setString(32,  "0");                     //任务单号
                    }
                    if (dpzrq!=null){
                        ps.setLong  (33,  dpzrq);                      //配板日期
                    }else {
                        ps.setLong  (33,  0L);                      //整车物流接收STD日
                    }
                    if (StringUtils.isNotBlank(cpzdbh)){
                        ps.setString(34,  cpzdbh);                     //配载单编号
                    }else {
                        ps.setString(34,  "0");                     //配载单编号
                    }
                    if (assign_time!=null){
                        ps.setLong  (35,  assign_time);                //指派运输商日期
                    }else {
                        ps.setLong  (35,  0L);                      //指派运输商日期
                    }
                    if (StringUtils.isNotBlank(assign_name)){
                        ps.setString(36,  assign_name);                //指派承运商名称
                    }else {
                        ps.setString(36,  "0");                     //指派承运商名称
                    }
                    if (actual_out_time!=null){
                        ps.setLong  (37,  actual_out_time);            //出库日期
                    }else {
                        ps.setLong  (37,  0L);                      //出库日期
                    }
                    if (shipment_time!=null){
                        ps.setLong  (38, shipment_time);              //起运日期 公路/铁路
                    }else {
                        ps.setLong  (38, 0L);                      //起运日期 公路/铁路
                    }
                    if (StringUtils.isNotBlank(vjsydm)){
                        ps.setString(39, vjsydm);                     //运输车号
                    }else {
                        ps.setString(39, "0");                     //运输车号
                    }
                    if (StringUtils.isNotBlank(start_city_name)){
                        ps.setString(40, start_city_name);            //始发城市
                    }else {
                        ps.setString(40, "0");                     //始发城市
                    }
                    if (StringUtils.isNotBlank(end_city_name)){
                        ps.setString(41, end_city_name);              //目的城市
                    }else {
                        ps.setString(41, "0");                     //目的城市
                    }
                    if (StringUtils.isNotBlank(dealer_name)){
                        ps.setString(42, dealer_name);                //经销商代码(名称)
                    }else {
                        ps.setString(42, "0");                     //经销商代码(名称)
                    }
                    if (StringUtils.isNotBlank(cjsdbh)){
                        ps.setString(43, epc.getCJSDBH());            //结算单编号
                    }else {
                        ps.setString(43, "0");                     //结算单编号
                    }
                    if (StringUtils.isNotBlank(cjsdbh)){
                        ps.setString(44, epc.getCJSDBH());            //结算单编号
                    }else {
                        ps.setString(44, "0");                     //结算单编号
                    }

                    //新添加铁水出入站台/港口的十二个字段
                    if (StringUtils.isNotBlank(start_platform_name)){
                        ps.setString(45, start_platform_name);        //铁路开始站台
                    }else {
                        ps.setString(45, "0");                     //铁路开始站台
                    }
                    if (StringUtils.isNotBlank(end_platform_name)){
                        ps.setString(46, end_platform_name);          //铁路目的站台
                    }else {
                        ps.setString(46, "0");                     //铁路目的站台
                    }
                    if (in_start_platform_time!=null){
                        ps.setLong  (47, in_start_platform_time);     //铁路入开始站台时间
                    }else {
                        ps.setLong  (47, 0L);                      //铁路入开始站台时间
                    }
                    if (out_start_platform_time!=null){
                        ps.setLong  (48, out_start_platform_time);    //铁路出开始站台时间
                    }else {
                        ps.setLong  (48, 0L);                      //铁路出开始站台时间
                    }
                    if (in_end_platform_time!=null){
                        ps.setLong  (49, in_end_platform_time);       //铁路入目的站台时间
                    }else {
                        ps.setLong  (49, 0L);                      //铁路入目的站台时间
                    }
                    if (unload_railway_time!=null){
                        ps.setLong  (50, unload_railway_time);        //铁路卸车时间
                    }else {
                        ps.setLong  (50, 0L);                      //铁路卸车时间
                    }
                    if (StringUtils.isNotBlank(start_waterway_name)){
                        ps.setString(51, start_waterway_name);       //水路开始港口名称
                    }else {
                        ps.setString(51, "0");                    //水路开始港口名称
                    }
                    if (StringUtils.isNotBlank(end_waterway_name)){
                        ps.setString(52, end_waterway_name);        //水路目的港口名称
                    }else {
                        ps.setString(52, "0");                   //水路目的港口名称
                    }
                    if (in_start_waterway_time!=null){
                        ps.setLong  (53, in_start_waterway_time);   //水路入开始港口时间
                    }else {
                        ps.setLong  (53, 0L);                    //水路入开始港口时间
                    }
                    if (end_start_waterway_time!=null){
                        ps.setLong  (54, end_start_waterway_time);  //水路出开始港口时间
                    }else {
                        ps.setLong  (54, 0L);                    //水路出开始港口时间
                    }
                    if (in_end_waterway_time!=null){
                        ps.setLong  (55, in_end_waterway_time);     //水路入目的港口时间
                    }else {
                        ps.setLong  (55, 0L);                    //水路入目的港口时间
                    }
                    if (unload_ship_time!=null){
                        ps.setLong  (56, unload_ship_time);         //水路卸船时间
                    }else {
                        ps.setLong  (56, 0L);                    //水路卸船时间
                    }
                    ps.setLong  (57, epc.getWAREHOUSE_CREATETIME());
                    ps.setLong  (58, epc.getWAREHOUSE_UPDATETIME());

                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(10000)
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


        env.execute("Dwm_SPTB02合OneOrderToEnd");
        log.info("base_station_data job任务开始执行");

    }
}
