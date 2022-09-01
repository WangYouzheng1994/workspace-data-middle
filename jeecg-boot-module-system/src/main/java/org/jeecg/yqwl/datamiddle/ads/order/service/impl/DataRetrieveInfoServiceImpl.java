package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveDetail;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveInfo;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.service.DataRetrieveDetailService;
import org.jeecg.yqwl.datamiddle.ads.order.service.DataRetrieveInfoService;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DataRetrieveInfoMapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IOracleSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DataRetrieveQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【data_retrieve_info(每日检索数据信息表)】的数据库操作Service实现
 * @createDate 2022-08-29 13:53:06
 */
@Service
@DS("wareHouse")
public class DataRetrieveInfoServiceImpl extends ServiceImpl<DataRetrieveInfoMapper, DataRetrieveInfo>
        implements DataRetrieveInfoService {

    @Autowired
    private DataRetrieveDetailService dataRetrieveDetailService;

    @Autowired
    private IOracleSptb02Service oracleSptb02Service;

    /**
     * 保存数据检索的结果
     *
     * @param dataRetrieveInfo 检索信息主表
     * @param vinMap           本库异常信息数据的vin码
     * @param vinMapFromOracle 源库异常信息数据的vin码
     * @author dabao
     * @date 2022/8/29
     */
    @Override
    public void saveInfo(DataRetrieveInfo dataRetrieveInfo, Map<Integer, List<String>> vinMap, Map<Integer, List<String>> vinMapFromOracle) {
        List<DataRetrieveDetail> details = new ArrayList<>();
        List<DataRetrieveInfo> infoList = new ArrayList<>();
        vinMap.forEach((key, value) -> {
            //取时间戳作为唯一编码与详情关联
            String code = dataRetrieveInfo.getRetrieveTime() + "_" + key;
            DataRetrieveInfo info = new DataRetrieveInfo();
            info.setRetrieveRange(dataRetrieveInfo.getRetrieveRange());
            info.setRetrieveTime(dataRetrieveInfo.getRetrieveTime());
            info.setCode(code);
            info.setAbnormalCountSelf(value.size());
            info.setAbnormalCountOrigin(CollectionUtils.isNotEmpty(vinMapFromOracle.get(key)) ? vinMapFromOracle.get(key).size() : 0);
            info.setType(key);
            infoList.add(info);
            //判断详情信息是否空并存入list
            buildDataRetrieveDetail(value, details, code, 0);
            if (CollectionUtils.isNotEmpty(vinMapFromOracle.get(key))) {
                buildDataRetrieveDetail(vinMapFromOracle.get(key), details, code, 1);
            }

        });


        //批量处理详情
        dataRetrieveDetailService.saveBatch(details);
        //保存主表信息
        saveBatch(infoList);
    }

    /**
     * 根据map构建details
     *
     * @param vinList  参数
     * @param details  存放对象的list
     * @param code     关联主表的编码
     * @param isSource 是否为源库 0本库，1源库
     * @author dabao
     * @date 2022/8/30
     */
    private void buildDataRetrieveDetail(List<String> vinList, List<DataRetrieveDetail> details, String code, Integer isSource) {
        //判断详情信息是否空
        if (CollectionUtils.isNotEmpty(vinList)) {
            vinList.forEach(item -> {
                DataRetrieveDetail dataRetrieveDetail = new DataRetrieveDetail();
                dataRetrieveDetail.setInfoCode(code);
                dataRetrieveDetail.setVin(item);
                dataRetrieveDetail.setSource(isSource);
                details.add(dataRetrieveDetail);
            });
        }
    }


    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return {@link Page<DataRetrieveInfo>}
     * @author dabao
     * @date 2022/8/30
     */
    @Override
    public Page<DataRetrieveInfo> selectDataRetrieveInfoPage(DataRetrieveQuery query) {
        Page<DataRetrieveInfo> page = new Page(query.getPageNo(), query.getPageSize());
        Integer total = selectCountByCount(query);
        createLimitData(query);
        List<DataRetrieveInfo> dataRetrieveInfos = selectDataRetrieveInfoList(query);
        page.setRecords(dataRetrieveInfos);
        page.setTotal(total.longValue());
        return page;
    }

    private List<DataRetrieveInfo> selectDataRetrieveInfoList(DataRetrieveQuery query) {
        LambdaQueryWrapper<DataRetrieveInfo> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件

        queryWrapper.orderByDesc(DataRetrieveInfo::getRetrieveTime);
        if (Objects.nonNull(query.getLimitStart()) && Objects.nonNull(query.getLimitEnd())) {
            queryWrapper.last(" limit " + query.getLimitStart() + "," + query.getLimitEnd());
        }
        return baseMapper.selectList(queryWrapper);
    }

    private void createLimitData(DataRetrieveQuery query) {
        if (query.getPageNo() != null && query.getPageSize() != null) {
            query.setLimitStart((query.getPageNo() - 1) * query.getPageSize());
            query.setLimitEnd(query.getPageSize());
        }
    }

    /**
     * 根据查询条件查总数
     *
     * @param query 查询参数
     * @return {@link null}
     * @author dabao
     * @date 2022/8/30
     */
    private Integer selectCountByCount(DataRetrieveQuery query) {
        LambdaQueryWrapper<DataRetrieveInfo> queryWrapper = new LambdaQueryWrapper<>();
        //此处可构造查询
        Integer total = baseMapper.selectCount(queryWrapper);
        return total;
    }


    @Override
    public Page<DwmVlmsDocs> selectDataRetrieveDetail(DataRetrieveQuery query) {
        if (Objects.isNull(query) || Objects.isNull(query.getInfoCode())) {
            throw new SecurityException("参数不能为空");
        }
        Page<DwmVlmsDocs> page = new Page<>(query.getPageNo(), query.getPageSize());
        createLimitData(query);
        Integer total = dataRetrieveDetailService.selectCountByCount(query);
        List<DwmVlmsDocs> dwmVlmsDocsList = dataRetrieveDetailService.selectDocsList(query);
        if (CollectionUtils.isEmpty(dwmVlmsDocsList)) {
            return page;
        }
        //源库数据要把源库的日期填充上
        List<String> sourceVins = dwmVlmsDocsList.stream()
                .filter(item -> item.getSource().equals(Byte.valueOf("1"))).map(DwmVlmsDocs::getVvin).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sourceVins)){
            List<DwmVlmsDocs> docsListSource = oracleSptb02Service.selectListByVin(sourceVins);
            transplantSourceTime(docsListSource, dwmVlmsDocsList);
        }
        page.setTotal(total.longValue());
        page.setRecords(dwmVlmsDocsList);
        return page;
    }

    private void transplantSourceTime(List<DwmVlmsDocs> docsListSource, List<DwmVlmsDocs> dwmVlmsDocsList) {
        Map<String, DwmVlmsDocs> sourceDataMap = docsListSource.stream()
                .collect(Collectors.toMap(DwmVlmsDocs::getVvin, DwmVlmsDocs -> DwmVlmsDocs));
        dwmVlmsDocsList.subList(0, docsListSource.size()).forEach(item -> {
            DwmVlmsDocs dwmVlmsDocs = sourceDataMap.get(item.getVvin());
            if (Objects.nonNull(dwmVlmsDocs)){
                if (Objects.nonNull(dwmVlmsDocs.getDdjrqR3())){
                    item.setDdjrqR3(dwmVlmsDocs.getDdjrqR3());
                }
                if (Objects.nonNull(dwmVlmsDocs.getAssignTime())){
                    item.setAssignTime(dwmVlmsDocs.getAssignTime());
                }
                if (Objects.nonNull(dwmVlmsDocs.getActualOutTime())){
                    item.setActualOutTime(dwmVlmsDocs.getActualOutTime());
                }
                if (Objects.nonNull(dwmVlmsDocs.getShipmentTime())){
                    item.setShipmentTime(dwmVlmsDocs.getShipmentTime());
                }
                if (Objects.nonNull(dwmVlmsDocs.getDtvsdhsj())){
                    item.setDtvsdhsj(dwmVlmsDocs.getDtvsdhsj());
                }
            }

        });
    }

}




