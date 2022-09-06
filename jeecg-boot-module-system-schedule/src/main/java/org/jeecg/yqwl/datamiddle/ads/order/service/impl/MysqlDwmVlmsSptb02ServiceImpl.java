package org.jeecg.yqwl.datamiddle.ads.order.service.impl;



import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.MysqlDwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IMysqlDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * mysql的 sptb02实现
 * @author dabao
 * @date 2022/8/31
 */
@Service
@DS("wareHouse")
@Transactional(rollbackFor = Exception.class)
public class MysqlDwmVlmsSptb02ServiceImpl extends ServiceImpl<MysqlDwmVlmsSptb02Mapper, DwmVlmsDocs> implements IMysqlDwmVlmsSptb02Service {

    /**
     * 查询出docs数据，只包含vin 和 时间
     * @param queryCriteria 查询参数
     * @author dabao
     * @date 2022/8/31
     * @return {@link List<DwmVlmsDocs>}
     */
    @Override
    public List<DwmVlmsDocs> selectDocsCcxdlList(GetQueryCriteria queryCriteria) {
        //分页去查
        Integer total = baseMapper.selectDocsCcxdlCount(queryCriteria);
        List<DwmVlmsDocs> dwmVlmsDocsList = new ArrayList<>();
        boolean flag = true;
        int pageNo = 1;
        int pageSize = 1000;
        //计算总共多少页
        int pageNoTotal = BigDecimal.valueOf(total).divide(BigDecimal.valueOf(pageSize), 0, BigDecimal.ROUND_UP).intValue();
        //一千条查一次
        queryCriteria.setPageSize(pageSize);
        while (flag){
            queryCriteria.setPageNo(pageNo);
            queryCriteria.setLimitStart((queryCriteria.getPageNo() - 1) * queryCriteria.getPageSize());
            queryCriteria.setLimitEnd(queryCriteria.getPageSize());
            List<DwmVlmsDocs> vlmsDocs = baseMapper.selectDocsCcxdlList(queryCriteria);
            dwmVlmsDocsList.addAll(vlmsDocs);
            pageNo ++;
            if (pageNo > pageNoTotal){
                flag = false;
            }
        }
        return dwmVlmsDocsList;
    }
}
