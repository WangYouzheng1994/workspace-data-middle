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
 * 基地表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptc62")
public class Sptc62 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 编码
     */
      @TableField("CID")
    private String cid;

      /**
     * 名称
     */
      @TableField("CNAME")
    private String cname;

      /**
     * 简称.用于动态统计
     */
      @TableField("CJC")
    private String cjc;

      /**
     * 备注
     */
      @TableField("CBZ")
    private String cbz;

      /**
     * 物流标准编码.选择,来源于M平台
     */
      @TableField("CWLBM")
    private String cwlbm;

      /**
     * 物流标准名称.来源于M平台
     */
      @TableField("CWLMC")
    private String cwlmc;

      /**
     * 停用标识
     */
      @TableField("CTYBS")
    private String ctybs;

      /**
     * 停用日期
     */
      @TableField("DTYRQ")
    private Long dtyrq;

      /**
     * 停用原因
     */
      @TableField("CTYYY")
    private String ctyyy;

      /**
     * 操作日期
     */
      @TableField("DCZRQ")
    private Long dczrq;

      /**
     * 操作人代码
     */
      @TableField("CCZYDM")
    private String cczydm;

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
     * 审批标识:  0  未审批  1:已审批
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
     * 终审审批标识: 0 未审批 1:已审批
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
