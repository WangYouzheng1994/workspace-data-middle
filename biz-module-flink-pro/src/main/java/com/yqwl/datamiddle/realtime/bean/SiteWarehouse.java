package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;
import java.util.Objects;

@Data
public class SiteWarehouse {
    private String vwlckdm;
    private String vwlckmc;
    private Integer warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String cbz;
    private Long dczrq;
    private String cczydm;
    private String type;
    private Timestamp createTime;
    private Timestamp updateTime;


}
