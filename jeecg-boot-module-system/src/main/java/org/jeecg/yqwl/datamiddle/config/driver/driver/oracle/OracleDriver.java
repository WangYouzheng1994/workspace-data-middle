package org.jeecg.yqwl.datamiddle.config.driver.driver.oracle;

import com.alibaba.druid.pool.DruidDataSource;
import org.jeecg.yqwl.datamiddle.config.driver.driver.AbstractJdbcDriver;
import org.jeecg.yqwl.datamiddle.config.driver.driver.IDBQuery;
import org.jeecg.yqwl.datamiddle.config.driver.entity.ITypeConvert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OracleDriver
 * @Author: XiaoKai
 * @Date: 2022-011-14
 * @Version: V2.0
 */
public class OracleDriver extends AbstractJdbcDriver {

    @Override
    public String getDriverClass() {
        return "oracle.jdbc.OracleDriver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new OracleQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new OracleTypeConvert();
    }

    @Override
    public String getType() {
        return "Oracle";
    }

    @Override
    public String getName() {
        return "Oracle数据库";
    }

}
