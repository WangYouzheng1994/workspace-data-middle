package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
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

import java.util.concurrent.TimeUnit;

/**
 * @Description: 对运单 DwmOneOrderToEnd的表插入Clickhouse
 * @Author: XiaoFeng
 * @Date: 2022/06/08
 * @Version: V1.0
 */
@Slf4j
public class WaybillDwmAppClickHouseSptb02 {

    public static void main(String[] args) throws Exception {
        //1.创建环境  Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
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

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_middle_flink.dwm_vlms_sptb02")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization()) // converts SourceRecord to JSON String
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");


        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwmSptb02> mapBsdEpc = mysqlSource.map(new MapFunction<String, DwmSptb02>() {
            @Override
            public DwmSptb02 map(String kafkaBsdEpcValue) throws Exception {
                JSONObject jsonObject = JSON.parseObject(kafkaBsdEpcValue);
                return jsonObject.getObject("after", DwmSptb02.class);
            }
        }).uid("transitionDwmOneOrderToEnd").name("transitionDwmOneOrderToEnd");
        //====================================sink clickhouse===============================================//
        //        组装sql
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(KafkaTopicConst.DWM_VLMS_SPTB02).append(" values ").append(StrUtil.getValueSql(DwmSptb02.class));
        log.info("组装clickhouse插入sql:{}", sql);
        mapBsdEpc.addSink(ClickHouseUtil.<DwmSptb02>getSink(sql.toString())).setParallelism(1).uid("sink-clickhouse").name("sink-clickhouse");
        mapBsdEpc.print("拉宽后数据输出：");


        log.info("将处理完的数据保存到clickhouse中");
        env.execute("sptb02-DwmOneOrderToEnd");
        log.info("DwmOneOrderToEnd层job任务开始执行");
    }
}
