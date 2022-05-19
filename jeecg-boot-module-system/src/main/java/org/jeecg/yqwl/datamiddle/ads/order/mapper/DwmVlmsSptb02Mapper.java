package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;

import java.util.List;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface DwmVlmsSptb02Mapper extends BaseMapper<DwmVlmsSptb02> {

     /**
      * 出库量
      * @param params
      * @return
      */
     List<DwmVlmsSptb02> stockOUtList(@Param("params") GetBaseBrandTime params);
     /**
      * top10 发运量
      * @param params
      * @return
      */
     List<DwmVlmsSptb02> sendList(@Param("params") GetBaseBrandTime params);

     /**
      * top10待发量
      * @param params
      * @return
      */
     List<DwmVlmsSptb02> pendingList (@Param("params") GetBaseBrandTime params);

     /**
      * top10在途量
      * @param params
      * @return
      */
     List<DwmVlmsSptb02> onWayList (@Param("params") GetBaseBrandTime params);



     List<DwmVlmsSptb02> getPlanAmount(@Param("params") GetBaseBrandTime params);

}
