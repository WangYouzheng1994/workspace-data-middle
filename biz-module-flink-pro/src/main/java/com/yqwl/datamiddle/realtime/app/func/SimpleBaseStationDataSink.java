package com.yqwl.datamiddle.realtime.app.func;

import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @Description: 单独处理 DwdBaseStationData中数据
 * @Author: muqing
 * @Date: 2022/06/08
 * @Version: V1.0
 */
@Slf4j
public class SimpleBaseStationDataSink<T> extends RichSinkFunction<DwdBaseStationData> {
    //入库标识
    private static final String IN_STORE = "InStock";
    //出库标识
    private static final String OUT_STOCK = "OutStock";

    @Override
    public void invoke(DwdBaseStationData data, Context context) throws Exception {
        // 获取操作类型
        String operateType = data.getOPERATE_TYPE();
        // 获取vin码
        String vin = data.getVIN();
        String wlckdm = data.getPHYSICAL_CODE();
        String warehouse_type = data.getWAREHOUSE_TYPE();
        String physical_code = data.getPHYSICAL_CODE();
        String operate_type = data.getOPERATE_TYPE();

        StringBuilder sb = new StringBuilder();

        //==============================================处理铁路运单=============================================================//
        //1.查询铁路运单 根据仓库代码 vvin码定位一条记录 ,每一个站台都会有两个时间，入站台时间和出站台时间
        // 查询开始站台的运单记录
        // 当前查询到记录值为空，从来没有被更新过
        if (StringUtils.isNotBlank(data.getVIN())) {
            if (IN_STORE.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_START_PLATFORM_TIME = 0 OR IN_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */

                sb.append("UPDATE dwm_vlms_sptb02 SET IN_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VVIN='" + vin + "' AND VYSFS IN ('L1', 'T') AND START_PHYSICAL_CODE='"
                        + wlckdm + "' AND  ( IN_START_PLATFORM_TIME = 0 OR IN_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }

            // 1.2 处理开始站台的出站台时间
            if (OUT_STOCK.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET OUT_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( OUT_START_PLATFORM_TIME = 0 OR OUT_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET OUT_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( OUT_START_PLATFORM_TIME = 0 OR OUT_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }

            //1.3 处理目的站台的入站台时间和出站台时间
            if (IN_STORE.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_END_PLATFORM_TIME = 0 OR IN_END_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET IN_END_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_END_PLATFORM_TIME = 0 OR IN_END_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }
            //处理出目的站台时间
            if (IN_STORE.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_RAILWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS in ('L1', 'T') AND END_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( UNLOAD_RAILWAY_TIME = 0 OR UNLOAD_RAILWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET UNLOAD_RAILWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS in ('L1', 'T') AND END_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( UNLOAD_RAILWAY_TIME = 0 OR UNLOAD_RAILWAY_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }

            //==============================================处理水路运单运单=============================================================//
            // 查询开始站台的运单记录
            //当前查询到记录值为空，从来没有被更新过
            if (IN_STORE.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_START_WATERWAY_TIME = 0 OR IN_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET IN_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_START_WATERWAY_TIME = 0 OR IN_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }
            // 1.2 处理开始站台的出站台时间
            if (OUT_STOCK.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET END_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( END_START_WATERWAY_TIME = 0 OR END_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET END_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( END_START_WATERWAY_TIME = 0 OR END_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }

            //1.3 处理目的站台的入站台时间和出站台时间
            if (IN_STORE.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_END_WATERWAY_TIME = 0 OR IN_END_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET IN_END_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( IN_END_WATERWAY_TIME = 0 OR IN_END_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }
            //处理出目的站台时间
            if (IN_STORE.equals(operateType)) {
            /* String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_SHIP_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_PHYSICAL_CODE='"
                    + wlckdm + "' AND VVIN='" + vin + "' AND ( UNLOAD_SHIP_TIME = 0 OR UNLOAD_SHIP_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET UNLOAD_SHIP_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_PHYSICAL_CODE='"
                        + wlckdm + "' AND VVIN='" + vin + "' AND ( UNLOAD_SHIP_TIME = 0 OR UNLOAD_SHIP_TIME > " + data.getSAMPLE_U_T_C() + " );");
            }
            // 2.将库房类型WAREHOUSE_TYPE更新到dwm_sptb02中去  前置条件: 仓库种类不为空,物理仓库代码不为空,vin码不为空,出入库类型为出库.
            if (StringUtils.isNotBlank(warehouse_type) && StringUtils.isNotBlank(physical_code) && StringUtils.isNotBlank(vin) && StringUtils.equals(operate_type, "OutStock")) {
            /* //执行sql前的条件
            String dwmSptb02Sql = "UPDATE dwm_vlms_sptb02 SET HIGHWAY_WAREHOUSE_TYPE= '" + warehouse_type + "' WHERE  VYSFS = 'G' AND VWLCKDM = '" + physical_code + "' AND VVIN ='" + vin + "'";
            DbUtil.executeUpdate(dwmSptb02Sql); */
                sb.append("UPDATE dwm_vlms_sptb02 SET HIGHWAY_WAREHOUSE_TYPE= '" + warehouse_type + "' WHERE  VYSFS = 'G' AND VWLCKDM = '" + physical_code + "' AND VVIN ='" + vin + "';");
            }
        }

        if (sb.length() > 0) {
            DbUtil.executeBatchUpdate(sb.toString());
        }
    }
}
