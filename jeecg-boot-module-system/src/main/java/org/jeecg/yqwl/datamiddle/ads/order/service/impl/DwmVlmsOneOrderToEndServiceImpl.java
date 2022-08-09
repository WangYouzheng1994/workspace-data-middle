package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsOneOrderToEndMapper;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@DS("slave")
@Service
public class DwmVlmsOneOrderToEndServiceImpl extends ServiceImpl<DwmVlmsOneOrderToEndMapper, DwmVlmsOneOrderToEnd> implements IDwmVlmsOneOrderToEndService {

    @Autowired
    private DwmVlmsOneOrderToEndMapper dwmVlmsOneOrderToEndMapper;
    @Autowired
    private DwmVlmsSptb02Mapper dwmVlmsSptb02Mapper;

    @Override
    public Integer countOneOrderToEndList(GetQueryCriteria queryCriteria) {
        Integer count = dwmVlmsOneOrderToEndMapper.countOneOrderToEndList(queryCriteria);

        return count == null ? 0 : count;
    }

    /**
     * 按条件进行分页查询
     *
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
        //遍历list VIN码:list下标
        Map<String, Integer> listMap = new HashMap<>();

        DwmVlmsOneOrderToEnd params = null;
        for (int i = 0; i < oneOrderToEndList.size(); i++) {
            params = oneOrderToEndList.get(i);
            listMap.put(params.getVin(), i);
            // 添加逻辑  如果是时间字段  需要在得到的值进行-8小时处理
            this.formatTime(params);
        }
       if (CollectionUtils.isNotEmpty(oneOrderToEndList)) {
            // 运输方式拼接显示处理。
            ArrayList<String> vinList = oneOrderToEndList.stream().collect(ArrayList::new, (list, item) -> list.add(item.getVin()), ArrayList::addAll);
            if (CollectionUtils.isNotEmpty(vinList)) {
                List<DwmVlmsSptb02> sptbTrafficTypeByVin = this.dwmVlmsSptb02Mapper.getSptbTrafficTypeByVin(vinList);
                // group concat
                // sptbTrafficTypeByVin.stream().collect(groupingBy())
                sptbTrafficTypeByVin.stream().collect(groupingBy(DwmVlmsSptb02::getVvin)).entrySet().stream().forEach(
                    (item) -> {
                        DwmVlmsOneOrderToEnd dbOotd = oneOrderToEndList.get(listMap.get(item.getKey()));
                        final int[] orderIdx = {0};
                        final String[] lastVwz = {""};
                        // List<String> trafficLists = new ArrayList();  DELETE By QingSong for Fix zental: 871
                        item.getValue().stream().forEach(it -> {
                            orderIdx[0]++;
                            // 累计多个运单的运输方式。
                            // trafficLists.add(it.getTrafficType());  DELETE By QingSong for Fix zental: 871
                            // 铁水的物流时间节点兜底处理 -- START By qingsong  2022年7月10日21:14:28
                            // Xxx: 1. 集港/集站时间 用物流溯源时间节点来更新，无法兜底
                            // 铁路单
                            if (StringUtils.equals(it.getTrafficType(), "T")) {
                                // 2. 始发站台离站时间 outStartPlatformTime 用运单的起运时间dsjcfsj
                                if (dbOotd.getOutStartPlatformTime() == 0L && it.getDsjcfsj() != null && it.getDsjcfsj() != 0L) {
                                    dbOotd.setOutStartPlatformTime(it.getDsjcfsj());
                                }
                                // 3. 到达目标站台时间 inEndPlatformTime 用运单的dgpsdhsj
                                if (dbOotd.getInEndPlatformTime() == 0L && it.getDgpsdhsj() != null && it.getDgpsdhsj() != 0L) {
                                    dbOotd.setInEndPlatformTime(it.getDgpsdhsj());
                                }
                            }
                            // 水路单
                            if (StringUtils.equals(it.getTrafficType(), "S")) {
                                // 2. 始发港口离港时间 endStartWaterwayTime用运单的起运时间dsjcfsj
                                if (dbOotd.getEndStartWaterwayTime() == 0L && it.getDsjcfsj() != null && it.getDsjcfsj() != 0L) {
                                    dbOotd.setEndStartWaterwayTime(it.getDsjcfsj());
                                }
                                // 3. 到达目的港时间 inEndWaterwayTime 用运单的dgpsdhsj
                                if (dbOotd.getInEndWaterwayTime() == 0L && it.getDgpsdhsj() != null && it.getDgpsdhsj() != 0L) {
                                    dbOotd.setInEndWaterwayTime(it.getDgpsdhsj());
                                }
                            }
                            // Xxx: 4. 卸船 应该用物流溯源时间节点来更新，无法兜底。
                            // 铁水的物流时间节点处理 -- END  By qingsong

                            // 对于在途位置的处理：根据运单区分顺序。依次取值赋值即可 Add BY QingSong
                            // 如果上一个节点到货那么显示下个节点。
                            if (orderIdx[0] == 1) {
                                // 如果是第一个节点。
                                dbOotd.setVwz(it.getVWZ());
                            } else if (StringUtils.isBlank(lastVwz[0])) {
                                // 如果上一个节点没有信息，那么也要显示本次节点，作为兜底。
                                dbOotd.setVwz(it.getVWZ());
                            } else if (StringUtils.equals(lastVwz[0], "已到货")) {
                                // 如果上一个节点是已到货 才能赋值。
                                dbOotd.setVwz(it.getVWZ());
                            }
                            // 上一个节点 赋值
                            lastVwz[0] = it.getVWZ();
                        });
                        // 运输方式转换
                        // dbOotd.setTrafficType(formatTrafficTypeToChinese(trafficLists)); DELETE By QingSong for Fix zental: 871
                    }
                );
            }
       }
        return oneOrderToEndList;
    }

    /**
     * DOCS count计数
     *
     * @param queryCriteria
     * @return
     */
    @Override
    public Integer countDocsList(GetQueryCriteria queryCriteria) {
        Integer count = dwmVlmsSptb02Mapper.countDocsList(queryCriteria);
        return count ==  null ? 0 : count;
    }

