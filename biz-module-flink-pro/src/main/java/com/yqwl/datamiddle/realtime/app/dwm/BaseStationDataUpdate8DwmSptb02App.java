package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.SimpleBaseStationDataSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 溯源更新dwdsptb02与dwmsptb02相关字段
 *               更新的字段有:
 *               IN_SITE_TIME 入库日期
 *               LEAVE_SITE_TIME 出库日期
 *               IN_WAREHOUSE_NAME 仓库名称
 *               铁水的 8个时间物流节点字段
 * @Author: XiaoFeng
 * @Date: 2022/11/07
 * @Version: V2.0
 */

@Slf4j
public class BaseStationDataUpdate8DwmSptb02App {

    public static void main(String[] args) throws Exception {
        // 获取执行环境:
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        env.setParallelism(1);
        // 从偏移量表中读取指定的偏移量模式
        HashMap<TopicPartition, Long> offsetMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(KafkaTopicConst.ODS_VLMS_SPTB02_LATEST_0701, 0);
        offsetMap.put(topicPartition, 109000L);

        log.info("初始化流处理环境完成");
        //设置CK相关参数
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        log.info("checkpoint设置完成");
        //kafka消费源相关参数配
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA)
                .setGroupId(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_GROUP_1)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                // .setStartingOffsets(OffsetsInitializer.offsets(offsetMap)) // 指定起始偏移量 60 6-1
                .build();

        SingleOutputStreamOperator<String> mysqlStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "BaseStationDataUpdate8DwmSptb02AppMySQL-Source").uid("BaseStationDataUpdate8DwmSptb02AppmysqlSource").name("BaseStationDataUpdate8DwmSptb02AppMysqlSource");
        //将json转成obj
        SingleOutputStreamOperator<DwdBaseStationData> baseStationDataMap = mysqlStream.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationData.class);
            }
        }).uid("BaseStationDataUpdate8DwmSptb02AppBaseStationDataMap").name("BaseStationDataUpdate8DwmSptb02AppBaseStationDataMap");

        baseStationDataMap.addSink(new SimpleBaseStationDataSink<DwdBaseStationData>()).uid("BaseStationDataUpdate8DwmSptb02AppAseStationDataSink").name("BaseStationDataUpdate8DwmSptb02AppAseStationDataSink");

        log.info("消费dwdBsd更新dwm_vlms_sptb02,8个时间节点");
        env.execute("消费dwdBsd更新dwm_vlms_sptb02,8个时间节点");
    }
}
