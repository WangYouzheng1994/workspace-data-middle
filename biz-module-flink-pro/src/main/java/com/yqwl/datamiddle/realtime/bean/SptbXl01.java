package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 线路主表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SptbXl01 {

    /**
     * 线路类型： M干线，F分驳线路
     */

    private String CXLLX;

    /**
     * 线路代码：默认为线路起点-线路终点代码
     */

    private String CXLDM;

    /**
     * 线路名称：默认为线路起点-线路终点城市
     */

    private String VXLMC;

    /**
     * 线路状态：0，初始；1提交。提交时，校验占比100；校验各线路重复；
     */

    private String CZT;

    /**
     * 操作员代码
     */

    private String CCZYDM;

    /**
     * 操作日期
     */

    private Long DCZSJ;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 主机公司
     */

    private String CZJGSDM;

    /**
     * 基地
     */

    private String CQWH;

    /**
     * 区域变更标识：线路上的区域是否随中标方而变化 0否，1是
     */

    private String CQYBG;

    /**
     * 选择运输商方式：线路是否按区域选择中标方       0否，1是
     */

    private String CXZYSS;

    /**
     * 审核标识1
     */

    private String CBS1;

    /**
     * 审核标识2
     */

    private String CBS2;

    /**
     * 审核标识3
     */

    private String CBS3;

    /**
     * 审核标识4
     */

    private String CBS4;

    /**
     * 操作员代码1
     */

    private String CCZYDM1;

    /**
     * 操作员代码2
     */

    private String CCZYDM2;

    /**
     * 操作员代码3
     */

    private String CCZYDM3;

    /**
     * 操作员代码4
     */

    private String CCZYDM4;

    /**
     * 审核日期1
     */

    private Long DSHRQ1;

    /**
     * 审核日期2
     */

    private Long DSHRQ2;

    /**
     * 审核日期3
     */

    private Long DSHRQ3;

    /**
     * 审核日期4
     */

    private Long DSHRQ4;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
