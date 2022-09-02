package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName data_retrieve_detail
 */
@TableName(value ="data_retrieve_detail")
@Data
public class DataRetrieveDetail implements Serializable {
    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联主表code
     */
    private String infoCode;

    /**
     * 车辆底盘码（用于查询数据）
     */
    private String vin;

    /**
     * 来源 0本库，1源表
     */
    private Integer source;

    /**
     * 是否被删除 0未删除，1删除
     */
    @TableLogic(value = "0")
    private Integer isDel;


}