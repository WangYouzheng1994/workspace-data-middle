package org.jeecg.yqwl.datamiddle.config.datasource.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.yqwl.datamiddle.config.datasource.entity.DatasourceConfig;
import org.jeecg.yqwl.datamiddle.config.datasource.service.IDatasourceConfigService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: datasource_config
 * @Author: jeecg-boot
 * @Date:   2022-05-09
 * @Version: V1.0
 */
@Api(tags="datasource_config")
@RestController
@RequestMapping("/org.jeecg.yqwl.datamiddle/datasourceConfig")
@Slf4j
public class DatasourceConfigController extends JeecgController<DatasourceConfig, IDatasourceConfigService> {
	@Autowired
	private IDatasourceConfigService datasourceConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param datasourceConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "datasource_config-分页列表查询")
	@ApiOperation(value="datasource_config-分页列表查询", notes="datasource_config-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DatasourceConfig datasourceConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DatasourceConfig> queryWrapper = QueryGenerator.initQueryWrapper(datasourceConfig, req.getParameterMap());
		Page<DatasourceConfig> page = new Page<DatasourceConfig>(pageNo, pageSize);
		IPage<DatasourceConfig> pageList = datasourceConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param datasourceConfig
	 * @return
	 */
	@AutoLog(value = "datasource_config-添加")
	@ApiOperation(value="datasource_config-添加", notes="datasource_config-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DatasourceConfig datasourceConfig) {
		datasourceConfigService.save(datasourceConfig);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param datasourceConfig
	 * @return
	 */
	@AutoLog(value = "datasource_config-编辑")
	@ApiOperation(value="datasource_config-编辑", notes="datasource_config-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DatasourceConfig datasourceConfig) {
		datasourceConfigService.updateById(datasourceConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "datasource_config-通过id删除")
	@ApiOperation(value="datasource_config-通过id删除", notes="datasource_config-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		datasourceConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "datasource_config-批量删除")
	@ApiOperation(value="datasource_config-批量删除", notes="datasource_config-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.datasourceConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "datasource_config-通过id查询")
	@ApiOperation(value="datasource_config-通过id查询", notes="datasource_config-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DatasourceConfig datasourceConfig = datasourceConfigService.getById(id);
		if(datasourceConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(datasourceConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param datasourceConfig
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DatasourceConfig datasourceConfig) {
        return super.exportXls(request, datasourceConfig, DatasourceConfig.class, "datasource_config");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DatasourceConfig.class);
    }

}
