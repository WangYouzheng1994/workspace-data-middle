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

import java.util.List;

/**
 * 表元数据，通过jdbc查询后，用于log日志的明文转换
 *
 * @author dujie
 */
public class TableMetaData {

    /**
     * schema名称 可以认为是库名
     */
    private final String SchemaName;
    /**
     * 表名
     */
    private final String tableName;
    /**
     * 列
     */
    private final List<String> fieldList;
    /** field type * */
    private final List<String> typeList;

    public TableMetaData(
            String schemaName, String tableName, List<String> fieldList, List<String> typeList) {
        SchemaName = schemaName;
        this.tableName = tableName;
        this.fieldList = fieldList;
        this.typeList = typeList;
    }

    public String getSchemaName() {
        return SchemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    @Override
    public String toString() {
        return "TableMetaData{"
                + "SchemaName='"
                + SchemaName
                + '\''
                + ", tableName='"
                + tableName
                + '\''
                + ", columns="
                + fieldList
                + ", types="
                + typeList
                + '}';
    }
}
