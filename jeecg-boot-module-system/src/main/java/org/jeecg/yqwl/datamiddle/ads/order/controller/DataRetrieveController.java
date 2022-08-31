package org.jeecg.yqwl.datamiddle.ads.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveInfo;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.service.DataRetrieveDetailService;
import org.jeecg.yqwl.datamiddle.ads.order.service.DataRetrieveInfoService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DataRetrieveQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据质量菜单
 * @author dabao
 * @date 2022/8/30
 */
@Slf4j
@Api(tags = "数据质量菜单")
@RestController
@RequestMapping("dataRetrieve")
public class DataRetrieveController {

    @Autowired
    private DataRetrieveInfoService dataRetrieveInfoService;


    @AutoLog(value = "分页查询数据质量菜单")
    @ApiOperation(value = "分页查询数据质量菜单", notes = "分页查询数据质量菜单")
    @PostMapping("/findPage")
    public Result<Page<DataRetrieveInfo>> selectDataRetrieveInfoList(@RequestBody DataRetrieveQuery query) {
        return Result.OK(dataRetrieveInfoService.selectDataRetrieveInfoPage(query));
    }

    @AutoLog(value = "查询数据质量菜单详情")
    @ApiOperation(value = "查询数据质量菜单详情", notes = "查询数据质量菜单详情")
    @PostMapping("/selectDataRetrieveDetail")
    public Result<Page<DwmVlmsDocs>> selectDataRetrieveDetail(@RequestBody DataRetrieveQuery query){
        return Result.OK(dataRetrieveInfoService.selectDataRetrieveDetail(query));
    }

}
