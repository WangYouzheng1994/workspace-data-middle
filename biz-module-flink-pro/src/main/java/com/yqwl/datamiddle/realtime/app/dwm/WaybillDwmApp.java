package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.DwdMysqlSink;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.OrderDetailWide;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
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
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
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
                .setTopics(KafkaTopicConst.DWD_VLMS_SPTB02)//消费 dwd 层 sptb02表  dwd_vlms_sptb02
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //将kafka中源数据转化成DataStream
        DataStreamSource<String> jsonDataStr = env.fromSource(kafkaSourceBuild, WatermarkStrategy.noWatermarks(), "kafka-consumer");
        //从Kafka主题中获取消费端
        log.info("从kafka的主题:" + KafkaTopicConst.ODS_VLMS_SPTB02 + "中获取的要处理的数据");

        //将kafka中原始json数据转化成实例对象
        SingleOutputStreamOperator<DwmSptb02> objStream = jsonDataStr.map(new MapFunction<String, DwmSptb02>() {
            @Override
            public DwmSptb02 map(String json) throws Exception {
                //after里的值转换成实例对象
                return JSON.parseObject(json, DwmSptb02.class);
            }
        }).uid("objStream").name("objStream");
        log.info("将kafka中after里数据转化成实例对象");

        //关联ods_vlms_sptb02d1 获取车架号 VVIN 从mysql中获取
        SingleOutputStreamOperator<DwmSptb02> sptb02d1DS = AsyncDataStream.unorderedWait(
                objStream,
                new DimAsyncFunction<DwmSptb02>(
                        DimUtil.MYSQL_DB_TYPE,
                        KafkaTopicConst.ODS_VLMS_SPTB02D1,
                        "CJSDBH") {

                    @Override
                    public Object getKey(DwmSptb02 dwmSptb02) {
                        return dwmSptb02.getCjsdbh();
                    }

                    @Override
                    public void join(DwmSptb02 dwmSptb02, JSONObject dimInfoJsonObj) throws Exception {
                        //将维度中 用户表中username 设置给订单宽表中的属性
                        String vvin = dimInfoJsonObj.getString("VVIN");
                        dwmSptb02.setVvin(vvin);
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
                        return Arrays.asList(dwmSptb02.getHostComCode(), dwmSptb02.getBaseCode(), dwmSptb02.getTransModeCode());
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
                            outSiteTime = dwmSptb02.getDpzrq();
                        }
                        if ("DCKRQ".equals(nodeCode) || "SYRKSJ".equals(nodeCode)) {
                            outSiteTime = dwmSptb02.getDckrq();
                        }
                        // 水运 是否是根据SPTB02_SEA_RK.DRKRQ  溯源系统入港扫描时间
                        if (outSiteTime != null) {
                            Date outSiteDate = new Date(outSiteTime);
                            //格式化时间
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formatDate = formatter.format(outSiteDate);
                            DateTime parse = DateUtil.parse(formatDate);
                            DateTime newStepTime = DateUtil.offsetHour(parse, Integer.parseInt(hours));
                            dwmSptb02.setTheoryShipmentTime(newStepTime.getTime());
                        }

                    }
                },
                60, TimeUnit.SECONDS).uid("theoryShipmentTimeDS").name("theoryShipmentTimeDS");


        //组装sql
        String sql = "insert into dwm_vlms_sptb02 values " + StrUtil.getValueSql(DwmSptb02.class);
        //宽表数据写入clickhouse
        theoryShipmentTimeDS.addSink(ClickHouseUtil.<DwmSptb02>getSink(sql))
                .uid("sinkClickhouse").name("sinkClickhouse");
        //将对象数据转为json
        //SingleOutputStreamOperator<String> resultDS = theoryShipmentTimeDS.map(DwmSptb02::toString);
        //将处理完的数据保存到kafka
        //resultDS.addSink(new DwdMysqlSink());
        log.info("将处理完的数据保存到kafka中");
        //将处理完的数据保存到mysql中
        log.info("将处理完的数据保存到mysql中");
        env.execute("sptb02-sink-clickhouse-dwm");
        log.info("sptb02dwd层job任务开始执行");
    }
}
