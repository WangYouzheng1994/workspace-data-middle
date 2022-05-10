package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 车型 大类
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac14 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

        
    private String ccxdl;

        
    private String vcxdlsm;

        
    private String cpp;

        
    private String cyscx;

        
    private String ctybs;

        
    private Long dtyrq;

        
    private String cvin;

        
    private String cbs;

        
    private String ccxlb;

      /**
     * 运费对应价格分类。根据SPTB77.CPP计算运费
     */
          
    private String vcpfl;

        
    private String vwlfl;

        
    private Integer id;

        
    private String cjhty;

      /**
     * 平台代码
     */
          
    private String cptdm;

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

      /**
     * 终审审批标识：0  未审批  1：已审批
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

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
