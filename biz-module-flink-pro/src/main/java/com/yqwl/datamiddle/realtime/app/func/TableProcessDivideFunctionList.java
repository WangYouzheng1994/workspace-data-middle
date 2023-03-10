package com.yqwl.datamiddle.realtime.app.func;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: 消费kafka中的数据将表进行分流处理
 * @Author: muqing
 * @Date: 2022/05/07
 * @Version: V1.0
 */
@Slf4j
public class TableProcessDivideFunctionList extends ProcessFunction<JSONObject, JSONObject> {

    //因为要将维度数据写到侧输出流，所以定义一个侧输出流标签
    private OutputTag<JSONObject> outputTag;

    public TableProcessDivideFunctionList(OutputTag<JSONObject> outputTag) {
        this.outputTag = outputTag;
    }

    //用于在内存中存储表配置对象 [表名,[表配置信息]]
    private final Map<String, CopyOnWriteArraySet<TableProcess>> tableProcessMap = new ConcurrentHashMap<>();


    @Override
    public void open(Configuration parameters) throws Exception {
        //初始化配置表信息
        initTableProcessMap();
        //开启定时任务,用于不断读取配置表信息 从现在起过 delay 毫秒以后，每隔 period 更新一次
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                initTableProcessMap();
            }
        }, 5000, 20000);
    }

    /**
     * 初始化分流配置集合
     */
    private void initTableProcessMap() {
        log.debug("更新配置的处理信息");
        //查询 MySQL 中的配置表数据
        List<TableProcess> tableProcessList = MysqlUtil.queryList("select * from table_process where is_use = 1 order by id", TableProcess.class, true);
        //遍历查询结果,将数据存入结果集合
        for (TableProcess tableProcess : tableProcessList) {
            log.debug("输出分流配置表中数据:{}", tableProcess.toString());
            //获取源表表名
            String sourceTable = tableProcess.getSourceTable();
            String sourceTableLow = StringUtils.toRootLowerCase(sourceTable);
            //获取数据操作类型
            String operateType = tableProcess.getOperateType();
            //拼接字段创建主键
            String key = sourceTableLow + ":" + operateType;
            //将数据存入结果集合
            if (tableProcessMap.containsKey(key)) {
                CopyOnWriteArraySet<TableProcess> tableProcesses = tableProcessMap.get(key);
                tableProcesses.add(tableProcess);
            } else {
                CopyOnWriteArraySet<TableProcess> tableProcessItemList = new CopyOnWriteArraySet<>();
                tableProcessItemList.add(tableProcess);
                tableProcessMap.put(key, tableProcessItemList);
            }
        }
        if (MapUtils.isEmpty(tableProcessMap)) {
            log.error("读取分流配置表异常");
            throw new RuntimeException("读取分流配置表异常");
        }
    }

    /**
     * Process one element from the input stream.
     */
    @Override
    public void processElement(JSONObject jsonObj, Context ctx, Collector<JSONObject> out) throws Exception {
        if (jsonObj != null) {
            //获取表名
            String tableName = JsonPartUtil.getTableNameStr(jsonObj);
            //将表名置为小写
            String lowerTableName = StringUtils.toRootLowerCase(tableName);
            //获取操作类型
            String type = JsonPartUtil.getTypeStr(jsonObj);
            //获取配置表的信息
            if (MapUtils.isNotEmpty(tableProcessMap)) {
                //将源表和操作类型组合成key, 例如：key=MDAC32:insert
                String key = StringUtils.joinWith(":", lowerTableName, type);
                CopyOnWriteArraySet<TableProcess> tableProcesses = tableProcessMap.get(key);
                if (CollectionUtils.isNotEmpty(tableProcesses)) {
                    for (TableProcess tableProcess : tableProcesses) {
                        //将sink的表添加到当前流记录中
                        jsonObj.put("sink_table", tableProcess.getSinkTable());
                        jsonObj.put("sink_pk", tableProcess.getSinkPk());

                        //比对sinkType, 如果是写到mysql，打上标签
                        if (TableProcess.SINK_TYPE_MYSQL.equalsIgnoreCase(tableProcess.getSinkType().trim())) {
                            // 如果是写到mysql的 那么把这个数据和outputTag标签绑定
                            // 单条处理
                            // 对数据转换成实体类,对默认值进行赋值
                            Class<?> aClass = Class.forName(tableProcess.getClassName());
                            //System.err.println("Class类实例:{}" + aClass);
                            //log.info("Class类实例:{}", aClass);
                            //获取after真实数据后，映射为实体类
                            Object afterObj = JsonPartUtil.getAfterObj(jsonObj, aClass);
                            //System.err.println("反射后映射的实体类:" + afterObj);
                            //log.info("反射后的实例:{}", afterObj);
                            //对映射后的实体类为null字段
                            Object bean = JsonPartUtil.getBean(afterObj);
                            Object o = JSON.toJSON(bean);
                            //log.info("实体赋值默认值后数据:{}", o);
                            jsonObj.put("after", o);
                            ctx.output(outputTag, jsonObj);
                            aClass = null;
                            afterObj = null;
                            bean = null;
                            // 如果是写到kafka的 那么直接写入到kafka中
                        } else if (TableProcess.SINK_TYPE_KAFKA.equalsIgnoreCase(tableProcess.getSinkType().trim())) {
                            out.collect(jsonObj);
                        }
                    }
                } else {
                    log.warn("No This Key: {}", key);
                }
            }
        }
    }
}
