package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.UserInfo;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@DS("slave0")
@Service
public class DwmVlmsSptb02ServiceImpl extends ServiceImpl<DwmVlmsSptb02Mapper, DwmVlmsSptb02> implements IDwmVlmsSptb02Service {
    @Resource
    private DwmVlmsSptb02Mapper dwmVlmsSptb02Mapper;

    /**
     * 查询top10发运量列表
     *
     * @param dwmVlmsSptb02
     * @return
     */

    @Override
    public Result findTop10SendList(DwmVlmsSptb02 dwmVlmsSptb02) {
        return null;
    }

    /**
     * 查询日计划量
     * @param dwmVlmsSptb02
     * @return
     */
    @Override
    public Integer findDayAmountOfPlan(DwmVlmsSptb02 dwmVlmsSptb02) {
        // 暂时写个count的测试,已通过
        Integer integer = this.dwmVlmsSptb02Mapper.selectCount(new LambdaQueryWrapper<DwmVlmsSptb02>());
        // todo:按照传过来的数据进行条件查询
        LambdaQueryWrapper<DwmVlmsSptb02> queryWrapper = new LambdaQueryWrapper<>();
        if (dwmVlmsSptb02 !=null){
            //基地
            if (StringUtils.isNotBlank(dwmVlmsSptb02.getTransModeCode())){
                queryWrapper.eq(DwmVlmsSptb02::getTransModeCode,"1");
            }
            //汽车品牌
            if (StringUtils.isNotBlank(dwmVlmsSptb02.getHostComCode())){
                queryWrapper.eq(DwmVlmsSptb02::getHostComCode,"1");
            }
            //todo:按照时间查询,注:StringUtils.isNotBlank()不支持Lang类型
            if (dwmVlmsSptb02.getDdjrq()!=null){
                queryWrapper.between(DwmVlmsSptb02::getDdjrq,"xxxxxxL","xxxxxxL");
            }

        }
        //todo: 返回值改成Wrapper
        return integer;
    }

    @Override
    public Result findUserInfo(UserInfo userInfo) {
        return null;
    }
}
