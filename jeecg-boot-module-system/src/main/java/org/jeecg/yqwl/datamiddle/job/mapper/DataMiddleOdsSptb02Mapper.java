package org.jeecg.yqwl.datamiddle.job.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
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
public interface DataMiddleOdsSptb02Mapper extends BaseMapper<DwdSptb02> {

    List<Sptb02> getOdsVlmsSptb02(@Param("rowNumber") Integer rowNumber, @Param("startDateStr")Long startDateStr, @Param("endDateStr")Long endDateStr, @Param("limitStart") Integer limitStart, @Param("limitEnd")Integer limitEnd);

    /**
     * 动态查维表
     * @param tableParams
     * @return
     */
    Map getTableValues(@Param("params") TableParams tableParams);

    /**
     * 插入Mysql的DwdSptb02表
     * @param dwdSptb02
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer addDwdSptb02(@Param("params")DwdSptb02 dwdSptb02);
}
