package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import javafx.animation.Transition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/5/11 19:25
 * @Version: V1.0
 */
public class JdbcBatchSink<T> extends RichSinkFunction<List<T>> {
    // Druid数据源，全局唯一（只创建一次）
    private static volatile DruidDataSource dataSource;

    @Override
    public void open(Configuration parameters) {
        try {
            // 创建连接
            getDruidDataSource();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Druid连接
     *
     * @return
     * @throws SQLException
     */
    private static DruidPooledConnection getDruidConnection() throws SQLException {
        DruidDataSource druidDataSource = getDruidDataSource();
        DruidPooledConnection connection = druidDataSource.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }

    /**
     * 创建Druid数据源
     *
     * @return
     * @throws SQLException
     */
    private static DruidDataSource createDruidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        Props props = PropertiesUtil.getProps();
        druidDataSource.setUrl(props.getStr("mysql.url"));
        druidDataSource.setUsername(props.getStr("mysql.username"));
        druidDataSource.setPassword(props.getStr("mysql.password"));

        /*----下面的具体配置参数自己根据项目情况进行调整----*/
        druidDataSource.setMaxActive(200);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxWait(60000);

        druidDataSource.setValidationQuery("select 1");

        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);

        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);

        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        druidDataSource.init();

        return druidDataSource;
    }

    /**
     * 获取Druid数据源
     *
     * @return
     * @throws SQLException
     */
    private static DruidDataSource getDruidDataSource() throws SQLException {
        // 保证Druid数据源在多线程下只创建一次
        if (dataSource == null) {
            synchronized (DbUtil.class) {
                if (dataSource == null) {
                    dataSource = createDruidDataSource();
                    return dataSource;
                }
            }
        }
       /* log.info(">>>>>>>>>>> 复用Druid数据源:url={}, username={}, password={}",
                druidDataSource.getUrl(), druidDataSource.getUsername(), druidDataSource.getPassword());*/
        return dataSource;
    }

    @Override
    public void invoke(List<T> value, Context context) throws SQLException, IllegalAccessException {
        if (CollectionUtils.isEmpty(value)) {
            return;
        }
        DruidPooledConnection druidConnection = getDruidConnection();
        StringBuffer sb = new StringBuffer();

        String tableName = StringUtils.EMPTY;
        StringBuffer placeholder = new StringBuffer();

        // 获取要渲染的 插入列。
        Class<?> entityClass = value.get(0).getClass();
        Field[] fields = entityClass.getDeclaredFields();
        List<Field> insertFields = new ArrayList(fields.length);

        // 获取表名
        TableName annotation = entityClass.getAnnotation(TableName.class);
        if (annotation == null || StringUtils.isBlank(annotation.value())) {
            throw new RuntimeException("写入mysql 实体类缺少tableName");
        } else {
            tableName = annotation.value();
        }

        // 填充列名
        StringBuffer sqlsb = new StringBuffer();
        sqlsb.append("replace into ");
        sqlsb.append(tableName);
        sqlsb.append("(");

        StringBuffer columnSb = new StringBuffer();

        for (Field field : fields) {
            if (field.getAnnotation(TransientSink.class) != null) {
                continue;
            }
            if (columnSb.length() != 0) {
                columnSb.append(", ");
                placeholder.append(", ");
            }
            columnSb.append(camelToUnderline(field.getName()));

            placeholder.append("?");
            field.setAccessible(true);
            insertFields.add(field);
        }
        sqlsb.append(columnSb);
        sqlsb.append(")");

        // 填充数据值
        sqlsb.append(" values ");
        // 填充占位符
        for (int rows = 0; rows < value.size(); rows++) {
            sqlsb.append(" ( ");
            sqlsb.append(placeholder);
            sqlsb.append(" ) ");
            if (rows != value.size() - 1) {
                sqlsb.append(", ");
            }
        }
        PreparedStatement preparedStatement = druidConnection.prepareStatement(sqlsb.toString());

        // preparestatement 往里面填充放值。
        // 遍历获取对象的所有的值。
        Integer paramindex = 1;
        for (T t : value) {
            // 获取所有的属性信息
            for (Field insertField : insertFields) {
                Object o = insertField.get(t);
                preparedStatement.setObject(paramindex, o);
                paramindex++;
            }
        }
        preparedStatement.execute();

        closeResource(druidConnection, preparedStatement, null);
    }

    /**
      * 执行SQL更新
    *
  * @param updateSql
  * @throws SQLException
   */
    public static void insert(String updateSql) throws SQLException {
                 Connection connection = null;
                 Statement statement = null;
                 ResultSet resultSet = null;
                 try {
                         connection = getDruidConnection();
                         statement = connection.createStatement();
                         int count = statement.executeUpdate(updateSql);
                     } finally {
                         // 切记!!! 一定要释放资源
                         closeResource(connection, statement, resultSet);
                     }
             }


    /**
     * 将驼峰命名转化成下划线
     * @param para
     * @return
     */
    public static String camelToUnderline(String para){
        if(para.length()<3){
            return para.toLowerCase();
        }
        StringBuilder sb=new StringBuilder(para);
        int temp=0;//定位
        //从第三个字符开始 避免命名不规范
        for(int i=2;i<para.length();i++){
            if(Character.isUpperCase(para.charAt(i))){
                sb.insert(i+temp, "_");
                temp+=1;
            }
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 释放资源
     *
     * @param connection
     * @param statement
     * @param resultSet
     * @throws SQLException
     */
    private static void closeResource(Connection connection,
                                      Statement statement, ResultSet resultSet) throws SQLException {
        // 注意资源释放顺序
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
