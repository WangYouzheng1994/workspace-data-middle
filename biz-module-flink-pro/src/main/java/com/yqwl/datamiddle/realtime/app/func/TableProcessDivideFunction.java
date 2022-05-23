package com.yqwl.datamiddle.realtime.app.func;

import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
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

/**
 * @Description: 消费kafka中的数据将表进行分流处理
 * @Author: muqing
 * @Date: 2022/05/07
 * @Version: V1.0
 */
@Slf4j
public class TableProcessDivideFunction extends ProcessFunction<JSONObject, JSONObject> {

    //因为要将维度数据写到侧输出流，所以定义一个侧输出流标签
    private OutputTag<JSONObject> outputTag;

    public TableProcessDivideFunction(OutputTag<JSONObject> outputTag) {
        this.outputTag = outputTag;
    }

    //用于在内存中存储表配置对象 [表名,[表配置信息]]
    private Map<String, List<TableProcess>> tableProcessMap = new ConcurrentHashMap<>();
    //表示目前内存中已经存在的要放入mysql中的表
    private Set<String> existsTables = new HashSet<>();


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
        }, 5000, 5000);
    }

    /**
     * 初始化分流配置集合
     */
    private void initTableProcessMap() {
        log.info("更新配置的处理信息");
        //查询 MySQL 中的配置表数据
        List<TableProcess> tableProcessList = MysqlUtil.queryList("select * from table_process order by id", TableProcess.class, true);
        //遍历查询结果,将数据存入结果集合
        for (TableProcess tableProcess : tableProcessList) {
            log.info("输出分流配置表中数据:{}", tableProcess.toString());
            //获取源表表名
            String sourceTable = tableProcess.getSourceTable();
            //获取数据操作类型
            String operateType = tableProcess.getOperateType();
            //获取结果表表名
            String sinkTable = tableProcess.getSinkTable();
            //获取 sink 类型
            String sinkType = tableProcess.getSinkType();
            //拼接字段创建主键
            String key = sourceTable + ":" + operateType;
            //将数据存入结果集合
            if (tableProcessMap.containsKey(key)) {
                tableProcessMap.get(key).add(tableProcess);
            } else {
                List<TableProcess> tableProcessItemlist = new ArrayList<>();
                tableProcessItemlist.add(tableProcess);
                tableProcessMap.put(key, tableProcessItemlist);
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
        //获取表名
        String tableName = JsonPartUtil.getTableNameStr(jsonObj);
        String lowerTableName = StringUtils.toRootLowerCase(tableName);
        //获取操作类型
        String type = JsonPartUtil.getTypeStr(jsonObj);
        //获取配置表的信息
        if (MapUtils.isNotEmpty(tableProcessMap)) {
            //将源表和操作类型组合成key, 例如：key=MDAC32:insert
            String key = StringUtils.joinWith(":", lowerTableName, type);
            List<TableProcess> tableProcesses = tableProcessMap.get(key);
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
                        Object afterObj = JsonPartUtil.getAfterObj(jsonObj, aClass);
                        Object bean = JsonPartUtil.getBean(afterObj);
                        jsonObj.put("after", bean);
                        ctx.output(outputTag, jsonObj);

                        // 如果是写到kafka的 那么直接写入到kafka中
                    } else if (TableProcess.SINK_TYPE_KAFKA.equalsIgnoreCase(tableProcess.getSinkType().trim())) {
                        out.collect(jsonObj);
                    }
                }
            } else {
                log.info("No This Key: {}", key);
            }

        }
    }
}
