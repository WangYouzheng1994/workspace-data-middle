package com.yqwl.datamiddle.realtime.app.dwm;

import cn.hutool.setting.dialect.Props;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.yqwl.datamiddle.realtime.app.func.SimpleBaseStationDataSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.util.CustomerDeserialization;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseStationDataUpdateDwmSptb02App {

    public static void main(String[] args) throws Exception {
        // 获取执行环境:
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        //====================================消费dwd_vlms_base_station_data的binlog 更新===============================================//
        //读取mysql配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //读取mysql binlog
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(props.getStr("cdc.mysql.hostname"))
                .port(props.getInt("cdc.mysql.port"))
                .databaseList(StrUtil.getStrList(props.getStr("cdc.mysql.database.list"), ","))
                .tableList("data_flink.dwd_vlms_base_station_data")
                .username(props.getStr("cdc.mysql.username"))
                .password(props.getStr("cdc.mysql.password"))
                .deserializer(new CustomerDeserialization())
                .build();

        SingleOutputStreamOperator<String> mysqlStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL-Source").uid("mysqlSource").name("mysqlSource");
        //将json转成obj
        SingleOutputStreamOperator<DwdBaseStationData> baseStationDataMap = mysqlStream.map(new MapFunction<String, DwdBaseStationData>() {
            @Override
            public DwdBaseStationData map(String json) throws Exception {
                return JsonPartUtil.getAfterObj(json, DwdBaseStationData.class);
            }
        }).uid("baseStationDataMap").name("baseStationDataMap");

        baseStationDataMap.addSink(new SimpleBaseStationDataSink<DwdBaseStationData>()).uid("aseStationDataSink").name("aseStationDataSink");


        log.info("消费base_station_data更新one-order");
        env.execute("消费base_station_data更新one-order");
    }
}
