package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 将oracle中其中相关的源表数据cdc同步到kafka的同一个topic中, 名称为:cdc_vlms_unite_oracle
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OracleCDCKafkaApp {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //flink程序重启，每次之间间隔10s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(2);

        log.info("stream流环境初始化完成");
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        properties.put("database.serverTimezone", "UTC");
        //properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=YES)(FAILOVER=YES)(ADDRESS=(PROTOCOL=tcp)(HOST=" + props.getStr("cdc.oracle.hostname") + ")(PORT=1521)))(CONNECT_DATA=(SID=" + props.getStr("cdc.oracle.database") + ")))");

        //读取oracle连接配置属性
        SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("cdc.oracle.hostname"))
                .port(props.getInt("cdc.oracle.port"))
                .database(props.getStr("cdc.oracle.database"))
                .schemaList(StrUtil.getStrList(props.getStr("cdc.oracle.schema.list"), ","))
                .tableList(StrUtil.getStrList(props.getStr("cdc.oracle.table.list"), ","))
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();

        // CheckpointConfig ck = env.getCheckpointConfig();
     /*   ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        ck.setCheckpointTimeout(60000);
        //确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        ck.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        ck.setMaxConcurrentCheckpoints(1);*/
        //System.setProperty("HADOOP_USER_NAME", "yunding");
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");
        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("oracleSourceStream").name("oracleSourceStream");

        //sptb02过滤 大于 2021-06-01 00:00:00的数据
        SingleOutputStreamOperator<String> ddjrqFilter = oracleSourceStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String json) throws Exception {
                JSONObject jsonObj = JSON.parseObject(json);
                String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);
                if ("SPTB02".equals(tableNameStr)) {
                    //临界值开始时间
                    String criticalStart = "2021-06-01 00:00:00";
                    //临界值结束时间
                    String criticalEnd = "2022-12-31 23:59:59";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
                    Date dateCriticalStart = sdf.parse(criticalStart);
                    Date dateCriticalEnd = sdf.parse(criticalEnd);
                    JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                    //运单日期
                    String ddjrq = afterObj.getString("DDJRQ");
                    if (StringUtils.isNotEmpty(ddjrq)) {
                        long ddjrqTime = Long.parseLong(ddjrq);
                        if (ddjrqTime >= dateCriticalStart.getTime() && ddjrqTime <= dateCriticalEnd.getTime()) {
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
        }).uid("ddjrqFilter").name("ddjrqFilter");

        //获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.CDC_VLMS_UNITE_ORACLE,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.CDC_VLMS_UNITE_ORACLE));

        ddjrqFilter.print("结果数据输出:");
        //输出到kafka
        //ddjrqFilter.addSink(sinkKafka).uid("sinkKafka").name("sinkKafka");
        log.info("add sink kafka设置完成");
        env.execute("oracle-cdc-kafka");
        log.info("oracle-cdc-kafka job开始执行");
    }
}
