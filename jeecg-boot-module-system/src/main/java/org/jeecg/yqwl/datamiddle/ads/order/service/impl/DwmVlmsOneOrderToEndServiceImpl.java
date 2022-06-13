package org.jeecg.yqwl.datamiddle.ads.order.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsOneOrderToEndMapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.jeecg.yqwl.datamiddle.ads.order.vo.SelectData;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */

@Slf4j
@DS("slave0")
@Service
public class DwmVlmsOneOrderToEndServiceImpl extends ServiceImpl<DwmVlmsOneOrderToEndMapper, DwmVlmsOneOrderToEnd> implements IDwmVlmsOneOrderToEndService {

    @Resource
    private DwmVlmsOneOrderToEndMapper dwmVlmsOneOrderToEndMapper;

    /**
     * 按条件进行分页查询
     * @param queryCriteria
     * @param page
     * @return
     */
    @Override
    public Page<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(GetQueryCriteria queryCriteria, Page<DwmVlmsOneOrderToEnd> page) {
        List<DwmVlmsOneOrderToEnd> oneOrderToEndList = dwmVlmsOneOrderToEndMapper.selectOneOrderToEndList(queryCriteria, page);
        //遍历list,并查询出同板数量赋值
        for ( int i = 0; i < oneOrderToEndList.size(); i ++ ) {
            DwmVlmsOneOrderToEnd params = oneOrderToEndList.get(i);
            //获取配载单编号的值
            String stowageNoteNo = params.getStowageNoteNo();
            //查询所有的配载单编号和同板数量
            List<SelectData> samePlateNumList = dwmVlmsOneOrderToEndMapper.selectTotal();
            //对得到的结果进行循环
            for ( int j = 0; j < samePlateNumList.size(); j++ ) {
                //获取配载单编号
                String noteNo = samePlateNumList.get(j).getStowageNoteNo();
                //添加配载单编号不为空,配载单编号为空不计算
                if ( StringUtils.isNotEmpty(stowageNoteNo) && StringUtils.isNotEmpty(noteNo) ) {
                    //判断配载单编号的值相同,则设置同板数量
                    if ( stowageNoteNo.equals(noteNo)) {
                        params.setSamePlateNum(samePlateNumList.get(j).getSamePlateNum());
                    }
                }
            }
            //TODO:添加逻辑  如果是时间字段  需要在得到的值进行-8小时处理
            //cp9OfflineTime,leaveFactoryTime,inSiteTime,inWarehouseName,taskNo,vehicleReceivingTime,   4
            // stowageNoteTime,stowageNoteNo,trafficType,assignTime,carrierName,actualOutTime,shipmentTime,transportVehicleNo,samePlateNum,   4
            // vehicleNum,startCityName,endCityName,vdwdm,startWaterwayName,inStartWaterwayTime,endStartWaterwayTime,endWaterwayName,  2
            // inEndWaterwayTime,startPlatformName,inStartPlatformTime,outStartPlatformTime,endPlatformName,inEndPlatformTime,unloadShipTime,   5
            // unloadRailwayTime,inDistributeTime,distributeAssignTime,distributeCarrierName,distributeVehicleNo,distributeVehicleNum,outDistributeTime,  4
            // distributeShipmentTime,dotSiteTime,finalSiteTime    3   共22个时间字段
            Long second = 8 * 60 * 60 * 1000L;
            //将字段转换成String类型
            //cp9OfflineTime
            if ( params.getCp9OfflineTime() != 0 ) {
                 Long cp9OfflineTime = params.getCp9OfflineTime() - second;
                params.setCp9OfflineTime(cp9OfflineTime);
            }

            //leaveFactoryTime
            if ( params.getLeaveFactoryTime() != 0) {
                 Long leaveFactoryTime = params.getLeaveFactoryTime() - second;
                 params.setLeaveFactoryTime(leaveFactoryTime);
            }
            //inSiteTime
            if ( params.getInSiteTime() != 0) {
                Long inSiteTime = params.getInSiteTime() - second;
                params.setInSiteTime(inSiteTime);
            }
            //vehicleReceivingTime
            if ( params.getVehicleReceivingTime() != 0) {
                Long vehicleReceivingTime = params.getVehicleReceivingTime() - second;
                params.setVehicleReceivingTime(vehicleReceivingTime);
            }
            //stowageNoteTime
            if ( params.getStowageNoteTime() != 0) {
                Long stowageNoteTime = params.getStowageNoteTime() - second;
                params.setStowageNoteTime(stowageNoteTime);
            }
            //assignTime
            if (params.getAssignTime() != 0) {
                Long assignTime = params.getAssignTime() - second;
                params.setAssignTime(assignTime);
            }
            //actualOutTime
            if ( params.getActualOutTime() != 0) {
                Long actualOutTime = params.getActualOutTime() - second;
                params.setActualOutTime(actualOutTime);
            }
            //shipmentTime
            if (params.getShipmentTime() != 0) {
                Long shipmentTime = params.getShipmentTime() - second;
                params.setShipmentTime(shipmentTime);
            }
            //inStartWaterwayTime,
            if ( params.getInStartWaterwayTime() != 0 ) {
                Long inStartWaterwayTime = params.getInStartWaterwayTime() - second;
                params.setInStartWaterwayTime(inStartWaterwayTime);
            }
            // endStartWaterwayTime
            if ( params.getEndStartWaterwayTime() != 0) {
                Long endStartWaterwayTime = params.getEndStartWaterwayTime() - second;
                params.setEndStartWaterwayTime(endStartWaterwayTime);
            }
            //inEndWaterwayTime
            if ( params.getInEndWaterwayTime() != 0) {
                Long inEndWaterwayTime = params.getInEndWaterwayTime() - second;
                params.setInEndWaterwayTime(inEndWaterwayTime);
            }
            // inStartPlatformTime
            if ( params.getInStartPlatformTime() != 0 ) {
                Long inStartPlatformTime = params.getInStartPlatformTime() - second;
                params.setInStartPlatformTime(inStartPlatformTime);
            }
            // outStartPlatformTime
            if ( params.getOutStartPlatformTime() != 0) {
                Long outStartPlatformTime = params.getOutStartPlatformTime() - second;
                params.setOutStartPlatformTime(outStartPlatformTime);
            }
            // inEndPlatformTime,
            if ( params.getInEndPlatformTime() != 0) {
                Long inEndPlatformTime = params.getInEndPlatformTime() - second;
                params.setInEndPlatformTime(inEndPlatformTime);
            }
            // unloadShipTime,
            if ( params.getUnloadShipTime() != 0) {
                Long unloadShipTime = params.getUnloadShipTime() - second;
                params.setUnloadShipTime(unloadShipTime);
            }
            // unloadRailwayTime,
            if ( params.getUnloadRailwayTime() != 0) {
                Long unloadRailwayTime = params.getUnloadRailwayTime() - second;
                params.setUnloadRailwayTime(unloadRailwayTime);
            }
            // inDistributeTime,
            if ( params.getInDistributeTime() != 0 ) {
                Long inDistributeTime = params.getInDistributeTime() - second;
                params.setInDistributeTime(inDistributeTime);
            }
            // distributeAssignTime
            if ( params.getDistributeAssignTime() != 0 ) {
                Long distributeAssignTime = params.getDistributeAssignTime() - second;
                params.setDistributeAssignTime(distributeAssignTime);
            }
            //outDistributeTime
            if ( params.getOutDistributeTime() != 0 ) {
                Long outDistributeTime = params.getOutDistributeTime() - second;
                params.setOutDistributeTime(outDistributeTime);
            }
            // distributeShipmentTime,
            if ( params.getDistributeShipmentTime() != 0) {
                Long distributeShipmentTime = params.getDistributeShipmentTime() - second;
                params.setDistributeShipmentTime(distributeShipmentTime);
            }
            // dotSiteTime,
            if ( params.getDotSiteTime() != 0) {
                Long dotSiteTime = params.getDotSiteTime() - second;
                params.setDotSiteTime(dotSiteTime);
            }
            // finalSiteTime
            if ( params.getFinalSiteTime() != 0) {
                Long finalSiteTime = params.getFinalSiteTime() - second;
                params.setFinalSiteTime(finalSiteTime);
            }
        }
        return page.setRecords(oneOrderToEndList);
    }


