package org.jeecg.yqwl.datamiddle.config.datasource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.yqwl.datamiddle.config.datasource.entity.DatasourceConfig;
import org.jeecg.yqwl.datamiddle.config.datasource.mapper.DatasourceConfigMapper;
import org.jeecg.yqwl.datamiddle.config.datasource.service.DataBaseService;
import org.jeecg.yqwl.datamiddle.config.driver.driver.Driver;
import org.jeecg.yqwl.datamiddle.config.driver.driver.DriverConfig;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Column;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Schema;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
@Service
public class DataBaseServiceImpl  extends ServiceImpl<DatasourceConfigMapper, DatasourceConfig> implements DataBaseService {

    /**
     * 查询所有未删除得datasource
     * @return
     */
    @Override
    public List<DatasourceConfig> listEnabledAll() {
        return this.list(new QueryWrapper<DatasourceConfig>().eq("del_Flag", 0));
    }

    @Override
    public List<Schema> getSchemasAndTables(Integer id) {
        DatasourceConfig datasourceConfig = getById(id);
        checkNull(datasourceConfig, "该数据源不存在！");
        Driver driver = Driver.build(getDriver(datasourceConfig));
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        try {
            driver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schemasAndTables;
    }

    @Override
    public List<Column> listColumns(Integer id, String schemaName, String tableName) {
        DatasourceConfig dataBase = getById(id);
        checkNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(getDriver(dataBase));
        List<Column> columns = driver.listColumns(schemaName, tableName);
        try {
            driver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columns;
    }
    public void checkNull(Object key, String msg) {
        if (key == null) {
            throw new JeecgBootException(msg);
        }
    }
    public DriverConfig getDriver(DatasourceConfig datasourceConfig) {
        String datasourceUrl = datasourceConfig.getDatasourceUrl();
        String datasourceName = datasourceConfig.getDatasourceName();
        String account = datasourceConfig.getAccount();
        String password = datasourceConfig.getPassword();
        //解析数据源类型
        String type = AnalysisUrl(datasourceUrl);
        return new DriverConfig(datasourceName, type, datasourceUrl, account, password);
    }

    /***
     *  解析出类型
     * @param datasourceUrl
     */
    private String AnalysisUrl(String datasourceUrl) {
        String type = null;
        if(datasourceUrl==null){
            return  null;
        }
        if (datasourceUrl.startsWith("jdbc:mysql")) {
            type = "MySQL";
        }else if (datasourceUrl.startsWith("jdbc:clickhouse")) {
            type = "ClickHouse";
        } else if (datasourceUrl.startsWith("jdbc:oracle")) {
            type = "Oracle";
        } else if (datasourceUrl.startsWith("jdbc:sqlserver")) {
            type = "SQLServer";
        } else if (datasourceUrl.startsWith("jdbc:phoenix")) {
            type = "Phoenix";
        } else if (datasourceUrl.startsWith("jdbc:pivotal")) {
            type = "Greenplum";
        }else if (datasourceUrl.startsWith("jdbc:postgresql")) {
            type = "PostgreSql";
        }
        return type;
    }

}
