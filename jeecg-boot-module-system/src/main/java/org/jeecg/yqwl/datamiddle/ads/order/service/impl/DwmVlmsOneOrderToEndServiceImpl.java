package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsOneOrderToEndMapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@DS("slave")
@Service
public class DwmVlmsOneOrderToEndServiceImpl extends ServiceImpl<DwmVlmsOneOrderToEndMapper, DwmVlmsOneOrderToEnd> implements IDwmVlmsOneOrderToEndService {

    @Resource
    private DwmVlmsOneOrderToEndMapper dwmVlmsOneOrderToEndMapper;

    @Override
    public Integer countOneOrderToEndList(GetQueryCriteria queryCriteria) {
        Integer count = dwmVlmsOneOrderToEndMapper.countOneOrderToEndList(queryCriteria);

        return count == null ? 0 : count;
    }

    /**
     * 按条件进行分页查询
     * @param queryCriteria
     * @return
     */
    @Override
    public List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(GetQueryCriteria queryCriteria) {

        if (queryCriteria.getPageNo() != null) {
            queryCriteria.setLimitStart((queryCriteria.getPageNo() - 1) * queryCriteria.getPageSize());
            queryCriteria.setLimitEnd(queryCriteria.getPageSize());
        }
        List<DwmVlmsOneOrderToEnd> oneOrderToEndList = dwmVlmsOneOrderToEndMapper.selectOneOrderToEndList(queryCriteria);
        //遍历list,并查询出同板数量赋值
        Map<String, Integer> samePlateNumMap = new HashMap<>();

        for ( int i = 0; i < oneOrderToEndList.size(); i ++ ) {
            DwmVlmsOneOrderToEnd params = oneOrderToEndList.get(i);
            // 添加逻辑  如果是时间字段  需要在得到的值进行-8小时处理
            // cp9OfflineTime,leaveFactoryTime,inSiteTime,inWarehouseName,taskNo,vehicleReceivingTime,   4
            // stowageNoteTime,stowageNoteNo,trafficType,assignTime,carrierName,actualOutTime,shipmentTime,transportVehicleNo,samePlateNum,   4
            // vehicleNum,startCityName,endCityName,vdwdm,startWaterwayName,inStartWaterwayTime,endStartWaterwayTime,endWaterwayName,  2
            // inEndWaterwayTime,startPlatformName,inStartPlatformTime,outStartPlatformTime,endPlatformName,inEndPlatformTime,unloadShipTime,   5
            // unloadRailwayTime,inDistributeTime,distributeAssignTime,distributeCarrierName,distributeVehicleNo,distributeVehicleNum,outDistributeTime,  4
            // distributeShipmentTime,dotSiteTime,finalSiteTime    3   共22个时间字段
//            Long second = 8 * 60 * 60 * 1000L;
            this.formatTime(params);

            //查询配载单编号
            String stowageNoteNo = params.getStowageNoteNo();
            //计算同板数量
            if (StringUtils.isNotBlank(stowageNoteNo)) {
//                if (samePlateNumMap.containsKey(stowageNoteNo)) {
//                    params.setSamePlateNum(samePlateNumMap.get(stowageNoteNo));
//                } else {
//                    List<SelectData> samePlateNumList = dwmVlmsOneOrderToEndMapper.selectTotal(stowageNoteNo);
//                    if ( CollectionUtils.isNotEmpty(samePlateNumList) ) {
//                        samePlateNumMap.put(stowageNoteNo, samePlateNumList.get(0).getSamePlateNum());
//                        params.setSamePlateNum(samePlateNumList.get(0).getSamePlateNum());
//                    }
//                }
            }
        }
        return oneOrderToEndList;
    }

    /**
     * 时间数据 从数据库里面查出来以后减掉八小时
     *
     * @param params rows of db
     */
    private void formatTime(DwmVlmsOneOrderToEnd params) {
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

//    /**
//     * 查询同板数量
//     * @return
//     */
//    @Override
//    public List<SelectData> selectTotal(String stowageNoteNo) {
////        List<SelectData> total = dwmVlmsOneOrderToEndMapper.selectTotal();
//        return null;
//    }
}
