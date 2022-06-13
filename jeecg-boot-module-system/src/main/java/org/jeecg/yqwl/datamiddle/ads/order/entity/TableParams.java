package org.jeecg.yqwl.datamiddle.ads.order.entity;

import lombok.Data;

/**
 * @Description: 动态传表的值
 * @Author: XiaoFeng
 * @Date: 2022/6/13 17:46
 * @Version: V1.0
 */
@Data
public class TableParams {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 传值的字段
     */
    private String field;

    /**
     * 参数
     */
    private String values;

    /**
     * site_warehouse 仓库的种类字段
     */
    private String type;

    /**
     * site_warehouse 仓库的种类值
     */
    private String typeValues;
}
