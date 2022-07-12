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

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmAppSPTB02 {

    //2019-12-31 00:00:00
    private static final long START = 1577721600000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;

    public static void main(String[] args) throws Exception {
        //Configuration configuration1 = new Configuration();
        //flink parallelism=16 savepoint state
        // configuration1.setString("execution.savepoint.path",
        // "hdfs://hadoop195:8020/flink/checkpoint/be4ef18e17df472b9c62fee199e8fb21/4641d34ccce58cab8466c991a62ed103/chk-66");
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
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


        Properties properties = new Properties();
        // 遇到错误跳过
        properties.setProperty("debezium.inconsistent.schema.handing.mode","warn");
        properties.setProperty("debezium.event.deserialization.failure.handling.mode","warn");


        //mysql消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwm_vlms_sptb02")
                //.tableList("data_middle_flink.dwm_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .debeziumProperties(properties)
                .distributionFactorUpper(10.0d)   //针对cdc的错误算法的更改
                .build();

        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSourceStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "OneOrderToEndDwmAppSPTB02MysqlSource").uid("OneOrderToEndDwmAppSPTB02MysqlSourceStream").name("OneOrderToEndDwmAppSPTB02MysqlSourceStream");
        //==============================================dwm_vlms_sptb02处理START=============================================================================//
        SingleOutputStreamOperator<OotdTransition> oneOrderToEndUpdateProcess = mysqlSourceStream.process(new ProcessFunction<String, OotdTransition>() {
            @Override
            public void processElement(String value, Context ctx, Collector<OotdTransition> out) throws Exception {
                DwmSptb02 dwmSptb02 = JsonPartUtil.getAfterObj(value, DwmSptb02.class);
                //DwmSptb02 dwmSptb02 = JSON.parseObject(value, DwmSptb02.class);
                Long ddjrq1 = dwmSptb02.getDDJRQ();
                if (Objects.nonNull(ddjrq1) && ddjrq1 > 0) {
                    if (ddjrq1 >= START && ddjrq1 <= END) {
                        OotdTransition ootdTransition = new OotdTransition();
                        String cjsdbh = dwmSptb02.getCJSDBH();                                  // 结算单编号
                        String vvin = dwmSptb02.getVVIN();                                      // 底盘号
                        String vehicle_code = dwmSptb02.getVEHICLE_CODE();                      // 车型
                        Long ddjrq = dwmSptb02.getDDJRQ();                                      // 整车物流接收STD日期
                        String cjhdh = dwmSptb02.getCJHDH();                                    // 任务单号
                        Long dphscsj = dwmSptb02.getDPHSCSJ();                                  // 配板日期
                        String vph = dwmSptb02.getVPH();                                        // 配载单编号
                        Long assign_time = dwmSptb02.getASSIGN_TIME();                          // 指派运输商日期
                        String transportName = dwmSptb02.getTRANSPORT_NAME();                   // 指派承运商名称
                        Long actual_out_time = dwmSptb02.getACTUAL_OUT_TIME();                  // 出库日期
                        Long shipment_time = dwmSptb02.getSHIPMENT_TIME();                      // 起运日期 公路/铁路
                        String vjsydm = dwmSptb02.getVJSYDM();                                  // 运输车号
                        String start_city_name = dwmSptb02.getSTART_CITY_NAME();                // 始发城市
                        String end_city_name = dwmSptb02.getEND_CITY_NAME();                    // 目的城市
                        String vdwdm = dwmSptb02.getVDWDM();                                    // 经销商代码
                        String dealer_name = dwmSptb02.getDEALER_NAME();                        // 经销商名称
                        //String vysfs = dwmSptb02.getVYSFS();                                  // 原始的运输方式
                        String traffic_type = dwmSptb02.getTRAFFIC_TYPE();                      // dwm的合出来的运输方式
                        String start_warehouse_name = dwmSptb02.getSTART_WAREHOUSE_NAME();      // 开始站台/港口仓库名称
                        String end_warehouse_name = dwmSptb02.getEND_WAREHOUSE_NAME();          // 到达站台/港口仓库名称
                        Long in_start_platform_time = dwmSptb02.getIN_START_PLATFORM_TIME();    // 铁路的入开始站台时间
                        Long out_start_platform_time = dwmSptb02.getOUT_START_PLATFORM_TIME();  // 铁路的出开始站台时间
                        Long in_end_platform_time = dwmSptb02.getIN_END_PLATFORM_TIME();        // 铁路的入目的站台时间
                        Long unload_railway_time = dwmSptb02.getUNLOAD_RAILWAY_TIME();          // 铁路的卸车时间
                        Long in_start_waterway_time = dwmSptb02.getIN_START_WATERWAY_TIME();    // 水路的入开始港口时间
                        Long end_start_waterway_time = dwmSptb02.getEND_START_WATERWAY_TIME();  // 水路的出开始港口时间
                        Long in_end_waterway_time = dwmSptb02.getIN_END_WATERWAY_TIME();        // 水路的入目的港口时间
                        Long unload_ship_time = dwmSptb02.getUNLOAD_SHIP_TIME();                // 水路的卸船时间
                        String highwayWarehouseType = dwmSptb02.getHIGHWAY_WAREHOUSE_TYPE();    // 公路运单物理仓库对应的仓库类型
                        Long warehouse_updatetime = System.currentTimeMillis();                 // 记录更新时间
                        String host_com_code = dwmSptb02.getHOST_COM_CODE();                    // 主机公司代码
                        String base_code = dwmSptb02.getBASE_CODE();                            // 基地代码
                        String base_name = dwmSptb02.getBASE_NAME();                            // 基地名称
                        ootdTransition.setWAREHOUSE_UPDATETIME(warehouse_updatetime);           // 记录更新时间
                        ootdTransition.setVVIN(vvin);                                           // vin码 先赋值
                        String vyscdm = dwmSptb02.getVYSCDM();                                  // 运输车代码 关联 mdac33.vyscdm
                        ootdTransition.setTraffic_type(traffic_type);                           // 运输类型
                        ootdTransition.setHighwayWarehouseType(highwayWarehouseType);           // ootd的赋值 公路运单物理仓库对应的仓库类型
                        // 兜底行为
                        Long dztxcsj  = dwmSptb02.getDZTXCSJ();                                 // 中铁卸车时间  兜底
                        Long dsjcfsj  = dwmSptb02.getDSJCFSJ();                                 // 始发站台/港口实际离场时间(实际出发时间)
                        Long dgpsdhsj = dwmSptb02.getDGPSDHSJ();                                // 目的站台,港口 到港/到站时间

                        if (StringUtils.isNotBlank(cjsdbh)) {
                            ootdTransition.setCJSDBH(cjsdbh);
                        }

                        // 第一个运单的落值情况
                        if (("G".equals(traffic_type) && "T1".equals(highwayWarehouseType)) || StringUtils.equalsAny(traffic_type, "T", "S")) {
                            if (StringUtils.isNotBlank(vehicle_code)) {
                                ootdTransition.setVEHICLE_CODE(vehicle_code);
                                /**
                                 * 根据产品编码查获取产品名称
                                 */
                                String mdac12Sql = "select VCPMC from " + KafkaTopicConst.ODS_VLMS_MDAC12 + " where CCPDM = '" + vehicle_code + "' limit 1 ";
                                JSONObject mdac12 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC12, mdac12Sql, vehicle_code);
                                if (mdac12 != null) {
                                    ootdTransition.setVEHICLE_NAME(mdac12.getString("VCPMC"));
                                }
                            }

                            if (StringUtils.isNotBlank(base_code)) {
                                ootdTransition.setBASE_CODE(base_code);
                            }
                            if (StringUtils.isNotBlank(base_name)) {
                                ootdTransition.setBASE_NAME(base_name);
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
                            // 运输车车牌号
                            if (StringUtils.isNotBlank(vjsydm)) {
                                ootdTransition.setVJSYDM(vjsydm);
                            }
                            if (StringUtils.isNotBlank(start_city_name)) {
                                ootdTransition.setSTART_CITY_NAME(start_city_name);
                            }
                            if (StringUtils.isNotBlank(end_city_name)) {
                                ootdTransition.setEND_CITY_NAME(end_city_name);
                            }
                            // 经销商代码
                            if (StringUtils.isNotBlank(vdwdm)) {
                                ootdTransition.setVDWDM(vdwdm);
                            }
                            // 经销商名称: DWD层sptb02.vdwdm  取自 mdac22.CJXSDM 优先去经销商简称 jxsjc 如果为空,取 jxsmc
                            if (StringUtils.isNotBlank(dealer_name)) {
                                ootdTransition.setDEALER_NAME(dealer_name);
                            }
                            if (StringUtils.isNotBlank(host_com_code)) {
                                ootdTransition.setBRAND(host_com_code);
                            }
                            // 轿运车乘位数 : mdac33.NCYDE 承运定额  使用sptb02.vyscdm关联 mdac33.vyscdm
                            if (StringUtils.isNotBlank(vyscdm)) {
                                String mdac33Sql = "select NCYDE from " + KafkaTopicConst.ODS_VLMS_MDAC33 + " where VYSCDM = '" + vyscdm + "' limit 1 ";
                                JSONObject mdac33 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC33, mdac33Sql, vyscdm);
                                if (mdac33 != null) {
                                    ootdTransition.setJYCCWS(mdac33.getInteger("NCYDE"));
                                }
                            }
                        }

                        //=====================================铁水运单处理=====================================================//
                        if (StringUtils.isNotBlank(traffic_type) && StringUtils.isNotBlank(cjsdbh)) {
                            //铁路运输方式
                            if ("T".equals(traffic_type) || "L1".equals(traffic_type)) {
                                // 开始站台仓库名称
                                if (StringUtils.isNotBlank(start_warehouse_name)) {
                                    ootdTransition.setSTART_PLATFORM_NAME(start_warehouse_name);
                                }
                                // 到达站台仓库名称
                                if (StringUtils.isNotBlank(end_warehouse_name)) {
                                    ootdTransition.setEND_PLATFORM_NAME(end_warehouse_name);
                                }
                                // 铁路的入开始站台时间
                                if (in_start_platform_time != null) {
                                    ootdTransition.setIN_START_PLATFORM_TIME(in_start_platform_time);
                                }
                                // 铁路的出开始站台时间 + 兜底 默认取的是物流溯源节点来更新
                                if (out_start_platform_time != null && out_start_platform_time != 0) {
                                    ootdTransition.setOUT_START_PLATFORM_TIME(out_start_platform_time);
                                } else if (dsjcfsj != null){
                                    // 兜底: 取的是sptb02的实际出发时间
                                    ootdTransition.setOUT_START_PLATFORM_TIME(dsjcfsj);
                                }
                                // 铁路的入目的站台时间 + 兜底 默认取的是物流溯源节点来更新
                                if (in_end_platform_time != null && in_end_platform_time!=0) {
                                    ootdTransition.setIN_END_PLATFORM_TIME(in_end_platform_time);
                                } else if (dgpsdhsj != null){
                                    // 兜底: 取的是sptb02的gps到货时间
                                    ootdTransition.setIN_END_PLATFORM_TIME(dgpsdhsj);
                                }
                                // 中铁卸车时间 + 兜底  默认取的是物流溯源节点来更新
                                if (unload_railway_time != null && unload_railway_time != 0){
                                    ootdTransition.setUNLOAD_RAILWAY_TIME(unload_railway_time);
                                }else if (dztxcsj != null ){
                                    // 兜底: 取的是sptb02的DZTXCSJ (中铁卸车时间)
                                    ootdTransition.setUNLOAD_RAILWAY_TIME(dztxcsj);
                                }
                            }
                            //水路运输方式
                            if ("S".equals(traffic_type) && StringUtils.isNotBlank(cjsdbh)) {
                                // 开始站台仓库名称
                                if (StringUtils.isNotBlank(start_warehouse_name)) {
                                    ootdTransition.setSTART_WATERWAY_NAME(start_warehouse_name);
                                }
                                // 到达站台仓库名称
                                if (StringUtils.isNotBlank(end_warehouse_name)) {
                                    ootdTransition.setEND_WATERWAY_NAME(end_warehouse_name);
                                }
                                // 水路的入开始港口时间
                                if (in_start_waterway_time != null) {
                                    ootdTransition.setIN_START_WATERWAY_TIME(in_start_waterway_time);
                                }
                                // 水路的出开始港口时间 + 兜底 默认取的是物流溯源节点来更新
                                if (end_start_waterway_time != null && end_start_waterway_time !=0) {
                                    ootdTransition.setEND_START_WATERWAY_TIME(end_start_waterway_time);
                                } else if(dsjcfsj != null){
                                    // 兜底: 取的是sptb02的实际出发时间
                                    ootdTransition.setEND_START_WATERWAY_TIME(dsjcfsj);
                                }
                                // 水路的入目的港口时间
                                if (in_end_waterway_time != null && in_end_waterway_time != 0) {
                                    ootdTransition.setIN_END_WATERWAY_TIME(in_end_waterway_time);
                                }else if (dgpsdhsj != null){
                                    // 兜底: 取的是sptb02的gps到货时间
                                    ootdTransition.setIN_END_WATERWAY_TIME(dgpsdhsj);
                                }
                                // 水路的卸船时间
                                if (unload_ship_time != null) {
                                    ootdTransition.setUNLOAD_SHIP_TIME(unload_ship_time);
                                }
                            }
                        }

                        //====================================末端配送==============================================//
                        if ("G".equals(traffic_type) && "T2".equals(highwayWarehouseType) && StringUtils.isNotBlank(cjsdbh)) {

                            // 配板时间
                            ootdTransition.setDISTRIBUTE_BOARD_TIME(dwmSptb02.getDPHSCSJ());
                            // 出库时间
                            ootdTransition.setOUT_DISTRIBUTE_TIME(dwmSptb02.getACTUAL_OUT_TIME());
                            // 指派时间
                            ootdTransition.setDISTRIBUTE_ASSIGN_TIME(dwmSptb02.getASSIGN_TIME());
                            // 承运商名称
                            ootdTransition.setDISTRIBUTE_CARRIER_NAME(dwmSptb02.getTRANSPORT_NAME());
                            // 承运车车牌号
                            ootdTransition.setDISTRIBUTE_VEHICLE_NO(dwmSptb02.getVJSYDM());
                            // 起运时间
                            ootdTransition.setDISTRIBUTE_SHIPMENT_TIME(dwmSptb02.getSHIPMENT_TIME());
                            // 分拨中心 轿运车位数
                            if (StringUtils.isNotBlank(vyscdm)) {
                                String mdac33Sql = "select NCYDE from " + KafkaTopicConst.ODS_VLMS_MDAC33 + " where VYSCDM = '" + vyscdm + "' limit 1 ";
                                JSONObject mdac33 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC33, mdac33Sql, vyscdm);
                                if (mdac33 != null) {
                                    ootdTransition.setDISTRIBUTE_VEHICLE_NUM(mdac33.getInteger("NCYDE"));
                                }
                            }
                        }
                        // 打点到货
                        if (dwmSptb02.getDOT_SITE_TIME() != null) {
                            ootdTransition.setDOT_SITE_TIME(dwmSptb02.getDOT_SITE_TIME());
                        }
                        // 最终到货时间
                        if (dwmSptb02.getFINAL_SITE_TIME() != null) {
                            ootdTransition.setFINAL_SITE_TIME(dwmSptb02.getFINAL_SITE_TIME());
                        }
                        // 对象null值进行默认值赋值
                        OotdTransition bean = JsonPartUtil.getBean(ootdTransition);
                        out.collect(bean);
                    }
                }
            }
        }).uid("OneOrderToEndDwmAppSPTB02UpdateProcess").name("OneOrderToEndDwmAppSPTB02UpdateProcess");

        //------------------------------------------------------------------------更新干线公路的运单----------------------------------------------------------------------//
        SingleOutputStreamOperator<OotdTransition> oneOrderToEndDwmAppSPTB02FilterG = oneOrderToEndUpdateProcess.process(new ProcessFunction<OotdTransition, OotdTransition>() {
            @Override
            public void processElement(OotdTransition value, ProcessFunction<OotdTransition, OotdTransition>.Context ctx, Collector<OotdTransition> out) throws Exception {
                if (StringUtils.equals(value.getTraffic_type(), "G") && "T1".equals(value.getHighwayWarehouseType())) {
                    out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppSPTB02FilterG").name("OneOrderToEndDwmAppSPTB02FilterG");
        oneOrderToEndDwmAppSPTB02FilterG.addSink( JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (" +
                        "VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, VDWDM, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, " +
                        "IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME,  WAREHOUSE_UPDATETIME, BRAND, " +
                        "DISTRIBUTE_BOARD_TIME, OUT_DISTRIBUTE_TIME, DISTRIBUTE_ASSIGN_TIME, " +
                        "DISTRIBUTE_CARRIER_NAME, DISTRIBUTE_VEHICLE_NO, DISTRIBUTE_SHIPMENT_TIME, DOT_SITE_TIME, FINAL_SITE_TIME ,BASE_CODE, BASE_NAME, VEHICLE_NUM, DISTRIBUTE_VEHICLE_NUM )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_CODE), VEHICLE_CODE) ,VEHICLE_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NAME), VEHICLE_NAME), VEHICLE_RECEIVING_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_RECEIVING_TIME), VEHICLE_RECEIVING_TIME), TASK_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TASK_NO), TASK_NO), PLAN_RELEASE_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(PLAN_RELEASE_TIME), PLAN_RELEASE_TIME), \n " +
                        " STOWAGE_NOTE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(STOWAGE_NOTE_NO), STOWAGE_NOTE_NO), ASSIGN_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ASSIGN_TIME), ASSIGN_TIME), CARRIER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(CARRIER_NAME), CARRIER_NAME), ACTUAL_OUT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ACTUAL_OUT_TIME), ACTUAL_OUT_TIME), SHIPMENT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SHIPMENT_TIME), SHIPMENT_TIME) ,TRANSPORT_VEHICLE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TRANSPORT_VEHICLE_NO), TRANSPORT_VEHICLE_NO), START_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(START_CITY_NAME), START_CITY_NAME), END_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(END_CITY_NAME), END_CITY_NAME), VDWDM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VDWDM), VDWDM), DEALER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(DEALER_NAME), DEALER_NAME), \n" +
                        " SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SETTLEMENT_Y1), SETTLEMENT_Y1)," +
                        "  BRAND = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BRAND) , BRAND), " +
                        "  BASE_CODE = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_CODE), BASE_CODE) ,BASE_NAME= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_NAME), BASE_NAME) , VEHICLE_NUM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NUM), VEHICLE_NUM)",
                (ps, ootd) -> {
                    String vvin = ootd.getVVIN();                                        //底盘号
                    String vehicle_code = ootd.getVEHICLE_CODE();                        //车型
                    String vehicle_name = ootd.getVEHICLE_NAME();                        //车型名称
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
                    String vdwdm = ootd.getVDWDM();                                      //经销商代码
                    String dealer_name = ootd.getDEALER_NAME();                          //经销商名称(简称 || 名称)
                    String cjsdbh = ootd.getCJSDBH();                                    //结算单编号
                    String base_code = ootd.getBASE_CODE();                              //基地代码
                    String base_name = ootd.getBASE_NAME();                              //基地名称
                    Integer jyccws = ootd.getJYCCWS();                                   //轿运车车位数
                    Integer distribute_vehicle_num = ootd.getDISTRIBUTE_VEHICLE_NUM();   //末端配送轿运车车位数

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

                    int i = 1;

                    ps.setString(i++, vvin);                          //底盘号
                    ps.setString(i++, vehicle_code);                  //车型
                    ps.setString(i++, vehicle_name);                  //车型名称
                    ps.setLong(i++, ddjrq);                           //整车物流接收STD日
                    ps.setString (i++, cjhdh);                        //任务单号
                    ps.setLong   (i++, dpzrq);                        //配板日期
                    ps.setString (i++, cpzdbh);                       //配载单编号
                    ps.setLong   (i++, assign_time);                  //指派运输商日期
                    ps.setString (i++, assign_name);                  //指派承运商名称
                    ps.setLong  (i++, actual_out_time);               //出库日期
                    ps.setLong  (i++, shipment_time);                 //起运日期 公路/铁路
                    ps.setString(i++, vjsydm);                        //运输车号
                    ps.setString(i++, start_city_name);               //始发城市
                    ps.setString(i++, end_city_name);                 //目的城市
                    ps.setString(i++, vdwdm);                         //经销商代码(名称)
                    ps.setString(i++, dealer_name);                   //经销商代码(名称)
                    ps.setString(i++, cjsdbh);                        //结算单编号


                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(i++, start_platform_name);           //铁路开始站台
                    ps.setString(i++, end_platform_name);             //铁路目的站台
                    ps.setLong  (i++, in_start_platform_time);        //铁路入开始站台时间
                    ps.setLong  (i++, out_start_platform_time);       //铁路出开始站台时间
                    ps.setLong  (i++, in_end_platform_time);          //铁路入目的站台时间
                    ps.setLong  (i++, unload_railway_time);           //铁路卸车时间
                    ps.setString(i++, start_waterway_name);           //水路开始港口名称
                    ps.setString(i++, end_waterway_name);             //水路目的港口名称
                    ps.setLong  (i++, in_start_waterway_time);        //水路入开始港口时间
                    ps.setLong  (i++, end_start_waterway_time);       //水路出开始港口时间
                    ps.setLong  (i++, in_end_waterway_time);          //水路入目的港口时间
                    ps.setLong  (i++, unload_ship_time);              //水路卸船时间
                    ps.setLong  (i++, ootd.getWAREHOUSE_UPDATETIME());//数据更新时间
                    ps.setString(i++, ootd.getBRAND());               //主机公司代码

                    //========================末端配送===============================//
                    ps.setLong  (i++, ootd.getDISTRIBUTE_BOARD_TIME());
                    ps.setLong  (i++, ootd.getOUT_DISTRIBUTE_TIME());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_ASSIGN_TIME());
                    ps.setString(i++, ootd.getDISTRIBUTE_CARRIER_NAME());
                    ps.setString(i++, ootd.getDISTRIBUTE_VEHICLE_NO());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_SHIPMENT_TIME());
                    ps.setLong  (i++, ootd.getDOT_SITE_TIME());
                    ps.setLong  (i++, ootd.getFINAL_SITE_TIME());
                    //-----------------------尾部新加的Base_code,base_name-------------//
                    ps.setString(i++, base_code);
                    ps.setString(i++, base_name);
                    // 轿运车车位数
                    ps.setInt(i++,jyccws);
                    // 末端配送轿运车车位数
                    ps.setInt(i++,distribute_vehicle_num);
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("OneOrderToEndDwmAppSPTB02AddSinkMysqlG").name("OneOrderToEndDwmAppSPTB02FilterAddSinkMysqlG");

        //-------------------------------------------------------------------------------------------更新铁路的运单---------------------------------------------------------------------------------//
        SingleOutputStreamOperator<OotdTransition> oneOrderToEndDwmAppSPTB02FilterT = oneOrderToEndUpdateProcess.process(new ProcessFunction<OotdTransition, OotdTransition>() {
            @Override
            public void processElement(OotdTransition value, ProcessFunction<OotdTransition, OotdTransition>.Context ctx, Collector<OotdTransition> out) throws Exception {
                if (StringUtils.equals(value.getTraffic_type(), "T")) {
                    out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppSPTB02FilterT").name("OneOrderToEndDwmAppSPTB02FilterT");
        oneOrderToEndDwmAppSPTB02FilterT.addSink(JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (" +
                        "VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, VDWDM, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, " +
                        "IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME,  WAREHOUSE_UPDATETIME, BRAND, " +
                        "DISTRIBUTE_BOARD_TIME, OUT_DISTRIBUTE_TIME, DISTRIBUTE_ASSIGN_TIME, " +
                        "DISTRIBUTE_CARRIER_NAME, DISTRIBUTE_VEHICLE_NO, DISTRIBUTE_SHIPMENT_TIME, DOT_SITE_TIME, FINAL_SITE_TIME ,BASE_CODE, BASE_NAME, VEHICLE_NUM, DISTRIBUTE_VEHICLE_NUM )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_CODE), VEHICLE_CODE) ,VEHICLE_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NAME), VEHICLE_NAME), VEHICLE_RECEIVING_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_RECEIVING_TIME), VEHICLE_RECEIVING_TIME), TASK_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TASK_NO), TASK_NO), PLAN_RELEASE_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(PLAN_RELEASE_TIME), PLAN_RELEASE_TIME), \n " +
                        " STOWAGE_NOTE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(STOWAGE_NOTE_NO), STOWAGE_NOTE_NO), ASSIGN_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ASSIGN_TIME), ASSIGN_TIME), CARRIER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(CARRIER_NAME), CARRIER_NAME), ACTUAL_OUT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ACTUAL_OUT_TIME), ACTUAL_OUT_TIME), SHIPMENT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SHIPMENT_TIME), SHIPMENT_TIME) ,TRANSPORT_VEHICLE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TRANSPORT_VEHICLE_NO), TRANSPORT_VEHICLE_NO), START_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(START_CITY_NAME), START_CITY_NAME), END_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(END_CITY_NAME), END_CITY_NAME), VDWDM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VDWDM), VDWDM), DEALER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(DEALER_NAME), DEALER_NAME), \n" +
                        " SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SETTLEMENT_Y1), SETTLEMENT_Y1)," +
                        "  BRAND = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BRAND) , BRAND), " +
                        " START_PLATFORM_NAME = VALUES(START_PLATFORM_NAME), END_PLATFORM_NAME = VALUES(END_PLATFORM_NAME), IN_START_PLATFORM_TIME = VALUES(IN_START_PLATFORM_TIME), OUT_START_PLATFORM_TIME = VALUES(OUT_START_PLATFORM_TIME), IN_END_PLATFORM_TIME = VALUES(IN_END_PLATFORM_TIME), UNLOAD_RAILWAY_TIME = VALUES(UNLOAD_RAILWAY_TIME),  " +
                        "  BASE_CODE = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_CODE), BASE_CODE) ,BASE_NAME= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_NAME), BASE_NAME) , VEHICLE_NUM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NUM), VEHICLE_NUM)",
                (ps, ootd) -> {
                    String vvin = ootd.getVVIN();                                        //底盘号
                    String vehicle_code = ootd.getVEHICLE_CODE();                        //车型
                    String vehicle_name = ootd.getVEHICLE_NAME();                        //车型名称
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
                    String vdwdm = ootd.getVDWDM();                                      //经销商代码
                    String dealer_name = ootd.getDEALER_NAME();                          //经销商名称(简称 || 名称)
                    String cjsdbh = ootd.getCJSDBH();                                    //结算单编号
                    String base_code = ootd.getBASE_CODE();                              //基地代码
                    String base_name = ootd.getBASE_NAME();                              //基地名称
                    Integer jyccws = ootd.getJYCCWS();                                   //轿运车车位数
                    Integer distribute_vehicle_num = ootd.getDISTRIBUTE_VEHICLE_NUM();   //末端配送轿运车车位数

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

                    int i = 1;

                    ps.setString(i++, vvin);                          //底盘号
                    ps.setString(i++, vehicle_code);                  //车型
                    ps.setString(i++, vehicle_name);                  //车型名称
                    ps.setLong(i++, ddjrq);                           //整车物流接收STD日
                    ps.setString (i++, cjhdh);                        //任务单号
                    ps.setLong   (i++, dpzrq);                        //配板日期
                    ps.setString (i++, cpzdbh);                       //配载单编号
                    ps.setLong   (i++, assign_time);                  //指派运输商日期
                    ps.setString (i++, assign_name);                  //指派承运商名称
                    ps.setLong  (i++, actual_out_time);               //出库日期
                    ps.setLong  (i++, shipment_time);                 //起运日期 公路/铁路
                    ps.setString(i++, vjsydm);                        //运输车号
                    ps.setString(i++, start_city_name);               //始发城市
                    ps.setString(i++, end_city_name);                 //目的城市
                    ps.setString(i++, vdwdm);                         //经销商代码(名称)
                    ps.setString(i++, dealer_name);                   //经销商代码(名称)
                    ps.setString(i++, cjsdbh);                        //结算单编号


                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(i++, start_platform_name);           //铁路开始站台
                    ps.setString(i++, end_platform_name);             //铁路目的站台
                    ps.setLong  (i++, in_start_platform_time);        //铁路入开始站台时间
                    ps.setLong  (i++, out_start_platform_time);       //铁路出开始站台时间
                    ps.setLong  (i++, in_end_platform_time);          //铁路入目的站台时间
                    ps.setLong  (i++, unload_railway_time);           //铁路卸车时间
                    ps.setString(i++, start_waterway_name);           //水路开始港口名称
                    ps.setString(i++, end_waterway_name);             //水路目的港口名称
                    ps.setLong  (i++, in_start_waterway_time);        //水路入开始港口时间
                    ps.setLong  (i++, end_start_waterway_time);       //水路出开始港口时间
                    ps.setLong  (i++, in_end_waterway_time);          //水路入目的港口时间
                    ps.setLong  (i++, unload_ship_time);              //水路卸船时间
                    ps.setLong  (i++, ootd.getWAREHOUSE_UPDATETIME());//数据更新时间
                    ps.setString(i++, ootd.getBRAND());               //主机公司代码

                    //========================末端配送===============================//
                    ps.setLong  (i++, ootd.getDISTRIBUTE_BOARD_TIME());
                    ps.setLong  (i++, ootd.getOUT_DISTRIBUTE_TIME());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_ASSIGN_TIME());
                    ps.setString(i++, ootd.getDISTRIBUTE_CARRIER_NAME());
                    ps.setString(i++, ootd.getDISTRIBUTE_VEHICLE_NO());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_SHIPMENT_TIME());
                    ps.setLong  (i++, ootd.getDOT_SITE_TIME());
                    ps.setLong  (i++, ootd.getFINAL_SITE_TIME());
                    //-----------------------尾部新加的Base_code,base_name-------------//
                    ps.setString(i++, base_code);
                    ps.setString(i++, base_name);
                    // 轿运车车位数
                    ps.setInt(i++,jyccws);
                    // 末端配送轿运车车位数
                    ps.setInt(i++,distribute_vehicle_num);

                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("OneOrderToEndDwmAppSPTB02AddSinkMysqlT").name("OneOrderToEndDwmAppSPTB02AddSinkMysqlT");

        //--------------------------------------------------------------------水路运单插入------------------------------------------------------------------------------------------------//
        SingleOutputStreamOperator<OotdTransition> oneOrderToEndDwmAppSPTB02FilterS = oneOrderToEndUpdateProcess.process(new ProcessFunction<OotdTransition, OotdTransition>() {
            @Override
            public void processElement(OotdTransition value, ProcessFunction<OotdTransition, OotdTransition>.Context ctx, Collector<OotdTransition> out) throws Exception {
                if (StringUtils.equals(value.getTraffic_type(), "S")) {
                    out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppSPTB02FilterS").name("OneOrderToEndDwmAppSPTB02FilterS");
        // 插入mysql
        oneOrderToEndDwmAppSPTB02FilterS.addSink(JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (" +
                        "VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, VDWDM, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, " +
                        "IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME,  WAREHOUSE_UPDATETIME, BRAND, " +
                        "DISTRIBUTE_BOARD_TIME, OUT_DISTRIBUTE_TIME, DISTRIBUTE_ASSIGN_TIME, " +
                        "DISTRIBUTE_CARRIER_NAME, DISTRIBUTE_VEHICLE_NO, DISTRIBUTE_SHIPMENT_TIME, DOT_SITE_TIME, FINAL_SITE_TIME ,BASE_CODE, BASE_NAME, VEHICLE_NUM, DISTRIBUTE_VEHICLE_NUM )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_CODE), VEHICLE_CODE) ,VEHICLE_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NAME), VEHICLE_NAME), VEHICLE_RECEIVING_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_RECEIVING_TIME), VEHICLE_RECEIVING_TIME), TASK_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TASK_NO), TASK_NO), PLAN_RELEASE_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(PLAN_RELEASE_TIME), PLAN_RELEASE_TIME), \n " +
                        " STOWAGE_NOTE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(STOWAGE_NOTE_NO), STOWAGE_NOTE_NO), ASSIGN_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ASSIGN_TIME), ASSIGN_TIME), CARRIER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(CARRIER_NAME), CARRIER_NAME), ACTUAL_OUT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ACTUAL_OUT_TIME), ACTUAL_OUT_TIME), SHIPMENT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SHIPMENT_TIME), SHIPMENT_TIME) ,TRANSPORT_VEHICLE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TRANSPORT_VEHICLE_NO), TRANSPORT_VEHICLE_NO), START_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(START_CITY_NAME), START_CITY_NAME), END_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(END_CITY_NAME), END_CITY_NAME), VDWDM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VDWDM), VDWDM), DEALER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(DEALER_NAME), DEALER_NAME), \n" +
                        " SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SETTLEMENT_Y1), SETTLEMENT_Y1)," +
                        " START_WATERWAY_NAME = VALUES(START_WATERWAY_NAME), END_WATERWAY_NAME = VALUES(END_WATERWAY_NAME), IN_START_WATERWAY_TIME = VALUES(IN_START_WATERWAY_TIME), END_START_WATERWAY_TIME = VALUES(END_START_WATERWAY_TIME), IN_END_WATERWAY_TIME = VALUES(IN_END_WATERWAY_TIME), UNLOAD_SHIP_TIME = VALUES(UNLOAD_SHIP_TIME) ,  BRAND = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BRAND) , BRAND), " +
                        "  BASE_CODE = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_CODE), BASE_CODE) ,BASE_NAME= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_NAME), BASE_NAME) , VEHICLE_NUM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NUM), VEHICLE_NUM)",
               (ps, ootd) -> {
                    String vvin = ootd.getVVIN();                                        //底盘号
                    String vehicle_code = ootd.getVEHICLE_CODE();                        //车型
                    String vehicle_name = ootd.getVEHICLE_NAME();                        //车型名称
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
                    String vdwdm = ootd.getVDWDM();                                      //经销商代码
                    String dealer_name = ootd.getDEALER_NAME();                          //经销商名称(简称 || 名称)
                    String cjsdbh = ootd.getCJSDBH();                                    //结算单编号
                    String base_code = ootd.getBASE_CODE();                              //基地代码
                    String base_name = ootd.getBASE_NAME();                              //基地名称
                    Integer jyccws = ootd.getJYCCWS();                                   //轿运车车位数
                    Integer distribute_vehicle_num = ootd.getDISTRIBUTE_VEHICLE_NUM();   //末端配送轿运车车位数

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

                    int i = 1;

                    ps.setString(i++, vvin);                          //底盘号
                    ps.setString(i++, vehicle_code);                  //车型
                    ps.setString(i++, vehicle_name);                  //车型名称
                    ps.setLong(i++, ddjrq);                           //整车物流接收STD日
                    ps.setString (i++, cjhdh);                        //任务单号
                    ps.setLong   (i++, dpzrq);                        //配板日期
                    ps.setString (i++, cpzdbh);                       //配载单编号
                    ps.setLong   (i++, assign_time);                  //指派运输商日期
                    ps.setString (i++, assign_name);                  //指派承运商名称
                    ps.setLong  (i++, actual_out_time);               //出库日期
                    ps.setLong  (i++, shipment_time);                 //起运日期 公路/铁路
                    ps.setString(i++, vjsydm);                        //运输车号
                    ps.setString(i++, start_city_name);               //始发城市
                    ps.setString(i++, end_city_name);                 //目的城市
                    ps.setString(i++, vdwdm);                         //经销商代码(名称)
                    ps.setString(i++, dealer_name);                   //经销商代码(名称)
                    ps.setString(i++, cjsdbh);                        //结算单编号


                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(i++, start_platform_name);           //铁路开始站台
                    ps.setString(i++, end_platform_name);             //铁路目的站台
                    ps.setLong  (i++, in_start_platform_time);        //铁路入开始站台时间
                    ps.setLong  (i++, out_start_platform_time);       //铁路出开始站台时间
                    ps.setLong  (i++, in_end_platform_time);          //铁路入目的站台时间
                    ps.setLong  (i++, unload_railway_time);           //铁路卸车时间
                    ps.setString(i++, start_waterway_name);           //水路开始港口名称
                    ps.setString(i++, end_waterway_name);             //水路目的港口名称
                    ps.setLong  (i++, in_start_waterway_time);        //水路入开始港口时间
                    ps.setLong  (i++, end_start_waterway_time);       //水路出开始港口时间
                    ps.setLong  (i++, in_end_waterway_time);          //水路入目的港口时间
                    ps.setLong  (i++, unload_ship_time);              //水路卸船时间
                    ps.setLong  (i++, ootd.getWAREHOUSE_UPDATETIME());//数据更新时间
                    ps.setString(i++, ootd.getBRAND());               //主机公司代码

                    //========================末端配送===============================//
                    ps.setLong  (i++, ootd.getDISTRIBUTE_BOARD_TIME());
                    ps.setLong  (i++, ootd.getOUT_DISTRIBUTE_TIME());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_ASSIGN_TIME());
                    ps.setString(i++, ootd.getDISTRIBUTE_CARRIER_NAME());
                    ps.setString(i++, ootd.getDISTRIBUTE_VEHICLE_NO());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_SHIPMENT_TIME());
                    ps.setLong  (i++, ootd.getDOT_SITE_TIME());
                    ps.setLong  (i++, ootd.getFINAL_SITE_TIME());
                    //-----------------------尾部新加的Base_code,base_name-------------//
                    ps.setString(i++, base_code);
                    ps.setString(i++, base_name);
                    // 轿运车车位数
                    ps.setInt(i++,jyccws);
                    // 末端配送轿运车车位数
                    ps.setInt(i++,distribute_vehicle_num);
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("OneOrderToEndDwmAppSPTB02AddSinkMysqlS").name("OneOrderToEndDwmAppSPTB02AddSinkMysqlS");

        //---------------------------------------------------------------------公路末端配送的插入-----------------------------------------------------------------------------------------//
        SingleOutputStreamOperator<OotdTransition> oneOrderToEndDwmAppSPTB02FilterEndG = oneOrderToEndUpdateProcess.process(new ProcessFunction<OotdTransition, OotdTransition>() {
            @Override
            public void processElement(OotdTransition value, ProcessFunction<OotdTransition, OotdTransition>.Context ctx, Collector<OotdTransition> out) throws Exception {
                if (StringUtils.equals(value.getTraffic_type(), "G") && "T2".equals(value.getHighwayWarehouseType())) {
                    out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppSPTB02FilterEndG").name("OneOrderToEndDwmAppSPTB02FilterEndG");
        oneOrderToEndDwmAppSPTB02FilterEndG.addSink(JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (" +
                        "VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, VDWDM, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, " +
                        "IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME,  WAREHOUSE_UPDATETIME, BRAND, " +
                        "DISTRIBUTE_BOARD_TIME, OUT_DISTRIBUTE_TIME, DISTRIBUTE_ASSIGN_TIME, " +
                        "DISTRIBUTE_CARRIER_NAME, DISTRIBUTE_VEHICLE_NO, DISTRIBUTE_SHIPMENT_TIME, DOT_SITE_TIME, FINAL_SITE_TIME ,BASE_CODE, BASE_NAME, VEHICLE_NUM, DISTRIBUTE_VEHICLE_NUM )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_CODE), VEHICLE_CODE) ,VEHICLE_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NAME), VEHICLE_NAME), VEHICLE_RECEIVING_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_RECEIVING_TIME), VEHICLE_RECEIVING_TIME), TASK_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TASK_NO), TASK_NO), PLAN_RELEASE_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(PLAN_RELEASE_TIME), PLAN_RELEASE_TIME), \n " +
                        " STOWAGE_NOTE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(STOWAGE_NOTE_NO), STOWAGE_NOTE_NO), ASSIGN_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ASSIGN_TIME), ASSIGN_TIME), CARRIER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(CARRIER_NAME), CARRIER_NAME), ACTUAL_OUT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(ACTUAL_OUT_TIME), ACTUAL_OUT_TIME), SHIPMENT_TIME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SHIPMENT_TIME), SHIPMENT_TIME) ,TRANSPORT_VEHICLE_NO=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(TRANSPORT_VEHICLE_NO), TRANSPORT_VEHICLE_NO), START_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(START_CITY_NAME), START_CITY_NAME), END_CITY_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(END_CITY_NAME), END_CITY_NAME), VDWDM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VDWDM), VDWDM), DEALER_NAME=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(DEALER_NAME), DEALER_NAME), \n" +
                        " SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(SETTLEMENT_Y1), SETTLEMENT_Y1)," +
                        " DISTRIBUTE_BOARD_TIME = VALUES(DISTRIBUTE_BOARD_TIME), OUT_DISTRIBUTE_TIME = VALUES(OUT_DISTRIBUTE_TIME), DISTRIBUTE_ASSIGN_TIME = VALUES(DISTRIBUTE_ASSIGN_TIME), DISTRIBUTE_CARRIER_NAME = VALUES(DISTRIBUTE_CARRIER_NAME), DISTRIBUTE_VEHICLE_NO = VALUES(DISTRIBUTE_VEHICLE_NO) , DISTRIBUTE_SHIPMENT_TIME = VALUES(DISTRIBUTE_SHIPMENT_TIME) , DISTRIBUTE_VEHICLE_NUM= VALUES(DISTRIBUTE_VEHICLE_NUM)," +
                        " BRAND = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BRAND) , BRAND), " +
                        " BASE_CODE = if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_CODE), BASE_CODE) ,BASE_NAME= if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(BASE_NAME), BASE_NAME) , VEHICLE_NUM=if(SETTLEMENT_Y1 = '' or VALUES(SETTLEMENT_Y1) < SETTLEMENT_Y1, VALUES(VEHICLE_NUM), VEHICLE_NUM)",

               (ps, ootd) -> {
                    String vvin = ootd.getVVIN();                                        // 底盘号
                    String vehicle_code = ootd.getVEHICLE_CODE();                        // 车型
                    String vehicle_name = ootd.getVEHICLE_NAME();                        // 车型名称
                    Long ddjrq = ootd.getDDJRQ();                                        // 整车物流接收STD日
                    String cjhdh = ootd.getCJHDH();                                      // 任务单号
                    Long dpzrq = ootd.getDPZRQ();                                        // 配板日期
                    String cpzdbh = ootd.getCPZDBH();                                    // 配载单编号
                    Long assign_time = ootd.getASSIGN_TIME();                            // 指派运输商日期
                    String assign_name = ootd.getASSIGN_NAME();                          // 指派承运商名称
                    Long actual_out_time = ootd.getACTUAL_OUT_TIME();                    // 出库日期
                    Long shipment_time = ootd.getSHIPMENT_TIME();                        // 起运日期 公路/铁路
                    String vjsydm = ootd.getVJSYDM();                                    // 运输车号
                    String start_city_name = ootd.getSTART_CITY_NAME();                  // 始发城市
                    String end_city_name = ootd.getEND_CITY_NAME();                      // 目的城市
                    String vdwdm = ootd.getVDWDM();                                      // 经销商代码
                    String dealer_name = ootd.getDEALER_NAME();                          // 经销商名称(简称 || 名称)
                    String cjsdbh = ootd.getCJSDBH();                                    // 结算单编号
                    String base_code = ootd.getBASE_CODE();                              // 基地代码
                    String base_name = ootd.getBASE_NAME();                              // 基地名称
                    Integer jyccws = ootd.getJYCCWS();                                   // 轿运车车位数
                    Integer distribute_vehicle_num = ootd.getDISTRIBUTE_VEHICLE_NUM();   // 末端配送轿运车车位数

                    //新添加铁水出入站台/港口的十二个字段
                    String start_platform_name = ootd.getSTART_PLATFORM_NAME();          // 铁路开始站台
                    String end_platform_name = ootd.getEND_PLATFORM_NAME();              // 铁路目的站台
                    Long in_start_platform_time = ootd.getIN_START_PLATFORM_TIME();      // 铁路入开始站台时间
                    Long out_start_platform_time = ootd.getOUT_START_PLATFORM_TIME();    // 铁路出开始站台时间
                    Long in_end_platform_time = ootd.getIN_END_PLATFORM_TIME();          // 铁路入目的站台时间
                    Long unload_railway_time = ootd.getUNLOAD_RAILWAY_TIME();            // 铁路卸车时间
                    String start_waterway_name = ootd.getSTART_WATERWAY_NAME();          // 水路开始港口名称
                    String end_waterway_name = ootd.getEND_WATERWAY_NAME();              // 水路目的港口名称
                    Long in_start_waterway_time = ootd.getIN_START_WATERWAY_TIME();      // 水路入开始港口时间
                    Long end_start_waterway_time = ootd.getEND_START_WATERWAY_TIME();    // 水路出开始港口时间
                    Long in_end_waterway_time = ootd.getIN_END_WATERWAY_TIME();          // 水路入目的港口时间
                    Long unload_ship_time = ootd.getUNLOAD_SHIP_TIME();                  // 水路卸船时间

                    int i = 1;

                    ps.setString(i++, vvin);                          // 底盘号
                    ps.setString(i++, vehicle_code);                  // 车型
                    ps.setString(i++, vehicle_name);                  // 车型名称
                    ps.setLong(i++, ddjrq);                           // 整车物流接收STD日
                    ps.setString (i++, cjhdh);                        // 任务单号
                    ps.setLong   (i++, dpzrq);                        // 配板日期
                    ps.setString (i++, cpzdbh);                       // 配载单编号
                    ps.setLong   (i++, assign_time);                  // 指派运输商日期
                    ps.setString (i++, assign_name);                  // 指派承运商名称
                    ps.setLong  (i++, actual_out_time);               // 出库日期
                    ps.setLong  (i++, shipment_time);                 // 起运日期 公路/铁路
                    ps.setString(i++, vjsydm);                        // 运输车号
                    ps.setString(i++, start_city_name);               // 始发城市
                    ps.setString(i++, end_city_name);                 // 目的城市
                    ps.setString(i++, vdwdm);                         // 经销商代码(名称)
                    ps.setString(i++, dealer_name);                   // 经销商代码(名称)
                    ps.setString(i++, cjsdbh);                        // 结算单编号


                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(i++, start_platform_name);           // 铁路开始站台
                    ps.setString(i++, end_platform_name);             // 铁路目的站台
                    ps.setLong  (i++, in_start_platform_time);        // 铁路入开始站台时间
                    ps.setLong  (i++, out_start_platform_time);       // 铁路出开始站台时间
                    ps.setLong  (i++, in_end_platform_time);          // 铁路入目的站台时间
                    ps.setLong  (i++, unload_railway_time);           // 铁路卸车时间
                    ps.setString(i++, start_waterway_name);           // 水路开始港口名称
                    ps.setString(i++, end_waterway_name);             // 水路目的港口名称
                    ps.setLong  (i++, in_start_waterway_time);        // 水路入开始港口时间
                    ps.setLong  (i++, end_start_waterway_time);       // 水路出开始港口时间
                    ps.setLong  (i++, in_end_waterway_time);          // 水路入目的港口时间
                    ps.setLong  (i++, unload_ship_time);              // 水路卸船时间
                    ps.setLong  (i++, ootd.getWAREHOUSE_UPDATETIME());// 数据更新时间
                    ps.setString(i++, ootd.getBRAND());               // 主机公司代码

                    //========================末端配送===============================//
                    ps.setLong  (i++, ootd.getDISTRIBUTE_BOARD_TIME());
                    ps.setLong  (i++, ootd.getOUT_DISTRIBUTE_TIME());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_ASSIGN_TIME());
                    ps.setString(i++, ootd.getDISTRIBUTE_CARRIER_NAME());
                    ps.setString(i++, ootd.getDISTRIBUTE_VEHICLE_NO());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_SHIPMENT_TIME());
                    ps.setLong  (i++, ootd.getDOT_SITE_TIME());
                    ps.setLong  (i++, ootd.getFINAL_SITE_TIME());
                    //-----------------------尾部新加的Base_code,base_name-------------//
                    ps.setString(i++, base_code);
                    ps.setString(i++, base_name);
                    // 轿运车车位数
                    ps.setInt(i++,jyccws);
                    // 末端配送轿运车车位数
                    ps.setInt(i++,distribute_vehicle_num);

                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("OneOrderToEndDwmAppSPTB02SinkMysqlEndG").name("OneOrderToEndDwmAppSPTB02SinkMysqlEndG");


       /* //5.sptb02与一单到底对应的字段插入mysql
        // 29个字段
        oneOrderToEndUpdateProcess.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (" +
                        "VIN, VEHICLE_CODE, VEHICLE_NAME, VEHICLE_RECEIVING_TIME, TASK_NO, PLAN_RELEASE_TIME, " +
                        "STOWAGE_NOTE_NO, ASSIGN_TIME, CARRIER_NAME, ACTUAL_OUT_TIME, SHIPMENT_TIME ,TRANSPORT_VEHICLE_NO, START_CITY_NAME, END_CITY_NAME, VDWDM, DEALER_NAME,SETTLEMENT_Y1," +
                        "START_PLATFORM_NAME, END_PLATFORM_NAME, IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME, IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME, START_WATERWAY_NAME, END_WATERWAY_NAME, " +
                        "IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME, " +
                        "IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME,  WAREHOUSE_UPDATETIME, BRAND, " +
                        "DISTRIBUTE_BOARD_TIME, OUT_DISTRIBUTE_TIME, DISTRIBUTE_ASSIGN_TIME, " +
                        "DISTRIBUTE_CARRIER_NAME, DISTRIBUTE_VEHICLE_NO, DISTRIBUTE_SHIPMENT_TIME, DOT_SITE_TIME, FINAL_SITE_TIME ,BASE_CODE, BASE_NAME, VEHICLE_NUM, DISTRIBUTE_VEHICLE_NUM )\n" +
                        "VALUES\n" +
                        "        ( ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "       VEHICLE_CODE=?,VEHICLE_NAME=?, VEHICLE_RECEIVING_TIME=?, TASK_NO=?, PLAN_RELEASE_TIME=?, \n " +
                        " STOWAGE_NOTE_NO=?, ASSIGN_TIME=?, CARRIER_NAME=?, ACTUAL_OUT_TIME=?, SHIPMENT_TIME=? ,TRANSPORT_VEHICLE_NO=?, START_CITY_NAME=?, END_CITY_NAME=?, VDWDM=?, DEALER_NAME=?, \n" +
                        " SETTLEMENT_Y1= if(SETTLEMENT_Y1 = '' or ? < SETTLEMENT_Y1, ?, SETTLEMENT_Y1)," +
                        " START_PLATFORM_NAME = ?, END_PLATFORM_NAME = ?, IN_START_PLATFORM_TIME = ?, OUT_START_PLATFORM_TIME = ?, IN_END_PLATFORM_TIME = ?, UNLOAD_RAILWAY_TIME = ?, START_WATERWAY_NAME = ?, END_WATERWAY_NAME = ?, " +
                        " IN_START_WATERWAY_TIME = ?, " +
                        " END_START_WATERWAY_TIME = ?, IN_END_WATERWAY_TIME = ?, UNLOAD_SHIP_TIME = ? ,  WAREHOUSE_UPDATETIME = ? , BRAND = ?, " +
                        " DISTRIBUTE_BOARD_TIME = ?, OUT_DISTRIBUTE_TIME = ?, DISTRIBUTE_ASSIGN_TIME = ? , DISTRIBUTE_CARRIER_NAME = ?, DISTRIBUTE_VEHICLE_NO = ? , DISTRIBUTE_SHIPMENT_TIME = ? ," +
                        " DOT_SITE_TIME = ?, FINAL_SITE_TIME = ? , BASE_CODE = ? ,BASE_NAME= ? , VEHICLE_NUM=?, DISTRIBUTE_VEHICLE_NUM=?",
                (ps, ootd) -> {
                    String vvin = ootd.getVVIN();                                        //底盘号
                    String vehicle_code = ootd.getVEHICLE_CODE();                        //车型
                    String vehicle_name = ootd.getVEHICLE_NAME();                        //车型名称
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
                    String vdwdm = ootd.getVDWDM();                                      //经销商代码
                    String dealer_name = ootd.getDEALER_NAME();                          //经销商名称(简称 || 名称)
                    String cjsdbh = ootd.getCJSDBH();                                    //结算单编号
                    String base_code = ootd.getBASE_CODE();                              //基地代码
                    String base_name = ootd.getBASE_NAME();                              //基地名称
                    Integer jyccws = ootd.getJYCCWS();                                   //轿运车车位数
                    Integer distribute_vehicle_num = ootd.getDISTRIBUTE_VEHICLE_NUM();   //末端配送轿运车车位数

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

                    int i = 1;

                    ps.setString(i++, vvin);                          //底盘号
                    ps.setString(i++, vehicle_code);                  //车型
                    ps.setString(i++, vehicle_name);                  //车型名称
                    ps.setLong(i++, ddjrq);                           //整车物流接收STD日
                    ps.setString (i++, cjhdh);                        //任务单号
                    ps.setLong   (i++, dpzrq);                        //配板日期
                    ps.setString (i++, cpzdbh);                       //配载单编号
                    ps.setLong   (i++, assign_time);                  //指派运输商日期
                    ps.setString (i++, assign_name);                  //指派承运商名称
                    ps.setLong  (i++, actual_out_time);               //出库日期
                    ps.setLong  (i++, shipment_time);                 //起运日期 公路/铁路
                    ps.setString(i++, vjsydm);                        //运输车号
                    ps.setString(i++, start_city_name);               //始发城市
                    ps.setString(i++, end_city_name);                 //目的城市
                    ps.setString(i++, vdwdm);                         //经销商代码(名称)
                    ps.setString(i++, dealer_name);                   //经销商代码(名称)
                    ps.setString(i++, cjsdbh);                        //结算单编号


                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(i++, start_platform_name);           //铁路开始站台
                    ps.setString(i++, end_platform_name);             //铁路目的站台
                    ps.setLong  (i++, in_start_platform_time);        //铁路入开始站台时间
                    ps.setLong  (i++, out_start_platform_time);       //铁路出开始站台时间
                    ps.setLong  (i++, in_end_platform_time);          //铁路入目的站台时间
                    ps.setLong  (i++, unload_railway_time);           //铁路卸车时间
                    ps.setString(i++, start_waterway_name);           //水路开始港口名称
                    ps.setString(i++, end_waterway_name);             //水路目的港口名称
                    ps.setLong  (i++, in_start_waterway_time);        //水路入开始港口时间
                    ps.setLong  (i++, end_start_waterway_time);       //水路出开始港口时间
                    ps.setLong  (i++, in_end_waterway_time);          //水路入目的港口时间
                    ps.setLong  (i++, unload_ship_time);              //水路卸船时间
                    ps.setLong  (i++, ootd.getWAREHOUSE_UPDATETIME());//数据更新时间
                    ps.setString(i++, ootd.getBRAND());               //主机公司代码

                    //========================末端配送===============================//
                    ps.setLong  (i++, ootd.getDISTRIBUTE_BOARD_TIME());
                    ps.setLong  (i++, ootd.getOUT_DISTRIBUTE_TIME());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_ASSIGN_TIME());
                    ps.setString(i++, ootd.getDISTRIBUTE_CARRIER_NAME());
                    ps.setString(i++, ootd.getDISTRIBUTE_VEHICLE_NO());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_SHIPMENT_TIME());
                    ps.setLong  (i++, ootd.getDOT_SITE_TIME());
                    ps.setLong  (i++, ootd.getFINAL_SITE_TIME());
                    //-----------------------尾部新加的Base_code,base_name-------------//
                    ps.setString(i++, base_code);
                    ps.setString(i++, base_name);
                    // 轿运车车位数
                    ps.setInt(i++,jyccws);
                    // 末端配送轿运车车位数
                    ps.setInt(i++,distribute_vehicle_num);

                    //on duplicate key
                    ps.setString(i++, vehicle_code);                 //车型
                    ps.setString(i++, vehicle_name);                 //车型名称
                    ps.setLong  (i++, ootd.getDDJRQ());              //整车物流接收STD日
                    ps.setString(i++, cjhdh);                        //任务单号
                    ps.setLong  (i++, dpzrq);                        //配板日期
                    ps.setString(i++, cpzdbh);                       //配载单编号
                    ps.setLong  (i++, assign_time);                  //指派运输商日期
                    ps.setString(i++, assign_name);                  //指派承运商名称
                    ps.setLong  (i++, actual_out_time);              //出库日期
                    ps.setLong  (i++, shipment_time);                //起运日期 公路/铁路
                    ps.setString(i++, vjsydm);                       //运输车号
                    ps.setString(i++, start_city_name);              //始发城市
                    ps.setString(i++, end_city_name);                //目的城市
                    ps.setString(i++, vdwdm);                        //经销商代码(名称)
                    ps.setString(i++, dealer_name);                  //经销商代码(名称)
                    ps.setString(i++, cjsdbh);                       //结算单编号
                    ps.setString(i++, cjsdbh);                       //结算单编号

                    //新添加铁水出入站台/港口的十二个字段
                    ps.setString(i++, start_platform_name);           //铁路开始站台
                    ps.setString(i++, end_platform_name);             //铁路目的站台
                    ps.setLong  (i++, in_start_platform_time);        //铁路入开始站台时间
                    ps.setLong  (i++, out_start_platform_time);       //铁路出开始站台时间
                    ps.setLong  (i++, in_end_platform_time);          //铁路入目的站台时间
                    ps.setLong  (i++, unload_railway_time);           //铁路卸车时间
                    ps.setString(i++, start_waterway_name);           //水路开始港口名称
                    ps.setString(i++, end_waterway_name);             //水路目的港口名称
                    ps.setLong  (i++, in_start_waterway_time);        //水路入开始港口时间
                    ps.setLong  (i++, end_start_waterway_time);       //水路出开始港口时间
                    ps.setLong  (i++, in_end_waterway_time);          //水路入目的港口时间
                    ps.setLong  (i++, unload_ship_time);              //水路卸船时间
                    ps.setLong  (i++, ootd.getWAREHOUSE_UPDATETIME());//更新时间
                    ps.setString(i++, ootd.getBRAND());               //主机公司代码

                    ps.setLong  (i++, ootd.getDISTRIBUTE_BOARD_TIME());
                    ps.setLong  (i++, ootd.getOUT_DISTRIBUTE_TIME());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_ASSIGN_TIME());
                    ps.setString(i++, ootd.getDISTRIBUTE_CARRIER_NAME());
                    ps.setString(i++, ootd.getDISTRIBUTE_VEHICLE_NO());
                    ps.setLong  (i++, ootd.getDISTRIBUTE_SHIPMENT_TIME());
                    ps.setLong  (i++, ootd.getDOT_SITE_TIME());
                    ps.setLong  (i++, ootd.getFINAL_SITE_TIME());
                    //-----------------------尾部新加的Base_code,base_name-------------//
                    ps.setString(i++, base_code);
                    ps.setString(i++, base_name);
                    // 轿运车车位数
                    ps.setInt(i++,jyccws);
                    // 末端配送轿运车车位数
                    ps.setInt(i++,distribute_vehicle_num);
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).setParallelism(1).uid("OneOrderToEndDwmAppSPTB02SinkMysql-dwm_vlms_one_order_to_end").name("OneOrderToEndDwmAppSPTB02SinkMysql-dwm_vlms_one_order_to_end");
        //==============================================dwm_vlms_sptb02处理END=============================================================================//
*/
        env.execute("Dwm_SPTB02合OneOrderToEnd");

    }
}
