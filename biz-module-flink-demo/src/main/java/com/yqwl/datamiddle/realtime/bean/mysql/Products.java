package com.yqwl.datamiddle.realtime.bean.mysql;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Products {

    private Integer productId;
    private String productName;
    private BigDecimal productAmount;
    private Integer productStock;




}
