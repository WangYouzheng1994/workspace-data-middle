package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 维护铁路批次，或水路批次
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb013 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 批次号
     */

    private String VPH;

    /**
     * 状态：0、初始；1、提交；2、完成
     */

    private String CZT;

    /**
     * 发车仓库
     */

    private String VFCCK;

    /**
     * 发车站台
     */

    private String VFCZT;

    /**
     * 收车站台
     */

    private String VSCZT;

    /**
     * 计划数量
     */

    private Integer NJHSL;

    /**
     * 实际数量
     */

    private Integer NSJSL;

    /**
     * 运输商
     */

    private String VYSS;

    /**
     * 操作员
     */

    private String VCZY;

    /**
     * 操作日期
     */

    private Long DCZRQ;

    /**
     * 提交日期
     */

    private Long DTJRQ;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 公司
     */

    private String VGS;

    /**
     * 铁路列号
     */

    private String VTLLH;

    /**
     * 标识：1铁路，2水路
     */

    private String CBS;

    /**
     * 分驳标识  0否，1是
     */

    private String CFBBS;

    /**
     * 短驳运输商
     */

    private String CDBYSS;

    /**
     * LJS代码
     */

    private String CLJSDM;

    /**
     * 船舶号
     */

    private String CCBH;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
