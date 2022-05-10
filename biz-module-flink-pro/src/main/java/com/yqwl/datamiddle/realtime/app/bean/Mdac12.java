package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac12 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 产品代码
     */
          
    private String ccpdm;

      /**
     * 产品名称
     */
          
    private String vcpmc;

        
    private String vcpjc;

        
    private Integer naqkc;

        
    private Integer npl;

        
    private Integer nplzl;

        
    private String ctybs;

        
    private Long dtyrq;

        
    private String cpp;

        
    private String ccxdl;

      /**
     * 20190404 30-40 红旗车型33  40-100 20210528
     */
          
    private String ccxdm;

        
    private String cjhty;

        
    private Long djhtyrq;

        
    private String cddty;

        
    private Long dddtyrq;

        
    private String clsty;

        
    private Long dlstyrq;

        
    private BigDecimal nccfdj;

        
    private String cphbs;

        
    private String cjhdbbs;

        
    private String vbz;

        
    private String cjkbs;

        
    private Integer id;

      /**
     * 描述
     */
          
    private String vms;

        
    private Integer nfwfdj;

      /**
     * 库龄设置，单位为天
     */
          
    private Integer nklsz;

        
    private String cxsty;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String cgs;

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

        
    private String finalApprovalFlag;

        
    private String finalApprovalUser;

        
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
