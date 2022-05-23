package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 铁路运输计划站点表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RailPlanAllotSite implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 收车地址
     */

    private String SCDMZ;

    /**
     * 省份
     */

    private String PROVINCE;

    /**
     * 校验省份
     */

    private String PROVINCE_CHECK;

    /**
     * 市
     */

    private String CITY;

    /**
     * 站点
     */

    private String SITE;

    /**
     * 市校验
     */

    private String CITY_CHECK;

    /**
     * 校验市县代码
     */

    private String PROVINCE_CODE;

    /**
     * 校验省份代码
     */

    private String CITY_CODE;

    /**
     * 操作日期
     */

    private Long DCZRQ;

    /**
     * 操作员
     */

    private String CCZYDM;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
