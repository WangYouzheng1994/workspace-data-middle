package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * druid数据库连接池操作工具类
 */
@Slf4j
public class DbUtil {

    // Druid数据源，全局唯一（只创建一次）
    private static volatile DruidDataSource druidDataSource;

    /**
     * 执行SQL更新
     *
     * @param updateSql
     * @throws SQLException
     */
    public static void insert(String updateSql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getDruidConnection();
            statement = connection.createStatement();
            int count = statement.executeUpdate(updateSql);
            log.info("单条数据执行成功,{}", count);
            System.out.println("单条数据执行成功:" + count);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, null);
        }
    }

    /**
     * 执行SQL批量添加
     *
     * @param sql
     * @throws SQLException
     */
    public static <T> void insertBatch(String sql, List<T> dataList) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        try {
            connection = getDruidConnection();
            ps = connection.prepareStatement(sql);
            connection.setAutoCommit(false);//取消自动提交
            for (int i = 0; i < dataList.size(); i++) {
                ps.setObject(i + 1, dataList.get(i));
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
            connection.commit();//所有语句都执行完毕后才手动提交sql语句
            log.info("批量插入数据成功");
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, resultSet);
        }
    }

    /**
     * 执行SQL查询
     *
     * @param querySql
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> executeQuery(String querySql) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getDruidConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(querySql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                int columnCount = metaData.getColumnCount();
                Map<String, Object> resultMap = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);// 字段名称
                    Object columnValue = resultSet.getObject(columnName);// 字段值
                    resultMap.put(columnName, columnValue);
                }
                resultList.add(resultMap);
            }
            log.info(">>>>>>>>>>>> 查询数据:{}", resultList);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, resultSet);
        }
        return resultList;
    }

    /**
     * 执行SQL查询
     *
     * @param querySql
     * @return
     * @throws Exception
     */
    public static ResultSet executeQuerySet(String querySql) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getDruidConnection();
            statement = connection.prepareStatement(querySql);
            resultSet = statement.executeQuery();
            log.info(">>>>>>>>>>>> 查询数据:{}", resultList);
        } finally {
            // 切记!!! 一定要释放资源
            //closeResource(connection, statement, resultSet);
        }
        return resultSet;
    }

    /**
     * 获取Druid数据源
     *
     * @return
     * @throws SQLException
     */
    private static DruidDataSource getDruidDataSource() throws SQLException {
        // 保证Druid数据源在多线程下只创建一次
        if (druidDataSource == null) {
            synchronized (DbUtil.class) {
                if (druidDataSource == null) {
                    druidDataSource = createDruidDataSource();
                    return druidDataSource;
                }
            }
        }
        log.info(">>>>>>>>>>> 复用Druid数据源:url={}, username={}, password={}",
                druidDataSource.getUrl(), druidDataSource.getUsername(), druidDataSource.getPassword());
        return druidDataSource;
    }

    /**
     * 创建Druid数据源
     *
     * @return
     * @throws SQLException
     */
    private static DruidDataSource createDruidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        druidDataSource.setUrl(props.getStr("mysql.url"));
        druidDataSource.setUsername(props.getStr("mysql.username"));
        druidDataSource.setPassword(props.getStr("mysql.password"));

        /*----下面的具体配置参数自己根据项目情况进行调整----*/
        druidDataSource.setMaxActive(20);
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxWait(60000);

        druidDataSource.setValidationQuery("select 1");

        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);

        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        //无连接可用超时
        //此配置项会影响性能，只在排查的时候打开，系统运行时最好关闭
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(180);

        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        druidDataSource.init();
        log.info(">>>>>>>>>>> 创建Druid数据源:url={}, username={}, password={}",
                druidDataSource.getUrl(), druidDataSource.getUsername(), druidDataSource.getPassword());
        return druidDataSource;
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
        return connection;
    }

    /**
     * 释放资源
     *
     * @param connection
     * @param statement
     * @param resultSet
     * @throws SQLException
     */
    private static void closeResource(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
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