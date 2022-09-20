package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02No8TimeFields;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消费mysql的 dwd_vlms_sptb02的数据，异步查询维表，拓宽新的字段成 dwm_vlms_sptb02表
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmAppSptb02Simple {
    //2022-01-01 00:00:00
    private static final long START = 1640966400000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;
    public static void main(String[] args) throws Exception {
        // Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        log.info("初始化流处理环境完成");
        //====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");

        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("waybill_dwm_sptb02_simple"));
        // 设置savepoint点二级目录位置
        // env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("waybill_dwm_sptb02_simple"));
        log.info("checkpoint设置完成");

        // mysql消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        // kafka消费源相关参数配置
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_SPTB02)
                .setGroupId(KafkaTopicConst.DWD_VLMS_SPTB02_GROUP)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "WaybillDwmAppSptb02SimpleMysqlSource").uid("WaybillDwmAppSptb02SimpleMysqlSourceStream").name("WaybillDwmAppSptb02SimpleMysqlSourceStream");

        SingleOutputStreamOperator<DwmSptb02No8TimeFields> dwmSptb02Process = mysqlSource.process(new ProcessFunction<String, DwmSptb02No8TimeFields>() {
            @Override
            public void processElement(String value, Context ctx, Collector<DwmSptb02No8TimeFields> out) throws Exception {
                //获取真实数据
                DwmSptb02No8TimeFields dwmSptb02 = JSON.parseObject(value, DwmSptb02No8TimeFields.class);
                String cjsdbhSource = dwmSptb02.getCJSDBH();
                Long ddjrq = dwmSptb02.getDDJRQ();
                if (ddjrq >= START && ddjrq <= END) {
                //------------------------------------增加排除vvin码的选择---------------------------------------------//
                // 按照sptb02的cjsdbh去sptb02d1查vin码
                if (StringUtils.isNotBlank(cjsdbhSource) ) {
                    String sptb02d1Sql = "select VVIN, CCPDM from " + KafkaTopicConst.ODS_VLMS_SPTB02D1 + " where CJSDBH = '" + cjsdbhSource + "' limit 1 ";
                    JSONObject sptb02d1 = MysqlUtil.queryNoRedis(sptb02d1Sql);
                    if (sptb02d1 != null) {
                        String vvin = sptb02d1.getString("VVIN");
                        //-----------------------------------只让vvin与sptb02表能匹配上的的进数-------------------------------------------//
                        if (StringUtils.isNotBlank(vvin)) {
                            String vfczt = dwmSptb02.getVFCZT();
                            String vsczt = dwmSptb02.getVSCZT();
                            dwmSptb02.setSTART_PHYSICAL_CODE(vfczt);
                            dwmSptb02.setEND_PHYSICAL_CODE(vsczt);
                            // 车型代码
                            String vehicle_code = sptb02d1.getString("CCPDM");
                            // 车架号 vin码
                            dwmSptb02.setVVIN(vvin);
                            dwmSptb02.setVEHICLE_CODE(vehicle_code);
                            if (StringUtils.isNotBlank(vehicle_code)) {
                                /**
                                 * 1.按照车型代码获取车型名称
                                 * 按照车型代码去关联mdac12 获得 CCXDL
                                 *  SELECT CCXDL FROM ods_vlms_mdac12 WHERE CCPDM = '4F80NL 4Z4ZMC 1909';
                                 */
                                String mdac12Sql = "select VCPMC ,CCXDL  from " + KafkaTopicConst.ODS_VLMS_MDAC12 + " where CCPDM = '" + vehicle_code + "' limit 1 ";
                                JSONObject mdac12 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC12, mdac12Sql, vehicle_code);
                                if (mdac12 != null) {
                                    dwmSptb02.setVEHICLE_NAME(mdac12.getString("VCPMC"));
                                    dwmSptb02.setCCXDL(mdac12.getString("CCXDL"));
                                }
                                /**
                                 * select b.vvin,d.vppsm
                                 * from sptb02 a
                                 * inner join sptb02d1 b on a.cjsdbh=b.cjsdbh
                                 * inner join mdac12 v on b.ccpdm = v.ccpdm
                                 * inner  join mdac10 d on v.cpp = d.cpp
                                 * where b.vvin = 'LFVVB9G35N5130282';
                                 */
                                String mdac1210Sql = "SELECT VPPSM FROM "+ KafkaTopicConst.DIM_VLMS_MDAC1210 +" dim_vlms_mdac1210 WHERE CCPDM='" + vehicle_code +"' limit 1";
                                JSONObject mdac1210 = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_MDAC1210, mdac1210Sql, vehicle_code);
                                if (mdac1210 != null){
                                    String vppsm = mdac1210.getString("VPPSM");
                                    if (StringUtils.isNotBlank(vppsm)){
                                        dwmSptb02.setBRAND_NAME(vppsm);
                                    }
                                }
                            }

                            /**
                             //理论起运时间 和 理论出库时间
                             //关联ods_vlms_lc_spec_config 获取 STANDARD_HOURS 标准时长
                             // 获取车架号 VVIN 从mysql中获取
                             * 查询 ods_vlms_lc_spec_config
                             * 过滤条件：
                             * 主机公司代码 CZJGSDM
                             *
                             * BASE_CODE(转换保存代码)  ->   CQWH 区位号(基地代码)
                             *
                             * TRANS_MODE_CODE       -> 运输方式
                             */
                            String hostComCode = dwmSptb02.getHOST_COM_CODE();
                            String baseCode = dwmSptb02.getBASE_CODE();
                            String transModeCode = dwmSptb02.getTRANS_MODE_CODE();
                            log.info("theoryShipmentTimeDS阶段获取到的查询条件值:{}, {}, {}", hostComCode, baseCode, transModeCode);
                            if (StringUtils.isNotBlank(hostComCode) && StringUtils.isNotBlank(baseCode) && StringUtils.isNotBlank(transModeCode)) {
                                //SPEC_CODE 指标代码 0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
                                String shipmentSpecConfigSql = "select STANDARD_HOURS, START_CAL_NODE_CODE from " + KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG + " where " +
                                        "HOST_COM_CODE = '" + hostComCode + "' and BASE_CODE = '" + baseCode + "' and TRANS_MODE_CODE = '" + transModeCode + "' AND STATUS = '1' AND SPEC_CODE = '4' limit 1 ";
                                //2022-09-19 新增--理论出库时间
                                String outSpecConfigSql = "select STANDARD_HOURS, START_CAL_NODE_CODE from " + KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG + " where " +
                                        "HOST_COM_CODE = '" + hostComCode + "' and BASE_CODE = '" + baseCode + "' and TRANS_MODE_CODE = '" + transModeCode + "' AND STATUS = '1' AND SPEC_CODE = '2' limit 1 ";

                                JSONObject specConfig = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG, shipmentSpecConfigSql, hostComCode, baseCode, transModeCode);
                                JSONObject outSpecConfig = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG + "-out", outSpecConfigSql, hostComCode, baseCode, transModeCode);
                                //=========================理论起运时间==================================//
                                if (specConfig != null) {
                                    // 定义要增加的时间戳
                                    Long outSiteTime = null;
                                    // 获取增加的时间步长
                                    String hours = specConfig.getString("STANDARD_HOURS");
                                    //获取前置节点代码
                                    String nodeCode = specConfig.getString("START_CAL_NODE_CODE").trim();
                                    /**
                                     * DZJDJRQ 主机厂计划下达时间       公路
                                     * DCKRQ    出库时间              铁路
                                     * SYRKSJ   溯源系统入港扫描时间    水运
                                     */
                                    if ("DZJDJRQ".equals(nodeCode)) {
                                        outSiteTime = dwmSptb02.getDPZRQ();
                                    }
                                    if ("DCKRQ".equals(nodeCode) || "SYRKSJ".equals(nodeCode)) {
                                        outSiteTime = dwmSptb02.getDCKRQ();
                                    }
                                    // 水运 是否是根据SPTB02_SEA_RK.DRKRQ  溯源系统入港扫描时间
                                    if (outSiteTime != null) {
                                        Date outSiteDate = new Date(outSiteTime);
                                        // 格式化时间
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String formatDate = formatter.format(outSiteDate);
                                        DateTime parse = DateUtil.parse(formatDate);
                                        DateTime newStepTime = DateUtil.offsetHour(parse, Integer.parseInt(hours));
                                        dwmSptb02.setTHEORY_SHIPMENT_TIME(newStepTime.getTime());
                                    }
                                }
                                //==================理论出库时间===========================//
                                if (outSpecConfig != null) {
                                    // 定义要增加的时间戳
                                    Long outTime = null;
                                    // 获取增加的时间步长
                                    String hours = outSpecConfig.getString("STANDARD_HOURS");
                                    // 获取前置节点代码
                                    String nodeCode = outSpecConfig.getString("START_CAL_NODE_CODE").trim();
                                    /**
                                     * DZJDJRQ     主机厂计划下达时间       一汽大众
                                     * DYSSZPSJ    运输商指派时间       其他主机厂
                                     */
                                    if ("DZJDJRQ".equals(nodeCode)) {
                                        outTime = dwmSptb02.getDPZRQ();
                                    }
                                    if ("DYSSZPSJ".equals(nodeCode)) {
                                        outTime = dwmSptb02.getDYSSZPSJ();
                                    }

                                    if (outTime != null) {
                                        Date outDate = new Date(outTime);
                                        // 格式化时间
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String formatDate = formatter.format(outDate);
                                        DateTime parse = DateUtil.parse(formatDate);
                                        DateTime newStepTime = DateUtil.offsetHour(parse, Integer.parseInt(hours));
                                        dwmSptb02.setTHEORY_OUT_TIME(newStepTime.getTime());
                                    }
                                }
                            }

                            /**
                             * 处理主机公司名称
                             * 关联sptc61 c1 on a.CZJGSDM = c1.cid，取c1.cjc
                             */
                            String czjgsdm = dwmSptb02.getCZJGSDM();
                            log.info("sptc61DS阶段获取到的查询条件值:{}", czjgsdm);
                            if (StringUtils.isNotBlank(czjgsdm)) {
                                String sptc61Sql = "select CJC from " + KafkaTopicConst.ODS_VLMS_SPTC61 + " where CID = '" + czjgsdm + "' limit 1 ";
                                JSONObject sptc61 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC61, sptc61Sql, czjgsdm);
                                if (sptc61 != null) {
                                    dwmSptb02.setCUSTOMER_NAME(sptc61.getString("CJC"));
                                }
                            }

                            /**
                             * 处理发车基地名称
                             * 关联sptc62 c2 on a.cqwh = c2.cid，取c2.cname
                             */
                            String cqwh = dwmSptb02.getCQWH();
                            log.info("sptc61DS阶段获取到的查询条件值:{}", cqwh);
                            if (StringUtils.isNotBlank(cqwh)) {
                                String sptc62Sql = "select CNAME from " + KafkaTopicConst.ODS_VLMS_SPTC62 + " where CID = '" + cqwh + "' limit 1 ";
                                JSONObject sptc62 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC62, sptc62Sql, cqwh);
                                if (sptc62 != null) {
                                    dwmSptb02.setBASE_NAME(sptc62.getString("CNAME"));
                                }
                            }


                            /**
                             * 处理发运仓库名称
                             * 关联sptc34 b on a.vwlckdm = b.vwlckdm， 取b.vwlckmc
                             */
                            String vwlckdm = dwmSptb02.getVWLCKDM();
                            log.info("sptc34DS阶段获取到的查询条件值:{}", vwlckdm);
                            if (StringUtils.isNotBlank(vwlckdm)) {
                                String sptc34Sql = "select VWLCKMC from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vwlckdm + "' limit 1 ";
                                JSONObject sptc34 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, sptc34Sql, vwlckdm);
                                if (sptc34 != null) {
                                    dwmSptb02.setSHIPMENT_WAREHOUSE_NAME(sptc34.getString("VWLCKMC"));
                                }
                            }

                            /**
                             * 处理 运输商名称
                             * nvl(y.vcydjc,y.vcydmc) 运输商,
                             * inner join mdac52 y on a.cyssdm = y.ccyddm
                             */
                            String cyssdm = dwmSptb02.getCYSSDM();
                            log.info("mdac52DS阶段获取到的查询条件值:{}", cyssdm);
                            if (StringUtils.isNotBlank(cyssdm)) {
                                String mdac52Sql = "select VCYDJC, VCYDMC from " + KafkaTopicConst.ODS_VLMS_MDAC52 + " where CCYDDM = '" + cyssdm + "' limit 1 ";
                                JSONObject mdac52 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC52, mdac52Sql, cyssdm);
                                if (mdac52 != null) {
                                    if (StringUtils.isNotBlank(mdac52.getString("VCYDJC"))) {
                                        dwmSptb02.setTRANSPORT_NAME(mdac52.getString("VCYDJC"));
                                    } else {
                                        dwmSptb02.setTRANSPORT_NAME(mdac52.getString("VCYDMC"));
                                    }
                                }
                            }

                            /**
                             * 处理经销商名称
                             * nvl(j.vjxsjc,j.vjxsmc) vscdwmc
                             * left join mdac22 j on a.vdwdm = j.cjxsdm
                             */
                            String vdwdm = dwmSptb02.getVDWDM();
                            if (StringUtils.isNotBlank(vdwdm)) {
                                String mdac22Sql = "select VJXSJC, VJXSMC from " + KafkaTopicConst.ODS_VLMS_MDAC22 + " where CJXSDM = '" + vdwdm + "' limit 1 ";
                                JSONObject mdac22 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC22, mdac22Sql, vdwdm);
                                if (mdac22 != null) {
                                    if (StringUtils.isNotBlank(mdac22.getString("VJXSJC"))) {
                                        dwmSptb02.setDEALER_NAME(mdac22.getString("VJXSJC"));
                                    } else {
                                        dwmSptb02.setDEALER_NAME(mdac22.getString("VJXSMC"));
                                    }
                                }
                            }

                            /**
                             * 处理 起货地 物理仓库代码  省区 县区名称
                             * 关联合并后的维表 dim_vlms_provinces
                             *   inner join sptc34 b on a.vwlckdm = b.vwlckdm
                             *   inner join mdac32 e on a.cdhddm = e.cdhddm
                             *   inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
                             *   inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
                             */
                            String startProvinceCode = dwmSptb02.getSTART_PROVINCE_CODE();
                            String startCityCode = dwmSptb02.getSTART_CITY_CODE();
                            log.info("provincesSptc34DS阶段异步查询获取的查询省编码值:{}, 市县编码值:{}", startProvinceCode, startCityCode);
                            if (StringUtils.isNotBlank(startProvinceCode) && StringUtils.isNotBlank(startCityCode)) {
                                String provinceSql = "select vsqmc, vsxmc from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + startProvinceCode + "' and csxdm = '" + startCityCode + "' limit 1 ";
                                JSONObject province = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, startProvinceCode, startCityCode);
                                if (province != null) {
                                    // 省区名称：山东省
                                    dwmSptb02.setSTART_PROVINCE_NAME(province.getString("vsqmc"));
                                    // 市县名称: 齐河
                                    dwmSptb02.setSTART_CITY_NAME(province.getString("vsxmc"));
                                }
                            }

                            /** 收车站台的相关字段赋值
                             *  处理 vfczt的 VFCZT_PROVINCE_CODE  VFCZT_CITY_CODE 给这俩字段赋值
                             *  inner join sptc34 b on a.vwlckdm = b.VFCZT
                             */
                            if (StringUtils.isNotBlank(vfczt)) {
                                String sptc34VFCZTSql = "select VSQDM, VSXDM from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vfczt + "' limit 1 ";
                                JSONObject odsVlmsSptc34VFCZT = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, sptc34VFCZTSql, vfczt);
                                if (odsVlmsSptc34VFCZT != null) {
                                    // 发车站台的省区代码
                                    String VFCZT_PROVINCE_CODE_34 = odsVlmsSptc34VFCZT.getString("VSQDM");
                                    // 发车站台的市县代码
                                    String VFCZT_CITY_CODE_34 = odsVlmsSptc34VFCZT.getString("VSXDM");
                                    dwmSptb02.setVFCZT_PROVINCE_CODE(VFCZT_PROVINCE_CODE_34);
                                    dwmSptb02.setVFCZT_CITY_CODE(VFCZT_CITY_CODE_34);
                                }
                            }

                            /** 发车站台的相关字段赋值
                             *  处理 vsczt的 VSCZT_PROVINCE_CODE  VSCZT_CITY_CODE 给这俩字段赋值
                             *  inner join sptc34 b on a.vwlckdm = b.VSCZT
                             */
                            if (StringUtils.isNotBlank(vfczt)) {
                                String sptc34VSCZTSql = "select VSQDM, VSXDM from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vsczt + "' limit 1 ";
                                JSONObject odsVlmsSptc34VSCZT = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, sptc34VSCZTSql, vsczt);
                                if (odsVlmsSptc34VSCZT != null) {
                                    // 收车站台的省区代码
                                    String VSCZT_PROVINCE_CODE_34 = odsVlmsSptc34VSCZT.getString("VSQDM");
                                    // 收车站台的市县代码
                                    String VSCZT_CITY_CODE_34 = odsVlmsSptc34VSCZT.getString("VSXDM");
                                    dwmSptb02.setVSCZT_PROVINCE_CODE(VSCZT_PROVINCE_CODE_34);
                                    dwmSptb02.setVSCZT_CITY_CODE(VSCZT_CITY_CODE_34);
                                }
                            }
                            /**
                             * 处理 到货地  省区 县区名称
                             * 关联合并后的维表 dim_vlms_provinces
                             *   inner join sptc34 b on a.vwlckdm = b.vwlckdm
                             *   inner join mdac32 e on a.cdhddm = e.cdhddm
                             *   inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
                             *   inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
                             */
                            String endProvinceCode = dwmSptb02.getEND_PROVINCE_CODE();
                            String endCityCode = dwmSptb02.getEND_CITY_CODE();
                            log.info("provincesMdac32DS阶段异步查询获取的查询省编码值:{}, 市县编码值:{}", endProvinceCode, endCityCode);
                            if (StringUtils.isNotBlank(endProvinceCode) && StringUtils.isNotBlank(endCityCode)) {
                                String provinceSql = "select vsqmc, vsxmc from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + endProvinceCode + "' and csxdm = '" + endCityCode + "' limit 1 ";
                                JSONObject province = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, endProvinceCode, endCityCode);
                                if (province != null) {
                                    // 例如 省区名称：山东省
                                    dwmSptb02.setEND_PROVINCE_NAME(province.getString("vsqmc"));
                                    // 例如 市县名称: 齐河
                                    dwmSptb02.setEND_CITY_NAME(province.getString("vsxmc"));
                                }

                                /**
                                 * 铁水的 cdhddm组成的 END_PROVINCE_CODE + END_CITY_CODE 与 其他 I.公路(vwlckdm) II.铁水(vsczt)作比较
                                 * 若相等 则为同城 1 否则为异地 2 默认为 0
                                 */
                                //  获取运单类型
                                String traffic_type = dwmSptb02.getTRAFFIC_TYPE();
                                //  获取到货地的省区市县所在地代码 用作和其他公铁水比较
                                String provincesEnd = endProvinceCode + endCityCode;
                                //------------------开始比较: 公 START_PROVINCE_CODE + START_CITY_CODE && traffic_type = 'G' ---------------------------------------------//
                                if (StringUtils.isNotBlank(startProvinceCode) && StringUtils.isNotBlank(startCityCode) && StringUtils.equals("G", traffic_type)) {
                                    // 公路的省市县代码
                                    String provincesG = startProvinceCode + startCityCode;
                                    if (StringUtils.equals(provincesEnd, provincesG)) {
                                        dwmSptb02.setTYPE_TC(1);
                                    } else {
                                        dwmSptb02.setTYPE_TC(2);
                                    }
                                }
                                //-----------------开始比较: 铁 VSCZT_PROVINCE_CODE + VSCZT_CITY_CODE && traffic_type = 'T' ----------------------------------------------------------//
                                String vsczt_province_code = dwmSptb02.getVSCZT_PROVINCE_CODE();
                                String vsczt_city_code = dwmSptb02.getVSCZT_CITY_CODE();
                                if (StringUtils.isNotBlank(vsczt_province_code) && StringUtils.isNotBlank(vsczt_city_code) && StringUtils.equals("T", traffic_type)) {
                                    String provincesT = vsczt_province_code + vsczt_city_code;
                                    if (StringUtils.equals(provincesEnd, provincesT)) {
                                        dwmSptb02.setTYPE_TC(1);
                                    } else {
                                        dwmSptb02.setTYPE_TC(2);
                                    }
                                }
                                //-----------------开始比较: 水 VSCZT_PROVINCE_CODE + VSCZT_CITY_CODE && traffic_type = 'S' ----------------------------------------------------------//
                                if (StringUtils.isNotBlank(vsczt_province_code) && StringUtils.isNotBlank(vsczt_city_code) && StringUtils.equals("S", traffic_type)) {
                                    String provincesS = vsczt_province_code + vsczt_city_code;
                                    if (StringUtils.equals(provincesEnd, provincesS)) {
                                        dwmSptb02.setTYPE_TC(1);
                                    } else {
                                        dwmSptb02.setTYPE_TC(2);
                                    }
                                }
                            }


                            // ======================= 理论到货时间 ==================================//
                            // 一般取  sptb02表的DBZDHSJ_DZ
                            // 佛山水运系统理论到货时间按照以下逻辑单独处理
                            // update LC_VW_DH_RECORD a set a.dlldhsj_xt = a.dzjdjrq + 2.2 + (select b.nbzwlsj_dz FROM sptb02 b where b.cjsdbh = a.cjsdbh)
                            // where a.vysfs = 'S' and a.vlj = '佛山基地';
                            //同城或者公路计划系统理论到货时间按照以下逻辑单独处理
                            //   update LC_VW_DH_RECORD
                            //    set  DLLDHSJ_XT = trunc(DLLDHSJ_XT,'dd')+1-1/(24*60*60)
                            //    WHERE
                            //    istc = 'YES' OR VYSFS = 'G';
                            if (Objects.nonNull(dwmSptb02.getDBZDHSJ_DZ()) && !dwmSptb02.getDBZDHSJ_DZ().equals(0L)){
                                Long theorySiteTime = dwmSptb02.getDBZDHSJ_DZ();
                                //处理佛山水运
                                // CQWH : 长春0431  成都028  佛山0757  青岛0532  天津022
                                if ("S".equals(dwmSptb02.getTRAFFIC_TYPE()) && "0757".equals(dwmSptb02.getCQWH())){
                                    String ddjrqSql = "select DDJRQ from " + KafkaTopicConst.ODS_VLMS_SPTB01C +  " where CPCDBH = '" + dwmSptb02.getCPCDBH() + "' limit 1 ";
                                    JSONObject ddjrqConfig = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB01C + "-ddjrq", ddjrqSql, dwmSptb02.getCPCDBH());
                                    if (Objects.nonNull(ddjrqConfig)){
                                        //2.2天 = 190080000 ms
                                        theorySiteTime = Long.parseLong(ddjrqConfig.getString("DDJRQ")) + theorySiteTime + 190080000L;
                                    }
                                }
                                //处理同城或者公路计划
                                if ("G".equals(dwmSptb02.getTRAFFIC_TYPE()) || Integer.valueOf(1).equals(dwmSptb02.getTYPE_TC())){
                                    //将日期转为今天的 23:59:59
                                    Date outDate = new Date(dwmSptb02.getDBZDHSJ_DZ());
                                    // 格式化时间
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    String formatDate = formatter.format(outDate) + " 23:59:59";
                                    DateTime parse = DateUtil.parse(formatDate);
                                    theorySiteTime = parse.getTime();
                                }
                                dwmSptb02.setTHEORY_SITE_TIME(theorySiteTime);
                            }

