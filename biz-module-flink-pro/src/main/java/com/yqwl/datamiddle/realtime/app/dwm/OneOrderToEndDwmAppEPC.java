package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.OotdTransition;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.DimUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 一单到底
 * @Author: muqing&XiaoFeng
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmAppEPC {

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
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

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //读取mysql binlog
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

        //==============================================dwd_base_station_data_epc处理 START====================================================================//
        //2.过滤出BASE_STATION_DATA_Epc的表
        DataStream<String> filterBsdEpcDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_flink") && jo.getString("tableName").equals("dwd_vlms_base_station_data_epc")) {
                    DwdBaseStationDataEpc after = jo.getObject("after", DwdBaseStationDataEpc.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterDwd_vlms_base_station_data_epc").name("filterDwd_vlms_base_station_data_epc");

        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = filterBsdEpcDs.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                DwdBaseStationDataEpc dataBsdEpc = jsonObject.getObject("after", DwdBaseStationDataEpc.class);
                Timestamp ts = jsonObject.getTimestamp("ts"); //取ts作为时间戳字段
                dataBsdEpc.setTs(ts);
                String vin = dataBsdEpc.getVIN();
                return dataBsdEpc;
            }
        }).uid("transitionBASE_STATION_DATA_EPCMap").name("transitionBASE_STATION_DATA_EPCMap");
        //4.插入mysql
        mapBsdEpc.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, CP9_OFFLINE_TIME, BASE_NAME, BASE_CODE )\n" +
                        "VALUES\n" +
                        "        ( ?, ?, ? ,?) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "   CP9_OFFLINE_TIME=? ,BASE_NAME=?,\n" +
                        "        BASE_CODE=?",
                (ps, epc) -> {
                    ps.setString(1, epc.getVIN());
                    ps.setLong(2, epc.getCP9_OFFLINE_TIME());
                    ps.setString(3, epc.getBASE_NAME());
                    ps.setString(4, epc.getBASE_CODE());
                    ps.setLong(5, epc.getCP9_OFFLINE_TIME());
                    ps.setString(6, epc.getBASE_NAME());
                    ps.setString(7, epc.getBASE_CODE());
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(2)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(MysqlConfig.URL)
                        .withDriverName(MysqlConfig.DRIVER)
                        .withUsername(MysqlConfig.USERNAME)
                        .withPassword(MysqlConfig.PASSWORD)
                        .build())).uid("baseStationDataEpcSink").name("baseStationDataEpcSink");
        //==============================================dwd_base_station_data_epc处理 END====================================================================//



        env.execute("一单到底合表开始");
        log.info("base_station_data job任务开始执行");

    }
}
