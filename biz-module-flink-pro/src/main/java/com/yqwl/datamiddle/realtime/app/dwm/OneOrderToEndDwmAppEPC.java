package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

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
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC)
                .setGroupId(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //1.将mysql中的源数据转化成 DataStream
        SingleOutputStreamOperator<String> mysqlSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "MysqlSource").uid("MysqlSourceStream").name("MysqlSourceStream");

        //==============================================dwd_base_station_data_epc处理 START====================================================================//

        //3.转实体类 BASE_STATION_DATA_EPC
        SingleOutputStreamOperator<DwdBaseStationDataEpc> mapBsdEpc = mysqlSource.map(new MapFunction<String, DwdBaseStationDataEpc>() {
            @Override
            public DwdBaseStationDataEpc map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationDataEpc.class);
            }
        }).uid("transitionBASE_STATION_DATA_EPCMap").name("transitionBASE_STATION_DATA_EPCMap");
        //4.插入mysql
        mapBsdEpc.addSink(JdbcSink.sink(

                "INSERT INTO dwm_vlms_one_order_to_end (VIN, CP9_OFFLINE_TIME, BASE_NAME, BASE_CODE, WAREHOUSE_CREATETIME, WAREHOUSE_UPDATETIME )\n" +
                        "VALUES\n" +
                        "        ( ?, ?, ?, ?, ?, ? ) \n" +
                        "        ON DUPLICATE KEY UPDATE \n" +
                        "   CP9_OFFLINE_TIME = ? ,BASE_NAME = ?,\n" +
                        "        BASE_CODE = ? , WAREHOUSE_CREATETIME = ? , WAREHOUSE_UPDATETIME = ?",
                (ps, epc) -> {
                    ps.setString(1, epc.getVIN());
                    ps.setLong(2, epc.getCP9_OFFLINE_TIME());
                    ps.setString(3, epc.getBASE_NAME());
                    ps.setString(4, epc.getBASE_CODE());
                    ps.setLong(5, epc.getWAREHOUSE_CREATETIME());
                    ps.setLong(6, epc.getWAREHOUSE_CREATETIME());
                    ps.setLong(7, epc.getCP9_OFFLINE_TIME());
                    ps.setString(8, epc.getBASE_NAME());
                    ps.setString(9, epc.getBASE_CODE());
                    ps.setLong(10, epc.getWAREHOUSE_CREATETIME());
                    ps.setLong(11, epc.getWAREHOUSE_CREATETIME());
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

        env.execute("bsdEpc更新一单到底表");
        log.info("base_station_data job任务开始执行");

    }
}
