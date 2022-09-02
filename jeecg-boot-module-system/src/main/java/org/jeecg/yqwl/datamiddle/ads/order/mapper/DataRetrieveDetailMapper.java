package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DataRetrieveQuery;

import java.util.List;

/**
* @author Administrator
* @description 针对表【data_retrieve_detail】的数据库操作Mapper
* @createDate 2022-08-29 13:53:06
* @Entity org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveDetail
*/
public interface DataRetrieveDetailMapper extends BaseMapper<DataRetrieveDetail> {

    List<DwmVlmsDocs> selectDocsList(@Param("param") DataRetrieveQuery query);
}




