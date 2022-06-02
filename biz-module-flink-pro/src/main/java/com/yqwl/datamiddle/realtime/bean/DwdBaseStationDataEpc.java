package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * rfid商品车车接口数据
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@TableName("dwd_vlms_base_station_data_epc")
@EqualsAndHashCode(callSuper = false)
@CamelUnderline(isChange = false)
public class DwdBaseStationDataEpc implements Serializable {



    /**
     * 车架号
     */

    private String VIN;

    /**
     * 操作员
     */

    private String OPERATOR;

    /**
     * 操作时间
     */

    private Long OPERATETIME;

    /**
     * Epc码
     */

    private String EPC;

    /**
     * 操作类型  1001新增   1002更新
     */

    private String OPERTYPE;

    /**
     * 工厂下线口
     */

    private String CP;

    /**
     * 站点编号
     */

    private String SHOPNO;

    /**
     * 增加时间
     */

    private Long INSERT_DATE;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;

    /**
     * 车型
     */
    private String VEHICLE_TYPE;

    /**
     * 品牌
     */
    private String BRAND;

    /**
     * cp9下线接车时间
     */
    private Long CP9_OFFLINE_TIME;

    /**
     * 新增更新时间字段
     */
    @TransientSink
    private Timestamp ts;
}
