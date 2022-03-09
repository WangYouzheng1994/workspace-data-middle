package com.yqwl.datamiddle.realtime.bean.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrdersDetail {

    private Integer id;
    private String orderNo;
    private Integer productId;
    private BigDecimal productAmount;
    private Date createTime;


}
