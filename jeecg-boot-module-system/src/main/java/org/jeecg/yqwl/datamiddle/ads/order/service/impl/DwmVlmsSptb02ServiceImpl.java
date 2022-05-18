package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.GetBaseBrandTime;
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
     * @param baseBrandTime
     * @return
     */
    @Override
    public Integer findDayAmountOfPlan(GetBaseBrandTime baseBrandTime) {
        String transModeCode = baseBrandTime.getTransModeCode(); //基地代码
        String hostComCode = baseBrandTime.getHostComCode();     //汽车品牌
        String startTime = baseBrandTime.getStartTime();         //开始时间
        String endTime = baseBrandTime.getEndTime();             //结束时间

        // 暂时写个count的测试,已通过
        Integer integer = this.dwmVlmsSptb02Mapper.selectCount(new LambdaQueryWrapper<DwmVlmsSptb02>());
        // 按照传过来的数据进行条件查询
        LambdaQueryWrapper<DwmVlmsSptb02> queryWrapper = new LambdaQueryWrapper<>();
        if (baseBrandTime !=null){
            //基地
            if (StringUtils.isNotBlank(transModeCode)){
                queryWrapper.eq(DwmVlmsSptb02::getTransModeCode,transModeCode);
                //如果查询所有的基地,就分组
                if (transModeCode.equals("0")){
                    queryWrapper.groupBy(DwmVlmsSptb02::getBaseName);
                }
            }
            //汽车品牌
            if (StringUtils.isNotBlank(hostComCode)){
                queryWrapper.eq(DwmVlmsSptb02::getHostComCode,hostComCode);
            }
            //按照开始,结束时间查询
            if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
                queryWrapper.between(DwmVlmsSptb02::getDdjrq,startTime,endTime);
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
