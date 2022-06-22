package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消费mysql的 dwd_vlms_sptb02的数据，异步查询维表，拓宽新的字段
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmAppSptb02Simple {

    public static void main(String[] args) throws Exception {
        //Flink 流式处理环境
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
        //System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");
        //mysql消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        //kafka消费源相关参数配置
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_SPTB02)
                .setGroupId(KafkaTopicConst.DWD_VLMS_SPTB02_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");

        SingleOutputStreamOperator<DwmSptb02> dwmSptb02Process = mysqlSource.process(new ProcessFunction<String, DwmSptb02>() {
            @Override
            public void processElement(String value, Context ctx, Collector<DwmSptb02> out) throws Exception {
                //获取真实数据
                DwmSptb02 dwmSptb02 = JsonPartUtil.getAfterObj(value, DwmSptb02.class);
                if (StringUtils.isNotBlank(dwmSptb02.getCJSDBH())) {
                    dwmSptb02.setSTART_PHYSICAL_CODE(dwmSptb02.getVFCZT());
                    dwmSptb02.setEND_PHYSICAL_CODE(dwmSptb02.getVSCZT());

                    /**
                     * 根据结算单编号查询vin码 产品代码
                     */
                    String cjsdbh = dwmSptb02.getCJSDBH();
                    log.info("sptb02d1DS阶段获取到的查询条件值:{}", cjsdbh);
                    if (StringUtils.isNotBlank(cjsdbh)) {
                        String sptb02d1Sql = "select * from " + KafkaTopicConst.ODS_VLMS_SPTB02D1 + " where CJSDBH = '" + cjsdbh + "' limit 1 ";
                        JSONObject sptb02d1 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTB02D1, sptb02d1Sql, cjsdbh);
                        if (sptb02d1 != null) {
                            dwmSptb02.setVVIN(sptb02d1.getString("VVIN"));
                            dwmSptb02.setVEHICLE_CODE(sptb02d1.getString("CCPDM"));
                        }
                    }

                    /**
                     //理论起运时间
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
                    if (StringUtils.isNotEmpty(hostComCode) && StringUtils.isNotEmpty(baseCode) && StringUtils.isNotEmpty(transModeCode)) {
                        String specConfigSql = "select * from " + KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG + " where HOST_COM_CODE = '" + hostComCode + "' and BASE_CODE = '" + baseCode + "' and TRANS_MODE_CODE = '" + transModeCode + "' AND STATUS = '1' AND SPEC_CODE = '4' limit 1 ";
                        JSONObject specConfig = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG, specConfigSql, hostComCode, baseCode, transModeCode);
                        if (specConfig != null) {
                            //定义要增加的时间戳
                            Long outSiteTime = null;
                            //获取增加的时间步长
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
                                //格式化时间
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formatDate = formatter.format(outSiteDate);
                                DateTime parse = DateUtil.parse(formatDate);
                                DateTime newStepTime = DateUtil.offsetHour(parse, Integer.parseInt(hours));
                                dwmSptb02.setTHEORY_SHIPMENT_TIME(newStepTime.getTime());
                            }
                        }
                    }

                    /**
                     * 处理主机公司名称
                     * 关联sptc61 c1 on a.CZJGSDM = c1.cid，取c1.cjc
                     */
                    String czjgsdm = dwmSptb02.getCZJGSDM();
                    log.info("sptc61DS阶段获取到的查询条件值:{}", czjgsdm);
                    if (StringUtils.isNotEmpty(czjgsdm)) {
                        String sptc61Sql = "select * from " + KafkaTopicConst.ODS_VLMS_SPTC61 + " where CID = '" + czjgsdm + "' limit 1 ";
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
                    if (StringUtils.isNotEmpty(cqwh)) {
                        String sptc62Sql = "select * from " + KafkaTopicConst.ODS_VLMS_SPTC62 + " where CID = '" + cqwh + "' limit 1 ";
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
                    if (StringUtils.isNotEmpty(vwlckdm)) {
                        String sptc34Sql = "select * from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vwlckdm + "' limit 1 ";
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
                    if (StringUtils.isNotEmpty(cyssdm)) {
                        String mdac52Sql = "select * from " + KafkaTopicConst.ODS_VLMS_MDAC52 + " where CCYDDM = '" + cyssdm + "' limit 1 ";
                        JSONObject mdac52 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC52, mdac52Sql, cyssdm);
                        if (mdac52 != null) {
                            if (StringUtils.isNotEmpty(mdac52.getString("VCYDJC"))) {
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
                        String mdac22Sql = "select * from " + KafkaTopicConst.ODS_VLMS_MDAC22 + " where CJXSDM = '" + vdwdm + "' limit 1 ";
                        JSONObject mdac22 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC22, mdac22Sql, vdwdm);
                        if (mdac22 != null) {
                            if (StringUtils.isNotEmpty(mdac22.getString("VJXSJC"))) {
                                dwmSptb02.setDEALER_NAME(mdac22.getString("VJXSJC"));
                            } else {
                                dwmSptb02.setTRANSPORT_NAME(mdac22.getString("VJXSMC"));
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
                    if (StringUtils.isNotEmpty(startProvinceCode) && StringUtils.isNotEmpty(startCityCode)) {
                        String provinceSql = "select * from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + startProvinceCode + "' and csxdm = '" + startCityCode + "' limit 1 ";
                        JSONObject province = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, startProvinceCode, startCityCode);
                        if (province != null) {
                            //省区名称：山东省
                            dwmSptb02.setSTART_PROVINCE_NAME(province.getString("vsqmc"));
                            //市县名称: 齐河
                            dwmSptb02.setSTART_CITY_NAME(province.getString("vsxmc"));
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
                    if (StringUtils.isNotEmpty(endProvinceCode) && StringUtils.isNotEmpty(endCityCode)) {
                        String provinceSql = "select * from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + endProvinceCode + "' and csxdm = '" + endCityCode + "' limit 1 ";
                        JSONObject province = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, endProvinceCode, endCityCode);
                        if (province != null) {
                            //例如 省区名称：山东省
                            dwmSptb02.setEND_PROVINCE_NAME(province.getString("vsqmc"));
                            //例如 市县名称: 齐河
                            dwmSptb02.setEND_CITY_NAME(province.getString("vsxmc"));
                        }
                    }

                    //实体类中null值进行默认值赋值
                    DwmSptb02 bean = JsonPartUtil.getBean(dwmSptb02);
                    out.collect(bean);
                }
            }
        }).uid("dwmSptb02Process").name("dwmSptb02Process");


        //===================================sink kafka=======================================================//
        SingleOutputStreamOperator<String> dwmSptb02Json = dwmSptb02Process.map(new MapFunction<DwmSptb02, String>() {
            @Override
            public String map(DwmSptb02 obj) throws Exception {
                return JSON.toJSONString(obj);
            }
        }).uid("dwmSptb02Json").name("dwmSptb02Json");
        //获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWM_VLMS_SPTB02,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWM_VLMS_SPTB02));

        dwmSptb02Json.addSink(sinkKafka).uid("sinkKafka").name("sinkKafka");


        //====================================sink mysql===============================================//
        String sql = MysqlUtil.getSql(DwmSptb02.class);
        dwmSptb02Process.addSink(JdbcSink.<DwmSptb02>getSink(sql)).uid("baseStationDataSink1").name("baseStationDataSink1");

        log.info("将处理完的数据保存到clickhouse中");
        env.execute("sptb02-sink-mysql-dwm");
        log.info("sptb02dwd层job任务开始执行");
    }
}
