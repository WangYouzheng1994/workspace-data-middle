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
import org.jeecg.yqwl.datamiddle.ads.order.vo.DwmSptb02VO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.TimelinessRatioVO;
import org.jeecg.yqwl.datamiddle.util.FormatDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        Result<ShipmentVO> dayAmountOfPlan = dwmVlmsSptb02Service.findDayAmountOfPlan(baseBrandTime);
        //  参数校验,去除key的空值
        Result<ShipmentVO> shipmentVOResult = FormatDataUtil.formatRemoveEmptyValue(dayAmountOfPlan);
        return shipmentVOResult ;
    }
    /**
     * 按条件查询到货量
     * @param baseBrandTime
     * @return
     */
    @PostMapping("/selectEndNum")
    public Result<?> queryEndNum (@RequestBody GetBaseBrandTime baseBrandTime) {
        Result<ShipmentVO> shipment = dwmVlmsSptb02Service.getFINAL_SITE_TIME(baseBrandTime);
        //  参数校验,去除key的空值
        Result<ShipmentVO> shipmentVOResult = FormatDataUtil.formatRemoveEmptyValue(shipment);
        return shipmentVOResult ;
    }

    /**
     * 按条件查询发运量
     * @param baseBrandTime
     * @return
     */
    @PostMapping("/selectShipment")
    public Result<?> queryShipment (@RequestBody GetBaseBrandTime baseBrandTime) {
        Result<ShipmentVO> shipment = dwmVlmsSptb02Service.findShipment(baseBrandTime);
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
        // TODO: 指派
        // TODO: 出库

        // TODO: 起运
        // 到货
        // dwmVlmsSptb02Service.findArrivalRate(baseBrandTime);
        // TODO: 合并出返回对象。
        TimelinessRatioVO timelinessRatioVO = new TimelinessRatioVO();
        timelinessRatioVO.setAllotPercent(10); //分配及时率
        timelinessRatioVO.setOutWarehousePercent(80);//出库及时率
        timelinessRatioVO.setStartPercent(50);//起运及时率
        timelinessRatioVO.setEndPercent(100);//到货及时率
        return Result.OK(timelinessRatioVO) ;
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
     * 添加
     *
     * @param dwmVlmsSptb02
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-添加")
    @ApiOperation(value = "DwmVlmsSptb02-添加", notes = "DwmVlmsSptb02-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DwmVlmsSptb02 dwmVlmsSptb02) {
        dwmVlmsSptb02Service.save(dwmVlmsSptb02);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param dwmVlmsSptb02
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-编辑")
    @ApiOperation(value = "DwmVlmsSptb02-编辑", notes = "DwmVlmsSptb02-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DwmVlmsSptb02 dwmVlmsSptb02) {
        dwmVlmsSptb02Service.updateById(dwmVlmsSptb02);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-通过id删除")
    @ApiOperation(value = "DwmVlmsSptb02-通过id删除", notes = "DwmVlmsSptb02-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        dwmVlmsSptb02Service.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-批量删除")
    @ApiOperation(value = "DwmVlmsSptb02-批量删除", notes = "DwmVlmsSptb02-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.dwmVlmsSptb02Service.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
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
     * top10发运量
     * @param
     * @return
     */
//    @AutoLog(value = "DwmVlmsSptb02-top10发运量")
//    @ApiOperation(value = "DwmVlmsSptb02-top10发运量", notes = "DwmVlmsSptb02-top10发运量")
//    @PostMapping(value = "/findTop10SendList")
//    public Result<?> findTop10SendList(@RequestBody GetBaseBrandTime baseBrandTime) {
//        Result<ShipmentVO> top10SendList = FormatDataUtil.formatRemoveEmptyValue(dwmVlmsSptb02Service.findTop10SendList(baseBrandTime));
//        return top10SendList;
//    }

    /**
     * top10待发量
     * @param
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-top10待发量")
    @ApiOperation(value = "DwmVlmsSptb02-top10发运量", notes = "DwmVlmsSptb02-top10待发量")
    @PostMapping(value = "/findTop10PendingList" )
    public Result<?> findTop10PendingList(@RequestBody GetBaseBrandTime baseBrandTime) {
        Result<ShipmentVO> top10PendingList = FormatDataUtil.formatRemoveEmptyValue(dwmVlmsSptb02Service.findTop10PendingList(baseBrandTime));
        return top10PendingList;
    }



    /**
     * top10在途量
     * @param
     * @return
     */
    @AutoLog(value = "DwmVlmsSptb02-top10在途量")
    @ApiOperation(value = "DwmVlmsSptb02-top10在途量", notes = "DwmVlmsSptb02-top10在途量")
    @PostMapping(value = "/findTop10OnWayList" )
    public Result<?> findTop10OnWayList(@RequestBody GetBaseBrandTime baseBrandTime) {
        Result<ShipmentVO> top10OnWayList = FormatDataUtil.formatRemoveEmptyValue(dwmVlmsSptb02Service.findTop10OnWayList(baseBrandTime));
        return top10OnWayList;
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
        Result<ShipmentVO> top10StockOutList = FormatDataUtil.formatRemoveEmptyValue(dwmVlmsSptb02Service.findTop10StockOutList(baseBrandTime));
        return top10StockOutList;
    }

    /**
     * 插入接口
     * @param dwmSptb02VO
     * @return
     */
    @PostMapping(value = "/insertCLickhouse123")
    public Result<?> insertClickhouse123(@RequestBody DwmSptb02VO dwmSptb02VO) {
//        dwmVlmsSptb02Service.insertClickhouse(dwmSptb02VO);
        return Result.OK("插入成功");
    }
}
