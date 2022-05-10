package org.jeecg.yqwl.datamiddle.config.driver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.yqwl.datamiddle.config.driver.entity.DatasourceDriver;
import org.jeecg.yqwl.datamiddle.config.driver.service.IDatasourceDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: datasource_driver
 * @Author: jeecg-boot
 * @Date:   2022-05-10
 * @Version: V1.0
 */
@Api(tags="datasource_driver")
@RestController
@RequestMapping("/1/datasourceDriver")
@Slf4j
public class DatasourceDriverController extends JeecgController<DatasourceDriver, IDatasourceDriverService> {
	@Autowired
	private IDatasourceDriverService datasourceDriverService;
	
	/**
	 * 分页列表查询
	 *
	 * @param datasourceDriver
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "datasource_driver-分页列表查询")
	@ApiOperation(value="datasource_driver-分页列表查询", notes="datasource_driver-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DatasourceDriver datasourceDriver,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DatasourceDriver> queryWrapper = QueryGenerator.initQueryWrapper(datasourceDriver, req.getParameterMap());
		Page<DatasourceDriver> page = new Page<DatasourceDriver>(pageNo, pageSize);
		IPage<DatasourceDriver> pageList = datasourceDriverService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param datasourceDriver
	 * @return
	 */
	@AutoLog(value = "datasource_driver-添加")
	@ApiOperation(value="datasource_driver-添加", notes="datasource_driver-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DatasourceDriver datasourceDriver) {
		datasourceDriverService.save(datasourceDriver);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param datasourceDriver
	 * @return
	 */
	@AutoLog(value = "datasource_driver-编辑")
	@ApiOperation(value="datasource_driver-编辑", notes="datasource_driver-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DatasourceDriver datasourceDriver) {
		datasourceDriverService.updateById(datasourceDriver);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "datasource_driver-通过id删除")
	@ApiOperation(value="datasource_driver-通过id删除", notes="datasource_driver-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		datasourceDriverService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "datasource_driver-批量删除")
	@ApiOperation(value="datasource_driver-批量删除", notes="datasource_driver-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.datasourceDriverService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "datasource_driver-通过id查询")
	@ApiOperation(value="datasource_driver-通过id查询", notes="datasource_driver-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DatasourceDriver datasourceDriver = datasourceDriverService.getById(id);
		if(datasourceDriver==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(datasourceDriver);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param datasourceDriver
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DatasourceDriver datasourceDriver) {
        return super.exportXls(request, datasourceDriver, DatasourceDriver.class, "datasource_driver");
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
        return super.importExcel(request, response, DatasourceDriver.class);
    }

}
