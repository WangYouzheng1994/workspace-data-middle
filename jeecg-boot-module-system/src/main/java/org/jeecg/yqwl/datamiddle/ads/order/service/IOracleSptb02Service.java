package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DwmSptb02VO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * @Description: 用来去Oracle查询数据库
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface IOracleSptb02Service extends IService<Sptb02> {
    /**
     * 查询是否有vin码对应的值
     * @param vvin
     * @return
     */
    Integer countOracleVinOfSptb02AndSptb02d1(String vvin);
}
