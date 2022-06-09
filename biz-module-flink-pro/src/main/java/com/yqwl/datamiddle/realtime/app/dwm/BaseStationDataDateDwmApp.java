package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.OotdTransition;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.DimUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
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
 * @Description: 监听 dwd_vlms_base_station_data 的binlog，更新铁水运单中的，发车站台的入出时间，收车站的入出时间
 * @Author: muqing
 * @Date: 2022/06/08
 * @Version: V1.0
 */
@Slf4j
public class BaseStationDataDateDwmApp {

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
                .tableList("")
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
