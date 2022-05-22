package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 基地表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptc62 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */

    private String CID;

    /**
     * 名称
     */

    private String CNAME;

    /**
     * 简称.用于动态统计
     */

    private String CJC;

    /**
     * 备注
     */

    private String CBZ;

    /**
     * 物流标准编码.选择,来源于M平台
     */

    private String CWLBM;

    /**
     * 物流标准名称.来源于M平台
     */

    private String CWLMC;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 停用原因
     */

    private String CTYYY;

    /**
     * 操作日期
     */

    private Long DCZRQ;

    /**
     * 操作人代码
     */

    private String CCZYDM;

    /**
     * 同步日期
     */

    private Long DTBRQ;

    /**
     * 版本号
     */

    private Integer BATCHNO;

    /**
     * 审批标识:  0  未审批  1:已审批
     */

    private String APPROVAL_FLAG;

    /**
     * 审批人
     */

    private String APPROVAL_USER;

    /**
     * 审批日期
     */

    private Long APPROVAL_DATE;

    /**
     * 终审审批标识: 0 未审批 1:已审批
     */

    private String FINAL_APPROVAL_FLAG;

    /**
     * 终审审批人
     */

    private String FINAL_APPROVAL_USER;

    /**
     * 终审审批日期
     */

    private Long FINAL_APPROVAL_DATE;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
