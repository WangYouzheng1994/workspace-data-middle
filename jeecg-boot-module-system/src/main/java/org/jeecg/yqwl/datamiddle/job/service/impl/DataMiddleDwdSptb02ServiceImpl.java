package org.jeecg.yqwl.datamiddle.job.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqwl.datamiddle.realtime.bean.DwmSptb02;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.phoenix.shaded.com.ibm.icu.text.SimpleDateFormat;
import org.apache.phoenix.shaded.com.ibm.icu.util.Calendar;
import org.jeecg.common.util.DateUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.TableParams;
import org.jeecg.yqwl.datamiddle.ads.order.enums.TableName;
import org.jeecg.yqwl.datamiddle.job.mapper.DataMiddleDwdSptb02Mapper;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleDwdSptb02Service;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @Description
 * @ClassName DataMiddleDwdSptb02ServiceImpl
 * @Author YULUO
 * @Date 2022/6/13
 */
@Slf4j
@Service
@DS("wareHouse")
public class DataMiddleDwdSptb02ServiceImpl extends ServiceImpl<DataMiddleDwdSptb02Mapper, DwdSptb02> implements DataMiddleDwdSptb02Service {

    @Resource
    private DataMiddleDwdSptb02Mapper dataMiddleDwdSptb02Mapper;

    @Override
    public void getDwdVlmsSptb02() {
        log.info("开始查询dwm_vlms_sptb02表数据!");
        //TODO : 查看距今15天以内的数据
        //获取15天的00:00:00
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -15);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String startTime = sdf.format(date);
        //获取当天的23:59:59
        String endTime = DateUtils.getToday4Night();
        boolean hasNext = true;
        Integer limit = 500;
        Integer rowNum=0;
        int interval = 1;
        do {
            log.info("开始循环数据 !" , interval++ );
            List<DwdSptb02> dwdSptb02List = this.dataMiddleDwdSptb02Mapper.getDwdVlmsSptb02(rowNum, startTime, endTime, interval, limit);
            log.info("组装数据进行拉宽处理!");
            //循环
            for ( DwdSptb02 dwdSptb02 : dwdSptb02List ) {
                DwmSptb02 dwmSptb02 = new DwmSptb02();
                //复制bean对象的属性
                BeanUtil.copyProperties(dwdSptb02,dwmSptb02);
                log.info("复制表到dwmSptb02!");
                /**
                 * 关联ods_vlms_sptb02d1 获取车架号 VVIN 从mysql中获取
                 */
                TableParams table = new TableParams();
                table.setTableName("ods_vlms_sptb02d1");
                table.setField("CJSDBH");
                String cjsdbh = dwmSptb02.getCJSDBH();
                log.info("sptb02d1DS阶段获取到的查询条件值:{}", cjsdbh);
                if ( StringUtils.isNotEmpty(cjsdbh)) {
                    table.setValues(cjsdbh);
                }

            }
        } while(hasNext);
        log.info("查询dwdSptb02数据结束!");
    }
}
