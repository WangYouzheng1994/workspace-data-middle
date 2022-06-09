package com.yqwl.datamiddle.realtime.app.func;

import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 单独处理 DwdBaseStationData中数据
 * @Author: muqing
 * @Date: 2022/06/08
 * @Version: V1.0
 */
@Slf4j
public class BaseStationDataSink<T> extends RichSinkFunction<DwdBaseStationData> {
    //入库标识
    private static final String IN_STORE = "InStock";
    //出库标识
    private static final String OUT_STOCK = "OutStock";

    @Override
    public void invoke(DwdBaseStationData data, Context context) throws Exception {
        //获取操作类型
        String operateType = data.getOPERATE_TYPE();
        //==============================================处理铁路运单=============================================================//
        //1.查询铁路运单 根据仓库代码 vvin码定位一条记录 ,每一个站台都会有两个时间，入站台时间和出站台时间
        // 查询开始站台的运单记录
        String startSiteSql = "SELECT IN_START_PLATFORM_TIME, OUT_START_PLATFORM_TIME FROM dwm_vlms_sptb02 WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
        List<Map<String, Object>> listStart = DbUtil.executeQuery(startSiteSql);
        if (CollectionUtils.isNotEmpty(listStart)) {
            Map<String, Object> firstRecord = listStart.get(0);
            // 1.1 处理开始站台的入站台时间
            Object inStartPlatformTime = firstRecord.get("IN_START_PLATFORM_TIME");
            //当前查询到记录值为空，从来没有被更新过
            if (IN_STORE.equals(operateType)) {
                if (Objects.nonNull(inStartPlatformTime)) {
                    long inStartPlatformTimeL = Long.parseLong(inStartPlatformTime.toString());
                    //之前没有更新过
                    if (inStartPlatformTimeL == 0) {
                        //判断入库 更新入站台时间
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(inSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND IN_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }
                }

            }
            // 1.2 处理开始站台的出站台时间
            Object outStartPlatformTime = firstRecord.get("OUT_START_PLATFORM_TIME");
            if (OUT_STOCK.equals(operateType)) {
                if (Objects.nonNull(outStartPlatformTime)) {
                    long outStartPlatformTimeL = Long.parseLong(outStartPlatformTime.toString());
                    //之前没有更新过
                    if (outStartPlatformTimeL == 0) {
                        //判断出库 更新出站台时间
                        String outSql = "UPDATE dwm_vlms_sptb02 SET OUT_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(outSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET OUT_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'  AND OUT_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }
                }
            }
        }


        //1.3 处理目的站台的入站台时间和出站台时间
        String endSiteSql = "SELECT IN_END_PLATFORM_TIME, UNLOAD_RAILWAY_TIME FROM dwm_vlms_sptb02 WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
        List<Map<String, Object>> listEnd = DbUtil.executeQuery(endSiteSql);
        if (CollectionUtils.isNotEmpty(listEnd)) {
            Map<String, Object> firstRecord = listEnd.get(0);
            //处理入目的站台时间
            Object inEndPlatformTime = firstRecord.get("IN_END_PLATFORM_TIME");
            if (IN_STORE.equals(operateType)) {
                //当前查询到记录值为空，从来没有被更新过
                if (Objects.nonNull(inEndPlatformTime)) {
                    long inEndPlatformTimeL = Long.parseLong(inEndPlatformTime.toString());
                    if (inEndPlatformTimeL == 0) {
                        //判断入库 更新入站台时间
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(inSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND IN_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }

                }
            }
            //处理出目的站台时间
            Object unloadRailwayTime = firstRecord.get("UNLOAD_RAILWAY_TIME");
            if (OUT_STOCK.equals(operateType)) {
                //当前查询到记录值为空，从来没有被更新过
                if (Objects.nonNull(unloadRailwayTime)) {
                    long unloadRailwayTimeL = Long.parseLong(unloadRailwayTime.toString());
                    if (unloadRailwayTimeL == 0) {
                        //判断出库 更新出站台时间
                        String outSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_RAILWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(outSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_RAILWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS in ('L1', 'T') AND END_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'  AND UNLOAD_RAILWAY_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }
                }
            }
        }

          //==============================================处理水路运单运单=============================================================//
        //1.查询铁路运单 根据仓库代码 vvin码定位一条记录 ,每一个站台都会有两个时间，入站台时间和出站台时间
        // 查询开始站台的运单记录
        String startWaterwaySql = "SELECT IN_START_WATERWAY_TIME, END_START_WATERWAY_TIME FROM dwm_vlms_sptb02 WHERE VYSFS = 'S' AND START_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
        List<Map<String, Object>> listStartWaterway = DbUtil.executeQuery(startWaterwaySql);
        if (CollectionUtils.isNotEmpty(listStartWaterway)) {
            Map<String, Object> firstRecord = listStartWaterway.get(0);
            // 1.1 处理开始站台的入站台时间
            Object inStartPlatformTime = firstRecord.get("IN_START_WATERWAY_TIME");
            //当前查询到记录值为空，从来没有被更新过
            if (IN_STORE.equals(operateType)) {
                if (Objects.nonNull(inStartPlatformTime)) {
                    long inStartPlatformTimeL = Long.parseLong(inStartPlatformTime.toString());
                    //之前没有更新过
                    if (inStartPlatformTimeL == 0) {
                        //判断入库 更新入站台时间
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS = 'S' AND START_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(inSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND IN_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }
                }

            }
            // 1.2 处理开始站台的出站台时间
            Object outStartPlatformTime = firstRecord.get("END_START_WATERWAY_TIME");
            if (OUT_STOCK.equals(operateType)) {
                if (Objects.nonNull(outStartPlatformTime)) {
                    long outStartPlatformTimeL = Long.parseLong(outStartPlatformTime.toString());
                    //之前没有更新过
                    if (outStartPlatformTimeL == 0) {
                        //判断出库 更新出站台时间
                        String outSql = "UPDATE dwm_vlms_sptb02 SET END_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(outSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET END_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'  AND END_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }
                }
            }
        }


        //1.3 处理目的站台的入站台时间和出站台时间
        String endWaterwaySql = "SELECT IN_END_WATERWAY_TIME, UNLOAD_SHIP_TIME FROM dwm_vlms_sptb02 WHERE  VYSFS = 'S' AND END_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
        List<Map<String, Object>> listWaterwayEnd = DbUtil.executeQuery(endWaterwaySql);
        if (CollectionUtils.isNotEmpty(listWaterwayEnd)) {
            Map<String, Object> firstRecord = listWaterwayEnd.get(0);
            //处理入目的站台时间
            Object inEndPlatformTime = firstRecord.get("IN_END_WATERWAY_TIME");
            if (IN_STORE.equals(operateType)) {
                //当前查询到记录值为空，从来没有被更新过
                if (Objects.nonNull(inEndPlatformTime)) {
                    long inEndPlatformTimeL = Long.parseLong(inEndPlatformTime.toString());
                    if (inEndPlatformTimeL == 0) {
                        //判断入库 更新入站台时间
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(inSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND IN_END_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }

                }
            }
            //处理出目的站台时间
            Object unloadRailwayTime = firstRecord.get("UNLOAD_SHIP_TIME");
            if (OUT_STOCK.equals(operateType)) {
                //当前查询到记录值为空，从来没有被更新过
                if (Objects.nonNull(unloadRailwayTime)) {
                    long unloadRailwayTimeL = Long.parseLong(unloadRailwayTime.toString());
                    if (unloadRailwayTimeL == 0) {
                        //判断出库 更新出站台时间
                        String outSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_SHIP_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='" + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'";
                        DbUtil.executeUpdate(outSql);
                        //之前有更新过此字段值
                    } else {
                        String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_SHIP_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS in ('L1', 'T') AND END_WAREHOUSE_CODE='"
                                + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "'  AND UNLOAD_SHIP_TIME > " + data.getSAMPLE_U_T_C() + "";
                        DbUtil.executeUpdate(inSql);
                    }
                }
            }
        }














    }
}
