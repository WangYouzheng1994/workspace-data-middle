package com.yqwl.datamiddle.realtime.app.func;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.util.DateTimeUtil;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.*;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/9/7 20:51
 * @Version: V1.0
 */
@Slf4j
public class SimpleHourOfDayUpdateTotalSink<T> extends RichSinkFunction<String> {

    private static Map<Long, String> keyMap = new HashMap<>();

    private final static List<Long> timestampEveryHouse = new ArrayList<>();

    @Override
    public void invoke(String value, Context context) throws Exception {
        JSONObject valueObj = JSON.parseObject(value);
        // 获取cdc进入kafka的时间
        long tsNum = Long.parseLong(JsonPartUtil.getTsStr(valueObj));
        //1662480000000L 20220907日00点 时间戳 获取
        if (CollectionUtils.isEmpty(timestampEveryHouse)){
            //第一次捕获数据时，将第一次cdc进入kafka的时间作为开始时间
            timestampEveryHouse.addAll(DateTimeUtil.getTimeStampEveryHouse(tsNum, 72));
        }
        // 1.先去看是否在要求取的时间范围内
        if (tsNum>= timestampEveryHouse.get(0) && tsNum < timestampEveryHouse.get(timestampEveryHouse.size() - 1)){
            //二分法比对
            dichotomy(tsNum, timestampEveryHouse);
            //初版代码 ↓↓
            // 2.继续分
            // 2.1按二分之一节点去分
//            if (tsNum <DAY20220906_12){
//                if (tsNum < DAY20220905_18){
//                    if (tsNum < DAY20220905_09){
//                        if (tsNum >= DAY20220905_00 && tsNum < DAY20220905_01){ MysqlUtil.incrementRedis("20220905:00-01");}//是不是重复了？
//                        if (tsNum >= DAY20220905_00 && tsNum < DAY20220905_01){ MysqlUtil.incrementRedis("20220905:00-01");}
//                        if (tsNum >= DAY20220905_01 && tsNum < DAY20220905_02){ MysqlUtil.incrementRedis("20220905:01-02");}
//                        if (tsNum >= DAY20220905_02 && tsNum < DAY20220905_03){ MysqlUtil.incrementRedis("20220905:02-03");}
//                        if (tsNum >= DAY20220905_03 && tsNum < DAY20220905_04){ MysqlUtil.incrementRedis("20220905:03-04");}
//                        if (tsNum >= DAY20220905_04 && tsNum < DAY20220905_05){ MysqlUtil.incrementRedis("20220905:04-05");}
//                        if (tsNum >= DAY20220905_05 && tsNum < DAY20220905_06){ MysqlUtil.incrementRedis("20220905:05-06");}
//                        if (tsNum >= DAY20220905_06 && tsNum < DAY20220905_07){ MysqlUtil.incrementRedis("20220905:06-07");}
//                        if (tsNum >= DAY20220905_07 && tsNum < DAY20220905_08){ MysqlUtil.incrementRedis("20220905:07-08");}
//                        if (tsNum >= DAY20220905_08 && tsNum < DAY20220905_09){ MysqlUtil.incrementRedis("20220905:08-09");}
//                        // 八分之一节点分
//                    }else if (tsNum >= DAY20220905_09){
//                        if (tsNum >= DAY20220905_09 && tsNum < DAY20220905_10){ MysqlUtil.incrementRedis("20220905:09-10");}
//                        if (tsNum >= DAY20220905_10 && tsNum < DAY20220905_11){ MysqlUtil.incrementRedis("20220905:10-11");}
//                        if (tsNum >= DAY20220905_11 && tsNum < DAY20220905_12){ MysqlUtil.incrementRedis("20220905:11-12");}
//                        if (tsNum >= DAY20220905_12 && tsNum < DAY20220905_13){ MysqlUtil.incrementRedis("20220905:12-13");}
//                        if (tsNum >= DAY20220905_13 && tsNum < DAY20220905_14){ MysqlUtil.incrementRedis("20220905:13-14");}
//                        if (tsNum >= DAY20220905_14 && tsNum < DAY20220905_15){ MysqlUtil.incrementRedis("20220905:14-15");}
//                        if (tsNum >= DAY20220905_15 && tsNum < DAY20220905_16){ MysqlUtil.incrementRedis("20220905:15-16");}
//                        if (tsNum >= DAY20220905_16 && tsNum < DAY20220905_17){ MysqlUtil.incrementRedis("20220905:16-17");}
//                        if (tsNum >= DAY20220905_17 && tsNum < DAY20220905_18){ MysqlUtil.incrementRedis("20220905:17-18");}
//                    }
//                }else if(tsNum >= DAY20220905_18){
//                    // 八分之一处
//                    if (tsNum < DAY20220906_04){
//                        if (tsNum >= DAY20220905_18 && tsNum < DAY20220905_19){ MysqlUtil.incrementRedis("20220905:18-19");}
//                        if (tsNum >= DAY20220905_19 && tsNum < DAY20220905_20){ MysqlUtil.incrementRedis("20220905:19-20");}
//                        if (tsNum >= DAY20220905_20 && tsNum < DAY20220905_21){ MysqlUtil.incrementRedis("20220905:20-21");}
//                        if (tsNum >= DAY20220905_21 && tsNum < DAY20220905_22){ MysqlUtil.incrementRedis("20220905:21-22");}
//                        if (tsNum >= DAY20220905_22 && tsNum < DAY20220905_23){ MysqlUtil.incrementRedis("20220905:22-23");}
//                        if (tsNum >= DAY20220905_23 && tsNum < DAY20220905_24){ MysqlUtil.incrementRedis("20220905:23-24");}
//
//                        if (tsNum >= DAY20220906_00 && tsNum < DAY20220906_01){ MysqlUtil.incrementRedis("20220906:00-01");}
//                        if (tsNum >= DAY20220906_01 && tsNum < DAY20220906_02){ MysqlUtil.incrementRedis("20220906:01-02");}
//                        if (tsNum >= DAY20220906_02 && tsNum < DAY20220906_03){ MysqlUtil.incrementRedis("20220906:02-03");}
//                        if (tsNum >= DAY20220906_03 && tsNum < DAY20220906_04){ MysqlUtil.incrementRedis("20220906:03-04");}
//                    }else if (tsNum >= DAY20220906_04){
//                        if (tsNum >= DAY20220906_04 && tsNum < DAY20220906_05){ MysqlUtil.incrementRedis("20220906:04-05");}
//                        if (tsNum >= DAY20220906_05 && tsNum < DAY20220906_06){ MysqlUtil.incrementRedis("20220906:05-06");}
//                        if (tsNum >= DAY20220906_06 && tsNum < DAY20220906_07){ MysqlUtil.incrementRedis("20220906:06-07");}
//                        if (tsNum >= DAY20220906_07 && tsNum < DAY20220906_08){ MysqlUtil.incrementRedis("20220906:07-08");}
//                        if (tsNum >= DAY20220906_08 && tsNum < DAY20220906_09){ MysqlUtil.incrementRedis("20220906:08-09");}
//                        if (tsNum >= DAY20220906_09 && tsNum < DAY20220906_10){ MysqlUtil.incrementRedis("20220906:09-10");}
//                        if (tsNum >= DAY20220906_10 && tsNum < DAY20220906_11){ MysqlUtil.incrementRedis("20220906:10-11");}
//                        if (tsNum >= DAY20220906_11 && tsNum < DAY20220906_12){ MysqlUtil.incrementRedis("20220906:11-12");}
//                    }
//
//                }
//            }else if (tsNum >=DAY20220906_12){
//                if (tsNum < DAY20220907_08){
//                    //二分之一处
//                    if (tsNum < DAY20220906_22){
//                        if (tsNum >= DAY20220906_12 && tsNum < DAY20220906_13){ MysqlUtil.incrementRedis("20220906:12-13");}
//                        if (tsNum >= DAY20220906_13 && tsNum < DAY20220906_14){ MysqlUtil.incrementRedis("20220906:13-14");}
//                        if (tsNum >= DAY20220906_14 && tsNum < DAY20220906_15){ MysqlUtil.incrementRedis("20220906:14-15");}
//                        if (tsNum >= DAY20220906_15 && tsNum < DAY20220906_16){ MysqlUtil.incrementRedis("20220906:15-16");}
//                        if (tsNum >= DAY20220906_16 && tsNum < DAY20220906_17){ MysqlUtil.incrementRedis("20220906:16-17");}
//                        if (tsNum >= DAY20220906_17 && tsNum < DAY20220906_18){ MysqlUtil.incrementRedis("20220906:17-18");}
//                        if (tsNum >= DAY20220906_18 && tsNum < DAY20220906_19){ MysqlUtil.incrementRedis("20220906:18-19");}
//                        if (tsNum >= DAY20220906_19 && tsNum < DAY20220906_20){ MysqlUtil.incrementRedis("20220906:19-20");}
//                        if (tsNum >= DAY20220906_20 && tsNum < DAY20220906_21){ MysqlUtil.incrementRedis("20220906:20-21");}
//                        if (tsNum >= DAY20220906_21 && tsNum < DAY20220906_22){ MysqlUtil.incrementRedis("20220906:21-22");}
//                    }else if (tsNum >= DAY20220906_22){
//                        if (tsNum >= DAY20220906_22 && tsNum < DAY20220906_23){ MysqlUtil.incrementRedis("20220906:22-23");}
//                        if (tsNum >= DAY20220906_23 && tsNum < DAY20220906_24){ MysqlUtil.incrementRedis("20220906:23-24");}
//
//                        if (tsNum >= DAY20220907_00 && tsNum < DAY20220907_01){ MysqlUtil.incrementRedis("20220907:00-01");}
//                        if (tsNum >= DAY20220907_01 && tsNum < DAY20220907_02){ MysqlUtil.incrementRedis("20220907:01-02");}
//                        if (tsNum >= DAY20220907_02 && tsNum < DAY20220907_03){ MysqlUtil.incrementRedis("20220907:02-03");}
//                        if (tsNum >= DAY20220907_03 && tsNum < DAY20220907_04){ MysqlUtil.incrementRedis("20220907:03-04");}
//                        if (tsNum >= DAY20220907_04 && tsNum < DAY20220907_05){ MysqlUtil.incrementRedis("20220907:04-05");}
//                        if (tsNum >= DAY20220907_05 && tsNum < DAY20220907_06){ MysqlUtil.incrementRedis("20220907:05-06");}
//                        if (tsNum >= DAY20220907_06 && tsNum < DAY20220907_07){ MysqlUtil.incrementRedis("20220907:06-07");}
//                        if (tsNum >= DAY20220907_07 && tsNum < DAY20220907_08){ MysqlUtil.incrementRedis("20220907:07-08");}
//                    }
//                }else if(tsNum >= DAY20220907_08){
//                    if (tsNum < DAY20220907_16){
//                        if (tsNum >= DAY20220907_08 && tsNum < DAY20220907_09){ MysqlUtil.incrementRedis("20220907:08-09");}
//                        if (tsNum >= DAY20220907_09 && tsNum < DAY20220907_10){ MysqlUtil.incrementRedis("20220907:09-10");}
//                        if (tsNum >= DAY20220907_10 && tsNum < DAY20220907_11){ MysqlUtil.incrementRedis("20220907:10-11");}
//                        if (tsNum >= DAY20220907_11 && tsNum < DAY20220907_12){ MysqlUtil.incrementRedis("20220907:11-12");}
//                        if (tsNum >= DAY20220907_12 && tsNum < DAY20220907_13){ MysqlUtil.incrementRedis("20220907:12-13");}
//                        if (tsNum >= DAY20220907_13 && tsNum < DAY20220907_14){ MysqlUtil.incrementRedis("20220907:13-14");}
//                        if (tsNum >= DAY20220907_14 && tsNum < DAY20220907_15){ MysqlUtil.incrementRedis("20220907:14-15");}
//                        if (tsNum >= DAY20220907_15 && tsNum < DAY20220907_16){ MysqlUtil.incrementRedis("20220907:15-16");}
//                    }else if (tsNum >= DAY20220907_16){
//                        if (tsNum >= DAY20220907_16 && tsNum < DAY20220907_17){ MysqlUtil.incrementRedis("20220907:16-17");}
//                        if (tsNum >= DAY20220907_17 && tsNum < DAY20220907_18){ MysqlUtil.incrementRedis("20220907:17-18");}
//                        if (tsNum >= DAY20220907_18 && tsNum < DAY20220907_19){ MysqlUtil.incrementRedis("20220907:18-19");}
//                        if (tsNum >= DAY20220907_19 && tsNum < DAY20220907_20){ MysqlUtil.incrementRedis("20220907:19-20");}
//                        if (tsNum >= DAY20220907_20 && tsNum < DAY20220907_21){ MysqlUtil.incrementRedis("20220907:20-21");}
//                        if (tsNum >= DAY20220907_21 && tsNum < DAY20220907_22){ MysqlUtil.incrementRedis("20220907:21-22");}
//                        if (tsNum >= DAY20220907_22 && tsNum < DAY20220907_23){ MysqlUtil.incrementRedis("20220907:22-23");}
//                        if (tsNum >= DAY20220907_23 && tsNum < DAY20220907_24){ MysqlUtil.incrementRedis("20220907:23-24");}
//                    }
//                }
//            }
        }

    }

