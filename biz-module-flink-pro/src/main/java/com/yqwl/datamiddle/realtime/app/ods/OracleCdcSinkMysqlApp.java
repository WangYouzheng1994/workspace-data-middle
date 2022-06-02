package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.DimBatchSink;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.bean.Mdac01;
import com.yqwl.datamiddle.realtime.bean.ProvincesWide;
import com.yqwl.datamiddle.realtime.bean.Sptb02d1;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;

/**
 * @Description: 将oracle中某个表消费到mysql中
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OracleCdcSinkMysqlApp {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        log.info("stream流环境初始化完成");
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        //properties.put("database.serverTimezone", "UTC");
        properties.put("database.serverTimezone", "Asia/Shanghai");
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

        //CheckpointConfig ck = env.getCheckpointConfig();
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

        //将json串转化成jsonObj
        SingleOutputStreamOperator<Sptb02d1> sourceStreamJsonObj = oracleSourceStream.map(new MapFunction<String, Sptb02d1>() {
            @Override
            public Sptb02d1 map(String json) throws Exception {
                System.out.println(json);
                JSONObject jsonObj = JSON.parseObject(json);
                //获取cdc进入kafka的时间
                String tsStr = JsonPartUtil.getTsStr(jsonObj);
                //获取after数据
                JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                afterObj.put("WAREHOUSE_CREATETIME", tsStr);
                afterObj.put("WAREHOUSE_UPDATETIME", tsStr);
                jsonObj.put("after", afterObj);
                //获取after真实数据后，映射为实体类
                Sptb02d1 sptb02d1 = JsonPartUtil.getAfterObj(jsonObj, Sptb02d1.class);
                log.info("反射后的实例:{}", sptb02d1);
                //对映射后的实体类为null字段
                return JsonPartUtil.getBean(sptb02d1);
            }
        }).uid("sourceStreamJsonObj").name("sourceStreamJsonObj");
        //sourceStreamJsonObj.print("结果数据输出:");
        //组装sql
        StringBuffer sql = new StringBuffer();
        String columnSql = StrUtil.getColumnSql(Sptb02d1.class);
        String valueSql = StrUtil.getValueSql(Sptb02d1.class);
        sql.append("replace into ").append(KafkaTopicConst.ODS_VLMS_SPTB02D1).append(columnSql).append(" values ").append(valueSql);
        log.info("组装的插入sql:{}", sql);
        sourceStreamJsonObj.addSink(JdbcSink.<Sptb02d1>getSink(sql.toString())).setParallelism(1).uid("oracle-cdc-mysql").name("oracle-cdc-mysql");
        log.info("add sink mysql设置完成");
        env.execute("oracle-cdc-mysql");
        log.info("oracle-cdc-kafka job开始执行");
    }
}
