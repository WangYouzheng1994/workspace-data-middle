package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DwmSptb02VO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;
import org.jeecg.yqwl.datamiddle.util.FormatDataUtil;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@Slf4j
@DS("slave")
@Service
public class DwmVlmsSptb02ServiceImpl extends ServiceImpl<DwmVlmsSptb02Mapper, DwmVlmsSptb02> implements IDwmVlmsSptb02Service {
    @Resource
    private DwmVlmsSptb02Mapper dwmVlmsSptb02Mapper;

    /**
     * 查询出库量列表
     * @param baseBrandTime
     * @return 品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10StockOutList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.stockOutList(baseBrandTime);
        ShipmentVO resultVO = FormatDataUtil.formatDataList(shipment,baseBrandTime);
        return Result.OK(resultVO);
    }

    /**
     * 查询top10待发量列表
     * @param  baseBrandTime
     * @return  品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10PendingList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.pendingList(baseBrandTime);
        //todo:对返回前端的值做处理
        ShipmentVO resultVO = FormatDataUtil.formatDataList(shipment,baseBrandTime);
        return Result.OK(resultVO);
    }

    /**
     * 查询top10在途量列表
     * @param baseBrandTime
     * @return  品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10OnWayList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.onWayList(baseBrandTime);
        ShipmentVO resultVO = FormatDataUtil.formatDataList(shipment,baseBrandTime);
        return Result.OK(resultVO);
    }
    /**
     * 按条件查询到货量
     *
     * @param baseBrandTime
     * @return
     */
    @Override
    public Result<ShipmentVO> getFINAL_SITE_TIME(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> planAmount = dwmVlmsSptb02Mapper.getFINAL_SITE_TIME(baseBrandTime);
        //todo:对返回前端的值做处理
        ShipmentVO shipmentVO = FormatDataUtil.formatDataList(planAmount, baseBrandTime);
        return Result.OK(shipmentVO);
    }
    /**
     * 按条件查询计划量
     * @param baseBrandTime
     * @return
     */
    @Override
    public Result<ShipmentVO> findDayAmountOfPlan(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> planAmount = dwmVlmsSptb02Mapper.getPlanAmount(baseBrandTime);
        //todo:对返回前端的值做处理
        ShipmentVO shipmentVO = FormatDataUtil.formatDataList(planAmount, baseBrandTime);
        return Result.OK(shipmentVO);
    }

    /**
     * 按条件查询发运量
     * @param baseBrandTime
     * @return
     */
    @Override
    public Result<ShipmentVO> findShipment(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.getShipment(baseBrandTime);
        ShipmentVO shipmentVO = FormatDataUtil.formatDataList(shipment, baseBrandTime);
        return Result.OK(shipmentVO);
    }

    /**
     * 获取到货样本数据总量
     *
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal findArrivalRate(GetBaseBrandTime baseBrandTime) {
        /**
         *1.获取已到货未超理论实践的值
         */
        BigDecimal arrivalRate = dwmVlmsSptb02Mapper.getArrivalRate(baseBrandTime);
        return arrivalRate;
    }

    /**
     * 获取准时到达样本总量
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal getArriveOnTime(GetBaseBrandTime baseBrandTime) {
        BigDecimal arriveOnTime = dwmVlmsSptb02Mapper.getArriveOnTime(baseBrandTime);
        return arriveOnTime;
    }

    /**
     * 起运样本总量
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal getTotalShipment(GetBaseBrandTime baseBrandTime) {
        BigDecimal totalShipment = dwmVlmsSptb02Mapper.getTotalShipment(baseBrandTime);
        return totalShipment;
    }

    /**
     * 起运及时样本总量
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal getTimelyShipment(GetBaseBrandTime baseBrandTime) {
        BigDecimal timelyShipment = dwmVlmsSptb02Mapper.getTimelyShipment(baseBrandTime);
        return timelyShipment;
    }

    /**
     * 出库及时样本总量
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal getOnTimeDelivery(GetBaseBrandTime baseBrandTime) {
        BigDecimal onTimeDelivery = dwmVlmsSptb02Mapper.getOnTimeDelivery(baseBrandTime);
        return onTimeDelivery;
    }

    /**
     * 出库样本总量
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal getTotalOutboundQuantity(GetBaseBrandTime baseBrandTime) {
        BigDecimal totalOutboundQuantity = dwmVlmsSptb02Mapper.getTotalOutboundQuantity(baseBrandTime);
        return totalOutboundQuantity;
    }

    /**
     * 插入clickhouse-dwm_vlms_sptb02表
     * @param dwmSptb02VO
     */
    @Override
    public void insertClickhouse(DwmSptb02VO dwmSptb02VO) {
        Long actual_out_time = dwmSptb02VO.getACTUAL_OUT_TIME();
        Long theory_out_time = dwmSptb02VO.getTHEORY_OUT_TIME();
        String base_name = dwmSptb02VO.getBASE_NAME();
        String customer_name = dwmSptb02VO.getCUSTOMER_NAME();
        String cqwh = dwmSptb02VO.getCQWH();
        String czjgsdm = dwmSptb02VO.getCZJGSDM();
        String cjsdbh="";
        int num =5000;
        int numValue = 1;

        DwmSptb02VO dwmSptb02VO1;
        List<DwmSptb02VO> dwmSptb02VOS = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            String uuid = UUID.randomUUID().toString();
            dwmSptb02VO1 = new DwmSptb02VO();
            dwmSptb02VO1.setCJSDBH(uuid);
            dwmSptb02VO1.setDDJRQ(2L);
            dwmSptb02VO1.setACTUAL_OUT_TIME(actual_out_time);
            dwmSptb02VO1.setTHEORY_OUT_TIME(theory_out_time);
            dwmSptb02VO1.setBASE_NAME(base_name);
            dwmSptb02VO1.setCUSTOMER_NAME(customer_name);
            dwmSptb02VO1.setCQWH(cqwh);
            dwmSptb02VO1.setCZJGSDM(czjgsdm);
            dwmSptb02VOS.add(dwmSptb02VO1);
        }
        dwmVlmsSptb02Mapper.insertClickhouse(dwmSptb02VOS);
    }


}
