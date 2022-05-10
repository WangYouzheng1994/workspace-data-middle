package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;


@Data
public class Sptb02RailseaRk {
    private Integer  idnum;
    private String cjsdbh;
    private String vvin;
    private String vwlckdm;
    private String vwlckmc;
    private Integer warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private Long drkrq;
    private Timestamp createTime;
    private Timestamp updateTime;


}
