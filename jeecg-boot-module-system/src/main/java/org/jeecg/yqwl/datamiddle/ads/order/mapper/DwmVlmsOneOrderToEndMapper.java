package org.jeecg.yqwl.datamiddle.ads.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;


import java.util.List;


/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
public interface DwmVlmsOneOrderToEndMapper extends BaseMapper<DwmVlmsOneOrderToEnd> {

    /**
     * 按条件分页查询
     * @param queryCriteria
     * @return
     */
    Integer countOneOrderToEndList(@Param("params") GetQueryCriteria queryCriteria);

    /**
     * 按条件分页查询
     * @param queryCriteria
     * @return
     */
    List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(@Param("params") GetQueryCriteria queryCriteria);


    /**
     * 一单到底---DOCS
     * @return
     */
    List<DwmVlmsDocs> selectDocsList(@Param("params") GetQueryCriteria queryCriteria);



}
