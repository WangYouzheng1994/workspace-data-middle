package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 20191026 大屏展示 默认仓库表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RfidWarehouse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer ID;

    /**
     * 库房代码
     */

    private String WAREHOUSE_CODE;

    /**
     * 库房名称
     */

    private String WAREHOUSE_NAME;

    /**
     * 归属基地
     */

    private String PART_BASE;

    /**
     * 库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）
     */

    private String WAREHOUSE_TYPE;

    /**
     * 站台对应的城市
     */

    private String CITY;

    /**
     * 默认在库量  number 转 int
     */

    private Integer DEFALUT_VALUE;

    /**
     * 标准库容     number 转 int
     */

    private Integer BZKR;

    /**
     * 最大库容     number 转 int
     */

    private Integer ZDKR;

    /**
     * 数据中台代码
     */

    private String DATACENTER_CODE;

    /**
     * 顺序 2019 12 23 宋琳添加   number 转 int
     */

    private Integer SEQUENCE;

    /**
     * 省份2019 12 23 宋琳添加
     */

    private String PROVINCE;

    /**
     * 整车数据代码
     */

    private String ZC_CODE;

    /**
     * 主机公司代码 1 大众  2 奔腾 3解放  17 红旗  29 马自达
     */

    private String CZJGSDM;

    /**
     * 数据中台代码备份
     */

    private String DATACENTER_CODE_BZK;

    /**
     * 所属省份
     */

    private String PROVICE;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
