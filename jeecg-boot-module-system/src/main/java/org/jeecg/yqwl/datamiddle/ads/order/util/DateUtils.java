package org.jeecg.yqwl.datamiddle.ads.order.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtils {

    public static final String YYYYMMDD = "yyyy-MM-dd";


    public static final String YYYYMMDD_ZH = "yyyy年MM月dd日";

    public static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY;

    /**
     * @param strDate
     * @return
     */
    public static Date parseDate(String strDate) {
        return parseDate(strDate, null);
    }

    /**
     * 获取今日0点时间戳
     * @param
     * @author dabao
     * @date 2022/10/9
     * @return {@link Long}
     */
    public static Long getTodayStartTimestamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format = dateFormat.format(date);
        Date date1 = DateUtils.parseDate(format + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        return date1.getTime();
    }

    /**
     * parseDate
     *
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date parseDate(String strDate, String pattern) {
        Date date = null;
        try {
            if (pattern == null) {
                pattern = YYYYMMDD;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            date = format.parse(strDate);
        } catch (Exception e) {
            log.error("parseDate error:" + e);
        }
        return date;
    }

    /**
     * format date
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return formatDate(date, null);
    }

    /**
     * format date
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        String strDate = null;
        try {
            if (pattern == null) {
                pattern = YYYYMMDD;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            strDate = format.format(date);
        } catch (Exception e) {
            log.error("formatDate error:", e);
        }
        return strDate;
    }

    /**
     * 取得日期：年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 取得日期：月
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month + 1;
    }

    /**
     * 取得日期：天
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int da = c.get(Calendar.DAY_OF_MONTH);
        return da;
    }

    /**
     * 取得日期：天
     *
     * @param date
     * @return
     */
    public static int getDayOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date) + " 00:00:00","yyyy-MM-dd HH:mm:ss");
        c.setTime(parseDate);
        int da = c.get(Calendar.DAY_OF_YEAR);
        return da;
    }


    public static long getFirstDayOfYear(Date date){
        Calendar c = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date, "yyyy") + "-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        c.setTime(parseDate);
        return c.getTimeInMillis();
    }

    public static long getLastDayOfYear(Date date){
        Calendar c = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date, "yyyy") + "-12-31 23:59:59","yyyy-MM-dd HH:mm:ss");
        c.setTime(parseDate);
        return c.getTimeInMillis();
    }

    /**
     * 取得当天日期是周几
     *
     * @param date
     * @return
     */
    public static int getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week_of_year = c.get(Calendar.DAY_OF_WEEK);
        return week_of_year - 1;
    }

    /**
     * 取得一年的第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week_of_year = c.get(Calendar.WEEK_OF_YEAR);
        return week_of_year;
    }


    /**
     * 取得一年的第几周
     *
     * @param dateStr
     * @return
     */
    public static int getWeekOfYear(String dateStr) {
        Date date = parseDate(dateStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week_of_year = c.get(Calendar.WEEK_OF_YEAR);
        return week_of_year;
    }

    /**
     * getWeekBeginAndEndDate
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getWeekBeginAndEndDate(Date date, String pattern) {
        Date monday = getMondayOfWeek(date);
        Date sunday = getSundayOfWeek(date);
        return formatDate(monday, pattern) + " - "
                + formatDate(sunday, pattern);
    }

    /**
     * 根据日期取得对应周周一日期
     *
     * @param date
     * @return
     */
    public static Date getMondayOfWeek(Date date) {
        Calendar monday = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date) + " 00:00:00","yyyy-MM-dd HH:mm:ss");
        monday.setTime(parseDate);
        monday.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return monday.getTime();
    }

    /**
     * 根据日期取得对应周周日日期
     *
     * @param date
     * @return
     */
    public static Date getSundayOfWeek(Date date) {
        Calendar sunday = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date) + " 23:59:59","yyyy-MM-dd HH:mm:ss");
        sunday.setTime(parseDate);
        sunday.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return sunday.getTime();
    }

    /**
     * 取得月的剩余天数
     *
     * @param date
     * @return
     */
    public static int getRemainDayOfMonth(Date date) {
        int dayOfMonth = getDayOfMonth(date);
        int day = getPassDayOfMonth(date);
        return dayOfMonth - day;
    }

    /**
     * 取得月已经过的天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得月天数
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得月第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date) + " 00:00:00","yyyy-MM-dd HH:mm:ss");
        c.setTime(parseDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * 取得月最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        Date parseDate = parseDate(formatDate(date) + " 23:59:59","yyyy-MM-dd HH:mm:ss");
        c.setTime(parseDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * 取得季度第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDateOfSeason(Date date) {
        return getFirstDateOfMonth(getSeasonDate(date)[0]);
    }

    /**
     * 取得季度最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDateOfSeason(Date date) {
        return getLastDateOfMonth(getSeasonDate(date)[2]);
    }

    /**
     * 取得季度天数
     *
     * @param date
     * @return
     */
    public static int getDayOfSeason(Date date) {
        int day = 0;
        Date[] seasonDates = getSeasonDate(date);
        for (Date date2 : seasonDates) {
            day += getDayOfMonth(date2);
        }
        return day;
    }

    /**
     * 取得季度剩余天数
     *
     * @param date
     * @return
     */
    public static int getRemainDayOfSeason(Date date) {
        return getDayOfSeason(date) - getPassDayOfSeason(date);
    }

    /**
     * 取得季度已过天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfSeason(Date date) {
        int day = 0;

        Date[] seasonDates = getSeasonDate(date);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);

        if (month == Calendar.JANUARY || month == Calendar.APRIL
                || month == Calendar.JULY || month == Calendar.OCTOBER) {// 季度第一个月
            day = getPassDayOfMonth(seasonDates[0]);
        } else if (month == Calendar.FEBRUARY || month == Calendar.MAY
                || month == Calendar.AUGUST || month == Calendar.NOVEMBER) {// 季度第二个月
            day = getDayOfMonth(seasonDates[0])
                    + getPassDayOfMonth(seasonDates[1]);
        } else if (month == Calendar.MARCH || month == Calendar.JUNE
                || month == Calendar.SEPTEMBER || month == Calendar.DECEMBER) {// 季度第三个月
            day = getDayOfMonth(seasonDates[0]) + getDayOfMonth(seasonDates[1])
                    + getPassDayOfMonth(seasonDates[2]);
        }
        return day;
    }

    /**
     * 取得季度月
     *
     * @param date
     * @return
     */
    public static Date[] getSeasonDate(Date date) {
        Date[] season = new Date[3];

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int nSeason = getSeason(date);
        if (nSeason == 1) {// 第一季度
            c.set(Calendar.MONTH, Calendar.JANUARY);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.FEBRUARY);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.MARCH);
            season[2] = c.getTime();
        } else if (nSeason == 2) {// 第二季度
            c.set(Calendar.MONTH, Calendar.APRIL);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.MAY);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.JUNE);
            season[2] = c.getTime();
        } else if (nSeason == 3) {// 第三季度
            c.set(Calendar.MONTH, Calendar.JULY);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.AUGUST);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.SEPTEMBER);
            season[2] = c.getTime();
        } else if (nSeason == 4) {// 第四季度
            c.set(Calendar.MONTH, Calendar.OCTOBER);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.NOVEMBER);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.DECEMBER);
            season[2] = c.getTime();
        }
        return season;
    }

    /**
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static int getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

    public static Integer getDateTime(String date, String dateTime) {
        switch (dateTime) {
            case "D":
                return getDayOfYear(parseDate(date));
            case "W":
                return getWeekOfYear(parseDate(date));
            case "M":
                return getMonth(parseDate(date));
            case "Q":
                return getSeason(parseDate(date));
        }
        return 0;
    }

    public static String getVersion(Date date) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(formatDate(date, "yyyy")).append("Q").append(getSeason(date)).append("-")
                .append(formatDate(date, "MMddHHmmss"));
        return stringBuffer.toString();

    }

    public static String coverZonedDateTime(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDD);
        LocalDateTime d = LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofLocal(d, ZoneId.systemDefault(), null);
        return simpleDateFormat.format(Date.from(zonedDateTime.toInstant()));
    }

    public static int dayComparePrecise(String day1, String day2) {
        Period period = Period.between(LocalDate.parse(day1), LocalDate.parse(day2));
        return period.getYears();
    }

    //根据起始时间和结束时间，生成Q1、Q2、Q3、Q4季度
    public static String getPeriod(String beginTime, String finishTime) {
        if (StringUtils.isEmpty(beginTime) || StringUtils.isEmpty(finishTime)) {
            return null;
        }
        String period = "";
        String[] seasonAry = new String[]{"Q1", "Q2", "Q3", "Q4"};
        Date beginYear = parseDate(beginTime, "yyyy");
        Date finishYear = parseDate(finishTime, "yyyy");
        int beginTimeSeason = getSeason(parseDate(beginTime));
        int finishTimeSeason = getSeason(parseDate(finishTime));
        // 同一年
        if (beginYear.equals(finishYear)) {
            for (int i = 0; i < seasonAry.length; i++) {
                if (i + 1 >= beginTimeSeason && i + 1 <= finishTimeSeason) {
                    if (StringUtils.isEmpty(period)) {
                        period = seasonAry[i];
                        continue;
                    }
                    period = period + "," + seasonAry[i];
                }
            }
        } else {
            // 不同年份的
            int differYear = dayComparePrecise(beginTime, finishTime);
            for (int i = 0; i <= differYear; i++) {
                for (int i1 = 0; i1 < seasonAry.length; i1++) {
                    if ((i == 0 && i1 + 1 < beginTimeSeason) || i == differYear && i1 + 1 > finishTimeSeason) {
                        continue;
                    }
                    if (StringUtils.isEmpty(period)) {
                        period = seasonAry[i1];
                    } else {
                        period = period + "," + seasonAry[i1];
                    }
                }
            }
        }
        return period;
    }


}
