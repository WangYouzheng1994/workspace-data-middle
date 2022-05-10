package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;
import lombok.ToString;


import java.math.BigDecimal;
import java.sql.Timestamp;


@Data
@ToString
public class Sysc08 {
    //    private  Integer IDNUM;
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
    private BigDecimal njd;
    private BigDecimal nwd;
    private Long dstamp;
    private String csxdmHq;
    private String approvalFlag;
    private String approvalUser;
    private Long approvalDate;
    private BigDecimal nradius;
    private String finalApprovalFlag;
    private String finalApprovalUser;
    private Long finalApprovalDate;
    private Timestamp createTime;
    private Timestamp updateTime;

    //新加kafka的ts时间戳
    private Timestamp ts;


}
