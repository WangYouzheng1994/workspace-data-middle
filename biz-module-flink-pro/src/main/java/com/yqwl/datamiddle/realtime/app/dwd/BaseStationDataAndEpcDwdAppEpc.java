package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
    //2021-06-01 00:00:00
    private static final long START = 1622476800000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;
    private static final String BASE_STATION_DATA = "BASE_STATION_DATA";
    private static final String BASE_STATION_DATA_EPC = "BASE_STATION_DATA_EPC";

    public static void main(String[] args) throws Exception {
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

        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        //properties.put("database.serverTimezone", "UTC");
        //properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=YES)(FAILOVER=YES)(ADDRESS=(PROTOCOL=tcp)(HOST=" + props.getStr("cdc.oracle.hostname") + ")(PORT=1521)))(CONNECT_DATA=(SID=" + props.getStr("cdc.oracle.database") + ")))");

        //读取oracle连接配置属性
        SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("cdc.oracle.hostname"))
                .port(props.getInt("cdc.oracle.port"))
                .database(props.getStr("cdc.oracle.database"))
                .schemaList(StrUtil.getStrList(props.getStr("cdc.oracle.schema.list"), ","))
                .tableList("TDS_LJ.BASE_STATION_DATA_EPC")
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("oracleSourceStream").name("oracleSourceStream");

        //过滤 大于 2021-06-01 00:00:00的数据
        SingleOutputStreamOperator<String> dataAndEpcFilter = oracleSourceStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String json) throws Exception {
                //要转换的时间格式
                JSONObject jsonObj = JSON.parseObject(json);
                JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);



                if (BASE_STATION_DATA_EPC.equals(tableNameStr)) {
                    //获取上报完成时间
                    String operateTime = afterObj.getString("OPERATETIME");
                    if (StringUtils.isNotEmpty(operateTime)) {
                        long operateTimeLong = Long.parseLong(operateTime);
                        if (operateTimeLong >= START && operateTimeLong <= END) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            }
        }).uid("dataAndEpcFilter").name("dataAndEpcFilter");


        //2.进行数据过滤


        // 过滤出BASE_STATION_DATA的表
        DataStream<String> filterBsdEpcDs = dataAndEpcFilter.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("BASE_STATION_DATA_EPC")) {
                    DwdBaseStationDataEpc after = jo.getObject("after", DwdBaseStationDataEpc.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterBASE_STATION_DATA_EPC").name("filterBASE_STATION_DATA_EPC");

        //3. 进行实体类的转换 I:添加kafka中ts字段作为当前时间戳 II.取cp中前4位数字翻译成基地名称作为基地的字段
        //BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = filterBsdEpcDs.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                DwdBaseStationDataEpc dataBsdEpc = jsonObject.getObject("after", DwdBaseStationDataEpc.class);
                Timestamp ts = jsonObject.getTimestamp("ts"); //取ts作为时间戳字段
                dataBsdEpc.setTs(ts);
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
                return dataBsdEpc;
            }
        }).uid("transitionBASE_STATION_DATA_EPCMap").name("transitionBASE_STATION_DATA_EPCMap");
        mapBsdEpc.print("合并基地名称字段后:");


        // 4.指定事件时间字段
        //DwdBaseStationDataEpc指定事件时间
        SingleOutputStreamOperator<DwdBaseStationDataEpc> dwdBaseStationDataEpcWithTS = mapBsdEpc.assignTimestampsAndWatermarks(
                WatermarkStrategy.<DwdBaseStationDataEpc>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<DwdBaseStationDataEpc>() {
                            @Override
                            public long extractTimestamp(DwdBaseStationDataEpc dwdBaseStationDataEpc, long l) {
                                Timestamp ts = dwdBaseStationDataEpc.getTs();
                                return ts.getTime();
                            }
                        })).uid("assIgnDwdBaseStationDataEpcEventTime").name("assIgnDwdBaseStationDataEpcEventTime");


        //5.分组指定关联key,base_station_data_epc 处理CP9下线接车日期
        SingleOutputStreamOperator<DwdBaseStationDataEpc> map = dwdBaseStationDataEpcWithTS.keyBy(DwdBaseStationDataEpc::getVIN).map(new CP9Station());




        //BASE_STATION_DATA_EPC
        map.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        map.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<DwdBaseStationDataEpc, List<DwdBaseStationDataEpc>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<DwdBaseStationDataEpc> iterable, Collector<List<DwdBaseStationDataEpc>> collector) throws Exception {
                ArrayList<DwdBaseStationDataEpc> es = Lists.newArrayList(iterable);
                if (es.size() > 0) {
                    collector.collect(es);
                }
            }
        }).addSink(JdbcSink.<DwdBaseStationDataEpc>getBatchSink()).uid("sink-mysqdsbEpc").name("sink-mysqdsbepc");

        try {
            env.execute("OracleSinkMysql");
        } catch (Exception e) {
            log.error("stream invoke error", e);
        }
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