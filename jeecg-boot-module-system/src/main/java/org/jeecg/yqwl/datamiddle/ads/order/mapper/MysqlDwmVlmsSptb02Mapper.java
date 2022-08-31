package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

import java.util.List;

@DS("wareHouse")
public interface MysqlDwmVlmsSptb02Mapper extends BaseMapper<DwmVlmsSptb02> {

    List<DwmVlmsDocs> selectDocsCcxdlList(@Param("params") GetQueryCriteria queryCriteria);
}
