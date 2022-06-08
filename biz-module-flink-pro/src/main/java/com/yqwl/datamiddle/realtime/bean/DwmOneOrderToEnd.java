package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 一单到底宽表
 * </p>
 *
 * @author yuluo
 * @since 2022-06-07
 */
@CamelUnderline(isChange = false)
@TableName("dwm_vlms_one_order_to_end")
@Data
@EqualsAndHashCode(callSuper = false)
public class DwmOneOrderToEnd implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 车架号底盘号
     */
    private String VIN;
    /**
     * 车型代码
     */

    private String VEHICLE_CODE;

    /**
     * 车型名称
     */

    private String VEHICLE_NAME;

    /**
     * 底盘号
     */

    private String BRAND;

    /**
     * cp9下线接车时间
     */

    private Long CP9_OFFLINE_TIME;

    /**
     * 出厂日期
     */

    private Long LEAVE_FACTORY_TIME;

    /**
     * 入库时间  及入一汽物流基地
     */

    private Long IN_SITE_TIME;

    /**
     * 入库仓库代码
     */

    private String IN_WAREHOUSE_CODE;

    /**
     * 入库名称
     */

    private String IN_WAREHOUSE_NAME;

    /**
     * 计划下达时间 取sptb02.dpzrq
     */

    private Long PLAN_RELEASE_TIME;

    /**
     * 任务单号 取sptb02.cjhdh
     */

    private String TASK_NO;

    /**
     * 配载单编号 Y号  取sptb02.cpzdbh
     */

    private String STOWAGE_NOTE_NO;

    /**
     * 配载单日期   sptb02.DPZRQ
     */

    private Long STOWAGE_NOTE_TIME;

    /**
     * 运单指派时间
     */

    private Long ASSIGN_TIME;

    /**
     * 运输方式
     */

    private String TRAFFIC_TYPE;

    /**
     * 承运商名称
     */

    private String CARRIER_NAME;

    /**
     * 实际出库时间
     */

    private Long ACTUAL_OUT_TIME;

    /**
     * 实际起运时间
     */

    private Long SHIPMENT_TIME;

    /**
     * 运输车号
     */

    private String TRANSPORT_VEHICLE_NO;

    /**
     * 轿车数量 只计算公路运输
     */

    private Integer VEHICLE_NUM;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;

    /**
     * 始发城市
     */

    private String START_CITY_NAME;

    /**
     * 目的城市
     */

    private String END_CITY_NAME;

    /**
     * 经销商代码
     */

    private String VDWDM;

    /**
     * 经销商名称
     */

    private String DEALER_NAME;

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
     * 运单生成时间
     */

    private Long ORDER_CREATE_TIME;

    /**
     * 基地
     */

    private String BASE_NAME;

    /**
     * 基地代码
     */
    private String BASE_CODE;

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
