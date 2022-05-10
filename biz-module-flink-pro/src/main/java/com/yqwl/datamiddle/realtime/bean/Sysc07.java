package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 省区代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Sysc07 implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long idnum;

    /**
     * 省区代码
     */

    private String csqdm;

    /**
     * 省区名称
     */

    private String vsqmc;

    /**
     * 所属地区代码
     */

    private String cdqdm;

    /**
     * 备注
     */

    private String vbz;


    private Integer id;

    /**
     * 省区简称
     */

    private String cjc;

    /**
     * 物流标准编码。选择，来源于M平台
     */

    private String cwlbm;

    /**
     * 物流标准名称。来源于M平台
     */

    private String cwlmc;

    /**
     * 同步日期
     */

    private Long dtbrq;

    /**
     * 版本号
     */

    private Integer batchno;

    /**
     * 可以显示的简称
     */

    private String cjc2;

    /**
     * 省会代码。如长春  为04
     */

    private String cshdm;

    /**
     * 时间戳。BI提数据
     */

    private Long dstamp;

    /**
     * 审批标识：0  未审批  1：已审批
     */

    private String approvalFlag;

    /**
     * 审批人
     */

    private String approvalUser;

    /**
     * 审批日期
     */

    private Long approvalDate;

    /**
     * 终审审批标识：0  未审批  1：已审批
     */

    private String finalApprovalFlag;

    /**
     * 终审审批人
     */

    private String finalApprovalUser;

    /**
     * 终审审批日期
     */

    private Long finalApprovalDate;

    /**
     * 创建时间
     */

    private Long warehouseCreatetime;

    /**
     * 更新时间
     */

    private Long warehouseUpdatetime;

    //新加kafka的ts时间戳
    private Timestamp ts;

}
