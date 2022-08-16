package org.jeecg.yqwl.datamiddle.util;


import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.yqwl.datamiddle.util.custom.GetterUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/9/6 9:22
 * @Version: V1.0
 */
@Slf4j
public class DateUtils {


    /**
     * Long类型时间转为{@link Date}<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     */
    public static Date longToDate(long date) {
        return new Date(date);
    }

    /**
     * 获取当前时间Date
     *
     * @return java.util.Date
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static Long nowLong() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间Date String
     *
     * @return java.util.Date
     */
    public static Date nowStr() {
        return new Date("");
    }

    /**
     * 获取当前时间Date
     *
     * @return java.util.Date
     */
    public static Date nowStr(EnumDateStyle dateStyle) {
//        return LocalDate.parse(dateStyle.getValue());
        return new Date();
    }

    /**
     * 字符串日期转 数字时间戳
     * @param dateStr
     * @param enumDateStyle
     * @return
     */
    public static Long dateStrToLong10(String dateStr, String enumDateStyle) {
        long lTime = 0;
        if (StringUtils.isNoneBlank(dateStr, enumDateStyle)) {
            SimpleDateFormat sdf = new SimpleDateFormat(enumDateStyle);
            Date date = null;
            try {
                date = sdf.parse(enumDateStyle);
                // 继续转换得到秒数的long型
                lTime = date.getTime() / 1000;
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
        return lTime;
    }

    /**
     * date 转 10位数字时间戳
     * @param date
     * @return
     */
    public static Long dateToLong10(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * date 转 13位数字时间戳
     * @param date
     * @return
     */
    public static Long dateToLong13(Date date) {
        return date.getTime();
    }

    /**
     * Long类型时间转为{@link DateTime}<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     */
    public static DateTime date(long date) {
        return new DateTime(date);
    }

    /**
     * 计算两个时间戳差值
     * 大金
     * start开始时间 位数：10
     * end结束时间戳 位数：10
     * @param start
     * @param end
     * @return
     */
    public static Map<String, Long> ComputationalCost(long start, long end){
        try {
            Map<String, Long> map=new HashMap();
            long diff=end-start;
            long days= diff / (SEC * MINUTE * HOUR);
            long hours= (diff - days * (SEC * MINUTE * HOUR)) / (SEC * MINUTE);
            long minutes = (diff - days * (SEC * MINUTE * HOUR) - hours * (SEC * MINUTE)) / (SEC);
            map.put("days",days);
            map.put("hours",hours);
            map.put("minutes",minutes);
            return map;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 时间格式定义
     */
    public static enum EnumDateStyle {
        YYYYMM("yyyyMM"),
        YYYYMMDD("yyyyMMdd"),

        MM_DD("MM-dd"),
        YYYY_MM("yyyy-MM"),
        YYYY_MM_DD("yyyy-MM-dd"),
        MM_DD_HH_MM("MM-dd HH:mm"),
        MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),
        YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),

        MM_DD_EN("MM/dd"),
        YYYY_MM_EN("yyyy/MM"),
        YYYY_MM_DD_EN("yyyy/MM/dd"),
        MM_DD_HH_MM_EN("MM/dd HH:mm"),
        MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss"),
        YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm"),
        YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss"),

        MM_DD_CN("MM月dd日"),
        YYYY_MM_CN("yyyy年MM月"),
        YYYY_MM_DD_CN("yyyy年MM月dd日"),
        MM_DD_HH_MM_CN("MM月dd日 HH:mm"),
        MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss"),
        YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm"),
        YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss"),

        HH_MM("HH:mm"),
        HH_MM_SS("HH:mm:ss");

        private String value;

        EnumDateStyle(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum EnumWeek {
        MONDAY("星期一", "Monday", "Mon.", 1), TUESDAY("星期二", "Tuesday", "Tues.", 2), WEDNESDAY("星期三", "Wednesday", "Wed.", 3), THURSDAY("星期四", "Thursday", "Thur.", 4), FRIDAY("星期五", "Friday", "Fri.", 5), SATURDAY("星期六", "Saturday", "Sat.",
                6), SUNDAY("星期日", "Sunday", "Sun.", 7);

        String name_cn;
        String name_en;
        String name_enShort;
        int number;

        EnumWeek(String name_cn, String name_en, String name_enShort, int number) {
            this.name_cn = name_cn;
            this.name_en = name_en;
            this.name_enShort = name_enShort;
            this.number = number;
        }

        public String getChineseName() {
            return name_cn;
        }

        public String getName() {
            return name_en;
        }

        public String getShortName() {
            return name_enShort;
        }

        public int getNumber() {
            return number;
        }
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前10位时间戳
     *
     * @return
     */
    public static Long getNowTime10() {
        return getNowDate().getTime() / 1000;
    }
    /**
     * 获取指定日期所在月份月末的时间
     * dajin
     * @param year
     * @param month
     * @return
     */
    public static Long getMonthEnd(int year,int month){
        Calendar c = Calendar.getInstance();
        c.set(year,month,1,23,59,59);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Long l=(c.getTimeInMillis()/1000);
        return l;
    }

    /**
     * 获取指定日期所在月份月初的时间
     * dajin
     * @param year
     * @param month
     * @return
     */
    public static Long getMonthBegin(int year,int month) {
        Calendar c = Calendar.getInstance();
        month=month-1;
        c.set(year,month,1,0,0,0);
        Long l=(c.getTimeInMillis()/1000);
        return l;
    }

    /**
     * 格式化当前时间成年月日
     *
     * @return
     */
    public static String getNowDateStr() {
        return getNowDateStr("yyyy-MM-dd");
    }

    public static String getNowDateStr(String parttern) {
        SimpleDateFormat format = new SimpleDateFormat(parttern);
        return format.format(getNowDate());
    }


    public static String getNowDateStr(EnumDateStyle parttern) {
        SimpleDateFormat format = new SimpleDateFormat(parttern.value);
        return format.format(getNowDate());
    }

    /**
     * 分钟转换成小时 min / hour 取整数部分，不足一小时的为0，大于一小时不足两小时的为1
     *
     * @return
     */
    public static int minToHour(Integer min) {
        return min / 60;
    }

    /**
     * 计算时间差
     * bigger - smaller
     * type: 1 天；2 小时； 3分钟; 4 秒。  默认是1，返回相差的天数
     *
     * @param bigger
     * @param smaller
     * @return
     */
    public static Long getDiffDay(Date bigger, Date smaller, Integer type) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = bigger.getTime() - smaller.getTime();
        diff = Math.abs(diff);
        if (type != null) {
            switch (type) {
                // 天
                case 1:
                    return diff / nd;
                // 小时
                case 2:
                    return diff % nd / nh;
                // 分钟
                case 3:
                    return diff % nd % nh / nm;
                // 秒
                case 4:
                    return diff % nd % nh % nm / ns;
                default:
                    return diff / nd;
            }
        } else {
            return diff / nd;
        }
    }


    /**
     * 获取当天0点0分0秒 13位时间戳
     *
     * @return
     */
    public static long getTodayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Date getTodayBeginDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天的00：00：00时间
     *
     * @return
     */
    public static String getToday4Dawn() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return sdf.format(new Date());
    }

    public static String getToday4Night() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        return sdf.format(new Date());
    }

    public static Date getMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String stampTOStr(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(GetterUtil.getLong(timestamp)));
    }

    public static String stampTOStr(String timestamp, String partten) {
        SimpleDateFormat sdf = new SimpleDateFormat(partten);
        return sdf.format(new Date(GetterUtil.getLong(timestamp)));
    }

    public static String dateFormat(Date date, String parttern) {
        SimpleDateFormat sdf = new SimpleDateFormat(parttern);
        return sdf.format(date);
    }

    public static String dateFormat(Date now) {
        return dateFormat(now, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 字符串转时间戳
     *
     * @return
     */
    public static long strToLong(String str, String parttern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parttern);
        try {
            Date parse = simpleDateFormat.parse(str);
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 字符串转10位时间戳
     *
     * @return
     */
    public static long strToTenLong(String str, String parttern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parttern);
        try {
            Date parse = simpleDateFormat.parse(str);
            return parse.getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Date> getMonthFromNow(int diff) {
        return null;
    }

    /**
     * 从当前月份开始 前推 月份 获取到所有的月份
     *
     * @param diff 前推多少个月
     * @return
     */
    public static List<String> getAllMonthStrFromNow(int diff, boolean includeNow) {
        List<String> list = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(DateUtils.getMonthStart());

        int count = 0;
        if (includeNow) {
            count++;
            list.add(simpleDateFormat.format(calBegin.getTime()));
        }
        while (count < diff) {
            calBegin.add(Calendar.MONTH, -1);
            list.add(simpleDateFormat.format(calBegin.getTime()));
            count++;
        }

        return list;
    }

    public List<Date> getDateWithRange(Date begin, Date end) {
        List<Date> list = new ArrayList<>();
        list.add(begin);
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(begin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        while (end.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            list.add(calBegin.getTime());
        }
        return list;
    }

    /**
     * 毫秒
     * 一秒有1000毫秒
     */
    public static final Integer MSEL = 1000;
    /**
     * 秒
     * 一分钟60秒
     */
    public static final Integer SEC = 60;
    /**
     * 分
     * 一小时60分
     */
    public static final Integer MINUTE = 60;
    /**
     * 时
     * 一天24小时
     */
    public static final Integer HOUR = 24;
}
