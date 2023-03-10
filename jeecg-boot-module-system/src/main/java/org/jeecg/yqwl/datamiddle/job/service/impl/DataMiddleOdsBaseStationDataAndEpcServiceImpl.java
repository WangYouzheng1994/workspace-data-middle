package org.jeecg.yqwl.datamiddle.job.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.yqwl.datamiddle.job.entity.BaseStationData;
import org.jeecg.yqwl.datamiddle.job.entity.BaseStationDataEpc;
import org.jeecg.yqwl.datamiddle.job.entity.DwdBaseStationDataEpc;
import org.jeecg.yqwl.datamiddle.job.entity.DwmVlmsOneOrderToEnd;
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
@Deprecated
public class DataMiddleOdsBaseStationDataAndEpcServiceImpl extends ServiceImpl<DataMiddleOdsBaseStationDataAndEpcMapper, DwdBaseStationDataEpc> implements DataMiddleOdsBaseStationDataAndEpcService {
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
        Long begin13 = 1577808000000L;   //当天00:00:00的13位时间戳
        Long end13 = DateUtils.getToday4NightLong13(); //当天23:59:59的13位时间戳
//                Long begin =1652177391000000L;
//                Long end =1652178367000000L;
//                Long begin13 =1072661789000L;
//                Long end13 =1653943564000L;
        boolean epcHasNext = true;
        boolean dataHasNext = true;
        int interval = 1;

        DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd = null;
        DwdBaseStationDataEpc dwdBaseStationDataEpc = null;
        String in_warehouse_code = null;
        String in_warehouse_name = null;
        String vin = null;
        Long operateTime = null;
        String operateType = null;
        Long sample_u_t_c = null;
        String wlckdm = null;
        String cp =null;
        //插入成功与否
        Integer insertStatusNum=null;
        Integer insertBaseStatusNum=null;
        Integer updateSiteTime=null;
        Integer updateLeaveFactoryTime=null;
        Integer updateCp9=null;
        List<DwdBaseStationDataEpc> dataEpcMapList = null;


        Integer pageStartEpc = 1;
        Integer limitEndEpc = 500;
        // 处理base_station_epc --- START
        do {
            log.info("开始循环, {}", interval++);
            //因为是由epc驱动的,所以开始先查epc的数据.
            dataEpcMapList = this.dataAndEpcMapper.getBaseStationDataEpcList(begin13, end13, (pageStartEpc - 1) * limitEndEpc, limitEndEpc);
            for (DwdBaseStationDataEpc baseStationDataEpc : dataEpcMapList) {
                cp = baseStationDataEpc.getCP();
                vin = baseStationDataEpc.getVIN();
                operateTime = baseStationDataEpc.getOPERATETIME();
                if (StringUtils.isNotBlank(cp) && StringUtils.isNotBlank(vin)){
                    String index = cp.substring(0, 1);
                    // 此处等同于判空 + 判长度
                    if (StringUtils.length(cp) >= 4 ) {
                        String baseCode = cp.substring(0, 4);

                        dwdBaseStationDataEpc= new DwdBaseStationDataEpc();
                        dwdBaseStationDataEpc.setVIN(vin);
                        if (StringUtils.equals(baseCode, "0431")) {
                            dwdBaseStationDataEpc.setBASE_NAME("长春");
                            dwdBaseStationDataEpc.setBASE_CODE("0431");
                        } else if (StringUtils.equals(baseCode, "0757")) {
                            dwdBaseStationDataEpc.setBASE_NAME("佛山");
                            dwdBaseStationDataEpc.setBASE_CODE("0757");
                        } else if (StringUtils.equals(baseCode, "0532")) {
                            dwdBaseStationDataEpc.setBASE_NAME("青岛");
                            dwdBaseStationDataEpc.setBASE_CODE("0532");
                        } else if (StringUtils.equals(baseCode, "028C")) {
                            dwdBaseStationDataEpc.setBASE_NAME("成都");
                            dwdBaseStationDataEpc.setBASE_CODE("028C");
                        } else if (StringUtils.equals(baseCode, "022C")) {
                            dwdBaseStationDataEpc.setBASE_NAME("天津");
                            dwdBaseStationDataEpc.setBASE_CODE("022C");
                        } else {
                            dwdBaseStationDataEpc.setBASE_NAME("");
                            dwdBaseStationDataEpc.setBASE_CODE("");
                        }
                        dwdBaseStationDataEpc.setWAREHOUSE_UPDATETIME(DateUtils.getMillis());
                        // e.插入一单到底的BASE_CODE+BASE_NAME字段(esp)
                        insertBaseStatusNum = this.dataAndEpcMapper.addDwmOOTDBase(dwdBaseStationDataEpc);
                }
            }
                if (operateTime !=null && StringUtils.isNotBlank(vin) ){
                    //注入cp9下线接车日期(esp)
                    updateCp9 = this.dataAndEpcMapper.updateCp9OffLineTime(operateTime, vin, DateUtils.getMillis());
                }

        }
            if (CollectionUtils.isNotEmpty(dataEpcMapList)) {
                // 插入的时候做insertOrUpdate
                if (CollectionUtils.size(dataEpcMapList) != limitEndEpc) {
                    epcHasNext = false;
                } else {
                    pageStartEpc++;
                }
            } else {
                epcHasNext = false;
            }
            dataEpcMapList = null;
        } while(epcHasNext);
        // 处理base_station_epc --- END

