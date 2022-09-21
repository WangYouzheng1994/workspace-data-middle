package org.jeecg.yqwl.datamiddle.ads.order.vo;


import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * 存放vvinList查出数据条数的类
 */
@Data
public class VvinGroupQuery {

    /**
     * 利用vvinList查出来的数据条数
     */
    private Integer dataCount;

    /**
     * 当前总数
     */
    private Integer currentTotal;

    /**
     * 分组的vvin
     */
    private List<String> vvinList;

    private List<String> vinList;

}
