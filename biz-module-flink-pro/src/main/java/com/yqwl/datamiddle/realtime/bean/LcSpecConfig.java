package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 大屏指标参数配置表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LcSpecConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本号
     */

    private Integer VERSION_CODE;

    /**
     * 主机公司代码 1：大众、2：红旗 、3：奔腾、4：马自达
     */

    private String HOST_COM_CODE;

    /**
     * 主机公司名称
     */

    private String HOST_COM_NAME;

    /**
     * 指标代码 0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
     */

    private String SPEC_CODE;

    /**
     * 指标名称
     */

    private String SPEC_NAME;

    /**
     * 基地代码
     */

    private String BASE_CODE;

    /**
     * 基地名称
     */

    private String BASE_NAME;

    /**
     * 分拨中心下基地代码
     */

    private String DIST_CODE;

    /**
     * 分拨中心下基地名称
     */

    private String DIST_NAME;

    /**
     * 运输方式代码
     */

    private String TRANS_MODE_CODE;

    /**
     * 运输方式名称
     */

    private String TRANS_MODE_NAME;

    /**
     * 前置计算时间节点代码
     */

    private String START_CAL_NODE_CODE;

    /**
     * 前置计算时间节点名称
     */

    private String START_CAL_NODE_NAME;

    /**
     * 后置计算时间节点代码
     */

    private String END_CAL_NODE_CODE;

    /**
     * 后置计算时间节点名称
     */

    private String END_CAL_NODE_NAME;

    /**
     * 标准时长（小时）
     */

    private String STANDARD_HOURS;

    /**
     * 状态 0：失效 1：有效 2：待确认
     */

    private Integer STATUS;

    /**
     * 创建人
     */

    private String CREATOR;

    /**
     * 创建时间
     */

    private Long CREATE_TIME;

    /**
     * 是否计算到23:59:59 默认0否 1：是
     */

    private Long IS_LAST_TIME;

    /**
     * 初审人
     */

    private String CSRXM;

    /**
     * 初审时间
     */

    private Long CSRQ;

    /**
     * 终审人
     */

    private String ZSRXM;

    /**
     * 终审时间
     */

    private Long ZSRQ;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
