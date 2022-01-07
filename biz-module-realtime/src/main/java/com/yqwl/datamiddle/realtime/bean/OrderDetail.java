package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 订单明细表
 * @Author: WangYouzheng
 * @Date: 2022/1/7 9:38
 * @Version: V1.0
 */
@Data
public class OrderDetail {
    Long id;
    Long order_id;
    Long sku_id;
    BigDecimal order_price;
    Long sku_num;
    String sku_name;
    String create_time;
    BigDecimal split_total_amount;
    BigDecimal split_activity_amount;
    BigDecimal split_coupon_amount;
    Long create_ts;
}