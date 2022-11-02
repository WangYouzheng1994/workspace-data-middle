package com.yqwl.datamiddle.realtime.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Felix
 * Date: 2021/2/20
 * Desc:  日期转换的工具类
 * SimpleDateFormat存在线程安全问题,底层调用 calendar.setTime(date);
 * 解决：在JDK8，提供了DateTimeFormatter替代SimpleDateFormat
 */
public class DateTimeUtil {
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        //System.out.println(System.currentTimeMillis());
        //System.out.println(timestampRound(System.currentTimeMillis()));
        //System.out.println(ZoneId.systemDefault());

        Date date = new Date();
        System.out.println(date);
        System.out.println(getDateAddDays(date.getTime(), 2));



    }

    /**
     * 将Date日期转换为字符串
     *
     * @return
     */
    public static String toYMDhms(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }

    /**
     * 将字符串日期转换为时间毫秒数
     *
     * @param dateStr
     * @return
     */
    public static Long toTs(String dateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dtf);
        long ts = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return ts;
    }


    public static String timeStampToDateStr(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date timeStamp2Date(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        try {
            return sdf.parse(sdf.format(timeLong));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date timeStamp2Date(long timeLong) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        try {
            return sdf.parse(sdf.format(timeLong));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * localdatetime转时间戳
     * @param time 参数
     * @return Long 时间戳
     */
    public static Long LocalDateTimeToTimestamp(LocalDateTime time){
        return time.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * 获取每个小时的时间戳
     * @param startTimestamp 开始时间的时间戳
     * @param hourNum 获取多少个小时
     * @author dabao
     * @date 2022/9/8
     * @return {@link List< Long>}
     */
    public static List<Long> getTimeStampEveryHouse(Long startTimestamp, Integer hourNum){
        //取小时整数
        Long startTime = timestampRound(startTimestamp);
        BigDecimal oneHouse = BigDecimal.valueOf(3600000L);
        BigDecimal start = BigDecimal.valueOf(startTime);
        List<Long> timestampList = new ArrayList<>();
        timestampList.add(startTime);
        for (int i = 1; i <= hourNum; i++) {
            BigDecimal addend = oneHouse.multiply(BigDecimal.valueOf(i)).setScale(0, BigDecimal.ROUND_HALF_UP);
            timestampList.add(start.add(addend).setScale(0).longValue());
        }
        return timestampList;
    }

    /**
     * 取正小时的时间戳
     * @param time 时间戳参数
     * @author dabao
     * @date 2022/9/14
     * @return {@link Long}
     */
    public static Long timestampRound(Long time){
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd HH");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date(time);
        Date newDate = null;
        try {
            newDate = formatter.parse(dfDate.format(date) + ":00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return newDate.getTime();
    }

    /**
     * 时间戳转日期字符串
     * @param time 时间戳
     * @author dabao
     * @date 2022/9/8
     * @return {@link String}
     */
    public static String timestampToDate(Long time){
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(time);
        return dfDate.format(date);
    }

    /**
     * 时间戳转小时字符串
     * @param time 时间戳
     * @author dabao
     * @date 2022/9/8
     * @return {@link String}
     */
    public static String timestampToHour(Long time){
        SimpleDateFormat dfDate = new SimpleDateFormat("HH");
        Date date = new Date(time);
        return dfDate.format(date);
    }


    /**
     * 获取两个时间戳之间最小的一个
     *
     * @param time1
     * @param time2
     * @return
     */
    public static Long getLeastDate(Long time1, Long time2) {
        Long differVal = time1 - time2;
        if (differVal > 0) {
            return time2;
        } else if (differVal < 0) {
            return time1;
        } else {
            return time1;
        }
    }


    /**
     * 对某个时间增加几天
     * @param time
     * @param days
     * @return
     */
    public static Long getDateAddDays(Long time, int days){
        Date outSiteDate = new Date(time);
        // 格式化时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = formatter.format(outSiteDate);
        DateTime parse = DateUtil.parse(formatDate);
        DateTime newStepTime = DateUtil.offsetDay(parse, days);
        return newStepTime.getTime();

    }


}
