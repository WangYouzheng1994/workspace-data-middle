package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
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
public class WaybillDwmAppSptb02And02D1Copy {

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
        //mysql消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("cdc.mysql.table.list"), ","))
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");

        // 2.过滤出Sptb02的表
        DataStream<String> filterBsdDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_flink") && jo.getString("tableName").equals("dwd_vlms_sptb02")) {
                    DwmSptb02 after = jo.getObject("after", DwmSptb02.class);
                    String cjsdbh = after.getCJSDBH();
                    if (cjsdbh != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterods_vlms_sptb02").name("filterdwd_vlms_sptb02");

        //3.转换Sptb02为实体类
        SingleOutputStreamOperator<DwmSptb02> mapBsd = filterBsdDs.map(new MapFunction<String, DwmSptb02>() {
            @Override
            public DwmSptb02 map(String kafkaBsdValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdValue);
                DwmSptb02 dataBsd = jsonObject.getObject("after", DwmSptb02.class);
//                Timestamp ts = jsonObject.getTimestamp("ts");
//                dataBsd.setTs(ts);
                return dataBsd;
            }
        }).uid("transitionSptb02").name("transitionSptb02");

        //4.根据车型代码,查出车辆名称
        SingleOutputStreamOperator<DwmSptb02> ootdAddCarNameStream = AsyncDataStream.unorderedWait(mapBsd,
                new DimAsyncFunction<DwmSptb02>(DimUtil.MYSQL_DB_TYPE, "ods_vlms_sptb02d1", "VVIN,CCPDM") {
                    @Override
                    public Object getKey(DwmSptb02 ootd) {
                        if (StringUtils.isNotBlank(ootd.getCJSDBH())) {
                            String cjsdbh = ootd.getCJSDBH();
                            return cjsdbh;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwmSptb02 ootd, JSONObject dimInfoJsonObj) throws Exception {
                        if (dimInfoJsonObj.getString("VVIN") != null) {
                            ootd.setVVIN(dimInfoJsonObj.getString("VVIN"));
                        }
                        if (dimInfoJsonObj.getString("CCPDM") != null) {
                            ootd.setVVIN(dimInfoJsonObj.getString("CCPDM"));
                        }
                    }
                }, 60, TimeUnit.SECONDS).uid("sptb02+02d1c").name("sptb02+02d1c");


        /**
         * 关联ods_vlms_sptb02d1 获取车架号 VVIN 从mysql中获取
         */
        SingleOutputStreamOperator<DwmSptb02> sptb02d1DS = AsyncDataStream.unorderedWait(
                mapBsd,
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


        /**
         * 处理 起货地 物理仓库代码  省区 县区名称
         * 关联合并后的维表 dim_vlms_provinces
         *   inner join sptc34 b on a.vwlckdm = b.vwlckdm
         *   inner join mdac32 e on a.cdhddm = e.cdhddm
         *   inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
         *   inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
         */
        SingleOutputStreamOperator<DwmSptb02> provincesSptc34DS = AsyncDataStream.unorderedWait(
                mdac22DS,
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

        //====================================sink clickhouse===============================================//
        //组装sql
        //StringBuffer sql = new StringBuffer();
        //sql.append("insert into ").append(KafkaTopicConst.DWM_VLMS_SPTB02_TEST1).append(" values ").append(StrUtil.getValueSql(DwmSptb02.class));
        //log.info("组装clickhouse插入sql:{}", sql);
        //endData.addSink(ClickHouseUtil.<DwmSptb02>getSink(sql.toString())).setParallelism(1).uid("sink-clickhouse").name("sink-clickhouse");
        //endData.print("拉宽后数据输出：");
        // mdac22DS.print("拉宽后输出数据：");

        endData.print("拉宽后数据输出:");
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




        log.info("将处理完的数据保存到clickhouse中");
        env.execute("sptb02-sink-clickhouse-dwm");
        log.info("sptb02dwd层job任务开始执行");
    }
}