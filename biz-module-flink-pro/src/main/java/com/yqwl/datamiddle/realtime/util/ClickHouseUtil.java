package com.yqwl.datamiddle.realtime.util;

import com.yqwl.datamiddle.realtime.enums.TransientSink;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 获取clickhouse jdbcSink，自动根据实体类去反射生成preparestatement阶段的赋值。
 */
public class ClickHouseUtil {

    public static <T> SinkFunction<T> getSink(String sql) {

        return JdbcSink.<T>sink(sql,
                new JdbcStatementBuilder<T>() {
                    @Override
                    public void accept(PreparedStatement preparedStatement, T t) throws SQLException {
                        try {
                            // 获取所有的属性信息
                            Field[] fields = t.getClass().getDeclaredFields();
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
                                list.add(field);
                            }
                            // 遍历字段
                            for (int i = 0; i < list.size(); i++) {
                                // 获取字段
                                Field field = list.get(i);
                                // 设置私有属性可访问
                                field.setAccessible(true);
                                // 获取值
                                Object value = field.get(t);
                                // 给预编译SQL对象赋值
                                preparedStatement.setObject(i + 1, value);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(2000)
                        .withMaxRetries(5)
                        .withBatchIntervalMs(5000)
                        .build(),
                    PropertiesUtil.getClickhouseJDBCConnection()
                );
    }
}
