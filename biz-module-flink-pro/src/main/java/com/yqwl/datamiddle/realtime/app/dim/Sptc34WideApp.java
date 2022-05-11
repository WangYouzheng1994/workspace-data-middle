package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.setting.dialect.Props;
import com.google.common.collect.Lists;
import com.yqwl.datamiddle.realtime.bean.Sptc34Wide;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.util.JDBCSink;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * 从kafka读取sptc34 的topic   并将 sptc34表拓宽字段生成新的维度表   并将生成的维度宽表传到 mysql中
 */
public class Sptc34WideApp {
    public static void main(String[] args) throws Exception{

        Configuration conf = new Configuration();
        conf.setString(RestOptions.BIND_PORT, "8081"); // 指定访问端口
        //获取执行环境
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        //设置并行度
        // env.setParallelism(3);
        env.disableOperatorChaining();
        //设置checkpoint`
        CheckpointConfig ck = env.getCheckpointConfig();
        //触发保存点的时间间隔, 每隔1000 ms进行启动一个检查点
        ck.setCheckpointInterval(10000);
        //采用精确一次模式
        ck.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //检查点保存路径
        ck.setCheckpointStorage("hdfs://192.168.3.95:8020/demo/cdc/checkpoint");
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        ck.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        System.setProperty("HADOOP_USER_NAME", "root");


        Props props = PropertiesUtil.getProps();

        //从kafka消费数据
        KafkaSource<String> kafkasource = KafkaSource.<String>builder()
                .setBootstrapServers(props.getStr("kafka.hostname"))
                .setTopics(KafkaTopicConst.ODS_VLMS_SPTC34)
                .setGroupId(KafkaTopicConst.ODS_VLMS_SPTC34_GROUP)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> kafkaStream = env.fromSource(kafkasource, WatermarkStrategy.noWatermarks(), "odssptc34kafka-source");
        // kafkaStream.print(System.currentTimeMillis()+"");
//        kafkaStream.print();

        //sptc34Wide表转换成实体类(sptc34表的数据传到sptc34Wide中)    sptc34表里面有176条数据省区市县代码是空值  已经使用空串代替
        SingleOutputStreamOperator<Sptc34Wide> Sptc34MapSteeam = kafkaStream.map(new MapFunction<String, Sptc34Wide>() {
            @Override
            public Sptc34Wide map(String value) throws Exception {
                //获取表字段并将没有值的数据添加默认值
                Sptc34Wide sptc34Wide = JsonPartUtil.getAfterObjWithDefault(value, Sptc34Wide.class);
                //合并省区市县字段
                String vsqsxdm = StringUtils.join(sptc34Wide.getVsqdm(), sptc34Wide.getVsxdm());
                //将合并的省区市县添加到sptc34Wide表的vsqsxdm 中
                sptc34Wide.setVsqsxdm(vsqsxdm);
//                sptc34Wide.setWarehouseCreatetime(System.currentTimeMillis());
                //获取kafka的时间戳作为创建时间和更新时间
                String tsStr = JsonPartUtil.getTsStr(value);
                //将String类型的时间戳转换成Long类型
                Long aLong = Long.valueOf(tsStr);
                //获取数据类型
                String typeStr = JsonPartUtil.getTypeStr(value);
                if ( typeStr.equals("insert") ) {

                    //获取当前的时间戳 到毫秒级 并添加到sptc34Wide表中的创建时间
                    sptc34Wide.setWarehouseCreatetime(aLong);
                }else if ( typeStr.equals("update") ) {
                    //获取当前的时间戳 到毫秒级  并添加到sptc34Wide表的更新时间中
                    sptc34Wide.setWarehouseUpdatetime(aLong);
                }
                return sptc34Wide;
            }
        }).uid("odsSptc34").name("odsSptc34");

        //将输出的内容打印到logger中
        // Sptc34MapSteeam.print(System.currentTimeMillis()+"");



        // 这里需要搞成 有超时时间的计数窗口。
        Sptc34MapSteeam.countWindowAll(500).apply(new AllWindowFunction<Sptc34Wide, List<Sptc34Wide>, GlobalWindow>() {
            @Override
            public void apply(GlobalWindow window, Iterable<Sptc34Wide> iterable, Collector<List<Sptc34Wide>> collector) throws Exception {
                ArrayList<Sptc34Wide> skuInfos = Lists.newArrayList(iterable);
                if (skuInfos.size() > 0) {
                    collector.collect(skuInfos);
                }
            }
        }).addSink(JDBCSink.<Sptc34Wide>getBatchSink());

/*        Sptc34MapSteeam.timeWindowAll(Time.milliseconds(20)).apply(new AllWindowFunction<Sptc34Wide, List<Sptc34Wide>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<Sptc34Wide> iterable, Collector<List<Sptc34Wide>> collector) throws Exception {
                ArrayList<Sptc34Wide> skuInfos = Lists.newArrayList(iterable);
                if (skuInfos.size() > 0) {
                    collector.collect(skuInfos);
                }
            }
        }).addSink(JDBCSink.<Sptc34Wide>getBatchSink());*/

        //连接mysql数据库,将数据存到mysql中
        /*Sptc34MapSteeam.addSink(JDBCSink.<Sptc34Wide>getSink("REPLACE INTO dim_vlms_sptc34  (IDNUM,  VWLCKDM,  VWLCKMC,  CZT," +
                        "   NKR, VSQDM, VSXDM, VLXR, VDH, VCZ, VEMAIL,  VYDDH,  VYB,  VDZ,  CTYBS,  DTYRQ,  VBZ,  CCKSX, " +
                        "  CGLKQKW, CCCSDM, VCFTJ, CWX,  CGS, CSCFBJH,  VDZCKDM,  CYSSDM,  CYSCDM,  VWLCKJC,  CWLBM,  CWLMC, " +
                        "  DTBRQ, BATCHNO,  CWLBM3,  CCKLX,  DSTAMP, APPROVAL_FLAG,  APPROVAL_USER,  APPROVAL_DATE,  FINAL_APPROVAL_FLAG, " +
                        "  FINAL_APPROVAL_USER,  FINAL_APPROVAL_DATE,  CZJGSDM,  VZTMC_ZT, VSQSXDM, WAREHOUSE_CREATETIME,  WAREHOUSE_UPDATETIME) VALUES " +
                        "(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)"))
                .uid("sptc34sinkMysql").name("sptc34sinkMysql");*/
        //启动
        env.execute("Sptc34WideApp");
    }
}
