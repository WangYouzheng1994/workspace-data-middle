package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
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
    private String VEHICLE_NAME;

    /**
     * 底盘号
     */
    private String VVIN;

    /**
     * EPC表里,CP9下线接车日期
     */
    private Long EPC_CP9_OFFLINE_TIME;

    /**
     *  新P号(二次配板) 取sptb02.vph
     */
    private String VPH;

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
     * dwm新增-发车基地名称
     */

    private String BASE_NAME;

    /**
     * 基地代码
     */
    private String BASE_CODE;

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
     * 配板编号 取自sptb02.CPZDBH
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
     * 末端分拨中心 轿车数量
     */
    private Integer DISTRIBUTE_VEHICLE_NUM;

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
     * 运输类型
     */
    private String traffic_type;

    /**
     * 公路运单物理仓库对应的仓库类型
     */
    private String  highwayWarehouseType;

    /**
     * 起运日期-公路 20220713新加字段
     */
    private Long SHIPMENT_G_TIME;

    /**
     * DCS到货时间(从sptb02中取)
     */
    private Long DTVSDHSJ;

    /**
     * 是否为公路的运输方式 1为是 0为否 20220719添加
     */
    private Integer TYPE_G;

    /**
     * 是否为铁路的运输方式 1为是 0为否 20220719添加
     */
    private Integer TYPE_T;

    /**
     * 是否为水路的运输方式 1为是 0为否 20220719添加
     */
    private Integer TYPE_S;

    /**
     * 是否同城异地 0无 1同城 2异地
     * 默认值为0  20220801添加
     */
    private Integer TYPE_TC;

    /**
     *发车站台的省区代码
     */
    private String VFCZT_PROVINCE_CODE;

    /**
     * 发车站台的市县代码
     */
    private String VFCZT_CITY_CODE;

    /**
     * 收车站台的省区代码
     */
    private String VSCZT_PROVINCE_CODE;

    /**
     *收车站台的市县代码
     */
    private String VSCZT_CITY_CODE;

    /**
     * R3的配板下发日期 取自SPTB01C.DDJRQ 20220818新增
     */
    private Long VEHICLE_PLATE_ISSUED_TIME_R3;

    /**
     * 汽车品牌名 取自MDAC10.VPPSM 20220825新增
     */
    private String BRAND_NAME;
    /**
     * 逻辑删除标记
     */
    private Integer DELETE_FLAG;

    /**
     * 用来给结算单编号备份的
     */
    private String backupsCjsdbh;

    /**
     * 运单类型
     */
    private String VYSFS;

    /**
     * SPTB02.CQRR
     * 存储区域公司
     */
    private String CQRR;


    /**
     * 末端分拨中心 配载单编号 sptb02.cpzdbh 2022.10.10新增
     */
    private String DISTRIBUTE_CPZDBH;

    /**
     * 末端分拨中心 计划下达时间 SPTB01C.DDJRQ 2022.10.10新增
     */
    private Long DISTRIBUTE_VEHICLE_PLATE_ISSUED_TIME_R3;
}