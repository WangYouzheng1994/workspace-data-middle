package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.SimpleFixDwmSptb02DataSink;
import com.yqwl.datamiddle.realtime.bean.*;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 修数的任务
 *     // 有乱码的字段 :
 *     // TRANSPORT_VEHICLE_NO  运输车号
 *     // START_CITY_NAME       始发城市
 *     // END_CITY_NAME         目的城市
 * @Author: XiaoFeng
 * @Date: 2022/7/5 11:56
 * @Version: V1.0
 */
@Slf4j
public class FixDwmSptb02 {
    //2021-06-01 00:00:00
    private static final long START = 1622476800000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        env.setParallelism(1);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");

        Properties properties = new Properties();
        // 遇到错误跳过
        properties.setProperty("debezium.inconsistent.schema.handing.mode","warn");
        properties.setProperty("debezium.event.deserialization.failure.handling.mode","warn");

        //mysql消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.ods_vlms_sptb02")
                //.tableList("data_middle_flink.ods_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .debeziumProperties(properties)
                .distributionFactorUpper(10.0d)   //针对cdc的错误算法的更改
                .build();

        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSourceStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "FixDwmSptb02MysqlSource").uid("FixDwmSptb02MysqlSourceStream").name("FixDwmSptb02SPTB02MysqlSourceStream");
        //2. 按时间来过滤
        SingleOutputStreamOperator<FixDwmsptb02Bean> oneOrderToEndUpdateProcess = mysqlSourceStream.process(new ProcessFunction<String, FixDwmsptb02Bean>() {
            @Override
            public void processElement(String value, Context ctx, Collector<FixDwmsptb02Bean> out) throws Exception {

                Sptb02 sptb02 = JsonPartUtil.getAfterObj(value, Sptb02.class);
                Long ddjrq = sptb02.getDDJRQ();
                if (Objects.nonNull(ddjrq) && ddjrq > 0) {
                    // 筛选6月1号以后的数据
                    if (ddjrq >= START && ddjrq <= END) {
                        FixDwmsptb02Bean fixDwmsptb02 = new FixDwmsptb02Bean();
                        // 结算单编号
                        String cjsdbh = sptb02.getCJSDBH();
                        // 车牌号 (字段正确)
                        String vjsydm = sptb02.getVJSYDM();

                        //赋值
                        fixDwmsptb02.setVJSYDM(vjsydm);
                        fixDwmsptb02.setCJSDBH(cjsdbh);

                        if (StringUtils.isNotBlank(cjsdbh)){
                            String sptb02Sql = "select START_PROVINCE_CODE, START_CITY_CODE, END_PROVINCE_CODE, END_CITY_CODE from " + KafkaTopicConst.DWD_VLMS_SPTB02 + " where CJSDBH = '" + cjsdbh + "' limit 1 ";
                            JSONObject sptb02Json = MysqlUtil.querySingle(KafkaTopicConst.DWD_VLMS_SPTB02, sptb02Sql, cjsdbh);
                            if (sptb02Json != null) {
                                // 起运地省区代码
                                String start_province_code = sptb02Json.getString("START_PROVINCE_CODE");
                                // 起运地县区代码
                                String start_city_code     = sptb02Json.getString("START_CITY_CODE");
                                // 到货地省区代码
                                String end_province_code   = sptb02Json.getString("END_PROVINCE_CODE");
                                // 到货地县区代码
                                String end_city_code       = sptb02Json.getString("END_CITY_CODE");

                                if ( StringUtils.isNotBlank(start_province_code) && StringUtils.isNotBlank(start_city_code)){
                                    String provinceSql = "select vsqmc, vsxmc from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + start_province_code + "' and csxdm = '" + start_city_code + "' limit 1 ";
                                    JSONObject startProvince = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, start_province_code, start_city_code);
                                    if ( startProvince != null) {
                                        //省区名称：山东省
                                        fixDwmsptb02.setSTART_PROVINCE_CODE(startProvince.getString("vsqmc"));
                                        //市县名称: 齐河
                                        fixDwmsptb02.setSTART_CITY_NAME(startProvince.getString("vsxmc"));
                                    }

                                    fixDwmsptb02.setSTART_PROVINCE_CODE(start_province_code);
                                    fixDwmsptb02.setSTART_CITY_CODE(start_city_code);
                                }
                                if ( StringUtils.isNotBlank(end_province_code) && StringUtils.isNotBlank(end_city_code)){
                                    String provinceSql = "select vsqmc, vsxmc from " + KafkaTopicConst.DIM_VLMS_PROVINCES + " where csqdm = '" + end_province_code + "' and csxdm = '" + end_city_code + "' limit 1 ";
                                    JSONObject endProvince = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_PROVINCES, provinceSql, end_province_code, end_city_code);
                                    if (endProvince != null) {
                                        //例如 省区名称：山东省
                                        fixDwmsptb02.setEND_PROVINCE_CODE(endProvince.getString("vsqmc"));
                                        //例如 市县名称: 齐河
                                        fixDwmsptb02.setEND_CITY_NAME(endProvince.getString("vsxmc"));
                                    }
                                    fixDwmsptb02.setEND_PROVINCE_CODE(end_province_code);
                                    fixDwmsptb02.setEND_CITY_CODE(end_city_code);
                                }
                            }
                        }
                        out.collect(fixDwmsptb02);
                    }
                }
            }
        }).uid("FixDwmSptb02UpdateProcess").name("FixDwmSptb02UpdateProcess");

    oneOrderToEndUpdateProcess.addSink(new SimpleFixDwmSptb02DataSink<>()).uid("FixDwmSptb02DataSink").name("FixDwmSptb02DataSink");
    env.execute("给一单到底乱码修数");
    }
}
