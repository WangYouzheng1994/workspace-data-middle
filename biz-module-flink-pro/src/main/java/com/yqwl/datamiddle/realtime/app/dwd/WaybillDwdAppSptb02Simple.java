package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import com.yqwl.datamiddle.realtime.beanmapper.Sptb02Mapper;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 消费kafka里topic为ods_vlms_sptb02的数据，对其中一些字段进行统一处理或字段拓宽 拓宽到dwd_vlms_sptb02表
 * @Author: xiaofeng
 * @Date: 2022/06/12
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwdAppSptb02Simple {
    //2022-01-01 00:00:00
    private static final long START = 1640966400000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;
    public static void main(String[] args) throws Exception {
        // 从偏移量表中读取指定的偏移量模式
        HashMap<TopicPartition, Long> offsetMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(KafkaTopicConst.ODS_VLMS_SPTB02_LATEST_0701, 0);
        offsetMap.put(topicPartition, 112127L);
        // Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        // 设置并行度为1
        env.setParallelism(1);
        log.info("初始化流处理环境完成");

        // ====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        // 设置checkpoint点二级目录位置
         ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("waybill_dwd_sptb02_simple"));
        // 设置savepoint点二级目录位置
        // env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("waybill_dwd_sptb02_simple"));

        log.info("checkpoint设置完成");

        // kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SPTB02_LATEST_0701)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SPTB02_LATEST_0701_GROUP)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                // .setStartingOffsets(OffsetsInitializer.offsets(offsetMap)) // 指定起始偏移量 60 6-1
                .build();

        // 1.将kafka中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "WaybillDwdAppSptb02SimpleMysqlSource").uid("WaybillDwdAppSptb02SimpleMysqlSourceStream").name("WaybillDwdAppSptb02SimpleMysqlSourceStream");

        // 对一些时间字段进行单独字段处理保存
        SingleOutputStreamOperator<DwdSptb02> dataDwdProcess = mysqlSource.process(new ProcessFunction<String, DwdSptb02>() {
            @Override
            public void processElement(String value, Context context, Collector<DwdSptb02> collector) throws Exception {
                // 获取真实数据
                JSONObject jsonObject = JSON.parseObject(value);
                String after = jsonObject.getString("after");
                Sptb02 sptb02 = JSON.parseObject(after, Sptb02.class);
                if (Objects.nonNull(sptb02) && StringUtils.isNotBlank(sptb02.getCJSDBH())) {
                    Long ddjrq = sptb02.getDDJRQ();
                    if (ddjrq >= START && ddjrq <= END) {
                        // 处理实体类 将数据copy到dwdSptb02
                        DwdSptb02 dwdSptb02 = Sptb02Mapper.INSTANCT.conver(sptb02);
                        // 获取原数据的运输方式
                        String vysfs = sptb02.getVYSFS();
                        log.info("运单运输方式数据:{}", vysfs);
                        if (StringUtils.isNotEmpty(vysfs)) {
                            // 1.处理 运输方式 ('J','TD','SD','G')='G'   (''L1'','T') ='T'    ('S') ='S'
                            // ('J','TD','SD','G')='G'
                            if (vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) {
                                dwdSptb02.setTRAFFIC_TYPE("G");
                                // 运输方式 适配 lc_spec_config
                                dwdSptb02.setTRANS_MODE_CODE("1");
                            }
                            // (''L1'','T') ='T'
                            if (vysfs.equals("L1") || vysfs.equals("T")) {
                                dwdSptb02.setTRAFFIC_TYPE("T");
                                dwdSptb02.setTRANS_MODE_CODE("2");
                            }
                            // ('S') ='S'
                            if (vysfs.equals("S")) {
                                dwdSptb02.setTRAFFIC_TYPE("S");
                                dwdSptb02.setTRANS_MODE_CODE("3");
                            }
                            // 2.处理 起运时间
                            // 公路取sptb02.dtvscfsj，铁水取sptb02取DSJCFSJ(实际离长时间)的值，实际起运时间， 实际出发时间
                            if ((vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) && Objects.nonNull(sptb02.getDTVSCFSJ())) {
                                dwdSptb02.setSHIPMENT_TIME(sptb02.getDTVSCFSJ());
                            }
                            if ((vysfs.equals("L1") || vysfs.equals("T") || vysfs.equals("S")) && Objects.nonNull(sptb02.getDSJCFSJ())) {
                                dwdSptb02.setSHIPMENT_TIME(sptb02.getDSJCFSJ());
                            }
                        }
                        // 3.处理 计划下达时间
                        if (Objects.nonNull(sptb02.getDPZRQ())) {
                            dwdSptb02.setPLAN_RELEASE_TIME(sptb02.getDPZRQ());
                        }
                        // 4.处理 运单指派时间
                        if (Objects.nonNull(sptb02.getDYSSZPSJ())) {
                            dwdSptb02.setASSIGN_TIME(sptb02.getDYSSZPSJ());
                        }
                        // 5.处理 打点到货时间
                        if (Objects.nonNull(sptb02.getDGPSDHSJ())) {
                            dwdSptb02.setDOT_SITE_TIME(sptb02.getDGPSDHSJ());
                        }
                        // 6.处理 最终到货时间
                        if (Objects.nonNull(sptb02.getDDHSJ())) {
                            dwdSptb02.setFINAL_SITE_TIME(sptb02.getDDHSJ());
                        }
                        // 7.处理 运单生成时间
                        if (Objects.nonNull(sptb02.getDDJRQ())) {
                            dwdSptb02.setORDER_CREATE_TIME(sptb02.getDDJRQ());
                        }
                        // 8.处理 基地代码 适配 lc_spec_config
                        String cqwh = sptb02.getCQWH();
                        if (Objects.nonNull(cqwh)) {
                            /**
                             * 0431、 -> 1  长春基地
                             * 022、  -> 5  天津基地
                             * 027、
                             * 028、  -> 2  成都基地
                             * 0757   -> 3  佛山基地
                             */
                            if ("0431".equals(cqwh)) {
                                dwdSptb02.setBASE_CODE("1");
                            }
                            if ("022".equals(cqwh)) {
                                dwdSptb02.setBASE_CODE("5");
                            }
                            if ("028".equals(cqwh)) {
                                dwdSptb02.setBASE_CODE("2");
                            }
                            if ("0757".equals(cqwh)) {
                                dwdSptb02.setBASE_CODE("3");
                            }
                        }
                        // 9.处理 主机公司代码
                        /**
                         * 主机公司代码 适配 lc_spec_config
                         *   1  一汽大众
                         *   2  一汽红旗
                         *   3  一汽马自达
                         */
                        // 获取主机公司代码
                        // sptb02中原字段值含义： 1 大众  2 奔腾 3解放  17 红旗  29 马自达
                        // 主机公司代码          1 大众  2 奔腾 3解放  17 红旗  29 马自达
                        String czjgsdm = sptb02.getCZJGSDM();
                        if (StringUtils.isNotEmpty(czjgsdm)) {
                            // 大众 1
                            if ("1".equals(czjgsdm)) {
                                dwdSptb02.setHOST_COM_CODE("1");
                            }
                            // 红旗 2
                            if ("17".equals(czjgsdm)) {
                                dwdSptb02.setHOST_COM_CODE("2");
                            }
                            // 马自达 3
                            if ("29".equals(czjgsdm)) {
                                dwdSptb02.setHOST_COM_CODE("3");
                            }
                            // 奔腾 4
                            if ("2".equals(czjgsdm)) {
                                dwdSptb02.setHOST_COM_CODE("4");
                            }
                            // 解放 5
                            if ("3".equals(czjgsdm)) {
                                dwdSptb02.setHOST_COM_CODE("5");
                            }

                        }
                        // 添加新的处理逻辑(新加)
                        // 10.处理 ACTUAL_OUT_TIME(实际出库时间)  取 sptb02.dckrq字段
                        if (Objects.nonNull(sptb02.getDCKRQ())) {
                            dwdSptb02.setACTUAL_OUT_TIME(sptb02.getDCKRQ());
                        }
                        //========================直接查询sql==================================//
                        /**
                         *  处理 物理仓库信息 省区代码 市县代码
                         *  inner join sptc34 b on a.vwlckdm = b.vwlckdm
                         *  inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
                         */
                        String vwlckdm = dwdSptb02.getVWLCKDM();
                        log.info("sptc34DS阶段获取到的查询条件值:{}", vwlckdm);
                        if (StringUtils.isNotBlank(vwlckdm)) {
                            String sptc34Sql = "select VSQDM, VSXDM from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vwlckdm + "' limit 1 ";
                            JSONObject odsVlmsSptc34 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, sptc34Sql, vwlckdm);
                            if (odsVlmsSptc34 != null) {
                                dwdSptb02.setSTART_PROVINCE_CODE(odsVlmsSptc34.getString("VSQDM"));
                                dwdSptb02.setSTART_CITY_CODE(odsVlmsSptc34.getString("VSXDM"));
                            }
                        }
                        /**
                         *  处理 经销商到货地 省区代码 市县代码
                         *  inner join mdac32 e on a.cdhddm = e.cdhddm
                         *  inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
                         */
                        String cdhddm = dwdSptb02.getCDHDDM();
                        log.info("mdac32DS阶段获取到的查询条件值:{}", cdhddm);
                        if (StringUtils.isNotBlank(cdhddm)) {
                            String sptc34Sql = "select CSQDM, CSXDM from " + KafkaTopicConst.ODS_VLMS_MDAC32 + " where CDHDDM = '" + cdhddm + "' limit 1 ";
                            JSONObject mdac32 = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_MDAC32, sptc34Sql, cdhddm);
                            if (mdac32 != null) {
                                dwdSptb02.setEND_PROVINCE_CODE(mdac32.getString("CSQDM"));
                                dwdSptb02.setEND_CITY_CODE(mdac32.getString("CSXDM"));
                            }
                        }
                        /**
                         *  处理 发车站台 对应的仓库代码 仓库名称
                         *  from sptb02 a
                         *  inner join sptb02d1 b         on a.cjsdbh = b.cjsdbh
                         *  left join site_warehouse c    on a.vfczt = c.vwlckdm and c.type =  'CONTRAST'
                         */
                        String vfczt = dwdSptb02.getVFCZT();
                        log.info("vfcztSiteWarehouseDS阶段获取到的查询条件值:{}", vfczt);
                        if (StringUtils.isNotBlank(vfczt)) {
                            String siteWarehouseSql = "select WAREHOUSE_CODE, WAREHOUSE_NAME from " + KafkaTopicConst.ODS_VLMS_SITE_WAREHOUSE + " where `TYPE` = 'CONTRAST' and VWLCKDM = '" + vfczt + "' limit 1 ";
                            JSONObject siteWarehouse = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SITE_WAREHOUSE, siteWarehouseSql, vfczt);
                            if (siteWarehouse != null) {
                                dwdSptb02.setSTART_WAREHOUSE_CODE(siteWarehouse.getString("WAREHOUSE_CODE"));
                                dwdSptb02.setSTART_WAREHOUSE_NAME(siteWarehouse.getString("WAREHOUSE_NAME"));
                            } else {
                                // 兜底行为  优先取溯源，取不到 取sptc34的vwlckmc 禅道#877 20220722
                                String sptc34Sql = "select VWLCKDM, VWLCKMC from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vfczt + "'";
                                JSONObject sptc34Vwlckdm = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, sptc34Sql, vfczt);
                                if (sptc34Vwlckdm != null) {
                                    dwdSptb02.setSTART_WAREHOUSE_CODE(sptc34Vwlckdm.getString("VWLCKDM"));
                                    dwdSptb02.setSTART_WAREHOUSE_NAME(sptc34Vwlckdm.getString("VWLCKMC"));
                                }
                            }

                        }
                        /**
                         *  处理 收车站台 对应的仓库代码 仓库名称
                         *  from sptb02 a
                         *  inner join sptb02d1 b    on a.cjsdbh = b.cjsdbh
                         *  left join site_warehouse c    on a.vfczt = c.vwlckdm and c.type =  'CONTRAST'
                         */
                        String vsczt = dwdSptb02.getVSCZT();
                        log.info("vscztSiteWarehouseDS阶段获取到的查询条件值:{}", vsczt);
                        if (StringUtils.isNotBlank(vsczt)) {
                            String siteWarehouseSql = "select WAREHOUSE_CODE, WAREHOUSE_NAME from " + KafkaTopicConst.ODS_VLMS_SITE_WAREHOUSE + " where `TYPE` = 'CONTRAST' and VWLCKDM = '" + vsczt + "' limit 1 ";
                            JSONObject siteWarehouse = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SITE_WAREHOUSE, siteWarehouseSql, vsczt);
                            if (siteWarehouse != null) {
                                dwdSptb02.setEND_WAREHOUSE_CODE(siteWarehouse.getString("WAREHOUSE_CODE"));
                                dwdSptb02.setEND_WAREHOUSE_NAME(siteWarehouse.getString("WAREHOUSE_NAME"));
                            } else if (siteWarehouse == null) {
                                String sptc34Sql = "select VWLCKDM, VWLCKMC from " + KafkaTopicConst.ODS_VLMS_SPTC34 + " where VWLCKDM = '" + vsczt + "'";
                                JSONObject sptc34Vwlckdm = MysqlUtil.querySingle(KafkaTopicConst.ODS_VLMS_SPTC34, sptc34Sql, vsczt);
                                if (sptc34Vwlckdm != null) {
                                    // 兜底行为  优先取溯源，取不到 取sptc34的vwlckmc 禅道#877 20220722
                                    dwdSptb02.setEND_WAREHOUSE_CODE(sptc34Vwlckdm.getString("VWLCKDM"));
                                    dwdSptb02.setEND_WAREHOUSE_NAME(sptc34Vwlckdm.getString("VWLCKMC"));
                                }
                            }
                        }
                        /**
                         *  处理 公路单的对应的物理仓库代码对应的类型
                         *  left join site_warehouse c    on a.vfczt = c.vwlckdm
                         */
                        String vwlckdm1 = dwdSptb02.getVWLCKDM();
                        log.info("highwayWarehouseTypeDS:{}", vwlckdm1);
                        if (StringUtils.isNotBlank(vwlckdm1)) {
                            String siteWarehouseSql = "SELECT r.WAREHOUSE_TYPE FROM ods_vlms_site_warehouse s JOIN ods_vlms_rfid_warehouse r ON s.WAREHOUSE_CODE=r.WAREHOUSE_CODE WHERE s.`TYPE` = 'CONTRAST' and s.VWLCKDM = '" + vwlckdm1 + "' LIMIT 1";
                            JSONObject siteWarehouse = MysqlUtil.querySingle("ods_vlms_site_warehouse:ods_vlms_rfid_warehouse", siteWarehouseSql, vwlckdm1);
                            if (siteWarehouse != null) {
                                dwdSptb02.setHIGHWAY_WAREHOUSE_TYPE(siteWarehouse.getString("WAREHOUSE_TYPE"));
                            }
                        }

                        // 对保存的数据为null的填充默认值
                        DwdSptb02 bean = JsonPartUtil.getBean(dwdSptb02);
                        // 实际保存的值为after里的值
                        collector.collect(bean);
                        dwdSptb02 = null;
                    }
                }
            }
        }).setParallelism(4).uid("WaybillDwdAppSptb02SimpleDataDwdProcess").name("WaybillDwdAppSptb02SimpleDataDwdProcess");

        //===================================sink kafka=======================================================//
        SingleOutputStreamOperator<String> dwdSptb02Json = dataDwdProcess.map(new MapFunction<DwdSptb02, String>() {
            @Override
            public String map(DwdSptb02 obj) throws Exception {
                return JSON.toJSONString(obj);
            }
        }).uid("WaybillDwdAppSptb02SimpleDwdSptb02Json").name("WaybillDwdAppSptb02SimpleDwdSptb02Json");

        // 获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWD_VLMS_SPTB02,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_SPTB02));

        dwdSptb02Json.addSink(sinkKafka).setParallelism(1).uid("WaybillDwdAppSptb02SimpleSinkKafkaDwdSptb02Simple").name("WaybillDwdAppSptb02SimpleSinkKafkaDwdSptb02Simple");

        //===================================sink mysql=======================================================//

        String sql = MysqlUtil.getSql(DwdSptb02.class);
        dataDwdProcess.addSink(JdbcSink.<DwdSptb02>getSink(sql)).setParallelism(1).uid("WaybillDwdAppSptb02SimpleSinkMysqlDwdSptb02Simple").name("WaybillDwdAppSptb02SimpleSinkMysqlDwdSptb02Simple");

        env.execute("sptb02-sink-mysql-dwd");
        log.info("sptb02dwd层job任务开始执行");
    }
}
