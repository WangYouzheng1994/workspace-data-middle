package org.jeecg.yqwl.datamiddle.ads.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.*;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.util.FormatDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date: 2022-05-12
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "DwmVlmsSptb02")
@RestController
@RequestMapping("/ads/order/dwmVlmsSptb02")
public class DwmVlmsSptb02Controller extends JeecgController<DwmVlmsSptb02, IDwmVlmsSptb02Service> {
    @Autowired
    private IDwmVlmsSptb02Service dwmVlmsSptb02Service;

    /**
     * 按条件查询计划量
     * @param baseBrandTime
     * @return
     */
    @PostMapping("/selectAmountOfPlan")
    public Result<?> queryDayAmountOfPlan (@RequestBody GetBaseBrandTime baseBrandTime) {
        checkStartTime(baseBrandTime);
        log.info("查询了计划量");
        Result<ShipmentVO> dayAmountOfPlan = dwmVlmsSptb02Service.findDayAmountOfPlan(baseBrandTime);
        //  参数校验,去除key的空值
        Result<ShipmentVO> shipmentVOResult = FormatDataUtil.formatRemoveEmptyValue(dayAmountOfPlan);
        return shipmentVOResult ;
    }

    /**
     * 出库量
     * @param
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-出库量")
    @ApiOperation(value = "DwmVlmsSptb02-出库量", notes = "DwmVlmsSptb02-出库量")
    @PostMapping(value = "/findTop10StockOutList")
    public Result<?> findTop10StockOutList(@RequestBody GetBaseBrandTime baseBrandTime ) {
        checkStartTime(baseBrandTime);
        log.info("查询了出库量");
        Result<ShipmentVO> top10StockOutList = FormatDataUtil.formatRemoveEmptyValue(dwmVlmsSptb02Service.findTop10StockOutList(baseBrandTime));
        return top10StockOutList;
    }

    /**
     * top10待发量
     * @param
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-top10待发量")
    @ApiOperation(value = "DwmVlmsSptb02-top10发运量", notes = "DwmVlmsSptb02-top10待发量")
    @PostMapping(value = "/findTop10PendingList" )
    @Deprecated
    public Result<?> findTop10PendingList(@RequestBody GetBaseBrandTime baseBrandTime) {
        checkStartTime(baseBrandTime);
        log.info("查询了待发量");
        Result<ShipmentVO> top10PendingList = FormatDataUtil.formatRemoveEmptyValue(dwmVlmsSptb02Service.findTop10PendingList(baseBrandTime));
        return top10PendingList;
    }

    /**
     * 按条件查询发运量/起运量
     * @param baseBrandTime
     * @return
     */
    @PostMapping("/selectShipment")
    public Result<?> queryShipment (@RequestBody GetBaseBrandTime baseBrandTime) {
        checkStartTime(baseBrandTime);
        log.info("查询了起运量");
        Result<ShipmentVO> shipment = dwmVlmsSptb02Service.findShipment(baseBrandTime);
        //  参数校验,去除key的空值
        Result<ShipmentVO> shipmentVOResult = FormatDataUtil.formatRemoveEmptyValue(shipment);
        return shipmentVOResult ;
    }


    /**
     * 按条件查询到货量
     * @param baseBrandTime
     * @return
     */
    @PostMapping("/selectEndNum")
    public Result<?> queryEndNum (@RequestBody GetBaseBrandTime baseBrandTime) {
        checkStartTime(baseBrandTime);
        log.info("查询了到货量");
        Result<ShipmentVO> shipment = dwmVlmsSptb02Service.getFINAL_SITE_TIME(baseBrandTime);
        //  参数校验,去除key的空值
        Result<ShipmentVO> shipmentVOResult = FormatDataUtil.formatRemoveEmptyValue(shipment);
        return shipmentVOResult ;
    }



