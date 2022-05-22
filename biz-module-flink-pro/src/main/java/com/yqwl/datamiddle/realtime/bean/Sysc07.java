package com.yqwl.datamiddle.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 省区代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sysc07 implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 省区代码
     */

    private String CSQDM;

    /**
     * 省区名称
     */

    private String VSQMC;

    /**
     * 所属地区代码
     */

    private String CDQDM;

    /**
     * 备注
     */

    private String VBZ;


    private Integer ID;

    /**
     * 省区简称
     */

    private String CJC;

    /**
     * 物流标准编码。选择，来源于M平台
     */

    private String CWLBM;

    /**
     * 物流标准名称。来源于M平台
     */

    private String CWLMC;

    /**
     * 同步日期
     */

    private Long DTBRQ;

    /**
     * 版本号
     */

    private Integer BATCHNO;

    /**
     * 可以显示的简称
     */

    private String CJC2;

    /**
     * 省会代码。如长春  为04
     */

    private String CSHDM;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 审批标识：0  未审批  1：已审批
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
     * 终审审批标识：0  未审批  1：已审批
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


    @JSONField(serialize = false)
    private Timestamp ts;

}
