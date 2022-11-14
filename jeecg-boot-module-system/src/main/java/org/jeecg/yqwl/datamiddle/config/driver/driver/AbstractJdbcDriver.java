package org.jeecg.yqwl.datamiddle.config.driver.driver;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Column;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Schema;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
/**
 * AbstractJdbcDriver
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public abstract class AbstractJdbcDriver extends AbstractDriver {

    protected static Logger logger = LoggerFactory.getLogger(AbstractJdbcDriver.class);

    protected ThreadLocal<Connection> conn = new ThreadLocal<>();

    private DruidDataSource dataSource;

    protected abstract String getDriverClass();

    static void checkNull(Object key, String msg) {
        if (key == null) {
            throw new JeecgBootException(msg);
        }
    }

    public DruidDataSource createDataSource() throws SQLException {
        if (null == dataSource) {
            synchronized (this.getClass()) {
                if (null == dataSource) {
                    DruidDataSource ds = new DruidDataSource();
                    createDataSource(ds, config);
                    ds.init();
                    this.dataSource = ds;
                }
            }
        }
        return dataSource;
    }

    @Override
    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        try {
            this.dataSource = createDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        createDataSource(dataSource, config);
        return this;
    }

    protected void createDataSource(DruidDataSource ds, DriverConfig config) {
        ds.setName(config.getName().replaceAll(":", ""));
        ds.setUrl(config.getUrl());
        ds.setDriverClassName(getDriverClass());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setValidationQuery("select 1");
        ds.setTestWhileIdle(true);
        ds.setBreakAfterAcquireFailure(true);
        ds.setFailFast(true);
        ds.setInitialSize(1);
        ds.setMaxActive(8);
        ds.setMinIdle(5);
    }

    @Override
    public Driver connect() {
        if (null == conn.get()) {
            try {
                Class.forName(getDriverClass());
                DruidPooledConnection connection = createDataSource().getConnection();
                conn.set(connection);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public boolean isHealth() {
        try {
            if (null!=conn.get()) {
                return !conn.get().isClosed();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        try {
            if (null!=conn.get()) {
                conn.get().close();
                conn.remove();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(PreparedStatement preparedStatement, ResultSet results) {
        try {
            if (null!=results) {
                results.close();
            }
            if (null!=preparedStatement) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Schema> listSchemas() {
        List<Schema> schemas = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String schemasSql = getDBQuery().schemaAllSql();
        try {
            preparedStatement = conn.get().prepareStatement(schemasSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String schemaName = results.getString(getDBQuery().schemaName());
                if (StringUtils.isNotEmpty(schemaName)) {
                    Schema schema = new Schema(schemaName);
                    schemas.add(schema);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
    }

    @Override
    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql = dbQuery.tablesSql(schemaName);
        try {
            preparedStatement = conn.get().prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                String tableName = results.getString(dbQuery.tableName());
                if (StringUtils.isNotEmpty(tableName)) {
                    Table tableInfo = new Table();
                    tableInfo.setName(tableName);
                    if (columnList.contains(dbQuery.tableComment())) {
                        tableInfo.setComment(results.getString(dbQuery.tableComment()));
                    }
                    tableInfo.setSchema(schemaName);
                    if (columnList.contains(dbQuery.tableType())) {
                        tableInfo.setType(results.getString(dbQuery.tableType()));
                    }
                    if (columnList.contains(dbQuery.catalogName())) {
                        tableInfo.setCatalog(results.getString(dbQuery.catalogName()));
                    }
                    if (columnList.contains(dbQuery.engine())) {
                        tableInfo.setEngine(results.getString(dbQuery.engine()));
                    }
                    if (columnList.contains(dbQuery.options())) {
                        tableInfo.setOptions(results.getString(dbQuery.options()));
                    }
                    if (columnList.contains(dbQuery.rows())) {
                        tableInfo.setRows(results.getLong(dbQuery.rows()));
                    }
                    if (columnList.contains(dbQuery.createTime())) {
                        tableInfo.setCreateTime(results.getTimestamp(dbQuery.createTime()));
                    }
                    if (columnList.contains(dbQuery.updateTime())) {
                        tableInfo.setUpdateTime(results.getTimestamp(dbQuery.updateTime()));
                    }
                    tableList.add(tableInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return tableList;
    }

    @Override
    public List<Column> listColumns(String schemaName, String tableName) {
        List<Column> columns = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String tableFieldsSql = dbQuery.columnsSql(schemaName, tableName);
        tableFieldsSql = String.format(tableFieldsSql, tableName);
        try {
            preparedStatement = conn.get().prepareStatement(tableFieldsSql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Column field = new Column();
                String columnName = results.getString(dbQuery.columnName());
                if (columnList.contains(dbQuery.columnKey())) {
                    String key = results.getString(dbQuery.columnKey());
                    field.setKeyFlag(StringUtils.isNotBlank(key) && StringUtils.equalsIgnoreCase(dbQuery.isPK(), key));
                }
                field.setName(columnName);
                if (columnList.contains(dbQuery.columnType())) {
                    String columnType = results.getString(dbQuery.columnType());
                    if (columnType.contains("(")) {
                        String type = columnType.replaceAll("\\(.*\\)", "");
                        if (!columnType.contains(",")) {
                            Integer length = Integer.valueOf(columnType.replaceAll("\\D", ""));
                            field.setLength(length);
                        } else {
                            // some database does not have precision
                            if (dbQuery.precision() != null) {
                                // 例如浮点类型的长度和精度是一样的，decimal(10,2)
                                field.setLength(results.getInt(dbQuery.precision()));
                            }
                        }
                        field.setType(type);
                    } else {
                        field.setType(columnType);
                    }
                }
                if (columnList.contains(dbQuery.columnComment())
                        && StringUtils.isNotEmpty(results.getString(dbQuery.columnComment()))) {
                    String columnComment = results.getString(dbQuery.columnComment()).replaceAll("\"|'", "");
                    field.setComment(columnComment);
                }
                if (columnList.contains(dbQuery.columnLength())) {
                    int length = results.getInt(dbQuery.columnLength());
                    if (!results.wasNull()) {
                        field.setLength(length);
                    }
                }
                if (columnList.contains(dbQuery.isNullable())) {
                    field.setNullable(StringUtils.equalsIgnoreCase(results.getString(dbQuery.isNullable()),
                            dbQuery.nullableValue()));
                }
                if (columnList.contains(dbQuery.characterSet())) {
                    field.setCharacterSet(results.getString(dbQuery.characterSet()));
                }
                if (columnList.contains(dbQuery.collation())) {
                    field.setCollation(results.getString(dbQuery.collation()));
                }
                if (columnList.contains(dbQuery.columnPosition())) {
                    field.setPosition(results.getInt(dbQuery.columnPosition()));
                }
                if (columnList.contains(dbQuery.precision())) {
                    field.setPrecision(results.getInt(dbQuery.precision()));
                }
                if (columnList.contains(dbQuery.scale())) {
                    field.setScale(results.getInt(dbQuery.scale()));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    field.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                if (columnList.contains(dbQuery.autoIncrement())) {
                    field.setAutoIncrement(
                            StringUtils.equalsIgnoreCase(results.getString(dbQuery.autoIncrement()), "auto_increment"));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    field.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                field.setJavaType(getTypeConvert().convert(field));
                columns.add(field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return columns;
    }

    public List<Map<String, String>> getSplitSchemaList() {
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql =
                "select DATA_LENGTH,TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `Database`,TABLE_COMMENT AS COMMENT,TABLE_CATALOG AS `CATALOG`,TABLE_TYPE"
                        + " AS `TYPE`,ENGINE AS `ENGINE`,CREATE_OPTIONS AS `OPTIONS`,TABLE_ROWS AS `ROWS`,CREATE_TIME,UPDATE_TIME from information_schema.tables WHERE TABLE_TYPE='BASE TABLE'";
        List<Map<String, String>> schemas = null;
        try {
            preparedStatement = conn.get().prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            schemas = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Map<String, String> map = new HashMap<>();
                for (String column : columnList) {
                    map.put(column, results.getString(column));
                }
                schemas.add(map);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
    }
}
