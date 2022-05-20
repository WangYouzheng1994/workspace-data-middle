package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
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
     List<GetBaseBrandTime> stockOUtList(@Param("params") DwmVlmsSptb02 params);

     /**
      * top10 发运量
      * @param params
      * @return
      */
     List<GetBaseBrandTime> sendList(@Param("params") DwmVlmsSptb02 params);

     /**
      * top10待发量
      * @param params
      * @return
      */
     List<GetBaseBrandTime> pendingList (@Param("params") DwmVlmsSptb02 params);

     /**
      * top10在途量
      * @param params
      * @return
      */
     List<GetBaseBrandTime> onWayList (@Param("params") DwmVlmsSptb02 params);

    /**
     * 按条件查询计划量
     * @param params
     * @return
     */
    List<DwmVlmsSptb02> getPlanAmount(@Param("params") GetBaseBrandTime params);

    /**
     * 按条件查询发运量
     * @param params
     * @return
     */
    List<ShipmentDTO> getShipment(@Param("params") GetBaseBrandTime params);

    /**
     * 到货准时率
     * @param params
     * @return
     */
    DwmVlmsSptb02 getArrivalRate(@Param("params") GetBaseBrandTime params);
}
