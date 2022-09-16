package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.handler.IFillRuleHandler;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentHaveTimestamp;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


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

    private static final Long ONE_DAY_MILLI = 86400000L;

    private static final Long ONE_MONTH_MILLI = 2592000000L;

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
        //考虑数据量问题，目前只显示30天的在运量
        if (baseBrandTime.getEndTime() - baseBrandTime.getStartTime() > ONE_MONTH_MILLI){
            baseBrandTime.setEndTime(baseBrandTime.getStartTime() + ONE_MONTH_MILLI.longValue());
        }
        //查出该时间段内已经起运，没有实际到货的数据
        List<DwmSptb02> dwmVlmsSptb02s = dwmVlmsSptb02Mapper.getOnwayDatas(baseBrandTime);
        //创建时间段和计量单位（年月日分组）的数组
        List<ShipmentHaveTimestamp> allTime = createAllTime(baseBrandTime);

        //构造ShipmentDTO
        List<ShipmentDTO> shipmentDTOS = new ArrayList<>();
        allTime.forEach(item -> {
            //计算符合条件的数据量
            Map<String, List<DwmSptb02>> dwmSptb02Map = dwmVlmsSptb02s.stream().filter(data -> {

                if (data.getFinalSiteTime() >= item.getEndTime() && data.getShipmentTime() <= item.getEndTime()){
                    //今天没到货，今天或今天之前起运，符合条件
                    return true;
                }
                if (data.getShipmentTime() <= item.getEndTime() &&
                        (data.getFinalSiteTime().equals(0L) || Objects.isNull(data.getFinalSiteTime())) ){
                    //已经发运但是没有到货
                    return true;
                }
                return false;
            }).collect(Collectors.groupingBy(DwmSptb02::getBaseName));


            dwmSptb02Map.forEach( (key,value) -> {
                ShipmentDTO shipmentDTO = new ShipmentDTO();
                shipmentDTO.setTotalNum(value.size());
                shipmentDTO.setGroupName(key);
                shipmentDTO.setDates(item.getDates());
                shipmentDTOS.add(shipmentDTO);
            });
        });

        ShipmentVO resultVO = FormatDataUtil.formatDataList(shipmentDTOS,baseBrandTime);
        return Result.OK(resultVO);
    }

    private List<ShipmentHaveTimestamp> createAllTime(GetBaseBrandTime baseBrandTime) {
        List<ShipmentHaveTimestamp> times = new ArrayList<>();
        //暂时按天计算，按其他时间粒度计算有歧义
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //开始到结束的天数 一天86400000毫秒
            BigDecimal dayNums = BigDecimal.valueOf(baseBrandTime.getEndTime()).subtract(BigDecimal.valueOf(baseBrandTime.getStartTime()))
                    .divide(BigDecimal.valueOf(ONE_DAY_MILLI), 0, BigDecimal.ROUND_DOWN);
            Date date = new Date(baseBrandTime.getStartTime());
            for (int i = 0; i <= dayNums.intValue(); i++){
                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                cd.add(Calendar.DATE,i);
                ShipmentHaveTimestamp haveTimestamp = new ShipmentHaveTimestamp();
                haveTimestamp.setDates(dateFormat.format(cd.getTime()));
                haveTimestamp.setDateTimestamp(cd.getTimeInMillis());
                haveTimestamp.setEndTime(cd.getTimeInMillis() + ONE_DAY_MILLI);
                times.add(haveTimestamp);
            }
//        else if ("week".equals(baseBrandTime.getTimeType())) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-ww");
//            Date startDate = new Date(baseBrandTime.getStartTime());
//            //开始到结束的天数 一天86400000毫秒
//            BigDecimal dayNums = BigDecimal.valueOf(baseBrandTime.getEndTime()).subtract(BigDecimal.valueOf(baseBrandTime.getStartTime()))
//                    .divide(BigDecimal.valueOf(86400000), 0, BigDecimal.ROUND_UP);
//
//            //计算周数
//            int weekNum = dayNums.divide(BigDecimal.valueOf(7),0,BigDecimal.ROUND_UP).intValue();
//            for (int i = 1; i <= weekNum; i++){
//                Calendar cd = Calendar.getInstance();
//                cd.setTime(startDate);
//                cd.add(Calendar.WEEK_OF_YEAR,i);
//                times.add(dateFormat.format(cd.getTime()));
//            }
//        } else if ("month".equals(baseBrandTime.getTimeType())) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
//            Date startDate = new Date(baseBrandTime.getStartTime());
//            Date endDate = new Date(baseBrandTime.getEndTime());
//            int startMonth = DateUtils.getMonth(startDate);
//            int endMonth = DateUtils.getMonth(endDate);
//            //计算年数
//            int years = DateUtils.getYear(startDate) - DateUtils.getYear(endDate);
//            int monthNum = years * 12 + endMonth - startMonth + 1;
//            for (int i = 1; i <= monthNum; i++){
//                Calendar cd = Calendar.getInstance();
//                cd.setTime(startDate);
//                cd.add(Calendar.MONTH,i);
//                times.add(dateFormat.format(cd.getTime()));
//            }
//        } else if ("quarter".equals(baseBrandTime.getTimeType())) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
//            Date startDate = new Date(baseBrandTime.getStartTime());
//            int startSeason = DateUtils.getSeason(startDate);
//            int endSeason = DateUtils.getSeason(new Date(baseBrandTime.getEndTime()));
//            int seasonNum = endSeason - startSeason + 1;
//            for (int i = 1; i <= seasonNum; i++){
//                Calendar cd = Calendar.getInstance();
//                cd.setTime(startDate);
//
//                times.add(dateFormat.format(cd.getTime()));
//            }
//        } else {
//
//        }
        return times;
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