        List<BaseStationData> baseStationDataList = null;

        Integer pageStartData = 1;
        Integer limitEndData = 500;
        do {
            baseStationDataList = this.dataAndEpcMapper.getOdsVlmsBaseStationData(begin13, end13, (pageStartData - 1) * limitEndData, limitEndData);
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
                // 用PHYSICAL_CODE 匹配运单中的 START_PHYSICAl_CODE, END_PHYSICAl_CODE 取代 START_WAREHOUSE_CODE, END_WAREHOUSE_CODE
                wlckdm = baseStationData.getPHYSICAL_CODE();

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
                dwmVlmsOneOrderToEnd.setWAREHOUSE_UPDATETIME(DateUtils.getMillis());
                // a.增加一单到底的 入库代码,名称,采样时间
                insertStatusNum = this.dataAndEpcMapper.addDwmOOTD(dwmVlmsOneOrderToEnd);

                // b.更新一单到底的 出厂日期字段
                // c.更新一单到底的 入库时间字段
                if (sample_u_t_c !=null && StringUtils.isNotBlank(vin)) {
                    updateLeaveFactoryTime = this.dataAndEpcMapper.updateOOTDLeaveFactoryTime(sample_u_t_c , vin , DateUtils.getMillis() );
                    updateSiteTime = this.dataAndEpcMapper.updateOOTDInSiteTime(sample_u_t_c, vin ,DateUtils.getMillis());
                }

                log.info("插入一单到底表的入库代码,名称,采样时间完成: {}",insertStatusNum);
                log.info("插入一单到底的表基地字段完成: {}",insertBaseStatusNum);
                log.info("更新一单到底的入库时间完成: {}",updateSiteTime);
                log.info("更新一单到底的出厂日期完成: {}",updateLeaveFactoryTime);
                log.info("更新一单到底的cp9下线日期完成: {}",updateCp9);

                //==============================================处理铁路运单=============================================================//
                //1.查询铁路运单 根据仓库代码 vvin码定位一条记录 ,每一个站台都会有两个时间，入站台时间和出站台时间
                // 查询开始站台的运单记录
                //当前查询到记录值为空，从来没有被更新过
                if (StringUtils.isNotBlank(in_warehouse_code) && StringUtils.isNotBlank(vin) && StringUtils.isNotBlank(operateType) && sample_u_t_c != null) {
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_PLATFORM_TIME=" + baseStationData.getSAMPLE_U_T_C() + "WHERE VYSFS IN ('L1', 'T') AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( IN_START_PLATFORM_TIME = 0 OR IN_START_PLATFORM_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 1.2 处理开始站台的出站台时间
                    if (OUT_STOCK.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET OUT_START_PLATFORM_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T')  AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( OUT_START_PLATFORM_TIME = 0 OR OUT_START_PLATFORM_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //1.3 处理目的站台的入站台时间和出站台时间
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_PLATFORM_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( IN_END_PLATFORM_TIME = 0 OR IN_END_PLATFORM_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //处理出目的站台时间
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_RAILWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE VYSFS in ('L1', 'T')  AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( UNLOAD_RAILWAY_TIME = 0 OR UNLOAD_RAILWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
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

                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_WATERWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE VYSFS = 'S' AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( IN_START_WATERWAY_TIME = 0 OR IN_START_WATERWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 1.2 处理开始站台的出站台时间
                    if (OUT_STOCK.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET END_START_WATERWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( END_START_WATERWAY_TIME = 0 OR END_START_WATERWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //1.3 处理目的站台的入站台时间和出站台时间
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_WATERWAY_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( IN_END_WATERWAY_TIME = 0 OR IN_END_WATERWAY_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //2.处理出目的站台时间
                    if (IN_STORE.equals(operateType)) {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_SHIP_TIME=" + baseStationData.getSAMPLE_U_T_C() + " WHERE VYSFS = 'S' AND END_PHYSICAl_CODE='"
                                + wlckdm + "' AND VVIN='" + baseStationData.getVIN() + "' AND ( UNLOAD_SHIP_TIME = 0 OR UNLOAD_SHIP_TIME > " + baseStationData.getSAMPLE_U_T_C() + " )";
                        try {
                            log.info("展示执行的sql:{}", inSql);
                            DbUtil.executeUpdate(inSql);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(baseStationDataList)) {
                // 插入的时候做insertOrUpdate
                if (CollectionUtils.size(baseStationDataList) != limitEndData) {
                    dataHasNext = false;
                } else{
                    pageStartData++;
                }
            } else {
                dataHasNext = false;
            }
            baseStationDataList = null;
        } while(dataHasNext);

        log.info("结束拉取OdsBsd服务数据");
    }
}