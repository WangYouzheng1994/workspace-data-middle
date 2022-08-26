package org.jeecg.yqwl.datamiddle.ads.order.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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

        String vin = queryCriteria.getVin();
        if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
            queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, ",")));
        } else if (StringUtils.length(vin) > 2 && StringUtils.contains(vin, "\n")) {
            queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, "\n")));
        }

        // 获取一单到底的运输方式
        String trafficType = queryCriteria.getTrafficType();
        if ( StringUtils.isNotBlank(trafficType) ) {
            if (StringUtils.contains(trafficType,"typeG") ) {
                queryCriteria.setTypeG(1);
            }else{
                queryCriteria.setTypeG(0);
            }
            if (StringUtils.contains(trafficType,"typeT") ) {
                queryCriteria.setTypeT(1);
            }else{
                queryCriteria.setTypeT(0);
            }
            if (StringUtils.contains(trafficType,"typeS") ) {
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
//        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);
        Integer pageNo = 1;
        Integer pageSize = 5000;

        boolean intervalFlag = true;
        queryCriteria.setPageSize(pageSize);

        Integer integer = dwmVlmsOneOrderToEndService.countOneOrderToEndList(queryCriteria);
        if (integer > 150000) {
            this.responseJsonString(response, JSONObject.toJSONString(Result.error("超出导出数量限制！")));
            return;
        }

        // 创建工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        // 设置字体和样式
        Font font = wb.createFont();
        // 字体名称
        font.setFontName("宋体");
        // 字体大小
        font.setFontHeightInPoints((short)12);
        // 设置字体加粗
//        font.setBold(true);
        // 设置格式居中显示
        /*CellStyle cellstyle = wb.createCellStyle();
        cellstyle.setAlignment(HorizontalAlignment.CENTER);*/
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 设置表头行高
        row.setHeight((short) (22.50 * 20));
        // 获取表头(前端页面一共有44个字段,entity一共是57个字段)
        // 最新表头  2022.7.12 更改 顺序更改和添加经销商名称  Y号  删除配板日期,将整车物流接收STD日期 改为 计划下达日期
        String[] headers = new String[]{"底盘号", "品牌", "基地", "车型", "始发城市","经销商目标城市","经销商代码","经销商名称",
                "CP9下线接车日期", "入库日期", "入库仓库", "任务单号", "计划下达日期", "配板单号", "运输方式", "指派日期",
                "指派承运商名称", "出库日期", "起运日期-公路", "运输车号", "同板数量", "轿运车车位数",
                "始发站名称","到达始发站时间/入站时间","始发站台铁路离站时间","目的站名称","到达目的站时间","卸车时间（铁路到目的站）",
                "始发港名称", "到达始发港口时间/入港时间", "始发港口水运离港时间", "目的港名称", "到达目的港时间","卸船时间（水路到目的站）",
                "末端分拨中心入库时间", "末端分拨中心指派时间", "末端分拨承运商", "分拨承运轿运车车牌号",
                "港/站分拨承运轿运车车位数", "分拨出库时间", "分拨起运时间", "送达时间-DCS到货时间", "经销商确认到货时间","位置信息","是否同城异地"};
        int i = 0;
        // 循环遍历表头,作为sheet页的第一行数据
        for (String header : headers) {
            // 获取表头列
            SXSSFCell cell = row.createCell(i++);
            // 为单元格赋值
            cell.setCellValue(header);
            /*// 设置表头居中显示
            cell.setCellStyle(cellstyle);
            // 设置表头字体
            cellstyle.setFont(font);*/
        }

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
                SXSSFCell cell = row1.createCell(j++);
                // cell.setCellStyle(cellstyle);
                cell.setCellValue(item.getVin());
                // brand 品牌
                SXSSFCell cell1 = row1.createCell(j++);
                // cell1.setCellStyle(cellstyle);
                cell1.setCellValue(item.getBrandName());
                // baseName 基地
                SXSSFCell cell2 = row1.createCell(j++);
                // cell2.setCellStyle(cellstyle);
                cell2.setCellValue(item.getBaseName());
                // vehicleName 车型
                SXSSFCell cell3 = row1.createCell(j++);
                // cell3.setCellStyle(cellstyle);
                cell3.setCellValue(item.getVehicleName());
                // startCityName 始发城市
                SXSSFCell cell4 = row1.createCell(j++);
                // cell4.setCellStyle(cellstyle);
                cell4.setCellValue(item.getStartCityName());
                // endCityName 经销商目标城市
                SXSSFCell cell5 = row1.createCell(j++);
                // cell5.setCellStyle(cellstyle);
                cell5.setCellValue(item.getEndCityName());
                // vdwdm 经销商代码
                SXSSFCell cell6 = row1.createCell(j++);
                // cell6.setCellStyle(cellstyle);
                cell6.setCellValue(item.getVdwdm());
                // DEALER_NAME  经销商名称
                SXSSFCell cell7 = row1.createCell(j++);
                // cell7.setCellStyle(cellstyle);
                cell7.setCellValue(item.getDealerName());
                // cp9OfflineTime  CP9下线接车日期  11
                if (item.getCp9OfflineTime() != 0) {
                    SXSSFCell cell8 = row1.createCell(j++);
                    // cell8.setCellStyle(cellstyle);
                    cell8.setCellValue(sdf.format(item.getCp9OfflineTime()));
                }else{
                    SXSSFCell cell8 = row1.createCell(j++);
                    // cell8.setCellStyle(cellstyle);
                    cell8.setCellValue("");
                }
                // inSiteTime  入库日期   16WEI
                if (item.getInSiteTime() != 0) {
                    SXSSFCell cell9 = row1.createCell(j++);
                    // cell9.setCellStyle(cellstyle);
                    cell9.setCellValue(sdf.format(item.getInSiteTime()));
                }else{
                    SXSSFCell cell9 = row1.createCell(j++);
                    // cell9.setCellStyle(cellstyle);
                    cell9.setCellValue("");
                }
                // inWarehouseName  入库仓库
                SXSSFCell cell10 = row1.createCell(j++);
                // cell10.setCellStyle(cellstyle);
                cell10.setCellValue(item.getInWarehouseName());
                // taskNo 任务单号
                SXSSFCell cell11 = row1.createCell(j++);
                // cell11.setCellStyle(cellstyle);
                cell11.setCellValue(item.getTaskNo());
                // vehiclePlateIssuedTimeR3  计划下达日期  修改为R3.DDJRQ的时间
                if (item.getVehiclePlateIssuedTimeR3() != 0) {
                    SXSSFCell cell12 = row1.createCell(j++);
                    // cell12.setCellStyle(cellstyle);
                    cell12.setCellValue(sdf.format(item.getVehiclePlateIssuedTimeR3()));
                }else{
                    SXSSFCell cell12 = row1.createCell(j++);
                    // cell12.setCellStyle(cellstyle);
                    cell12.setCellValue("");
                }
                // Cpzdbh 配板单号
                SXSSFCell cell13 = row1.createCell(j++);
                // cell13.setCellStyle(cellstyle);
                cell13.setCellValue(item.getCpzdbh());
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
                SXSSFCell cell14 = row1.createCell(j++);
                // cell14.setCellStyle(cellstyle);
                cell14.setCellValue(item.setTrafficType(trafficType));
                // assignTime  指派日期  11
                if (item.getAssignTime() != 0) {
                    SXSSFCell cell15 = row1.createCell(j++);
                    // cell15.setCellStyle(cellstyle);
                    cell15.setCellValue(sdf.format(item.getAssignTime()));
                }else{
                    SXSSFCell cell15 = row1.createCell(j++);
                    // cell15.setCellStyle(cellstyle);
                    cell15.setCellValue("");
                }
                // carrierName  指派承运商名称
                SXSSFCell cell16 = row1.createCell(j++);
                // cell16.setCellStyle(cellstyle);
                cell16.setCellValue(item.getCarrierName());
                // actualOutTime  出库日期  11
                if (item.getActualOutTime() != 0) {
                    SXSSFCell cell17 = row1.createCell(j++);
                    // cell17.setCellStyle(cellstyle);
                    cell17.setCellValue(sdf.format(item.getActualOutTime()));
                }else{
                    SXSSFCell cell17 = row1.createCell(j++);
                    // cell17.setCellStyle(cellstyle);
                    cell17.setCellValue("");
                }
                // shipmentTime  起运日期-公路   11
                if (item.getShipmentGTime() != 0) {
                    SXSSFCell cell18 = row1.createCell(j++);
                    // cell18.setCellStyle(cellstyle);
                    cell18.setCellValue(sdf.format(item.getShipmentGTime()));
                }else{
                    SXSSFCell cell18 = row1.createCell(j++);
                    // cell18.setCellStyle(cellstyle);
                    cell18.setCellValue("");
                }
                // transportVehicleNo  运输车号
                SXSSFCell cell19 = row1.createCell(j++);
                // cell19.setCellStyle(cellstyle);
                cell19.setCellValue(item.getTransportVehicleNo());
                // samePlateNum  同板数量
                SXSSFCell cell20 = row1.createCell(j++);
                // cell20.setCellStyle(cellstyle);
                cell20.setCellValue(item.getSamePlateNum());
                // vehicleNum  轿运车车位数
                row1.createCell(j++).setCellValue(item.getVehicleNum());
                // startPlatformName  始发站名称
                SXSSFCell cell21 = row1.createCell(j++);
                // cell21.setCellStyle(cellstyle);
                cell21.setCellValue(item.getStartPlatformName());
                // inStartPlatformTime  到达始发站时间/入站时间   16WEI
                if (item.getInStartPlatformTime() != 0) {
                    SXSSFCell cell22 = row1.createCell(j++);
                    // cell22.setCellStyle(cellstyle);
                    cell22.setCellValue(sdf.format(item.getInStartPlatformTime()));
                }else{
                    SXSSFCell cell22 = row1.createCell(j++);
                    // cell22.setCellStyle(cellstyle);
                    cell22.setCellValue("");
                }
                // outStartPlatformTime  始发站台铁路离站时间    16WEI
                if (item.getOutStartPlatformTime() != 0) {
                    SXSSFCell cell23 = row1.createCell(j++);
                    // cell23.setCellStyle(cellstyle);
                    cell23.setCellValue(sdf.format(item.getOutStartPlatformTime()));
                }else{
                    SXSSFCell cell23 = row1.createCell(j++);
                    // cell23.setCellStyle(cellstyle);
                    cell23.setCellValue("");
                }
                // endPlatformName  目的站名称
                SXSSFCell cell24 = row1.createCell(j++);
                // cell24.setCellStyle(cellstyle);
                cell24.setCellValue(item.getEndPlatformName());
                // inEndPlatformTime  到达目的站时间     16WEI
                if (item.getInEndPlatformTime() != 0) {
                    SXSSFCell cell25 = row1.createCell(j++);
                    // cell25.setCellStyle(cellstyle);
                    cell25.setCellValue(sdf.format(item.getInEndPlatformTime()));
                }else{
                    SXSSFCell cell25 = row1.createCell(j++);
                    // cell25.setCellStyle(cellstyle);
                    cell25.setCellValue("");
                }
                // unloadRailwayTime  卸车时间（铁路到目的站）   16WEI
                if (item.getUnloadRailwayTime() != 0) {
                    SXSSFCell cell26 = row1.createCell(j++);
                    // cell26.setCellStyle(cellstyle);
                    cell26.setCellValue(sdf.format(item.getUnloadRailwayTime()));
                }else{
                    SXSSFCell cell26 = row1.createCell(j++);
                    // cell26.setCellStyle(cellstyle);
                    cell26.setCellValue("");
                }
                // startWaterwayName  始发港名称
                SXSSFCell cell27 = row1.createCell(j++);
                // cell27.setCellStyle(cellstyle);
                cell27.setCellValue(item.getStartWaterwayName());
                // inStartWaterwayTime 到达始发港口时间/入港时间    16WEI
                if (item.getInStartWaterwayTime() != 0) {
                    SXSSFCell cell28 = row1.createCell(j++);
                    // cell28.setCellStyle(cellstyle);
                    cell28.setCellValue(sdf.format(item.getInStartWaterwayTime()));
                }else{
                    SXSSFCell cell28 = row1.createCell(j++);
                    // cell28.setCellStyle(cellstyle);
                    cell28.setCellValue("");
                }
                // endStartWaterwayTime 始发港口水运离港时间  16WEI
                if (item.getEndStartWaterwayTime() != 0) {
                    SXSSFCell cell29 = row1.createCell(j++);
                    // cell29.setCellStyle(cellstyle);
                    cell29.setCellValue(sdf.format(item.getEndStartWaterwayTime()));
                }else{
                    SXSSFCell cell29 = row1.createCell(j++);
                    // cell29.setCellStyle(cellstyle);
                    cell29.setCellValue("");
                }
                // endWaterwayName 目的港名称
                SXSSFCell cell30 = row1.createCell(j++);
                // cell30.setCellStyle(cellstyle);
                cell30.setCellValue(item.getEndWaterwayName());
                // inEndWaterwayTime  到达目的港时间    16WEI
                if (item.getInEndWaterwayTime() != 0) {
                    SXSSFCell cell31 = row1.createCell(j++);
                    // cell31.setCellStyle(cellstyle);
                    cell31.setCellValue(sdf.format(item.getInEndWaterwayTime()));
                }else{
                    SXSSFCell cell31 = row1.createCell(j++);
                    // cell31.setCellStyle(cellstyle);
                    cell31.setCellValue("");
                }
                // unloadShipTime  卸船时间（水路到目的站）   16WEI
                if (item.getUnloadShipTime() != 0) {
                    SXSSFCell cell32 = row1.createCell(j++);
                    // cell32.setCellStyle(cellstyle);
                    cell32.setCellValue(sdf.format(item.getUnloadShipTime()));
                }else{
                    SXSSFCell cell32 = row1.createCell(j++);
                    // cell32.setCellStyle(cellstyle);
                    cell32.setCellValue("");
                }
                // inDistributeTime  末端分拨中心入库时间   16WEI
                if (item.getInDistributeTime() != 0) {
                    SXSSFCell cell33 = row1.createCell(j++);
                    // cell33.setCellStyle(cellstyle);
                    cell33.setCellValue(sdf.format(item.getInDistributeTime()));
                }else{
                    SXSSFCell cell33 = row1.createCell(j++);
                    // cell33.setCellStyle(cellstyle);
                    cell33.setCellValue("");
                }
                // distributeAssignTime 末端分拨中心指派时间   11
                if (item.getDistributeAssignTime() != 0) {
                    SXSSFCell cell34 = row1.createCell(j++);
                    // cell34.setCellStyle(cellstyle);
                    cell34.setCellValue(sdf.format(item.getDistributeAssignTime()));
                }else{
                    SXSSFCell cell34 = row1.createCell(j++);
                    // cell34.setCellStyle(cellstyle);
                    cell34.setCellValue("");
                }
                // distributeCarrierName   末端分拨承运商
                SXSSFCell cell35 = row1.createCell(j++);
                // cell35.setCellStyle(cellstyle);
                cell35.setCellValue(item.getDistributeCarrierName());
                // distributeVehicleNo  分拨承运轿运车车牌号
                SXSSFCell cell36 = row1.createCell(j++);
                // cell36.setCellStyle(cellstyle);
                cell36.setCellValue(item.getDistributeVehicleNo());
                // distributeVehicleNum  港/站分拨承运轿运车车位数
                SXSSFCell cell37 = row1.createCell(j++);
                // cell37.setCellStyle(cellstyle);
                cell37.setCellValue(item.getDistributeVehicleNum());
                // outDistributeTime  分拨出库时间   11
                if (item.getOutDistributeTime() != 0) {
                    SXSSFCell cell38 = row1.createCell(j++);
                    // cell38.setCellStyle(cellstyle);
                    cell38.setCellValue(sdf.format(item.getOutDistributeTime()));
                }else{
                    SXSSFCell cell38 = row1.createCell(j++);
                    // cell38.setCellStyle(cellstyle);
                    cell38.setCellValue("");
                }
                // distributeShipmentTime  分拨起运时间  11
                if (item.getDistributeShipmentTime() != 0) {
                    SXSSFCell cell39 = row1.createCell(j++);
                    // cell39.setCellStyle(cellstyle);
                    cell39.setCellValue(sdf.format(item.getDistributeShipmentTime()));
                }else{
                    SXSSFCell cell39 = row1.createCell(j++);
                    // cell39.setCellStyle(cellstyle);
                    cell39.setCellValue("");
                }
                // dtvsdhsj  送达时间-DCS到货时间  11
                if (item.getDtvsdhsj() != 0) {
                    SXSSFCell cell40 = row1.createCell(j++);
                    // cell40.setCellStyle(cellstyle);
                    cell40.setCellValue(sdf.format(item.getDtvsdhsj()));
                }else{
                    SXSSFCell cell40 = row1.createCell(j++);
                    // cell40.setCellStyle(cellstyle);
                    cell40.setCellValue("");
                }
                // finalSiteTime  经销商确认到货时间  11
                if (item.getFinalSiteTime() != 0) {
                    SXSSFCell cell41 = row1.createCell(j++);
                    // cell41.setCellStyle(cellstyle);
                    cell41.setCellValue(sdf.format(item.getFinalSiteTime()));
                }else{
                    SXSSFCell cell41 = row1.createCell(j++);
                    // cell41.setCellStyle(cellstyle);
                    cell41.setCellValue("");
                }
                // vwz 位置信息
                SXSSFCell cell42 = row1.createCell(j++);
                // cell42.setCellStyle(cellstyle);
                cell42.setCellValue(item.getVwz());
                // TYPE_TC 是否同城异地
                String typeTc = item.getTypeTc().toString();
                if ( "0".equals(typeTc) ) {
                    typeTc = "无";
                }else if ( "1".equals(typeTc) ) {
                    typeTc = "同城";
                }else if ( "2".equals(typeTc) ) {
                    typeTc = "异地";
                }
                SXSSFCell cell43 = row1.createCell(j++);
                // cell43.setCellStyle(cellstyle);
                cell43.setCellValue(item.setTypeTc(typeTc));

                // 设置数据的行高
                // row1.setHeight((short) (16.5 * 20));
                // 设置自适应列宽
                /*for ( int m = 0; m < headers.length; m++ ) {
                    // 跟踪列以调节大小
                    // sheet.trackAllColumnsForAutoSizing();
                    // 调整宽度以适应内容
                    // sheet.autoSizeColumn(m);
                    // 设置宽度的取值
                    // int width = Math.max(15 * 256, Math.min(256 * 256, sheet.getColumnWidth(m) * 12 / 10));
                    // sheet.setColumnWidth(m, width);
                }*/
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

//        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);

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