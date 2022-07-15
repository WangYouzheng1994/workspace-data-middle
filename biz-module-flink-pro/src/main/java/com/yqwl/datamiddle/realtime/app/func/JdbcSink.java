package com.yqwl.datamiddle.realtime.app.func;

import com.yqwl.datamiddle.realtime.enums.TransientSink;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取sink 工具类
 */
public class JdbcSink {
    /**
     * 获取批量sink
     *
     * @param <T>
     * @return
     */
    public static <T> JdbcBatchSink<T> getBatchSink() {
        return new JdbcBatchSink<T>();
    }

    public static <T> SinkFunction<T> getSink(String sql) {

        return org.apache.flink.connector.jdbc.JdbcSink.<T>sink(sql,
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
                            throw new RuntimeException(e);
                        }
                    }
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(5000L)
                        .withMaxRetries(5)
                        .build(),
                PropertiesUtil.getMysqlJDBCConnection());
    }
}
