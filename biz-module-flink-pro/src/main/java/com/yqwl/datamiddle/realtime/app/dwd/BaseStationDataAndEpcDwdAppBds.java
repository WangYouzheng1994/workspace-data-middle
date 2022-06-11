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
public class BaseStationDataAndEpcDwdAppBds {
    //2021-06-01 00:00:00
    private static final long START = 1622476800000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;
    private static final String BASE_STATION_DATA = "BASE_STATION_DATA";
    private static final String BASE_STATION_DATA_EPC = "BASE_STATION_DATA_EPC";

    public static void main(String[] args) throws Exception {
        Configuration con = new Configuration();
        /**
         *  -Djobmanager.memory.process.size=768m \
         *         -Djobmanager.memory.off-heap.size=256m \
         *         -Djobmanager.memory.jvm-metaspace.size=256m \
         *         -Djobmanager.memory.jvm-overhead.fraction=0.1 \
         *         -Djobmanager.memory.jvm-overhead.min=32m \
         *         -Djobmanager.memory.jvm-overhead.max=128m \
         *         -Dtaskmanager.memory.process.size=1684m \
         *         -Dtaskmanager.memory.framework.heap.size=256m \
         *         -Dtaskmanager.memory.task.heap.size=768m \
         *         -Dtaskmanager.memory.managed.size=16m \
         *         -Dtaskmanager.memory.framework.off-heap.size=128m \
         *         -Dtaskmanager.memory.task.off-heap.size=128m \
         *         -Dtaskmanager.memory.network.min=32m \
         *         -Dtaskmanager.memory.network.max=128m \
         *         -Dtaskmanager.memory.network.fraction=0.1 \
         *         -Dtaskmanager.memory.jvm-metaspace.size=128m \
         *         -Dtaskmanager.memory.jvm-overhead.min=128m \
         *         -Dtaskmanager.memory.jvm-overhead.max=256m \
         *         -Dtaskmanager.memory.jvm-overhead.fraction=0.1 \
         *         -Dtaskmanager.numberOfTaskSlots=4 \
         */
        con.setString("jobmanager.memory.process.size", "768m");
        con.setString("jobmanager.memory.off-heap.size", "256m");
        con.setString("jobmanager.memory.jvm-metaspace.size", "256m");
        con.setString("jobmanager.memory.jvm-overhead.fraction", "0.1");
        con.setString("jobmanager.memory.jvm-overhead.min", "32m");
        con.setString("jobmanager.memory.jvm-overhead.max", "128m");
        con.setString("taskmanager.memory.process.size", "1684m");
        con.setString("taskmanager.memory.framework.heap.size", "256m");
        con.setString("taskmanager.memory.task.heap.size", "768m");
        con.setString("taskmanager.memory.managed.size", "16m");
        con.setString("taskmanager.memory.framework.off-heap.size", "128m");
        con.setString("taskmanager.memory.framework.heap.size", "128m");
        con.setString("taskmanager.memory.network.min", "32m");
        con.setString("taskmanager.memory.network.max", "128m");
        con.setString("taskmanager.memory.network.fraction", "0.1");
        con.setString("taskmanager.memory.jvm-metaspace.size", "128m");
        con.setString("taskmanager.memory.jvm-overhead.min", "128m");
        con.setString("taskmanager.memory.jvm-overhead.max", "256m");
        con.setString("taskmanager.memory.jvm-overhead.fraction", "0.1");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(con);

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
                .tableList("TDS_LJ.BASE_STATION_DATA")
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("oracleSourceStream").name("oracleSourceStream");

        //过滤 大于 2021-06-01 00:00:00的数据
      /*  SingleOutputStreamOperator<String> dataAndEpcFilter = oracleSourceStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String json) throws Exception {
                //要转换的时间格式
                JSONObject jsonObj = JSON.parseObject(json);
                JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);

                if (BASE_STATION_DATA.equals(tableNameStr)) {
                    //获取上报完成时间
                    String sample_u_t_c = afterObj.getString("SAMPLE_U_T_C");
                    if (StringUtils.isNotEmpty(sample_u_t_c)) {
                        long cutSampleTime = Long.parseLong(sample_u_t_c) / 1000;
                        if (cutSampleTime >= START && cutSampleTime <= END) {
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
        DataStream<String> filterBsdDs = dataAndEpcFilter.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                JSONObject jo = JSON.parseObject(s);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("BASE_STATION_DATA")) {
                    DwdBaseStationData after = jo.getObject("after", DwdBaseStationData.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterBASE_STATION_DATA").name("filterBASE_STATION_DATA");*/


        //3. 进行实体类的转换 I:添加kafka中ts字段作为当前时间戳 II.取cp中前4位数字翻译成基地名称作为基地的字段

        //BASE_STATION_DATA
        SingleOutputStreamOperator<DwdBaseStationData> mapBsd = oracleSourceStream.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String kafkaBsdValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdValue);
                DwdBaseStationData dataBsd = jsonObject.getObject("after", DwdBaseStationData.class);
                Timestamp ts = jsonObject.getTimestamp("ts");
                dataBsd.setTs(ts);
                return dataBsd;
            }
        }).uid("transitionBASE_STATION_DATA").name("transitionBASE_STATION_DATA");

        // 4.指定事件时间字段

        //DwdBaseStationData 指定事件时间
        SingleOutputStreamOperator<DwdBaseStationData> dwdBaseStationDataWithTS = mapBsd.assignTimestampsAndWatermarks(
                WatermarkStrategy.<DwdBaseStationData>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<DwdBaseStationData>() {
                            @Override
                            public long extractTimestamp(DwdBaseStationData dwdBaseStationData, long l) {
                                Timestamp ts = dwdBaseStationData.getTs();
                                return ts.getTime();
                            }
                        })).uid("assIgnDwdBaseStationDataEventTime").name("assIgnDwdBaseStationDataEpcEventTime");



        //6.1处理字段 base_station_data 和rfid_warehouse关联添加入库仓库的字段
        // provincesWideWithSysc09.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        SingleOutputStreamOperator<DwdBaseStationData> outSingleOutputStreamOperator = AsyncDataStream.unorderedWait(
                dwdBaseStationDataWithTS,
                new DimAsyncFunction<DwdBaseStationData>(DimUtil.MYSQL_DB_TYPE, "ods_vlms_rfid_warehouse", "WAREHOUSE_CODE") {
                    @Override
                    public Object getKey(DwdBaseStationData dwdBsd) {
                        if (StringUtils.isNotEmpty(dwdBsd.getSHOP_NO())) {
                            String shop_no = dwdBsd.getSHOP_NO();
                            return shop_no;
                        }
                        return null;
                    }

                    @Override
                    public void join(DwdBaseStationData dBsd, JSONObject dimInfoJsonObj) {
                        if (dimInfoJsonObj.getString("WAREHOUSE_CODE") != null) {
                            dBsd.setIN_WAREHOUSE_CODE(dimInfoJsonObj.getString("WAREHOUSE_CODE"));
                            dBsd.setIN_WAREHOUSE_NAME(dimInfoJsonObj.getString("WAREHOUSE_NAME"));
                        }
                    }
                }, 60, TimeUnit.SECONDS).uid("base+rfid");

        //7.开窗,按照时间窗口存储到mysql
        //BASE_STATION_DATA
        outSingleOutputStreamOperator.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        outSingleOutputStreamOperator.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<DwdBaseStationData, List<DwdBaseStationData>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<DwdBaseStationData> iterable, Collector<List<DwdBaseStationData>> collector) throws Exception {
                ArrayList<DwdBaseStationData> es = Lists.newArrayList(iterable);
                log.info("插入sql");
                if (es.size() > 0) {
                    collector.collect(es);
                }
            }
        }).addSink(JdbcSink.<DwdBaseStationData>getBatchSink()).uid("sink-mysqDsb").name("sink-mysqldsb");



        try {
            env.execute("OracleSinkMysql");
        } catch (Exception e) {
            log.error("stream invoke error", e);
        }
    }


}