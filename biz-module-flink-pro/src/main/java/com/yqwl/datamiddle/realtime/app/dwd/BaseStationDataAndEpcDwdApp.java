package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.yqwl.datamiddle.realtime.bean.BaseStationDataEpc;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import com.yqwl.datamiddle.realtime.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

/**
 * @Description: 一单到底
 * @Author: muqing
 * @Date: 2022/05/06
 * @Version: V1.0
 */
@Slf4j
public class BaseStationDataAndEpcDwdApp {

    public static void main(String[] args) throws Exception {
        //Flink 流式处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        log.info("初始化流处理环境完成");
        //设置CK相关参数
      /*  CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(10000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);*/
        System.setProperty("HADOOP_USER_NAME", "root");
        log.info("checkpoint设置完成");

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);

        // kafka source1 base_station_data_epc
        KafkaSource<String> bsdEpcSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC)
                .setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // kafka source2 base_station_data
        KafkaSource<String> bsdSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA)
                .setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_EPC_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        //将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> bsdEpcDs = env.fromSource(bsdEpcSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-epc").uid("bsdEpcDs").name("bsdEpcDs");
        SingleOutputStreamOperator<String> bsdDs = env.fromSource(bsdSource, WatermarkStrategy.noWatermarks(), "kafka-consumer-bsd").uid("bsdDs").name("bsdDs");


        SingleOutputStreamOperator<DwdBaseStationDataEpc> processBsdEpcDs = bsdEpcDs.process(new ProcessFunction<String, DwdBaseStationDataEpc>() {

            @Override
            public void processElement(String json, Context ctx, Collector<DwdBaseStationDataEpc> out) throws Exception {
                BaseStationDataEpc dataEpc = JsonPartUtil.getAfterObj(json, BaseStationDataEpc.class);
                DwdBaseStationDataEpc toEnd = new DwdBaseStationDataEpc();
                //todo 根据 vin 按规则拆分出 车型 品牌
                String vin = dataEpc.getVIN();
                toEnd.setVIN(dataEpc.getVIN());


                out.collect(toEnd);
            }

        });


        //base_station_data获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA));


















        log.info("将处理完的数据保存到clickhouse中");
        env.execute("sptb02-sink-clickhouse-dwm");
        log.info("sptb02dwd层job任务开始执行");

    }
}
