package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_lc_spec_config")
public class LcSpecConfig implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 版本号
     */
      @TableField("VERSION_CODE")
    private Integer versionCode;

      /**
     * 主机公司代码 1：大众、2：红旗 、3：奔腾、4：马自达
     */
      @TableField("HOST_COM_CODE")
    private String hostComCode;

      /**
     * 主机公司名称
     */
      @TableField("HOST_COM_NAME")
    private String hostComName;

      /**
     * 指标代码 0：倒运及时率 1：计划指派及时率 2：出库及时率 3：运输指派及时率 4：运输商起运及时率 5：运输商监控到货及时率 6：运输商核实到货及时率
     */
      @TableField("SPEC_CODE")
    private String specCode;

      /**
     * 指标名称
     */
      @TableField("SPEC_NAME")
    private String specName;

      /**
     * 基地代码
     */
      @TableField("BASE_CODE")
    private String baseCode;

      /**
     * 基地名称
     */
      @TableField("BASE_NAME")
    private String baseName;

      /**
     * 分拨中心下基地代码
     */
      @TableField("DIST_CODE")
    private String distCode;

      /**
     * 分拨中心下基地名称
     */
      @TableField("DIST_NAME")
    private String distName;

      /**
     * 运输方式代码
     */
      @TableField("TRANS_MODE_CODE")
    private String transModeCode;

      /**
     * 运输方式名称
     */
      @TableField("TRANS_MODE_NAME")
    private String transModeName;

      /**
     * 前置计算时间节点代码
     */
      @TableField("START_CAL_NODE_CODE")
    private String startCalNodeCode;

      /**
     * 前置计算时间节点名称
     */
      @TableField("START_CAL_NODE_NAME")
    private String startCalNodeName;

      /**
     * 后置计算时间节点代码
     */
      @TableField("END_CAL_NODE_CODE")
    private String endCalNodeCode;

      /**
     * 后置计算时间节点名称
     */
      @TableField("END_CAL_NODE_NAME")
    private String endCalNodeName;

      /**
     * 标准时长（小时）
     */
      @TableField("STANDARD_HOURS")
    private String standardHours;

      /**
     * 状态 0：失效 1：有效 2：待确认
     */
      @TableField("STATUS")
    private Integer status;

      /**
     * 创建人
     */
      @TableField("CREATOR")
    private String creator;

      /**
     * 创建时间
     */
      @TableField("CREATE_TIME")
    private Long createTime;

      /**
     * 是否计算到23:59:59 默认0否 1：是
     */
      @TableField("IS_LAST_TIME")
    private Long isLastTime;

      /**
     * 初审人
     */
      @TableField("CSRXM")
    private String csrxm;

      /**
     * 初审时间
     */
      @TableField("CSRQ")
    private Long csrq;

      /**
     * 终审人
     */
      @TableField("ZSRXM")
    private String zsrxm;

      /**
     * 终审时间
     */
      @TableField("ZSRQ")
    private Long zsrq;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
