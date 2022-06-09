package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.*;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
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

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/6/9 20:00
 * @Version: V1.0
 */
@Slf4j
public class DimWarehouseRSWideApp {
    public static void main(String[] args) {
        //1.创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(600000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");


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
                .tableList("TDS_LJ.SITE_WAREHOUSE", "TDS_LJ.RFID_WAREHOUSE")
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("oracleSourceStream").name("oracleSourceStream");

        //2.1.过滤出SITE_WAREHOUSE表
        DataStream<String> filterSite = oracleSourceStream.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("SITE_WAREHOUSE")) {
                    SiteWarehouse after = jo.getObject("after", SiteWarehouse.class);
                    String vwlckdm = after.getVWLCKDM();
                    if (vwlckdm != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterSITE_WAREHOUSE").name("filterSITE_WAREHOUSE");
        //2.2过滤出RFID_WAREHOUSE表
        DataStream<String> filterRfid = oracleSourceStream.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("TDS_LJ") && jo.getString("tableName").equals("RFID_WAREHOUSE")) {
                    RfidWarehouse after = jo.getObject("after", RfidWarehouse.class);
                    Integer id=  after.getID();
                    if (id != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterRFID_WAREHOUSE").name("filterRFID_WAREHOUSE");

        //3.1 SITE_WAREHOUSE转实体类
        SingleOutputStreamOperator<SiteWarehouse> mapSite = filterSite.map(new MapFunction<String, SiteWarehouse>() {
            @Override
            public SiteWarehouse map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                SiteWarehouse dataSite = jsonObject.getObject("after", SiteWarehouse.class);
                Timestamp ts = jsonObject.getTimestamp("ts"); //取ts作为时间戳字段
                dataSite.setTs(ts);
                return dataSite;
            }
        }).uid("transitionSiteWarehouse").name("transitionSiteWarehouse");
        //3.2 RFID_WAREHOUSE转实体类
        SingleOutputStreamOperator<RfidWarehouse> mapRfid = filterRfid.map(new MapFunction<String, RfidWarehouse>() {
            @Override
            public RfidWarehouse map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                RfidWarehouse dataRfid = jsonObject.getObject("after", RfidWarehouse.class);
                Timestamp ts = jsonObject.getTimestamp("ts"); //取ts作为时间戳字段
                dataRfid.setTs(ts);
                return dataRfid;
            }
        }).uid("transitionRfidWarehouse").name("transitionRfidWarehouse");

        //4.指定事件时间字段
        //SITE_WAREHOUSE指定事件时间
        SingleOutputStreamOperator<SiteWarehouse> siteWithTs = mapSite.assignTimestampsAndWatermarks(
                WatermarkStrategy.<SiteWarehouse>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<SiteWarehouse>() {
                            @Override
                            public long extractTimestamp(SiteWarehouse siteWarehouse, long recordTimestamp) {
                                Timestamp ts = siteWarehouse.getTs();
                                return ts.getTime();
                            }
                        })
        ).uid("SiteWarehouseTsDS").name("SiteWarehouseTsDS");
        //RFID_WAREHOUSE指定事件时间
        SingleOutputStreamOperator<RfidWarehouse> RfidWithTs = mapRfid.assignTimestampsAndWatermarks(
                WatermarkStrategy.<RfidWarehouse>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner(new SerializableTimestampAssigner<RfidWarehouse>() {
                            @Override
                            public long extractTimestamp(RfidWarehouse siteWarehouse, long recordTimestamp) {
                                Timestamp ts = siteWarehouse.getTs();
                                return ts.getTime();
                            }
                        })
        ).uid("RfidWarehouseTsDS").name("RfidWarehouseTsDS");

        //5.分组指定关联key
        // SITE_WAREHOUSE,RFID_WAREHOUSE 按照wareHouseCode字段分组
        KeyedStream<SiteWarehouse, String> siteWarehouseStringKeyedStream = siteWithTs.keyBy(SiteWarehouse::getWAREHOUSE_CODE);
        KeyedStream<RfidWarehouse, String> rfidWarehouseStringKeyedStream = RfidWithTs.keyBy(RfidWarehouse::getWAREHOUSE_CODE);

        //6.进行表拓宽 俩表用wareHouseCode进行关联
        SingleOutputStreamOperator<DimWarehouseRS> rsWide = siteWarehouseStringKeyedStream
                .intervalJoin(rfidWarehouseStringKeyedStream)
                .between(Time.minutes(-10), Time.minutes(10))
                .process(new ProcessJoinFunction<SiteWarehouse, RfidWarehouse, DimWarehouseRS>() {
                    @Override
                    public void processElement(SiteWarehouse left, RfidWarehouse right, ProcessJoinFunction<SiteWarehouse, RfidWarehouse, DimWarehouseRS>.Context ctx, Collector<DimWarehouseRS> out) throws Exception {
                        out.collect(new DimWarehouseRS(right, left));
                    }
                }).uid("rsWide").name("rsWide");
        //7.开窗,按照时间窗口
        rsWide.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
        rsWide.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new AllWindowFunction<DimWarehouseRS, List<DimWarehouseRS>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<DimWarehouseRS> iterable, Collector<List<DimWarehouseRS>> collector) throws Exception {
                ArrayList<DimWarehouseRS> es = Lists.newArrayList(iterable);
                if (es.size() > 0) {
                    collector.collect(es);
                }
            }
        }).addSink(JdbcSink.<DimWarehouseRS>getBatchSink()).uid("sink-mysql").name("sink-mysql");


        try {
            env.execute("KafkaSinkMysql");
        } catch (Exception e) {
            log.error("stream invoke error", e);
        }
    }
}
