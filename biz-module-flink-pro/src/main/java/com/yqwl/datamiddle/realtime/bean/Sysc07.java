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
 * 省区代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sysc07")
public class Sysc07 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 省区代码
     */
      @TableField("CSQDM")
    private String csqdm;

      /**
     * 省区名称
     */
      @TableField("VSQMC")
    private String vsqmc;

      /**
     * 所属地区代码
     */
      @TableField("CDQDM")
    private String cdqdm;

      /**
     * 备注
     */
      @TableField("VBZ")
    private String vbz;

    @TableField("ID")
    private Integer id;

      /**
     * 省区简称
     */
      @TableField("CJC")
    private String cjc;

      /**
     * 物流标准编码。选择，来源于M平台
     */
      @TableField("CWLBM")
    private String cwlbm;

      /**
     * 物流标准名称。来源于M平台
     */
      @TableField("CWLMC")
    private String cwlmc;

      /**
     * 同步日期
     */
      @TableField("DTBRQ")
    private Long dtbrq;

      /**
     * 版本号
     */
      @TableField("BATCHNO")
    private Integer batchno;

      /**
     * 可以显示的简称
     */
      @TableField("CJC2")
    private String cjc2;

      /**
     * 省会代码。如长春  为04
     */
      @TableField("CSHDM")
    private String cshdm;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 审批标识：0  未审批  1：已审批
     */
      @TableField("APPROVAL_FLAG")
    private String approvalFlag;

      /**
     * 审批人
     */
      @TableField("APPROVAL_USER")
    private String approvalUser;

      /**
     * 审批日期
     */
      @TableField("APPROVAL_DATE")
    private Long approvalDate;

      /**
     * 终审审批标识：0  未审批  1：已审批
     */
      @TableField("FINAL_APPROVAL_FLAG")
    private String finalApprovalFlag;

      /**
     * 终审审批人
     */
      @TableField("FINAL_APPROVAL_USER")
    private String finalApprovalUser;

      /**
     * 终审审批日期
     */
      @TableField("FINAL_APPROVAL_DATE")
    private Long finalApprovalDate;

      /**
     * 创建时间
     */
      @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
      @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
