package org.jeecg.yqwl.datamiddle.config.datasource.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import org.jeecg.common.system.vo.DynamicDataSourceModel;
import org.jeecg.common.util.oConvertUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.yqwl.datamiddle.config.datasource.entity.DatasourceConfig;
import org.jeecg.yqwl.datamiddle.config.datasource.service.IDatasourceConfigService;
import org.jeecg.yqwl.datamiddle.config.driver.entity.DatasourceDriver;
import org.jeecg.yqwl.datamiddle.config.driver.service.IDatasourceDriverService;
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
@RequestMapping("/config/datasourceConfig")
@Slf4j
public class DatasourceConfigController extends JeecgController<DatasourceConfig, IDatasourceConfigService> {
	@Autowired
	private IDatasourceConfigService datasourceConfigService;
	@Autowired
	private IDatasourceDriverService datasourceDriverService;
	
	/**
	 * ??????????????????
	 *
	 * @param datasourceConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "datasource_config-??????????????????")
	@ApiOperation(value="datasource_config-??????????????????", notes="datasource_config-??????????????????")
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
	 *   ??????
	 *
	 * @param datasourceConfig
	 * @return
	 */
	@AutoLog(value = "datasource_config-??????")
	@ApiOperation(value="datasource_config-??????", notes="datasource_config-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DatasourceConfig datasourceConfig) {
		datasourceConfigService.save(datasourceConfig);
		return Result.OK("???????????????");
	}
	
	/**
	 *  ??????
	 *
	 * @param datasourceConfig
	 * @return
	 */
	@AutoLog(value = "datasource_config-??????")
	@ApiOperation(value="datasource_config-??????", notes="datasource_config-??????")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DatasourceConfig datasourceConfig) {
		datasourceConfigService.updateById(datasourceConfig);
		return Result.OK("????????????!");
	}
	
	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "datasource_config-??????id??????")
	@ApiOperation(value="datasource_config-??????id??????", notes="datasource_config-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		datasourceConfigService.removeById(id);
		return Result.OK("????????????!");
	}
	
	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "datasource_config-????????????")
	@ApiOperation(value="datasource_config-????????????", notes="datasource_config-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.datasourceConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("??????????????????!");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "datasource_config-??????id??????")
	@ApiOperation(value="datasource_config-??????id??????", notes="datasource_config-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DatasourceConfig datasourceConfig = datasourceConfigService.getById(id);
		if(datasourceConfig==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(datasourceConfig);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param datasourceConfig
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DatasourceConfig datasourceConfig) {
        return super.exportXls(request, datasourceConfig, DatasourceConfig.class, "datasource_config");
    }

    /**
      * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, DatasourceConfig.class);
    }


	 @PostMapping({"/testConnection"})
	 public Result testConnection(@RequestBody DatasourceConfig config) {
 		 Connection conn = null;

		 Result result;

		 DatasourceDriver driver = datasourceDriverService.getById(config.getDataDriverId());
		 if (driver == null) {
		 	return Result.error("???????????????");
		 }
		 try {
			 //????????????
			 Class.forName(driver.getDataDriverClass());
			 // Class.forName(var1.getDriver());
			 // jdbc:mysql://192.168.3.4:3306/data_middle fengqiwulian
			 conn = DriverManager.getConnection(config.getDatasourceUrl(), config.getAccount(), config.getPassword());
			 if (conn != null) {
				 result = Result.OK("?????????????????????");
				 return result;
			 }

			 result = Result.OK("????????????????????????????????????");
			 return result;
		 } catch (ClassNotFoundException e) {
			 log.error(e.toString());
			 result = Result.error("??????????????????????????????????????????");
		 } catch (Exception e) {
			 log.error(e.toString());
			 result = Result.error("????????????????????????" + e.getMessage());
			 return result;
		 } finally {
			 try {
				 if (conn != null && !conn.isClosed()) {
					 conn.close();
				 }
			 } catch (SQLException var16) {
				 log.error(var16.toString());
			 }
		 }

		 return result;
	 }
}
