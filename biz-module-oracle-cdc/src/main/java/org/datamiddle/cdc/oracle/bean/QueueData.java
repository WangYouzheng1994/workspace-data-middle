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
package org.datamiddle.cdc.oracle.bean;


import org.apache.flink.table.data.RowData;

import java.math.BigInteger;

/**
 * 队列中的数据 scn表示此数据的偏移量， rowdata为行数据，后续需要进行去flink化
 * @author dujie
 */
public class QueueData {
    private final BigInteger scn;
    private final RowData data;
    private final String rsId;

    public QueueData(BigInteger lsn, RowData data,String rsId) {
        this.scn = lsn;
        this.data = data;
        this.rsId =rsId;
    }
    public String getRsId(){
        return rsId;
    }
    public BigInteger getScn() {
        return scn;
    }

    public RowData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "QueueData{" + "scn=" + scn + ", data=" + data + '}';
    }
}
