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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            List<DwdSptb02> dwdSptb02List = this.dataMiddleDwdSptb02Mapper.getDwdVlmsSptb02(rowNum, startTime, endTime, rowNum, limit);
            log.info("组装数据进行拉宽处理!");
            //循环
            for ( DwdSptb02 dwdSptb02 : dwdSptb02List ) {
                DwmSptb02 dwmSptb02 = new DwmSptb02();
                //复制bean对象的属性
                BeanUtil.copyProperties(dwdSptb02,dwmSptb02);
                log.info("复制dwdsptb02表到dwmSptb02!");
                /**
                 * 关联ods_vlms_sptb02d1 获取车架号 VVIN 从mysql中获取
                 */
                TableParams tableParams = new TableParams();
                tableParams.setTableName("ods_vlms_sptb02d1");
                tableParams.setField("CJSDBH");
                String cjsdbh = dwdSptb02.getCJSDBH();
                if ( StringUtils.isNotEmpty(cjsdbh)) {
                    tableParams.setValues(cjsdbh);
                    String vvin = "";
                    String ccpdm = "";
                    Map tableValues = this.dataMiddleDwdSptb02Mapper.getTableValues(tableParams);
                    if ( tableValues != null ) {
                         vvin = (String)tableValues.get("VVIN");
                         ccpdm = (String)tableValues.get("CCPDM");
                        if ( StringUtils.isNotEmpty(vvin) ) {
                            dwmSptb02.setVVIN(vvin);//车架号
                        }
                        if ( StringUtils.isNotEmpty(ccpdm) ) {
                            dwmSptb02.setVEHICLE_CODE(ccpdm); //车型代码
                        }
                    }
                }

                /**
                 //理论起运时间
                 //关联ods_vlms_lc_spec_config 获取 STANDARD_HOURS 标准时长
                 // 获取车架号 VVIN 从mysql中获取
                 * 查询 ods_vlms_lc_spec_config
                 * 过滤条件：
                 * 主机公司代码 CZJGSDM
                 *
                 * BASE_CODE(转换保存代码)  ->   CQWH 区位号(基地代码)
                 *
                 * TRANS_MODE_CODE       -> 运输方式
                 */
                tableParams.setTableName("ods_vlms_lc_spec_config");
                tableParams.setField("HOST_COM_CODE");
                tableParams.setField("BASE_CODE");
                tableParams.setField("TRANS_MODE_CODE");
                //筛选条件
                tableParams.setField("STATUS");  //STATUS = '1'
                tableParams.setField("SPEC_CODE"); //SPEC_CODE = '4'
                String hostComCode = dwdSptb02.getHOST_COM_CODE();
                String baseCode = dwdSptb02.getBASE_CODE();
                String transModeCode = dwdSptb02.getTRANS_MODE_CODE();
                if (StringUtils.isNotEmpty(hostComCode) && StringUtils.isNotEmpty(baseCode) && StringUtils.isNotEmpty(transModeCode)) {
                    tableParams.setValues("hostComCode");
                    tableParams.setValues("baseCode");
                    tableParams.setValues("transModeCode");
                    //定义要增加的时间戳
                    Long outSiteTime = null;
                    //定义增加的时间步长
                    String hours = "";
                    //定义前置节点的代码
                    String nodeCode = "";
                    Map tableValues = this.dataMiddleDwdSptb02Mapper.getTableValues(tableParams);
                    if ( tableValues != null ) {
                        hours = (String)tableValues.get("STANDARD_HOURS");
                        nodeCode = (String)tableValues.get("START_CAL_NODE_CODE");


                    }

                }




            }
        } while(hasNext);
        log.info("查询dwdSptb02数据结束!");
    }
}
