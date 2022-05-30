package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.DwdMysqlSink;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.ProvincesWide;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.DimUtil;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
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
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 对运单 sptb02 表进行统一数据格式 字段统一等
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwdApp {

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
                .setTopics(KafkaTopicConst.ODS_VLMS_SPTB02)//消费sptb02 原数据topic  ods_vlms_sptb02
                .setGroupId(KafkaTopicConst.ODS_VLMS_SPTB02_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer").uid("jsonDataStr").name("jsonDataStr");

        //从Kafka主题中获取消费端
        log.info("从kafka的主题:" + KafkaTopicConst.ODS_VLMS_SPTB02 + "中获取的要处理的数据");

        //将kafka中原始json数据转化成实例对象
        SingleOutputStreamOperator<Sptb02> objStream = jsonDataStr.map(new MapFunction<String, Sptb02>() {
            @Override
            public Sptb02 map(String json) throws Exception {
                System.out.println("kafka时消费到的数据:" + json);
                JSONObject jsonObject = JSON.parseObject(json);
                String after = jsonObject.getString("after");
                Sptb02 sptb02 = JSON.parseObject(after, Sptb02.class);
                System.out.println("转换成实体类对象后数据:" + sptb02);
                return sptb02;
            }
        }).uid("objStream").name("objStream");
        log.info("将kafka中原始json数据转化成实例对象");
        //对一些时间字段进行单独字段处理保存
        SingleOutputStreamOperator<DwdSptb02> dataDwdProcess = objStream.process(new ProcessFunction<Sptb02, DwdSptb02>() {
            @Override
            public void processElement(Sptb02 sptb02, Context context, Collector<DwdSptb02> collector) throws Exception {
                System.out.println("processElement方法开始时，数据值：" + sptb02);
                log.info("将kafka中原始json数据转化成实例对象");
                //处理实体类
                DwdSptb02 dwdSptb02 = new DwdSptb02();
                //将sptb02属性值copy到dwdSptb02
                //BeanUtils.copyProperties(sptb02, dwdSptb02);
                BeanUtil.copyProperties(sptb02, dwdSptb02);
                System.out.println("属性copy后值：" + dwdSptb02.toString());
                //获取原数据的运输方式
                String vysfs = sptb02.getVYSFS();
                System.out.println("运输方式：" + vysfs);
                if (StringUtils.isNotEmpty(vysfs)) {
                    //1.处理 运输方式 ('J','TD','SD','G')='G'   (''L1'','T') ='T'    ('S') ='S'
                    //('J','TD','SD','G')='G'
                    if (vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) {
                        dwdSptb02.setTRAFFIC_TYPE("G");
                        //运输方式 适配 lc_spec_config
                        dwdSptb02.setTRANS_MODE_CODE("1");
                    }
                    //(''L1'','T') ='T'
                    if (vysfs.equals("L1") || vysfs.equals("T")) {
                        dwdSptb02.setTRAFFIC_TYPE("T");
                        dwdSptb02.setTRANS_MODE_CODE("2");
                    }
                    //('S') ='S'
                    if (vysfs.equals("S")) {
                        dwdSptb02.setTRAFFIC_TYPE("S");
                        dwdSptb02.setTRANS_MODE_CODE("3");
                    }
                    //2.处理 起运时间
                    //公路取sptb02.dtvscfsj，其他取sptb02取DSJCFSJ(实际离长时间)的值，实际起运时间， 实际出发时间
                    if ((vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) && Objects.nonNull(sptb02.getDTVSCFSJ())) {
                        dwdSptb02.setSHIPMENT_TIME(sptb02.getDTVSCFSJ());
                    }
                    if ((vysfs.equals("L1") || vysfs.equals("T") || vysfs.equals("S")) && Objects.nonNull(sptb02.getDTVSCFSJ())) {
                        dwdSptb02.setSHIPMENT_TIME(sptb02.getDTVSCFSJ());
                    }
                }
                //3.处理 计划下达时间
                if (Objects.nonNull(sptb02.getDPZRQ())) {
                    dwdSptb02.setPLAN_RELEASE_TIME(sptb02.getDPZRQ());
                }
                //4.处理 运单指派时间
                if (Objects.nonNull(sptb02.getDYSSZPSJ())) {
                    dwdSptb02.setASSIGN_TIME(sptb02.getDYSSZPSJ());
                }
                //5.处理 打点到货时间
                if (Objects.nonNull(sptb02.getDGPSDHSJ())) {
                    dwdSptb02.setDOT_SITE_TIME(sptb02.getDGPSDHSJ());
                }
                //6.处理 最终到货时间
                if (Objects.nonNull(sptb02.getDDHSJ())) {
                    dwdSptb02.setFINAL_SITE_TIME(sptb02.getDDHSJ());
                }
                //7.处理 运单生成时间
                if (Objects.nonNull(sptb02.getDDJRQ())) {
                    dwdSptb02.setORDER_CREATE_TIME(sptb02.getDDJRQ());
                }
                //8.处理 基地代码 适配 lc_spec_config
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
                //9.处理 主机公司代码
                /**
                 * 主机公司代码 适配 lc_spec_config
                 *   1  一汽大众
                 *   2  一汽红旗
                 *   3  一汽马自达
                 */
                //获取主机公司代码
                //sptb02中原字段值含义： '大众','1','红旗','17','奔腾','2','马自达','29'
                String czjgsdm = sptb02.getCZJGSDM();
                if (StringUtils.isNotEmpty(czjgsdm)) {
                    if ("1".equals(czjgsdm)) {
                        dwdSptb02.setHOST_COM_CODE("1");
                    }
                    if ("17".equals(czjgsdm)) {
                        dwdSptb02.setHOST_COM_CODE("2");
                    }
                    if ("29".equals(czjgsdm)) {
                        dwdSptb02.setHOST_COM_CODE("3");
                    }
                }
                //添加新的处理逻辑(新加)
                //10.处理 ACTUAL_OUT_TIME(实际出库时间)  取 sptb02.dckrq字段
                if (Objects.nonNull(sptb02.getDCKRQ())) {
                    dwdSptb02.setACTUAL_OUT_TIME(sptb02.getDCKRQ());
                }
//                //11.处理 THEORY_OUT_TIME(理论出库时间)  取 sptb02.dpzrq 主机厂下达计划时间+1天  24小时= 86400L (造假数据) 需要在dwd层处理
//                if (Objects.nonNull(sptb02.getDPZRQ())) {
//                    dwdSptb02.setTHEORY_OUT_TIME(sptb02.getDPZRQ() + 86400L );
//                }

                //对保存的数据为null的填充默认值
                //DwdSptb02 bean = JsonPartUtil.getBean(dwdSptb02);
                //实际保存的值为after里的值
                System.out.println("处理完的数据填充后的值:" + dwdSptb02.toString());
                log.info("处理完的数据填充后的值:" + dwdSptb02.toString());
                collector.collect(dwdSptb02);
            }
        }).uid("dataDwdProcess").name("dataDwdProcess");


        /**
         *  处理 物理仓库信息 省区代码 市县代码
         *  inner join sptc34 b on a.vwlckdm = b.vwlckdm
         *  inner join v_sys_sysc07sysc08 v1 on b.vsqdm = v1.csqdm and b.vsxdm = v1.csxdm
         */
        SingleOutputStreamOperator<DwdSptb02> sptc34DS = AsyncDataStream.unorderedWait(
                dataDwdProcess,
                new DimAsyncFunction<DwdSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTC34,
                        "VWLCKDM") {

                    @Override
                    public Object getKey(DwdSptb02 dwdSptb02) {
                        String vwlckdm = dwdSptb02.getVWLCKDM();
                        if (StringUtils.isNotEmpty(vwlckdm)) {
                            return vwlckdm;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwdSptb02 dwdSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        dwdSptb02.setSTART_PROVINCE_CODE(dimInfoJsonObj.getString("VSQDM"));
                        dwdSptb02.setSTART_CITY_CODE(dimInfoJsonObj.getString("VSXDM"));
                    }
                },
                60, TimeUnit.SECONDS).uid("sptc34DS").name("sptc34DS");

        /**
         *  处理 经销商到货地 省区代码 市县代码
         *  inner join mdac32 e on a.cdhddm = e.cdhddm
         *  inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
         */
        SingleOutputStreamOperator<DwdSptb02> mdac32DS = AsyncDataStream.unorderedWait(
                sptc34DS,
                new DimAsyncFunction<DwdSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_MDAC32,
                        "CDHDDM") {

                    @Override
                    public Object getKey(DwdSptb02 dwdSptb02) {
                        String cdhddm = dwdSptb02.getCDHDDM();
                        if (StringUtils.isNotEmpty(cdhddm)) {
                            return cdhddm;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwdSptb02 dwdSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        dwdSptb02.setEND_PROVINCE_CODE(dimInfoJsonObj.getString("CSQDM"));
                        dwdSptb02.setEND_CITY_CODE(dimInfoJsonObj.getString("CSXDM"));
                    }
                },
                60, TimeUnit.SECONDS).uid("mdac32DS").name("mdac32DS");


        //将实体类映射成json
        SingleOutputStreamOperator<String> mapJson = mdac32DS.map(new MapFunction<DwdSptb02, String>() {
            @Override
            public String map(DwdSptb02 obj) throws Exception {
                DwdSptb02 bean = JsonPartUtil.getBean(obj);
                return JSON.toJSONString(bean);
            }
        }).uid("mapJson").name("mapJson");

        //组装kafka生产端
        dataDwdProcess.print("数据拉宽后结果输出:");
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWD_VLMS_SPTB02,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_SPTB02));
        //将处理完的数据保存到kafka
        log.info("将处理完的数据保存到kafka中");
        mapJson.addSink(sinkKafka).setParallelism(1).uid("dwd-sink-kafka").name("dwd-sink-kafka");


        /* 7.开窗,按照数量(后续改为按照时间窗口)*/
     /*   log.info("将处理完的数据保存到mysql中");
        mapJson.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        mapJson.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<String, List<DwdSptb02>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<String> iterable, Collector<List<DwdSptb02>> collector) throws Exception {
                List<DwdSptb02> list = new ArrayList<>();
                for (String s : iterable) {
                    DwdSptb02 dwdSptb02 = JSON.parseObject(s, DwdSptb02.class);
                    list.add(dwdSptb02);
                }
                if (list.size() > 0) {
                    collector.collect(list);
                }
            }
        }).addSink(JdbcSink.<DwdSptb02>getBatchSink()).setParallelism(1).uid("sink-mysql").name("sink-mysql");*/


        env.execute("sptb02-sink-kafka-dwd");
        log.info("sptb02dwd层job任务开始执行");
    }
}
