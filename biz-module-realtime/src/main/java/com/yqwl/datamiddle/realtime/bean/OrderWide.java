package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/1/7 9:46
 * @Version: V1.0
 */
@Data
public class OrderWide {
    Long detail_id;
    Long order_id ;
    Long sku_id;
    BigDecimal order_price ;
    Long sku_num ;
    String sku_name;
    Long province_id;
    String order_status;
    Long user_id;
    BigDecimal total_amount;
    BigDecimal activity_reduce_amount;
    BigDecimal coupon_reduce_amount;
    BigDecimal original_total_amount;
    BigDecimal feight_fee;
    BigDecimal split_feight_fee;
    BigDecimal split_activity_amount;
    BigDecimal split_coupon_amount;
    BigDecimal split_total_amount;
    String expire_time;
    String create_time;
    String operate_time;
    String create_date; // 把其他字段处理得到
    String create_hour;
    String province_name;//查询维表得到
    String province_area_code;
    String province_iso_code;
    String province_3166_2_code;
}
