package org.jeecg.yqwl.datamiddle.ads.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.jeecg.yqwl.datamiddle.ads.order.vo.SelectData;

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
     * @param page
     * @return
     */
    List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(@Param("params") GetQueryCriteria queryCriteria,  Page<DwmVlmsOneOrderToEnd> page);


    /**
     * 同板数量
     * @return
     */
    List<SelectData> selectTotal(@Param("stowageNoteNo") String stowageNoteNo);


}