    /**
     * DOCS 列表页查询
     *
     * @param queryCriteria
     * @return
     */
    @Override
    public List<DwmVlmsDocs> selectDocsList(GetQueryCriteria queryCriteria) {
        if (queryCriteria.getPageNo() != null) {
            queryCriteria.setLimitStart((queryCriteria.getPageNo() - 1) * queryCriteria.getPageSize());
            queryCriteria.setLimitEnd(queryCriteria.getPageSize());
        }
        List<DwmVlmsDocs> dwmVlmsDocs = dwmVlmsSptb02Mapper.selectDocsList(queryCriteria);

        Map<String, Integer> listMap = new HashMap<>();
        DwmVlmsDocs params = null;
        for (int i = 0; i < dwmVlmsDocs.size(); i++) {
            params = dwmVlmsDocs.get(i);
            listMap.put(params.getVvin(), i);
//            this.docsFormatTime(params);
        }
        return dwmVlmsDocs;
    }

    /**
     * docs车型列表页计数
     * @param queryCriteria
     * @return
     */
    @Override
    public Integer countDocsCcxdlList(GetQueryCriteria queryCriteria) {
        Integer num = dwmVlmsSptb02Mapper.countDocsCcxdlList(queryCriteria);
        return num == null ? 0 : num ;
    }

