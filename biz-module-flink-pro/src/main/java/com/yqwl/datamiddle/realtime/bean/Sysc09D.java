package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Sysc09D {
    private  int IDNUM;
    private String czddm;
    private String csjxm;
    private String vsjxc;
    private String cjb;
    private String ctybs;
    private Long dtyrq;
    private String vbz;
    private String vbz2;
    private String vbz3;
    private Timestamp createTime;
    private Timestamp updateTime;

}
