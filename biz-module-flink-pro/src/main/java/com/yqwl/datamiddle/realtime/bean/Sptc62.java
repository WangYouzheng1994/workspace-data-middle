package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;

@Data
public class Sptc62 {
    private  int IDNUM;
    private String cid;
    private String cname;
    private String cjc;
    private String cbz;
    private String cwlbm;
    private String cwlmc;
    private String ctybs;
    private Long dtyrq;
    private String ctyyy;
    private Long dczrq;
    private String cczydm;
    private Long dtbrq;
    private Integer batchno;
    private String approvalFlag;
    private String approvalUser;
    private Long approvalDate;
    private String finalApprovalFlag;
    private String finalApprovalUser;
    private Long finalApprovalDate;
    private Timestamp createTime;
    private Timestamp updateTime;

}
