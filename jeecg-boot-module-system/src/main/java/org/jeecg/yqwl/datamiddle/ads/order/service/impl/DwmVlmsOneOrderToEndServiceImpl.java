package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.jeecg.yqwl.datamiddle.ads.order.vo.VvinGroupQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@DS("slave")
@Service
public class DwmVlmsOneOrderToEndServiceImpl extends ServiceImpl<DwmVlmsOneOrderToEndMapper, DwmVlmsOneOrderToEnd> implements IDwmVlmsOneOrderToEndService {

    @Autowired
    private DwmVlmsOneOrderToEndMapper dwmVlmsOneOrderToEndMapper;
    @Autowired
    private DwmVlmsSptb02Mapper dwmVlmsSptb02Mapper;

    @Value("${jeecg.shardsNumber}")
    private Integer shardsNumber;


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
//            this.formatTime(params);
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
        return count == null ? 0 : count;
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

//        Map<String, Integer> listMap = new HashMap<>();
//        DwmVlmsDocs params = null;
//        for (int i = 0; i < dwmVlmsDocs.size(); i++) {
//            params = dwmVlmsDocs.get(i);
//            listMap.put(params.getVvin(), i);
//            this.docsFormatTime(params);
//        }
        return dwmVlmsDocs;
    }



    @Override
    public Page<DwmVlmsDocs> selectDocsPage(GetQueryCriteria queryCriteria) {
        if (queryCriteria.getPageNo() != null) {
            queryCriteria.setLimitStart((queryCriteria.getPageNo() - 1) * queryCriteria.getPageSize());
            queryCriteria.setLimitEnd(queryCriteria.getPageSize());
        }
        Page<DwmVlmsDocs> page = new Page(queryCriteria.getPageNo(), queryCriteria.getPageSize());
        //存放总数量
        Integer finalTotal = 0;
        int vvinSize = 0;
        //判断vin码的数量如果超过一定数量，分批查询
        if (CollectionUtils.isNotEmpty(queryCriteria.getVvinList())) {
            vvinSize = queryCriteria.getVvinList().size();
        }
        if (vvinSize > shardsNumber) {
            //计算需要分几组
            BigDecimal vvinDecimal = BigDecimal.valueOf(vvinSize);
            BigDecimal numberDecimal = BigDecimal.valueOf(shardsNumber);
            //结果要向上取整
            int count = vvinDecimal.divide(numberDecimal, 0, BigDecimal.ROUND_UP).intValue();
            //存放分组返回数量以及分组查询的vin
            List<VvinGroupQuery> vvinGroup = new ArrayList<>();
            //开始处理
            for (int i = 1; i <= count; i++) {
                //数组截取开始下标
                int startIndex = numberDecimal.multiply(BigDecimal.valueOf(i - 1)).intValue();
                //数组截取结束下标
                int endIndex = numberDecimal.multiply(BigDecimal.valueOf(i)).intValue();
                if (endIndex > vvinSize) {
                    endIndex = vvinSize;
                }
                List<String> newVinList = queryCriteria.getVvinList().subList(startIndex, endIndex);
                GetQueryCriteria newQuery = new GetQueryCriteria();
                BeanUtils.copyProperties(queryCriteria, newQuery);
                newQuery.setVvinList(newVinList);
                Integer total = countDocsList(newQuery);
                //将本次查询数据存入到list
                VvinGroupQuery vvinGroupQuery = new VvinGroupQuery();
                vvinGroupQuery.setDataCount(total);
                vvinGroupQuery.setVvinList(newVinList);
                vvinGroup.add(vvinGroupQuery);
            }
            //重新构造查询
            List<DwmVlmsDocs> vlmsDocs = buildNewQuery(queryCriteria, vvinGroup);
            //总数
            finalTotal = vvinGroup.stream().map(VvinGroupQuery::getDataCount).reduce(0, (n1, n2) -> n1 + n2);
            page.setRecords(vlmsDocs);
            page.setTotal(finalTotal);
            return page;
        }
        //正常情况处理
        finalTotal = countDocsList(queryCriteria);
        List<DwmVlmsDocs> dwmVlmsDocs = dwmVlmsSptb02Mapper.selectDocsList(queryCriteria);
        page.setRecords(dwmVlmsDocs);
        page.setTotal(finalTotal);

        return page;
    }

    private List<DwmVlmsDocs> buildNewQuery(GetQueryCriteria queryCriteria, List<VvinGroupQuery> vvinGroupQueries) {
        List<DwmVlmsDocs> dwmVlmsDocsList = new ArrayList<>();
        //计算所需要的数据位置
        BigDecimal pageNo = BigDecimal.valueOf(queryCriteria.getPageNo());
        BigDecimal pageSize = BigDecimal.valueOf(queryCriteria.getPageSize());
        BigDecimal startCount = pageNo.subtract(BigDecimal.ONE).multiply(pageSize).setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal endCount = startCount.add(pageSize);
        //设置数组的基数
        AtomicInteger cardinality = new AtomicInteger(0);
        for (VvinGroupQuery item : vvinGroupQueries) {
            //列表内最后一个数据的总量
            int dataCount = cardinality.get() + item.getDataCount();
            //判断是否符合条件的数据
            if (startCount.intValue() >= cardinality.get() && startCount.intValue() <= dataCount) {
                //计算开始
                int limitStart = startCount.intValue() - cardinality.get();

                GetQueryCriteria newQuery = new GetQueryCriteria();
                BeanUtils.copyProperties(queryCriteria, newQuery);
                newQuery.setVvinList(item.getVvinList());
                newQuery.setLimitStart(limitStart);
                //判断数组末尾数据大小是否大于所需数据末尾大小
                int margin = dataCount - startCount.intValue();
                if (margin < queryCriteria.getPageSize()) {
                    newQuery.setLimitEnd(margin);
                    dwmVlmsDocsList.addAll(dwmVlmsSptb02Mapper.selectDocsList(newQuery));

                    cardinality.addAndGet(item.getDataCount());
                    continue;
                } else {
                    dwmVlmsDocsList.addAll(dwmVlmsSptb02Mapper.selectDocsList(newQuery));
                    return dwmVlmsDocsList;
                }
            }
            if (startCount.intValue() < cardinality.get() && dataCount < endCount.intValue()) {
                GetQueryCriteria newQuery = new GetQueryCriteria();
                BeanUtils.copyProperties(queryCriteria, newQuery);
                newQuery.setVvinList(item.getVvinList());
                newQuery.setLimitStart(cardinality.get());
                newQuery.setLimitEnd(dataCount);
                dwmVlmsDocsList.addAll(dwmVlmsSptb02Mapper.selectDocsList(newQuery));

                cardinality.addAndGet(item.getDataCount());
                continue;
            }
            if (endCount.intValue() >= cardinality.get() && endCount.intValue() <= dataCount) {
                //处理上次循环中遗留的数据
                GetQueryCriteria newQuery = new GetQueryCriteria();
                BeanUtils.copyProperties(queryCriteria, newQuery);
                newQuery.setVvinList(item.getVvinList());
                newQuery.setLimitStart(0);
                newQuery.setLimitEnd(endCount.intValue() - cardinality.get());
                dwmVlmsDocsList.addAll(dwmVlmsSptb02Mapper.selectDocsList(newQuery));
                return dwmVlmsDocsList;
            }
            cardinality.addAndGet(item.getDataCount());
        }

        return dwmVlmsDocsList;
    }


    /**
     * docs车型列表页计数
     *
     * @param queryCriteria
     * @return
     */
    @Override
    public Integer countDocsCcxdlList(GetQueryCriteria queryCriteria) {
        Integer num = dwmVlmsSptb02Mapper.countDocsCcxdlList(queryCriteria);
        return num == null ? 0 : num;
    }

    /**
     * docs车型列表页查询
     *
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
     * 按照Vin码去查询总数
     *
     * @param vvin
     * @return
     */
    @Override
    public Integer countClickhouseVin(String vvin) {
        Integer integer = dwmVlmsOneOrderToEndMapper.countClickhouseVin(vvin);
        return integer;
    }

    @Override
    public List<String> getOneOrderToEndVin(List<String> vvin) {
        List<String> oneOrderToEndVin = dwmVlmsOneOrderToEndMapper.getOneOrderToEndVin(vvin);
        return oneOrderToEndVin;
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
                        case "G":
                            engTrafficLists.set(i, "公");
                            break;
                        case "T":
                            engTrafficLists.set(i, "铁");
                            break;
                        case "S":
                            engTrafficLists.set(i, "水");
                            break;
                        default:
                            engTrafficLists.set(i, "未知");
                            break;
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
        // 出厂日期  此字段目前在一单到底的页面中已经删除(雨落)
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
     * docs表时间减8小时(六个字段)
     *
     * @param params
     */
    private void docsFormatTime(DwmVlmsDocs params) {
        // ddjrq
        //if (params.getDdjrq() != 0) {
        //    Long ddjrq = params.getDdjrq() - 28800000L;
        //    params.setDdjrq(ddjrq);
        //}
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
