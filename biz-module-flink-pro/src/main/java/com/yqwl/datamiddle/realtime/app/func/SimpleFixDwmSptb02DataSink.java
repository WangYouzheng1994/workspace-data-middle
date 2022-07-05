package com.yqwl.datamiddle.realtime.app.func;

import com.yqwl.datamiddle.realtime.bean.DwdBaseStationData;
import com.yqwl.datamiddle.realtime.bean.FixDwmsptb02Bean;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import com.yqwl.datamiddle.realtime.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @Description: 单独处理 Sptb02中数据
 * @Author: muqing
 * @Date: 2022/06/08
 * @Version: V1.0
 */
@Slf4j
public class SimpleFixDwmSptb02DataSink<T> extends RichSinkFunction<FixDwmsptb02Bean> {
    @Override
    public void invoke(FixDwmsptb02Bean data, Context context) throws Exception {
        // 结算单编号
        String cjsdbh = data.getCJSDBH();
        // 获取车牌号
        String vjsydm = data.getVJSYDM();
        // 起运地省区代码
        String start_province_code = data.getSTART_PROVINCE_CODE();
        // 起运地县区名称
        String start_city_name = data.getSTART_CITY_NAME();
        // 到货地省区代码
        String end_province_code   = data.getEND_PROVINCE_CODE();
        // 到货地县区名称
        String end_city_name = data.getEND_CITY_NAME();
        if (StringUtils.isNotBlank(cjsdbh) ) {
            String inSql = "UPDATE dwm_vlms_one_order_to_end SET TRANSPORT_VEHICLE_NO= '" + vjsydm + "' ";
            DbUtil.executeUpdate(inSql);
        }
        //==============================================处理车牌号+起运地县区名称=============================================================//
        if ( StringUtils.isNotBlank(start_city_name) ) {
            String inSql = "UPDATE dwm_vlms_one_order_to_end SET START_CITY_NAME = '" + start_city_name + "'     WHERE SETTLEMENT_Y1 ='"
                    + cjsdbh + "' ";
            DbUtil.executeUpdate(inSql);
        }

        //-----------------------------------------------处理车牌号+ 到货地县区名称-----------------------------------------------------------//
        if ( StringUtils.isNotBlank(end_city_name) ) {
            String inSql = "UPDATE dwm_vlms_one_order_to_end SET END_CITY_NAME = '" + end_city_name + "'     WHERE SETTLEMENT_Y1 ='"
                    + cjsdbh + "' ";
            DbUtil.executeUpdate(inSql);
        }
    }
}
