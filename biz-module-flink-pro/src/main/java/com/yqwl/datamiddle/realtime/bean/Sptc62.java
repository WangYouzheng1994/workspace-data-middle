package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
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

public class Sptc62 implements Serializable {

    private static final long serialVersionUID = 1L;


      private Long idnum;

      /**
     * 编码
     */
      
    private String cid;

      /**
     * 名称
     */
      
    private String cname;

      /**
     * 简称.用于动态统计
     */
      
    private String cjc;

      /**
     * 备注
     */
      
    private String cbz;

      /**
     * 物流标准编码.选择,来源于M平台
     */
      
    private String cwlbm;

      /**
     * 物流标准名称.来源于M平台
     */
      
    private String cwlmc;

      /**
     * 停用标识
     */
      
    private String ctybs;

      /**
     * 停用日期
     */
      
    private Long dtyrq;

      /**
     * 停用原因
     */
      
    private String ctyyy;

      /**
     * 操作日期
     */
      
    private Long dczrq;

      /**
     * 操作人代码
     */
      
    private String cczydm;

      /**
     * 同步日期
     */
      
    private Long dtbrq;

      /**
     * 版本号
     */
      
    private Integer batchno;

      /**
     * 审批标识:  0  未审批  1:已审批
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
     * 终审审批标识: 0 未审批 1:已审批
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


}
