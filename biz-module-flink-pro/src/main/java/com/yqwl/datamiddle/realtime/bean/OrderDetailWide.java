package com.yqwl.datamiddle.realtime.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description: 订单宽表实体类
 * @Author: WangYouzheng
 * @Date: 2022/1/7 9:46
 * @Version: V1.0
 */
@Data
@AllArgsConstructor
public class OrderDetailWide {
    private Integer id;
    private String orderNo;
    private Integer productId;
    private Integer productAmount;
    private Integer productCount;
    private Long createTime;


    private String productName;
    private Integer userId;
    private String username;

    /**
     * 合并订单表和明细表的数据
     *
     * @param orderInfo
     * @param orderDetail
     */
    public OrderDetailWide(Orders orderInfo, OrdersDetail orderDetail) {
        mergeOrderInfo(orderInfo);
        mergeOrderDetail(orderDetail);
    }

    /**
     * 合并订单数据
     *
     * @param orderInfo
     */
    public void mergeOrderInfo(Orders orderInfo) {
        if (orderInfo != null) {
            this.id = orderInfo.getId();
            this.orderNo = orderInfo.getOrderNo();
            this.createTime = orderInfo.getCreateTime();
            this.userId = orderInfo.getUserId();
        }
    }

    /**
     * 合并设置明细数据
     *
     * @param orderDetail
     */
    public void mergeOrderDetail(OrdersDetail orderDetail) {
        if (orderDetail != null) {
            this.productId = orderDetail.getProductId();
            this.productAmount = orderDetail.getProductAmount();
            this.productCount = orderDetail.getProductCount();
        }
    }


}