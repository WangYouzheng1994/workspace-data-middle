package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.constant.TimeGranularity;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DimVlmsProvinces;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentHaveTimestamp;
import org.jeecg.yqwl.datamiddle.ads.order.util.DateUtils;
import org.jeecg.yqwl.datamiddle.ads.order.vo.*;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.util.FormatDataUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date: 2022-05-12
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
     *
     * @param baseBrandTime
     * @return 品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10StockOutList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.stockOutList(baseBrandTime);
        ShipmentVO resultVO = FormatDataUtil.formatDataList(shipment, baseBrandTime);
        return Result.OK(resultVO);
    }

    /**
     * 查询top10待发量列表
     *
     * @param baseBrandTime
     * @return 品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10PendingList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.pendingList(baseBrandTime);
        //todo:对返回前端的值做处理
        ShipmentVO resultVO = FormatDataUtil.formatDataList(shipment, baseBrandTime);
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param baseBrandTime
     * @return
     */
    @Override
    public BigDecimal getTotalOutboundQuantity(GetBaseBrandTime baseBrandTime) {
        BigDecimal totalOutboundQuantity = dwmVlmsSptb02Mapper.getTotalOutboundQuantity(baseBrandTime);
        return totalOutboundQuantity;
    }

    @Override
    public TodayIndicatorsVo getTodayIndicators(GetBaseBrandTime query) {
        TodayIndicatorsVo todayIndicatorsVo = new TodayIndicatorsVo();
        // 获取今日开始与结束时间
        Long todayStart = DateUtils.getTodayStartTimestamp();
        //        Long todayStart = DateUtils.parseDate("2022-10-09 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        Long todayEnd = todayStart + TimeGranularity.ONE_DAY_MILLI;
        query.setStartTime(todayStart);
        query.setEndTime(todayEnd);
        // 今日起运量
        Long shipmentToday = dwmVlmsSptb02Mapper.getShipmentToday(query);
        // 今日在途量
        Long onWayCountToday = dwmVlmsSptb02Mapper.getOnWayCountToday(query);
        // 今日待发量
        Long pendingCountToday= dwmVlmsSptb02Mapper.getPendingCountToday(query);
        // 今日运力需求量
        Long capacityDemandToday = dwmVlmsSptb02Mapper.getCapacityDemandToday(query);
        todayIndicatorsVo.setShipmentToday(shipmentToday);
        todayIndicatorsVo.setOnWayToday(onWayCountToday);
        todayIndicatorsVo.setPendingToday(pendingCountToday);
        todayIndicatorsVo.setCapacityDemandToday(capacityDemandToday);
        return todayIndicatorsVo;
    }

    @Override
    public List<List<ConvertDataVo>> getConvertData(GetBaseBrandTime query) {
        // 获取今日开始与结束时间
        Long todayStart = DateUtils.getTodayStartTimestamp();
//        Long todayStart = DateUtils.parseDate("2022-10-09 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        Long todayEnd = todayStart + TimeGranularity.ONE_DAY_MILLI;
        query.setStartTime(todayStart);
        query.setEndTime(todayEnd);

        List<List<ConvertDataVo>> convertDataList = new ArrayList<>();

        // 查sptb02表数据
        List<OnWayCountVo> onWayCountByCity = dwmVlmsSptb02Mapper.getOnWayCountByCity(query);
        if (CollectionUtils.isEmpty(onWayCountByCity)){
            List<ConvertDataVo> list = new ArrayList<>();
            convertDataList.add(list);
            return convertDataList;
        }
        List<String> startCitys = onWayCountByCity.stream().map(OnWayCountVo::getStartCityName).collect(Collectors.toList());
        List<String> endCitys = onWayCountByCity.stream().map(OnWayCountVo::getEndCityName).collect(Collectors.toList());
        startCitys.addAll(endCitys);
        List<String> allCitys = startCitys.stream().distinct().collect(Collectors.toList());
        // 查province表数据
        List<DimVlmsProvinces> provincesByCity = dwmVlmsSptb02Mapper.getProvincesByCity(allCitys);

        // 根据城市名分组
        Map<String, DimVlmsProvinces> provincesMap = provincesByCity.stream().collect(Collectors.toMap(item -> item.getSqsxdm() + item.getVsxmc(), Function.identity()));
        // 关联到sptb02
        onWayCountByCity.forEach(item -> {
            ConvertDataVo startCityVo = new ConvertDataVo();
            ConvertDataVo endCityVo = new ConvertDataVo();
            List<ConvertDataVo> convertDataVoList = new ArrayList<>();
            DimVlmsProvinces startProvince = provincesMap.get(item.getStartProvinceCode() + item.getStartCityCode() + item.getStartCityName());
            if (Objects.isNull(startProvince)){
                // 处理与维表对应不上的意外情况，直接跳过本次循环
                return;
            }
            BigDecimal njd = startProvince.getNjd();
            BigDecimal nwd = startProvince.getNwd();
            BigDecimal[] longitude = {njd, nwd};
            startCityVo.setName(item.getStartCityName());
            startCityVo.setCoord(longitude);
            startCityVo.setValue(item.getValue());

            DimVlmsProvinces endProvince = provincesMap.get(item.getEndProvinceCode() + item.getEndCityCode() + item.getEndCityName());
            if (Objects.isNull(endProvince)){
                return;
            }
            BigDecimal njdEnd = endProvince.getNjd();
            BigDecimal nwdEnd = endProvince.getNwd();
            BigDecimal[] longitudeEnd = {njdEnd, nwdEnd};

            endCityVo.setName(item.getEndCityName());
            endCityVo.setCoord(longitudeEnd);

            convertDataVoList.add(startCityVo);
            convertDataVoList.add(endCityVo);

            convertDataList.add(convertDataVoList);
        });
        return convertDataList;
    }

    /**
     * 插入clickhouse-dwm_vlms_sptb02表
     *
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
        String cjsdbh = "";
        int num = 5000;
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


    @Override
    public List<TopTenDataVo> getOnWayTopTenData(GetBaseBrandTime query) {
        // 获取今日开始与结束时间
        Long todayStart = DateUtils.getTodayStartTimestamp();
//        Long todayStart = DateUtils.parseDate("2022-10-09 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        Long todayEnd = todayStart + TimeGranularity.ONE_DAY_MILLI;
        // 参数过度
        Long startTime = query.getStartTime();
        Long endTime = query.getEndTime();
        query.setStartTime(todayStart);
        query.setEndTime(todayEnd);
        query.setLimitEnd(10);
        List<OnWayCountVo> onWayCountByCity = dwmVlmsSptb02Mapper.getOnWayCountByCity(query);
        List<TopTenDataVo> topTenDataVos = onWayCountByCity.stream().map(item -> {
            TopTenDataVo vo = new TopTenDataVo();
            vo.setStartCityName(!StringUtils.isEmpty(item.getStartCityName()) ? item.getStartCityName() : "");
            vo.setEndCityName(!StringUtils.isEmpty(item.getEndCityName()) ? item.getEndCityName() : "");
            vo.setValue(Objects.nonNull(item.getValue()) ? item.getValue() : 0);
            return vo;
        }).collect(Collectors.toList());
        // 设置源参数
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        return topTenDataVos;
    }

    @Override
    public List<TopTenDataVo> getArrivalsTopTen(GetBaseBrandTime query) {
        List<TopTenDataVo> arrivalsTopTen = dwmVlmsSptb02Mapper.getArrivalsTopTen(query);
        return CollectionUtils.isEmpty(arrivalsTopTen) ? new ArrayList<>() : arrivalsTopTen;
    }

    @Override
    public List<TopTenDataVo> getAmountOfPlanTopTen(GetBaseBrandTime query) {
        List<TopTenDataVo> amountOfPlanTopTen = dwmVlmsSptb02Mapper.getAmountOfPlanTopTen(query);
        return CollectionUtils.isEmpty(amountOfPlanTopTen) ? new ArrayList<>() : amountOfPlanTopTen;
    }

    @Override
    public List<TopTenDataVo> getShipmentTopTen(GetBaseBrandTime query) {
        List<TopTenDataVo> shipmentTopTen = dwmVlmsSptb02Mapper.getShipmentTopTen(query);
        return CollectionUtils.isEmpty(shipmentTopTen) ? new ArrayList<>() : shipmentTopTen;
    }


}
