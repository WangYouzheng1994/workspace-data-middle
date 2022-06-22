package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.time.Duration;
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
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
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

        Props props = PropertiesUtil.getProps();
        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        //properties.put("database.serverTimezone", "UTC");
        //properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=YES)(FAILOVER=YES)(ADDRESS=(PROTOCOL=tcp)(HOST=" + props.getStr("cdc.oracle.hostname") + ")(PORT=1521)))(CONNECT_DATA=(SID=" + props.getStr("cdc.oracle.database") + ")))");

        //kafka消费源相关参数配置
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC)
                .setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> oracleSourceStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "MySQL-Source").uid("oracleSourceStream").name("oracleSourceStream");


        SingleOutputStreamOperator<DwdBaseStationDataEpc> epcProcess = oracleSourceStream.process(new ProcessFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public void processElement(String value, Context ctx, Collector<DwdBaseStationDataEpc> out) throws Exception {
                DwdBaseStationDataEpc dataBsdEpc = JSON.parseObject(value, DwdBaseStationDataEpc.class);
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
        }).uid("epcProcess").name("epcProcess");


        epcProcess.print("消费数据:");

        // 4.指定事件时间字段
        //DwdBaseStationDataEpc指定事件时间
      /*  SingleOutputStreamOperator<DwdBaseStationDataEpc> dwdBaseStationDataEpcWithTS = epcProcess.assignTimestampsAndWatermarks(
                WatermarkStrategy.<DwdBaseStationDataEpc>forBoundedOutOfOrderness(Duration.ofSeconds(5)))
                        .withTimestampAssigner(new SerializableTimestampAssigner<DwdBaseStationDataEpc>() {
                            @Override
                            public long extractTimestamp(DwdBaseStationDataEpc dwdBaseStationDataEpc, long l) {
                                return dwdBaseStationDataEpc.getWAREHOUSE_CREATETIME();
                            }
                        })).uid("assIgnDwdBaseStationDataEpcEventTime").name("assIgnDwdBaseStationDataEpcEventTime");
*/

        //5.分组指定关联key,base_station_data_epc 处理CP9下线接车日期
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapEpc = epcProcess.keyBy(DwdBaseStationDataEpc::getVIN).map(new CP9Station())
                .uid("mapEpc").name("mapEpc");

        //===================================sink kafka=======================================================//
        SingleOutputStreamOperator<String> mapEpcJson = mapEpc.map(new MapFunction<DwdBaseStationDataEpc, String>() {
            @Override
            public String map(DwdBaseStationDataEpc value) throws Exception {
                return JSON.toJSONString(value);
            }
        }).uid("mapEpcJson").name("mapEpcJson");
        mapEpcJson.print("消费数据:");
        //获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                KafkaUtil.KAFKA_SERVER,
                KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC));

        mapEpcJson.addSink(sinkKafka).uid("sinkKafka").name("sinkKafka");

        //===================================sink mysql=======================================================//
        //组装sql
        String sql = MysqlUtil.getSql(DwdBaseStationDataEpc.class);
        log.info("组装的插入sql:{}", sql);
        mapEpc.addSink(JdbcSink.<DwdBaseStationDataEpc>getSink(sql)).setParallelism(1).uid("oracle-cdc-mysql").name("oracle-cdc-mysql");

        env.execute("拉宽bsdEpc表进入dwdBsdEpc");

    }

    /**
     * 状态后端
     */
    public static class CP9Station extends RichMapFunction<DwdBaseStationDataEpc, DwdBaseStationDataEpc> {
        // 声明Map类型的状态后端
        private transient MapState<String, Long> myMapState;

        @Override
        public void open(Configuration parameters) throws Exception {
            MapStateDescriptor<String, Long> mapStateDescriptor = new MapStateDescriptor<>("vin码和操作时间", String.class, Long.class);
            myMapState = getRuntimeContext().getMapState(mapStateDescriptor);
        }

        @Override
        public DwdBaseStationDataEpc map(DwdBaseStationDataEpc dwdBaseStationDataEpc) throws Exception {
            /**
             * 1.当前状态后端的状态为Map类型,key为String,也就是汽车的Vin码,value为vin码所对应的数据,vin所对应的操作时间
             * 2.每次流到了到了这里,就会调用这里的map:
             */
            String vin = dwdBaseStationDataEpc.getVIN();                    //车架号
            Long nowOperatetime = dwdBaseStationDataEpc.getOPERATETIME();   //操作时间
            // 1):判断状态后端有无当前数据的vin码的key所对应的对象,没有就添加上
            if (myMapState.get(vin) == null) {
                myMapState.put(vin, nowOperatetime);
                dwdBaseStationDataEpc.setCP9_OFFLINE_TIME(nowOperatetime);
                return dwdBaseStationDataEpc;
            } else {
                // 2):当前'状态后端'有vin码对应的value就会判断操作时间,
                //    如果'状态后端'已有的操作时间大于'当前流数据'的操作时间则删除'状态后端'中的key(因为取的是第一条下线时间的数据).
                //    然后再把更早的下线时间存到'状态后端'中.
                Long oldOperatetime = myMapState.get(vin);
//                Long oldOperatetime = oldDataEpc.getOPERATETIME();
                if (oldOperatetime > nowOperatetime) {
                    myMapState.remove(vin);
                    myMapState.put(vin, nowOperatetime);
                    dwdBaseStationDataEpc.setCP9_OFFLINE_TIME(nowOperatetime);
                    return dwdBaseStationDataEpc;
                } else {
                    // 3):如果'状态后端'已有的操作时间小于当前流的操作时间,就会保留当前状态后端的操作时间,且设置为DwdBaseStationDataEpc的第一次下线时间.
                    dwdBaseStationDataEpc.setCP9_OFFLINE_TIME(oldOperatetime);
                    return dwdBaseStationDataEpc;
                }
            }
        }
    }
}
