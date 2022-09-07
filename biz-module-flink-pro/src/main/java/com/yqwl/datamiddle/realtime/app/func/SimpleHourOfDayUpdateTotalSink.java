package com.yqwl.datamiddle.realtime.app.func;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/9/7 20:51
 * @Version: V1.0
 */
@Slf4j
public class SimpleHourOfDayUpdateTotalSink<T> extends RichSinkFunction<String> {
    // 20220905日
    private static final long DAY20220905_00 = 1662307200000L;
    private static final long DAY20220905_01 = 1662310800000L;
    private static final long DAY20220905_02 = 1662314400000L;
    private static final long DAY20220905_03 = 1662318000000L;
    private static final long DAY20220905_04 = 1662321600000L;
    private static final long DAY20220905_05 = 1662321600000L;
    private static final long DAY20220905_06 = 1662328800000L;
    private static final long DAY20220905_07 = 1662332400000L;
    private static final long DAY20220905_08 = 1662336000000L;
    private static final long DAY20220905_09 = 1662339600000L; // 八分之一节点
    private static final long DAY20220905_10 = 1662343200000L;
    private static final long DAY20220905_11 = 1662346800000L;
    private static final long DAY20220905_12 = 1662350400000L;
    private static final long DAY20220905_13 = 1662354000000L;
    private static final long DAY20220905_14 = 1662357600000L;
    private static final long DAY20220905_15 = 1662361200000L;
    private static final long DAY20220905_16 = 1662364800000L;
    private static final long DAY20220905_17 = 1662368400000L;
    private static final long DAY20220905_18 = 1662372000000L; // 四分之一节点
    private static final long DAY20220905_19 = 1662375600000L;
    private static final long DAY20220905_20 = 1662379200000L;
    private static final long DAY20220905_21 = 1662382800000L;
    private static final long DAY20220905_22 = 1662386400000L;
    private static final long DAY20220905_23 = 1662390000000L;
    private static final long DAY20220905_24 = 1662393600000L;

    // 20220906日
    private static final long DAY20220906_00 = 1662393600000L;
    private static final long DAY20220906_01 = 1662397200000L;
    private static final long DAY20220906_02 = 1662400800000L; // 八分之一节点
    private static final long DAY20220906_03 = 1662404400000L;
    private static final long DAY20220906_04 = 1662408000000L;
    private static final long DAY20220906_05 = 1662411600000L;
    private static final long DAY20220906_06 = 1662415200000L;
    private static final long DAY20220906_07 = 1662418800000L;
    private static final long DAY20220906_08 = 1662422400000L;
    private static final long DAY20220906_09 = 1662426000000L;
    private static final long DAY20220906_10 = 1662429600000L;
    private static final long DAY20220906_11 = 1662433200000L;
    private static final long DAY20220906_12 = 1662436800000L; // 二分之一节点
    private static final long DAY20220906_13 = 1662440400000L;
    private static final long DAY20220906_14 = 1662444000000L;
    private static final long DAY20220906_15 = 1662447600000L;
    private static final long DAY20220906_16 = 1662451200000L;
    private static final long DAY20220906_17 = 1662454800000L;
    private static final long DAY20220906_18 = 1662458400000L;
    private static final long DAY20220906_19 = 1662462000000L;
    private static final long DAY20220906_20 = 1662465600000L; // 八分之一节点
    private static final long DAY20220906_21 = 1662469200000L;
    private static final long DAY20220906_22 = 1662472800000L;
    private static final long DAY20220906_23 = 1662476400000L;
    private static final long DAY20220906_24 = 1662480000000L;

    // 20220906日
    private static final long DAY20220907_00 = 1662480000000L; // 八分之一节点
    private static final long DAY20220907_01 = 1662483600000L;
    private static final long DAY20220907_02 = 1662487200000L;
    private static final long DAY20220907_03 = 1662490800000L;
    private static final long DAY20220907_04 = 1662494400000L;
    private static final long DAY20220907_05 = 1662498000000L;
    private static final long DAY20220907_06 = 1662501600000L;
    private static final long DAY20220907_07 = 1662505200000L;
    private static final long DAY20220907_08 = 1662508800000L; // 四分之一节点
    private static final long DAY20220907_09 = 1662512400000L;
    private static final long DAY20220907_10 = 1662516000000L;
    private static final long DAY20220907_11 = 1662519600000L;
    private static final long DAY20220907_12 = 1662523200000L;
    private static final long DAY20220907_13 = 1662526800000L;
    private static final long DAY20220907_14 = 1662530400000L;
    private static final long DAY20220907_15 = 1662534000000L;
    private static final long DAY20220907_16 = 1662537600000L; // 八分之一节点
    private static final long DAY20220907_17 = 1662541200000L;
    private static final long DAY20220907_18 = 1662544800000L;
    private static final long DAY20220907_19 = 1662548400000L;
    private static final long DAY20220907_20 = 1662552000000L;
    private static final long DAY20220907_21 = 1662555600000L;
    private static final long DAY20220907_22 = 1662559200000L;
    private static final long DAY20220907_23 = 1662562800000L;
    private static final long DAY20220907_24 = 1662566400000L;


