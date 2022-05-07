package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Sptc34Wide extends Sptc34{

    private  int IDNUM;
    private String vwlckdm;
    private String vwlckmc;
    private String czt;
    private Integer nkr;
    private String vsqdm;
    private String vsxdm;
    private String vlxr;
    private String vdh;
    private String vcz;
    private String vemail;
    private String vyddh;
    private String vyb;
    private String vdz;
    private String ctybs;
    private Long dtyrq;
    private String vbz;
    private String ccksx;
    private String cglkqkw;
    private String cccsdm;
    private String vcftj;
    private String cwx;
    private String cgs;
    private String cscfbjh;
    private String vdzckdm;
    private String cyssdm;
    private String cyscdm;
    private String vwlckjc;
    private String cwlbm;
    private String cwlmc;
    private Long dtbrq;
    private Integer batchno;
    private String cwlbm3;
    private String ccklx;
    private Long dstamp;
    private String approvalFlag;
    private String approvalUser;
    private Long approvalDate;
    private String finalApprovalFlag;
    private String finalApprovalUser;
    private Long finalApprovalDate;
    private String czjgsdm;
    private String vztmcZt;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String vsqsxdm ;

    public Sptc34Wide() {
    }

    public Sptc34Wide(int IDNUM, String vwlckdm, String vwlckmc,
                      String czt, Integer nkr, String vsqdm, String vsxdm,
                      String vlxr, String vdh, String vcz, String vemail, String vyddh,
                      String vyb, String vdz, String ctybs, Long dtyrq, String vbz, String ccksx,
                      String cglkqkw, String cccsdm, String vcftj, String cwx, String cgs, String cscfbjh,
                      String vdzckdm, String cyssdm, String cyscdm, String vwlckjc, String cwlbm, String cwlmc,
                      Long dtbrq, Integer batchno, String cwlbm3, String ccklx, Long dstamp, String approvalFlag,
                      String approvalUser, Long approvalDate, String finalApprovalFlag, String finalApprovalUser,
                      Long finalApprovalDate, String czjgsdm, String vztmcZt, Timestamp createTime, Timestamp updateTime, String vsqsxdm) {
        this.IDNUM = IDNUM;
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
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.vsqsxdm = vsqdm + vsxdm;
    }
}
