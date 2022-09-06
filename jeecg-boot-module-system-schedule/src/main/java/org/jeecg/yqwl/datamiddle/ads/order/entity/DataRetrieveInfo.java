package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日检索数据信息表

 * @TableName data_retrieve_info
 */
@TableName(value ="data_retrieve_info")
@Data
public class DataRetrieveInfo implements Serializable {
    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 唯一值
     */
    private String code;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 检索时间
     */
    private Long retrieveTime;

    /**
     * 检索范围（近一个月，时间开始0点--时间结束23点59分59秒）
     */
    private String retrieveRange;

    /**
     * 源异常数据量
     */
    private Integer abnormalCountOrigin;

    /**
     * 本库异常数据量
     */
    private Integer abnormalCountSelf;

    /**
     * 是否有效
     */
    @TableLogic(value = "0")
    private Integer isDel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

}