    @Override
    public void invoke(String value, Context context) throws Exception {
        JSONObject valueObj = JSON.parseObject(value);
        // 获取cdc进入kafka的时间
        long tsNum = Long.parseLong(JsonPartUtil.getTsStr(valueObj));
        StringBuilder sb = new StringBuilder();
        // 1.先去看是否在要求取的时间范围内
        if (tsNum>= DAY20220905_00 && tsNum < DAY20220907_24){
            // 2.继续分
            // 2.1按二分之一节点去分
            if (tsNum <DAY20220906_12){
                if (tsNum < DAY20220905_18){
                    if (tsNum < DAY20220905_09){
                        if (tsNum >= DAY20220905_00 && tsNum < DAY20220905_01){ MysqlUtil.incrementRedis("20220905:00-01");}
                        if (tsNum >= DAY20220905_00 && tsNum < DAY20220905_01){ MysqlUtil.incrementRedis("20220905:00-01");}
                        if (tsNum >= DAY20220905_01 && tsNum < DAY20220905_02){ MysqlUtil.incrementRedis("20220905:01-02");}
                        if (tsNum >= DAY20220905_02 && tsNum < DAY20220905_03){ MysqlUtil.incrementRedis("20220905:02-03");}
                        if (tsNum >= DAY20220905_03 && tsNum < DAY20220905_04){ MysqlUtil.incrementRedis("20220905:03-04");}
                        if (tsNum >= DAY20220905_04 && tsNum < DAY20220905_05){ MysqlUtil.incrementRedis("20220905:04-05");}
                        if (tsNum >= DAY20220905_05 && tsNum < DAY20220905_06){ MysqlUtil.incrementRedis("20220905:05-06");}
                        if (tsNum >= DAY20220905_06 && tsNum < DAY20220905_07){ MysqlUtil.incrementRedis("20220905:06-07");}
                        if (tsNum >= DAY20220905_07 && tsNum < DAY20220905_08){ MysqlUtil.incrementRedis("20220905:07-08");}
                        if (tsNum >= DAY20220905_08 && tsNum < DAY20220905_09){ MysqlUtil.incrementRedis("20220905:08-09");}
                        // 八分之一节点分
                    }else if (tsNum >= DAY20220905_09){
                        if (tsNum >= DAY20220905_09 && tsNum < DAY20220905_10){ MysqlUtil.incrementRedis("20220905:09-10");}
                        if (tsNum >= DAY20220905_10 && tsNum < DAY20220905_11){ MysqlUtil.incrementRedis("20220905:10-11");}
                        if (tsNum >= DAY20220905_11 && tsNum < DAY20220905_12){ MysqlUtil.incrementRedis("20220905:11-12");}
                        if (tsNum >= DAY20220905_12 && tsNum < DAY20220905_13){ MysqlUtil.incrementRedis("20220905:12-13");}
                        if (tsNum >= DAY20220905_13 && tsNum < DAY20220905_14){ MysqlUtil.incrementRedis("20220905:13-14");}
                        if (tsNum >= DAY20220905_14 && tsNum < DAY20220905_15){ MysqlUtil.incrementRedis("20220905:14-15");}
                        if (tsNum >= DAY20220905_15 && tsNum < DAY20220905_16){ MysqlUtil.incrementRedis("20220905:15-16");}
                        if (tsNum >= DAY20220905_16 && tsNum < DAY20220905_17){ MysqlUtil.incrementRedis("20220905:16-17");}
                        if (tsNum >= DAY20220905_17 && tsNum < DAY20220905_18){ MysqlUtil.incrementRedis("20220905:17-18");}
                    }
                }else if(tsNum >= DAY20220905_18){
                    // 八分之一处
                    if (tsNum < DAY20220906_04){
                        if (tsNum >= DAY20220905_18 && tsNum < DAY20220905_19){ MysqlUtil.incrementRedis("20220905:18-19");}
                        if (tsNum >= DAY20220905_19 && tsNum < DAY20220905_20){ MysqlUtil.incrementRedis("20220905:19-20");}
                        if (tsNum >= DAY20220905_20 && tsNum < DAY20220905_21){ MysqlUtil.incrementRedis("20220905:20-21");}
                        if (tsNum >= DAY20220905_21 && tsNum < DAY20220905_22){ MysqlUtil.incrementRedis("20220905:21-22");}
                        if (tsNum >= DAY20220905_22 && tsNum < DAY20220905_23){ MysqlUtil.incrementRedis("20220905:22-23");}
                        if (tsNum >= DAY20220905_23 && tsNum < DAY20220905_24){ MysqlUtil.incrementRedis("20220905:23-24");}

                        if (tsNum >= DAY20220906_00 && tsNum < DAY20220906_01){ MysqlUtil.incrementRedis("20220906:00-01");}
                        if (tsNum >= DAY20220906_01 && tsNum < DAY20220906_02){ MysqlUtil.incrementRedis("20220906:01-02");}
                        if (tsNum >= DAY20220906_02 && tsNum < DAY20220906_03){ MysqlUtil.incrementRedis("20220906:02-03");}
                        if (tsNum >= DAY20220906_03 && tsNum < DAY20220906_04){ MysqlUtil.incrementRedis("20220906:03-04");}
                    }else if (tsNum >= DAY20220906_04){
                        if (tsNum >= DAY20220906_04 && tsNum < DAY20220906_05){ MysqlUtil.incrementRedis("20220906:04-05");}
                        if (tsNum >= DAY20220906_05 && tsNum < DAY20220906_06){ MysqlUtil.incrementRedis("20220906:05-06");}
                        if (tsNum >= DAY20220906_06 && tsNum < DAY20220906_07){ MysqlUtil.incrementRedis("20220906:06-07");}
                        if (tsNum >= DAY20220906_07 && tsNum < DAY20220906_08){ MysqlUtil.incrementRedis("20220906:07-08");}
                        if (tsNum >= DAY20220906_08 && tsNum < DAY20220906_09){ MysqlUtil.incrementRedis("20220906:08-09");}
                        if (tsNum >= DAY20220906_09 && tsNum < DAY20220906_10){ MysqlUtil.incrementRedis("20220906:09-10");}
                        if (tsNum >= DAY20220906_10 && tsNum < DAY20220906_11){ MysqlUtil.incrementRedis("20220906:10-11");}
                        if (tsNum >= DAY20220906_11 && tsNum < DAY20220906_12){ MysqlUtil.incrementRedis("20220906:11-12");}
                    }

                }
            }else if (tsNum >=DAY20220906_12){
                if (tsNum < DAY20220907_08){
                    //二分之一处
                    if (tsNum < DAY20220906_22){
                        if (tsNum >= DAY20220906_12 && tsNum < DAY20220906_13){ MysqlUtil.incrementRedis("20220906:12-13");}
                        if (tsNum >= DAY20220906_13 && tsNum < DAY20220906_14){ MysqlUtil.incrementRedis("20220906:13-14");}
                        if (tsNum >= DAY20220906_14 && tsNum < DAY20220906_15){ MysqlUtil.incrementRedis("20220906:14-15");}
                        if (tsNum >= DAY20220906_15 && tsNum < DAY20220906_16){ MysqlUtil.incrementRedis("20220906:15-16");}
                        if (tsNum >= DAY20220906_16 && tsNum < DAY20220906_17){ MysqlUtil.incrementRedis("20220906:16-17");}
                        if (tsNum >= DAY20220906_17 && tsNum < DAY20220906_18){ MysqlUtil.incrementRedis("20220906:17-18");}
                        if (tsNum >= DAY20220906_18 && tsNum < DAY20220906_19){ MysqlUtil.incrementRedis("20220906:18-19");}
                        if (tsNum >= DAY20220906_19 && tsNum < DAY20220906_20){ MysqlUtil.incrementRedis("20220906:19-20");}
                        if (tsNum >= DAY20220906_20 && tsNum < DAY20220906_21){ MysqlUtil.incrementRedis("20220906:20-21");}
                        if (tsNum >= DAY20220906_21 && tsNum < DAY20220906_22){ MysqlUtil.incrementRedis("20220906:21-22");}
                    }else if (tsNum >= DAY20220906_22){
                        if (tsNum >= DAY20220906_22 && tsNum < DAY20220906_23){ MysqlUtil.incrementRedis("20220906:22-23");}
                        if (tsNum >= DAY20220906_23 && tsNum < DAY20220906_24){ MysqlUtil.incrementRedis("20220906:23-24");}

                        if (tsNum >= DAY20220907_00 && tsNum < DAY20220907_01){ MysqlUtil.incrementRedis("20220907:00-01");}
                        if (tsNum >= DAY20220907_01 && tsNum < DAY20220907_02){ MysqlUtil.incrementRedis("20220907:01-02");}
                        if (tsNum >= DAY20220907_02 && tsNum < DAY20220907_03){ MysqlUtil.incrementRedis("20220907:02-03");}
                        if (tsNum >= DAY20220907_03 && tsNum < DAY20220907_04){ MysqlUtil.incrementRedis("20220907:03-04");}
                        if (tsNum >= DAY20220907_04 && tsNum < DAY20220907_05){ MysqlUtil.incrementRedis("20220907:04-05");}
                        if (tsNum >= DAY20220907_05 && tsNum < DAY20220907_06){ MysqlUtil.incrementRedis("20220907:05-06");}
                        if (tsNum >= DAY20220907_06 && tsNum < DAY20220907_07){ MysqlUtil.incrementRedis("20220907:06-07");}
                        if (tsNum >= DAY20220907_07 && tsNum < DAY20220907_08){ MysqlUtil.incrementRedis("20220907:07-08");}
                    }
                }else if(tsNum >= DAY20220907_08){
                    if (tsNum < DAY20220907_16){
                        if (tsNum >= DAY20220907_08 && tsNum < DAY20220907_09){ MysqlUtil.incrementRedis("20220907:08-09");}
                        if (tsNum >= DAY20220907_09 && tsNum < DAY20220907_10){ MysqlUtil.incrementRedis("20220907:09-10");}
                        if (tsNum >= DAY20220907_10 && tsNum < DAY20220907_11){ MysqlUtil.incrementRedis("20220907:10-11");}
                        if (tsNum >= DAY20220907_11 && tsNum < DAY20220907_12){ MysqlUtil.incrementRedis("20220907:11-12");}
                        if (tsNum >= DAY20220907_12 && tsNum < DAY20220907_13){ MysqlUtil.incrementRedis("20220907:12-13");}
                        if (tsNum >= DAY20220907_13 && tsNum < DAY20220907_14){ MysqlUtil.incrementRedis("20220907:13-14");}
                        if (tsNum >= DAY20220907_14 && tsNum < DAY20220907_15){ MysqlUtil.incrementRedis("20220907:14-15");}
                        if (tsNum >= DAY20220907_15 && tsNum < DAY20220907_16){ MysqlUtil.incrementRedis("20220907:15-16");}
                    }else if (tsNum >= DAY20220907_16){
                        if (tsNum >= DAY20220907_16 && tsNum < DAY20220907_17){ MysqlUtil.incrementRedis("20220907:16-17");}
                        if (tsNum >= DAY20220907_17 && tsNum < DAY20220907_18){ MysqlUtil.incrementRedis("20220907:17-18");}
                        if (tsNum >= DAY20220907_18 && tsNum < DAY20220907_19){ MysqlUtil.incrementRedis("20220907:18-19");}
                        if (tsNum >= DAY20220907_19 && tsNum < DAY20220907_20){ MysqlUtil.incrementRedis("20220907:19-20");}
                        if (tsNum >= DAY20220907_20 && tsNum < DAY20220907_21){ MysqlUtil.incrementRedis("20220907:20-21");}
                        if (tsNum >= DAY20220907_21 && tsNum < DAY20220907_22){ MysqlUtil.incrementRedis("20220907:21-22");}
                        if (tsNum >= DAY20220907_22 && tsNum < DAY20220907_23){ MysqlUtil.incrementRedis("20220907:22-23");}
                        if (tsNum >= DAY20220907_23 && tsNum < DAY20220907_24){ MysqlUtil.incrementRedis("20220907:23-24");}
                    }
                }
            }
        }else {
            MysqlUtil.incrementRedis("20220908_00-01");
        }

    }
}
