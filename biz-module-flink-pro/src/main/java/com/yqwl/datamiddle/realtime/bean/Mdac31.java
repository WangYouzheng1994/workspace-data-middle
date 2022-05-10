package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 承运商代码。承运商代码，用于运输
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac31 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 承运商代码
     */
          
    private String ccysdm;

      /**
     * 承运商名称
     */
          
    private String vcysmc;

      /**
     * 联系人
     */
          
    private String clxr;

      /**
     * 省区代码
     */
          
    private String csqdm;

      /**
     * 市县代码
     */
          
    private String csxdm;

      /**
     * 电话
     */
          
    private String vdh;

      /**
     * 移动电话
     */
          
    private String vyddh;

      /**
     * 传真
     */
          
    private String vcz;

      /**
     * e_mail
     */
          
    private String veMail;

      /**
     * 0、在用，1、停用
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

      /**
     * 机构代码
     */
          
    private String cjgdm;

      /**
     * 0/承输商，1/仓储商
     */
          
    private String cbs;

      /**
     * 负责人
     */
          
    private String vfzr;

      /**
     * 开户行
     */
          
    private String vkhh;

      /**
     * 银行帐号
     */
          
    private String vyhzh;

      /**
     * 纳税登记号
     */
          
    private String vnsdjh;

      /**
     * 法人代表
     */
          
    private String cfrdb;

      /**
     * 地址
     */
          
    private String vdz;

      /**
     * 简称
     */
          
    private String vcysjc;

      /**
     * 注册地址
     */
          
    private String vzcdz;

      /**
     * 合同起始日期
     */
          
    private Long dqsrq;

      /**
     * 合同截止日期
     */
          
    private Long djzrq;

        
    private String vbz;

      /**
     * 承运标识-承运商或加盟商
     */
          
    private String cgrdm;

        
    private String cssxt;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String cgs;

      /**
     * 序号
     */
          
    private String cxh;

      /**
     * 时间戳。BI提数据
     */
          
    private Long dstamp;

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