//                            // 分配及时率  出库及时率  起运及时率 到货及时率
//                            /**
//                             * 处理理论出库时间 THEORY_OUT_TIME
//                             * 关联LC_SPEC_CONFIG 获取前置节点, 标准时长,指标代码,标准时长,状态
//                             * 前置节点代码  START_CAL_NODE_CODE
//                             * 标准时长(小时,需要转成秒级别)  STANDARD_HOURS
//                             * 指标代码(取2): SPEC_CODE  0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
//                             * 使用到dwdSptb02 的czjgsdm  cqwh vysfs
//                             * vysfs 已经处理成 TRAFFIC_TYPE    dwdsptb02
//                             *                 TRANS_MODE_CODE   lc_spec_config
//                             * czjgsdm  已经处理成sptb02.HOST_COM_CODE 1:大众  2:红旗 3:马自达
//                             * cqwh 已经处理成sptb02.BASE_CODE  1:长春  2:成都  3.佛山 5:天津
//                             * 只处理大众理论出库时间  不区分公铁水和基地  取大众计划下达时间 DZJDJRQ+24小时(86400L)  主机公司代码是1 (大众)
//                             * 红旗理论出库时间  基地只有长春基地,  取运输商指派时间 sptb02.DYSSZPSJ  公路:+24小时(86400L)   铁路:+72小时(259200L)  水路:+36小时(172800L)  红旗  主机公司代码是2
//                             * 马自达理论出库时间  基地只有长春  取运输商指派时间 sptb02.DYSSZPSJ 公路:+24小时(86400L)  铁路和水路:+36小时(172800L)  马自达  主机公司代码是 3
//                             */
//                            log.info("获取查询需要的主机公司代码,基地代码,运输方式代码" , hostComCode , baseCode ,transModeCode);
//                            if (StringUtils.isNotBlank(hostComCode) && StringUtils.isNotBlank(baseCode) && StringUtils.isNotBlank(transModeCode)) {
//                                String configSql = " select * from " + KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG + " where HOST_COM_CODE = '" + hostComCode + "' and BASE_CODE = '" + baseCode + "' and TRANS_MODE_CODE = '" + transModeCode + "' AND STATUS = '1' AND SPEC_CODE = '2' limit 1 ";
//                                JSONObject config = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG,configSql, hostComCode , baseCode ,transModeCode,"1","2");
//                                if ( config != null ) {
//                                    // 获取前置节点代码  START_CAL_NODE_CODE
//                                    String nodeCodeStr = config.getString("START_CAL_NODE_CODE").trim();
//                                    // 将标准时长并将小时转换成毫秒级别  STANDARD_HOURS
//                                    Long second = Long.parseLong(config.getString("STANDARD_HOURS")) * 60 * 60 * 1000L;
//                                    // 获取主机公司代码 HOST_COM_CODE
//                                    String hostCode = config.getString("HOST_COM_CODE").trim();
//                                    // 获取运输方式名称  TRANS_MODE_NAME
//                                    String transName = config.getString("TRANS_MODE_NAME").trim();
//                                    // 获取基地名称 BASE_NAME
//                                    String baseName = config.getString("BASE_NAME").trim();
//                                    // 只处理大众理论出库时间  不区分公铁水和基地  取大众计划下达时间 DZJDJRQ +24小时(86400L)  主机公司代码是1 (大众)  主机厂下达计划时间  取sptb02.dpzrq
//                                    if ("1".equals(hostCode) && "DZJDJRQ".equals(nodeCodeStr)) {
//                                        if (dwmSptb02.getDPZRQ() != 0) {
//                                            dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDPZRQ() + second);
//                                        }
//                                    }
//                                    // 红旗理论出库时间  基地只有长春基地,  取运输商指派时间 sptb02.DYSSZPSJ  公路:+24小时(86400L)   铁路:+72小时(259200L)  水路:+36小时(172800L)  红旗  主机公司代码是2
//                                    if ("2".equals(hostCode) && "长春基地".equals(baseName) && "DYSSZPSJ".equals(nodeCodeStr)) {
//                                        if (dwmSptb02.getDYSSZPSJ() != 0) {
//                                            switch (transName) {
//                                                case "公路":
//                                                    dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
//                                                    break;
//                                                case "铁路":
//                                                    dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
//                                                    break;
//                                                case "水路":
//                                                    dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
//                                                    break;
//                                            }
//                                        }
//                                    }
//                                    // 马自达理论出库时间  基地只有长春  取运输商指派时间 sptb02.DYSSZPSJ 公路:+24小时(86400L)  铁路和水路:+36小时(172800L)  马自达  主机公司代码是 3
//                                    if ("3".equals(hostCode) && "长春基地".equals(baseName) && "DYSSZPSJ".equals(nodeCodeStr)) {
//                                        if (dwmSptb02.getDYSSZPSJ() != 0) {
//                                            if ("公路".equals(transName)) {
//                                                dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
//                                            } else if ("铁路".equals(transName) || "水路".equals(transName)) {
//                                                dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            /**
//                             * 处理理论到货时间  THEORY_SITE_TIME (铁路和水路数据)
//                             * 先处理dwmsptb02表与sptb013表的字段
//                             * SPTB013 需要的字段 NSJSL,VPH
//                             * 1 (b3.nsjsl /10 >= 26) 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26) 3 b3.nsjsl /10 <15
//                             * 关联条件  left  join sptb013    b3 on a.vph = b3.vph
//                             * 字段  RAILWAY_TRAIN_TYPE  铁路种类
//                             */
//                            // 获取vph
//                            String vph = dwmSptb02.getVPH();
//                            String trafficType = dwmSptb02.getTRAFFIC_TYPE();
//                            log.info("获取需要查询的vph {}",vph);
//                            if (StringUtils.isNotBlank(vph) && "T".equals(trafficType) || "S".equals(trafficType)) {
//                                String sptb013Sql = " select  * from " + KafkaTopicConst.ODS_VLMS_SPTB013 + " where vph = '" + vph +  "' limit 1 ";
//                                JSONObject sptb013 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB013,sptb013Sql,vph);
//                                if ( sptb013 != null ) {
//                                    // 获取实际数量
//                                    Integer nsjsl = sptb013.getIntValue("NSJSL");
//                                    // 1 (b3.nsjsl /10 >= 26)
//                                    // 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26)
//                                    // 3 b3.nsjsl /10 <15
//                                    if (nsjsl != 0) {
//                                        // 大于26  1
//                                        if (nsjsl / 10 >= 26) {
//                                            dwmSptb02.setRAILWAY_TRAIN_TYPE(1);
//                                            // 大于等于 15 小于26  2
//                                        } else if (nsjsl / 10 >= 15 && nsjsl / 10 < 26) {
//                                            dwmSptb02.setRAILWAY_TRAIN_TYPE(2);
//                                            // 小于15 3
//                                        } else if (nsjsl / 10 < 15) {
//                                            dwmSptb02.setRAILWAY_TRAIN_TYPE(3);
//                                        }
//                                    }
//                                }
//                            }
//                            /**
//                             * 处理理论到货时间
//                             * 与spti32表关联  取公路的理论到货时间
//                             *  ##### c1  sptc34  c2 mdac32  a sptb02  i spti32
//                             * left join spti32 i on c1.vsqdm = i.cqssqdm and c1.vsxdm = i.cqscsdm and c2.csqdm = i.cmbsqdm and c2.csxdm = i.cmbcsdm
//                             *        and a.czjgsdm = i.czjgs and a.traffic_type = i.vysfs
//                             * 关联条件  CQSSQDM 起始省区代码  CQSCSDM 起始市县代码  CMBSQDM 目标省区代码 CMBCSDM 目标市县代码  CZJGS 主机公司代码  VYSFS 运输方式
//                             * SPTB02
//                             *            END_PROVINCE_CODE 到货地省区代码     CMBSQDM
//                             *            END_CITY_CODE  到货地市县代码      CMBCSDM
//                             *            START_PROVINCE_CODE  起货地省区代码    CQSSQDM
//                             *            START_CITY_CODE 起货地市县代码     CQSCSDM
//                             *            TRAFFIC_TYPE 运输方式        VYSFS
//                             *            HOST_COM_CODE 主机公司代码     CZJGS
//                             */
//                            // 获取起始省区代码
//                            String startprovincecode = dwmSptb02.getSTART_PROVINCE_CODE();
//                            // 获取起始市县代码  START_CITY_CODE
//                            String startcitycode = dwmSptb02.getSTART_CITY_CODE();
//                            // 获取到货地省区代码  END_PROVINCE_CODE
//                            String endprovincecode = dwmSptb02.getEND_PROVINCE_CODE();
//                            // 获取到货地市县代码   END_CITY_CODE
//                            String endcitycode = dwmSptb02.getEND_CITY_CODE();
//                            // 获取主机公司名称  HOST_COM_CODE
//                            // 前面已经获取到数据  直接拿过来用即可
//                            // String czjgsdm = dwmSptb02.getCZJGSDM();
//                            // 获取运输方式  TRAFFIC_TYPE
//                            // String trafficType = dwmSptb02.getTRAFFIC_TYPE();
//                            if (StringUtils.isNotBlank(startprovincecode) && StringUtils.isNotBlank(startcitycode) && StringUtils.isNotBlank(endprovincecode) && StringUtils.isNotBlank(endcitycode) && StringUtils.isNotBlank(czjgsdm) && StringUtils.isNotBlank(trafficType)) {
//                                String spti32Sql = " select *  from " + KafkaTopicConst.ODS_VLMS_SPTI32 + " where CQSSQDM = '" + startprovincecode + "'and CQSCSDM = '" + startcitycode + "'and CMBSQDM = '" + endprovincecode + "' and  CMBCSDM = '" + endcitycode+ "'and CZJGS = '" + czjgsdm + "' and VYSFS = '" + trafficType + "' limit 1" ;
//                                JSONObject spti32 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTI32,spti32Sql,startprovincecode,startcitycode,endprovincecode,endcitycode,czjgsdm);
//                                if ( spti32 != null ) {
//                                    // VYSFS 运输方式
//                                    String vysfs = spti32.getString("VYSFS");
//                                    // sptb02.dckrq+spti32.ndhsj_xt
//                                    Long ndhsj_xt = spti32.getLong("NDHSJ_XT") * 60 * 60 * 1000L;
//                                    if (ndhsj_xt != 0 && "G".equals(vysfs)) {
//                                        dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDCKRQ() + ndhsj_xt);
//                                    }
//                                }
//                            }
//                            /**
//                             * 处理理论到货时间  THEORY_SITE_TIME
//                             * 与 spti32_rail_sea 关联
//                             * 关联字段  CZJGSDM  主机公司名称  vysfs S T
//                             *  left  join spti32_rail_sea i1 on a.CZJGSDM = i1.CZJGSDM and decode(a.VYSFS,''S'',''S'',''T'') = i1.VYSFS and   a.VFCZT = i1.CQSZTDM and a.VSCZT = i1.CMBZTDM
//                             *  RAILWAY_TRAIN_TYPE  1 (b3.nsjsl /10 >= 26) 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26) 3 b3.nsjsl /10 <15
//                             *  SPTB02
//                             *  CZJGSDM 主机公司代码
//                             *  TRAFFIC_TYPE 运输方式
//                             *  VFCZT  发车站台
//                             *
//                             *  VSCZT  收车站台
//                             *  铁水只有符合条件的才有理论到货时间
//                             */
//                            // 获取主机公司名称,运输方式  发车站台  收车站台等
//                            String czjgs = dwmSptb02.getCZJGSDM();
////                            String trafficType = dwmSptb02.getTRAFFIC_TYPE();
//                            // 上面已经添加次字段的取值  这边直接拿着用就可以了
////                            String vfczt = dwmSptb02.getVFCZT();
////                            String vsczt = dwmSptb02.getVSCZT();
//                            if (StringUtils.isNotBlank(czjgs) && StringUtils.isNotBlank(trafficType) && StringUtils.isNotBlank(vfczt) && StringUtils.isNotBlank(vsczt)) {
//                                String spti32RailSeaSql = "select * from " +  KafkaTopicConst.ODS_VLMS_SPTI32_RAIL_SEA + " where CZJGSDM = '" + czjgs + "' and VYSFS = '" + trafficType  + "' and CQSZTDM = '" + vfczt + "' and CMBZTDM = '" + vsczt + "' limit 1";
//                                JSONObject spti32RailSea = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTI32_RAIL_SEA,spti32RailSeaSql,czjgs,trafficType,vfczt,vsczt);
//                                if ( spti32RailSea != null ) {
//                                    // 获取NDHSJ_XTDH_ml(满列)  大于26  RAILWAY_TRAIN_TYPE 1 准换成毫秒级别数据
//                                    BigDecimal ml = spti32RailSea.getBigDecimal("NDHSJ_XTDH_ML");
//                                    long ndhsjxtdhml = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(ml)).setScale(0).longValue();
//                                    // 获取NDHSJ_XTDH_dz(大组)  15<= x < 26   RAILWAY_TRAIN_TYPE 2
//                                    BigDecimal dz = spti32RailSea.getBigDecimal("NDHSJ_XTDH_DZ");
//                                    long ndhsjxtdhdz = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(dz)).setScale(0).longValue();
//                                    // 获取NDHSJ_XTDH_sl(散列) 小于15  RAILWAY_TRAIN_TYPE 3
//                                    BigDecimal sl = spti32RailSea.getBigDecimal("NDHSJ_XTDH_SL");
//                                    long ndhsjxtdhsl = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(sl)).setScale(0).longValue();
//                                    // ndhsj_dz_ml  S
//                                    BigDecimal dzml = spti32RailSea.getBigDecimal("NDHSJ_DZ_ML");
//                                    long ndhsjdzml = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(dzml)).setScale(0).longValue();
//                                    // left  join spti32_rail_sea i1 on a.CZJGSDM = i1.CZJGSDM and decode(a.VYSFS,''S'',''S'',''T'') = i1.VYSFS and   a.VFCZT = i1.CQSZTDM and a.VSCZT = i1.CMBZTDM
//                                    String seavysfs = spti32RailSea.getString("VYSFS");
//                                    if (dwmSptb02.getDSJCFSJ() != 0) {
//                                        if ("S".equals(seavysfs)) {
//                                            dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjdzml);
//                                        } else if ("T".equals(seavysfs)) {
//                                            if (dwmSptb02.getRAILWAY_TRAIN_TYPE() == 1) {
//                                                dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjxtdhml);
//                                            } else if (dwmSptb02.getRAILWAY_TRAIN_TYPE() == 2) {
//                                                dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjxtdhdz);
//                                            } else if (dwmSptb02.getRAILWAY_TRAIN_TYPE() == 3) {
//                                                dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjxtdhsl);
//                                            }
//                                        }
//                                    }
//
//                                }
//                            }
                            //---------------新增R3的DDJRQ字段:DDJRQ_R3  此表是在sptb01c中取的,与sptb02以CPCDBH关联 _禅道891-------------------------------------------------------------------------------------------//
                            /** 配车单编号
                             *  String jhxdSql = "select count(*)\n" +
                             *                 "  from sptb01c a\n" +
                             *                 "  left join sptb02 b\n" +
                             *                 "    on a.cpcdbh = b.cpcdbh\n" +
                             */
                            String cpcdbh = dwmSptb02.getCPCDBH();
                            if (StringUtils.isNotBlank(cpcdbh)){
                                String sptb01cDDJRQSql = "select DDJRQ from " + KafkaTopicConst.ODS_VLMS_SPTB01C + " where CPCDBH = '" + cpcdbh + "' limit 1 ";
                                JSONObject odsVlmsSptb01cDDJRQ = MysqlUtil.queryNoRedis(sptb01cDDJRQSql);
                                if (odsVlmsSptb01cDDJRQ != null){
                                    Long ddjrqR3 = odsVlmsSptb01cDDJRQ.getLong("DDJRQ");
                                    dwmSptb02.setDDJRQ_R3(ddjrqR3);
                                }
                            }
                            dwmSptb02.setWAREHOUSE_UPDATETIME(System.currentTimeMillis());

                            //实体类中null值进行默认值赋值
                            DwmSptb02No8TimeFields bean = JsonPartUtil.getBean(dwmSptb02);
                            out.collect(bean);
                        }
                    }
                }
            }                                                                                                                 // 算子不合并
            }
        }).setParallelism(1).uid("WaybillDwmAppSptb02SimpleDwmSptb02Process").name("WaybillDwmAppSptb02SimpleDwmSptb02Process").disableChaining();

        //====================================sink mysql===============================================//
        String sql = MysqlUtil.getOnDuplicateKeySql(DwmSptb02No8TimeFields.class);
        dwmSptb02Process.addSink(JdbcSink.<DwmSptb02No8TimeFields>getSink(sql)).setParallelism(1).uid("WaybillDwmAppSptb02Simple_SinkMysql").name("WaybillDwmAppSptb02Simple_SinkMysql");

        log.info("将处理完的数据保存到clickhouse中");
        env.execute("Kafka:DwdSptb02->DwmSptb02(mysql & kafka)");
        log.info("sptb02dwd层job任务开始执行");
    }
}
