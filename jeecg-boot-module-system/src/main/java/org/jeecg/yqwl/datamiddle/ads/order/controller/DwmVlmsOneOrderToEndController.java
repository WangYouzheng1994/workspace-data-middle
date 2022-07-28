package org.jeecg.yqwl.datamiddle.ads.order.controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.netty.util.internal.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 大众一单到底全节点查询
 * @Author: jeecg-boot
 * @Date: 2022-06-06
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "一单到底")
@RestController
@RequestMapping("/ads/order/dwmVlmsOneOrderToEnd")
public class DwmVlmsOneOrderToEndController extends JeecgController<DwmVlmsOneOrderToEnd, IDwmVlmsOneOrderToEndService> {
    @Autowired
    private IDwmVlmsOneOrderToEndService dwmVlmsOneOrderToEndService;

    /**
     * 分页列表查询
     *
     * @param dwmVlmsOneOrderToEnd
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "一单到底-分页列表查询")
    @ApiOperation(value = "一单到底-分页列表查询", notes = "一单到底-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<DwmVlmsOneOrderToEnd> queryWrapper = QueryGenerator.initQueryWrapper(dwmVlmsOneOrderToEnd, req.getParameterMap());
        Page<DwmVlmsOneOrderToEnd> page = new Page<DwmVlmsOneOrderToEnd>(pageNo, pageSize);
        IPage<DwmVlmsOneOrderToEnd> pageList = dwmVlmsOneOrderToEndService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param dwmVlmsOneOrderToEnd
     * @return
     */
    @AutoLog(value = "一单到底-添加")
    @ApiOperation(value = "一单到底-添加", notes = "一单到底-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd) {
        dwmVlmsOneOrderToEndService.save(dwmVlmsOneOrderToEnd);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param dwmVlmsOneOrderToEnd
     * @return
     */
    @AutoLog(value = "一单到底-编辑")
    @ApiOperation(value = "一单到底-编辑", notes = "一单到底-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd) {
        dwmVlmsOneOrderToEndService.updateById(dwmVlmsOneOrderToEnd);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "一单到底-通过id删除")
    @ApiOperation(value = "一单到底-通过id删除", notes = "一单到底-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        dwmVlmsOneOrderToEndService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "一单到底-批量删除")
    @ApiOperation(value = "一单到底-批量删除", notes = "一单到底-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.dwmVlmsOneOrderToEndService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "一单到底-通过id查询")
    @ApiOperation(value = "一单到底-通过id查询", notes = "一单到底-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd = dwmVlmsOneOrderToEndService.getById(id);
        return Result.OK(dwmVlmsOneOrderToEnd);
    }

    /**
     * 导出excel
     */
    @AutoLog(value = "导出")
    @ApiOperation(value = "导出", notes = "导出")
    @PostMapping(value = "/exportXls")
    public void exportXls(@RequestBody GetQueryCriteria queryCriteria, HttpServletResponse response) throws IOException {
        // 创建工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 获取表头(前端页面一共有44个字段,entity一共是57个字段)
//        String[] headers = new String[]{"底盘号", "品牌", "基地", "车型", "CP9下线接车日期", "出厂日期", "入库日期", "入库仓库", "任务单号",
//                "整车物流接收STD日期", "配板日期", "配板单号", "运输方式", "指派日期", "指派承运商名称", "出库日期", "起运日期-公路/铁路",
//                "运输车号", "同板数量", "轿运车车位数", "始发城市", "目的城市", "经销商代码", "始发港名称", "到达始发港口时间/入港时间",
//                "始发港口水运离港时间", "目的港名称", "到达目的港时间", "始发站名称", "到达始发站时间/入站时间", "始发站台铁路离站时间",
//                "目的站名称", "到达目的站时间", "卸船时间（水路到目的站）", "卸车时间（铁路到目的站）", "末端分拨中心入库时间",
//                "末端分拨中心指派时间", "末端分拨承运商", "分拨承运轿运车车牌号", "港/站分拨承运轿运车车位数", "分拨出库时间",
//                "分拨起运时间", "送达时间-DCS到货时间", "经销商确认到货时间"};
        // 最新表头  2022.7.12 更改 顺序更改和添加经销商名称  Y号  删除配板日期,将整车物流接收STD日期 改为 计划下达日期
        String[] headers = new String[]{"底盘号", "品牌", "基地", "车型", "始发城市","经销商目标城市","经销商代码","经销商名称",
                "CP9下线接车日期", "入库日期", "入库仓库", "任务单号", "计划下达日期", "配板单号", "运输方式", "指派日期",
                "指派承运商名称", "出库日期", "起运日期-公路", "运输车号", "同板数量", "轿运车车位数",
                "始发站名称","到达始发站时间/入站时间","始发站台铁路离站时间","目的站名称","到达目的站时间","卸车时间（铁路到目的站）",
                "始发港名称", "到达始发港口时间/入港时间", "始发港口水运离港时间", "目的港名称", "到达目的港时间","卸船时间（水路到目的站）",
                 "末端分拨中心入库时间", "末端分拨中心指派时间", "末端分拨承运商", "分拨承运轿运车车牌号",
                "港/站分拨承运轿运车车位数", "分拨出库时间", "分拨起运时间", "送达时间-DCS到货时间", "经销商确认到货时间","位置信息"};
        int i = 0;
        // 循环遍历表头,作为sheet页的第一行数据
        for (String header : headers) {
            // 获取表头列
            SXSSFCell cell = row.createCell(i++);
            // 为单元格赋值
            cell.setCellValue(header);
        }

        String vin = queryCriteria.getVin();
        if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
            queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, ",")));
        } else if (StringUtils.length(vin) > 2 && StringUtils.contains(vin, "\n")) {
            queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, "\n")));
        }

        // 获取一单到底的运输方式
        String trafficType = queryCriteria.getTrafficType();
        String precise = queryCriteria.getPrecise();
        // 精准查询
        if ( StringUtils.isNotBlank(trafficType) ) {
            if ( StringUtils.contains(trafficType,"typeG") ) {
                queryCriteria.setTypeG(1);
            }else{
                queryCriteria.setTypeG(0);
            }
            if ( StringUtils.contains(trafficType,"typeT") ) {
                queryCriteria.setTypeT(1);
            }else{
                queryCriteria.setTypeT(0);
            }
            if ( StringUtils.contains(trafficType,"typeS") ) {
                queryCriteria.setTypeS(1);
            }else{
                queryCriteria.setTypeS(0);
            }
        }

        // 过滤选中的数据
        String selections = queryCriteria.getSelections();
        if (StringUtil.length(selections) > 2) {
            queryCriteria.setVinList(Arrays.asList(StringUtils.split(selections, ",")));
        }
        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);
        Integer pageNo = 1;
        Integer pageSize = 5000;

        boolean intervalFlag = true;
        queryCriteria.setPageSize(pageSize);

        List<DwmVlmsOneOrderToEnd> pageList = null;

        // 转换时间格式,将Long类型转换成date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置新增数据行,从第一行开始
        int rowNum = 1;

        Integer maxSize = 150000;

        SXSSFRow row1 = null;
        do {
            queryCriteria.setPageNo(pageNo);

            // 获取查询数据
            pageList = dwmVlmsOneOrderToEndService.selectOneOrderToEndList(queryCriteria);
            for (DwmVlmsOneOrderToEnd item : pageList) {
                // 时间字段转换成年月日时分秒类型
                row1 = sheet.createRow(rowNum);
                int j = 0;
                // vin 底盘号
                row1.createCell(j++).setCellValue(item.getVin());
                // brand 品牌
                row1.createCell(j++).setCellValue(DwmVlmsFormatUtil.formatBrandToChinese(item.getBrand()));
                // baseName 基地
                row1.createCell(j++).setCellValue(item.getBaseName());
                // vehicleName 车型
                row1.createCell(j++).setCellValue(item.getVehicleName());
                // startCityName 始发城市
                row1.createCell(j++).setCellValue(item.getStartCityName());
                // endCityName 经销商目标城市
                row1.createCell(j++).setCellValue(item.getEndCityName());
                // vdwdm 经销商代码
                row1.createCell(j++).setCellValue(item.getVdwdm());
                // DEALER_NAME  经销商名称
                row1.createCell(j++).setCellValue(item.getDealerName());
                // cp9OfflineTime  CP9下线接车日期  11
                if (item.getCp9OfflineTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getCp9OfflineTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
//                // leaveFactoryTime  出厂日期      16WEI
//                if (item.getLeaveFactoryTime() != 0) {
//                    row1.createCell(j++).setCellValue(sdf.format(item.getLeaveFactoryTime()));
//                }else{
//                    row1.createCell(j++).setCellValue("");
//                }
                // inSiteTime  入库日期   16WEI
                if (item.getInSiteTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getInSiteTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // inWarehouseName  入库仓库
                row1.createCell(j++).setCellValue(item.getInWarehouseName());
                // taskNo 任务单号
                row1.createCell(j++).setCellValue(item.getTaskNo());
                // vehicleReceivingTime  计划下达日期  11
                if (item.getVehicleReceivingTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getVehicleReceivingTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // Cpzdbh 配板单号
                row1.createCell(j++).setCellValue(item.getCpzdbh());
                // trafficType  运输方式
                // 利用获取的typeT,typeS,typeG 进行拼接 并将值赋值给trafficType
                String typeT = item.getTypeT().toString();
                String typeS = item.getTypeS().toString();
                String typeG = item.getTypeG().toString();
                // 将运输方式转换成公铁水
                if ( "1".equals(typeT) ) {
                    typeT = "铁";
                } else if ( "0".equals(typeT) ) {
                    typeT = "";
                }
                if ( "1".equals(typeS) ) {
                    typeS = "水";
                }else if ( "0".equals(typeS) ) {
                    typeS = "";
                }
                if ( "1".equals(typeG) ) {
                    typeG = "公";
                }else if ( "0".equals(typeG) ) {
                    typeG = "";
                }
                // 判断公铁水的值是否为空并添加到list集合中
                List<String> list = new ArrayList<>();
                if ( StringUtils.isNotBlank(typeT)  ) {
                    list.add(typeT);
                }
                if ( StringUtils.isNotBlank(typeS)  ) {
                    list.add(typeS);
                }
                if ( StringUtils.isNotBlank(typeG)  ) {
                    list.add(typeG);
                }
                // 将公铁水的值拼接起来并赋值给trafficType
                trafficType = StringUtils.join(list,",");
                row1.createCell(j++).setCellValue(item.setTrafficType(trafficType));
                // assignTime  指派日期  11
                if (item.getAssignTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getAssignTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // carrierName  指派承运商名称
                row1.createCell(j++).setCellValue(item.getCarrierName());
                // actualOutTime  出库日期  11
                if (item.getActualOutTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getActualOutTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // shipmentTime  起运日期-公路   11
                if (item.getShipmentGTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getShipmentGTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // transportVehicleNo  运输车号
                row1.createCell(j++).setCellValue(item.getTransportVehicleNo());
                // samePlateNum  同板数量
                row1.createCell(j++).setCellValue(item.getSamePlateNum());
                // vehicleNum  轿运车车位数
                row1.createCell(j++).setCellValue(item.getVehicleNum());
                // startPlatformName  始发站名称
                row1.createCell(j++).setCellValue(item.getStartPlatformName());
                // inStartPlatformTime  到达始发站时间/入站时间   16WEI
                if (item.getInStartPlatformTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getInStartPlatformTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // outStartPlatformTime  始发站台铁路离站时间    16WEI
                if (item.getOutStartPlatformTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getOutStartPlatformTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // endPlatformName  目的站名称
                row1.createCell(j++).setCellValue(item.getEndPlatformName());
                // inEndPlatformTime  到达目的站时间     16WEI
                if (item.getInEndPlatformTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getInEndPlatformTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // unloadRailwayTime  卸车时间（铁路到目的站）   16WEI
                if (item.getUnloadRailwayTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getUnloadRailwayTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // startWaterwayName  始发港名称
                row1.createCell(j++).setCellValue(item.getStartWaterwayName());
                // inStartWaterwayTime 到达始发港口时间/入港时间    16WEI
                if (item.getInStartWaterwayTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getInStartWaterwayTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // endStartWaterwayTime 始发港口水运离港时间  16WEI
                if (item.getEndStartWaterwayTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getEndStartWaterwayTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // endWaterwayName 目的港名称
                row1.createCell(j++).setCellValue(item.getEndWaterwayName());
                // inEndWaterwayTime  到达目的港时间    16WEI
                if (item.getInEndWaterwayTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getInEndWaterwayTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // unloadShipTime  卸船时间（水路到目的站）   16WEI
                if (item.getUnloadShipTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getUnloadShipTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // inDistributeTime  末端分拨中心入库时间   16WEI
                if (item.getInDistributeTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getInDistributeTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // distributeAssignTime 末端分拨中心指派时间   11
                if (item.getDistributeAssignTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDistributeAssignTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // distributeCarrierName   末端分拨承运商
                row1.createCell(j++).setCellValue(item.getDistributeCarrierName());
                // distributeVehicleNo  分拨承运轿运车车牌号
                row1.createCell(j++).setCellValue(item.getDistributeVehicleNo());
                // distributeVehicleNum  港/站分拨承运轿运车车位数
                row1.createCell(j++).setCellValue(item.getDistributeVehicleNum());
                // outDistributeTime  分拨出库时间   11
                if (item.getOutDistributeTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getOutDistributeTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // distributeShipmentTime  分拨起运时间  11
                if (item.getDistributeShipmentTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDistributeShipmentTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // dtvsdhsj  送达时间-DCS到货时间  11
                if (item.getDtvsdhsj() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDtvsdhsj()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // finalSiteTime  经销商确认到货时间  11
                if (item.getFinalSiteTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getFinalSiteTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // vwz 位置信息
                row1.createCell(j++).setCellValue(item.getVwz());
                rowNum++;
            }

            // 如果没有查询到数据  或者 分页查询的数量不符合pageSize 那么终止循环
            if (CollectionUtils.isEmpty(pageList) || CollectionUtils.size(pageList) < pageSize) {
                intervalFlag = false;
            } else {
                pageNo++;
            }

            if (rowNum >= maxSize) {
                intervalFlag = false;
            }
        } while (intervalFlag);

        // 设置内容类型
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream outputStream = response.getOutputStream();
        response.setHeader("Content-disposition", "attachment;filename=trainingRecord.xls");
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
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
        return super.importExcel(request, response, DwmVlmsOneOrderToEnd.class);
    }

    /**
     * 按条件查询一单到底
     *
     * @param queryCriteria
     * @return
     */
    @AutoLog(value = "一单到底-按条件查询")
    @ApiOperation(value = "一单到底-按条件查询", notes = "一单到底-按条件查询")
    @PostMapping("/selectOneOrderToEndList")
    public Result<Page<DwmVlmsOneOrderToEnd>> selectOneOrderToEndList(@RequestBody GetQueryCriteria queryCriteria) {
        // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- START
        String vin = queryCriteria.getVin();
        if (StringUtils.isNotBlank(vin)) {
            if (StringUtils.contains(vin, ",") && StringUtils.contains(vin, "\n")) {
                return Result.error("vin码批量查询，分割模式只可以用英文逗号或者回车换行一种模式，不可混搭，请检查vin码查询条件", null);
            }
            // vin码批量模式： 0 逗号， 1 回车换行
            if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
                queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, ",")));
            } else if (StringUtils.length(vin) > 2 && StringUtils.contains(vin, "\n")) {
                queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, "\n")));
            }
        }

        // 获取一单到底的运输方式
        String trafficType = queryCriteria.getTrafficType();
        String precise = queryCriteria.getPrecise();
        // 精准查询
        if ( StringUtils.isNotBlank(trafficType) ) {
            if ( StringUtils.contains(trafficType,"typeG") ) {
                queryCriteria.setTypeG(1);
            } else {
                queryCriteria.setTypeG(0);
            }
            if ( StringUtils.contains(trafficType,"typeT") ) {
                queryCriteria.setTypeT(1);
            } else {
                queryCriteria.setTypeT(0);
            }
            if ( StringUtils.contains(trafficType,"typeS") ) {
                queryCriteria.setTypeS(1);
            } else {
                queryCriteria.setTypeS(0);
            }
        }

        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);

        // TODO: 临时设置成只看大众，后续改成根据角色查询不同客户品牌。

        // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- END
        Integer total = dwmVlmsOneOrderToEndService.countOneOrderToEndList(queryCriteria);
        Page<DwmVlmsOneOrderToEnd> page = new Page<DwmVlmsOneOrderToEnd>(queryCriteria.getPageNo(), queryCriteria.getPageSize());
        List<DwmVlmsOneOrderToEnd> pageList = dwmVlmsOneOrderToEndService.selectOneOrderToEndList(queryCriteria);
        page.setRecords(pageList);
        page.setTotal(total);
        return Result.OK(page);
    }
}