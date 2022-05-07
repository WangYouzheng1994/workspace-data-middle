package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
@Data
public class Sptb013{
    private  int IDNUM;
    private String vph;
    private String czt;
    private String vfcck;
    private String vfczt;
    private String vsczt;
    private Integer njhsl;
    private Integer nsjsl;
    private String vyss;
    private String vczy;
    private Long dczrq;
    private Long dtjrq;
    private String ctybs;
    private Long dtyrq;
    private String vgs;
    private String vtllh;
    private String cbs;
    private String cfbbs;
    private String cdbyss;
    private String cljsdm;
    private String ccbh;
    private Timestamp createTime;
    private Timestamp updateTime;


}
