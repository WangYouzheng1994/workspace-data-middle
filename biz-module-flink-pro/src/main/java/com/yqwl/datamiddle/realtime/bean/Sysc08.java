package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;


import java.sql.Timestamp;


@Data
public class Sysc08 {
    private  int IDNUM;
    private String csxdm;
    private String csqdm;
    private String vsxmc;
    private String vbz;
    private Integer id;
    private String cwlbm;
    private String cwlmc;
    private Long dtbrq;
    private Integer batchno;
    private String cdsdm;
    private Integer njd;
    private Integer nwd;
    private Long dstamp;
    private String csxdmHq;
    private String approvalFlag;
    private String approvalUser;
    private Long approvalDate;
    private Integer nradius;
    private String finalApprovalFlag;
    private String finalApprovalUser;
    private Long finalApprovalDate;
    private Timestamp createTime;
    private Timestamp updateTime;

}
