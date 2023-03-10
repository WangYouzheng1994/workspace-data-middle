package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02No8TimeFields;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.TimeConst;
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
import org.apache.kafka.common.TopicPartition;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消费mysql的 dwd_vlms_sptb02的数据，异步查询维表，拓宽新的字段成 dwm_vlms_sptb02表
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.1
 */
@Slf4j
public class WaybillDwmAppSptb02Simple {
    public static void main(String[] args) throws Exception {
        // 从偏移量表中读取指定的偏移量模式
        HashMap<TopicPartition, Long> offsetMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(KafkaTopicConst.DWD_VLMS_SPTB02, 0);
        offsetMap.put(topicPartition, 112904L);
        // Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        // 算子不合并
        env.disableOperatorChaining();
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
        // mysql 消费源相关参数配
        Properties properties = new Properties();
        // 遇到错误跳过
        properties.setProperty("debezium.inconsistent.schema.handing.mode","warn");
        properties.setProperty("debezium.event.deserialization.failure.handling.mode","warn");

        // mysql消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        // kafka消费源相关参数配置
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwd_vlms_sptb02")
                // .tableList("data_middle_flink.dwd_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .debeziumProperties(properties)
                .startupOptions(StartupOptions.latest())
                .distributionFactorUpper(10.0d)   //针对cdc的错误算法的更改
                .build();

        // 1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSourceStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "OneOrderToEndDwmAppSPTB02MysqlSource").uid("OneOrderToEndDwmAppSPTB02MysqlSourceStream").name("OneOrderToEndDwmAppSPTB02MysqlSourceStream");

        SingleOutputStreamOperator<DwmSptb02No8TimeFields> dwmSptb02Process = mysqlSourceStream.process(new ProcessFunction<String, DwmSptb02No8TimeFields>() {
            @Override
            public void processElement(String value, Context ctx, Collector<DwmSptb02No8TimeFields> out) throws Exception {
                // 获取真实数据
                DwmSptb02No8TimeFields dwmSptb02 = JsonPartUtil.getAfterObj(value, DwmSptb02No8TimeFields.class);
                // DwmSptb02No8TimeFields dwmSptb02 = JSON.parseObject(value, DwmSptb02No8TimeFields.class);
                String cjsdbhSource = dwmSptb02.getCJSDBH();
                Long ddjrq = dwmSptb02.getDDJRQ();
                if (ddjrq != null && ddjrq >= TimeConst.DATE_2020_12_01 && ddjrq <= TimeConst.DATE_2023_11_28) {
                //------------------------------------增加排除vvin码的选择---------------------------------------------//
                // 按照sptb02的cjsdbh去sptb02d1查vin码
                if (StringUtils.isNotBlank(cjsdbhSource) ) {
                    String sptb02d1Sql = "select CCPDM from " + KafkaTopicConst.ODS_VLMS_SPTB02D1 + " where CJSDBH = '" + cjsdbhSource + "' limit 1 ";
                    JSONObject sptb02d1 = MysqlUtil.queryNoRedis(sptb02d1Sql);
                    String vvin = dwmSptb02.getVVIN();
                    //-----------------------------------只让vvin与sptb02表能匹配上的的进数-------------------------------------------//
                    if (StringUtils.isNotBlank(vvin)) {
                        //当前时间
                        long currentTime = System.currentTimeMillis();
                        String vfczt = dwmSptb02.getVFCZT();
                        String vsczt = dwmSptb02.getVSCZT();
                        dwmSptb02.setSTART_PHYSICAL_CODE(vfczt);
                        dwmSptb02.setEND_PHYSICAL_CODE(vsczt);

                        if (sptb02d1 != null) {
                            // 车型代码
                            String vehicle_code = sptb02d1.getString("CCPDM");
                            dwmSptb02.setVEHICLE_CODE(vehicle_code);
                            if (StringUtils.isNotBlank(vehicle_code)) {
                                /*
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
                                /*
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
                        }



                        /*
                         * 理论起运时间 和 理论出库时间
                         * 关联ods_vlms_lc_spec_config 获取 STANDARD_HOURS 标准时长
                         * 获取车架号 VVIN 从mysql中获取
                         * 查询 ods_vlms_lc_spec_config
                         * 过滤条件：
                         * 主机公司代码 CZJGSDM
                         * BASE_CODE(转换保存代码)  ->   CQWH 区位号(基地代码)
                         * TRANS_MODE_CODE       -> 运输方式
                         */
                        String hostComCode = dwmSptb02.getHOST_COM_CODE();
                        String baseCode = dwmSptb02.getBASE_CODE();
                        String transModeCode = dwmSptb02.getTRANS_MODE_CODE();
                        log.info("theoryShipmentTimeDS阶段获取到的查询条件值:{}, {}, {}", hostComCode, baseCode, transModeCode);
                        if (StringUtils.isNotBlank(hostComCode) && StringUtils.isNotBlank(baseCode) && StringUtils.isNotBlank(transModeCode)) {
                            // SPEC_CODE 指标代码 0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
                            String shipmentSpecConfigSql = "select STANDARD_HOURS, START_CAL_NODE_CODE from " + KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG + " where " +
                                    "HOST_COM_CODE = '" + hostComCode + "' and BASE_CODE = '" + baseCode + "' and TRANS_MODE_CODE = '" + transModeCode + "' AND STATUS = '1' AND SPEC_CODE = '4' limit 1 ";
                            // 2022-09-19 新增--理论出库时间
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
                                /*
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
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    String formatDate = formatter.format(outSiteDate) + " 23:59:59";
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
                                /*
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

                        /*
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

                        /*
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


                        /*
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

                        /*
                         * 处理收发车站台
                         */
                        String sptb02VSCZT = dwmSptb02.getVSCZT();
                        if (StringUtils.isNotBlank(sptb02VSCZT)){
                            String vscztNameSql = "select VWLCKMC from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + sptb02VSCZT + "' limit 1 ";
                            JSONObject jsonVscztName = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, vscztNameSql, sptb02VSCZT + "(VSCZT)");
                            if (Objects.nonNull(jsonVscztName)){
                                dwmSptb02.setVSCZT_NAME(jsonVscztName.getString("VWLCKMC"));
                            }
                        }
                        String sptb02VFCZT = dwmSptb02.getVFCZT();
                        if (StringUtils.isNotBlank(sptb02VFCZT)){
                            String vfcztNameSql = "select VWLCKMC from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + sptb02VFCZT + "' limit 1 ";
                            JSONObject jsonVfcztName = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, vfcztNameSql, sptb02VFCZT + "(VFCZT)");
                            if (Objects.nonNull(jsonVfcztName)){
                                dwmSptb02.setVFCZT_NAME(jsonVfcztName.getString("VWLCKMC"));
                            }
                        }

                        /*
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

                        /*
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

                        /*
                         * 处理 起货地 物理仓库代码  省区 县区名称
                         * 关联合并后的维表 dim_vlms_provinces
                         *   inner join sptc34 b on a.vwlckdm = b.vwlckdm
                         *   inner join mdac32 e on a.cdhddm = e.cdhddm
                         *   inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
                         *   inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
                         */
                        String startProvinceCode = dwmSptb02.getSTART_PROVINCE_CODE();
                        String startCityCode = dwmSptb02.getSTART_CITY_CODE();
                        String vysfs = dwmSptb02.getVYSFS();
                        log.info("provincesSptc34DS阶段异步查询获取的查询省编码值:{}, 市县编码值:{}", startProvinceCode, startCityCode);
                        if (StringUtils.equalsAny(vysfs,"SD","TD") && StringUtils.isNotBlank(vsczt)){
                            /*
                             *  新增SD,TD线路的始发地城市 修改为按照 收车站台去取值 "也就是说 TD，SD运输方式的计划 得用 vsczt 去匹配始发城市" -白 10月10日 11:48
                             *  注:SD,TD无始发省区名称
                             */

                                String sptc34SqlOfSDTD = "select VWLCKMC from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vsczt + "' limit 1 ";
                                JSONObject odsVlmsSptc34OfSDTD = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34SDTD, sptc34SqlOfSDTD, vsczt);
                                if (odsVlmsSptc34OfSDTD != null) {
                                    dwmSptb02.setSTART_CITY_NAME(odsVlmsSptc34OfSDTD.getString("VWLCKMC"));
                                }
                        }else if (StringUtils.isNotBlank(startProvinceCode) && StringUtils.isNotBlank(startCityCode)) {
                            String provinceSql = "select vsqmc, vsxmc from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + startProvinceCode + "' and csxdm = '" + startCityCode + "' limit 1 ";
                            JSONObject province = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, startProvinceCode, startCityCode);
                            if (province != null) {
                                // 省区名称：山东省
                                dwmSptb02.setSTART_PROVINCE_NAME(province.getString("vsqmc"));
                                // 市县名称: 齐河
                                dwmSptb02.setSTART_CITY_NAME(province.getString("vsxmc"));
                            }
                        }

                        /*
                         *  发车站台的相关字段赋值
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

                        /*
                         *  收车站台的相关字段赋值
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
                        /*
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

                            /*
                             * 铁水的 cdhddm组成的 END_PROVINCE_CODE + END_CITY_CODE 与 其他 I.公路(vwlckdm) II.铁水(vsczt)作比较
                             * 若相等 则为同城 1 否则为异地 2 默认为 0
                             */
                            //  获取运单类型
                            String traffic_type = dwmSptb02.getTRAFFIC_TYPE();
                            //  获取到货地的省区市县所在地代码 用作和其他公铁水比较
                            String provincesEnd = endProvinceCode + endCityCode;

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
                        /*
                         *  一般取  sptb02表的DBZDHSJ_DZ
                         *  佛山水运系统理论到货时间按照以下逻辑单独处理
                         *  update LC_VW_DH_RECORD a set a.dlldhsj_xt = a.dzjdjrq + 2.2 + (select b.nbzwlsj_dz FROM sptb02 b where b.cjsdbh = a.cjsdbh)
                         *  where a.vysfs = 'S' and a.vlj = '佛山基地';
                         *  同城或者公路计划系统理论到货时间按照以下逻辑单独处理
                         *    update LC_VW_DH_RECORD
                         *     set  DLLDHSJ_XT = trunc(DLLDHSJ_XT,'dd')+1-1/(24*60*60)
                         *     WHERE
                         *     istc = 'YES' OR VYSFS = 'G';
                         */

                        if (Objects.nonNull(dwmSptb02.getDBZDHSJ_DZ()) && !dwmSptb02.getDBZDHSJ_DZ().equals(0L)){
                            Long theorySiteTime = dwmSptb02.getDBZDHSJ_DZ();
                            // 将日期转为今天的 23:59:59
                            // todo: 抽出方法类
                            Date outDate = new Date(theorySiteTime);
                            // 格式化时间
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String formatDate = formatter.format(outDate) + " 23:59:59";
                            DateTime parse = DateUtil.parse(formatDate);
                            theorySiteTime = parse.getTime();
                            //处理佛山水运 CQWH : 长春0431  成都028  佛山0757  青岛0532  天津022
                            if ("S".equals(dwmSptb02.getTRAFFIC_TYPE()) && "0757".equals(dwmSptb02.getCQWH())){
                                String ddjrqSql = "select DDJRQ from " + KafkaTopicConst.ODS_VLMS_SPTB01C +  " where CPCDBH = '" + dwmSptb02.getCPCDBH() + "' limit 1 ";
                                JSONObject ddjrqConfig = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB01C + "-ddjrq", ddjrqSql, dwmSptb02.getCPCDBH());
                                if (Objects.nonNull(ddjrqConfig)){
                                    //2.2天 = 190080000 ms
                                    theorySiteTime = Long.parseLong(ddjrqConfig.getString("DDJRQ")) + theorySiteTime + 190080000L;
                                }
                            }
                            dwmSptb02.setTHEORY_SITE_TIME(theorySiteTime);
                        }
                        //---------------新增R3的DDJRQ字段:DDJRQ_R3  此表是在sptb01c中取的,与sptb02以CPCDBH关联 _禅道891-------------------------------------------------------------------------------------------//
                        String cpcdbh = dwmSptb02.getCPCDBH();
                        if (StringUtils.isNotBlank(cpcdbh)){
                            String sptb01cDDJRQSql = "select DDJRQ from " + KafkaTopicConst.ODS_VLMS_SPTB01C + " where CPCDBH = '" + cpcdbh + "' limit 1 ";
                            JSONObject odsVlmsSptb01cDDJRQ = MysqlUtil.queryNoRedis(sptb01cDDJRQSql);
                            if (odsVlmsSptb01cDDJRQ != null){
                                Long ddjrqR3 = odsVlmsSptb01cDDJRQ.getLong("DDJRQ");
                                dwmSptb02.setDDJRQ_R3(ddjrqR3);
                            }
                        }

                        /*
                         * 逻辑处理核实到货时间
                         * 核实到货时间取值比较复杂，具体要参考PKC_FAWL_VW_DH存储过程54-63行的逻辑
                         *  case
                         * 1.when istc = 'NO' and VYSFS_SHOW = '铁路' and least(nvl(a1.dztxcsj+1,sysdate),nvl(k.drkrq,sysdate)) >= nvl(i.sd_date,sysdate) then least(SDSJ,i.sd_date)
                         * 2.when istc = 'NO' and VYSFS_SHOW = '铁路' and least(nvl(a1.dztxcsj+1,sysdate),nvl(k.drkrq,sysdate)) < nvl(i.sd_date,sysdate) then least(nvl(a1.dztxcsj+1,sysdate),nvl(k.drkrq,sysdate))
                         * 3.when ((istc = 'NO' and VYSFS_SHOW = '水运') or (a.vysfs in ('T','L1') and (a.cpzdbh like 'Y90%' or a.cpzdbh like 'Y00%'))) and least(nvl(a1.dgpsdhsj+1,sysdate),nvl(sd.drkrq,sysdate)) >= nvl(i.sd_date,sysdate) then least(nvl(SDSJ,sysdate),nvl(i.sd_date,sysdate))
                         * 4.when ((istc = 'NO' and VYSFS_SHOW = '水运') or (a.vysfs in ('T','L1') and (a.cpzdbh like 'Y90%' or a.cpzdbh like 'Y00%'))) and least(nvl(a1.dgpsdhsj+1,sysdate),nvl(sd.drkrq,sysdate)) < nvl(i.sd_date,sysdate) then least(nvl(a1.dgpsdhsj+1,sysdate),nvl(sd.drkrq,sysdate))--sd.drkrq
                         * 5.when istc = 'NO' and a.VYSFS = 'G' and nvl(DXXDHRQ,sysdate) >= nvl(i.sd_date,sysdate) then least(i.sd_date,a1.dtvsdhsj)
                         * 6.when istc = 'NO' and a.VYSFS = 'G' and nvl(DXXDHRQ,sysdate) < nvl(i.sd_date,sysdate) then least(DXXDHRQ,a1.dtvsdhsj)
                         * 7.when a.vysfs = 'J' then jsd.drkrq
                         * 8.when istc = 'YES' and nvl(DXXDHRQ,sysdate) >= nvl(i.sd_date,sysdate) then least(i.sd_date,a1.dtvsdhsj)
                         * 9.when istc = 'YES' and nvl(DXXDHRQ,sysdate) < nvl(i.sd_date,sysdate) then least(DXXDHRQ,a1.dtvsdhsj) end
                         *
                         * drkrq      ==> 入库时间         IN_SITE_TIME
                         * DXXDHRQ    ==>大众到货时间       DDHSJ
                         */
                        //是否同城 1同城 2异地 默认值为0
                        Integer typeTc = dwmSptb02.getTYPE_TC();
                        String vysfs1 = dwmSptb02.getVYSFS();
                        //运单运输类型
                        String trafficType = dwmSptb02.getTRAFFIC_TYPE();
                        //配载单编号
                        String cpzdbh = dwmSptb02.getCPZDBH();
                        Long dztxcsj = (Objects.nonNull(dwmSptb02.getDZTXCSJ()) && dwmSptb02.getDZTXCSJ() > 0) ? DateTimeUtil.getDateAddDays(dwmSptb02.getDZTXCSJ(), 1) : currentTime;
                        Long dgpsdhsj = (Objects.nonNull(dwmSptb02.getDGPSDHSJ()) && dwmSptb02.getDGPSDHSJ() > 0) ? DateTimeUtil.getDateAddDays(dwmSptb02.getDGPSDHSJ(), 1) : currentTime;
                        Long drkrq = (Objects.nonNull(dwmSptb02.getIN_SITE_TIME()) && dwmSptb02.getIN_SITE_TIME() > 0) ? dwmSptb02.getIN_SITE_TIME() : currentTime;
                        Long dxxdhrq = (Objects.nonNull(dwmSptb02.getDDHSJ()) && dwmSptb02.getDDHSJ() > 0) ? dwmSptb02.getDDHSJ() : currentTime;
                        //least(nvl(a1.dztxcsj+1,sysdate),nvl(k.drkrq,sysdate)) 的逻辑翻译
                        Long dztxcsjDrkrqDiffer = DateTimeUtil.getLeastDate(dztxcsj, drkrq);
                        //least(nvl(a1.dgpsdhsj+1,sysdate),nvl(sd.drkrq,sysdate)) 的逻辑翻译
                        Long dgpsdhsjDrkrqDiffer = DateTimeUtil.getLeastDate(dgpsdhsj, drkrq);
                        //获取SDSJ时间
                        String sptb02ImportSql = "select SD_DATE from " + KafkaTopicConst.ODS_VLMS_SPTB02_STD_IMPORT + " where CJSDBH = '" + cjsdbhSource + "' and TYPE = 'SD' limit 1 ";
                        JSONObject sptb02ImportObj  = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB02_STD_IMPORT, sptb02ImportSql, cjsdbhSource);
                        Long sdsj = (Objects.nonNull(sptb02ImportObj) && Objects.nonNull(sptb02ImportObj.getLong("SD_DATE"))) ? sptb02ImportObj.getLong("SD_DATE") : currentTime;
                        //1
                        if (Objects.nonNull(typeTc) && typeTc == 2 && "T".equals(trafficType) && (dztxcsjDrkrqDiffer - sdsj >= 0)) {
                            dwmSptb02.setACCOUNTING_TIME(sdsj);
                        }
                        //2
                        else if (Objects.nonNull(typeTc) && typeTc == 2 && "T".equals(trafficType) && (dztxcsjDrkrqDiffer - sdsj < 0)) {
                            dwmSptb02.setACCOUNTING_TIME(dztxcsjDrkrqDiffer);
                        }
                        //3
                        else if (((Objects.nonNull(typeTc) && typeTc == 2 && "S".equals(trafficType)) || ("T".equals(trafficType) && (cpzdbh.startsWith("Y90") || cpzdbh.startsWith("Y00")))) && (dgpsdhsjDrkrqDiffer - sdsj >= 0)) {
                            dwmSptb02.setACCOUNTING_TIME(sdsj);
                        }
                        //4
                        else if (((Objects.nonNull(typeTc) && typeTc == 2 && "S".equals(trafficType)) || ("T".equals(trafficType) && (cpzdbh.startsWith("Y90") || cpzdbh.startsWith("Y00")))) && (dgpsdhsjDrkrqDiffer - sdsj < 0)) {
                            dwmSptb02.setACCOUNTING_TIME(dgpsdhsjDrkrqDiffer);
                        }
                        //5
                        else if ((Objects.nonNull(typeTc) && typeTc == 2 && "G".equals(vysfs1)) && (dxxdhrq - sdsj >= 0)) {
                            dwmSptb02.setACCOUNTING_TIME(DateTimeUtil.getLeastDate(sdsj, dwmSptb02.getDTVSDHSJ()));
                        }
                        //6
                        else if ((Objects.nonNull(typeTc) && typeTc == 2 && "G".equals(vysfs1)) && (dxxdhrq - sdsj < 0)) {
                            dwmSptb02.setACCOUNTING_TIME(DateTimeUtil.getLeastDate(dxxdhrq, dwmSptb02.getDTVSDHSJ()));
                        }
                        //7
                        else if ("J".equals(vysfs1)) {
                            dwmSptb02.setACCOUNTING_TIME(drkrq);
                        }
                        //8
                        else if (Objects.nonNull(typeTc) && typeTc == 1 && (dxxdhrq - sdsj >= 0)) {
                            dwmSptb02.setACCOUNTING_TIME(DateTimeUtil.getLeastDate(sdsj, dwmSptb02.getDTVSDHSJ()));
                        }
                        //9
                        else if (Objects.nonNull(typeTc) && typeTc == 1 && (dxxdhrq - sdsj < 0)) {
                            dwmSptb02.setACCOUNTING_TIME(DateTimeUtil.getLeastDate(dxxdhrq, dwmSptb02.getDTVSDHSJ()));
                        }
                        //兜底  ACCOUNTING_TIME不设置为当前时间
                        // else {
                        //     dwmSptb02.setACCOUNTING_TIME(currentTime);
                        // }

                        dwmSptb02.setWAREHOUSE_UPDATETIME(currentTime);

                        // 实体类中null值进行默认值赋值
                        DwmSptb02No8TimeFields bean = JsonPartUtil.getBean(dwmSptb02);
                        out.collect(bean);
                    }
                }
            }
            }
        }).uid("WaybillDwmAppSptb02SimpleDwmSptb02Process").name("WaybillDwmAppSptb02SimpleDwmSptb02Process");

        //====================================sink mysql===============================================//
        String sql = MysqlUtil.getOnDuplicateKeySql(DwmSptb02No8TimeFields.class);
        dwmSptb02Process.addSink(JdbcSink.<DwmSptb02No8TimeFields>getSink(sql)).uid("WaybillDwmAppSptb02Simple_SinkMysql").name("WaybillDwmAppSptb02Simple_SinkMysql");

        log.info("将处理完的数据保存到clickhouse中");
        env.execute("Kafka:DwdSptb02->DwmSptb02(mysql & kafka)");
        log.info("sptb02dwd层job任务开始执行");
    }
}