    /**
     * 导出
     * @param queryCriteria
     * @return
     */
    @Override
    public SXSSFWorkbook export(GetQueryCriteria queryCriteria) {
        //创建工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook();
        //在工作簿中创建sheet页
        SXSSFSheet sheet = wb.createSheet("sheet1");
        //创建行,从0开始
        SXSSFRow row = sheet.createRow(0);
        //获取表头(前端页面一共有44个字段,entity一共是57个字段)
//	  Field[] fileds = DwmVlmsOneOrderToEnd.class.getDeclaredFields();
        String[] headers = new String[]{"底盘号","品牌","基地","车型","CP9下线接车日期","出厂日期","入库日期","入库仓库","任务单号",
                "整车物流接收STD日期","配板日期","配板单号","运输方式","指派日期","指派承运商名称","出库日期","起运日期-公路/铁路",
                "运输车号","同板数量","轿运车车位数","始发城市","目的城市","经销商代码","始发港名称","到达始发港口时间/入港时间",
                "始发港口水运离港时间","目的港名称","到达目的港时间","始发站名称","到达始发站时间/入站时间","始发站台铁路离站时间",
                "目的站名称","到达目的站时间","卸船时间（水路到目的站）","卸车时间（铁路到目的站）","末端分拨中心入库时间",
                "末端分拨中心指派时间","末端分拨承运商","分拨承运轿运车车牌号","港/站分拨承运轿运车车位数","分拨出库时间",
                "分拨起运时间","送达时间-DCS到货时间","经销商确认到货时间"};
        int i = 0;
        //循环遍历表头,作为sheet页的第一行数据
        for ( String header : headers ) {
            //获取表头列
            SXSSFCell cell = row.createCell(i++);
            //为单元格赋值
            cell.setCellValue(header);
        }

        //获取查询数据
        List<DwmVlmsOneOrderToEnd> exportList = dwmVlmsOneOrderToEndMapper.export(queryCriteria);
        //计算同板数量

        //转换时间格式,将Long类型转换成date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //所有的时间字段减去8小时
        Long scend = 8 * 60 *60 * 1000L;
        //设置新增数据行,从第一行开始
        int rowNum = 1;
        //将列表的数据获取到Excel表中
        //vin,brand,baseName,vehicleName,cp9OfflineTime,leaveFactoryTime,inSiteTime,inWarehouseName,taskNo,vehicleReceivingTime,
        // stowageNoteTime,stowageNoteNo,trafficType,assignTime,carrierName,actualOutTime,shipmentTime,transportVehicleNo,samePlateNum,
        // vehicleNum,startCityName,endCityName,vdwdm,startWaterwayName,inStartWaterwayTime,endStartWaterwayTime,endWaterwayName,
        // inEndWaterwayTime,startPlatformName,inStartPlatformTime,outStartPlatformTime,endPlatformName,inEndPlatformTime,unloadShipTime,
        // unloadRailwayTime,inDistributeTime,distributeAssignTime,distributeCarrierName,distributeVehicleNo,distributeVehicleNum,outDistributeTime,
        // distributeShipmentTime,dotSiteTime,finalSiteTime
        for ( DwmVlmsOneOrderToEnd item : exportList ) {
            //TODO:时间字段转换成年月日时分秒类型
            SXSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(item.getVin());//vin
            row1.createCell(1).setCellValue(item.getBrand());//brand
            row1.createCell(2).setCellValue(item.getBaseName());//baseName
            row1.createCell(3).setCellValue(item.getVehicleName());//vehicleName
            if ( item.getCp9OfflineTime() != 0 ) {
                row1.createCell(4).setCellValue(sdf.format(item.getCp9OfflineTime() - scend));//cp9OfflineTime
            }
            if ( item.getLeaveFactoryTime() != 0 ) {
                row1.createCell(5).setCellValue(sdf.format(item.getLeaveFactoryTime() - scend));//leaveFactoryTime
            }
            if ( item.getInSiteTime() != 0 ) {
                row1.createCell(6).setCellValue(sdf.format(item.getInSiteTime() - scend));//inSiteTime
            }
            row1.createCell(7).setCellValue(item.getInWarehouseName());//inWarehouseName
            row1.createCell(8).setCellValue(item.getTaskNo());//taskNo
            if ( item.getVehicleReceivingTime() != 0 ) {
                row1.createCell(9).setCellValue(sdf.format(item.getVehicleReceivingTime() - scend));//vehicleReceivingTime
            }
            if ( item.getStowageNoteTime() != 0 ) {
                row1.createCell(10).setCellValue(sdf.format(item.getStowageNoteTime() - scend));//stowageNoteTime
            }
            row1.createCell(11).setCellValue(item.getStowageNoteNo());//stowageNoteNo
            row1.createCell(12).setCellValue(item.getTrafficType());//trafficType
            if ( item.getAssignTime() != 0 ) {
                row1.createCell(13).setCellValue(sdf.format(item.getAssignTime() - scend));//assignTime
            }
            row1.createCell(14).setCellValue(item.getCarrierName());//carrierName
            if ( item.getActualOutTime() != 0 ) {
                row1.createCell(15).setCellValue(sdf.format(item.getActualOutTime() - scend));//actualOutTime
            }
            if ( item.getShipmentTime() != 0 ) {
                row1.createCell(16).setCellValue(sdf.format(item.getShipmentTime() - scend));//shipmentTime
            }
            row1.createCell(17).setCellValue(item.getTransportVehicleNo());//transportVehicleNo

            row1.createCell(18).setCellValue(item.getSamePlateNum());//samePlateNum

            row1.createCell(19).setCellValue(item.getVehicleNum());//vehicleNum
            row1.createCell(20).setCellValue(item.getStartCityName());//startCityName
            row1.createCell(21).setCellValue(item.getEndCityName());//endCityName
            row1.createCell(22).setCellValue(item.getVdwdm());//vdwdm
            row1.createCell(23).setCellValue(item.getStartWaterwayName());//startWaterwayName
            if ( item.getInStartWaterwayTime() != 0 ) {
                row1.createCell(24).setCellValue(sdf.format(item.getInStartWaterwayTime() - scend));//inStartWaterwayTime
            }
            if ( item.getEndStartWaterwayTime() != 0 ) {
                row1.createCell(25).setCellValue(sdf.format(item.getEndStartWaterwayTime() - scend));//endStartWaterwayTime
            }
            row1.createCell(26).setCellValue(item.getEndWaterwayName());//endWaterwayName
            if ( item.getInEndWaterwayTime() != 0 ) {
                row1.createCell(27).setCellValue(sdf.format(item.getInEndWaterwayTime() - scend));//inEndWaterwayTime
            }
            row1.createCell(28).setCellValue(item.getStartPlatformName());//startPlatformName
            if ( item.getInStartPlatformTime() != 0 ) {
                row1.createCell(29).setCellValue(sdf.format(item.getInStartPlatformTime() - scend));//inStartPlatformTime
            }
            if ( item.getOutStartPlatformTime() != 0 ) {
                row1.createCell(30).setCellValue(sdf.format(item.getOutStartPlatformTime() - scend));//outStartPlatformTime
            }
            row1.createCell(31).setCellValue(item.getEndPlatformName());//endPlatformName
            if ( item.getInEndPlatformTime() != 0 ) {
                row1.createCell(32).setCellValue(sdf.format(item.getInEndPlatformTime() - scend));//inEndPlatformTime
            }
            if ( item.getUnloadShipTime() != 0 ) {
                row1.createCell(33).setCellValue(sdf.format(item.getUnloadShipTime() - scend));//unloadShipTime
            }
            if ( item.getUnloadRailwayTime() != 0 ) {
                row1.createCell(34).setCellValue(sdf.format(item.getUnloadRailwayTime() - scend));//unloadRailwayTime
            }
            if ( item.getInDistributeTime() != 0 ) {
                row1.createCell(35).setCellValue(sdf.format(item.getInDistributeTime() - scend));//inDistributeTime
            }
            if ( item.getDistributeAssignTime() != 0 ) {
                row1.createCell(36).setCellValue(sdf.format(item.getDistributeAssignTime() - scend));//distributeAssignTime
            }
            row1.createCell(37).setCellValue(item.getDistributeCarrierName());//distributeCarrierName
            row1.createCell(38).setCellValue(item.getDistributeVehicleNo());//distributeVehicleNo
            row1.createCell(39).setCellValue(item.getDistributeVehicleNum());//distributeVehicleNum
            if ( item.getOutDistributeTime() != 0 ) {
                row1.createCell(40).setCellValue(sdf.format(item.getOutDistributeTime() - scend));//outDistributeTime
            }
            if ( item.getDistributeShipmentTime() != 0 ) {
                row1.createCell(41).setCellValue(sdf.format(item.getDistributeShipmentTime() - scend));//distributeShipmentTime
            }
            if ( item.getDotSiteTime() != 0 ) {
                row1.createCell(42).setCellValue(sdf.format(item.getDotSiteTime() - scend));//dotSiteTime
            }
            if ( item.getFinalSiteTime() != 0 ) {
                row1.createCell(43).setCellValue(sdf.format(item.getFinalSiteTime() - scend));//finalSiteTime
            }
            rowNum ++;
        }
        return wb;
    }

    /**
     * 查询同板数量
     * @return
     */
    @Override
    public List<SelectData> selectTotal() {
        List<SelectData> total = dwmVlmsOneOrderToEndMapper.selectTotal();
        return total;
    }
}
