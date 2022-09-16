package org.jeecg.yqwl.datamiddle.ads.order.entity.ext;

import lombok.Data;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/5/20 14:07
 * @Version: V1.0
 */
@Data
public class ShipmentHaveTimestamp extends ShipmentDTO{

    /**
     * 时间戳类型的时间
     */
    private Long dateTimestamp;

    private Long endTime;

}
