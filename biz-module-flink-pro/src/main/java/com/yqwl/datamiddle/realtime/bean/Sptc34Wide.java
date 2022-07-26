package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
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
@TableName("dim_vlms_sptc34")
@Data
//@CamelUnderline
public class Sptc34Wide  {


      private Long IDNUM;

      /**
     * 物理仓库代码（站台代码)
     */
          
    private String VWLCKDM;

      /**
     * 增加长度: 由20增至50
     */
          
    private String VWLCKMC;

      /**
     * 状态(0:可用,1:盘点) 默认可用,暂时不用!
     */
          
    private String CZT;

      /**
     * 库容
     */
          
    private Integer NKR;

      /**
     * 省区代码
     */
          
    private String VSQDM;

      /**
     * 市县代码
     */
          
    private String VSXDM;

      /**
     * 联系人
     */
          
    private String VLXR;

      /**
     * 电话
     */
          
    private String VDH;

      /**
     * 传真
     */
          
    private String VCZ;

      /**
     * EMAIL
     */
          
    private String VEMAIL;

      /**
     * 移动电话
     */
          
    private String VYDDH;

      /**
     * 邮编
     */
          
    private String VYB;

      /**
     * 地址20210108 50-100
     */
          
    private String VDZ;

      /**
     * 停用标识
     */
          
    private String CTYBS;

      /**
     * 停用日期
     */
          
    private Long DTYRQ;

        
    private String VBZ;

      /**
     * 字典:WLCKSX,0公路 1铁路 2水路
     */
          
    private String CCKSX;

      /**
     * 管理库区库位. 0 管理  1 不管理
     */
          
    private String CGLKQKW;

        
    private String CCCSDM;

      /**
     * 存放条件
     */
          
    private String VCFTJ;

      /**
     * 是否外协  0/本公司  1/外协
     */
          
    private String CWX;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String CGS;

      /**
     * 针对铁路站台,是否生成分驳计划 0 否  1 是
     */
          
    private String CSCFBJH;

      /**
     * 大众物理仓库代码
     */
          
    private String VDZCKDM;

      /**
     * 针对铁路站台,设置的默认短驳运输商
     */
          
    private String CYSSDM;

      /**
     * 针对铁路站台,设置的默认运输车
     */
          
    private String CYSCDM;

      /**
     * 仓库简称
     */
          
    private String VWLCKJC;

      /**
     * 物流标准编码  选择  来源于M平台
     */
          
    private String CWLBM;

      /**
     * 物流标准名称 来源于M平台
     */
          
    private String CWLMC;

      /**
     * 同步日期
     */
          
    private Long DTBRQ;

      /**
     * 版本号
     */
          
    private Integer BATCHNO;

        
    private String CWLBM3;

      /**
     * 字典:WLCKLX (1 基地库  2 中转库)
     */
          
    private String CCKLX;

      /**
     * 时间戳  BI提数据
     */
          
    private Long DSTAMP;

      /**
     * 审批标识  0 未审批  1 已审批
     */
          
    private String APPROVAL_FLAG;

      /**
     * 审批人
     */
          
    private String APPROVAL_USER;

      /**
     * 审批日期
     */
          
    private Long APPROVAL_DATE;

      /**
     * 终审审批标识  0 未审批  1 已审批
     */
          
    private String FINAL_APPROVAL_FLAG;

      /**
     * 终审审批人
     */
          
    private String FINAL_APPROVAL_USER;

      /**
     * 终审审批日期
     */
          
    private Long FINAL_APPROVAL_DATE;

      /**
     * 主机公司代码  字典WTDW
     */
          
    private String CZJGSDM;

      /**
     * 20220309 中铁新接口 站点名称
     */
          
    private String VZTMC_ZT;

    //省市区代码
    private  String VSQSXDM;

      /**
     * 创建时间
     */
          
    private Long WAREHOUSE_CREATETIME;

      /**
     * 更新时间
     */
          
    private Long WAREHOUSE_UPDATETIME;


    public Sptc34Wide() {
    }

    public Sptc34Wide(Long IDNUM, String VWLCKDM, String VWLCKMC, String CZT, Integer NKR, String VSQDM, String VSXDM, String VLXR, String VDH, String VCZ, String VEMAIL, String VYDDH, String VYB, String VDZ, String CTYBS, Long DTYRQ, String VBZ, String CCKSX, String CGLKQKW, String CCCSDM, String VCFTJ, String CWX, String CGS, String CSCFBJH, String VDZCKDM, String CYSSDM, String CYSCDM, String VWLCKJC, String CWLBM, String CWLMC, Long DTBRQ, Integer BATCHNO, String CWLBM3, String CCKLX, Long DSTAMP, String APPROVAL_FLAG, String APPROVAL_USER, Long APPROVAL_DATE, String FINAL_APPROVAL_FLAG, String FINAL_APPROVAL_USER, Long FINAL_APPROVAL_DATE, String CZJGSDM, String VZTMC_ZT, String VSQSXDM, Long WAREHOUSE_CREATETIME, Long WAREHOUSE_UPDATETIME) {
        this.IDNUM = IDNUM;
        this.VWLCKDM = VWLCKDM;
        this.VWLCKMC = VWLCKMC;
        this.CZT = CZT;
        this.NKR = NKR;
        this.VSQDM = VSQDM;
        this.VSXDM = VSXDM;
        this.VLXR = VLXR;
        this.VDH = VDH;
        this.VCZ = VCZ;
        this.VEMAIL = VEMAIL;
        this.VYDDH = VYDDH;
        this.VYB = VYB;
        this.VDZ = VDZ;
        this.CTYBS = CTYBS;
        this.DTYRQ = DTYRQ;
        this.VBZ = VBZ;
        this.CCKSX = CCKSX;
        this.CGLKQKW = CGLKQKW;
        this.CCCSDM = CCCSDM;
        this.VCFTJ = VCFTJ;
        this.CWX = CWX;
        this.CGS = CGS;
        this.CSCFBJH = CSCFBJH;
        this.VDZCKDM = VDZCKDM;
        this.CYSSDM = CYSSDM;
        this.CYSCDM = CYSCDM;
        this.VWLCKJC = VWLCKJC;
        this.CWLBM = CWLBM;
        this.CWLMC = CWLMC;
        this.DTBRQ = DTBRQ;
        this.BATCHNO = BATCHNO;
        this.CWLBM3 = CWLBM3;
        this.CCKLX = CCKLX;
        this.DSTAMP = DSTAMP;
        this.APPROVAL_FLAG = APPROVAL_FLAG;
        this.APPROVAL_USER = APPROVAL_USER;
        this.APPROVAL_DATE = APPROVAL_DATE;
        this.FINAL_APPROVAL_FLAG = FINAL_APPROVAL_FLAG;
        this.FINAL_APPROVAL_USER = FINAL_APPROVAL_USER;
        this.FINAL_APPROVAL_DATE = FINAL_APPROVAL_DATE;
        this.CZJGSDM = CZJGSDM;
        this.VZTMC_ZT = VZTMC_ZT;
        this.VSQSXDM = VSQSXDM;
        this.WAREHOUSE_CREATETIME = WAREHOUSE_CREATETIME;
        this.WAREHOUSE_UPDATETIME = WAREHOUSE_UPDATETIME;
    }
}
