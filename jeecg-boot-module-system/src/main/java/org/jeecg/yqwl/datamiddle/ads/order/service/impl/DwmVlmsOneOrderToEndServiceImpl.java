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
            SXSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(item.getVin());//vin
            row1.createCell(1).setCellValue(item.getBrand());//brand
            row1.createCell(2).setCellValue(item.getBaseName());//baseName
            row1.createCell(3).setCellValue(item.getVehicleName());//vehicleName
            row1.createCell(4).setCellValue(item.getCp9OfflineTime());//cp9OfflineTime
            row1.createCell(5).setCellValue(item.getLeaveFactoryTime());//leaveFactoryTime
            row1.createCell(6).setCellValue(item.getInSiteTime());//inSiteTime
            row1.createCell(7).setCellValue(item.getInWarehouseName());//inWarehouseName
            row1.createCell(8).setCellValue(item.getTaskNo());//taskNo
            row1.createCell(9).setCellValue(item.getVehicleReceivingTime());//vehicleReceivingTime
            row1.createCell(10).setCellValue(item.getStowageNoteTime());//stowageNoteTime
            row1.createCell(11).setCellValue(item.getStowageNoteNo());//stowageNoteNo
            row1.createCell(12).setCellValue(item.getTrafficType());//trafficType
            row1.createCell(13).setCellValue(item.getAssignTime());//assignTime
            row1.createCell(14).setCellValue(item.getCarrierName());//carrierName
            row1.createCell(15).setCellValue(item.getActualOutTime());//actualOutTime
            row1.createCell(16).setCellValue(item.getShipmentTime());//shipmentTime
            row1.createCell(17).setCellValue(item.getTransportVehicleNo());//transportVehicleNo
            row1.createCell(18).setCellValue(item.getSamePlateNum());//samePlateNum
            row1.createCell(19).setCellValue(item.getVehicleNum());//vehicleNum
            row1.createCell(20).setCellValue(item.getStartCityName());//startCityName
            row1.createCell(21).setCellValue(item.getEndCityName());//endCityName
            row1.createCell(22).setCellValue(item.getVdwdm());//vdwdm
            row1.createCell(23).setCellValue(item.getStartWaterwayName());//startWaterwayName
            row1.createCell(24).setCellValue(item.getInStartWaterwayTime());//inStartWaterwayTime
            row1.createCell(25).setCellValue(item.getEndStartWaterwayTime());//endStartWaterwayTime
            row1.createCell(26).setCellValue(item.getEndWaterwayName());//endWaterwayName
            row1.createCell(27).setCellValue(item.getInEndWaterwayTime());//inEndWaterwayTime
            row1.createCell(28).setCellValue(item.getStartPlatformName());//startPlatformName
            row1.createCell(29).setCellValue(item.getInStartPlatformTime());//inStartPlatformTime
            row1.createCell(30).setCellValue(item.getOutStartPlatformTime());//outStartPlatformTime
            row1.createCell(31).setCellValue(item.getEndPlatformName());//endPlatformName
            row1.createCell(32).setCellValue(item.getInEndPlatformTime());//inEndPlatformTime
            row1.createCell(33).setCellValue(item.getUnloadShipTime());//unloadShipTime
            row1.createCell(34).setCellValue(item.getUnloadRailwayTime());//unloadRailwayTime
            row1.createCell(35).setCellValue(item.getInDistributeTime());//inDistributeTime
            row1.createCell(36).setCellValue(item.getDistributeAssignTime());//distributeAssignTime
            row1.createCell(37).setCellValue(item.getDistributeCarrierName());//distributeCarrierName
            row1.createCell(38).setCellValue(item.getDistributeVehicleNo());//distributeVehicleNo
            row1.createCell(39).setCellValue(item.getDistributeVehicleNum());//distributeVehicleNum
            row1.createCell(40).setCellValue(item.getOutDistributeTime());//outDistributeTime
            row1.createCell(41).setCellValue(item.getDistributeShipmentTime());//distributeShipmentTime
            row1.createCell(42).setCellValue(item.getDotSiteTime());//dotSiteTime
            row1.createCell(43).setCellValue(item.getFinalSiteTime());//finalSiteTime
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
