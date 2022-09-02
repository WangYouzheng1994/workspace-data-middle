package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.OracleDocsTimeVo;

import java.util.List;

/**
 * @Description: 用来去Oracle查询数据库
 * @Author: Xiaofeng
 * @Date:   2022-05-12
 * @Version: V1.0
 */

@DS("oracle-vts")
public interface OracleSptb02Mapper extends BaseMapper<Sptb02> {
    /**
     * 查询是否有vin码对应的值
     * @param vvin
     * @return
     */
    Integer  countOracleVinOfSptb02AndSptb02d1(@Param("vvin") String vvin);

    /**
     * 去oracle原表中查相关数据
     * @param vinList vin列表
     * @author dabao
     * @date 2022/8/31
     * @return {@link List<DwmVlmsDocs>}
     */
    List<DwmVlmsDocs> selectListByVin(@Param("list") List<String> vinList);
}
