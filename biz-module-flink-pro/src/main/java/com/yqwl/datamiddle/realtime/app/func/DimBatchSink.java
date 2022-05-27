package com.yqwl.datamiddle.realtime.app.func;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @Description: 自定义mysql sink function
 * @Author: muqing
 * @Date: 2022/05/07
 * @Version: V1.0
 */
@Slf4j
public class DimBatchSink extends RichSinkFunction<Map<String, List<JSONObject>>> {

    //定义mysql连接对象
    private Connection conn = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        //对连接对象进行初始化
        Class.forName(MysqlConfig.DRIVER);
        //Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        //conn = DriverManager.getConnection(props.getStr("mysql.url"), props.getStr("mysql.username"), props.getStr("mysql.password"));
    }

    /**
     * 对流中的数据进行处理
     */
    @Override
    public void invoke(Map<String, List<JSONObject>> map, Context context) throws Exception {
        //获取目标保存的表名
        long start = System.currentTimeMillis();
        //获取执行sql
        Set<Map.Entry<String, List<JSONObject>>> entrySet = map.entrySet();
        for (Map.Entry<String, List<JSONObject>> entry : entrySet) {
            //对每一批数据进行处理
            String sql = genInsertSql(entry.getKey(), entry.getValue());
            System.out.println("组装生成的sql:" + sql);
            log.info("组装生成的sql:{}", sql);
            //执行SQL
            DbUtil.insert(sql);

    /*        PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.execute();
            } catch (SQLException e) {
                log.error("向mysql插入数据失败, {}", e.getMessage());
                throw new RuntimeException("向mysql插入数据失败");
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }*/
        }
        //System.out.println("数据插入执行时间：" + (System.currentTimeMillis() - start));
        log.info("数据插入执行时间：" + (System.currentTimeMillis() - start));
    }

    // 根据data属性和值生成 insert sql语句

    /**
     * *
     * {
     * * "database":"datasource_kafka",
     * * "before":{},
     * * "after":{"order_no":"20220303911728","create_time":1649412632000,"product_count":1,"product_id":434,"id":297118,"product_amount":3426},
     * * "type":"read",  query
     * * "tableName":"orders_detail",
     * * "ts":1651830021955,D
     * * "sink_table": ""
     * * }
     * <p>
     * {
     * * "database":"datasource_kafka",
     * * "before":{},
     * * "after":{"order_no":"20220303911728","create_time":1649412632000,"product_count":1,"product_id":434,"id":297118,"product_amount":3426},
     * * "type":"read",  query
     * * "tableName":"orders_detail",
     * * "ts":1651830021955,D
     * * "sink_table": ""
     * * }
     *
     * @param tableName
     * @param dataList  代表after里真实数据
     * @return
     */
    private String genInsertSql(String tableName, List<JSONObject> dataList) {
        //"insert into 表名(列名.....) values (值....)"
        //获取第一个元素，目标是组装字段列表部分
        JSONObject jsonObject = dataList.get(0);
        //获取真实数据
        //JSONObject afterObj = JsonPartUtil.getAfterObj(jsonObject);
        //获取真实数据的字段名称
        Set<String> columns = jsonObject.keySet();
        log.info("获取当前数据所有字段数量:{}, 字段值:{}", columns.size(), columns);
        //System.err.println("获取当前数据key:" + columns.size());
        //System.err.println("获取当前数据key:" + columns);
        //1.定义sql
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("replace into ").append(tableName).append("(")
                .append(StringUtils.join(columns, ",")).append(" )")
                .append(" values ");

        //2.处理value部分
        StringBuffer valueSql = new StringBuffer();
        //循环同一个表名下所有数据
        //cdc过来的数据都是有值的 (), (),
        for (JSONObject obj : dataList) {
            StringBuffer valueForTable = new StringBuffer();
            //获取每一条真实数据
            //JSONObject afterObjVal = JsonPartUtil.getAfterObj(obj);
            valueForTable.append("(");
            Collection<Object> values = obj.values();
            //System.err.println("获取当前数据values:" + values.size());
            //System.err.println("获取当前数据values:" + values);
            log.info("获取当前数据数量:{}, values:{}", values.size(), values);
            Collection<Object> newValues = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof String) {
                    String newStr = "'" + value + "'";
                    newValues.add(newStr);
                } else {
                    newValues.add(value);
                }
            }
            valueForTable.append(StringUtils.join(newValues, ",")).append(")").append(",");
            valueSql.append(valueForTable);
        }
        String valueSql2 = valueSql.toString();
        String substring = valueSql2.substring(0, valueSql2.lastIndexOf(",") - 1);

        insertSql.append(substring).append(")");
        return insertSql.toString();
    }

}
