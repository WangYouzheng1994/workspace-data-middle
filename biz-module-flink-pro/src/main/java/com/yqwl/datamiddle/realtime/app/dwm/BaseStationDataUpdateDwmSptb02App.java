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
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseStationDataUpdateDwmSptb02App {

    public static void main(String[] args) throws Exception {
        // 获取执行环境:
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, org.apache.flink.api.common.time.Time.of(30, TimeUnit.SECONDS)));
        env.setParallelism(1);

        //kafka消费源相关参数配
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA)
                .setGroupId(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA_GROUP_1)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        SingleOutputStreamOperator<String> mysqlStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "MySQL-Source").uid("mysqlSource").name("mysqlSource");
        //将json转成obj
        SingleOutputStreamOperator<DwdBaseStationData> baseStationDataMap = mysqlStream.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String json) throws Exception {
                return JSON.parseObject(json, DwdBaseStationData.class);
            }
        }).uid("baseStationDataMap").name("baseStationDataMap");

        baseStationDataMap.addSink(new SimpleBaseStationDataSink<DwdBaseStationData>()).uid("aseStationDataSink").name("aseStationDataSink");


        log.info("消费dwd_vlms_base_station_data更新dwm_vlms_sptb02");
        env.execute("消费dwd_vlms_base_station_data更新dwm_vlms_sptb02");
    }
}