    /**
     * docs车型列表页查询
     * @param queryCriteria
     * @return
     */
    @Override
    public List<DwmVlmsDocs> selectDocsCcxdlList(GetQueryCriteria queryCriteria) {
        if (queryCriteria.getPageNo() != null) {
            queryCriteria.setLimitStart((queryCriteria.getPageNo() - 1) * queryCriteria.getPageSize());
            queryCriteria.setLimitEnd(queryCriteria.getPageSize());
        }
        List<DwmVlmsDocs> dwmVlmsDocs = dwmVlmsSptb02Mapper.selectDocsCcxdlList(queryCriteria);
        Map<String, Integer> listMap = new HashMap<>();
        DwmVlmsDocs params = null;
        for (int i = 0; i < dwmVlmsDocs.size(); i++) {
            params = dwmVlmsDocs.get(i);
            listMap.put(params.getVvin(), i);
//            this.docsFormatTime(params);
        }
        return dwmVlmsDocs;
    }

    /**
     * 英文运输方式
     *
     * @param engTrafficLists
     * @return
     */
    private String formatTrafficTypeToChinese(List<String> engTrafficLists) {
        if (CollectionUtils.isNotEmpty(engTrafficLists)) {
            String eng = null;
            for (int i = 0; i < engTrafficLists.size(); i++) {
                eng = engTrafficLists.get(i);
                if (StringUtils.isNotBlank(eng)) {
                    switch (eng) {
                        case "G" : engTrafficLists.set(i, "公");break;
                        case "T" : engTrafficLists.set(i, "铁");break;
                        case "S" : engTrafficLists.set(i, "水");break;
                        default: engTrafficLists.set(i, "未知");break;
                    }
                }
            }
            return StringUtils.join(engTrafficLists, "，");
        } else {
            return "";
        }
    }

    /**
     * 格式化16位微秒时间戳为13位毫秒时间戳
     *
     * @param params
     */
    private void formatMicros2Millis(DwmVlmsOneOrderToEnd params) {
        // 出厂日期
        if (params.getLeaveFactoryTime() != null && StringUtils.length(params.getLeaveFactoryTime() + "") == 16) {
            params.setLeaveFactoryTime(params.getLeaveFactoryTime() / 1000);
        }
        // 入库日期
        if (params.getInSiteTime() != null && StringUtils.length(params.getInSiteTime() + "") == 16) {
            params.setInSiteTime(params.getInSiteTime() / 1000);
        }
        // 入开始站台时间
//        if (params.getInStartPlatformTime() != null) {
//            params.setInStartPlatformTime(params.getInStartPlatformTime() / 1000);
//        }
//        // 出开始站台时间，需要判定 兜底赋值为13位的情况。兜底：dsjcfsj
//        if (params.getOutStartPlatformTime() != null && StringUtils.length(params.getOutStartPlatformTime() + "") == 16) {
//            params.setOutStartPlatformTime(params.getOutStartPlatformTime() / 1000);
//        }
//        // 入目标站台时间，需要判定 兜底赋值为13位的情况。兜底：gps到货时间
//        if (params.getInEndPlatformTime() != null && StringUtils.length(params.getInEndPlatformTime() + "") == 16) {
//            params.setInEndPlatformTime(params.getInEndPlatformTime() / 1000);
//        }
//        // 铁路卸车时间，需要判定 兜底赋值为13位的情况。兜底：DZTXCSJ
//        if (params.getUnloadRailwayTime() != null && StringUtils.length(params.getUnloadRailwayTime() + "") == 16) {
//            params.setUnloadRailwayTime(params.getUnloadRailwayTime() / 1000);
//        }
//        // 入开始港口时间
//        if (params.getInStartWaterwayTime() != null) {
//            params.setInStartWaterwayTime(params.getInStartWaterwayTime() / 1000);
//        }
//        // 出开始港口时间，需要判定 兜底赋值为13位的情况。兜底：dsjcfsj
//        if (params.getEndStartWaterwayTime() != null && StringUtils.length(params.getEndStartWaterwayTime() + "") == 16) {
//            params.setEndStartWaterwayTime(params.getEndStartWaterwayTime() / 1000);
//        }
//        // 入目标港口时间，需要判定 兜底赋值为13位的情况。兜底：gps到货时间
//        if (params.getInEndWaterwayTime() != null && StringUtils.length(params.getInEndWaterwayTime() + "") == 16) {
//            params.setInEndWaterwayTime(params.getInEndWaterwayTime() / 1000);
//        }
//        // 水路卸船时间
//        if (params.getUnloadShipTime() != null) {
//            params.setUnloadShipTime(params.getUnloadShipTime() / 1000);
//        }
        // 入末端分拨库时间
        if (params.getInDistributeTime() != null && StringUtils.length(params.getInDistributeTime() + "") == 16) {
            params.setInDistributeTime(params.getInDistributeTime() / 1000);
        }
    }

