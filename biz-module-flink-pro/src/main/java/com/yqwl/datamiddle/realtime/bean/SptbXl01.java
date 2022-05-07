package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class SptbXl01 {
    private  int IDNUM;
    private String cxllx;
    private String cxldm;
    private String vxlmc;
    private String czt;
    private String cczydm;
    private Long dczsj;
    private String ctybs;
    private Long dtyrq;
    private String czjgsdm;
    private String cqwh;
    private String cqybg;
    private String cxzyss;
    private String cbs1;
    private String cbs2;
    private String cbs3;
    private String cbs4;
    private String cczydm1;
    private String cczydm2;
    private String cczydm3;
    private String cczydm4;
    private Long dshrq1;
    private Long dshrq2;
    private Long dshrq3;
    private Long dshrq4;
    private Timestamp createTime;
    private Timestamp updateTime;


}