    /**
     * 判断是否在时间段内，并将本次记录存到redis中
     * @param startTime 区间开始时间
     * @param endTime 区间结束时间
     * @param tsNum 任务时间
     * @author dabao
     * @date 2022/9/8
     * @return {@link boolean}
     */
    private boolean judgementAndIncrementRedis(Long startTime, Long endTime, Long tsNum){

        if (tsNum >= startTime && tsNum < endTime){
            if (Objects.isNull(keyMap.get(startTime))){
                //构造一个时间字符串作为redis的key  并且存放到map中，以防重复创建相同的key导致new很多的date
                String date = DateTimeUtil.timestampToDate(startTime);
                String startHour = DateTimeUtil.timestampToHour(startTime);
                String endHour = DateTimeUtil.timestampToHour(endTime);
                keyMap.put(startTime, date+":" + startHour + "-" +endHour);
            }
            MysqlUtil.incrementRedis(keyMap.get(startTime));
            return true;
        }
        return false;
    }

    /**
     * 二分法判断在哪个时间段内
     * @param tsNum 任务时间
     * @param timestampEveryHouse 每个小时的时间戳
     * @author dabao
     * @date 2022/9/8
     * @return
     */
    private void dichotomy(Long tsNum, List<Long> timestampEveryHouse){
        //列表最多分三次
        for(int i = 0; i < 3; i++) {
            //列表是否可分
            if (timestampEveryHouse.size() % 2 != 0 ) {
                i = 3;
                for (int j = 0; j < timestampEveryHouse.size() - 1; j++) {
                    boolean flag = judgementAndIncrementRedis(timestampEveryHouse.get(j), timestampEveryHouse.get(j + 1), tsNum);
                    if (flag){
                        return;
                    }
                }
            }

            //分列对比中值
            if (tsNum < timestampEveryHouse.get(timestampEveryHouse.size()/2)) {
                timestampEveryHouse = timestampEveryHouse.subList(0, timestampEveryHouse.size()/2);
            }
            else {
                timestampEveryHouse = timestampEveryHouse.subList(timestampEveryHouse.size()/2-1, timestampEveryHouse.size());
            }

        }
        //分完三次后 对比
        for (int j = 0; j < timestampEveryHouse.size() - 1; j++) {
            boolean flag = judgementAndIncrementRedis(timestampEveryHouse.get(j), timestampEveryHouse.get(j + 1), tsNum);
            if (flag){
                return;
            }
        }

    }
}
