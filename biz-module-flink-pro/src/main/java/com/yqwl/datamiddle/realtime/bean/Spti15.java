package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
@Data
public class Spti15{
    private String cddy;
    private String cyysdm;
    private Long dczrq;
    private String cczydm;
    private String vbz;
    private String cgs;
    private String ctybs;
    private Long dtyrq;
    private String ctyyy;
    private Timestamp createTime;
    private Timestamp updateTime;
}
