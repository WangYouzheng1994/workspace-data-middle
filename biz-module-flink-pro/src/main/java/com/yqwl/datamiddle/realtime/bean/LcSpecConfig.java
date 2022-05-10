package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 大屏指标参数配置表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class LcSpecConfig implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 版本号
     */
          
    private Integer versionCode;

      /**
     * 主机公司代码 1：大众、2：红旗 、3：奔腾、4：马自达
     */
          
    private String hostComCode;

      /**
     * 主机公司名称
     */
          
    private String hostComName;

      /**
     * 指标代码 0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
     */
          
    private String specCode;

      /**
     * 指标名称
     */
          
    private String specName;

      /**
     * 基地代码
     */
          
    private String baseCode;

      /**
     * 基地名称
     */
          
    private String baseName;

      /**
     * 分拨中心下基地代码
     */
          
    private String distCode;

      /**
     * 分拨中心下基地名称
     */
          
    private String distName;

      /**
     * 运输方式代码
     */
          
    private String transModeCode;

      /**
     * 运输方式名称
     */
          
    private String transModeName;

      /**
     * 前置计算时间节点代码
     */
          
    private String startCalNodeCode;

      /**
     * 前置计算时间节点名称
     */
          
    private String startCalNodeName;

      /**
     * 后置计算时间节点代码
     */
          
    private String endCalNodeCode;

      /**
     * 后置计算时间节点名称
     */
          
    private String endCalNodeName;

      /**
     * 标准时长（小时）
     */
          
    private String standardHours;

      /**
     * 状态 0：失效 1：有效 2：待确认
     */
          
    private Integer status;

      /**
     * 创建人
     */
          
    private String creator;

      /**
     * 创建时间
     */
          
    private Long createTime;

      /**
     * 是否计算到23:59:59 默认0否 1：是
     */
          
    private Long isLastTime;

      /**
     * 初审人
     */
          
    private String csrxm;

      /**
     * 初审时间
     */
          
    private Long csrq;

      /**
     * 终审人
     */
          
    private String zsrxm;

      /**
     * 终审时间
     */
          
    private Long zsrq;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
