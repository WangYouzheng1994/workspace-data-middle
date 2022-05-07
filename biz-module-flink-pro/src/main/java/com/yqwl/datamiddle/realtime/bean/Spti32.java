package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Spti32 {
    private  int IDNUM;
    private String cqssqdm;
    private String cqscsdm;
    private String cmbsqdm;
    private String cmbcsdm;
    private BigDecimal nlc;
    private String czjgs;
    private String vysfs;
    private Long nztsj;
    private Long nts;
    private String cbz;
    private Long dczrq;
    private String cczydm;
    private Long ndhsjGps;
    private Long ndhsjXt;
    private Timestamp createTime;
    private Timestamp updateTime;

}
