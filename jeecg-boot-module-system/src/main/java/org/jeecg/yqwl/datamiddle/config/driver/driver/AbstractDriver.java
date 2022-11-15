package org.jeecg.yqwl.datamiddle.config.driver.driver;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.yqwl.datamiddle.config.driver.entity.ITypeConvert;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Schema;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Table;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AbstractDriver
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public abstract class AbstractDriver implements Driver {

    protected DriverConfig config;

    public abstract IDBQuery getDBQuery();

    public abstract ITypeConvert getTypeConvert();

    public boolean canHandle(String type) {
        return StringUtils.equalsIgnoreCase(getType(), type);
    }

    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        return this;
    }

    public boolean isHealth() {
        return false;
    }

    public List<Schema> getSchemasAndTables(String type) {
        return listSchemas(type).stream().peek(schema -> schema.setTables(listTables(schema.getName()))).sorted().collect(Collectors.toList());
    }

    public List<Table> getTablesAndColumns(String schema) {
        return listTables(schema).stream().peek(table -> table.setColumns(listColumns(schema, table.getName()))).sorted().collect(Collectors.toList());
    }

    @Override
    public Table getTable(String schemaName, String tableName) {
        List<Table> tables = listTables(schemaName);
        Table table = null;
        for (Table item : tables) {
            if (StringUtils.equals(item.getName(), tableName)) {
                table = item;
            }
        }
        if (null!=table) {
            table.setColumns(listColumns(schemaName, table.getName()));
        }
        return table;
    }
}
