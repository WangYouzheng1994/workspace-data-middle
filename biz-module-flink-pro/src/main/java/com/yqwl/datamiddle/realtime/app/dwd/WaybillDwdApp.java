package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DwdMysqlSink;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.Objects;

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
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSourceBuild = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SPTB02)//消费sptb02 原数据topic  ods_vlms_sptb02
                .setGroupId(KafkaTopicConst.ODS_VLMS_SPTB02_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //将kafka中源数据转化成DataStream
        DataStreamSource<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer");
        env.setParallelism(1);
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
        SingleOutputStreamOperator<String> dataDwdProcess = objStream.process(new ProcessFunction<Sptb02, String>() {
            @Override
            public void processElement(Sptb02 sptb02, Context context, Collector<String> collector) throws Exception {
                System.out.println("processElement方法开始时，数据值：" + sptb02);
                log.info("将kafka中原始json数据转化成实例对象");
                //处理实体类
                DwdSptb02 dwdSptb02 = new DwdSptb02();
                //将sptb02属性值copy到dwdSptb02
                //BeanUtils.copyProperties(sptb02, dwdSptb02);
                BeanUtil.copyProperties(sptb02, dwdSptb02);
                System.out.println("属性copy后值：" + dwdSptb02.toString());
                //获取原数据的运输方式
                String vysfs = sptb02.getVysfs();
                System.out.println("运输方式：" + vysfs);
                if (StringUtils.isNotEmpty(sptb02.getVysfs())) {
                    //1.处理 运输方式 ('J','TD','SD','G')='G'   (''L1'','T') ='T'    ('S') ='S'
                    //('J','TD','SD','G')='G'
                    if (vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) {
                        dwdSptb02.setTrafficType("G");
                        //运输方式 适配 lc_spec_config
                        dwdSptb02.setTransModeCode("1");
                    }
                    //(''L1'','T') ='T'
                    if (vysfs.equals("L1") || vysfs.equals("T")) {
                        dwdSptb02.setTrafficType("T");
                        dwdSptb02.setTransModeCode("2");
                    }
                    //('S') ='S'
                    if (vysfs.equals("S")) {
                        dwdSptb02.setTrafficType("S");
                        dwdSptb02.setTransModeCode("3");
                    }
                    //2.处理 起运时间
                    //公路取sptb02.dtvscfsj，其他取sptb02取DSJCFSJ(实际离长时间)的值，实际起运时间， 实际出发时间
                    if ((vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) && Objects.nonNull(sptb02.getDtvscfsj())) {
                        dwdSptb02.setShipmentTime(sptb02.getDtvscfsj());
                    }
                    if ((vysfs.equals("L1") || vysfs.equals("T") || vysfs.equals("S")) && Objects.nonNull(sptb02.getDsjcfsj())) {
                        dwdSptb02.setShipmentTime(sptb02.getDsjcfsj());
                    }
                }
                //3.处理 计划下达时间
                if (Objects.nonNull(sptb02.getDpzrq())) {
                    dwdSptb02.setPlanReleaseTime(sptb02.getDpzrq());
                }
                //4.处理 运单指派时间
                if (Objects.nonNull(sptb02.getDysszpsj())) {
                    dwdSptb02.setAssignTime(sptb02.getDysszpsj());
                }
                //5.处理 打点到货时间
                if (Objects.nonNull(sptb02.getDgpsdhsj())) {
                    dwdSptb02.setDotSiteTime(sptb02.getDgpsdhsj());
                }
                //6.处理 最终到货时间
                if (Objects.nonNull(sptb02.getDdhsj())) {
                    dwdSptb02.setFinalSiteTime(sptb02.getDdhsj());
                }
                //7.处理 运单生成时间
                if (Objects.nonNull(sptb02.getDdjrq())) {
                    dwdSptb02.setOrderCreateTime(sptb02.getDdhsj());
                }
                //8.处理 基地代码 适配 lc_spec_config
                String cqwh = sptb02.getCqwh();
                if (Objects.nonNull(cqwh)) {
                    /**
                     * 0431、 -> 1  长春基地
                     * 022、  -> 5  天津基地
                     * 027、
                     * 028、  -> 2  成都基地
                     * 0757   -> 3  佛山基地
                     */
                    if ("0431".equals(cqwh)) {
                        dwdSptb02.setBaseCode("1");
                    }
                    if ("022".equals(cqwh)) {
                        dwdSptb02.setBaseCode("5");
                    }
                    if ("028".equals(cqwh)) {
                        dwdSptb02.setBaseCode("2");
                    }
                    if ("0757".equals(cqwh)) {
                        dwdSptb02.setBaseCode("3");
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
                String czjgsdm = sptb02.getCzjgsdm();
                if (StringUtils.isNotEmpty(czjgsdm)) {
                    if ("1".equals(czjgsdm)) {
                        dwdSptb02.setHostComCode("1");
                    }
                    if ("17".equals(czjgsdm)) {
                        dwdSptb02.setHostComCode("2");
                    }
                    if ("29".equals(czjgsdm)) {
                        dwdSptb02.setHostComCode("3");
                    }
                }
                //对保存的数据为null的填充默认值
                //DwdSptb02 bean = JsonPartUtil.getBean(dwdSptb02);
                //实际保存的值为after里的值
                System.out.println("处理完的数据填充后的值:" + dwdSptb02.toString());
                log.info("处理完的数据填充后的值:" + dwdSptb02.toString());
                collector.collect(dwdSptb02.toString());
            }
        }).uid("dataDwdProcess").name("dataDwdProcess");

        dataDwdProcess.print("数据拉宽后输出:");
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWD_VLMS_SPTB02,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_SPTB02));
        //将处理完的数据保存到kafka
        dataDwdProcess.addSink(sinkKafka).uid("dwd-sink-kafka").name("dwd-sink-kafka");
        log.info("将处理完的数据保存到kafka中");
        //将处理完的数据保存到mysql中
        //dataDwdProcess.addSink(new DwdMysqlSink()).setParallelism(1).uid("dwd-sink-mysql").name("dwd-sink-mysql");
        log.info("将处理完的数据保存到mysql中");
        env.execute("sptb02-sink-kafka-dwd");
        log.info("sptb02dwd层job任务开始执行");
    }
}
