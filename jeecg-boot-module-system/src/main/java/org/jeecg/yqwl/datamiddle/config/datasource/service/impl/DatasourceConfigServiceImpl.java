package org.jeecg.yqwl.datamiddle.config.datasource.service.impl;

import org.jeecg.yqwl.datamiddle.config.datasource.entity.DatasourceConfig;
import org.jeecg.yqwl.datamiddle.config.datasource.mapper.DatasourceConfigMapper;
import org.jeecg.yqwl.datamiddle.config.datasource.service.IDatasourceConfigService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: datasource_config
 * @Author: jeecg-boot
 * @Date:   2022-05-09
 * @Version: V1.0
 */
@Service
public class DatasourceConfigServiceImpl extends ServiceImpl<DatasourceConfigMapper, DatasourceConfig> implements IDatasourceConfigService {

}
