package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.OracleSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.service.IOracleSptb02Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description: 用来去Oracle查询数据库
 * @Author: XiaoFeng
 * @Date: 2022/8/16 20:10
 * @Version: V1.0
 */
@Slf4j
@DS("oracle-vts")
@Service
public class IOracleSptb02ServiceImpl extends ServiceImpl<OracleSptb02Mapper, Sptb02>  implements IOracleSptb02Service {
    @Resource
    private OracleSptb02Mapper oracleSptb02Mapper;

    @Override
    public Integer countOracleVinOfSptb02AndSptb02d1(String vvin) {
        Integer integer = oracleSptb02Mapper.countOracleVinOfSptb02AndSptb02d1(vvin);
        return integer;
    }
}
