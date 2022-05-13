package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.UserInfo;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/5/12 20:00
 * @Version: V1.0
 */
public interface  UserInfoMapper extends BaseMapper<UserInfo> {

    // ID 查询
  public  UserInfo selectById (@Param("id") Integer id) ;
    // 查询全部

}
