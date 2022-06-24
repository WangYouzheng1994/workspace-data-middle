package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 一单到底抽取dwd_vlms_sptb02的过渡字段
 * @Author: XiaoFeng
 * @Date: 2022/6/7 14:05
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@CamelUnderline(isChange = false)
public class OotdTransition {
    /**
     * 结算单编号
     */
    private String CJSDBH;
    /**
     * 车型代码
     */
    private String VEHICLE_CODE;

    /**
     * 车型名称
     */
    private String VEHICLE_NMAE;

    /**
     * 底盘号
     */
    private String VVIN;

    /**
     * EPC表里,CP9下线接车日期
     */
    private Long EPC_CP9_OFFLINE_TIME;

    /**
     * 出厂日期 BDS表里的
     */
    private Long BDS_LEAVE_FACTORY_TIME;

    /**
     * 入库日期 BDS表里的
     */
    private Long BDS_IN_SITE_TIME;

    /**
     * 入库仓库名称 BDS表里的
     */
    private String BDS_BASE_NAME;

    /**
     * 整车物流接受STD日期
     */
    private Long DDJRQ;

    /**
     * 任务单号
     */
    private String CJHDH;

    /**
     * 配板日期
     */
    private Long DPZRQ;

    /**
     * 配板编号
     */
    private String CPZDBH;

    /**
     * 指派运输商日期
     */
    private Long ASSIGN_TIME;

    /**
     * 指派运输商名称
     */
    private String ASSIGN_NAME;

    /**
     * 出库日期
     */
    private Long ACTUAL_OUT_TIME;

    /**
     * 起运日期-公路/铁路
     */
    private Long SHIPMENT_TIME;

    /**
     * 运输车号
     */
    private String VJSYDM;

    /** todo:从其他的表里面来的
     * 轿运车车位数 G
     */
    private Integer JYCCWS;

    /**
     * 同板数量
     */
    private Integer VNUM;

    /**
     * 始发城市
     */
    private String START_CITY_NAME;

    /**
     * 目的城市
     */
    private String END_CITY_NAME;

    /**
     * 经销商代码*(名称)
     */
    private String VDWDM;

    /**
     * 经销商代码*(名称)
     */
    private String DEALER_NAME;

    /**
     * 开始站台 应用于铁路 如青岛站
     */
    private String START_PLATFORM_NAME;

    /**
     * 目的站台 应用于铁路 如潍坊站
     */
    private String END_PLATFORM_NAME;

    /**
     * 入开始站台时间 应用于铁路
     */
    private Long IN_START_PLATFORM_TIME;

    /**
     * 出开始站台时间 应用于铁路
     */
    private Long OUT_START_PLATFORM_TIME;

    /**
     * 入目的站台时间 应用于铁路
     */
    private Long IN_END_PLATFORM_TIME;

    /**
     * 卸车时间 应用于铁路
     */
    private Long UNLOAD_RAILWAY_TIME;

    /**
     * 开始港口名称 应用于水路
     */
    private String START_WATERWAY_NAME;

    /**
     * 目的港口名称 应用于水路
     */
    private String END_WATERWAY_NAME;

    /**
     * 入开始港口时间 应用于水路
     */
    private Long IN_START_WATERWAY_TIME;

    /**
     * 出开始港口时间 应用于水路
     */
    private Long END_START_WATERWAY_TIME;

    /**
     * 入目的港口时间 应用于水路
     */
    private Long IN_END_WATERWAY_TIME;

    /**
     * 卸船时间 应用于水路
     */
    private Long UNLOAD_SHIP_TIME;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;

    /**
     * 品牌
     */
    private String BRAND;

    //=============末端配送===================//

    /**
     * 末端分拨中心 入库时间
     */

    private Long IN_DISTRIBUTE_TIME;

    /**
     * 末端分拨中心 配板时间
     */

    private Long DISTRIBUTE_BOARD_TIME;

    /**
     * 末端分拨中心 出库时间
     */

    private Long OUT_DISTRIBUTE_TIME;

    /**
     * 末端分拨中心 指派时间
     */

    private Long DISTRIBUTE_ASSIGN_TIME;

    /**
     * 末端分拨中心 承运商名称
     */

    private String DISTRIBUTE_CARRIER_NAME;

    /**
     * 末端分拨中心 承运轿车车牌号
     */

    private String DISTRIBUTE_VEHICLE_NO;

    /**
     * 末端分拨中心 起运时间
     */

    private Long DISTRIBUTE_SHIPMENT_TIME;

    /**
     * 公路打点到货时间
     */

    private Long DOT_SITE_TIME;

    /**
     * 最终到货时间
     */

    private Long FINAL_SITE_TIME;



}
