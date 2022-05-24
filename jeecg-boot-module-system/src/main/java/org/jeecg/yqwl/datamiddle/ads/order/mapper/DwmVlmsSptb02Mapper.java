package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;

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
      * @param baseBrandTime
      * @return
      */
     List<ShipmentDTO> stockOutList(@Param("params") GetBaseBrandTime baseBrandTime);

     /**
      * top10 发运量
      * @param baseBrandTime
      * @return
      */
//     List<ShipmentDTO > sendList(@Param("params") GetBaseBrandTime baseBrandTime);

     /**
      * top10待发量
      * @param baseBrandTime
      * @return
      */
     List<ShipmentDTO> pendingList (@Param("params") GetBaseBrandTime baseBrandTime);



     /**
      * top10在途量
      * @param baseBrandTime
      * @return
      */
     List<ShipmentDTO> onWayList (@Param("params") GetBaseBrandTime baseBrandTime);

    /**
     * 按条件查询计划量
     * @param params
     * @return
     */
    List<ShipmentDTO> getPlanAmount(@Param("params") GetBaseBrandTime params);

    /**
     * 按条件查询发运量
     * @param params
     * @return
     */
    List<ShipmentDTO> getShipment(@Param("params") GetBaseBrandTime params);

    /**
     * 按条件查询到货量
     * @param baseBrandTime
     * @return
     */
    List<ShipmentDTO > getFINAL_SITE_TIME (@Param("params") GetBaseBrandTime baseBrandTime);

    /**
     * 到货准时率
     * @param params
     * @return
     */
    DwmVlmsSptb02 getArrivalRate(@Param("params") GetBaseBrandTime params);
}
