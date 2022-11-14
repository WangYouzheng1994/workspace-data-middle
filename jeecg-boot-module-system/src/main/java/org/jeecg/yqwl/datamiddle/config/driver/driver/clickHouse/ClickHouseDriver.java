package org.jeecg.yqwl.datamiddle.config.driver.driver.clickHouse;

import org.jeecg.yqwl.datamiddle.config.driver.driver.AbstractJdbcDriver;
import org.jeecg.yqwl.datamiddle.config.driver.driver.IDBQuery;
import org.jeecg.yqwl.datamiddle.config.driver.entity.ITypeConvert;

/**
 * ClickHouseDriver
 *
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public class ClickHouseDriver extends AbstractJdbcDriver {

    @Override
    public String getDriverClass() {
        return "ru.yandex.clickhouse.ClickHouseDriver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new ClickHouseQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new ClickHouseTypeConvert();
    }

    @Override
    public String getType() {
        return "ClickHouse";
    }

    @Override
    public String getName() {
        return "ClickHouse OLAP 数据库";
    }




}
