package org.jeecg.yqwl.datamiddle.job.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.*;

import java.util.List;
import java.util.Map;


/**
 * @Description: 关于查询ods_BaseStationDataAndEpc的表
 * @Author: XiaoFeng
 * @Date: 2022/6/13 14:58
 * @Version: V1.0
 */
public interface DataMiddleOdsBaseStationDataAndEpcMapper extends BaseMapper<BaseStationData> {

    /**
     * 按照sample_u_t_c时间查询ods_base_station_data的数据
     * @param rowNumber
     * @param startDateStr
     * @param endDateStr
     * @param limitStart
     * @param limitEnd
     * @return
     */
    List<BaseStationData> getOdsVlmsBaseStationData(@Param("rowNumber") Integer rowNumber, @Param("startDateStr")Long startDateStr, @Param("endDateStr")Long endDateStr, @Param("limitStart") Integer limitStart, @Param("limitEnd")Integer limitEnd);

    /**
     * 插入一单到底的表
     * @param dwmVlmsOneOrderToEnd
     * @return
     */
    Integer addDwmOOTD(@Param("params") DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd);

    /**
     * 更新一单到底的入库时间
     * @param SAMPLE_U_T_C
     * @param VIN
     */
    void updateOOTDInSiteTime(@Param("SAMPLE_U_T_C") Long SAMPLE_U_T_C, @Param("VIN") String VIN);
}
