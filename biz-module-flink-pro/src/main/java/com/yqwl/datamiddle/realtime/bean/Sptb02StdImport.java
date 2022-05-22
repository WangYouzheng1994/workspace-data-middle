package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 运单STD导入
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb02StdImport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer ID;

    /**
     * 结算单编号（作废了）
     */

    private String CJSDBH;

    /**
     * 配载单编号
     */

    private String CPZDBH;

    /**
     * 清单号
     */

    private String VWXDWDM;

    /**
     * 任务单号
     */

    private String CJHDH;

    /**
     * 运输方式  公路:G 铁路：T或L1  水路 :S  集港 ：J  水运短拨：SD 铁路短拨：TD （作废了）
     */

    private String VYSFS;

    /**
     * SD:收单  SCSJ:试乘试驾
     */

    private String TYPE;

    /**
     * 创建人
     */

    private String CREATE_BY;

    /**
     * 创建人姓名
     */

    private String CREATE_BY_NAME;

    /**
     * 创建时间
     */

    private Long CREATE_DATE;

    /**
     * 收单时间
     */

    private Long SD_DATE;

    /**
     * 监控理论到货时间
     */

    private Long VDHZSX_GPS;

    /**
     * 监控到货时间
     */

    private Long DGPSDHSJ;

    /**
     * 审核人
     */

    private String APPROVER_USER;

    /**
     * 大众审核时间 20210430 DTF
     */

    private Long DSHSJ_DZ;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
