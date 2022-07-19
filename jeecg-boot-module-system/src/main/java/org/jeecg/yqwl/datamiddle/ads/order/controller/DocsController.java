package org.jeecg.yqwl.datamiddle.ads.order.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Api(tags = "一单到底")
@RestController
@RequestMapping("/ads/order/dwmVlmsOneOrderToEnd")
public class DocsController extends JeecgController<DwmVlmsOneOrderToEnd, IDwmVlmsOneOrderToEndService> {

    @Autowired
    private IDwmVlmsOneOrderToEndService dwmVlmsOneOrderToEndService;

    /**
     *docs页面导出
     * @param queryCriteria
     * @param response
     * @throws IOException
     */
    @AutoLog(value = "docs")
    @ApiOperation(value = "docs", notes = "docs")
    @PostMapping(value = "/exportXlsList")
    public void exportXlsList(@RequestBody GetQueryCriteria queryCriteria, HttpServletResponse response) throws IOException {
        // 创建工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 获取表头
        String[] headers = new String[]{"底盘号", "品牌", "基地", "车型", "始发城市", "经销商目标城市", "经销商代码", "经销商名称",
                "计划下达日期", "配板单号", "指派日期", "指派承运商名称", "出库日期", "起运日期", "运输车号", "同板数量", "DCS到货时间",
                "经销商确认到货时间"};
        int i = 0;
        // 循环遍历表头,作为sheet页的第一行数据
        for (String header : headers) {
            // 获取表头列
            SXSSFCell cell = row.createCell(i++);
            // 为单元格赋值
            cell.setCellValue(header);
        }

        String vvin = queryCriteria.getVvin();
        // vin码批量模式： 0 逗号， 1 回车换行
        if (StringUtil.length(vvin) > 2 && StringUtils.contains(vvin, ",")) {
            queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, ",")));
        } else if (StringUtils.length(vvin) > 2 && StringUtils.contains(vvin, "\n")) {
            queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, "\n")));
        }


        // 过滤选中的数据
        String selections = queryCriteria.getSelections();
        if (StringUtil.length(selections) > 2) {
            queryCriteria.setVvinList(Arrays.asList(StringUtils.split(selections, ",")));
        }

        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);

        Integer pageNo = 1;
        Integer pageSize = 5000;

        boolean intervalFlag = true;
        queryCriteria.setPageSize(pageSize);

        List<DwmVlmsDocs> pageList = null;

        // 转换时间格式,将Long类型转换成date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置新增数据行,从第一行开始
        int rowNum = 1;

        Integer maxSize = 150000;

        SXSSFRow row1 = null;
        do {
            queryCriteria.setPageNo(pageNo);

            // 获取查询数据
            pageList = dwmVlmsOneOrderToEndService.selectDocsList(queryCriteria);
            for (DwmVlmsDocs item : pageList) {
                // 时间字段转换成年月日时分秒类型
                row1 = sheet.createRow(rowNum);
                int j = 0;
                // vvin 底盘号
                row1.createCell(j++).setCellValue(item.getVvin());
                // HostComCode  品牌
                row1.createCell(j++).setCellValue(DwmVlmsFormatUtil.formatBrandToChinese(item.getHostComCode()));
                // baseName  基地
                row1.createCell(j++).setCellValue(item.getBaseName());
                // vehicleName  车型
                row1.createCell(j++).setCellValue(item.getVehicleName());
                // startCityName  始发城市
                row1.createCell(j++).setCellValue(item.getStartCityName());
                // endCityName   经销商目标城市
                row1.createCell(j++).setCellValue(item.getEndCityName());
                // vdwdm  经销商代码
                row1.createCell(j++).setCellValue(item.getVdwdm());
                //  DEALER_NAME   经销商名称
                row1.createCell(j++).setCellValue(item.getDealerName());
                // ddjrq 计划下达日期
                if (item.getDdjrq() != 0 ) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDdjrq()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // vph  配板单号
                row1.createCell(j++).setCellValue(item.getCpzdbh());
                // assignTime  指派日期
                if (item.getAssignTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getAssignTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // TRANSPORT_NAME 指派承运商名称
                row1.createCell(j++).setCellValue(item.getTransportName());
                // actualOutTime  出库日期
                if (item.getActualOutTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getActualOutTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // shipmentTime  起运日期
                if (item.getShipmentTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getShipmentTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // VJSYDM 运输车号
                row1.createCell(j++).setCellValue(item.getVjsydm());
                //  samePlateNum 同板数量
                row1.createCell(j++).setCellValue(item.getSamePlateNum());
                // Dtvsdhsj  DCS到货时间
                if (item.getDtvsdhsj() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDtvsdhsj()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // finalSiteTime   经销商确认到货时间
                if (item.getFinalSiteTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getFinalSiteTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                rowNum++;
            }

            // 如果没有查询到数据  或者 分页查询的数量不符合pageSize 那么终止循环
            if ( CollectionUtils.isEmpty(pageList) || CollectionUtils.size(pageList) < pageSize) {
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
     *docsID车型页面导出
     * @param queryCriteria
     * @param response
     * @throws IOException
     */
    @AutoLog(value = "docs ID车型")
    @ApiOperation(value = "docs ID车型", notes = "docs ID车型")
    @PostMapping(value = "/exportXlsCcxdlList")
    public void exportXlsCcxdlList(@RequestBody GetQueryCriteria queryCriteria, HttpServletResponse response) throws IOException {
        // 创建工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
//        // 设置列宽(写死)
//        int [] width = {20,6,6,25,10,15,15,30,20,15,20,15,20,20,15,8,20,20};
//        for (int columnIndex = 0; columnIndex < 18; columnIndex++) {
//            sheet.setColumnWidth(columnIndex, width[columnIndex] * 256);
//        }
//
//        // 设置
//        Font font = wb.createFont();
//        CellStyle style = wb.createCellStyle();


        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 设置表头行高
//        row.setHeight((short) (22.50 * 20));
        // 获取表头   18个字段
        String[] headers = new String[]{"底盘号", "品牌", "基地", "车型", "始发城市", "经销商目标城市", "经销商代码", "经销商名称",
                "计划下达日期", "配板单号", "指派日期", "指派承运商名称", "出库日期", "起运日期", "运输车号", "同板数量", "DCS到货时间",
                "经销商确认到货时间"};
        int i = 0;
        // 循环遍历表头,作为sheet页的第一行数据
        for (String header : headers) {
            // 获取表头列
            SXSSFCell cell = row.createCell(i++);
            // 为单元格赋值
            cell.setCellValue(header);
        }

        String vvin = queryCriteria.getVvin();
        // vin码批量模式： 0 逗号， 1 回车换行
        if (StringUtil.length(vvin) > 2 && StringUtils.contains(vvin, ",")) {
            queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, ",")));
        } else if (StringUtils.length(vvin) > 2 && StringUtils.contains(vvin, "\n")) {
            queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, "\n")));
        }

        String ccxdl = queryCriteria.getCcxdl();
        if ( StringUtil.length(ccxdl) > 2 && StringUtils.contains(ccxdl,",")) {
            queryCriteria.setCcxdlList(Arrays.asList(StringUtils.split(ccxdl,",")));
        }

        // 过滤选中的数据
        String selections = queryCriteria.getSelections();
        if (StringUtil.length(selections) > 2) {
            queryCriteria.setVvinList(Arrays.asList(StringUtils.split(selections, ",")));
        }
        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);
        Integer pageNo = 1;
        Integer pageSize = 5000;

        boolean intervalFlag = true;
        queryCriteria.setPageSize(pageSize);

        List<DwmVlmsDocs> pageList = null;

        // 转换时间格式,将Long类型转换成date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置新增数据行,从第一行开始
        int rowNum = 1;

        Integer maxSize = 150000;

        SXSSFRow row1 = null;
        do {
            queryCriteria.setPageNo(pageNo);

            // 获取查询数据
            pageList = dwmVlmsOneOrderToEndService.selectDocsCcxdlList(queryCriteria);
            for (DwmVlmsDocs item : pageList) {
                // 时间字段转换成年月日时分秒类型
                row1 = sheet.createRow(rowNum);
                int j = 0;
                // vvin 底盘号
                row1.createCell(j++).setCellValue(item.getVvin());
                // HostComCode  品牌
                row1.createCell(j++).setCellValue(DwmVlmsFormatUtil.formatBrandToChinese(item.getHostComCode()));
                // baseName  基地
                row1.createCell(j++).setCellValue(item.getBaseName());
                // vehicleName  车型
                row1.createCell(j++).setCellValue(item.getVehicleName());
                // startCityName  始发城市
                row1.createCell(j++).setCellValue(item.getStartCityName());
                // endCityName   经销商目标城市
                row1.createCell(j++).setCellValue(item.getEndCityName());
                // vdwdm  经销商代码
                row1.createCell(j++).setCellValue(item.getVdwdm());
                //  DEALER_NAME   经销商名称
                row1.createCell(j++).setCellValue(item.getDealerName());
                // ddjrq 计划下达日期
                if (item.getDdjrq() != 0 ) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDdjrq()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // Cpzdbh  配板单号
                row1.createCell(j++).setCellValue(item.getCpzdbh());
                // assignTime  指派日期
                if (item.getAssignTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getAssignTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // TRANSPORT_NAME 指派承运商名称
                row1.createCell(j++).setCellValue(item.getTransportName());
                // actualOutTime  出库日期
                if (item.getActualOutTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getActualOutTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // shipmentTime  起运日期
                if (item.getShipmentTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getShipmentTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // VJSYDM 运输车号
                row1.createCell(j++).setCellValue(item.getVjsydm());
                //  samePlateNum 同板数量
                row1.createCell(j++).setCellValue(item.getSamePlateNum());
                // Dtvsdhsj  DCS到货时间
                if (item.getDtvsdhsj() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getDtvsdhsj()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
                // finalSiteTime   经销商确认到货时间
                if (item.getFinalSiteTime() != 0) {
                    row1.createCell(j++).setCellValue(sdf.format(item.getFinalSiteTime()));
                }else{
                    row1.createCell(j++).setCellValue("");
                }
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

        // 设置表格默认行高
//        sheet.setDefaultRowHeight((short) (16.5 * 20));


//        for ( int j = 0; j < pageList.size(); j++ ) {
//            sheet.trackAllColumnsForAutoSizing();
//            sheet.autoSizeColumn(j);
//            int width = Math.max(15 * 256, Math.min(256 * 256, sheet.getColumnWidth(j) * 12 / 10));
//            sheet.setColumnWidth(j, width);
//        }
        // 设置内容类型
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream outputStream = response.getOutputStream();
        response.setHeader("Content-disposition", "attachment;filename=trainingRecord.xls");
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * DOCS列表页查询
     *
     * @param queryCriteria
     * @return
     */
    @AutoLog(value = "一单到底-DOCS查询")
    @ApiOperation(value = "一单到底-DOCS查询", notes = "一单到底-DOCS查询")
    @PostMapping("/selectDocsList")
    public Result<Page<DwmVlmsDocs>> selectDocsList(@RequestBody GetQueryCriteria queryCriteria) {
        // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- START
        String vvin = queryCriteria.getVvin();
        if ( StringUtils.isNotBlank(vvin)) {
            if (StringUtils.contains(vvin, ",") && StringUtils.contains(vvin, "\n")) {
                return Result.error("vin码批量查询，分割模式只可以用英文逗号或者回车换行一种模式，不可混搭，请检查vin码查询条件", null);
            }
            // vin码批量模式： 0 逗号， 1 回车换行
            if ( StringUtil.length(vvin) > 2 && StringUtils.contains(vvin, ",")) {
                queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, ",")));
            } else if (StringUtils.length(vvin) > 2 && StringUtils.contains(vvin, "\n")) {
                queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, "\n")));
            }
        }

//        formatQueryTime(queryCriteria);

        Integer total = dwmVlmsOneOrderToEndService.countDocsList(queryCriteria);
        Page<DwmVlmsDocs> page = new Page(queryCriteria.getPageNo(), queryCriteria.getPageSize());
        List<DwmVlmsDocs> pageList = dwmVlmsOneOrderToEndService.selectDocsList(queryCriteria);

        page.setRecords(pageList);
        page.setTotal(total);
        return Result.OK(page);
    }

    /**
     * DOCS车型检索列表页查询
     *
     * @param queryCriteria
     * @return
     */
    @AutoLog(value = "一单到底-DOCS查询")
    @ApiOperation(value = "一单到底-DOCS查询", notes = "一单到底-DOCS查询")
    @PostMapping("/selectDocsCcxdlList")
    public Result<Page<DwmVlmsDocs>> selectDocsCcxdlList(@RequestBody GetQueryCriteria queryCriteria) {
        // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- START
        String vvin = queryCriteria.getVvin();
        if (StringUtils.isNotBlank(vvin)) {
            if (StringUtils.contains(vvin, ",") && StringUtils.contains(vvin, "\n")) {
                return Result.error("vin码批量查询，分割模式只可以用英文逗号或者回车换行一种模式，不可混搭，请检查vin码查询条件", null);
            }
            // vin码批量模式： 0 逗号， 1 回车换行
            if (StringUtil.length(vvin) > 2 && StringUtils.contains(vvin, ",")) {
                queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, ",")));
            } else if (StringUtils.length(vvin) > 2 && StringUtils.contains(vvin, "\n")) {
                queryCriteria.setVvinList(Arrays.asList(StringUtils.split(vvin, "\n")));
            }
        }
        String ccxdl = queryCriteria.getCcxdl();
        if ( StringUtil.length(ccxdl) > 2 && StringUtils.contains(ccxdl,",")) {
            queryCriteria.setCcxdlList(Arrays.asList(StringUtils.split(ccxdl,",")));
        }

//        formatQueryTime(queryCriteria);

        Integer total = dwmVlmsOneOrderToEndService.countDocsCcxdlList(queryCriteria);
        Page<DwmVlmsDocs> page = new Page(queryCriteria.getPageNo(), queryCriteria.getPageSize());
        List<DwmVlmsDocs> pageList = dwmVlmsOneOrderToEndService.selectDocsCcxdlList(queryCriteria);

        page.setRecords(pageList);
        page.setTotal(total);
        return Result.OK(page);
    }


}
