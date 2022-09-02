package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.OracleSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.service.IOracleSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.OracleDocsTimeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用来去Oracle查询数据库
 * @Author: XiaoFeng
 * @Date: 2022/8/16 20:10
 * @Version: V1.0
 */
@Slf4j
@DS("oracle-vts")
@Service
public class IOracleSptb02ServiceImpl extends ServiceImpl<OracleSptb02Mapper, Sptb02>  implements IOracleSptb02Service {
    @Resource
    private OracleSptb02Mapper oracleSptb02Mapper;

    /**
     * oracle查询语句中in list集合中最大长度 == oracle语法限制
     */
    private final static int MAX_SIZE = 1000;

    @Override
    public Integer countOracleVinOfSptb02AndSptb02d1(String vvin) {
        Integer integer = oracleSptb02Mapper.countOracleVinOfSptb02AndSptb02d1(vvin);
        return integer;
    }

    /**
     * 根据vin码查询数据
     * @param vinList 参数
     * @author dabao
     * @date 2022/8/29
     * @return {@link List< DwmVlmsDocs>}
     */
    @Override
    @DS("oracle-vts")
    public List<DwmVlmsDocs> selectListByVin(List<String> vinList) {
        if (CollectionUtils.isEmpty(vinList)){
            return Lists.newArrayList();
        }
        List<String> vins = vinList.stream().distinct().collect(Collectors.toList());
        List<DwmVlmsDocs> dwmVlmsDocsList = new ArrayList<>();
        //oracle查询语句中in list集合中最大不能超过1000，语法限制，需做处理
        if (vins.size() > MAX_SIZE){
            //计算循环次数
            BigDecimal size = BigDecimal.valueOf(vins.size());
            BigDecimal max = BigDecimal.valueOf(MAX_SIZE);
            int count = size.divide(max, 0, BigDecimal.ROUND_UP).intValue();
            for (int i = 0; i < count; i++){
                int start = max.multiply(BigDecimal.valueOf(i)).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
                // max*(i+1)-1
                int end = max.multiply(BigDecimal.valueOf(i + 1)).subtract(BigDecimal.ONE).intValue();
                if (i == (count-1)){
                    end = vins.size() - 1;
                }
                List<DwmVlmsDocs> vlmsDocs = oracleSptb02Mapper.selectListByVin(vins.subList(start, end));

                dwmVlmsDocsList.addAll(vlmsDocs);
            }
            return dwmVlmsDocsList;
        }
        dwmVlmsDocsList = oracleSptb02Mapper.selectListByVin(vins);

        return dwmVlmsDocsList;
    }
}
