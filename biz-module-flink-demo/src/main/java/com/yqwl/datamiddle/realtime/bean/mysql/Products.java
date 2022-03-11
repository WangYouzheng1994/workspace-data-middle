package com.yqwl.datamiddle.realtime.bean.mysql;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Products {

    private Integer productId;
    private String productName;
    private BigDecimal productAmount;
    private Integer productStock;


    // Java Bean 必须实现的方法，信息通过字符串进行拼接
    public static String convertToCsv(Products model) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");


        builder.append(model.getProductId());
        builder.append(", ");


        builder.append("'");
        builder.append(model.getProductName());
        builder.append("', ");


        //builder.append("'");
        builder.append(model.getProductStock());
        builder.append(", ");


        builder.append(model.getProductAmount());

        builder.append(" )");
        return builder.toString();
    }

}
