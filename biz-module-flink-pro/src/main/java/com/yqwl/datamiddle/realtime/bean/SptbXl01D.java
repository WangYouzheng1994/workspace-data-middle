package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;

@Data
public class SptbXl01D {
    private Integer  idnum;
    private String cxldm;
    private String cqddm;
    private String vqdmc;
    private String czddm;
    private String vzdmc;
    private String cczydm;
    private Long dczsj;
    private String ctybs;
    private Long dtyrq;
    private String czjgsdm;
    private Timestamp createTime;
    private Timestamp updateTime;


}
