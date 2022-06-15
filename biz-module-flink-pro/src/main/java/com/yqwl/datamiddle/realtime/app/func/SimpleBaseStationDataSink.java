package com.yqwl.datamiddle.realtime.app.func;

import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
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
        //获取操作类型
        String operateType = data.getOPERATE_TYPE();
        //==============================================处理铁路运单=============================================================//
        //1.查询铁路运单 根据仓库代码 vvin码定位一条记录 ,每一个站台都会有两个时间，入站台时间和出站台时间
        // 查询开始站台的运单记录
        //当前查询到记录值为空，从来没有被更新过
        if (IN_STORE.equals(operateType)) {
            log.info("vvin码：" + data.getVIN());
            String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( IN_START_PLATFORM_TIME = 0 OR IN_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " )";
            log.info("执行sql："+ inSql);
            int i = DbUtil.executeUpdate(inSql);
            log.info("执行结果："+ i);
        }
        // 1.2 处理开始站台的出站台时间
        if (OUT_STOCK.equals(operateType)) {
            String inSql = "UPDATE dwm_vlms_sptb02 SET OUT_START_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND START_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( OUT_START_PLATFORM_TIME = 0 OR OUT_START_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);
        }

        //1.3 处理目的站台的入站台时间和出站台时间
        if (IN_STORE.equals(operateType)) {
            String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_PLATFORM_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS IN ('L1', 'T') AND END_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( IN_END_PLATFORM_TIME = 0 OR IN_END_PLATFORM_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);
        }
        //处理出目的站台时间
        if (OUT_STOCK.equals(operateType)) {
            String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_RAILWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE VYSFS in ('L1', 'T') AND END_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( UNLOAD_RAILWAY_TIME = 0 OR UNLOAD_RAILWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);
        }

        //==============================================处理水路运单运单=============================================================//
        // 查询开始站台的运单记录
        //当前查询到记录值为空，从来没有被更新过
        if (IN_STORE.equals(operateType)) {

            String inSql = "UPDATE dwm_vlms_sptb02 SET IN_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( IN_START_WATERWAY_TIME = 0 OR IN_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);
        }
        // 1.2 处理开始站台的出站台时间
        if (OUT_STOCK.equals(operateType)) {
            String inSql = "UPDATE dwm_vlms_sptb02 SET END_START_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND START_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( END_START_WATERWAY_TIME = 0 OR END_START_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);
        }

        //1.3 处理目的站台的入站台时间和出站台时间
        if (IN_STORE.equals(operateType)) {
            String inSql = "UPDATE dwm_vlms_sptb02 SET IN_END_WATERWAY_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( IN_END_WATERWAY_TIME = 0 OR IN_END_WATERWAY_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);
        }
        //处理出目的站台时间
        if (OUT_STOCK.equals(operateType)) {
            String inSql = "UPDATE dwm_vlms_sptb02 SET UNLOAD_SHIP_TIME=" + data.getSAMPLE_U_T_C() + " WHERE  VYSFS = 'S' AND END_WAREHOUSE_CODE='"
                    + data.getIN_WAREHOUSE_CODE() + "' AND VVIN='" + data.getVIN() + "' AND ( UNLOAD_SHIP_TIME = 0 OR UNLOAD_SHIP_TIME > " + data.getSAMPLE_U_T_C() + " )";
            DbUtil.executeUpdate(inSql);

        }

    }
}
