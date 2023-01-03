package com.yqwl.datamiddle.realtime.util;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.SchemaBuilder;

public class TimestampConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    private static final Map<String, String> MONTH_MAP;/* = Map.ofEntries(Map.entry("jan", "01"), Map.entry("feb", "02"),
                Map.entry("mar", "03"), Map.entry("apr", "04"), Map.entry("may", "05"), Map.entry("jun", "06"),
                Map.entry("jul", "07"), Map.entry("aug", "08"), Map.entry("sep", "09"), Map.entry("oct", "10"),
                Map.entry("nov", "11"), Map.entry("dec", "12"));*/
    public static final int MILLIS_LENGTH = 13;

    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DEFAULT_DATE_FORMAT = "YYYY-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.SSS";

    public static final List<String> SUPPORTED_DATA_TYPES; /*= List.of("date", "time", "datetime", "timestamp",
            "datetime2");*/

    static {
        MONTH_MAP = new HashMap<>();
        MONTH_MAP.put("jan", "01");
        MONTH_MAP.put("feb", "02");
        MONTH_MAP.put("mar", "03");
        MONTH_MAP.put("apr", "04");
        MONTH_MAP.put("may", "05");
        MONTH_MAP.put("jul", "06");
        MONTH_MAP.put("aug", "07");
        MONTH_MAP.put("jun", "08");
        MONTH_MAP.put("sep", "09");
        MONTH_MAP.put("oct", "10");
        MONTH_MAP.put("nov", "11");
        MONTH_MAP.put("dec", "12");

        SUPPORTED_DATA_TYPES = new ArrayList<>();
        SUPPORTED_DATA_TYPES.add("date");
        SUPPORTED_DATA_TYPES.add("time");
        SUPPORTED_DATA_TYPES.add("datetime");
        SUPPORTED_DATA_TYPES.add("timestamp");
        SUPPORTED_DATA_TYPES.add("datetime2");
    }

    private static final String DATETIME_REGEX = "(?<datetime>(?<date>(?:(?<year>\\d{4})-(?<month>\\d{1,2})-(?<day>\\d{1,2}))|(?:(?<day2>\\d{1,2})\\/(?<month2>\\d{1,2})\\/(?<year2>\\d{4}))|(?:(?<day3>\\d{1,2})-(?<month3>\\w{3})-(?<year3>\\d{4})))?(?:\\s?T?(?<time>(?<hour>\\d{1,2}):(?<minute>\\d{1,2}):(?<second>\\d{1,2})\\.?(?<milli>\\d{0,7})?)?))";
    private static final Pattern regexPattern = Pattern.compile(DATETIME_REGEX);

    public String strDatetimeFormat, strDateFormat, strTimeFormat;
    public Boolean debug;

