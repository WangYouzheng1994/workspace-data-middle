package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveDetail;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.service.DataRetrieveDetailService;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DataRetrieveDetailMapper;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DataRetrieveQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
* @author Administrator
* @description 针对表【data_retrieve_detail】的数据库操作Service实现
* @createDate 2022-08-29 13:53:06
*/
@Service
@DS("master")
@Transactional(rollbackFor = Exception.class)
public class DataRetrieveDetailServiceImpl extends ServiceImpl<DataRetrieveDetailMapper, DataRetrieveDetail>
    implements DataRetrieveDetailService{

    @Override
    public Integer selectCountByCount(DataRetrieveQuery query) {
        LambdaQueryWrapper<DataRetrieveDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataRetrieveDetail::getInfoCode, query.getInfoCode());
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public List<DwmVlmsDocs> selectDocsList(DataRetrieveQuery query) {
        //根据条件查询
        List<DwmVlmsDocs> dwmVlmsDocsList = baseMapper.selectDocsList(query);
        return dwmVlmsDocsList;
    }
}




