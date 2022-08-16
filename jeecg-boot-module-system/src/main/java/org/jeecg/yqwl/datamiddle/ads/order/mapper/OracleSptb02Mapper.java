package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;

/**
 * @Description: 用来去Oracle查询数据库
 * @Author: Xiaofeng
 * @Date:   2022-05-12
 * @Version: V1.0
 */

public interface OracleSptb02Mapper extends BaseMapper<Sptb02> {
    /**
     * 查询是否有vin码对应的值
     * @param vvin
     * @return
     */
    Integer  countOracleVinOfSptb02AndSptb02d1(@Param("vvin") String vvin);
}