//    private final SchemaBuilder datetimeSchema = SchemaBuilder.string().optional().name("oryanmoshe.time.DateTimeString");

    private SimpleDateFormat simpleDatetimeFormatter, simpleDateFormatter, simpleTimeFormatter;

    @Override
    public void configure(Properties props) {
        this.strDatetimeFormat = props.getProperty("format.datetime", DEFAULT_DATETIME_FORMAT);
        this.simpleDatetimeFormatter = new SimpleDateFormat(this.strDatetimeFormat);

        this.strDateFormat = props.getProperty("format.date", DEFAULT_DATE_FORMAT);
        this.simpleDateFormatter = new SimpleDateFormat(this.strDateFormat);

        this.strTimeFormat = props.getProperty("format.time", DEFAULT_TIME_FORMAT);
        this.simpleTimeFormatter = new SimpleDateFormat(this.strTimeFormat);

        this.debug = "true".equals(props.getProperty("debug", "false"));

        this.simpleDatetimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.simpleTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (this.debug)
            System.out.printf(
                    "[TimestampConverter.configure] Finished configuring formats. this.strDatetimeFormat: %s, this.strTimeFormat: %s%n",
                    this.strDatetimeFormat, this.strTimeFormat);
    }

    @Override
    public void converterFor(RelationalColumn column, ConverterRegistration<SchemaBuilder> registration) {
        if (this.debug)
            System.out.printf(
                    "[TimestampConverter.converterFor] Starting to register column. column.name: %s, column.typeName: %s, column.hasDefaultValue: %s, column.defaultValue: %s, column.isOptional: %s%n",
                    column.name(), column.typeName(), column.hasDefaultValue(), column.defaultValue(), column.isOptional());
        if (SUPPORTED_DATA_TYPES.stream().anyMatch(s -> StringUtils.startsWithIgnoreCase(column.typeName(), s))) {
            boolean isTime = "time".equalsIgnoreCase(column.typeName());
            // Use a new SchemaBuilder every time in order to avoid changing "Already set" options
            // in the schema builder between tables.
            // 增量: TO_TIMESTAMP('2022-08-04 16:52:28.')
            registration.register(SchemaBuilder.string().optional(), rawValue -> {
                if (rawValue == null) {
                    // DEBUG
                    if (this.debug) {
                        System.out.printf("[TimestampConverter.converterFor] rawValue of %s is null.%n", column.name());
                    }

                    if (column.isOptional()) {
                        return null;
                    }
                    else if (column.hasDefaultValue()) {
                        return column.defaultValue();
                    }
                    return rawValue;
                }
                String rawValueStr = rawValue.toString();
                Integer numLength = rawValueStr.length();
                // 当前已支持版本 :
                // 2022-06-23 01:42:18.0   LENGTH: 21
                // TO_TIMESTAMP('2022-08-04 09:19:14.600131').length              : 42
                // TO_DATE('2022-08-03 22:18:18', 'YYYY-MM-DD HH24:MI:SS').length : 55
                //
                if (numLength > 33 && numLength < 50){
                    String rawValueSubStr = rawValueStr.substring(rawValueStr.indexOf("'") + 1, rawValueStr.lastIndexOf("'"));
                    Integer rawValueSubStrLength = rawValueSubStr.length();
                    if (rawValueSubStrLength == 20){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                    if (rawValueSubStrLength == 21){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                    if (rawValueSubStrLength == 22){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                    if (rawValueSubStrLength == 23){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                    if (rawValueSubStrLength == 24){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                    if (rawValueSubStrLength == 25){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                    if (rawValueSubStrLength == 26){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }

                }else if(numLength >=50){
                    String rawValueSubStr50 = rawValueStr.substring(rawValueStr.indexOf("'") + 1, rawValueStr.indexOf("'")+20);
                    Integer rawValueSubStrLength50 = rawValueSubStr50.length();

                    if (rawValueSubStrLength50 == 19){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String timeValues = String.valueOf(LocalDateTime.parse(rawValueSubStr50, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (timeValues != null){
                            return timeValues;
                        }
                    }
                }else {
                    if (numLength ==19){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String s = String.valueOf(LocalDateTime.parse(rawValueStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    if (numLength ==20){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.");
                        String s = String.valueOf(LocalDateTime.parse(rawValueStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    if (numLength ==21){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                        String s = String.valueOf(LocalDateTime.parse(rawValueStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    if (numLength ==22){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
                        String s = String.valueOf(LocalDateTime.parse(rawValueStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    if (numLength ==23){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                        String s = String.valueOf(LocalDateTime.parse(rawValueStr, df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    // 2022-07-15 15:27:06.0103   LENGTH: 24
                    if (numLength ==24){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                        String s = String.valueOf(LocalDateTime.parse(rawValue.toString(), df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    if (numLength ==25){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS");
                        String s = String.valueOf(LocalDateTime.parse(rawValue.toString(), df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                    // 2022-07-14 11:36:44.893154   LENGTH: 26
                    if (numLength ==26){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                        String s = String.valueOf(LocalDateTime.parse(rawValue.toString(), df).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                        if (s != null){
                            return s;
                        }
                    }
                }


                Long millis = getMillis(rawValue.toString(), isTime);
                if (millis == null)
                    return rawValue.toString();

                Instant instant = Instant.ofEpochMilli(millis);
                Date dateObject = Date.from(instant);
                if (this.debug)
                    System.out.printf(
                            "[TimestampConverter.converterFor] Before returning conversion. column.name: %s, column.typeName: %s, millis: %d%n",
                            column.name(), column.typeName(), millis);
                switch (column.typeName().toLowerCase()) {
                    case "time":
                        return this.simpleTimeFormatter.format(dateObject);
                    case "date":
                        return this.simpleDateFormatter.format(dateObject);
                    default:
                        return this.simpleDatetimeFormatter.format(dateObject);
                }
            });
        }
    }

    private Long getMillis(String timestamp, boolean isTime) {
        if (StringUtils.isNotBlank(timestamp))
            return null;

        if (timestamp.contains(":") || timestamp.contains("-")) {
            return milliFromDateString(timestamp);
        }

        int excessLength = timestamp.length() - MILLIS_LENGTH;
        long longTimestamp = Long.parseLong(timestamp);

        if (isTime)
            return longTimestamp;

        if (excessLength < 0)
            return longTimestamp * 24 * 60 * 60 * 1000;

        long millis = longTimestamp / (long) Math.pow(10, excessLength);
        return millis;
    }

    private Long milliFromDateString(String timestamp) {
        Matcher matches = regexPattern.matcher(timestamp);

        if (matches.find()) {
            String year = (matches.group("year") != null ? matches.group("year")
                    : (matches.group("year2") != null ? matches.group("year2") : matches.group("year3")));
            String month = (matches.group("month") != null ? matches.group("month")
                    : (matches.group("month2") != null ? matches.group("month2") : matches.group("month3")));
            String day = (matches.group("day") != null ? matches.group("day")
                    : (matches.group("day2") != null ? matches.group("day2") : matches.group("day3")));
            String hour = matches.group("hour") != null ? matches.group("hour") : "00";
            String minute = matches.group("minute") != null ? matches.group("minute") : "00";
            String second = matches.group("second") != null ? matches.group("second") : "00";
            String milli = matches.group("milli") != null ? matches.group("milli") : "000";

            if (milli.length() > 3)
                milli = milli.substring(0, 3);

            String dateStr = "";
            dateStr += String.format("%s:%s:%s.%s", ("00".substring(hour.length()) + hour),
                    ("00".substring(minute.length()) + minute), ("00".substring(second.length()) + second),
                    (milli + "000".substring(milli.length())));

            if (year != null) {
                if (month.length() > 2)
                    month = MONTH_MAP.get(month.toLowerCase());

                dateStr = String.format("%s-%s-%sT%sZ", year, ("00".substring(month.length()) + month),
                        ("00".substring(day.length()) + day), dateStr);
            } else {
                dateStr = String.format("%s-%s-%sT%sZ", "2020", "01", "01", dateStr);
            }

            Date dateObj = Date.from(Instant.parse(dateStr));
            return dateObj.getTime();
        }

        return null;
    }
}

