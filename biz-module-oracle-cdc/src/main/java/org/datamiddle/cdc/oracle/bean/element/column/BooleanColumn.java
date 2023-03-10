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
package org.datamiddle.cdc.oracle.bean.element.column;


import org.datamiddle.cdc.oracle.bean.element.AbstractBaseColumn;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Date: 2021/04/27 Company: www.dtstack.com
 *
 * @author tudou
 */
public class BooleanColumn extends AbstractBaseColumn {

    public BooleanColumn(boolean data) {
        super(data, 1);
    }

    public BooleanColumn(boolean data, int byteSize) {
        super(data, byteSize);
    }

    public static BooleanColumn from(boolean data) {
        return new BooleanColumn(data, 0);
    }

    @Override
    public Boolean asBoolean() {
        if (null == data) {
            return null;
        }
        return (boolean) data;
    }

    @Override
    public byte[] asBytes() {
        if (null == data) {
            return null;
        }
        // throw new CastException("boolean", "Bytes", this.asString());
        throw new RuntimeException();
    }

    @Override
    public String asString() {
        if (null == data) {
            return null;
        }
        return (boolean) data ? "true" : "false";
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (null == data) {
            return null;
        }
        return BigDecimal.valueOf((boolean) data ? 1L : 0L);
    }

    @Override
    public Timestamp asTimestamp() {
        if (null == data) {
            return null;
        }
        // throw new CastException("boolean", "Timestamp", this.asString());
        throw new RuntimeException();
    }

    @Override
    public Time asTime() {
        if (null == data) {
            return null;
        }
        // throw new CastException("boolean", "java.sql.Time", this.asString());
        throw new RuntimeException();
    }

    @Override
    public Date asSqlDate() {
        if (null == data) {
            return null;
        }
        // throw new CastException("boolean", "java.sql.Date", this.asString());
        throw new RuntimeException();
    }

    @Override
    public String asTimestampStr() {
        // throw new CastException("boolean", "Timestamp", this.asString());
        throw new RuntimeException();
    }
}
