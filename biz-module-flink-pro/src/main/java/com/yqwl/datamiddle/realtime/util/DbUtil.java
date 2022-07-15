package com.yqwl.datamiddle.realtime.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;

import java.sql.*;
import java.util.*;

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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, null);
        }
    }

    /**
     * 执行SQL更新
     *
     * @param sql
     * @throws SQLException
     */
    public static void insertPrepare(String sql, List<List<Object>> valueList) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getDruidConnection();
            preparedStatement = connection.prepareStatement(sql);

            if (CollectionUtils.isNotEmpty(valueList)) {
                int index = 1;
                for (int i = 0; i < valueList.size(); i++) {
                    List<Object> objects = valueList.get(i);
                    for (Object object : objects) {
                        preparedStatement.setObject(index, object);
                        index++;
                    }
                }
            }
            boolean index = preparedStatement.execute();
            log.info("单条数据执行成功情况,{}", index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, preparedStatement, null);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, resultSet);
        }
        return resultList;
    }

    /**
     * 执行SQL更新
     *
     * @param updateSql
     * @return
     * @throws Exception
     */
    public static int executeUpdate(String updateSql) throws Exception {
        Connection connection = null;
        Statement statement = null;
        int index = -1;
        try {
            connection = getDruidConnection();
            statement = connection.createStatement();
            index = statement.executeUpdate(updateSql);
            log.info(">>>>>>>>>>>> 更新数据:{}", index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, null);
        }
        return index;
    }

    /**
     * 批量更新 多条语句
     *
     * @param updateSql
     * @return
     * @throws Exception
     */
    public static long executeBatchUpdate(String updateSql) throws Exception {
        Connection connection = null;
        Statement statement = null;
        long index = -1;

        try {
            connection = getDruidConnection();
            statement = connection.createStatement();
            index = statement.executeUpdate(updateSql);
            log.info(">>>>>>>>>>>> 更新数据:{}", index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 切记!!! 一定要释放资源
            closeResource(connection, statement, null);
        }
        return index;
    }


    /**
     * mysql 查询方法，根据给定的 class 类型 返回对应类型的元素列表
     *
     * @param sql
     * @param clazz
     * @param underScoreToCamel 是否把对应字段的下划线名转为驼峰名
     * @param <T>
     * @return
     */
    public static <T> List<T> queryList(String sql, Class<T> clazz, Boolean underScoreToCamel) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //建立连接
            conn = getDruidConnection();
            //创建数据库操作对象
            ps = conn.prepareStatement(sql);
            //执行 SQL 语句
            rs = ps.executeQuery();
            //处理结果集
            ResultSetMetaData md = rs.getMetaData();
            //声明集合对象，用于封装返回结果
            List<T> resultList = new ArrayList<T>();
            //每循环一次，获取一条查询结果
            while (rs.next()) {
                //通过反射创建要将查询结果转换为目标类型的对象
                T obj = clazz.newInstance();
                //对查询出的列进行遍历，每遍历一次得到一个列名
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String propertyName = md.getColumnName(i);
                    //如果开启了下划线转驼峰的映射，那么将列名里的下划线转换为属性的打
                    if (underScoreToCamel) {
                        //直接调用 Google 的 guava 的 CaseFormat LOWER_UNDERSCORE 小写开头+下划线->LOWER_CAMEL 小写开头+驼峰
                        propertyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, propertyName);
                    }
                    //调用 apache 的 commons-bean 中的工具类，给 Bean 的属性赋值
                    BeanUtils.setProperty(obj, propertyName, rs.getObject(i));
                }
                resultList.add(obj);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询 mysql 失败！");
        } finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
        //Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        druidDataSource.setUrl(PropertiesUtil.getPropsStr("mysql.url"));
        druidDataSource.setUsername(PropertiesUtil.getPropsStr("mysql.username"));
        druidDataSource.setPassword(PropertiesUtil.getPropsStr("mysql.password"));

        /*----下面的具体配置参数自己根据项目情况进行调整----*/
        druidDataSource.setMaxActive(1500);
        druidDataSource.setInitialSize(50);
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
    public static DruidPooledConnection getDruidConnection() throws SQLException {
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