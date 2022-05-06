package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
@Data
public class Sptc61 {
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
    private Integer nsort;
    private String cjc2;
    private String ccydebs;
    private String approvalFlag;
    private String approvalUser;
    private Long approvalDate;
    private String finalApprovalFlag;
    private String finalApprovalUser;
    private Long finalApprovalDate;
    private Timestamp createTime;
    private Timestamp updateTime;


}
