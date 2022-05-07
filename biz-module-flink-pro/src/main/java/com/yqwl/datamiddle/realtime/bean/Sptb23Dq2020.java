package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Sptb23Dq2020 {
    private String vin;
    private String ch;
    private String jjdh;
    private Long xdsj;
    private String pp;
    private String dcs;
    private String fz;
    private String dz;
    private Long fcsj;
    private Long ddsj;
    private String sheng;
    private String shi;
    private String xian;
    private Long bgsj;
    private String zkbz;
    private Long dczrq;
    private String cbs;
    private BigDecimal jd;
    private BigDecimal wd;
    private Integer id;
    private Timestamp createTime;
    private Timestamp updateTime;
}
