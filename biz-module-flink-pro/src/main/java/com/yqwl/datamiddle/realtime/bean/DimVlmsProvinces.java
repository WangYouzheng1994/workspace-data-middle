package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DimVlmsProvinces {
    private Long IDNUM;

    private String csqdm;

    private String cdsdm;

    private String csxdm;

    //在原视图的基础上新增的一条联合的数据:省区市县代码
    private String sqsxdm;

    private String vsqmc;

    private String vsqjc;

    private String vdsmc;

    private String vsxmc;

    private String cjc;

    private String cdqdm;

    private String vdqmc;

    private String cwlbm3;

    private String cwlmc3;

    private BigDecimal njd;

    private BigDecimal nwd;

    private String cwlmc;

    private String cwlbm_sq;

    private Long WAREHOUSE_CREATETIME;

    private Long WAREHOUSE_UPDATETIME;
}
