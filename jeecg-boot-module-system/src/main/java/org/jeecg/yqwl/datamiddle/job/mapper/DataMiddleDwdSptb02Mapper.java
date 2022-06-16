package org.jeecg.yqwl.datamiddle.job.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmSptb02;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.TableParams;


import java.util.List;
import java.util.Map;

public interface DataMiddleDwdSptb02Mapper extends BaseMapper<DwdSptb02> {

    /**
     * 查询dwdsptb02表数据
     * @param rowNumber  行号
     * @param startDateStr  开始时间
     * @param endDateStr   结束时间
     * @param limitStart   从第几条数据开始
     * @param limitEnd    返回的数据条数
     * @return
     */
    List<DwdSptb02> getDwdVlmsSptb02(@Param("rowNumber") Integer rowNumber, @Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr,
                                            @Param("limitStart") Integer limitStart, @Param("limitEnd") Integer limitEnd);

    /**
     * 关联的维度表 表名
     * @param tableParams  表名
     * @return
     */
    Map getTableValues(@Param("params") TableParams tableParams);

    /**
     * 往mysql中的dwmsptb02表里插入数据
     * @param dwmSptb02
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer addDwmSptb02(@Param("params") DwmSptb02 dwmSptb02);
}
