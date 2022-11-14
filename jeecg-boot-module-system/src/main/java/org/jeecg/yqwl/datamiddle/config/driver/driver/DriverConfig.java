/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.jeecg.yqwl.datamiddle.config.driver.driver;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.common.exception.JeecgBootException;

import java.util.Map;

/**
 * DriverConfig
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
@Getter
@Setter
public class DriverConfig {

    private String name;
    private String type;
    private String driverClassName;
    private String ip;
    private Integer port;
    private String url;
    private String username;
    private String password;

    public DriverConfig() {
    }

    public DriverConfig(String name, String type, String url, String username, String password) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DriverConfig build(Map<String, String> confMap) {
        checkNull(confMap, "数据源配置不能为空");
        return new DriverConfig(confMap.get("name"), confMap.get("type"), confMap.get("url"), confMap.get("username")
            , confMap.get("password"));
    }
    public static void checkNull(Object key, String msg) {
        if (key == null) {
            throw new JeecgBootException(msg);
        }
    }
}
