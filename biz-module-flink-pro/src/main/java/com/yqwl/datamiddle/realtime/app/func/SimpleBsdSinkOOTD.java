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
        // 获取Shop_No匹配ware_hosue_code
        String shop_no = dbsData.getSHOP_NO();

        StringBuilder sb = new StringBuilder();
        //-------------------------------------处理更新Mysql----------------------------------//
        // 1.插入mysql 更新IN_WAREHOUSE_NAME，IN_WAREHOUSE_CODE 仓库代码,仓库名称
        if (StringUtils.isNotBlank(vin) && sample_u_t_c != null ) {
            // 1.过滤出所有出库操作记录
            if (StringUtils.equals(operate_type, "OutStock") && (StringUtils.equals(shop_no, "DZCP901") || StringUtils.equals(shop_no, "DZCP9"))) {
                // 1.1.更新出厂日期
                sb.append("UPDATE dwm_vlms_one_order_to_end e  SET e.LEAVE_FACTORY_TIME = " + sample_u_t_c +
                        " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.CP9_OFFLINE_TIME < " + sample_u_t_c +
                        " AND ( e.LEAVE_FACTORY_TIME = 0 OR e.LEAVE_FACTORY_TIME > " + sample_u_t_c + ");");
            }
            if (StringUtils.isNotBlank(in_warehouse_code) ) {
                    // 2.更新入库日期,入库仓库名称,入库仓库代码,更新更新时间
                    // sb.append("UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a on e.IN_WAREHOUSE_CODE = a.WAREHOUSE_CODE SET e.IN_WAREHOUSE_NAME= '" +in_warehouse_name+ "' , e.IN_WAREHOUSE_CODE= '"
                     sb.append("UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a on a.WAREHOUSE_CODE ='"+ in_warehouse_code +"' SET e.IN_WAREHOUSE_NAME= '" +in_warehouse_name+ "' , e.IN_WAREHOUSE_CODE= '"
                             + in_warehouse_code + "', e.IN_SITE_TIME = " + sample_u_t_c +
                            " , e.WAREHOUSE_UPDATETIME = " + nowTime + "  WHERE e.VIN = '" + vin + "'  AND e.LEAVE_FACTORY_TIME < " + sample_u_t_c + " AND a.WAREHOUSE_TYPE = 'T1' "
                            + "AND (e.IN_SITE_TIME > " + sample_u_t_c + " or e.IN_SITE_TIME = 0);");
                    if (StringUtils.equals(operate_type, "InStock")) {
                        // 3.更新末端配送入库时间
                        sb.append("UPDATE dwm_vlms_one_order_to_end e JOIN dim_vlms_warehouse_rs a on a.WAREHOUSE_CODE = '" + in_warehouse_code +
                                "' JOIN dwm_vlms_sptb02 s on e.VIN = s.VVIN SET e.IN_DISTRIBUTE_TIME = " + sample_u_t_c +
                                " , e.WAREHOUSE_UPDATETIME = " + nowTime + " WHERE e.VIN = '" + vin + "'  AND e.LEAVE_FACTORY_TIME < " + sample_u_t_c + " AND a.WAREHOUSE_TYPE = 'T2' AND s.TRAFFIC_TYPE = 'G' "
                                + " AND e.IN_SITE_TIME < " + sample_u_t_c + ";");
                    }
            }

        }

        if (sb.length() > 0) {
            DbUtil.executeBatchUpdate(sb.toString());
        }

    }
}
