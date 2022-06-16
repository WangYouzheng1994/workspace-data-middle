package com.yqwl.datamiddle.realtime.app.ods;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.Sptb02d1;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 将oracle中某个表直接cdc同步到mysql中
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class OracleCdcSinkMysqlSptb02d1App {
    //2020-01-01 00:00:00
    private static final long START = 1577808000000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        log.info("stream流环境初始化完成");

        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(600000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //检查点必须在一分钟内完成，或者被丢弃【CheckPoint的超时时间】
        //ck.setCheckpointTimeout(60000);
        //确保检查点之间有至少500 ms的间隔【CheckPoint最小间隔】
        //ck.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        //ck.setMaxConcurrentCheckpoints(1);
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
                .tableList("TDS_LJ.SPTB02D1")
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();


        log.info("checkpoint设置完成");
        SingleOutputStreamOperator<String> oracleSourceStream = env.addSource(oracleSource).uid("oracleSourceStream").name("oracleSourceStream");

        SingleOutputStreamOperator<Sptb02d1> processBsd = oracleSourceStream.process(new ProcessFunction<String, Sptb02d1>() {
            @Override
            public void processElement(String value, Context ctx, Collector<Sptb02d1> out) throws Exception {
                JSONObject jsonObj = JSON.parseObject(value);
                //获取表名
                //String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);
                //获取cdc时间
                String tsStr = JsonPartUtil.getTsStr(jsonObj);
                //获取真实数据
                JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                afterObj.put("WAREHOUSE_CREATETIME", tsStr);
                afterObj.put("WAREHOUSE_UPDATETIME", tsStr);
                jsonObj.put("after", afterObj);

                //获取after真实数据后，映射为实体类
                Sptb02d1 sptb02d1 = JsonPartUtil.getAfterObj(jsonObj, Sptb02d1.class);
                //log.info("反射后的实例:{}", baseStationData);
                //对映射后的实体类为null字段赋值默认值
                Sptb02d1 bean = JsonPartUtil.getBean(sptb02d1);
                out.collect(bean);
            }
        }).uid("processBsd").name("processBsd");

        //组装sql
        String sql = MysqlUtil.getSql(Sptb02d1.class);
        log.info("组装的插入sql:{}", sql);
        processBsd.addSink(JdbcSink.<Sptb02d1>getSink(sql)).setParallelism(1).uid("oracle-cdc-mysql").name("oracle-cdc-mysql");
        log.info("add sink mysql设置完成");
        env.execute("oracle-cdc-mysql-sptb02d1");
        log.info("oracle-cdc-kafka job开始执行");
    }
}
