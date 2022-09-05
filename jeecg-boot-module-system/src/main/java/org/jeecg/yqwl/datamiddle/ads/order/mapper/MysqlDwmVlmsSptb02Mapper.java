package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

import java.util.List;

/**
 * mysql数据库的mapper
 * @author dabao
 * @date 2022/8/30
 */
@DS("wareHouse")
public interface MysqlDwmVlmsSptb02Mapper extends BaseMapper<DwmVlmsSptb02> {

    /**
     * 根据查询你条件查询每条vin对应的时间字段
     * @param queryCriteria 查询参数
     * @author dabao
     * @date 2022/9/1
     * @return {@link List<DwmVlmsDocs>}
     */
    List<DwmVlmsDocs> selectDocsCcxdlList(@Param("params") GetQueryCriteria queryCriteria);

    Integer selectDocsCcxdlCount(@Param("params") GetQueryCriteria queryCriteria);
}
