package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.BackStationUtilCp9;
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

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: <一单到底>Oracle: base_station_data与base_station_data_epc存到Mysql表
 * @Author: XiaoFeng
 * @Date: 2022/6/02 10:30
 * @Version: V1.2
 */
@Slf4j
public class BaseStationDataAndEpcDwdAppEpc {
    public static void main(String[] args) throws Exception {
        // 开始流式模式
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        // 设置并行度为1
        env.setParallelism(1);
        log.info("初始化流处理环境完成");

        //====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("base_station_data_epc_dwd_app_epc"));
        // 设置savepoint点二级目录位置
        //env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("base_station_data_epc_dwd_app_epc"));
        log.info("checkpoint设置完成");

        Props props = PropertiesUtil.getProps();
        // oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        // properties.put("database.serverTimezone", "UTC");
        // properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=YES)(FAILOVER=YES)(ADDRESS=(PROTOCOL=tcp)(HOST=" + props.getStr("cdc.oracle.hostname") + ")(PORT=1521)))(CONNECT_DATA=(SID=" + props.getStr("cdc.oracle.database") + ")))");

        // kafka消费源相关参数配置
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_LATEST_0701)
                .setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_LATEST_0701_GROUP)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> oracleSourceStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "BaseStationDataAndEpcDwdAppEpcMySQL-Source").uid("BaseStationDataAndEpcDwdAppEpcOracleSourceStream").name("BaseStationDataAndEpcDwdAppEpcOracleSourceStream");
        SingleOutputStreamOperator<DwdBaseStationDataEpc> epcProcess = oracleSourceStream.process(new ProcessFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public void processElement(String value, Context ctx, Collector<DwdBaseStationDataEpc> out) throws Exception {
                JSONObject jsonObject = JSON.parseObject(value);
                String after = jsonObject.getString("after");
                DwdBaseStationDataEpc dataBsdEpc = JSON.parseObject(after, DwdBaseStationDataEpc.class);
                //车架号和操作时间不能为空
                if (StringUtils.isNotBlank(dataBsdEpc.getVIN()) && dataBsdEpc.getOPERATETIME() > 0) {
                    /**
                     * 得到cp,取前四位,转基地名称:
                     *                        '0431',
                     *                        '长春基地',
                     *                        '0757',
                     *                        '佛山基地',
                     *                        '0532',
                     *                        '青岛基地',
                     *                        '028C',
                     *                        '成都基地',
                     *                        '022C',
                     *                        '天津基地',
                     */
                    //先判是否为空为null为空格为空串 ps:本来是赋值"青岛基地",现在雨落要求我改成"青岛"
                    String cp = dataBsdEpc.getCP();
                    if (StringUtils.isNotBlank(cp) && cp.length() >= 4) {
                        String baseCode = cp.substring(0, 4);
                        if (StringUtils.equals(baseCode, "0431")) {
                            dataBsdEpc.setBASE_NAME("长春");
                            dataBsdEpc.setBASE_CODE("0431");
                        } else if (StringUtils.equals(baseCode, "0757")) {
                            dataBsdEpc.setBASE_NAME("佛山");
                            dataBsdEpc.setBASE_CODE("0757");
                        } else if (StringUtils.equals(baseCode, "0532")) {
                            dataBsdEpc.setBASE_NAME("青岛");
                            dataBsdEpc.setBASE_CODE("0532");
                        } else if (StringUtils.equals(baseCode, "028C")) {
                            dataBsdEpc.setBASE_NAME("成都");
                            dataBsdEpc.setBASE_CODE("028C");
                        } else if (StringUtils.equals(baseCode, "022C")) {
                            dataBsdEpc.setBASE_NAME("天津");
                            dataBsdEpc.setBASE_CODE("022C");
                        }
                    }

                    out.collect(dataBsdEpc);
                }

            }
        }).uid("BaseStationDataAndEpcDwdAppEpcepcProcess").name("BaseStationDataAndEpcDwdAppEpcepcProcess");

        // 5.分组指定关联key,base_station_data_epc 处理CP9下线接车日期
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapEpc = epcProcess.keyBy(DwdBaseStationDataEpc::getVIN).map(new BackStationUtilCp9())
                .uid("BaseStationDataAndEpcDwdAppUpdateCp9WithStationBackends").name("BaseStationDataAndEpcDwdAppUpdateCp9WithStationBackends");

        //===================================sink kafka=======================================================//
        SingleOutputStreamOperator<String> mapEpcJson = mapEpc.map(new MapFunction<DwdBaseStationDataEpc, String>() {
            @Override
            public String map(DwdBaseStationDataEpc value) throws Exception {
                return JSON.toJSONString(value);
            }
        }).uid("BaseStationDataAndEpcDwdAppEpcmapEpcJson").name("BaseStationDataAndEpcDwdAppEpcmapEpcJson");

        // 获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC));
        mapEpcJson.addSink(sinkKafka).uid("BaseStationDataAndEpcDwdAppEpcsinkKafkaDwdEpc").name("BaseStationDataAndEpcDwdAppEpcsinkKafkaDwdEpc");

        //===================================sink mysql=======================================================//
        // 组装sql
        String sql = MysqlUtil.getSql(DwdBaseStationDataEpc.class);
        mapEpc.addSink(JdbcSink.<DwdBaseStationDataEpc>getSink(sql)).setParallelism(1).uid("BaseStationDataAndEpcDwdAppEpcOracle-cdc-mysql").name("BaseStationDataAndEpcDwdAppEpcOracle-cdc-mysql");
        env.execute("拉宽bsdEpc表进入dwdBsdEpc");
    }


}
