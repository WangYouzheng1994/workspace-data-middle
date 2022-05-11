package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 物理仓库信息表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Sptc34Wide implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 物理仓库代码（站台代码)
     */
          
    private String vwlckdm;

      /**
     * 增加长度: 由20增至50
     */
          
    private String vwlckmc;

      /**
     * 状态(0:可用,1:盘点) 默认可用,暂时不用!
     */
          
    private String czt;

      /**
     * 库容
     */
          
    private Integer nkr;

      /**
     * 省区代码
     */
          
    private String vsqdm;

      /**
     * 市县代码
     */
          
    private String vsxdm;

      /**
     * 联系人
     */
          
    private String vlxr;

      /**
     * 电话
     */
          
    private String vdh;

      /**
     * 传真
     */
          
    private String vcz;

      /**
     * EMAIL
     */
          
    private String vemail;

      /**
     * 移动电话
     */
          
    private String vyddh;

      /**
     * 邮编
     */
          
    private String vyb;

      /**
     * 地址20210108 50-100
     */
          
    private String vdz;

      /**
     * 停用标识
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

        
    private String vbz;

      /**
     * 字典:WLCKSX,0公路 1铁路 2水路
     */
          
    private String ccksx;

      /**
     * 管理库区库位. 0 管理  1 不管理
     */
          
    private String cglkqkw;

        
    private String cccsdm;

      /**
     * 存放条件
     */
          
    private String vcftj;

      /**
     * 是否外协  0/本公司  1/外协
     */
          
    private String cwx;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String cgs;

      /**
     * 针对铁路站台,是否生成分驳计划 0 否  1 是
     */
          
    private String cscfbjh;

      /**
     * 大众物理仓库代码
     */
          
    private String vdzckdm;

      /**
     * 针对铁路站台,设置的默认短驳运输商
     */
          
    private String cyssdm;

      /**
     * 针对铁路站台,设置的默认运输车
     */
          
    private String cyscdm;

      /**
     * 仓库简称
     */
          
    private String vwlckjc;

      /**
     * 物流标准编码  选择  来源于M平台
     */
          
    private String cwlbm;

      /**
     * 物流标准名称 来源于M平台
     */
          
    private String cwlmc;

      /**
     * 同步日期
     */
          
    private Long dtbrq;

      /**
     * 版本号
     */
          
    private Integer batchno;

        
    private String cwlbm3;

      /**
     * 字典:WLCKLX (1 基地库  2 中转库)
     */
          
    private String ccklx;

      /**
     * 时间戳  BI提数据
     */
          
    private Long dstamp;

      /**
     * 审批标识  0 未审批  1 已审批
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
     * 终审审批标识  0 未审批  1 已审批
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
     * 主机公司代码  字典WTDW
     */
          
    private String czjgsdm;

      /**
     * 20220309 中铁新接口 站点名称
     */
          
    private String vztmcZt;

    //省市区代码
    private  String vsqsxdm;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


    public Sptc34Wide() {
    }

    public Sptc34Wide(Long idnum, String vwlckdm, String vwlckmc, String czt, Integer nkr, String vsqdm, String vsxdm, String vlxr, String vdh,
                      String vcz, String vemail, String vyddh, String vyb, String vdz, String ctybs, Long dtyrq, String vbz, String ccksx,
                      String cglkqkw, String cccsdm, String vcftj, String cwx, String cgs, String cscfbjh, String vdzckdm, String cyssdm, String cyscdm,
                      String vwlckjc, String cwlbm, String cwlmc, Long dtbrq, Integer batchno, String cwlbm3, String ccklx, Long dstamp, String approvalFlag,
                      String approvalUser, Long approvalDate, String finalApprovalFlag, String finalApprovalUser, Long finalApprovalDate, String czjgsdm, String vztmcZt,
                      String vsqsxdm, Long warehouseCreatetime, Long warehouseUpdatetime) {
        this.idnum = idnum;
        this.vwlckdm = vwlckdm;
        this.vwlckmc = vwlckmc;
        this.czt = czt;
        this.nkr = nkr;
        this.vsqdm = vsqdm;
        this.vsxdm = vsxdm;
        this.vlxr = vlxr;
        this.vdh = vdh;
        this.vcz = vcz;
        this.vemail = vemail;
        this.vyddh = vyddh;
        this.vyb = vyb;
        this.vdz = vdz;
        this.ctybs = ctybs;
        this.dtyrq = dtyrq;
        this.vbz = vbz;
        this.ccksx = ccksx;
        this.cglkqkw = cglkqkw;
        this.cccsdm = cccsdm;
        this.vcftj = vcftj;
        this.cwx = cwx;
        this.cgs = cgs;
        this.cscfbjh = cscfbjh;
        this.vdzckdm = vdzckdm;
        this.cyssdm = cyssdm;
        this.cyscdm = cyscdm;
        this.vwlckjc = vwlckjc;
        this.cwlbm = cwlbm;
        this.cwlmc = cwlmc;
        this.dtbrq = dtbrq;
        this.batchno = batchno;
        this.cwlbm3 = cwlbm3;
        this.ccklx = ccklx;
        this.dstamp = dstamp;
        this.approvalFlag = approvalFlag;
        this.approvalUser = approvalUser;
        this.approvalDate = approvalDate;
        this.finalApprovalFlag = finalApprovalFlag;
        this.finalApprovalUser = finalApprovalUser;
        this.finalApprovalDate = finalApprovalDate;
        this.czjgsdm = czjgsdm;
        this.vztmcZt = vztmcZt;
        this.vsqsxdm = vsqsxdm;
        this.warehouseCreatetime = warehouseCreatetime;
        this.warehouseUpdatetime = warehouseUpdatetime;
    }
}
