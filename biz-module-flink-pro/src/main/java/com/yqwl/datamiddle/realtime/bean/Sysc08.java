package com.yqwl.datamiddle.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>
 * 市县代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sysc08 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 市县代码
     */

    private String CSXDM;

    /**
     * 省区代码
     */

    private String CSQDM;

    /**
     * 市县名称
     */

    private String VSXMC;

    /**
     * 备注
     */

    private String VBZ;


    private Integer ID;

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
     * 地市代码。SYSC09.CDSDM
     */

    private String CDSDM;

    /**
     * 经度
     */

    private BigDecimal NJD;

    /**
     * 纬度
     */

    private BigDecimal NWD;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 20171206 红旗市县代码乱码 处理
     */

    private String CSXDM_HQ;

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
     * 半径
     */

    private BigDecimal NRADIUS;

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
    private Long ts;
}
