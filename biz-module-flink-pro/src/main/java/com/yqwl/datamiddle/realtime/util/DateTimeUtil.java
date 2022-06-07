package com.yqwl.datamiddle.realtime.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
        System.out.println(ZoneId.systemDefault());
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
}
