package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac10 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

        
    private String cpp;

        
    private String vppsm;

        
    private String ctybs;

        
    private Long dtyrq;

      /**
     * 对应委托单位.与字典表SYSC09D对应CZDDM = 'WTDW'
     */
          
    private Integer ngsdj;

      /**
     * 排序
     */
          
    private String cpx;

      /**
     * 计划标识.0不检查，1/检查
     */
          
    private String cfhbs;

      /**
     * ID
     */
          
    private Integer id;

        
    private String ccqck;

        
    private String cwlwz;

      /**
     * 主机厂公司
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

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