    /**
     * 到货率查询
     * @param baseBrandTime
     * @return
     */
    @PostMapping("/selectArrivalRate")
    public Result<?> queryArrivalRate (@RequestBody GetBaseBrandTime baseBrandTime) {
        checkStartTime(baseBrandTime);
        BigDecimal num = new BigDecimal("100");
        // 起运及时率
        //获取起运样本总量
        BigDecimal totalShipment = dwmVlmsSptb02Service.getTotalShipment(baseBrandTime);
        //获取起运准时样本数量
        BigDecimal timelyShipment = dwmVlmsSptb02Service.getTimelyShipment(baseBrandTime);
        Integer shipmentValue;
        //判断分母是否为0,若为0,返回0,否则返回计算
        if ( totalShipment.equals(BigDecimal.ZERO)) {
            shipmentValue = 0;
        }else{
            //起运准时样本数量/起运样本总数 * 100  转换成Integer
            shipmentValue = timelyShipment.divide(totalShipment, 2, BigDecimal.ROUND_HALF_UP).multiply(num).intValue();
        }

        //出库及时率
        //获取出库准时样本数量
        BigDecimal onTimeDelivery = dwmVlmsSptb02Service.getOnTimeDelivery(baseBrandTime);
        //获取出库样本总量
        BigDecimal totalOutboundQuantity = dwmVlmsSptb02Service.getTotalOutboundQuantity(baseBrandTime);
        Integer Outbound;
        //判断是否为0,若为0,返回0,否则返回计算
        if ( totalOutboundQuantity.equals(BigDecimal.ZERO) ) {
            Outbound = 0;
        }else{
            Outbound = onTimeDelivery.divide(totalOutboundQuantity, 2, BigDecimal.ROUND_HALF_UP).multiply(num).intValue();
        }


        //  到货准时率计算
        // 到货样本总量  1733
        BigDecimal arrivalRate = dwmVlmsSptb02Service.findArrivalRate(baseBrandTime);
        // 到货准时样本数量   52
        BigDecimal arriveOnTime = dwmVlmsSptb02Service.getArriveOnTime(baseBrandTime);
        Integer arrivalValue;
        //判断是否为0,若为0,返回0,否则返回计算
        if ( arrivalRate.equals(BigDecimal.ZERO) ) {
            arrivalValue = 0;
        }else{
            arrivalValue = arriveOnTime.divide(arrivalRate, 2, BigDecimal.ROUND_HALF_UP).multiply(num).intValue();
        }

        //合并出返回对象。
       TimelinessRatioVO timelinessRatioVO = new TimelinessRatioVO();
        //起运及时率
        timelinessRatioVO.setStartPercent(shipmentValue);
        //出库及时率
        timelinessRatioVO.setOutWarehousePercent(Outbound);
        //到货及时率
        timelinessRatioVO.setEndPercent(arrivalValue);
        return Result.OK(timelinessRatioVO);
    }



    /**
     * 分页列表查询
     *
     * @param dwmVlmsSptb02
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-分页列表查询")
    @ApiOperation(value = "DwmVlmsSptb02-分页列表查询", notes = "DwmVlmsSptb02-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(DwmVlmsSptb02 dwmVlmsSptb02,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<DwmVlmsSptb02> queryWrapper = QueryGenerator.initQueryWrapper(dwmVlmsSptb02, req.getParameterMap());
        Page<DwmVlmsSptb02> page = new Page<DwmVlmsSptb02>(pageNo, pageSize);
        IPage<DwmVlmsSptb02> pageList = dwmVlmsSptb02Service.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-通过id查询")
    @ApiOperation(value = "DwmVlmsSptb02-通过id查询", notes = "DwmVlmsSptb02-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        DwmVlmsSptb02 dwmVlmsSptb02 = dwmVlmsSptb02Service.getById(id);
        return Result.OK(dwmVlmsSptb02);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param dwmVlmsSptb02
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DwmVlmsSptb02 dwmVlmsSptb02) {
        return super.exportXls(request, dwmVlmsSptb02, DwmVlmsSptb02.class, "DwmVlmsSptb02");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DwmVlmsSptb02.class);
    }

    /**
     * 校验开始时间有没有传
     * @param param 参数
     * @author dabao
     * @date 2022/10/9
     */
    private void checkStartTime(GetBaseBrandTime param){
        if (Objects.isNull(param.getStartTime())){
            throw new SecurityException("请选择开始时间");
        }
    }

    /**
     * 获取 今日指标数据
     * @param query 查询条件
     * @author dabao
     * @date 2022/10/9
     * @return {@link Result<TodayIndicatorsVo>}
     */
    @PostMapping("/getTodayIndicators")
    public Result<TodayIndicatorsVo> getTodayIndicators(@RequestBody GetBaseBrandTime query){
        TodayIndicatorsVo todayIndicators = dwmVlmsSptb02Service.getTodayIndicators(query);
        return Result.OK(todayIndicators);
    }



}
