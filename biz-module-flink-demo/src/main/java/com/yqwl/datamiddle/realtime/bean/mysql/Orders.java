package com.yqwl.datamiddle.realtime.bean.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Orders {

    private Integer id;
    private String orderNo;
    private Integer status;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private Date createTime;

}
