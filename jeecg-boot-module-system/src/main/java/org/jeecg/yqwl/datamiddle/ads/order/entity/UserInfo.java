package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/5/12 20:08
 * @Version: V1.0
 */
@Data
@TableName("test2")
public class UserInfo {
    private Integer id;
    private String name;
    private String address;
}
