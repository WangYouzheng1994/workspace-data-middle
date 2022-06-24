package com.yqwl.datamiddle.realtime.app.func;

import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/6/24 14:44
 * @Version: V1.0
 */
@Slf4j
public class SimpleBsdSinkOOTD<T> extends RichSinkFunction<DwdBaseStationData> {
    @Override
    public void invoke(DwdBaseStationData dbsData, Context context) throws Exception {
        System.out.println("start" + System.currentTimeMillis());
        // 获取Vin码
        String vin = dbsData.getVIN();
        // 获取仓库代码
        String in_warehouse_code = dbsData.getIN_WAREHOUSE_CODE();
        // 获取仓库名称
        String in_warehouse_name = dbsData.getIN_WAREHOUSE_NAME();
        // 获取当前时间
        Long nowTime = System.currentTimeMillis();
        // 获取采样完成时间
        Long sample_u_t_c = dbsData.getSAMPLE_U_T_C();
        // 获取出库操作记录
        String operate_type = dbsData.getOPERATE_TYPE();

        StringBuilder sb = new StringBuilder();
        //-------------------------------------处理更新Mysql----------------------------------//
        // 1.插入mysql 更新IN_WAREHOUSE_NAME，IN_WAREHOUSE_CODE 仓库代码,仓库名称  **最慢**
        if (StringUtils.isNotBlank(vin) && StringUtils.isNotBlank(in_warehouse_code)) {
            sb.append("UPDATE dwm_vlms_one_order_to_end SET IN_WAREHOUSE_NAME = '" + in_warehouse_name + "' , IN_WAREHOUSE_CODE= '"
                    + in_warehouse_code + "' , WAREHOUSE_UPDATETIME = " + nowTime + " WHERE VIN = '" + vin + "' ");
            /*String IN_WAREHOUSE_NAMESql = "UPDATE dwm_vlms_one_order_to_end SET IN_WAREHOUSE_NAME = '" + in_warehouse_name + "' , IN_WAREHOUSE_CODE= '"
                    + in_warehouse_code + "' , WAREHOUSE_UPDATETIME = " + nowTime + " WHERE VIN = '" + vin + "' ";
                DbUtil.executeUpdate(IN_WAREHOUSE_NAMESql);*/

        // 2.更新基地入库时间 **快**
            if (sample_u_t_c !=null){
                sb.append("UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a SET e.IN_SITE_TIME = " + sample_u_t_c +
                                " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.LEAVE_FACTORY_TIME < " + sample_u_t_c + " AND a.WAREHOUSE_TYPE = 'T1' "
                                + "AND (e.IN_SITE_TIME > " + sample_u_t_c + " or e.IN_SITE_TIME = 0)");
                /*String IN_SITE_TIMESql = "UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a SET e.IN_SITE_TIME = " + sample_u_t_c +
                        " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.LEAVE_FACTORY_TIME < " + sample_u_t_c + " AND a.WAREHOUSE_TYPE = 'T1' "
                        + "AND (e.IN_SITE_TIME > " + sample_u_t_c + " or e.IN_SITE_TIME = 0) ";
                DbUtil.executeUpdate(IN_SITE_TIMESql);*/

        // 3.更新末端配送入库时间  **慢**
                sb.append("UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a JOIN dwm_vlms_sptb02 s SET e.IN_DISTRIBUTE_TIME = " + sample_u_t_c +
                                " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.LEAVE_FACTORY_TIME < " + sample_u_t_c + " AND a.WAREHOUSE_TYPE = 'T2' AND s.VYSFS = 'G' "
                                + "AND e.IN_SITE_TIME < " + sample_u_t_c + "");
                /*String IN_DISTRIBUTE_TIMESql = "UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a SET e.IN_DISTRIBUTE_TIME = " + sample_u_t_c +
                        " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.LEAVE_FACTORY_TIME < " + sample_u_t_c + " AND a.WAREHOUSE_TYPE = 'T2' "
                        + "AND e.IN_SITE_TIME < " + sample_u_t_c ;
                DbUtil.executeUpdate(IN_DISTRIBUTE_TIMESql);*/

        // 4.过滤出所有出库操作记录  ** 快
                if (StringUtils.equals(operate_type,"OutStock")){
        // 5.更新出厂日期   ** 快
                    sb.append("UPDATE dwm_vlms_one_order_to_end e JOIN ods_vlms_base_station_data a SET e.LEAVE_FACTORY_TIME = " + sample_u_t_c +
                                    " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.CP9_OFFLINE_TIME < " + sample_u_t_c + " AND (a.SHOP_NO = 'DZCP901' OR a.SHOP_NO = 'DZCP9' ) "
                                    + "AND a.OPERATE_TYPE='OutStock'  AND ( e.LEAVE_FACTORY_TIME = 0 OR e.LEAVE_FACTORY_TIME > " + sample_u_t_c + ")");
                    /*String  LEAVE_FACTORY_TIMESql = "UPDATE dwm_vlms_one_order_to_end e JOIN ods_vlms_base_station_data a SET e.LEAVE_FACTORY_TIME = " + sample_u_t_c +
                                        " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.CP9_OFFLINE_TIME < " + sample_u_t_c + " AND (a.SHOP_NO = 'DZCP901' OR a.SHOP_NO = 'DZCP9' ) "
                                        + "AND a.OPERATE_TYPE='OutStock'  AND ( e.LEAVE_FACTORY_TIME = 0 OR e.LEAVE_FACTORY_TIME > " + sample_u_t_c + ")";
                    DbUtil.executeUpdate(LEAVE_FACTORY_TIMESql);
                    log.info("sql: {}",LEAVE_FACTORY_TIMESql);*/
                }
            }
        }

        if (sb.length() > 0) {
            DbUtil.executeBatchUpdate(sb.toString());
        }

        System.out.println("end" + System.currentTimeMillis());
    }
}
