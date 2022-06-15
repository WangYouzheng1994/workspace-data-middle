package org.jeecg.yqwl.datamiddle.job.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.*;
import org.jeecg.yqwl.datamiddle.job.mapper.DataMiddleOdsBaseStationDataAndEpcMapper;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleOdsBaseStationDataAndEpcService;
import org.jeecg.yqwl.datamiddle.util.DbUtil;
import org.springframework.stereotype.Service;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import javax.annotation.Resource;
import java.util.*;


/**
 * @Description: 定时任务查询OdsBaseStationDataAndEpc表更新到一单到底表dwm_vlms_one_order_to_end
 * @Author: XiaoFeng
 * @Date: 2022/6/15 14:03
 * @Version: V1.0
 */
@Slf4j
@Service
@DS("wareHouse")
public class DataMiddleOdsBaseStationDataAndEpcServiceImpl extends ServiceImpl<DataMiddleOdsBaseStationDataAndEpcMapper, BaseStationData> implements DataMiddleOdsBaseStationDataAndEpcService {
    @Resource
    private DataMiddleOdsBaseStationDataAndEpcMapper dataAndEpcMapper;

    //入库标识
    private static final String IN_STORE = "InStock";
    //出库标识
    private static final String OUT_STOCK = "OutStock";
    /**
     * 查询OdsBaseStationDataAndEpc表更新到一单到底表dwm_vlms_one_order_to_end
     */
    @Override
    public void getOdsVlmsBaseStationDataAndEpc() {
        log.info("开始查询OdsBsd的表");
//        Long begin = DateUtils.getToday4DawnLong13();   //当天00:00:00的13位时间戳
//        Long end = DateUtils.getToday4NightLong13(); //当天23:59:59的13位时间戳
        Long begin =1652177391000000L;
        Long end =1652178367000000L;
        boolean hasNext = true;
        int limit = 500;
        Integer rowNum=0;
        int interval = 1;

        DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd = null;
        String in_warehouse_code = null;
        String in_warehouse_name = null;
        String vin = null;
        String operateType = null;
        Long sample_u_t_c = null;
        String wlckdm = null;
        do {
            log.info("开始循环, {}", interval++);
            List<BaseStationData> baseStationDataList = this.dataAndEpcMapper.getOdsVlmsBaseStationData(rowNum, begin, end, rowNum, limit);
            for (BaseStationData baseStationData : baseStationDataList) {
                dwmVlmsOneOrderToEnd = new DwmVlmsOneOrderToEnd();
                // 与rfidWarehouse表联查出来的仓库Code值
                in_warehouse_code = baseStationData.getIN_WAREHOUSE_CODE();
                // 与rfidWarehouse表联查出来的入库仓库名称
                in_warehouse_name = baseStationData.getIN_WAREHOUSE_NAME();
                // 汽车车架号
                vin = baseStationData.getVIN();
                // 出入库标识
                operateType = baseStationData.getOPERATE_TYPE();
                // 采样完成时间
                sample_u_t_c = baseStationData.getSAMPLE_U_T_C();
                // 用wlckdm 匹配运单中的 START_PHYSICAl_CODE, END_PHYSICAl_CODE 取代 START_WAREHOUSE_CODE, END_WAREHOUSE_CODE
                wlckdm = baseStationData.getWLCKDM();

                if (StringUtils.isNotBlank(in_warehouse_code)){
                    dwmVlmsOneOrderToEnd.setInWarehouseCode(in_warehouse_code);  //入库代码
                }
                if (StringUtils.isNotBlank(in_warehouse_name)){
                    dwmVlmsOneOrderToEnd.setInWarehouseName(in_warehouse_name);  //入库名称
                }
                if (StringUtils.isNotBlank(vin)){
                    dwmVlmsOneOrderToEnd.setVin(vin);
                }
                if (sample_u_t_c !=null){
                    dwmVlmsOneOrderToEnd.setSampleutc(sample_u_t_c);
                }
                Integer integer = this.dataAndEpcMapper.addDwmOOTD(dwmVlmsOneOrderToEnd);  //增加入库代码,名称,采样时间
                if (sample_u_t_c !=null && StringUtils.isNotBlank(vin)){
                    this.dataAndEpcMapper.updateOOTDInSiteTime(sample_u_t_c,vin);          //更新入库时间
                }

                log.info("插入一单到底的表完成: {}",integer);
                //==============================================处理铁路运单=============================================================//
                //1.查询铁路运单 根据仓库代码 vvin码定位一条记录 ,每一个站台都会有两个时间，入站台时间和出站台时间
                // 查询开始站台的运单记录
                //当前查询到记录值为空，从来没有被更新过
                if (StringUtils.isNotBlank(in_warehouse_code) && StringUtils.isNotBlank(vin) && StringUtils.isNotBlank(operateType) && sample_u_t_c != null) {
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET IN_START_PLATFORM_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE TRAFFIC_TYPE IN ('L1', 'T') AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( IN_START_PLATFORM_TIME = 0 OR IN_START_PLATFORM_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 1.2 处理开始站台的出站台时间
                    if (OUT_STOCK.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET OUT_START_PLATFORM_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE TRAFFIC_TYPE IN ('L1', 'T') AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( OUT_START_PLATFORM_TIME = 0 OR OUT_START_PLATFORM_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //1.3 处理目的站台的入站台时间和出站台时间
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET IN_END_PLATFORM_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE TRAFFIC_TYPE IN ('L1', 'T') AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( IN_END_PLATFORM_TIME = 0 OR IN_END_PLATFORM_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //处理出目的站台时间
                    if (OUT_STOCK.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET UNLOAD_RAILWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE TRAFFIC_TYPE in ('L1', 'T') AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( UNLOAD_RAILWAY_TIME = 0 OR UNLOAD_RAILWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    //==============================================处理水路运单运单=============================================================//
                    // 查询开始站台的运单记录
                    //当前查询到记录值为空，从来没有被更新过
                    if (IN_STORE.equals(operateType)) {

                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET IN_START_WATERWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE  TRAFFIC_TYPE = 'S' AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( IN_START_WATERWAY_TIME = 0 OR IN_START_WATERWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 1.2 处理开始站台的出站台时间
                    if (OUT_STOCK.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET END_START_WATERWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE  TRAFFIC_TYPE = 'S' AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( END_START_WATERWAY_TIME = 0 OR END_START_WATERWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //1.3 处理目的站台的入站台时间和出站台时间
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET IN_END_WATERWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE  TRAFFIC_TYPE = 'S' AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( IN_END_WATERWAY_TIME = 0 OR IN_END_WATERWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //处理出目的站台时间
                    if (OUT_STOCK.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_one_order_to_end SET UNLOAD_SHIP_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE  TRAFFIC_TYPE = 'S' AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VIN='" + baseStationData.getVIN() + "' AND ( UNLOAD_SHIP_TIME = 0 OR UNLOAD_SHIP_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
            log.info("开始组装数据进行维表拉宽");
            if (CollectionUtils.isNotEmpty(baseStationDataList)) {
                // 插入的时候做insertOrUpdate
                Integer rn = baseStationDataList.get(baseStationDataList.size() - 1).getRn();
                rowNum = rn; // 记录偏移量
                if (CollectionUtils.size(baseStationDataList) != limit) {
                    hasNext = false;
                }
            } else {
                hasNext = false;
            }
            baseStationDataList = null;
        } while(hasNext);

        log.info("结束拉取OdsBsd服务数据");
    }
}