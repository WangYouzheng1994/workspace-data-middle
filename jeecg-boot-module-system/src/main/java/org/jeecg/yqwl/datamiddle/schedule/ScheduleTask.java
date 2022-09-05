package org.jeecg.yqwl.datamiddle.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveInfo;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.enums.DateProblemEnum;
import org.jeecg.yqwl.datamiddle.ads.order.service.DataRetrieveInfoService;
import org.jeecg.yqwl.datamiddle.ads.order.service.IMysqlDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.service.IOracleSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 定时任务
 * @author dabao
 * @date 2022/8/29
 */
@Slf4j
@RestController
@RequestMapping("scheduleTask")
public class ScheduleTask {

    @Autowired
    private IMysqlDwmVlmsSptb02Service mysqlDwmVlmsSptb02Service;

    @Autowired
    private IOracleSptb02Service oracleSptb02Service;

    @Autowired
    private DataRetrieveInfoService dataRetrieveInfoService;

    /**
     * Long的初始
     */
    private final static Long zero = 0L;
    /**
     * 定时检索数据 每日0点执行
     * @author dabao
     * @date 2022/8/31
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void retrieveData(){
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        long nowLong = now.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        //获取30天之前的时间
        LocalDateTime last = now.minus(30, ChronoUnit.DAYS);
        long lastLong = last.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        GetQueryCriteria queryCriteria = new GetQueryCriteria();
        queryCriteria.setVehiclePlateIssuedTimeStart(lastLong);
        queryCriteria.setVehiclePlateIssuedTimeEnd(nowLong);
        queryCriteria.setTrafficType("G");
        //查询近30日mysql中的数据 利用分页分部查
        List<DwmVlmsDocs> dwmVlmsDocs = mysqlDwmVlmsSptb02Service.selectDocsCcxdlList(queryCriteria);
        if (CollectionUtils.isEmpty(dwmVlmsDocs)){
            return;
        }
        //判断问题,并将vin按问题类型分组
        Map<Integer, List<String>> vinMap = groupByProblem(dwmVlmsDocs);
        //根据编码查出原表近30日数据
        List<String> vinList = new ArrayList<>();
        vinMap.forEach( (key,value) -> {
            if (CollectionUtils.isNotEmpty(value)){
                vinList.addAll(value);
            }
        });
        if (CollectionUtils.isEmpty(vinList)){
            //没有异常数据
            return;
        }
        List<DwmVlmsDocs> dwmVlmsDocsFromOracle = oracleSptb02Service.selectListByVin(vinList);
        //判断有无问题
        Map<Integer, List<String>> vinMapFromOracle = groupByProblem(dwmVlmsDocsFromOracle);
        //存放问题数据
        DataRetrieveInfo dataRetrieveInfo = new DataRetrieveInfo();
        //初始化数据
        dataRetrieveInfo.setRetrieveTime(nowLong);
        //处理时间范围的时间格式
        DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String range = dfDate.format(last) + "0点 -- " + dfDate.format(now) + "23点59分59秒";
        dataRetrieveInfo.setRetrieveRange(range);
        //保存数据 也要分部保存
        dataRetrieveInfoService.saveInfo(dataRetrieveInfo, vinMap, vinMapFromOracle);
    }

    /**
     * 将数据按照问题类型分组
     * @param dwmVlmsDocs 查询得到的数组
     * @author dabao
     * @date 2022/8/31
     * @return {@link Map<Integer,List<String>>}
     */
    private Map<Integer,List<String>> groupByProblem(List<DwmVlmsDocs> dwmVlmsDocs){
        //创建分组
        HashMap<Integer, List<String>> vinMap = new HashMap<>();

        dwmVlmsDocs.forEach(item -> {
            //满足第一种问题 指派时间早于计划下达时间 指派时间为空-过
            if (Objects.nonNull(item.getAssignTime())
                    && !item.getAssignTime().equals(zero)
                    && item.getAssignTime() < item.getDdjrqR3()){
                //判断 数组是否存在，存在就加一条数据，不存在就新建一个list
                buildVinMap(vinMap, DateProblemEnum.PROBLEM_00.getProblemCode(), item.getVvin());
            }
            //满足第二种问题 出库时间早于指派时间+有出库时间，没有指派时间
            if (judgmentProblem(item.getAssignTime(), item.getActualOutTime())){
                //判断 数组是否存在，存在就加一条数据，不存在就新建一个list
                buildVinMap(vinMap, DateProblemEnum.PROBLEM_01.getProblemCode(), item.getVvin());
            }
            //满足第三种问题 起运时间早于出库时间
            if (judgmentProblem(item.getActualOutTime(), item.getShipmentTime())){
                //判断 数组是否存在，存在就加一条数据，不存在就新建一个list
                buildVinMap(vinMap, DateProblemEnum.PROBLEM_02.getProblemCode(), item.getVvin());
            }
            //满足第四种问题 到货时间早于起运时间
            if (judgmentProblem(item.getShipmentTime(),item.getDtvsdhsj())){
                //判断 数组是否存在，存在就加一条数据，不存在就新建一个list
                buildVinMap(vinMap, DateProblemEnum.PROBLEM_03.getProblemCode(), item.getVvin());
            }
        });
        return vinMap;
    }

    /**
     * 将vin按照问题类型分组
     * @param vinMap 分组
     * @param problemCode 问题编号
     * @param vin 对应的vin码
     * @author dabao
     * @date 2022/8/31
     */
    private void buildVinMap(HashMap<Integer, List<String>> vinMap,Integer problemCode, String vin){
        //判断 数组是否存在，存在就加一条数据，不存在就新建一个list
        if (CollectionUtils.isNotEmpty(vinMap.get(problemCode))){
            vinMap.get(problemCode).add(vin);
        }else {
            List<String> vins = new ArrayList<>();
            vins.add(vin);
            vinMap.put(problemCode, vins);
        }
    }

    /**
     * 比较两个时间是否符合错误条件
     * 判断条件：
     * 1.如果靠前时间没有，但是有靠后的时间
     * 2.两个时间都存在的前提下并且靠后时间不为0（mysql数据库中的初始值为0）
     *   靠后时间早于靠前时间。
     * @param preTime 理论靠前时间
     * @param nextTime 理论靠后时间
     * @author dabao
     * @date 2022/9/1
     * @return {@link Boolean} 符合错误条件返回true
     */
    private Boolean judgmentProblem(Long preTime, Long nextTime){
             //条件
        return Objects.isNull(preTime) && Objects.nonNull(nextTime)
                || (Objects.nonNull(nextTime)
                && !nextTime.equals(zero)
                && nextTime < preTime);
    }
}
