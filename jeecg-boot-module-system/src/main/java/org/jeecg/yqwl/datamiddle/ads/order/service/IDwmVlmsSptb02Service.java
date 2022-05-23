package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;
import java.math.BigDecimal;


/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@DS("slave0")
public interface IDwmVlmsSptb02Service extends IService<DwmVlmsSptb02> {

    /**
     * 出库量查询列表
     * @param baseBrandTime
     * @return
     */
    Result<ShipmentVO> findTop10StockOutList(GetBaseBrandTime baseBrandTime);
    /**
     * 查询top10发运量列表
     *@param baseBrandTime
     * @return
     */
//    Result<ShipmentVO> findTop10SendList(GetBaseBrandTime baseBrandTime);


    /**
     * 查询top10待发量列表
     * @param baseBrandTime
     * @return
     */
    Result<ShipmentVO> findTop10PendingList(GetBaseBrandTime baseBrandTime);


    /**
     * 查询top10在途量列表
     * @param baseBrandTime
     * @return
     */
    Result<ShipmentVO> findTop10OnWayList(GetBaseBrandTime baseBrandTime);



    /**
     * 按条件查询计划量
     * @param baseBrandTime
     * @return
     */
    Result<ShipmentVO> findDayAmountOfPlan(GetBaseBrandTime baseBrandTime );

    /**
     * 按条件查询发运量
     *
     * @param baseBrandTime
     * @return
     */
    Result<ShipmentVO> findShipment(GetBaseBrandTime baseBrandTime );

    /**
     * 按条件查询到货量
     *
     * @param baseBrandTime
     * @return
     */
    Result<ShipmentVO> getFINAL_SITE_TIME(GetBaseBrandTime baseBrandTime );
    /**
     * 获取到货及时率
     *
     * @param baseBrandTime
     * @return
     */
    Result<BigDecimal> findArrivalRate(GetBaseBrandTime baseBrandTime );




}