    /**
     * 时间数据 从数据库里面查出来以后减掉八小时 60 * 60 * 8 * 1000 = 28800000 毫秒
     *
     * @param params rows of db
     */
    private void formatTime(DwmVlmsOneOrderToEnd params) {
        // 16位 convert to 13位 注销时间处理  2022.8.4
         formatMicros2Millis(params);

        if (params.getCp9OfflineTime() != 0) {
            Long cp9OfflineTime = params.getCp9OfflineTime() - 28800000L;
            params.setCp9OfflineTime(cp9OfflineTime);
        }

        //leaveFactoryTime
//        if (params.getLeaveFactoryTime() != 0) {
//            Long leaveFactoryTime = params.getLeaveFactoryTime() - 28800000L;
//            params.setLeaveFactoryTime(leaveFactoryTime);
//        }
//        //inSiteTime
//        if (params.getInSiteTime() != 0) {
//            Long inSiteTime = params.getInSiteTime() - 28800000L;
//            params.setInSiteTime(inSiteTime);
//        }
//        //vehicleReceivingTime
//        if (params.getVehicleReceivingTime() != 0) {
//            Long vehicleReceivingTime = params.getVehicleReceivingTime() - 28800000L;
//            params.setVehicleReceivingTime(vehicleReceivingTime);
//        }
//        //stowageNoteTime
////        if (params.getStowageNoteTime() != 0) {
////            Long stowageNoteTime = params.getStowageNoteTime() - 28800000L;
////            params.setStowageNoteTime(stowageNoteTime);
////        }
//        //assignTime
//        if (params.getAssignTime() != 0) {
//            Long assignTime = params.getAssignTime() - 28800000L;
//            params.setAssignTime(assignTime);
//        }
//        //actualOutTime
//        if (params.getActualOutTime() != 0) {
//            Long actualOutTime = params.getActualOutTime() - 28800000L;
//            params.setActualOutTime(actualOutTime);
//        }
//        //shipmentTime
////        if (params.getShipmentTime() != 0) {
////            Long shipmentTime = params.getShipmentTime() - 28800000L;
////            params.setShipmentTime(shipmentTime);
////        }
//        // shipmentGTime
//        if (params.getShipmentGTime() != 0) {
//            Long ShipmentGTime = params.getShipmentGTime() - 28800000L;
//            params.setShipmentGTime(ShipmentGTime);
//        }
//        //inStartWaterwayTime,
//        if (params.getInStartWaterwayTime() != 0) {
//            Long inStartWaterwayTime = params.getInStartWaterwayTime() - 28800000L;
//            params.setInStartWaterwayTime(inStartWaterwayTime);
//        }
//        // endStartWaterwayTime
//        if (params.getEndStartWaterwayTime() != 0) {
//            Long endStartWaterwayTime = params.getEndStartWaterwayTime() - 28800000L;
//            params.setEndStartWaterwayTime(endStartWaterwayTime);
//        }
//        //inEndWaterwayTime
//        if (params.getInEndWaterwayTime() != 0) {
//            Long inEndWaterwayTime = params.getInEndWaterwayTime() - 28800000L;
//            params.setInEndWaterwayTime(inEndWaterwayTime);
//        }
//        // inStartPlatformTime
//        if (params.getInStartPlatformTime() != 0) {
//            Long inStartPlatformTime = params.getInStartPlatformTime() - 28800000L;
//            params.setInStartPlatformTime(inStartPlatformTime);
//        }
//        // outStartPlatformTime
//        if (params.getOutStartPlatformTime() != 0) {
//            Long outStartPlatformTime = params.getOutStartPlatformTime() - 28800000L;
//            params.setOutStartPlatformTime(outStartPlatformTime);
//        }
//        // inEndPlatformTime,
//        if (params.getInEndPlatformTime() != 0) {
//            Long inEndPlatformTime = params.getInEndPlatformTime() - 28800000L;
//            params.setInEndPlatformTime(inEndPlatformTime);
//        }
//        // unloadShipTime,
//        if (params.getUnloadShipTime() != 0) {
//            Long unloadShipTime = params.getUnloadShipTime() - 28800000L;
//            params.setUnloadShipTime(unloadShipTime);
//        }
//        // unloadRailwayTime,
//        if (params.getUnloadRailwayTime() != 0) {
//            Long unloadRailwayTime = params.getUnloadRailwayTime() - 28800000L;
//            params.setUnloadRailwayTime(unloadRailwayTime);
//        }
//        // inDistributeTime,
//        if (params.getInDistributeTime() != 0) {
//            Long inDistributeTime = params.getInDistributeTime() - 28800000L;
//            params.setInDistributeTime(inDistributeTime);
//        }
//        // distributeAssignTime
//        if (params.getDistributeAssignTime() != 0) {
//            Long distributeAssignTime = params.getDistributeAssignTime() - 28800000L;
//            params.setDistributeAssignTime(distributeAssignTime);
//        }
//        //outDistributeTime
//        if (params.getOutDistributeTime() != 0) {
//            Long outDistributeTime = params.getOutDistributeTime() - 28800000L;
//            params.setOutDistributeTime(outDistributeTime);
//        }
//        // distributeShipmentTime,
//        if (params.getDistributeShipmentTime() != 0) {
//            Long distributeShipmentTime = params.getDistributeShipmentTime() - 28800000L;
//            params.setDistributeShipmentTime(distributeShipmentTime);
//        }
//        // dotSiteTime,
////        if (params.getDotSiteTime() != 0) {
////            Long dotSiteTime = params.getDotSiteTime() - 28800000L;
////            params.setDotSiteTime(dotSiteTime);
////        }
        // dotSiteTime,
        if (params.getDtvsdhsj() != 0) {
            Long dtvsdhsj = params.getDtvsdhsj() - 28800000L;
            params.setDtvsdhsj(dtvsdhsj);
        }
//        // finalSiteTime
//        if (params.getFinalSiteTime() != 0) {
//            Long finalSiteTime = params.getFinalSiteTime() - 28800000L;
//            params.setFinalSiteTime(finalSiteTime);
//        }
    }

