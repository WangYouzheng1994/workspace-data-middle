/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.test.cdc.oracle.bean.element.column;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jeecgframework.boot.DateUtil;
import org.test.cdc.oracle.bean.element.AbstractBaseColumn;
import org.test.cdc.oracle.bean.element.ClassSizeUtil;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Date: 2021/04/26 Company: www.dtstack.com
 *
 * @author tudou
 */
public class StringColumn extends AbstractBaseColumn {

    private String format = "yyyy-MM-dd HH:mm:ss";
    private boolean isCustomFormat = false;

    public StringColumn(final String data) {
        super(data, 0);
        byteSize += ClassSizeUtil.getStringSize(data);
    }

    public StringColumn(final String data, String format) {
        super(data, 0);
        if (StringUtils.isNotBlank(format)) {
            this.format = format;
            isCustomFormat = true;
        }
        byteSize += ClassSizeUtil.getStringSize(data);
    }

    public StringColumn(String data, String format, boolean isCustomFormat, int byteSize) {
        super(data, byteSize);
        this.format = format;
        this.isCustomFormat = isCustomFormat;
    }

    public static StringColumn from(final String data, String format, boolean isCustomFormat) {
        return new StringColumn(data, format, isCustomFormat, 0);
    }

    public StringColumn(Byte aByte) {
        super(aByte, 0);
        byteSize += 1;
    }

    @Override
    public String asString() {
        if (null == data) {
            return null;
        }
        if (isCustomFormat) {
            return asTimestampStr();
        } else {
            return String.valueOf(data);
        }
    }

    @Override
    public Date asDate() {
        if (null == data) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Long time = null;
        Date result = null;
        String data = String.valueOf(this.data);
        try {
            // 如果string是时间戳
            time = NumberUtils.createLong(data);
        } catch (Exception ignored) {
            // doNothing
        }
        if (time != null) {
            Date date = new Date(time);
            try {
                result = dateFormat.parse(dateFormat.format(date));
            } catch (ParseException ignored) {
                // doNothing
            }
        } else {
            try {
                // 如果是日期格式字符串
                result = dateFormat.parse(data);
            } catch (ParseException ignored) {
                // doNothing
            }
        }

        if (result == null) {
            result = DateUtil.columnToDate(data, dateFormat);

            if (result == null) {
                throw new RuntimeException();
                // throw new CastException("String", "Date", data);
            }
        }

        return result;
    }

    @Override
    public byte[] asBytes() {
        if (null == data) {
            return null;
        }
        return ((String) data).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Boolean asBoolean() {
        if (null == data) {
            return null;
        }

        String data = String.valueOf(this.data);
        // 如果是数值类型
        try {
            return NumberUtils.toInt(data) != 0;
        } catch (Exception ignored) {
            // doNothing
        }

        if ("true".equalsIgnoreCase(data)) {
            return true;
        }

        if ("false".equalsIgnoreCase(data)) {
            return false;
        }

        throw new RuntimeException();
        // throw new CastException("String", "Boolean", data);
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (null == data) {
            return null;
        }
        String data = String.valueOf(this.data);
        this.validateDoubleSpecific(data);

        try {
            return new BigDecimal(data);
        } catch (Exception e) {
            throw new RuntimeException();
            // throw new CastException("String", "BigDecimal", data);
        }
    }

    @Override
    public Double asDouble() {
        if (null == data) {
            return null;
        }

        String data = String.valueOf(this.data);
        if ("NaN".equals(data)) {
            return Double.NaN;
        }

        if ("Infinity".equals(data)) {
            return Double.POSITIVE_INFINITY;
        }

        if ("-Infinity".equals(data)) {
            return Double.NEGATIVE_INFINITY;
        }

        return super.asDouble();
    }

    @Override
    public Timestamp asTimestamp() {
        if (null == data) {
            return null;
        }
        try {
            return new Timestamp(this.asDate().getTime());
        } catch (Exception e) {
            throw new RuntimeException();
            // throw new CastException("String", "Timestamp", (String) data);
        }
    }

    private void validateDoubleSpecific(final String data) {
        if ("NaN".equals(data) || "Infinity".equals(data) || "-Infinity".equals(data)) {
            throw new RuntimeException();
            /*throw new CastException(
                    String.format(
                            "String[%s]belongs to the special type of Double and cannot be converted to other types.",
                            data));*/
        }
    }

    @Override
    public Time asTime() {
        if (null == data) {
            return null;
        }
        return new Time(asTimestamp().getTime());
    }

    @Override
    public java.sql.Date asSqlDate() {
        if (null == data) {
            return null;
        }
        return java.sql.Date.valueOf(asTimestamp().toLocalDateTime().toLocalDate());
    }

    @Override
    public String asTimestampStr() {
        if (null == data) {
            return null;
        }
        SimpleDateFormat dateFormat = DateUtil.buildDateFormatter(format);
        String data = String.valueOf(this.data);
        try {
            // 如果string是时间戳
            Long time = NumberUtils.createLong(data);
            return dateFormat.format(time);
        } catch (Exception ignored) {
            // doNothing
        }

        try {
            if (isCustomFormat) {
                // 格式化
                return dateFormat.format(asDate().getTime());
            } else {
                // 校验格式
                DateUtil.stringToDate(data);
                return data;
            }
        } catch (Exception e) {
            throw new RuntimeException();
            // throw new CastException("String", "Timestamp", data);
        }
    }

    public boolean isCustomFormat() {
        return isCustomFormat;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
