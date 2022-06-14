package org.jeecg.yqwl.datamiddle.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.TableParams;


import java.util.List;
import java.util.Map;

public interface DataMiddleDwdSptb02Mapper extends BaseMapper<DwdSptb02> {

    List<DwdSptb02> getDwdVlmsSptb02(@Param("rowNumber") Integer rowNumber, @Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr,
                                            @Param("limitStart") Integer limitStart, @Param("limitEnd") Integer limitEnd);

    Map getTableValues(@Param("params") TableParams tableParams);

}
