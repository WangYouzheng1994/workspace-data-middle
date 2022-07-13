package org.jeecg.yqwl.datamiddle.ads.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DwmSptb02VO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;


import java.math.BigDecimal;
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
    List<ShipmentDTO> getFINAL_SITE_TIME (@Param("params") GetBaseBrandTime baseBrandTime);

    /**
     * 到货样本总量
     * @param params
     * @return
     */
    BigDecimal getArrivalRate(@Param("params") GetBaseBrandTime params);

    /**
     * 获取到货准时样本总数量
     * @param params
     * @return
     */
    BigDecimal getArriveOnTime(@Param("params") GetBaseBrandTime params);


    /**
     * 获取起运准时样本数量
     * @param params
     * @return
     */
    BigDecimal getTimelyShipment(@Param("params") GetBaseBrandTime params);

    /**
     * 获取起运总体样本数量
     * @param params
     * @return
     */
    BigDecimal getTotalShipment(@Param("params") GetBaseBrandTime params);

    /**
     * 获取出库准时样本总量
     * @param params
     * @return
     */
    BigDecimal getOnTimeDelivery(@Param("params") GetBaseBrandTime params);

    /**
     * 获取出库数据总量
     * @param params
     * @return
     */
    BigDecimal getTotalOutboundQuantity(@Param("params") GetBaseBrandTime params);

    /**
     * 插入clickhouse-dwm_vlms_sptb02表
     * @param params
     */
    void insertClickhouse(@Param("list") List<DwmSptb02VO> params);


    /**
     * 根据vin码获取 trafficType，剔除J类型的
     *
     * @param vins
     * @return
     */
    List<DwmVlmsSptb02> getSptbTrafficTypeByVin(List<String> vins);

    /**
     * 一单到底---DOCS
     * @return
     */
    List<DwmVlmsDocs> selectDocsList(@Param("params") GetQueryCriteria queryCriteria);

    /**
     *
     * @param queryCriteria
     * @return
     */
    Integer countDocsList(@Param("params") GetQueryCriteria queryCriteria);

}
