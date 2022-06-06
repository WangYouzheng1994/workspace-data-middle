package org.jeecg.yqwl.datamiddle.ads.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;



/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
public interface DwmVlmsOneOrderToEndMapper extends BaseMapper<DwmVlmsOneOrderToEnd> {


    DwmVlmsOneOrderToEnd selectOneOrderToEndList(@Param("params") GetQueryCriteria queryCriteria);
}
