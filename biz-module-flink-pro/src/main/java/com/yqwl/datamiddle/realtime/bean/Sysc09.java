package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;


import java.sql.Timestamp;


@Data
public class Sysc09 {
    private Integer  idnum;
    private String cdsdm;
    private String csqdm;
    private String vdsmc;
    private String vbz;
    private Long dstamp;
    private String cwlbm;
    private String cwlmc;
    private Timestamp createTime;
    private Timestamp updateTime;


}
