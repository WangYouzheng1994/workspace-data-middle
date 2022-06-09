package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.bean.DwmOneOrderToEnd;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import com.yqwl.datamiddle.realtime.vo.DwmSptb02Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Description: 一单到底
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OneOrderToEndDwmApp1 {

    private static final String SPTB02_TABLE_NAME = "dwm_vlms_sptb02";


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

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("cdc.mysql.table.list"), ","))
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();

        //env.addSource(mySqlSource).uid("oracleSourceStream").name("oracleSourceStream");
        // 将kafka中源数据转化成DataStream
        //SingleOutputStreamOperator<String> bsdDs = env.fromSource(bsdSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-bsd").uid("bsdDs").name("bsdDs");
        // SingleOutputStreamOperator<String> bsdEpcDs = env.fromSource(bsdEpcSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-epc").uid("bsdEpcDs").name("bsdEpcDs");

        SingleOutputStreamOperator<String> cdcMysqlStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL-Source").uid("cdc-mysql").name("cdc-mysql");


        SingleOutputStreamOperator<String> dwmSptb02Filter = cdcMysqlStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String json) throws Exception {
                JSONObject jsonObj = JSON.parseObject(json);
                String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);
                if (SPTB02_TABLE_NAME.equals(tableNameStr)) {
                    return true;
                }
                return false;
            }
        }).uid("dwmSptb02Filter").name("dwmSptb02Filter");

        SingleOutputStreamOperator<DwmSptb02> dwmSptb02Map = dwmSptb02Filter.map(new MapFunction<String, DwmSptb02>() {
            @Override
            public DwmSptb02 map(String json) throws Exception {

                return JsonPartUtil.getAfterObj(json, DwmSptb02.class);
            }
        }).uid("dwmSptb02Map").name("dwmSptb02Map");





        SingleOutputStreamOperator<DwmSptb02Vo> dwmSptb02Process = dwmSptb02Map.process(new ProcessFunction<DwmSptb02, DwmSptb02Vo>() {
            @Override
            public void processElement(DwmSptb02 dwmSptb02, Context ctx, Collector<DwmSptb02Vo> out) throws Exception {
                //获取运输方式
                DwmSptb02Vo dwmSptb02Vo = new DwmSptb02Vo();
                String vysfs = dwmSptb02.getVYSFS();
                if (StringUtils.isNotBlank(vysfs)) {
                    //铁路
                    if ("T".equals(vysfs) || "L1".equals(vysfs)) {
                        dwmSptb02Vo.setSTART_PLATFORM_NAME(dwmSptb02.getVFCZT());
                        dwmSptb02Vo.setEND_PLATFORM_NAME(dwmSptb02.getVSCZT());
                    }
                }



















            }
        }).uid("dwmSptb02Process").name("dwmSptb02Process");












        env.execute("合表开始");
        log.info("base_station_data job任务开始执行");

    }
}
