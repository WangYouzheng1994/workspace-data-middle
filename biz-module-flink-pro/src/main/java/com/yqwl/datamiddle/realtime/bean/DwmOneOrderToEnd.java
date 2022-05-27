package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *一单到底数据宽表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-27
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class DwmOneOrderToEnd implements Serializable  {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    //private Long IDNUM;

    /**
     * 车架号,底盘号
     */
    private String VIN;

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
     * 出厂日期
     */
    private Long LEAVE_FACTORY_TIME;

    /**
     * 入库时间及入一汽物流基地
     */
    private Long IN_SITE_TIME;

    /**
     * 入库名称
     */
    private String IN_WAREHOUSE_NAME;

    /**
     * 计划下达时间,取sptb02.dpzrq
     */
    private Long PLAN_RELEASE_TIME;

    /**
     * 任务单号 取sptb02.cjsdbh
     */
    private String TASK_NO;

    /**
     * 配载单编号 Y号 取sptb02.cpzdbh
     */
    private String STOWAGE_NOTE_NO;

    /**
     * 配载单日期 取sptb02.dpzrq
     */
    private Long STOWAGE_NOTE_TIME;

    /**
     * 运单指派时间
     */
    private Long ASSIGN_TIME;

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
     * 轿车数量  ,只计算公路运输
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
     * 开始站台,应用于铁路
     */
    private String START_PLATFORM_NAME;

    /**
     * 目的站台,应用于铁路
     */
    private String END_PLATFORM_NAME;

    /**
     * 入开始站台时间,应用于铁路
     */
    private Long IN_START_PLATFORM_TIME;

    /**
     * 出开始站台时间,应用于铁路
     */
    private Long OUT_START_PLATFORM_TIME;


    /**
     * 入目的站台时间,应用于铁路
     */
    private Long IN_END_PLATFORM_TIME;

    /**
     * 卸车时间,应用于铁路
     */
    private Long UNLOAD_RAILWAY_TIME;



}
