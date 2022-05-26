package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 对运单 sptb02 表进行统一数据格式 字段统一等
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmApp {

    public static void main(String[] args) throws Exception {
        //Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
      /*  CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);*/
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        KafkaSource<String> kafkaSourceBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
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
                        if (StringUtils.isNotEmpty(cjsdbh)) {
                            return cjsdbh;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //将维度中 用户表中username 设置给订单宽表中的属性
                        String vvin = dimInfoJsonObj.getString("VVIN");
                        dwmSptb02.setVVIN(vvin);
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


        //对实体类中null赋默认值
        SingleOutputStreamOperator<DwmSptb02> endData = sptc34DS.map(new MapFunction<DwmSptb02, DwmSptb02>() {
            @Override
            public DwmSptb02 map(DwmSptb02 obj) throws Exception {
                return JsonPartUtil.getBean(obj);
            }
        }).uid("endData").name("endData");

        //组装sql
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(KafkaTopicConst.DWM_VLMS_SPTB02).append(" values ").append(StrUtil.getValueSql(DwmSptb02.class));
        log.info("组装clickhouse插入sql:{}", sql);
        endData.addSink(ClickHouseUtil.<DwmSptb02>getSink(sql.toString())).setParallelism(1).uid("sink-clickhouse").name("sink-clickhouse");
        endData.print("拉宽后数据输出：");
        // mdac22DS.print("拉宽后输出数据：");
        log.info("将处理完的数据保存到clickhouse中");
        env.execute("sptb02-sink-clickhouse-dwm");
        log.info("sptb02dwd层job任务开始执行");
    }
}
