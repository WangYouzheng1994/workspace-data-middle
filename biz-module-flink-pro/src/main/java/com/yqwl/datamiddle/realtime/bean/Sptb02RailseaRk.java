package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Sptb02RailseaRk {
    private  int IDNUM;
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
