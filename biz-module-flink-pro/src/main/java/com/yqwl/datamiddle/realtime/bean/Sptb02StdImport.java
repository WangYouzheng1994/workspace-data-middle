package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Sptb02StdImport{
    private int id;
    private String cjsdbh;
    private String cpzdbh;
    private String vwxdwdm;
    private String cjhdh;
    private String vysfs;
    private String type;
    private String createBy;
    private String createByName;
    private Long createDate;
    private Long sdDate;
    private Long vdhzsxGps;
    private Long dgpsdhsj;
    private String approverUser;
    private Long dshsjDz;
    private Timestamp createTime;
    private Timestamp updateTime;

}
