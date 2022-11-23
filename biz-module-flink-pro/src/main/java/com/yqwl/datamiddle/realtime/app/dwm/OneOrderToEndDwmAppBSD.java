package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.SimpleBsdSinkOOTD;
import com.yqwl.datamiddle.realtime.bo.DwdBaseStationDataBO;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.TimeConst;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class OneOrderToEndDwmAppBSD {
    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        // 算子不合并
        env.disableOperatorChaining();
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
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        // 1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "OneOrderToEndDwmAppBSDMysqlSource").uid("OneOrderToEndDwmAppBSDMysqlSourceStream").name("OneOrderToEndDwmAppBSDMysqlSourceStream");
        //==============================================dwd_base_station_data处理 START==========================================================================//

        /**
         * 改造思路:
         * 1. 新增扩充dim_vlms_warehouse_rs和dwm_vlms_sptb02表的字段 作为后续判断的依据
         * 2. 根据不同的算子去按条件赋值
         */
        // 2.转换BASE_STATION_DATA为实体类
        SingleOutputStreamOperator<DwdBaseStationDataBO> mapBsd = mysqlSource.map(new MapFunction<String, DwdBaseStationDataBO>() {
            @Override
            public DwdBaseStationDataBO map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationDataBO.class);
            }
        }).uid("OneOrderToEndDwmAppBSDTransitionBASE_STATION_DATA").name("OneOrderToEndDwmAppBSDTransitionBASE_STATION_DATA");

        // 3.新增过滤时间的操作
        SingleOutputStreamOperator<DwdBaseStationDataBO> mapBsdFilterTime = mapBsd.process(new ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>() {
            @Override
            public void processElement(DwdBaseStationDataBO value, ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>.Context ctx, Collector<DwdBaseStationDataBO> out) throws Exception {
                if (value.getSAMPLE_U_T_C() >= TimeConst.DATE_2020_12_01 && value.getSAMPLE_U_T_C() <= TimeConst.DATE_2023_11_28 && StringUtils.isNotBlank(value.getIN_WAREHOUSE_CODE())) {
                    out.collect(value);
                }
            }
        }).uid("OneOrderToEndDwmAppFilter2022Time").name("OneOrderToEndDwmAppFilter2022Time");

        // 4.开始按照条件去给实体类赋值
        SingleOutputStreamOperator<DwdBaseStationDataBO> mapBsdFilterInWarehouseTypeAndTrafficType = mapBsdFilterTime.process(new ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>() {
            @Override
            public void processElement(DwdBaseStationDataBO dwdBaseStationDataBO, ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>.Context ctx, Collector<DwdBaseStationDataBO> out) throws Exception {
                String in_warehouse_code = dwdBaseStationDataBO.getIN_WAREHOUSE_CODE();
                String operate_type = dwdBaseStationDataBO.getOPERATE_TYPE();
                String vin = dwdBaseStationDataBO.getVIN();
                Long sample_u_t_c = dwdBaseStationDataBO.getSAMPLE_U_T_C();
                if (StringUtils.isNotBlank(in_warehouse_code) && StringUtils.isNotBlank(vin) && sample_u_t_c != null) {
                    String getWAREHOUSE_TYPEOfDimRsSql = "SELECT WAREHOUSE_TYPE FROM "+ KafkaTopicConst.DIM_VLMS_WAREHOUSE_RS+ " WHERE WAREHOUSE_CODE = '" + in_warehouse_code + "' limit 1 ";
                    JSONObject wareHouseTypeOfDimRsJson = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB02D1, getWAREHOUSE_TYPEOfDimRsSql, in_warehouse_code);
                    // 这里的判断是给后续的两个插入sql的前提  (I: 更新入库日期,入库仓库名称,入库仓库代码,更新更新时间 只需要这一个判断条件)
                    if (wareHouseTypeOfDimRsJson != null) {
                        String warehouse_type = wareHouseTypeOfDimRsJson.getString("WAREHOUSE_TYPE");
                        dwdBaseStationDataBO.setWAREHOUSE_TYPEfDimRS(warehouse_type);
                        // 更新末端配送入库时间的字段赋值的判断前提 II: operate_type=InStock && TRAFFIC_TYPE = 'G' && WAREHOUSE_TYPE = 'T2'
                        if (StringUtils.equals(operate_type, "InStock")){
                            String getTRAFFIC_TYPEOfSPTB02SQL = "SELECT TRAFFIC_TYPE FROM "+ KafkaTopicConst.DWM_VLMS_SPTB02 + " WHERE VVIN = '" + vin + "' AND TRAFFIC_TYPE ='G' limit 1 ";
                            JSONObject trafficTypeOfDimRsJson = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB02D1, getTRAFFIC_TYPEOfSPTB02SQL, vin);
                            // 有值就是TRAFFIC_TYPE = 'G',是符合条件的
                            if (trafficTypeOfDimRsJson != null){
                                // 然后再去dim_vlms_warehouse_rs里面查询是否符合它的条件 反正最后匹配的时候是看一个条件去赋值,只要最后这个有值,就说明前面的条件匹配
                                String traffic_type = trafficTypeOfDimRsJson.getString("TRAFFIC_TYPE");
                                dwdBaseStationDataBO.setTRAFFIC_TYPEOfSPTB02(traffic_type);
                            }
                        }
                    }
                    out.collect(dwdBaseStationDataBO);
                }
            }
        }).uid("OneOrderToEndDwmAppFilterInWarehouseTypeAndTrafficType").name("OneOrderToEndDwmAppFilterInWarehouseTypeAndTrafficType");

        // 先更新出厂日期
        SingleOutputStreamOperator<DwdBaseStationDataBO> mapBsdFilterOperateTypeAndShopNo = mapBsdFilterInWarehouseTypeAndTrafficType.process(new ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>() {
            @Override
            public void processElement(DwdBaseStationDataBO dwdBaseStationDataBO, ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>.Context ctx, Collector<DwdBaseStationDataBO> out) throws Exception {
                String operate_type = dwdBaseStationDataBO.getOPERATE_TYPE();
                String shop_no = dwdBaseStationDataBO.getSHOP_NO();
                if (StringUtils.equals(operate_type, "OutStock") && (StringUtils.equals(shop_no, "DZCP901") || StringUtils.equals(shop_no, "DZCP9"))) {
                        out.collect(dwdBaseStationDataBO);
                }
            }
        }).uid("OneOrderToEndDwmAppFilterOperateTypeAndShopNoFields").name("OneOrderToEndDwmAppFilterOperateTypeAndShopNoFields");
        mapBsdFilterOperateTypeAndShopNo.addSink(JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (VIN, LEAVE_FACTORY_TIME, WAREHOUSE_UPDATETIME, WAREHOUSE_UPDATETIME, WAREHOUSE_CREATETIME) " +
                        " VALUES\n" +
                        " (?, ?, ?, ?) \n" +
                        " ON DUPLICATE KEY UPDATE \n" +
                        " LEAVE_FACTORY_TIME = if(CP9_OFFLINE_TIME < ? AND (LEAVE_FACTORY_TIME = 0 OR LEAVE_FACTORY_TIME > ? ), VALUES(LEAVE_FACTORY_TIME), LEAVE_FACTORY_TIME) , WAREHOUSE_UPDATETIME = VALUES(WAREHOUSE_UPDATETIME) ",
                (ps, bsd) -> {
                    Long nowTime = System.currentTimeMillis();
                    ps.setString(1, bsd.getVIN());
                    ps.setLong  (2, bsd.getSAMPLE_U_T_C());
                    ps.setLong  (3, nowTime);
                    ps.setLong  (4, bsd.getSAMPLE_U_T_C());
                    ps.setLong  (5, bsd.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                PropertiesUtil.getMysqlJDBCConnection())).uid("OneOrderToEndMysqlInsertLeaveFactoryTime").name("OneOrderToEndMysqlInsertLeaveFactoryTime");

        /**
         * 由上面那个算子分化出其他的算子公用
         *  I:更新入库日期,入库仓库名称,入库仓库代码,更新更新时间字段所需要的赋值
         *  * UPDATE dwm_vlms_one_order_to_end e
         *  * JOIN dim_vlms_warehouse_rs a ON a.WAREHOUSE_CODE = 'JL002'
         *  * SET
         *  * e.IN_WAREHOUSE_NAME = '东山站台',
         *  * e.IN_WAREHOUSE_CODE = 'JL002',
         *  * e.IN_SITE_TIME = 1656297798000,
         *  * e.WAREHOUSE_UPDATETIME = 1660198918944
         *  * WHERE
         *  * 	e.VIN = 'LFV3A28W0N3351195'
         *  * 	AND e.LEAVE_FACTORY_TIME < 1656297798000 AND a.WAREHOUSE_TYPE = 'T1' AND ( e.IN_SITE_TIME > 1656297798000
         *  * 		OR e.IN_SITE_TIME = 0
         *  * 	);
         */
        // 5. 给 I 筛选条件赋值
        SingleOutputStreamOperator<DwdBaseStationDataBO> mapBsdFilterWarehouseType = mapBsdFilterInWarehouseTypeAndTrafficType.process(new ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>() {
            @Override
            public void processElement(DwdBaseStationDataBO dwdBaseStationDataBO, ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>.Context ctx, Collector<DwdBaseStationDataBO> out) throws Exception {
                String warehouse_type = dwdBaseStationDataBO.getWAREHOUSE_TYPE();
                if (StringUtils.equals(warehouse_type, "T1") ) {
                    out.collect(dwdBaseStationDataBO);
                }
            }
        }).uid("OneOrderToEndDwmAppWarehouseTypeFields").name("OneOrderToEndDwmAppWarehouseTypeFields");
        // 5.1: 给 I 插入sql  更新入库日期,入库仓库名称,入库仓库代码,更新更新时间字段
        mapBsdFilterWarehouseType.addSink(JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (VIN, IN_WAREHOUSE_NAME, IN_WAREHOUSE_CODE, IN_SITE_TIME, WAREHOUSE_UPDATETIME) " +
                        " VALUES\n" +
                        " ( ?, ?, ?, ?, ?) \n" +
                        " ON DUPLICATE KEY UPDATE \n" +
                        " IN_WAREHOUSE_NAME = VALUES(IN_WAREHOUSE_NAME), " +
                        " IN_WAREHOUSE_CODE = VALUES(IN_WAREHOUSE_CODE), " +
                        " IN_SITE_TIME = if(LEAVE_FACTORY_TIME < ? AND (IN_SITE_TIME = 0 OR IN_SITE_TIME > ?), VALUES(IN_SITE_TIME), IN_SITE_TIME), WAREHOUSE_UPDATETIME = VALUES(WAREHOUSE_UPDATETIME)",
                (ps, bsd) -> {
                    Long nowTime = System.currentTimeMillis();
                    ps.setString(1, bsd.getVIN());
                    if (StringUtils.isNotBlank(bsd.getPHYSICAL_NAME())){
                        ps.setString(2, bsd.getPHYSICAL_NAME());
                    }else {
                        ps.setString(2, "");
                    }
                    ps.setString(3, bsd.getIN_WAREHOUSE_CODE());
                    ps.setLong  (4, bsd.getSAMPLE_U_T_C());
                    ps.setLong  (5, nowTime);
                    ps.setLong  (6, bsd.getSAMPLE_U_T_C());
                    ps.setLong  (7, bsd.getSAMPLE_U_T_C());

                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                PropertiesUtil.getMysqlJDBCConnection())).uid("OneOrderToEndMysqlInsertInSiteTime").name("OneOrderToEndMysqlInsertInSiteTime");

        /** II:更新末端配送入库时间的字段赋值
         * UPDATE dwm_vlms_one_order_to_end e
         * JOIN dim_vlms_warehouse_rs a ON a.WAREHOUSE_CODE = 'JL002'
         * JOIN dwm_vlms_sptb02 s ON e.VIN = s.VVIN
         * SET e.IN_DISTRIBUTE_TIME = 1656297798000,
         * e.WAREHOUSE_UPDATETIME = 1660198918944
         * WHERE
         * 	e.VIN = 'LFV3A28W0N3351195'
         * 	AND e.LEAVE_FACTORY_TIME < 1656297798000
         * 	AND a.WAREHOUSE_TYPE = 'T2'
         * 	AND s.TRAFFIC_TYPE = 'G'
         * 	AND e.IN_SITE_TIME < 1656297798000;
         * 1.先走一个Sptb02的sql去查询TRAFFIC_TYPE字段，然后以此作为匹配条件
         * SELECT TRAFFIC_TYPE FROM dwm_vlms_sptb02 WHERE VVIN = 'LFV3A28W0N3351195' AND TRAFFIC_TYPE ='G';
         * 2.查完后去
         */
        // 6.1 给 II 进行条件的筛选
        SingleOutputStreamOperator<DwdBaseStationDataBO> mapBsdFilterWarehouseTypeAndTrafficType = mapBsdFilterInWarehouseTypeAndTrafficType.process(new ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>() {
            @Override
            public void processElement(DwdBaseStationDataBO dwdBaseStationDataBO, ProcessFunction<DwdBaseStationDataBO, DwdBaseStationDataBO>.Context ctx, Collector<DwdBaseStationDataBO> out) throws Exception {
                String warehouse_type = dwdBaseStationDataBO.getWAREHOUSE_TYPE();
                String traffic_typeOfSPTB02 = dwdBaseStationDataBO.getTRAFFIC_TYPEOfSPTB02();
                String operate_type = dwdBaseStationDataBO.getOPERATE_TYPE();
                if (StringUtils.equals(warehouse_type,"T2") && StringUtils.equals(traffic_typeOfSPTB02,"G") && StringUtils.equals(operate_type, "InStock")) {
                        out.collect(dwdBaseStationDataBO);
                }
            }
        }).uid("OneOrderToEndDwmAppFilterWarehouseTypeAndTrafficTypeFields").name("OneOrderToEndDwmAppFilterWarehouseTypeAndTrafficTypeFields");

        // 6.2 执行插入/更新 IN_DISTRIBUTE_TIME(末端配送入库时间)
        // 注 :此处的IN_DISTRIBUTE_TIME入库时间按理说应该不第一次就插入,但考虑到出厂日期数据的数据不全,大多数时候都是第一条为此时间,故默认插入.
        // 另附IN_DISTRIBUTE_TIME的更新条件 : 1.晚于出厂日期的第一条入库(此条件不确定)  2.且记录不是分拨库（基地库）(已在6.1处满足)
        mapBsdFilterWarehouseTypeAndTrafficType.addSink(JdbcSink.sink(
                "INSERT INTO dwm_vlms_one_order_to_end (VIN, IN_DISTRIBUTE_TIME, WAREHOUSE_UPDATETIME) " +
                        " VALUES\n" +
                        " (?, ?, ?) \n" +
                        " ON DUPLICATE KEY UPDATE \n" +
                        " IN_DISTRIBUTE_TIME = if(LEAVE_FACTORY_TIME < ? and IN_SITE_TIME < ?, VALUES(IN_DISTRIBUTE_TIME), IN_DISTRIBUTE_TIME) , WAREHOUSE_UPDATETIME = VALUES(WAREHOUSE_UPDATETIME) ",
                (ps, bsd) -> {
                    Long nowTime = System.currentTimeMillis();
                    ps.setString(1, bsd.getVIN());
                    ps.setLong  (2, bsd.getSAMPLE_U_T_C());
                    ps.setLong  (3, nowTime);
                    ps.setLong  (4, bsd.getSAMPLE_U_T_C());
                    ps.setLong  (5, bsd.getSAMPLE_U_T_C());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withBatchIntervalMs(2000L)
                        .withMaxRetries(5)
                        .build(),
                PropertiesUtil.getMysqlJDBCConnection())).uid("OneOrderToEndMysqlInsertInDistribute").name("OneOrderToEndMysqlInsertInDistribute");

        // 3.更新 dwdBds->dwmOOTD 一单到底表
        //mapBsdFilterTime.addSink(new SimpleBsdSinkOOTD<DwdBaseStationDataBO>()).uid("OneOrderToEndDwmAppBSDBsdSinkOOTD").name("OneOrderToEndDwmAppBSDBsdSinkOOTD");
        //==============================================dwd_base_station_data处理 END==========================================================================//
        env.execute("dwdBsd更新一单到底表");
        log.info("base_station_data job任务开始执行");

    }
}
