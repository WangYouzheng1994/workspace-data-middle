package com.yqwl.datamiddle.realtime.app.dwm.sinktoch.app;

import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import com.yqwl.datamiddle.realtime.util.ClickhouseDruidUtil;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description: 动态根据 分流 去 clickhouse
 * @Author: WangYouzheng
 * @Date: 2022/7/20 13:48
 * @Version: V1.0
 */
@Slf4j
public class MySqlDynamicCHSink extends RichSinkFunction<List<String>> {
    /**
     * 只缓存mysql到clickhouse的数据
     */
    private final Map<String, TableProcess> processTableMap = new HashMap<>();

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        // 初始化分流Map
        List<TableProcess> tableProcesses = MysqlUtil.queryList("select source_table, sink_table, class_name from table_process where sink_type='clickhouse' and level_name='dwm'", TableProcess.class, true);

        if (CollectionUtils.isNotEmpty(tableProcesses)) {
            String className = null;
            String sourceTable = null;
            String sinkTable = null;
            for (TableProcess tableProcess : tableProcesses) {
                // 参数校验
                sourceTable = tableProcess.getSourceTable();
                className = tableProcess.getClassName();
                sinkTable = tableProcess.getSinkTable();
                if (StringUtils.isNotBlank(className)) {
                    try {
                        tableProcess.setClazz(Class.forName(className));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        // 配置表沒初始化好不准啓動服務。
                        throw e;
                    }
                }
                processTableMap.put(sourceTable, tableProcess);
            }
        }
    }

    @Override
    public void invoke(List<String> cdcJsonList, Context context) throws Exception {
        if (CollectionUtils.isNotEmpty(cdcJsonList)) {
            StringBuilder totalSb = new StringBuilder();

            JSONObject cdcJsonObj = null;
            String tableName = null;
            StringBuilder sb = null;
            // value值部分
            List<List<Object>> valueList = new ArrayList(cdcJsonList.size());

            for (String cdcJson : cdcJsonList) {
                // 1. 反序列化
                cdcJsonObj = JSONObject.parseObject(cdcJson);
                // 2. 获取动态分流后的目标表信息 tableName
                tableName = cdcJsonObj.getString("tableName");
                TableProcess tableProcess = processTableMap.get(tableName);
                // 3. 动态拼接批量SQL
                sb = new StringBuilder();
                sb.append("insert into ")
                .append(tableProcess.getSinkTable());
                sb.append(" values ");
                sb.append(getValueSql(tableProcess.getClazz()));
                sb.append(";");
                // 4. 执行
                totalSb.append(sb);
                valueList.add(getValueList(cdcJsonObj.getJSONObject("after")));
            }

            // 提交
            ClickhouseDruidUtil.insertPrepare(totalSb.toString(), valueList);
        }
    }

    /**
     * 获取每行的数据 用于拼接prepare阶段的 value赋值。
     * @param afterData
     * @return
     */
    private List<Object> getValueList(JSONObject afterData) {
        List<Object> newList = new ArrayList<>();
        afterData.forEach((k, v) -> {
            if (!StringUtils.equals(k, "IDNUM")) {
                newList.add(v);
            }
        });
        return newList;
    }

    /**
     * 组装sql values 部分 ?,?
     */
    public static <T> String getValueSql(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> list = new ArrayList<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            // 序列化id不处理
            if (StringUtils.equals(fieldName, "serialVersionUID")) {
                continue;
            }
            // 获取字段上的注解
            TransientSink annotation = field.getAnnotation(TransientSink.class);
            if (annotation != null) {
                continue;
            }
            // mysql到clichouse同样的表，区别是要剔除IDNUM
            if (StringUtils.equals(fieldName, "IDNUM")) {
                continue;
            }

            list.add(field);
        }
        List<String> wildcard = new ArrayList<>();
        for (Field field : list) {
            wildcard.add("?");
        }
        return " (" + StringUtils.join(wildcard, ",") + ") ";
    }
}