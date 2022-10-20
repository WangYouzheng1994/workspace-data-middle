package org.jeecg.yqwl.datamiddle.ads.order.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.yqwl.datamiddle.ads.order.entity.*;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.service.IOracleSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.jeecg.yqwl.datamiddle.job.common.KafkaTopicConst;
import org.jeecg.yqwl.datamiddle.job.common.excel.ExcelLoadResult;
import org.jeecg.yqwl.datamiddle.job.common.excel.ExcelReadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: DwmVlmsSptb02-DOCS菜单
 * @Author: DaBao & XiaoFeng
 * @Date: 2022-05-12
 * @Version: V2.0
 */

@Slf4j
@Api(tags = "后台_DOCS菜单")
@RestController
@RequestMapping("/ads/order/docsSearch")
public class DocsController extends JeecgController<DwmVlmsOneOrderToEnd, IDwmVlmsOneOrderToEndService> {

    @Autowired
    private IDwmVlmsOneOrderToEndService dwmVlmsOneOrderToEndService;

    @Autowired
    private IOracleSptb02Service iOracleSptb02Service;

    /**
     * 通过excel导入数据
     * 查询Oracle专用的
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcelOfOracle", method = RequestMethod.POST)
    public void importExcelOracle(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, IOException {
        // 读取表格数据
        ExcelLoadResult excelLoadResult = ExcelReadUtil.readExcelData(request);
        List dataList = excelLoadResult.getDataList();

        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        // 设置字体和样式
        Font font = wb.createFont();
        // 字体名称
        font.setFontName("宋体");
        // 字体大小
        font.setFontHeightInPoints((short) 12);
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 设置表头行高
        row.setHeight((short) (22.50 * 20));

        int line = 0;
        SXSSFRow row1 = null;
        for (int i = 0; i < dataList.size(); i++) {
            HashMap dataMap = (HashMap) dataList.get(i);
            String vinStr = (String) dataMap.get("全长底盘号(数据中台表格无此底盘号版本)");
            // 3.构建SQL语句
            // SELECT COUNT(*)  FROM SPTB02 d1 JOIN SPTB02D1 d2 on d1.CJSDBH = d2.CJSDBH WHERE D2.VVIN='LFV3B2FY3N3056545';
            Integer integer = iOracleSptb02Service.countOracleVinOfSptb02AndSptb02d1(vinStr);
            System.out.println("展示:::::::::::::::::::::"+integer);
            // 4.执行SQL语句
            boolean oracleHas = true;
            if (integer >= 1) {
                oracleHas = false;
            }
            if (oracleHas) {
                // row 行
                row1= sheet.createRow(line);
                // 这个地方是设置第几列的
                SXSSFCell cell = row1.createCell(0);
                cell.setCellValue(vinStr);
                line++;
            }

        }
        // 设置内容类型
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=Vin.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 通过excel导入数据
     * 查询Oracle专用的
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcelOfClickhouse", method = RequestMethod.POST)
    public void importExcelClickhouse(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, IOException {
        // 读取表格数据
        ExcelLoadResult excelLoadResult = ExcelReadUtil.readExcelData(request);
        List dataList = excelLoadResult.getDataList();

        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        // 设置字体和样式
        Font font = wb.createFont();
        // 字体名称
        font.setFontName("宋体");
        // 字体大小
        font.setFontHeightInPoints((short) 12);
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 设置表头行高
        row.setHeight((short) (22.50 * 20));

        int line = 0;
        SXSSFRow row1 = null;
        for (int i = 0; i < dataList.size(); i++) {
            HashMap dataMap = (HashMap) dataList.get(i);
            String vinStr = (String) dataMap.get("全长底盘号(数据中台表格无此底盘号版本)");
            // 3.构建SQL语句
            // SELECT COUNT(*)  FROM SPTB02 d1 JOIN SPTB02D1 d2 on d1.CJSDBH = d2.CJSDBH WHERE D2.VVIN='LFV3B2FY3N3056545';
            Integer integer = dwmVlmsOneOrderToEndService.countClickhouseVin(vinStr);
            System.out.println("展示:::::::::::::::::::::"+integer);
            // 4.执行SQL语句
            boolean oracleHas = true;
            if (integer >= 1) {
                oracleHas = false;
            }
            if (oracleHas) {
                // row 行
                row1= sheet.createRow(line);
                // 这个地方是设置第几列的
                SXSSFCell cell = row1.createCell(0);
                cell.setCellValue(vinStr);
                line++;
            }

        }
        // 设置内容类型
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=Vin.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
    /**
     * 通过excel导入数据
     * 查询Oracle专用的且为BatchIn的版本
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcelOfClickhouseBatchIn", method = RequestMethod.POST)
    public void importExcelClickhouseBatchIn(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, IOException {
        // 读取表格数据
        ExcelLoadResult excelLoadResult = ExcelReadUtil.readExcelData(request);
        List dataList = excelLoadResult.getDataList();

        SXSSFWorkbook wb = new SXSSFWorkbook();
        // 在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        // 设置字体和样式
        Font font = wb.createFont();
        // 字体名称
        font.setFontName("宋体");
        // 字体大小
        font.setFontHeightInPoints((short) 12);
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 设置表头行高
        row.setHeight((short) (22.50 * 20));
        List<String> totalListStr = new ArrayList<String>();
        List<String> resultVinLists = new ArrayList<>();
        int line = 0;
        SXSSFRow row1 = null;
        for (int i = 0; i < dataList.size(); i++) {
            HashMap dataMap = (HashMap) dataList.get(i);
            String vinStr = (String) dataMap.get("全长底盘号(数据中台表格无此底盘号版本)");
            //  1.新建一个List<string> Vin的 把导入的表的所有Vin收集起来
            //  2.再去用Vin的集合组成的in去Clickhosue查寻所有的Vin,把查到的Vin的List和List<Vin>进行比对,取交集

            totalListStr.add(vinStr);
        }
        // 用谷歌的插件把List分成多个List
        List<List<String>> listsParts = Lists.partition(totalListStr, 999);
        for (List<String> listsPart : listsParts) {
            List<String> oneOrderToEndVin = dwmVlmsOneOrderToEndService.getOneOrderToEndVin(listsPart);
            totalListStr = totalListStr.stream().filter(item -> !oneOrderToEndVin.contains(item)).collect(Collectors.toList());
        }
        // 单元格赋值
        for (String resultOfVin : totalListStr) {
            // row 行
            row1= sheet.createRow(line);
            // 这个地方是设置第几列的
            SXSSFCell cell = row1.createCell(0);
            cell.setCellValue(resultOfVin);
            line++;
        }
        // 设置内容类型
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=Vin.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

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
        // 设置表头行高
        row.setHeight((short) (22.50 * 20));
        // 获取表头
        String[] headers = new String[]{"底盘号", "品牌", "基地","车型代码", "车型", "始发城市", "经销商目标城市", "经销商代码", "经销商名称",
                "计划下达日期", "配板单号","运输方式", "指派日期", "指派承运商名称", "出库日期", "起运日期", "运输车号", "同板数量", "DCS到货时间",
                "经销商确认到货时间"};
        int i = 0;
        // 循环遍历表头,作为sheet页的第一行数据
        for (String header : headers) {
            // 获取表头列
            SXSSFCell cell = row.createCell(i++);
            // 为单元格赋值
            cell.setCellValue(header);
            // 设置表头居中显示
            // cell.setCellStyle(cellstyle);
            // 设置表头字体
            // cellstyle.setFont(font);
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

//        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);

        Integer integer = dwmVlmsOneOrderToEndService.selectDocsCount(queryCriteria);
        if (integer > 150000) {
            this.responseJsonString(response, JSONObject.toJSONString(Result.error("超出导出数量限制！")));
            return;
        }

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
                // VVIN  底盘号
                SXSSFCell cell = row1.createCell(j++);
                cell.setCellValue(item.getVvin());
                // HostComCode  品牌
                SXSSFCell cell1 = row1.createCell(j++);
                cell1.setCellValue(item.getBrandName());
                // baseName  基地
                SXSSFCell cell2 = row1.createCell(j++);
                cell2.setCellValue(item.getBaseName());
                // vehicleCode  车型代码
                SXSSFCell cellvehicleCode = row1.createCell(j++);
                cellvehicleCode.setCellValue(item.getVehicleCode());
                // vehicleName  车型
                SXSSFCell cell3 = row1.createCell(j++);
                cell3.setCellValue(item.getVehicleName());
                // startCityName  始发城市
                SXSSFCell cell4 = row1.createCell(j++);
                cell4.setCellValue(item.getStartCityName());
                // endCityName   经销商目标城市
                SXSSFCell cell5 = row1.createCell(j++);
                cell5.setCellValue(item.getEndCityName());
                // vdwdm  经销商代码
                SXSSFCell cell6 = row1.createCell(j++);
                cell6.setCellValue(item.getVdwdm());
                //  DEALER_NAME   经销商名称
                SXSSFCell cell7 = row1.createCell(j++);
                cell7.setCellValue(item.getDealerName());
                // ddjrq 计划下达日期
                if (item.getDdjrqR3() != 0 ) {
                    SXSSFCell cell8 = row1.createCell(j++);
                    cell8.setCellValue(sdf.format(item.getDdjrqR3()));
                }else{
                    SXSSFCell cell8 = row1.createCell(j++);
                    cell8.setCellValue("");
                }
                // Cpzdbh  配板单号
                SXSSFCell cell9 = row1.createCell(j++);
                cell9.setCellValue(item.getCpzdbh());
                //运输方式
                SXSSFCell cellTen = row1.createCell(j++);
                if (Objects.nonNull(item.getVysfs())){
                    cellTen.setCellValue(item.getVysfs());
                } else {
                    cellTen.setCellValue("");
                }
                // assignTime  指派日期
                if (item.getAssignTime() != 0) {
                    SXSSFCell cell10 = row1.createCell(j++);
                    cell10.setCellValue(sdf.format(item.getAssignTime()));
                } else {
                    SXSSFCell cell10 = row1.createCell(j++);
                    cell10.setCellValue("");
                }
                // TRANSPORT_NAME 指派承运商名称
                SXSSFCell cell11 = row1.createCell(j++);
                cell11.setCellValue(item.getTransportName());
                // actualOutTime  出库日期
                if (item.getActualOutTime() != 0) {
                    SXSSFCell cell12 = row1.createCell(j++);
                    cell12.setCellValue(sdf.format(item.getActualOutTime()));
                }else{
                    SXSSFCell cell12 = row1.createCell(j++);
                    cell12.setCellValue("");
                }
                // shipmentTime  起运日期
                if (item.getShipmentTime() != 0) {
                    SXSSFCell cell13 = row1.createCell(j++);
                    cell13.setCellValue(sdf.format(item.getShipmentTime()));
                }else{
                    SXSSFCell cell13 = row1.createCell(j++);
                    cell13.setCellValue("");
                }
                // VJSYDM 运输车号
                SXSSFCell cell14 = row1.createCell(j++);
                cell14.setCellValue(item.getVjsydm());
                //  samePlateNum 同板数量
                SXSSFCell cell15 = row1.createCell(j++);
                cell15.setCellValue(item.getSamePlateNum());
                // Dtvsdhsj  DCS到货时间
                if (item.getDtvsdhsj() != 0) {
                    SXSSFCell cell16 = row1.createCell(j++);
                    cell16.setCellValue(sdf.format(item.getDtvsdhsj()));
                }else{
                    SXSSFCell cell16 = row1.createCell(j++);
                    cell16.setCellValue("");
                }
                // finalSiteTime   经销商确认到货时间
                if (item.getFinalSiteTime() != 0) {
                    SXSSFCell cell17 = row1.createCell(j++);
                    cell17.setCellValue(sdf.format(item.getFinalSiteTime()));
                }else{
                    SXSSFCell cell17 = row1.createCell(j++);
                    cell17.setCellValue("");
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
        // 创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        // 获取表头   18个字段
        String[] headers = new String[]{"底盘号", "品牌", "基地","车型代码", "车型", "始发城市", "经销商目标城市", "经销商代码", "经销商名称",
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
//        DwmVlmsFormatUtil.formatQueryTime(queryCriteria);
        Integer pageNo = 1;
        Integer pageSize = 5000;

        boolean intervalFlag = true;
        queryCriteria.setPageSize(pageSize);

        List<DwmVlmsDocs> pageList = null;

        // 转换时间格式,将Long类型转换成date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer integer = dwmVlmsOneOrderToEndService.selectDocsCount(queryCriteria);
        if (integer > 150000) {
            this.responseJsonString(response, JSONObject.toJSONString(Result.error("超出导出数量限制！")));
            return;
        }

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
                // VVIN  底盘号
                SXSSFCell cell = row1.createCell(j++);
                // cell.setCellStyle(cellstyle);
                cell.setCellValue(item.getVvin());
                // HostComCode  品牌
                SXSSFCell cell1 = row1.createCell(j++);
                // cell1.setCellStyle(cellstyle);
                cell1.setCellValue(item.getBrandName());
                // baseName  基地
                SXSSFCell cell2 = row1.createCell(j++);
                // cell2.setCellStyle(cellstyle);
                cell2.setCellValue(item.getBaseName());
                // vehicleCode  车型代码
                SXSSFCell cellvehicleCode  = row1.createCell(j++);
                // cell3.setCellStyle(cellstyle);
                cellvehicleCode.setCellValue(item.getVehicleCode());
                // vehicleName  车型
                SXSSFCell cell3 = row1.createCell(j++);
                // cell3.setCellStyle(cellstyle);
                cell3.setCellValue(item.getVehicleName());
                // startCityName  始发城市
                SXSSFCell cell4 = row1.createCell(j++);
                // cell4.setCellStyle(cellstyle);
                cell4.setCellValue(item.getStartCityName());
                // endCityName   经销商目标城市
                SXSSFCell cell5 = row1.createCell(j++);
                // cell5.setCellStyle(cellstyle);
                cell5.setCellValue(item.getEndCityName());
                // vdwdm  经销商代码
                SXSSFCell cell6 = row1.createCell(j++);
                // cell6.setCellStyle(cellstyle);
                cell6.setCellValue(item.getVdwdm());
                //  DEALER_NAME   经销商名称
                SXSSFCell cell7 = row1.createCell(j++);
                // cell7.setCellStyle(cellstyle);
                cell7.setCellValue(item.getDealerName());
                // ddjrq 计划下达日期
                if (item.getDdjrqR3() != 0 ) {
                    SXSSFCell cell8 = row1.createCell(j++);
                    // cell8.setCellStyle(cellstyle);
                    cell8.setCellValue(sdf.format(item.getDdjrqR3()));
                }else{
                    SXSSFCell cell8 = row1.createCell(j++);
                    // cell8.setCellStyle(cellstyle);
                    cell8.setCellValue("");
                }
                // Cpzdbh  配板单号
                SXSSFCell cell9 = row1.createCell(j++);
                // cell9.setCellStyle(cellstyle);
                cell9.setCellValue(item.getCpzdbh());
                // assignTime  指派日期
                if (item.getAssignTime() != 0) {
                    SXSSFCell cell10 = row1.createCell(j++);
                    // cell10.setCellStyle(cellstyle);
                    cell10.setCellValue(sdf.format(item.getAssignTime()));
                }else{
                    SXSSFCell cell10 = row1.createCell(j++);
                    // cell10.setCellStyle(cellstyle);
                    cell10.setCellValue("");
                }
                // TRANSPORT_NAME 指派承运商名称
                SXSSFCell cell11 = row1.createCell(j++);
                // cell11.setCellStyle(cellstyle);
                cell11.setCellValue(item.getTransportName());
                // actualOutTime  出库日期
                if (item.getActualOutTime() != 0) {
                    SXSSFCell cell12 = row1.createCell(j++);
                    // cell12.setCellStyle(cellstyle);
                    cell12.setCellValue(sdf.format(item.getActualOutTime()));
                }else{
                    SXSSFCell cell12 = row1.createCell(j++);
                    // cell12.setCellStyle(cellstyle);
                    cell12.setCellValue("");
                }
                // shipmentTime  起运日期
                if (item.getShipmentTime() != 0) {
                    SXSSFCell cell13 = row1.createCell(j++);
                    // cell13.setCellStyle(cellstyle);
                    cell13.setCellValue(sdf.format(item.getShipmentTime()));
                }else{
                    SXSSFCell cell13 = row1.createCell(j++);
                    // cell13.setCellStyle(cellstyle);
                    cell13.setCellValue("");
                }
                // VJSYDM 运输车号
                SXSSFCell cell14 = row1.createCell(j++);
                // cell14.setCellStyle(cellstyle);
                cell14.setCellValue(item.getVjsydm());
                //  samePlateNum 同板数量
                SXSSFCell cell15 = row1.createCell(j++);
                // cell15.setCellStyle(cellstyle);
                cell15.setCellValue(item.getSamePlateNum());
                // Dtvsdhsj  DCS到货时间
                if (item.getDtvsdhsj() != 0) {
                    SXSSFCell cell16 = row1.createCell(j++);
                    // cell16.setCellStyle(cellstyle);
                    cell16.setCellValue(sdf.format(item.getDtvsdhsj()));
                }else{
                    SXSSFCell cell16 = row1.createCell(j++);
                    // cell16.setCellStyle(cellstyle);
                    cell16.setCellValue("");
                }
                // finalSiteTime   经销商确认到货时间
                if (item.getFinalSiteTime() != 0) {
                    SXSSFCell cell17 = row1.createCell(j++);
                    // cell17.setCellStyle(cellstyle);
                    cell17.setCellValue(sdf.format(item.getFinalSiteTime()));
                }else{
                    SXSSFCell cell17 = row1.createCell(j++);
                    // cell17.setCellStyle(cellstyle);
                    cell17.setCellValue("");
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

//        Integer total = dwmVlmsOneOrderToEndService.countDocsList(queryCriteria);
//        Page<DwmVlmsDocs> page = new Page(queryCriteria.getPageNo(), queryCriteria.getPageSize());
//        List<DwmVlmsDocs> pageList = dwmVlmsOneOrderToEndService.selectDocsList(queryCriteria);
//
//        page.setRecords(pageList);
//        page.setTotal(total);

        return Result.OK(dwmVlmsOneOrderToEndService.selectDocsPage(queryCriteria));
    }

    /**
     * DOCS ID车型检索列表页查询
     *
     * @param queryCriteria
     * @return
     */
    @AutoLog(value = "一单到底-DOCS ID车型")
    @ApiOperation(value = "一单到底-DOCS ID车型", notes = "一单到底-DOCS ID车型")
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
        // id车型条件
        String ccxdl = queryCriteria.getCcxdl();
        if ( StringUtil.length(ccxdl) > 2 && StringUtils.contains(ccxdl,",")) {
            queryCriteria.setCcxdlList(Arrays.asList(StringUtils.split(ccxdl,",")));
        }
//        formatQueryTime(queryCriteria);

//        Integer total = dwmVlmsOneOrderToEndService.countDocsCcxdlList(queryCriteria);
//        Page<DwmVlmsDocs> page = new Page(queryCriteria.getPageNo(), queryCriteria.getPageSize());
//        List<DwmVlmsDocs> pageList = dwmVlmsOneOrderToEndService.selectDocsCcxdlList(queryCriteria);
//
//        page.setRecords(pageList);
//        page.setTotal(total);
        return Result.OK(dwmVlmsOneOrderToEndService.selectDocsPage(queryCriteria));
    }


}
