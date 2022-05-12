package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@Service
public class DwmVlmsSptb02ServiceImpl extends ServiceImpl<DwmVlmsSptb02Mapper, DwmVlmsSptb02> implements IDwmVlmsSptb02Service {

    /**
     * 查询top10发运量列表
     *
     * @param dwmVlmsSptb02
     * @return
     */
    @DS("xxxxx")
    @Override
    public Result findTop10SendList(DwmVlmsSptb02 dwmVlmsSptb02) {

        return null;
    }
}
