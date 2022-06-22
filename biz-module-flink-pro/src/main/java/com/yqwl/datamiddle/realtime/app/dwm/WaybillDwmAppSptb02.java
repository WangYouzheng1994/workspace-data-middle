package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消费kafka里topic为dwd_vlms_sptb02的数据，异步查询维表，拓宽新的字段
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmAppSptb02 {

    public static void main(String[] args) throws Exception {
        //Flink 流式处理环境
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

        KafkaSource<String> kafkaSourceBuild = KafkaSource.<String>builder()
                .setBootstrapServers(KafkaUtil.KAFKA_SERVER)
                .setTopics(KafkaTopicConst.DWD_VLMS_SPTB02)//消费 dwd 层 sptb02表  dwd_vlms_sptb02
                .setGroupId(KafkaTopicConst.DWD_VLMS_SPTB02_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer").uid("jsonDataStr").name("jsonDataStr");

        //将kafka中原始json数据转化成实例对象
        SingleOutputStreamOperator<DwmSptb02> objStream = jsonDataStr.map(new MapFunction<String, DwmSptb02>() {
            @Override
            public DwmSptb02 map(String json) throws Exception {
                //after里的值转换成实例对象
                return JSON.parseObject(json, DwmSptb02.class);
            }
        }).uid("objStream").name("objStream");
        log.info("将kafka中after里数据转化成实例对象");

        objStream.print("source转换成实体类输出===>");

        /**
         * 关联ods_vlms_sptb02d1 获取车架号 VVIN 从mysql中获取
         */
        SingleOutputStreamOperator<DwmSptb02> sptb02d1DS = AsyncDataStream.unorderedWait(
                objStream,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTB02D1,
                        "CJSDBH") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String cjsdbh = dwmSptb02.getCJSDBH();
                        log.info("sptb02d1DS阶段获取到的查询条件值:{}", cjsdbh);
                        if (StringUtils.isNotEmpty(cjsdbh)) {
                            return cjsdbh;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //将维度中 用户表中username 设置给订单宽表中的属性
                        String vvin = dimInfoJsonObj.getString("VVIN");
                        log.info("sptb02d1DS阶段获取到的VVIN:{}", vvin);
                        //获取车型代码
                        String ccpdm = dimInfoJsonObj.getString("CCPDM");
                        log.info("sptb02d1DS阶段获取到的CCPDM:{}", ccpdm);
                        dwmSptb02.setVVIN(vvin);
//                        dwmSptb02.setCPCDBH(ccpdm);
                        dwmSptb02.setVEHICLE_CODE(ccpdm); //车型代码
                    }
                },
                60, TimeUnit.SECONDS).uid("sptb02d1DS").name("sptb02d1DS");
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
        SingleOutputStreamOperator<DwmSptb02> theoryShipmentTimeDS = AsyncDataStream.unorderedWait(
                sptb02d1DS,
                /**
                 * HOST_COM_CODE,   主机公司代码
                 * BASE_CODE,       基地代码
                 * TRANS_MODE_CODE  运输方式
                 */
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG,
                        "HOST_COM_CODE,BASE_CODE,TRANS_MODE_CODE",
                        " AND STATUS = '1' AND SPEC_CODE = '4'") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String hostComCode = dwmSptb02.getHOST_COM_CODE();
                        String baseCode = dwmSptb02.getBASE_CODE();
                        String transModeCode = dwmSptb02.getTRANS_MODE_CODE();
                        log.info("theoryShipmentTimeDS阶段获取到的查询条件值:{}, {}, {}", hostComCode, baseCode, transModeCode);
                        if (StringUtils.isNotEmpty(hostComCode) && StringUtils.isNotEmpty(baseCode) && StringUtils.isNotEmpty(transModeCode)) {
                            return Arrays.asList(dwmSptb02.getHOST_COM_CODE(), dwmSptb02.getBASE_CODE(), dwmSptb02.getTRANS_MODE_CODE());
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {

                        //定义要增加的时间戳
                        Long outSiteTime = null;
                        //获取增加的时间步长
                        String hours = dimInfoJsonObj.getString("STANDARD_HOURS");
                        //获取前置节点代码
                        String nodeCode = dimInfoJsonObj.getString("START_CAL_NODE_CODE").trim();
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
                },
                60, TimeUnit.SECONDS).uid("theoryShipmentTimeDS").name("theoryShipmentTimeDS");


        /**
         * 处理主机公司名称
         * 关联sptc61 c1 on a.CZJGSDM = c1.cid，取c1.cjc
         */
        SingleOutputStreamOperator<DwmSptb02> sptc61DS = AsyncDataStream.unorderedWait(
                theoryShipmentTimeDS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTC61,
                        "CID") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String czjgsdm = dwmSptb02.getCZJGSDM();
                        log.info("sptc61DS阶段获取到的查询条件值:{}", czjgsdm);
                        if (StringUtils.isNotEmpty(czjgsdm)) {
                            return czjgsdm;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        dwmSptb02.setCUSTOMER_NAME(dimInfoJsonObj.getString("CJC"));
                    }
                },
                60, TimeUnit.SECONDS).uid("sptc61DS").name("sptc61DS");

        /**
         * 处理发车基地名称
         * 关联sptc62 c2 on a.cqwh = c2.cid，取c2.cname
         */
        SingleOutputStreamOperator<DwmSptb02> sptc62DS = AsyncDataStream.unorderedWait(
                sptc61DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTC62,
                        "CID") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String cqwh = dwmSptb02.getCQWH();
                        log.info("sptc62DS阶段获取到的查询条件值:{}", cqwh);
                        if (StringUtils.isNotEmpty(cqwh)) {
                            return cqwh;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        dwmSptb02.setBASE_NAME(dimInfoJsonObj.getString("CNAME"));
                    }
                },
                60, TimeUnit.SECONDS).uid("sptc62DS").name("sptc62DS");


        /**
         * 处理发运仓库名称
         * 关联sptc34 b on a.vwlckdm = b.vwlckdm， 取b.vwlckmc
         */
        SingleOutputStreamOperator<DwmSptb02> sptc34DS = AsyncDataStream.unorderedWait(
                sptc62DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTC34,
                        "VWLCKDM") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String vwlckdm = dwmSptb02.getVWLCKDM();
                        log.info("sptc34DS阶段获取到的查询条件值:{}", vwlckdm);
                        if (StringUtils.isNotEmpty(vwlckdm)) {
                            return vwlckdm;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        dwmSptb02.setSHIPMENT_WAREHOUSE_NAME(dimInfoJsonObj.getString("VWLCKMC"));
                    }
                },

                60, TimeUnit.SECONDS).uid("sptc34DS").name("sptc34DS");

        /**
         * 处理 运输商名称
         * nvl(y.vcydjc,y.vcydmc) 运输商,
         * inner join mdac52 y on a.cyssdm = y.ccyddm
         */
        SingleOutputStreamOperator<DwmSptb02> mdac52DS = AsyncDataStream.unorderedWait(
                sptc34DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_MDAC52,
                        "CCYDDM") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        return dwmSptb02.getCYSSDM();
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        if (StringUtils.isNotEmpty(dimInfoJsonObj.getString("VCYDJC"))) {
                            dwmSptb02.setTRANSPORT_NAME(dimInfoJsonObj.getString("VCYDJC"));
                        } else {
                            dwmSptb02.setTRANSPORT_NAME(dimInfoJsonObj.getString("VCYDMC"));
                        }
                    }
                },
                60, TimeUnit.SECONDS).uid("mdac52DS").name("mdac52DS");


        /**
         * 处理经销商名称
         * nvl(j.vjxsjc,j.vjxsmc) vscdwmc
         * left join mdac22 j on a.vdwdm = j.cjxsdm
         */
        SingleOutputStreamOperator<DwmSptb02> mdac22DS = AsyncDataStream.unorderedWait(
                mdac52DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_MDAC22,
                        "CJXSDM") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        return dwmSptb02.getVDWDM();
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        if (StringUtils.isNotEmpty(dimInfoJsonObj.getString("VJXSJC"))) {
                            dwmSptb02.setDEALER_NAME(dimInfoJsonObj.getString("VJXSJC"));
                        } else {
                            dwmSptb02.setTRANSPORT_NAME(dimInfoJsonObj.getString("VJXSMC"));
                        }
                    }
                },
                60, TimeUnit.SECONDS).uid("mdac22DS").name("mdac22DS");




        //分配及时率  出库及时率  起运及时率 到货及时率
        /**
         * 处理理论出库时间 THEORY_OUT_TIME
         * 关联LC_SPEC_CONFIG 获取前置节点, 标准时长,指标代码,标准时长,状态
         * 前置节点代码  START_CAL_NODE_CODE
         * 标准时长(小时,需要转成秒级别)  STANDARD_HOURS
         * 指标代码(取2): SPEC_CODE  0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
         * 使用到dwdSptb02 的czjgsdm  cqwh vysfs
         * vysfs 已经处理成 TRAFFIC_TYPE    dwdsptb02
         *                 TRANS_MODE_CODE   lc_spec_config
         * czjgsdm  已经处理成sptb02.HOST_COM_CODE 1:大众  2:红旗 3:马自达
         * cqwh 已经处理成sptb02.BASE_CODE  1:长春  2:成都  3.佛山 5:天津
         * TODO:只处理大众理论出库时间  不区分公铁水和基地  取大众计划下达时间 DZJDJRQ+24小时(86400L)  主机公司代码是1 (大众)
         * TODO:红旗理论出库时间  基地只有长春基地,  取运输商指派时间 sptb02.DYSSZPSJ  公路:+24小时(86400L)   铁路:+72小时(259200L)  水路:+36小时(172800L)  红旗  主机公司代码是2
         * TODO:马自达理论出库时间  基地只有长春  取运输商指派时间 sptb02.DYSSZPSJ 公路:+24小时(86400L)  铁路和水路:+36小时(172800L)  马自达  主机公司代码是 3
         */
        SingleOutputStreamOperator<DwmSptb02> theoryouttimeDS = AsyncDataStream.unorderedWait(
                mdac22DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_LC_SPEC_CONFIG,
                        "HOST_COM_CODE,BASE_CODE,TRANS_MODE_CODE",
                        " AND STATUS = '1' AND SPEC_CODE = '2'") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String hostComCode = dwmSptb02.getHOST_COM_CODE();
                        String baseCode = dwmSptb02.getBASE_CODE();
                        String transModeCode = dwmSptb02.getTRANS_MODE_CODE();
                        if (StringUtils.isNotEmpty(hostComCode) && StringUtils.isNotEmpty(baseCode) && StringUtils.isNotEmpty(transModeCode)) {
                            return Arrays.asList(dwmSptb02.getHOST_COM_CODE(), dwmSptb02.getBASE_CODE(), dwmSptb02.getTRANS_MODE_CODE());
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {

                        //获取前置节点代码  START_CAL_NODE_CODE
                        String nodeCode = dimInfoJsonObj.getString("START_CAL_NODE_CODE").trim();
                        //将标准时长并将小时转换成毫秒级别  STANDARD_HOURS
                        Long second = Long.parseLong(dimInfoJsonObj.getString("STANDARD_HOURS")) * 60 * 60 * 1000L;
                        //获取主机公司代码 HOST_COM_CODE
                        String hostCode = dimInfoJsonObj.getString("HOST_COM_CODE").trim();
                        //获取运输方式名称  TRANS_MODE_NAME
                        String transName = dimInfoJsonObj.getString("TRANS_MODE_NAME").trim();
                        //获取基地名称 BASE_NAME
                        String baseName = dimInfoJsonObj.getString("BASE_NAME").trim();
                        //TODO:只处理大众理论出库时间  不区分公铁水和基地  取大众计划下达时间 DZJDJRQ +24小时(86400L)  主机公司代码是1 (大众)  主机厂下达计划时间  取sptb02.dpzrq
                        if ("1".equals(hostCode) && "DZJDJRQ".equals(nodeCode)) {
                            if (dwmSptb02.getDPZRQ() != 0) {
                                dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDPZRQ() + second);
                            }
                        }
                        //TODO:红旗理论出库时间  基地只有长春基地,  取运输商指派时间 sptb02.DYSSZPSJ  公路:+24小时(86400L)   铁路:+72小时(259200L)  水路:+36小时(172800L)  红旗  主机公司代码是2
                        if ("2".equals(hostCode) && "长春基地".equals(baseName) && "DYSSZPSJ".equals(nodeCode)) {
                            if (dwmSptb02.getDYSSZPSJ() != 0) {
                                switch (transName) {
                                    case "公路":
                                        dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
                                        break;
                                    case "铁路":
                                        dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
                                        break;
                                    case "水路":
                                        dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
                                        break;
                                }

                            }

                        }
                        //TODO:马自达理论出库时间  基地只有长春  取运输商指派时间 sptb02.DYSSZPSJ 公路:+24小时(86400L)  铁路和水路:+36小时(172800L)  马自达  主机公司代码是 3
                        if ("3".equals(hostCode) && "长春基地".equals(baseName) && "DYSSZPSJ".equals(nodeCode)) {
                            if (dwmSptb02.getDYSSZPSJ() != 0) {
                                if ("公路".equals(transName)) {
                                    dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
                                } else if ("铁路".equals(transName) || "水路".equals(transName)) {
                                    dwmSptb02.setTHEORY_OUT_TIME(dwmSptb02.getDYSSZPSJ() + second);
                                }
                            }
                        }
                    }
                },
                60, TimeUnit.SECONDS).uid("theoryouttimeDS").name("theoryouttimeDS");
        /**
         * 处理理论到货时间  THEORY_SITE_TIME (铁路和水路数据)
         * 先处理dwmsptb02表与sptb013表的字段
         * SPTB013 需要的字段 NSJSL,VPH
         * 1 (b3.nsjsl /10 >= 26) 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26) 3 b3.nsjsl /10 <15
         * 关联条件  left  join sptb013    b3 on a.vph = b3.vph
         * 字段  RAILWAY_TRAIN_TYPE  铁路种类
         */

        SingleOutputStreamOperator<DwmSptb02> sptb013DS = AsyncDataStream.unorderedWait(
                theoryouttimeDS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTB013, "VPH") {
                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        //获取vph
                        String vph = dwmSptb02.getVPH();
                        if (StringUtils.isNotEmpty(vph)) {
                            return vph;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //获取实际数量
                        Integer nsjsl = dimInfoJsonObj.getIntValue("NSJSL");
                        //1 (b3.nsjsl /10 >= 26)
                        // 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26)
                        // 3 b3.nsjsl /10 <15
                        if (nsjsl != 0) {
                            //大于26  1
                            if (nsjsl / 10 >= 26) {
                                dwmSptb02.setRAILWAY_TRAIN_TYPE(1);
                                //大于等于 15 小于26  2
                            } else if (nsjsl / 10 >= 15 && nsjsl / 10 < 26) {
                                dwmSptb02.setRAILWAY_TRAIN_TYPE(2);
                                //小于15 3
                            } else if (nsjsl / 10 < 15) {
                                dwmSptb02.setRAILWAY_TRAIN_TYPE(3);
                            }
                        }
                    }
                },
                60, TimeUnit.SECONDS).uid("sptb013DS").name("sptb013DS");


        /**
         * 处理理论到货时间
         * 与spti32表关联  取公路的理论到货时间
         *  ##### c1  sptc34  c2 mdac32  a sptb02  i spti32
         * left join spti32 i on c1.vsqdm = i.cqssqdm and c1.vsxdm = i.cqscsdm and c2.csqdm = i.cmbsqdm and c2.csxdm = i.cmbcsdm
         *        and a.czjgsdm = i.czjgs and a.vysfs = i.vysfs
         * 关联条件  CQSSQDM 起始省区代码  CQSCSDM 起始市县代码  CMBSQDM 目标省区代码 CMBCSDM 目标市县代码  CZJGS 主机公司代码  VYSFS 运输方式
         * SPTB02
         *            END_PROVINCE_CODE 到货地省区代码
         *           END_CITY_CODE  到货地市县代码
         *            START_PROVINCE_CODE  起货地省区代码
         *            START_CITY_CODE 起货地市县代码
         *            TRAFFIC_TYPE 运输方式
         *            HOST_COM_CODE 主机公司代码
         */
        SingleOutputStreamOperator<DwmSptb02> spti32DS = AsyncDataStream.unorderedWait(
                sptb013DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTI32, "CQSSQDM,CQSCSDM,CMBSQDM,CMBCSDM,CZJGS,VYSFS") {
                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        //获取起始省区代码
                        String startprovincecode = dwmSptb02.getSTART_PROVINCE_CODE();
                        //获取起始市县代码  START_CITY_CODE
                        String startcitycode = dwmSptb02.getSTART_CITY_CODE();
                        //获取到货地省区代码  END_PROVINCE_CODE
                        String endprovincecode = dwmSptb02.getEND_PROVINCE_CODE();
                        //获取到货地市县代码   END_CITY_CODE
                        String endcitycode = dwmSptb02.getEND_CITY_CODE();
                        //获取主机公司名称  HOST_COM_CODE
                        String czjgsdm = dwmSptb02.getCZJGSDM();
                        //获取运输方式  TRAFFIC_TYPE
                        String traffictype = dwmSptb02.getTRAFFIC_TYPE();
                        if (StringUtils.isNotEmpty(startprovincecode) && StringUtils.isNotEmpty(startcitycode)
                                && StringUtils.isNotEmpty(endprovincecode) && StringUtils.isNotEmpty(endcitycode)
                                && StringUtils.isNotEmpty(czjgsdm) && StringUtils.isNotEmpty(traffictype)) {
                            return Arrays.asList(dwmSptb02.getSTART_PROVINCE_CODE(), dwmSptb02.getSTART_CITY_CODE(),
                                    dwmSptb02.getEND_PROVINCE_CODE(), dwmSptb02.getEND_CITY_CODE()
                                    , dwmSptb02.getCZJGSDM(), dwmSptb02.getTRAFFIC_TYPE());
                        }

                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //VYSFS 运输方式
                        String vysfs = dimInfoJsonObj.getString("VYSFS");
                        //sptb02.dckrq+spti32.ndhsj_xt
                        Long ndhsj_xt = dimInfoJsonObj.getLong("NDHSJ_XT") * 60 * 60 * 1000L;
                        if (ndhsj_xt != 0 && "G".equals(vysfs)) {
                            dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDCKRQ() + ndhsj_xt);
                        }
                    }
                },
                60, TimeUnit.SECONDS).uid("spti32DS").name("spti32DS");

        /**
         * 处理理论到货时间  THEORY_SITE_TIME
         * 与 spti32_rail_sea 关联
         * 关联字段  CZJGSDM  主机公司名称  vysfs S T
         *  left  join spti32_rail_sea i1 on a.CZJGSDM = i1.CZJGSDM and decode(a.VYSFS,''S'',''S'',''T'') = i1.VYSFS and   a.VFCZT = i1.CQSZTDM and a.VSCZT = i1.CMBZTDM
         *  RAILWAY_TRAIN_TYPE  1 (b3.nsjsl /10 >= 26) 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26) 3 b3.nsjsl /10 <15
         *  SPTB02
         *  CZJGSDM 主机公司代码
         *  TRAFFIC_TYPE 运输方式
         *  VFCZT  发车站台
         *  VSCZT  收车站台
         */

        SingleOutputStreamOperator<DwmSptb02> spti32RailSeaDS = AsyncDataStream.unorderedWait(
                spti32DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTI32_RAIL_SEA, "CZJGSDM,VYSFS,CQSZTDM,CMBZTDM") {
                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        //获取主机公司名称,运输方式  发车站台  收车站台等
                        String czjgsdm = dwmSptb02.getCZJGSDM();
                        String traffictype = dwmSptb02.getTRAFFIC_TYPE();
                        String vfczt = dwmSptb02.getVFCZT();
                        String vsczt = dwmSptb02.getVSCZT();
                        if (StringUtils.isNotEmpty(czjgsdm) && StringUtils.isNotEmpty(traffictype) && StringUtils.isNotEmpty(vfczt) && StringUtils.isNotEmpty(vsczt)) {
                            return Arrays.asList(dwmSptb02.getCZJGSDM(), dwmSptb02.getTRAFFIC_TYPE(), dwmSptb02.getVFCZT(), dwmSptb02.getVSCZT());
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //获取NDHSJ_XTDH_ml(满列)  大于26  RAILWAY_TRAIN_TYPE 1 准换成毫秒级别数据
                        BigDecimal ml = dimInfoJsonObj.getBigDecimal("NDHSJ_XTDH_ML");
                        long ndhsjxtdhml = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(ml)).setScale(0).longValue();
                        //获取NDHSJ_XTDH_dz(大组)  15<= x < 26   RAILWAY_TRAIN_TYPE 2
                        BigDecimal dz = dimInfoJsonObj.getBigDecimal("NDHSJ_XTDH_DZ");
                        long ndhsjxtdhdz = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(dz)).setScale(0).longValue();
                        //获取NDHSJ_XTDH_sl(散列) 小于15  RAILWAY_TRAIN_TYPE 3
                        BigDecimal sl = dimInfoJsonObj.getBigDecimal("NDHSJ_XTDH_SL");
                        long ndhsjxtdhsl = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(sl)).setScale(0).longValue();
                        //ndhsj_dz_ml  S
                        BigDecimal dzml = dimInfoJsonObj.getBigDecimal("NDHSJ_DZ_ML");
                        long ndhsjdzml = BigDecimalUtil.multiply(BigDecimalUtil.getBigDecimal("3600000"), BigDecimalUtil.getBigDecimal(dzml)).setScale(0).longValue();
                        //left  join spti32_rail_sea i1 on a.CZJGSDM = i1.CZJGSDM and decode(a.VYSFS,''S'',''S'',''T'') = i1.VYSFS and   a.VFCZT = i1.CQSZTDM and a.VSCZT = i1.CMBZTDM
                        String seavysfs = dimInfoJsonObj.getString("VYSFS");
                        if (dwmSptb02.getDSJCFSJ() != 0) {
                            if ("S".equals(seavysfs)) {
                                dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjdzml);
                            } else if ("T".equals(seavysfs)) {
                                if (dwmSptb02.getRAILWAY_TRAIN_TYPE() == 1) {
                                    dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjxtdhml);
                                } else if (dwmSptb02.getRAILWAY_TRAIN_TYPE() == 2) {
                                    dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjxtdhdz);
                                } else if (dwmSptb02.getRAILWAY_TRAIN_TYPE() == 3) {
                                    dwmSptb02.setTHEORY_SITE_TIME(dwmSptb02.getDSJCFSJ() + ndhsjxtdhsl);
                                }
                            }
                        }
                    }
                },
                60, TimeUnit.SECONDS).uid("spti32RailSeaDS").name("spti32RailSeaDS");

        /**
         * 处理 起货地 物理仓库代码  省区 县区名称
         * 关联合并后的维表 dim_vlms_provinces
         *   inner join sptc34 b on a.vwlckdm = b.vwlckdm
         *   inner join mdac32 e on a.cdhddm = e.cdhddm
         *   inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
         *   inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
         */
        SingleOutputStreamOperator<DwmSptb02> provincesSptc34DS = AsyncDataStream.unorderedWait(
                spti32RailSeaDS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.DIM_VLMS_PROVINCES,
                        "csqdm,csxdm") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String startProvinceCode = dwmSptb02.getSTART_PROVINCE_CODE();
                        String startCityCode = dwmSptb02.getSTART_CITY_CODE();
                        log.info("provincesSptc34DS阶段异步查询获取的查询省编码值:{}, 市县编码值:{}", startProvinceCode, startCityCode);
                        if (StringUtils.isNotEmpty(startProvinceCode) && StringUtils.isNotEmpty(startCityCode)) {
                            return Arrays.asList(startProvinceCode, startCityCode);
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //省区名称：山东省
                        dwmSptb02.setSTART_PROVINCE_NAME(dimInfoJsonObj.getString("vsqmc"));
                        //市县名称: 齐河
                        dwmSptb02.setSTART_CITY_NAME(dimInfoJsonObj.getString("vsxmc"));
                    }
                },
                60, TimeUnit.SECONDS).uid("provincesSptc34DS").name("provincesSptc34DS");


        /**
         * 处理 到货地  省区 县区名称
         * 关联合并后的维表 dim_vlms_provinces
         *   inner join sptc34 b on a.vwlckdm = b.vwlckdm
         *   inner join mdac32 e on a.cdhddm = e.cdhddm
         *   inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
         *   inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
         */
        SingleOutputStreamOperator<DwmSptb02> provincesMdac32DS = AsyncDataStream.unorderedWait(
                provincesSptc34DS,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.DIM_VLMS_PROVINCES,
                        "csqdm,csxdm") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        String endProvinceCode = dwmSptb02.getEND_PROVINCE_CODE();
                        String endCityCode = dwmSptb02.getEND_CITY_CODE();
                        log.info("provincesMdac32DS阶段异步查询获取的查询省编码值:{}, 市县编码值:{}", endProvinceCode, endCityCode);
                        if (StringUtils.isNotEmpty(endProvinceCode) && StringUtils.isNotEmpty(endCityCode)) {
                            return Arrays.asList(endProvinceCode, endCityCode);
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //例如 省区名称：山东省
                        dwmSptb02.setEND_PROVINCE_NAME(dimInfoJsonObj.getString("vsqmc"));
                        //例如 市县名称: 齐河
                        dwmSptb02.setEND_CITY_NAME(dimInfoJsonObj.getString("vsxmc"));
                    }
                },
                60, TimeUnit.SECONDS).uid("provincesMdac32DS").name("provincesMdac32DS");


        //对实体类中null赋默认值
        SingleOutputStreamOperator<DwmSptb02> endData = provincesMdac32DS.map(new MapFunction<DwmSptb02, DwmSptb02>() {
            @Override
            public DwmSptb02 map(DwmSptb02 obj) throws Exception {
                return JsonPartUtil.getBean(obj);
            }
        }).uid("endData").name("endData");

        endData.print("最终数据输出：");
        //====================================sink clickhouse===============================================//
        //组装sql
        //StringBuffer sql = new StringBuffer();
        //sql.append("insert into ").append(KafkaTopicConst.DWM_VLMS_SPTB02_TEST1).append(" values ").append(StrUtil.getValueSql(DwmSptb02.class));
        //log.info("组装clickhouse插入sql:{}", sql);
        //endData.addSink(ClickHouseUtil.<DwmSptb02>getSink(sql.toString())).setParallelism(1).uid("sink-clickhouse").name("sink-clickhouse");
        //endData.print("拉宽后数据输出：");
        // mdac22DS.print("拉宽后输出数据：");


        //====================================sink mysql===============================================//
      /*  SingleOutputStreamOperator<DwmSptb02> dwmSptb02Watermark = endData.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        SingleOutputStreamOperator<List<DwmSptb02>> dwmSptb02Window = dwmSptb02Watermark.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<DwmSptb02, List<DwmSptb02>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<DwmSptb02> iterable, Collector<List<DwmSptb02>> collector) throws Exception {
                ArrayList<DwmSptb02> list = Lists.newArrayList(iterable);
                if (list.size() > 0) {
                    collector.collect(list);
                }
            }
        }).uid("dwmSptb02Window").name("dwmSptb02Window");

        dwmSptb02Window.addSink(JdbcSink.<DwmSptb02>getBatchSink()).setParallelism(1).uid("sink-mysql").name("sink-mysql");*/


/*        //====================================消费dwd_vlms_base_station_data的binlog 更新===============================================//
        //读取mysql配置
        Props props = PropertiesUtil.getProps();
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwd_vlms_base_station_data")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization())
                .build();

        SingleOutputStreamOperator<String> mysqlStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL-Source").uid("mysqlSource").name("mysqlSource");
        //将json转成obj
        SingleOutputStreamOperator<DwdBaseStationData> baseStationDataMap = mysqlStream.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String json) throws Exception {
                return JsonPartUtil.getAfterObj(json, DwdBaseStationData.class);
            }
        }).uid("baseStationDataMap").name("baseStationDataMap");

        baseStationDataMap.addSink(new SimpleBaseStationDataSink<DwdBaseStationData>()).uid("aseStationDataSink").name("aseStationDataSink");*/


        log.info("将处理完的数据保存到clickhouse中");
        env.execute("WaybillDwmApp");
        log.info("sptb02dwd层job任务开始执行");
    }
}
