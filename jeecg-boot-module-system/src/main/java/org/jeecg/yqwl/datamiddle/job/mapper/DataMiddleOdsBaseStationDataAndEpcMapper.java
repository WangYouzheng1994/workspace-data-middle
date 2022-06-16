package org.jeecg.yqwl.datamiddle.job.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.job.entity.BaseStationData;
import org.jeecg.yqwl.datamiddle.job.entity.BaseStationDataEpc;
import org.jeecg.yqwl.datamiddle.job.entity.DwdBaseStationDataEpc;
import org.jeecg.yqwl.datamiddle.job.entity.DwmVlmsOneOrderToEnd;


import java.util.List;
import java.util.Map;


/**
 * @Description: 关于查询ods_BaseStationDataAndEpc的表
 * @Author: XiaoFeng
 * @Date: 2022/6/13 14:58
 * @Version: V1.0
 */
public interface DataMiddleOdsBaseStationDataAndEpcMapper extends BaseMapper<BaseStationData> {
    List<BaseStationDataEpc>  getBaseStationDataEpcList(@Param("startDateStr")Long startDateStr, @Param("endDateStr")Long endDateStr, @Param("limitStart")Integer limitStart, @Param("limitEnd")Integer limitEnd);

    /**
     * 按照sample_u_t_c时间查询ods_base_station_data的数据
     * @param startDateStr
     * @param endDateStr
     * @param limitStart
     * @param limitEnd
     * @return
     */
    List<BaseStationData> getOdsVlmsBaseStationData(@Param("startDateStr")Long startDateStr, @Param("endDateStr")Long endDateStr, @Param("limitStart") Integer limitStart, @Param("limitEnd")Integer limitEnd);

    /**
     * 插入一单到底的表
     * @param dwmVlmsOneOrderToEnd
     * @return
     */
    Integer addDwmOOTD(@Param("params") DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd);

    Integer addDwmOOTDBase(@Param("params") DwdBaseStationDataEpc dwdBaseStationDataEpc);

    /**
     * 更新一单到底的出厂日期
     * @param SAMPLE_U_T_C
     * @param VIN
     * @return
     */
    Integer updateOOTDLeaveFactoryTime(@Param("SAMPLE_U_T_C") Long SAMPLE_U_T_C, @Param("VIN") String VIN, @Param("WareHouseTime") Long WareHouseTime);

    /**
     * 更新一单到底的Cp9(EPC)下线时间
     * @param OPERATETIME
     * @param VIN
     * @return
     */
    Integer updateCp9OffLineTime(@Param("OPERATETIME") Long OPERATETIME, @Param("VIN") String VIN, @Param("WareHouseTime") Long WareHouseTime);
    /**
     * 更新一单到底的入库时间
     * @param SAMPLE_U_T_C
     * @param VIN
     */
    Integer updateOOTDInSiteTime(@Param("SAMPLE_U_T_C") Long SAMPLE_U_T_C, @Param("VIN") String VIN, @Param("WareHouseTime") Long WareHouseTime);
}
