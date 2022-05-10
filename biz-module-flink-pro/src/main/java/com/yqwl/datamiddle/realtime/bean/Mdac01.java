package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;


@Data
@ToString
public class Mdac01 {

//    private  Integer IDNUM;


    private String cdqdm;

    private String vdqmc;

    private String clxrdm;

    private String vdh;

    private String cfzrdm;

    private String csqdm;

    private String csxdm;

    private String vdz;

    private String vcz;

    private String veMail;

    private String cyb;

    private String vbz;

    private String ctybs;

    private Long dtyrq;

    private Timestamp createTime;

    private Timestamp updateTime;

    //新加kafka的ts时间戳
    private Timestamp ts;

}

