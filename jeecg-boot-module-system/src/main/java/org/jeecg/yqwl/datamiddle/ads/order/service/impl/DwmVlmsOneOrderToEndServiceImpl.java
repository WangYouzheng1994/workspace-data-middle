package org.jeecg.yqwl.datamiddle.ads.order.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsOneOrderToEndMapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
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

    @Override
    public Page<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(GetQueryCriteria queryCriteria, Page<DwmVlmsOneOrderToEnd> page) {
        List<DwmVlmsOneOrderToEnd> dwmVlmsOneOrderToEnds = dwmVlmsOneOrderToEndMapper.selectOneOrderToEndList(queryCriteria, page);
        // 遍历dwmVlmsOneOrderToEnds
        // 对每一行的值，查出铜板数量。 并且赋值。
        return page.setRecords(dwmVlmsOneOrderToEnds);
    }

    @Override
    public List<DwmVlmsOneOrderToEnd> getDwmVlmsOneOrderToEnd(GetQueryCriteria queryCriteria) {
        List<DwmVlmsOneOrderToEnd> oneOrderToEndList = dwmVlmsOneOrderToEndMapper.selectOneOrderToEndList(queryCriteria);
        return oneOrderToEndList;
    }

    /**
     * 查询同板数量
     * @return
     */
    @Override
    public Integer selectTotal() {
        Integer total = dwmVlmsOneOrderToEndMapper.selectTotal();
        return total;
    }
}
