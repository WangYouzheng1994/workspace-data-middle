package org.jeecg.yqwl.datamiddle.job.mapper;


import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.TableParams;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;

import java.util.List;
import java.util.Map;


/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/6/13 14:58
 * @Version: V1.0
 */
public interface DataMiddleOdsSptb02Mapper {

    List<Sptb02> getOdsVlmsSptb02(@Param("rowNumber") Integer rowNumber, @Param("startDateStr")String startDateStr, @Param("endDateStr")String endDateStr, @Param("limitStart") Integer limitStart, @Param("limitEnd")Integer limitEnd);

    Map getTablValues(@Param("params") TableParams tableParams);

}
