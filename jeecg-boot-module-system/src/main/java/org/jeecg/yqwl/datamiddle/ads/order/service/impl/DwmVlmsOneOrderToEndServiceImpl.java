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
import java.util.Date;
import java.util.List;


/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */

@Slf4j
@DS("slave_1")
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
//        System.out.println("减8小时操作开始:" + new Date().getTime());
        //遍历list,并查询出同板数量赋值
        for ( int i = 0; i < oneOrderToEndList.size(); i ++ ) {
            DwmVlmsOneOrderToEnd params = oneOrderToEndList.get(i);
            //TODO:添加逻辑  如果是时间字段  需要在得到的值进行-8小时处理
            //cp9OfflineTime,leaveFactoryTime,inSiteTime,inWarehouseName,taskNo,vehicleReceivingTime,   4
            // stowageNoteTime,stowageNoteNo,trafficType,assignTime,carrierName,actualOutTime,shipmentTime,transportVehicleNo,samePlateNum,   4
            // vehicleNum,startCityName,endCityName,vdwdm,startWaterwayName,inStartWaterwayTime,endStartWaterwayTime,endWaterwayName,  2
            // inEndWaterwayTime,startPlatformName,inStartPlatformTime,outStartPlatformTime,endPlatformName,inEndPlatformTime,unloadShipTime,   5
            // unloadRailwayTime,inDistributeTime,distributeAssignTime,distributeCarrierName,distributeVehicleNo,distributeVehicleNum,outDistributeTime,  4
            // distributeShipmentTime,dotSiteTime,finalSiteTime    3   共22个时间字段
//            Long second = 8 * 60 * 60 * 1000L;
            //将字段转换成String类型
            //cp9OfflineTime
            if ( params.getCp9OfflineTime() != 0 ) {
                 Long cp9OfflineTime = params.getCp9OfflineTime() - 28800000L;
                params.setCp9OfflineTime(cp9OfflineTime);
            }

            //leaveFactoryTime
            if ( params.getLeaveFactoryTime() != 0) {
                 Long leaveFactoryTime = params.getLeaveFactoryTime() - 28800000L;
                 params.setLeaveFactoryTime(leaveFactoryTime);
            }
            //inSiteTime
            if ( params.getInSiteTime() != 0) {
                Long inSiteTime = params.getInSiteTime() - 28800000L;
                params.setInSiteTime(inSiteTime);
            }
            //vehicleReceivingTime
            if ( params.getVehicleReceivingTime() != 0) {
                Long vehicleReceivingTime = params.getVehicleReceivingTime() - 28800000L;
                params.setVehicleReceivingTime(vehicleReceivingTime);
            }
            //stowageNoteTime
            if ( params.getStowageNoteTime() != 0) {
                Long stowageNoteTime = params.getStowageNoteTime() - 28800000L;
                params.setStowageNoteTime(stowageNoteTime);
            }
            //assignTime
            if (params.getAssignTime() != 0) {
                Long assignTime = params.getAssignTime() - 28800000L;
                params.setAssignTime(assignTime);
            }
            //actualOutTime
            if ( params.getActualOutTime() != 0) {
                Long actualOutTime = params.getActualOutTime() - 28800000L;
                params.setActualOutTime(actualOutTime);
            }
            //shipmentTime
            if (params.getShipmentTime() != 0) {
                Long shipmentTime = params.getShipmentTime() - 28800000L;
                params.setShipmentTime(shipmentTime);
            }
            //inStartWaterwayTime,
            if ( params.getInStartWaterwayTime() != 0 ) {
                Long inStartWaterwayTime = params.getInStartWaterwayTime() - 28800000L;
                params.setInStartWaterwayTime(inStartWaterwayTime);
            }
            // endStartWaterwayTime
            if ( params.getEndStartWaterwayTime() != 0) {
                Long endStartWaterwayTime = params.getEndStartWaterwayTime() - 28800000L;
                params.setEndStartWaterwayTime(endStartWaterwayTime);
            }
            //inEndWaterwayTime
            if ( params.getInEndWaterwayTime() != 0) {
                Long inEndWaterwayTime = params.getInEndWaterwayTime() - 28800000L;
                params.setInEndWaterwayTime(inEndWaterwayTime);
            }
            // inStartPlatformTime
            if ( params.getInStartPlatformTime() != 0 ) {
                Long inStartPlatformTime = params.getInStartPlatformTime() - 28800000L;
                params.setInStartPlatformTime(inStartPlatformTime);
            }
            // outStartPlatformTime
            if ( params.getOutStartPlatformTime() != 0) {
                Long outStartPlatformTime = params.getOutStartPlatformTime() - 28800000L;
                params.setOutStartPlatformTime(outStartPlatformTime);
            }
            // inEndPlatformTime,
            if ( params.getInEndPlatformTime() != 0) {
                Long inEndPlatformTime = params.getInEndPlatformTime() - 28800000L;
                params.setInEndPlatformTime(inEndPlatformTime);
            }
            // unloadShipTime,
            if ( params.getUnloadShipTime() != 0) {
                Long unloadShipTime = params.getUnloadShipTime() - 28800000L;
                params.setUnloadShipTime(unloadShipTime);
            }
            // unloadRailwayTime,
            if ( params.getUnloadRailwayTime() != 0) {
                Long unloadRailwayTime = params.getUnloadRailwayTime() - 28800000L;
                params.setUnloadRailwayTime(unloadRailwayTime);
            }
            // inDistributeTime,
            if ( params.getInDistributeTime() != 0 ) {
                Long inDistributeTime = params.getInDistributeTime() - 28800000L;
                params.setInDistributeTime(inDistributeTime);
            }
            // distributeAssignTime
            if ( params.getDistributeAssignTime() != 0 ) {
                Long distributeAssignTime = params.getDistributeAssignTime() - 28800000L;
                params.setDistributeAssignTime(distributeAssignTime);
            }
            //outDistributeTime
            if ( params.getOutDistributeTime() != 0 ) {
                Long outDistributeTime = params.getOutDistributeTime() - 28800000L;
                params.setOutDistributeTime(outDistributeTime);
            }
            // distributeShipmentTime,
            if ( params.getDistributeShipmentTime() != 0) {
                Long distributeShipmentTime = params.getDistributeShipmentTime() - 28800000L;
                params.setDistributeShipmentTime(distributeShipmentTime);
            }
            // dotSiteTime,
            if ( params.getDotSiteTime() != 0) {
                Long dotSiteTime = params.getDotSiteTime() - 28800000L;
                params.setDotSiteTime(dotSiteTime);
            }
            // finalSiteTime
            if ( params.getFinalSiteTime() != 0) {
                Long finalSiteTime = params.getFinalSiteTime() - 28800000L;
                params.setFinalSiteTime(finalSiteTime);
            }
        }
