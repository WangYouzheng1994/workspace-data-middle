package org.jeecg.yqwl.datamiddle.config.driver.driver.mysql;

import org.jeecg.yqwl.datamiddle.config.driver.driver.AbstractJdbcDriver;
import org.jeecg.yqwl.datamiddle.config.driver.driver.IDBQuery;
import org.jeecg.yqwl.datamiddle.config.driver.entity.ITypeConvert;

import java.util.HashMap;
import java.util.Map;

/**
 * MysqlDriver
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public class MySqlDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new MySqlTypeConvert();
    }

    @Override
    public String getType() {
        return "MySql";
    }

    @Override
    public String getName() {
        return "MySql数据库";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

}
