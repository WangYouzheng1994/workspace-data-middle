package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 20210312 大众新标准物流时间表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Spti32Dz implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运输方式
     */

    private String VYSFS;

    /**
     * 类型:TC同城；YC异城；
     */

    private String CTYPE;

    /**
     * 满载率:DZ大组；ML满列；SH散货；
     */

    private String VMZL;

    /**
     * 始发省区代码
     */

    private String VQYSQDM;

    /**
     * 始发城市代码
     */

    private String VQYSXDM;

    /**
     * 分拨省区代码
     */

    private String VFBSQDM;

    /**
     * 分拨城市代码
     */

    private String VFBSXDM;

    /**
     * 终到省份代码
     */

    private String VMDSQDM;

    /**
     * 终到城市代码
     */

    private String VMDSXDM;

    /**
     * 终到地级市代码
     */

    private String VMDXQDM;

    /**
     * 终到运区代码
     */

    private String VMDYQDM;

    /**
     * 分段物流时间:公路
     */

    private Long NWLSJ_G;

    /**
     * 分段物流时间:集港
     */

    private Long NWLSJ_J;

    /**
     * 分段物流时间:铁水
     */

    private Long NWLSJ_TS;

    /**
     * 分段物流时间:分拨
     */

    private Long NWLSJ_F;

    /**
     * 整体考核物流时间
     */

    private Long NWLSJ_ALL;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
