package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.ClickHouseUtil;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 对运单 DwmOneOrderToEnd的表插入Clickhouse
 * @Author: XiaoFeng
 * @Date: 2022/06/08
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmAppClickHouseSptb02 {
    //2022-01-01 00:00:00
    private static final long START = 1640966400000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;


    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        // 算子拒绝合并
        env.disableOperatorChaining();
        log.info("初始化流处理环境完成");
        //====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");

        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("waybill_dwm_clickhouse_sptb02"));
        // 设置savepoint点二级目录位置
        //env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("waybill_dwm_clickhouse_sptb02"));

        log.info("checkpoint设置完成");

        Properties properties = new Properties();
        // 以下为了遇到报错跳过错误而加
        properties.setProperty("debezium.inconsistent.schema.handing.mode","warn");
        properties.setProperty("debezium.event.deserialization.failure.handling.mode","warn");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwm_vlms_sptb02")
//                .tableList("data_middle_flink.dwm_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .debeziumProperties(properties)
                .startupOptions(StartupOptions.initial())
                .distributionFactorUpper(10.0d)  // 针对cdc的错误算法的更改
                .serverId("5417-5420")
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "WaybillDwmAppClickHouseSptb02MysqlSource").uid("WaybillDwmAppClickHouseSptb02").name("WaybillDwmAppClickHouseSptb02");


        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwmSptb02> mapBsdEpc = mysqlSource.map(new MapFunction<String, DwmSptb02>() {
            @Override
            public DwmSptb02 map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                DwmSptb02 after = jsonObject.getObject("after", DwmSptb02.class);
                // 设置更新时间，兜底合并clickhouse
                if (after.getWAREHOUSE_UPDATETIME() == null || after.getWAREHOUSE_UPDATETIME() == 0L) {
                    after.setWAREHOUSE_UPDATETIME(System.currentTimeMillis());
                }
                return after;
            }
        }).uid("WaybillDwmAppClickHouseSptb02TransitionDwmOneOrderToEnd").name("WaybillDwmAppClickHouseSptb02TransitionDwmOneOrderToEnd");
        //====================================sink clickhouse===============================================//
        //        组装sql
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(KafkaTopicConst.DWM_VLMS_SPTB02).append(" values ").append(StrUtil.getValueSql(DwmSptb02.class));
        log.info("组装clickhouse插入sql:{}", sql);
        mapBsdEpc.addSink(ClickHouseUtil.<DwmSptb02>getSink(sql.toString())).setParallelism(1).uid("WaybillDwmAppClickHouseSptb02Sink-clickhouse").name("WaybillDwmAppClickHouseSptb02Sink-clickhouse");

        log.info("将处理完的数据保存到clickhouse中");
        env.execute("Mysql_DwmSptb02->Clickhosue_DwmSptb02");
        log.info("DwmOneOrderToEnd层job任务开始执行");
    }
}