//        System.out.println("减8小时操作结束:" + new Date().getTime());
//        System.out.println("开始计算同板数量:" + new Date().getTime());
//        //TODO:计算同板数量
//        for ( int k = 0; k < oneOrderToEndList.size(); k++ ) {
//            DwmVlmsOneOrderToEnd params = oneOrderToEndList.get(k);
//            //获取配载单编号的值
//            String stowageNoteNo = params.getStowageNoteNo();
//            //查询所有的配载单编号和同板数量
//            List<SelectData> samePlateNumList = dwmVlmsOneOrderToEndMapper.selectTotal();
//            //对得到的结果进行循环
//            for ( int j = 0; j < samePlateNumList.size(); j++ ) {
//                //获取配载单编号
//                String noteNo = samePlateNumList.get(j).getStowageNoteNo();
//                Integer samePlateNum = samePlateNumList.get(j).getSamePlateNum();
//                //添加配载单编号不为空,配载单编号为空不计算
//                if ( StringUtils.isNotEmpty(stowageNoteNo) && StringUtils.isNotEmpty(noteNo) ) {
//                    //判断配载单编号的值相同,则设置同板数量
//                    if ( stowageNoteNo.equals(noteNo)) {
//                        params.setSamePlateNum(samePlateNum);
//                    }
//                }
//            }
//        }
//
//        System.out.println("同板数量计算结束:" + new Date().getTime());
        return page.setRecords(oneOrderToEndList);
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
