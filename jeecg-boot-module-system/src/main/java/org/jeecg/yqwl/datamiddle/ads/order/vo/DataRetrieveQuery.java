package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

/**
 * 数据质量菜单检索条件
 * @author dabao
 * @date 2022/8/30
 */
@Data
public class DataRetrieveQuery {
    /**
     * 分页：页码
     */
    private Integer pageNo = 1;

    /**
     * 分页：每页大小
     */
    private Integer pageSize = 20;

    /**
     * limit开始
     */
    private Integer limitStart;

    /**
     * limit结束
     */
    private Integer limitEnd;

    /**
     * 详情关联主表的信息
     */
    private String infoCode;

}
