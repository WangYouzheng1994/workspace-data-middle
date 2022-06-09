package com.yqwl.datamiddle.realtime.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 一单到底宽表
 * </p>
 *
 * @author yiqi
 * @since 2022-06-07
 */
@Data
public class DwmSptb02Vo {

    /**
     * 开始站台 应用于铁路
     */

    private String START_PLATFORM_NAME;

    /**
     * 目的站台 应用于铁路
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
     * 卸车时间  应用于铁路
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
     * 卸船时间 应用水路
     */

    private Long UNLOAD_SHIP_TIME;

    /**
     * 末端分拨中心 入库时间
     */

    private Long IN_DISTRIBUTE_TIME;

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

    /**
     * 结算单编号 多个逗号隔开
     */

    private String SETTLE_NO;

    /**
     * 基地
     */

    private String BASE_NAME;

    /**
     * 整车物流接收STD日期  sptb02.ddjrq
     */

    private Long VEHICLE_RECEIVING_TIME;

    /**
     * 同板数量
     */

    private Integer SAME_PLATE_NUM;

    /**
     * 末端分拨中心 轿车数量
     */

    private Integer DISTRIBUTE_VEHICLE_NUM;

    /**
     * 第一个运单的结算单编号
     */

    private String SETTLEMENT_Y1;

    /**
     * 铁路单结算单编号
     */

    private String RAILWAY_SETTLEMENT_NO;

    /**
     * 水路单结算单编号
     */

    private String WATERWAY_SETTLEMENT_NO;

    /**
     * 末端配送结算单编号
     */

    private String END_DISTRIBUTE_NO;


}
