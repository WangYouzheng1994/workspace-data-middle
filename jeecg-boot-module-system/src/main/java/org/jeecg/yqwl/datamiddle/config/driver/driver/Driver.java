package org.jeecg.yqwl.datamiddle.config.driver.driver;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.exception.MetaDataException;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Column;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Schema;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Table;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/***
 * Driver 工具类
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public  interface Driver extends AutoCloseable  {
    /**
     *  获取数据源
     * @param config
     * @return
     */
    static Optional<Driver> get(DriverConfig config) {
        checkNull(config, "数据源配置不能为空");
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            if (driver.canHandle(config.getType())) {
                return Optional.of(driver.setDriverConfig(config));
            }
        }
        return Optional.empty();
    }

    /***
     *  创建链接数据
     * @param config
     * @return
     */
    static Driver build(DriverConfig config) {
        String key = config.getName();

        if (DriverPool.exist(key)) {
            return getHealthDriver(key);
        }
        synchronized (Driver.class) {
            Optional<Driver> optionalDriver = Driver.get(config);
            if (!optionalDriver.isPresent()) {
                throw new MetaDataException("缺少数据源类型【" + config.getType() + "】的依赖，请在 lib 下添加对应的扩展依赖");
            }
            Driver driver = optionalDriver.get().connect();
            DriverPool.push(key, driver);
            return driver;
        }
    }

    /**
     *  获取是否存在健康得链接
     * @param key
     * @return
     */
    static Driver getHealthDriver(String key) {
        Driver driver = DriverPool.get(key);
        if (driver.isHealth()) {
            return driver;
        } else {
            return driver.connect();
        }
    }

    /***
     *  创建多种数据源
     * @param connector
     * @param url
     * @param username
     * @param password
     * @return
     */
    static Driver build(String connector, String url, String username, String password) {
        String type = null;
        if (StringUtils.equalsIgnoreCase(connector, "doris")) {
            type = "Doris";
        } else if (StringUtils.equalsIgnoreCase(connector, "starrocks")) {
            type = "StarRocks";
        } else if (StringUtils.equalsIgnoreCase(connector, "clickhouse")) {
            type = "ClickHouse";
        } else if (StringUtils.equalsIgnoreCase(connector, "jdbc")) {
            if (url.startsWith("jdbc:mysql")) {
                type = "MySQL";
            } else if (url.startsWith("jdbc:postgresql")) {
                type = "PostgreSql";
            } else if (url.startsWith("jdbc:oracle")) {
                type = "Oracle";
            } else if (url.startsWith("jdbc:sqlserver")) {
                type = "SQLServer";
            } else if (url.startsWith("jdbc:phoenix")) {
                type = "Phoenix";
            } else if (url.startsWith("jdbc:pivotal")) {
                type = "Greenplum";
            }
        }
        if (StringUtils.isEmpty(type)) {
            throw new MetaDataException("缺少数据源类型:【" + connector + "】");
        }
        DriverConfig driverConfig = new DriverConfig(url, type, url, username, password);
        return build(driverConfig);
    }
    static void checkNull(Object key, String msg) {
        if (key == null) {
            throw new JeecgBootException(msg);
        }
    }

    Driver setDriverConfig(DriverConfig config);

    boolean isHealth();

    Driver connect();

    String getType();

    String getName();

    void close();

    boolean canHandle(String type);

    List<Schema> getSchemasAndTables(String Type);

    List<Column> listColumns(String schemaName, String tableName);

    List<Schema> listSchemas(String type);

    Table getTable(String schemaName, String tableName);

    List<Table> listTables(String schemaName);
}
