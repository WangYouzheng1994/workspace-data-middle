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

package org.jeecg.yqwl.datamiddle.config.datasource.controller;


import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.config.datasource.entity.DatasourceConfig;
import org.jeecg.yqwl.datamiddle.config.datasource.service.DataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DataBaseController
 * 元数据管理
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
@RestController
@RequestMapping("/api/database")
public class DataBaseController {
    @Autowired
    private DataBaseService databaseService;
    private static Logger logger = LoggerFactory.getLogger(DataBaseController.class);

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody DatasourceConfig database) {
        database = databaseService.getById(database.getId());
        return Result.OK( "获取成功",database);
    }

    /**
     * 获取可用的集群列表
     */
    @GetMapping("/listEnabledAll")
    public Result listEnabledAll() {
        List<DatasourceConfig> dataBases = databaseService.listEnabledAll();
        return Result.OK( "获取成功",dataBases);
    }

    /**
     * 获取元数据的表
     */
    @GetMapping("/getSchemasAndTables")
    public Result getSchemasAndTables(@RequestParam Integer id) {
        return Result.OK( "获取成功",databaseService.getSchemasAndTables(id));
    }

    /**
     * 获取元数据的指定表的列
     */
    @GetMapping("/listColumns")
    public Result listColumns(@RequestParam Integer id, @RequestParam String schemaName, @RequestParam String tableName) {
        return Result.OK( "获取成功",databaseService.listColumns(id, schemaName, tableName));
    }


    /**
     * 获取 SqlGeneration
     */
    /*@GetMapping("/getSqlGeneration")
    public Result getSqlGeneration(@RequestParam Integer id, @RequestParam String schemaName, @RequestParam String tableName) {
        return Result.succeed(databaseService.getSqlGeneration(id, schemaName, tableName), "获取成功");
    }*/
}
