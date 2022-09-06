package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/5/20 15:51
 * @Version: V1.0
 */
@Data
public class ShipmentVO {
    /**
     * 时间 x轴。
     */
    List<String> timing;

    List<Item> result;

    /**
     * 设置x轴
     *
     * @param timing
     */
    public static ShipmentVO of(List<String> timing) {
        ShipmentVO shipmentVO = new ShipmentVO();
        shipmentVO.timing = timing;
        shipmentVO.result = new ArrayList<>(timing.size());
        return shipmentVO;
    }

    /**
     * 新增返回值项
     * @param item
     */
    public void addResultItem(Item item) {
        this.result.add(item);
    }

    @Data
    public static class Item {
        private String name;
        private List<Integer> dataList;
    }
}
