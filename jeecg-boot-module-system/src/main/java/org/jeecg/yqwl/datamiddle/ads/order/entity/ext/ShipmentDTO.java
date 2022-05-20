package org.jeecg.yqwl.datamiddle.ads.order.entity.ext;

import lombok.Data;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/5/20 14:07
 * @Version: V1.0
 */
@Data
public class ShipmentDTO {
    /**
     * 基地或品牌
     */
    private String groupName;

    /**
     * 数据量
     */
    private Integer totalNum;

    /**
     * 日期
     * 天：yyyy-MM-dd
     * 周：
     * 月：
     * 季度：
     * 年：
     */
    private String dates;
}
