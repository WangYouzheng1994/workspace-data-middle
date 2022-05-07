package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


@Data
public class OrdersDetail {

    private Integer id;
    private String orderNo;
    private Integer productId;
    private Integer productAmount;
    private Integer productCount;
    private Long createTime;

}
