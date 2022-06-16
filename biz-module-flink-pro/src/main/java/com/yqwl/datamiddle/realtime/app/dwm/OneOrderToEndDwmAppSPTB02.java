package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.OotdTransition;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
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
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwm_vlms_sptb02")
                //.tableList("data_middle_flink.dwm_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSourceStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");

        //==============================================dwm_vlms_sptb02处理START=============================================================================//
        SingleOutputStreamOperator<OotdTransition> oneOrderToEndUpdateProcess = mysqlSourceStream.process(new ProcessFunction<String, OotdTransition>() {
            @Override
            public void processElement(String value, Context ctx, Collector<OotdTransition> out) throws Exception {
                OotdTransition ootdTransition = new OotdTransition();
                DwmSptb02 dwmSptb02 = JsonPartUtil.getAfterObj(value, DwmSptb02.class);
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
                String host_com_code = dwmSptb02.getHOST_COM_CODE();                    //主机公司代码
                ootdTransition.setWAREHOUSE_CREATETIME(warehouse_createtime);           //记录创建时间
                ootdTransition.setWAREHOUSE_UPDATETIME(warehouse_updatetime);           //记录更新时间


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
                    if (StringUtils.isNotBlank(host_com_code)) {
                        ootdTransition.setBRAND(host_com_code);
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

                    }

                    /**
                     * 根据产品编码查获取产品名称
                     */
                    if (StringUtils.isNotBlank(vehicle_code)) {
                        String mdac12Sql = "select * from " + KafkaTopicConst.ODS_VLMS_MDAC12 + " where CCPDM = '" + vehicle_code + "' limit 1 ";
                        JSONObject mdac12 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC12, mdac12Sql, vehicle_code);
                        if (mdac12 != null) {
                            ootdTransition.setVEHICLE_NMAE(mdac12.getString("VCPMC"));
                        }
                    }
                }

                //对象null值进行默认值赋值
                OotdTransition bean = JsonPartUtil.getBean(ootdTransition);
                out.collect(bean);
            }
        }).uid("oneOrderToEndUpdateProcess").name("oneOrderToEndUpdateProcess");


        //5.sptb02与一单到底对应的字段插入mysql
        // 29个字段
        oneOrderToEndUpdateProcess.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (" +
                        "VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, " +
                        "IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME, WAREHOUSE_CREATETIME, WAREHOUSE_UPDATETIME, BRAND  )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=?,VEHICLE_NAME=?, VEHICLE_RECEIVING_TIME=?, TASK_NO=?, PLAN_RELEASE_TIME=?, \n " +
                        "STOWAGE_NOTE_NO=?, ASSIGN_TIME=?, CARRIER_NAME=?, ACTUAL_OUT_TIME=?, SHIPMENT_TIME=? ,TRANSPORT_VEHICLE_NO=?, START_CITY_NAME=?, END_CITY_NAME=?, DEALER_NAME=?, \n" +
                        "SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or ? < SETTLEMENT_Y1, ?, SETTLEMENT_Y1)," +
                        "START_PLATFORM_NAME = ?, END_PLATFORM_NAME = ?, IN_START_PLATFORM_TIME = ?, OUT_START_PLATFORM_TIME = ?, IN_END_PLATFORM_TIME = ?, UNLOAD_RAILWAY_TIME = ?, START_WATERWAY_NAME = ?, END_WATERWAY_NAME = ?, " +
                        "IN_START_WATERWAY_TIME = ?, " +
                        "END_START_WATERWAY_TIME = ?, IN_END_WATERWAY_TIME = ?, UNLOAD_SHIP_TIME = ? , WAREHOUSE_CREATETIME = ?, WAREHOUSE_UPDATETIME = ? , BRAND = ?",
                (ps, ootd) -> {
                    String vvin = ootd.getVVIN();                                        //底盘号
                    String vehicle_code = ootd.getVEHICLE_CODE();                        //车型
                    String vehicle_name = ootd.getVEHICLE_NMAE();                        //车型名称
                    Long ddjrq = ootd.getDDJRQ();                                        //整车物流接收STD日
                    String cjhdh = ootd.getCJHDH();                                      //任务单号
                    Long dpzrq = ootd.getDPZRQ();                                        //配板日期
                    String cpzdbh = ootd.getCPZDBH();                                    //配载单编号
                    Long assign_time = ootd.getASSIGN_TIME();                            //指派运输商日期
                    String assign_name = ootd.getASSIGN_NAME();                          //指派承运商名称
                    Long actual_out_time = ootd.getACTUAL_OUT_TIME();                    //出库日期
                    Long shipment_time = ootd.getSHIPMENT_TIME();                        //起运日期 公路/铁路
                    String vjsydm = ootd.getVJSYDM();                                    //运输车号
                    String start_city_name = ootd.getSTART_CITY_NAME();                  //始发城市
                    String end_city_name = ootd.getEND_CITY_NAME();                      //目的城市
                    String dealer_name = ootd.getDEALER_NAME();                          //经销商代码(名称)
                    String cjsdbh = ootd.getCJSDBH();                                    //结算单编号

                    //新添加铁水出入站台/港口的十二个字段
                    String start_platform_name = ootd.getSTART_PLATFORM_NAME();          //铁路开始站台
                    String end_platform_name = ootd.getEND_PLATFORM_NAME();              //铁路目的站台
                    Long in_start_platform_time = ootd.getIN_START_PLATFORM_TIME();      //铁路入开始站台时间
                    Long out_start_platform_time = ootd.getOUT_START_PLATFORM_TIME();    //铁路出开始站台时间
                    Long in_end_platform_time = ootd.getIN_END_PLATFORM_TIME();          //铁路入目的站台时间
                    Long unload_railway_time = ootd.getUNLOAD_RAILWAY_TIME();            //铁路卸车时间
                    String start_waterway_name = ootd.getSTART_WATERWAY_NAME();          //水路开始港口名称
                    String end_waterway_name = ootd.getEND_WATERWAY_NAME();              //水路目的港口名称
                    Long in_start_waterway_time = ootd.getIN_START_WATERWAY_TIME();      //水路入开始港口时间
                    Long end_start_waterway_time = ootd.getEND_START_WATERWAY_TIME();    //水路出开始港口时间
                    Long in_end_waterway_time = ootd.getIN_END_WATERWAY_TIME();          //水路入目的港口时间
                    Long unload_ship_time = ootd.getUNLOAD_SHIP_TIME();                  //水路卸船时间


                    ps.setString(1, vvin);                        //底盘号
                    ps.setString(2, vehicle_code);               //车型
                    ps.setString(3, vehicle_name);               //车型名称
                    ps.setLong(4, ddjrq);                      //整车物流接收STD日
                    ps.setString(5, cjhdh);                      //任务单号
                    ps.setLong(6, dpzrq);                      //配板日期
                    ps.setString(7, cpzdbh);                     //配载单编号
                    ps.setLong(8, assign_time);                //指派运输商日期
                    ps.setString(9, assign_name);                //指派承运商名称
                    ps.setLong(10, actual_out_time);            //出库日期
                    ps.setLong(11, shipment_time);              //起运日期 公路/铁路
                    ps.setString(12, vjsydm);                     //运输车号
                    ps.setString(13, start_city_name);            //始发城市
                    ps.setString(14, end_city_name);              //目的城市
                    ps.setString(15, dealer_name);                //经销商代码(名称)
                    ps.setString(16, ootd.getCJSDBH());            //结算单编号


                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(17, start_platform_name);        //铁路开始站台
                    ps.setString(18, end_platform_name);          //铁路目的站台
                    ps.setLong(19, in_start_platform_time);       //铁路入开始站台时间
                    ps.setLong(20, out_start_platform_time);      //铁路出开始站台时间
                    ps.setLong(21, in_end_platform_time);         //铁路入目的站台时间
                    ps.setLong(22, unload_railway_time);          //铁路卸车时间
                    ps.setString(23, start_waterway_name);        //水路开始港口名称
                    ps.setString(24, end_waterway_name);          //水路目的港口名称
                    ps.setLong(25, in_start_waterway_time);       //水路入开始港口时间
                    ps.setLong(26, end_start_waterway_time);      //水路出开始港口时间
                    ps.setLong(27, in_end_waterway_time);         //水路入目的港口时间
                    ps.setLong(28, unload_ship_time);             //水路卸船时间
                    ps.setLong(29, ootd.getWAREHOUSE_CREATETIME());//数据创建时间
                    ps.setLong(30, ootd.getWAREHOUSE_UPDATETIME());//数据更新时间
                    ps.setString(31, ootd.getBRAND());             //主机公司代码

                    //on duplicate key
                    ps.setString(32, vehicle_code);               //车型
                    ps.setString(33, vehicle_name);               //车型名称
                    ps.setLong(34, ootd.getDDJRQ());              //整车物流接收STD日
                    ps.setString(35, cjhdh);                      //任务单号
                    ps.setLong(36, dpzrq);                        //配板日期
                    ps.setString(37, cpzdbh);                     //配载单编号
                    ps.setLong(38, assign_time);                  //指派运输商日期
                    ps.setString(39, assign_name);                //指派承运商名称
                    ps.setLong(40, actual_out_time);              //出库日期
                    ps.setLong(41, shipment_time);                //起运日期 公路/铁路
                    ps.setString(42, vjsydm);                     //运输车号
                    ps.setString(43, start_city_name);            //始发城市
                    ps.setString(44, end_city_name);              //目的城市
                    ps.setString(45, dealer_name);                //经销商代码(名称)
                    ps.setString(46, ootd.getCJSDBH());            //结算单编号
                    ps.setString(47, ootd.getCJSDBH());            //结算单编号

                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(48, start_platform_name);        //铁路开始站台
                    ps.setString(49, end_platform_name);          //铁路目的站台
                    ps.setLong(50, in_start_platform_time);       //铁路入开始站台时间
                    ps.setLong(51, out_start_platform_time);      //铁路出开始站台时间
                    ps.setLong(52, in_end_platform_time);         //铁路入目的站台时间
                    ps.setLong(53, unload_railway_time);          //铁路卸车时间
                    ps.setString(54, start_waterway_name);        //水路开始港口名称
                    ps.setString(55, end_waterway_name);          //水路目的港口名称
                    ps.setLong(56, in_start_waterway_time);       //水路入开始港口时间
                    ps.setLong(57, end_start_waterway_time);      //水路出开始港口时间
                    ps.setLong(58, in_end_waterway_time);         //水路入目的港口时间
                    ps.setLong(59, unload_ship_time);             //水路卸船时间
                    ps.setLong(60, ootd.getWAREHOUSE_CREATETIME());//创建时间
                    ps.setLong(61, ootd.getWAREHOUSE_UPDATETIME());//更新时间
                    ps.setString(62, ootd.getBRAND());             //主机公司代码

                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(10000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
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
