package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.DimAsyncFunction;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.bean.DwmOneOrderToEnd;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 对运单 sptb02 表进行统一数据格式 字段统一等
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmAppOOTD {

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        /*CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);*/
        System.setProperty("HADOOP_USER_NAME", "root");
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
        //2.过滤出一单到底的表
        DataStream<String> filterBsdEpcDs = mysqlSource.filter(new RichFilterFunction<String>() {
            @Override
            public boolean filter(String mysqlDataStream) throws Exception {
                JSONObject jo = JSON.parseObject(mysqlDataStream);
                if (jo.getString("database").equals("data_middle_flink") && jo.getString("tableName").equals("dwm_vlms_one_order_to_end")) {
                    DwdBaseStationDataEpc after = jo.getObject("after", DwdBaseStationDataEpc.class);
                    String vin = after.getVIN();
                    if (vin != null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }).uid("filterDwmOneOrderToEnd").name("filterDwmOneOrderToEnd");

        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwmOneOrderToEnd> mapBsdEpc = filterBsdEpcDs.map(new MapFunction<String, DwmOneOrderToEnd>() {
            @Override
            public DwmOneOrderToEnd map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                return jsonObject.getObject("after", DwmOneOrderToEnd.class);
            }
        }).uid("transitionDwmOneOrderToEnd").name("transitionDwmOneOrderToEnd");
        //====================================sink clickhouse===============================================//
//        组装sql
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(KafkaTopicConst.DwmOneOrderToEnd).append(" values ").append(StrUtil.getValueSql(DwmOneOrderToEnd.class));
        log.info("组装clickhouse插入sql:{}", sql);
        mapBsdEpc.addSink(ClickHouseUtil.<DwmOneOrderToEnd>getSink(sql.toString())).setParallelism(1).uid("sink-clickhouse").name("sink-clickhouse");
        mapBsdEpc.print("拉宽后数据输出：");


        //====================================sink mysql===============================================//

        log.info("将处理完的数据保存到clickhouse中");
        env.execute("sptb02-DwmOneOrderToEnd");
        log.info("DwmOneOrderToEnd层job任务开始执行");
    }
}