    /**
     * docs表时间减8小时(六个字段)
     * @param params
     */
    private void docsFormatTime(DwmVlmsDocs params){
        // ddjrq
        if (params.getDdjrq() != 0) {
            Long ddjrq = params.getDdjrq() - 28800000L;
            params.setDdjrq(ddjrq);
        }
        // assignTime
        if (params.getAssignTime() != 0) {
            Long assignTime = params.getAssignTime() - 28800000L;
            params.setAssignTime(assignTime);
        }
        // shipmentTime
        if (params.getShipmentTime() != 0) {
            Long shipmentTime = params.getShipmentTime() - 28800000L;
            params.setShipmentTime(shipmentTime);
        }
        // actualOutTime
        if (params.getActualOutTime() != 0) {
            Long actualOutTime = params.getActualOutTime() - 28800000L;
            params.setActualOutTime(actualOutTime);
        }
        // dotSiteTime,
        if (params.getDtvsdhsj() != 0) {
            Long dtvsdhsj = params.getDtvsdhsj() - 28800000L;
            params.setDtvsdhsj(dtvsdhsj);
        }
        // finalSiteTime
        if (params.getFinalSiteTime() != 0) {
            Long finalSiteTime = params.getFinalSiteTime() - 28800000L;
            params.setFinalSiteTime(finalSiteTime);
        }
    }


}
