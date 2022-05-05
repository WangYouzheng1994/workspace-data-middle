package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Orders {

    private Integer id;
    private String orderNo;
    private Integer status;
    private Integer discountAmount;
    private Integer totalAmount;
    private Integer paymentAmount;
    private Long createTime;
    private Integer userId;

}
