package com.yqwl.datamiddle.realtime.util;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;
import org.apache.kafka.connect.data.SchemaBuilder;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: oracle cdc debezium 转换回调
 * @Author: xiaofeng
 * @Date: 2022年7月15日15:51:21
 * @Version: V1.0
 */
public class OracleTimeConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    private static final ZoneId GMT_ZONE_ID = ZoneId.systemDefault();

    private static final Pattern TO_DATE = Pattern.compile("TO_DATE\\('(.*)',[ ]*'(.*)'\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TO_TIMESTAMP = Pattern.compile("TO_TIMESTAMP\\('(.*)'\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIMESTAMP_OR_DATE_REGEX = Pattern.compile("^TIMESTAMP[(]\\d[)]$|^DATE$", Pattern.CASE_INSENSITIVE);

    private static final DateTimeFormatter TIMESTAMP_AM_PM_SHORT_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("dd-MMM-yy hh.mm.ss")
            .optionalStart()
            .appendPattern(".")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false)
            .optionalEnd()
            .appendPattern(" a")
            .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendPattern(".")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false)
            .optionalEnd()
            .toFormatter();

    @Override
    public void configure(Properties props) {

    }

    @Override
    public void converterFor(RelationalColumn column, ConverterRegistration<SchemaBuilder> registration) {
        String typeName = column.typeName();
        if (TIMESTAMP_OR_DATE_REGEX.matcher(typeName).matches()) {
            registration.register(SchemaBuilder.int64().optional(), value -> {
                if (value == null) {
                    if (column.isOptional()) {
                        return null;
                    } else if (column.hasDefaultValue()) {
                        return column.defaultValue();
                    } else {
                        return null;
                    }
                }

                if (value instanceof Long) {
                    return value;
                }
                if (value instanceof String) {
                    Instant instant = resolveTimestampStringAsInstant((String) value);
                    if (instant != null) {
                        return instant.toEpochMilli();
                    }
                }

                return null;
            });
        }
    }

    private Instant resolveTimestampStringAsInstant(String data) {
        LocalDateTime dateTime;

        final Matcher toTimestampMatcher = TO_TIMESTAMP.matcher(data);
        if (toTimestampMatcher.matches()) {
            String dateText = toTimestampMatcher.group(1);
            if (dateText.indexOf(" AM") > 0 || dateText.indexOf(" PM") > 0) {
                dateTime = LocalDateTime.from(TIMESTAMP_AM_PM_SHORT_FORMATTER.parse(dateText.trim()));
            } else {
                dateTime = LocalDateTime.from(TIMESTAMP_FORMATTER.parse(dateText.trim()));
            }
            return dateTime.atZone(GMT_ZONE_ID).toInstant();
        }

        final Matcher toDateMatcher = TO_DATE.matcher(data);
        if (toDateMatcher.matches()) {
            dateTime = LocalDateTime.from(TIMESTAMP_FORMATTER.parse(toDateMatcher.group(1)));
            return dateTime.atZone(GMT_ZONE_ID).toInstant();
        }

        // 解析失败
        return null;
    }
}

