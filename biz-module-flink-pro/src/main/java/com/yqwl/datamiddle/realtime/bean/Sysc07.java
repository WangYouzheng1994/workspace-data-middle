package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Sysc07 {
    private String csqdm;
    private String vsqmc;
    private String cdqdm;
    private String vbz;
    private Integer id;
    private String cjc;
    private String cwlbm;
    private String cwlmc;
    private Long dtbrq;
    private Integer batchno;
    private String cjc2;
    private String cshdm;
    private Long dstamp;
    private String approvalFlag;
    private String approvalUser;
    private Long approvalDate;
    private String finalApprovalFlag;
    private String finalApprovalUser;
    private Long finalApprovalDate;
    private Timestamp createTime;
    private Timestamp updateTime;

}
