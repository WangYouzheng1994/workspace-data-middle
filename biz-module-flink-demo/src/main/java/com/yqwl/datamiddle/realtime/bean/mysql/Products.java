package com.yqwl.datamiddle.realtime.bean.mysql;

import lombok.Data;

/**
 * 注意：
 *      类中属性的排列顺序一定要和clickhouse中表中字段属性顺序保持一致
 *      clickhouse 接受的数据格式为 csv 格式
 *   事例如下：
 *      (1, '小米手环6 全面彩屏', 654, 200)
 *
 */
@Data
public class Products {

    private Integer productId;
    private String productName;
    private Integer productStock;
    private Integer productAmount;


}
