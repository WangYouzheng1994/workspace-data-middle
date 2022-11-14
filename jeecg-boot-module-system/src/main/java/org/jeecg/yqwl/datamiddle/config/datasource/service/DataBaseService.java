package org.jeecg.yqwl.datamiddle.config.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.config.datasource.entity.DatasourceConfig;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Column;
import org.jeecg.yqwl.datamiddle.config.driver.entity.Schema;

import java.util.List;

/**
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public interface DataBaseService  extends IService<DatasourceConfig> {

    List<DatasourceConfig> listEnabledAll();

    List<Schema> getSchemasAndTables(Integer id);

    List<Column>  listColumns(Integer id, String schemaName, String tableName);
}
