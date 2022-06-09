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
                //判断配载单编号的值相同,则设置同板数量
                if ( stowageNoteNo.equals(samePlateNumList.get(j).getStowageNoteNo())) {
                    params.setSamePlateNum(samePlateNumList.get(j).getSamePlateNum());
                }
            }
        }
        return page.setRecords(oneOrderToEndList);
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
    public List<SelectData> selectTotal() {
        List<SelectData> total = dwmVlmsOneOrderToEndMapper.selectTotal();
        return total;
    }
}
