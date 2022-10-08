package com.yqwl.datamiddle.realtime.app.dwd;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.app.func.JdbcSink;
import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.util.KafkaUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * @Description: <一单到底>Oracle: base_station_data与base_station_data_epc存到Mysql表
 * 版本更改: 1.3 由kafka到kafka&&mysql
 * @Author: XiaoFeng
 * @Date: 2022/6/02 10:30
 * @Version: V1.3
 */
@Slf4j
public class BaseStationDataAndEpcDwdAppBsd {
    //2021-06-01 00:00:00
    private static final long START = 1622476800000L;
    //2022-12-31 23:59:59
    private static final long END = 1672502399000L;
    private static final String BASE_STATION_DATA = "BASE_STATION_DATA";
    private static final String BASE_STATION_DATA_EPC = "BASE_STATION_DATA_EPC";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, org.apache.flink.api.common.time.Time.of(10, TimeUnit.SECONDS)));
        env.setParallelism(1);
        log.info("初始化流处理环境完成");

        //====================================checkpoint配置===============================================//
        CheckpointConfig ck = env.getCheckpointConfig();
        ck.setCheckpointInterval(300000);
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //系统异常退出或人为Cancel掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "yunding");
        // 设置checkpoint点二级目录位置
        ck.setCheckpointStorage(PropertiesUtil.getCheckpointStr("base_station_data_epc_dwd_app_bsd"));
        // 设置savepoint点二级目录位置
        // env.setDefaultSavepointDirectory(PropertiesUtil.getSavePointStr("base_station_data_epc_dwd_app_bsd"));

        log.info("checkpoint设置完成");

        //读取oracle连接配置属性
        /*        Props props = PropertiesUtil.getProps();
        //oracle cdc 相关配置
        Properties properties = new Properties();
        properties.put("database.tablename.case.insensitive", "false");
        properties.put("log.mining.strategy", "online_catalog"); //解决归档日志数据延迟
        properties.put("log.mining.continuous.mine", "true");   //解决归档日志数据延迟
        properties.put("decimal.handling.mode", "string");   //解决number类数据 不能解析的方法
        //properties.put("database.serverTimezone", "UTC");
        //properties.put("database.serverTimezone", "Asia/Shanghai");
        properties.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=YES)(FAILOVER=YES)(ADDRESS=(PROTOCOL=tcp)(HOST=" + props.getStr("cdc.oracle.hostname") + ")(PORT=1521)))(CONNECT_DATA=(SID=" + props.getStr("cdc.oracle.database") + ")))");

                SourceFunction<String> oracleSource = OracleSource.<String>builder()
                .hostname(props.getStr("cdc.oracle.hostname"))
                .port(props.getInt("cdc.oracle.port"))
                .database(props.getStr("cdc.oracle.database"))
                .schemaList(StrUtil.getStrList(props.getStr("cdc.oracle.schema.list"), ","))
                .tableList("TDS_LJ.BASE_STATION_DATA")
                .username(props.getStr("cdc.oracle.username"))
                .password(props.getStr("cdc.oracle.password"))
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(properties)
                .build();*/

        //kafka消费源相关参数配置
        Props props = PropertiesUtil.getProps();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_LATEST_0701)
                .setGroupId(KafkaTopicConst.ODS_VLMS_BASE_STATION_DATA_LATEST_0701_GROUP)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 将kafka中源数据转化成DataStream
        SingleOutputStreamOperator<String> oracleSourceStream = env.fromSource(kafkaSource,WatermarkStrategy.noWatermarks(),"BaseStationDataAndEpcDwdAppBsdods_bsd-kafka").uid("BaseStationDataAndEpcDwdAppBsdoracleSourceStream").name("BaseStationDataAndEpcDwdAppBsdoracleSourceStream");
        // oracleSourceStream.print("source 输出：");
        //过滤 大于 2021-06-01 00:00:00的数据
       /*  SingleOutputStreamOperator<String> dataAndEpcFilter = oracleSourceStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String json) throws Exception {
                //要转换的时间格式
                JSONObject jsonObj = JSON.parseObject(json);
                JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObj);
                String tableNameStr = JsonPartUtil.getTableNameStr(jsonObj);

                if (BASE_STATION_DATA.equals(tableNameStr)) {
                    //获取上报完成时间
                    String sample_u_t_c = afterObj.getString("SAMPLE_U_T_C");
                    if (StringUtils.isNotEmpty(sample_u_t_c)) {
                        long cutSampleTime = Long.parseLong(sample_u_t_c) / 1000;
                        if (cutSampleTime >= START && cutSampleTime <= END) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                         }}
                return true;
            }
        }).uid("BaseStationDataAndEpcDwdAppBsddataAndEpcFilter").name("BaseStationDataAndEpcDwdAppBsddataAndEpcFilter");
       */


        //vesion1.3 一源到底 kafka->kafka && mysql
        SingleOutputStreamOperator<DwdBaseStationData> dwmProcess = oracleSourceStream.process(new ProcessFunction<String, DwdBaseStationData>() {
            @Override
            public void processElement(String value, Context ctx, Collector<DwdBaseStationData> out) throws Exception {
                log.info("process方法开始执行");
                // 1 .转实体类
                JSONObject jsonObject = JSON.parseObject(value);
                String after = jsonObject.getString("after");
                DwdBaseStationData dwdBaseStationData = JSON.parseObject(after, DwdBaseStationData.class);
                String vin = dwdBaseStationData.getVIN();
                if (StringUtils.isNotBlank(vin)) {
                    // 2 .处理字段 base_station_data 和rfid_warehouse关联添加入库仓库的字段
                    String shop_no = dwdBaseStationData.getSHOP_NO();
                    String warehouse_code = "";
                    String warehouse_type = "";
                    String warehouse_name = "";
                    // 出入库类型
                    String PHYSICAL_CODE = "";
                    if (StringUtils.isNotBlank(shop_no)) {
                        String bdsSql = "select * from " + KafkaTopicConst.DIM_VLMS_WAREHOUSE_RS + " where WAREHOUSE_CODE = '" + shop_no + "' limit 1";
                        JSONObject bdsResult = MysqlUtil.querySingle(KafkaTopicConst.DIM_VLMS_WAREHOUSE_RS, bdsSql, shop_no);
                        if (bdsResult != null) {
                            // 库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）
                            warehouse_type = bdsResult.getString("WAREHOUSE_TYPE");
                            // 库房代码
                            warehouse_code = bdsResult.getString("WAREHOUSE_CODE");
                            // 库房名称
                            warehouse_name = bdsResult.getString("WAREHOUSE_NAME");
                            // VWLCKDM 物理仓库代码
                            PHYSICAL_CODE = bdsResult.getString("VWLCKDM");
                            if (StringUtils.isNotBlank(warehouse_type)) {
                                dwdBaseStationData.setWAREHOUSE_TYPE(warehouse_type);
                            }
                            if (StringUtils.isNotBlank(warehouse_code)) {
                                dwdBaseStationData.setIN_WAREHOUSE_CODE(warehouse_code);
                            }
                            if (StringUtils.isNotBlank(warehouse_name)) {
                                dwdBaseStationData.setIN_WAREHOUSE_NAME(warehouse_name);
                            }
                            if (StringUtils.isNotBlank(PHYSICAL_CODE)) {
                                dwdBaseStationData.setPHYSICAL_CODE(PHYSICAL_CODE);
                            }
                        }
                    }
                    out.collect(dwdBaseStationData);
                }
            }
        }).uid("BaseStationDataAndEpcDwdAppBsddwmBsdProcess").name("BaseStationDataAndEpcDwdAppBsddwmBsdProcess");

        //--------------------------------存入DwdBaseStationData mysql------------------------------------//
        String bsdSql = MysqlUtil.getSql(DwdBaseStationData.class);
        dwmProcess.addSink(JdbcSink.<DwdBaseStationData>getSink(bsdSql)).uid("BaseStationDataAndEpcDwdAppBsdsink-mysqDsb").name("BaseStationDataAndEpcDwdAppBsdsink-mysqldsb");

        //-------------------------------存入kafkaDwdBaseStationDataTopic--------------------------------//
        SingleOutputStreamOperator<String> dwmSptb02Json = dwmProcess.map(new MapFunction<DwdBaseStationData, String>() {
            @Override
            public String map(DwdBaseStationData obj) throws Exception {
                return JSON.toJSONString(obj);
            }
        }).uid("BaseStationDataAndEpcDwdAppBsddwmBsdJson").name("BaseStationDataAndEpcDwdAppBsddwmBsdJson");

        // 获取kafka生产者
        FlinkKafkaProducer<String> sinkKafka = KafkaUtil.getKafkaProductBySchema(
                props.getStr("kafka.hostname"),
                KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA,
                KafkaUtil.getKafkaSerializationSchema(KafkaTopicConst.DWD_VLMS_BASE_STATION_DATA));
        dwmSptb02Json.addSink(sinkKafka).uid("BaseStationDataAndEpcDwdAppBsdinkKafkaDwdBsd").name("BaseStationDataAndEpcDwdAppBsdsinkKafkaDwdBsd");
        env.execute("拉宽bsd表进入dwdBsd");
    }
}
