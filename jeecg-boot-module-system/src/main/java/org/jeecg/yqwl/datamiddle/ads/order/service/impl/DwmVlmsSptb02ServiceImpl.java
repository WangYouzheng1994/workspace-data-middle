package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.handler.IFillRuleHandler;
import org.jeecg.yqwl.datamiddle.ads.order.content.TimeGranularity;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentHaveTimestamp;
import org.jeecg.yqwl.datamiddle.ads.order.util.DateUtils;
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
        //查出该时间段内已经起运，没有实际到货的数据,数据量过大，进行分页处理
        List<DwmSptb02> dwmVlmsSptb02s = getOnwayDatas(baseBrandTime);
        //创建时间段和计量单位（年月日分组）的数组
        List<ShipmentHaveTimestamp> allTime = createAllTime(baseBrandTime);
        //构造ShipmentDTO
        List<ShipmentDTO> shipmentDTOS = new ArrayList<>();
        List<ShipmentHaveTimestamp> shipmentHaveTimestamps = new ArrayList<>();
        allTime.forEach(item -> {
            //过滤符合条件的数据
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
            }).collect(Collectors.groupingBy(i -> {
                //分组条件
                if (!"".equals(baseBrandTime.getCqwh()) && "".equals(baseBrandTime.getCzjgsdm())){
                    return i.getCustomerName();
                }else {
                    return i.getBaseName();
                }
            }));


            dwmSptb02Map.forEach( (key,value) -> {
                //填充DTO
                ShipmentDTO shipmentDTO = new ShipmentDTO();
                shipmentDTO.setTotalNum(value.size());
                shipmentDTO.setGroupName(key);
                shipmentDTO.setDates(item.getDates());
                shipmentDTOS.add(shipmentDTO);
                //填充item
                if (!TimeGranularity.DAY.equals(baseBrandTime.getTimeType())) {
                    ShipmentHaveTimestamp shipmentHaveTimestamp = new ShipmentHaveTimestamp();
                    shipmentHaveTimestamp.setDates(item.getDates());
                    shipmentHaveTimestamp.setDateTimestamp(item.getDateTimestamp());
                    shipmentHaveTimestamp.setEndTime(item.getEndTime());
                    shipmentHaveTimestamp.setGroupName(key);
                    shipmentHaveTimestamp.setTotalNum(value.size());
                    shipmentHaveTimestamps.add(shipmentHaveTimestamp);
                }

            });
        });
        ShipmentVO resultVO = null;
        //其他时间粒度统计
        if (!TimeGranularity.DAY.equals(baseBrandTime.getTimeType())) {
            resultVO = FormatDataUtil.formatDataList(buildShipmentDtoByGrain(shipmentHaveTimestamps,baseBrandTime),baseBrandTime);
        }else {
            resultVO = FormatDataUtil.formatDataList(shipmentDTOS,baseBrandTime);
        }
        return Result.OK(resultVO);
    }

    private List<DwmSptb02> getOnwayDatas(GetBaseBrandTime baseBrandTime) {
        //分页去查
        Integer total = dwmVlmsSptb02Mapper.getOnwayDatasCount(baseBrandTime);
        List<DwmSptb02> dwmSptb02s = new ArrayList<>();
        boolean flag = true;
        int pageNo = 1;
        int pageSize = 5000;
        //计算总共多少页
        int pageNoTotal = BigDecimal.valueOf(total).divide(BigDecimal.valueOf(pageSize), 0, BigDecimal.ROUND_UP).intValue();
        //一千条查一次
        baseBrandTime.setPageSize(pageSize);
        while (flag){
            baseBrandTime.setPageNo(pageNo);
            baseBrandTime.setLimitStart((baseBrandTime.getPageNo() - 1) * baseBrandTime.getPageSize());
            baseBrandTime.setLimitEnd(baseBrandTime.getPageSize());
            List<DwmSptb02> onwayDatas = dwmVlmsSptb02Mapper.getOnwayDatas(baseBrandTime);
            dwmSptb02s.addAll(onwayDatas);
            pageNo ++;
            if (pageNo > pageNoTotal){
                flag = false;
            }
        }
        return dwmSptb02s;
    }

    private List<ShipmentDTO> buildShipmentDtoByGrain(List<ShipmentHaveTimestamp> allTime, GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipmentDTOS = new ArrayList<>();
        if (TimeGranularity.WEEK.equals(baseBrandTime.getTimeType())){
            //处理周
            allTime.forEach(item -> {
                item.setDates(item.getDates().substring(0, 5) + DateUtils.getWeekOfYear(new Date(item.getDateTimestamp())));
            });
        } else if (TimeGranularity.MONTH.equals(baseBrandTime.getTimeType())) {
            //处理月
            allTime.forEach(item -> {
                item.setDates(item.getDates().substring(0, 5) + DateUtils.getMonth(new Date(item.getDateTimestamp())));
            });
        } else if (TimeGranularity.QUARTER.equals(baseBrandTime.getTimeType())) {
            //处理季度
            allTime.forEach(item -> {
                item.setDates(item.getDates().substring(0, 5) + DateUtils.getSeason(new Date(item.getDateTimestamp())));
            });
        } else if (TimeGranularity.YEAR.equals(baseBrandTime.getTimeType())) {
            //处理年
            allTime.forEach(item -> {
                item.setDates(item.getDates().substring(0, 4));
            });
        }
        Map<String, List<ShipmentHaveTimestamp>> listMap = allTime.stream().collect(Collectors.groupingBy(item -> item.getDates() + "," + item.getGroupName()));
        listMap.forEach((key, value) -> {
            int sum = value.stream().mapToInt(ShipmentDTO::getTotalNum).sum();
            String[] splitKey = key.split(",");
            ShipmentDTO shipmentDTO = new ShipmentDTO();
            shipmentDTO.setDates(splitKey[0]);
            shipmentDTO.setGroupName(splitKey[1]);
            shipmentDTO.setTotalNum(sum);
            shipmentDTOS.add(shipmentDTO);
        });
        return shipmentDTOS;
    }

    /**
     * 生成所有天
     * @param baseBrandTime 查询条件
     * @author dabao
     * @date 2022/9/16
     * @return {@link List<ShipmentHaveTimestamp>}
     */
    private List<ShipmentHaveTimestamp> createAllTime(GetBaseBrandTime baseBrandTime) {
        List<ShipmentHaveTimestamp> times = new ArrayList<>();
        //按天计算
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
