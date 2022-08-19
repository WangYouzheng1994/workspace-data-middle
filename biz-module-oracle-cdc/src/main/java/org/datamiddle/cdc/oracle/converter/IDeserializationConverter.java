/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datamiddle.cdc.oracle.converter;

import org.apache.flink.table.data.RowData;

import java.io.Serializable;

/**
 * 转换成flink type的解析转换器顶级接口，因为只有一个方法，所以这也是一个函数式接口，flinkx中 使用此接口通过lambda进行实现。
 *
 *
 * @param <T>
 * @param <E>
 */
public interface IDeserializationConverter<T, E> extends Serializable {

    /**
     * Runtime converter to convert field to {@link RowData} type object
     *
     * @param field
     * @return
     * @throws Exception
     */
    E deserialize(T field) throws Exception;
}
