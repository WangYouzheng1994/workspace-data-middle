package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;


@Data
public class RfidWarehouse {
    private int id;
    private String warehouseCode;
    private String warehouseName;
    private String partBase;
    private String warehouseType;
    private String city;
    private Integer defalutValue;
    private Integer bzkr;
    private Integer zdkr;
    private String datacenterCode;
    private Integer sequence;
    private String province;
    private String zcCode;
    private String czjgsdm;
    private String datacenterCodeBzk;
    private String provice;
    private Timestamp createTime;
    private Timestamp updateTime;


}
