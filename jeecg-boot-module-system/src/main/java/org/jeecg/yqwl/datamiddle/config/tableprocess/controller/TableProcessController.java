package org.jeecg.yqwl.datamiddle.config.tableprocess.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.yqwl.datamiddle.config.tableprocess.entity.TableProcess;
import org.jeecg.yqwl.datamiddle.config.tableprocess.service.ITableProcessService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: table_process
 * @Author: jeecg-boot
 * @Date:   2022-05-07
 * @Version: V1.0
 */
@Api(tags="table_process")
@RestController
@RequestMapping("/config/tableProcess")
@Slf4j
public class TableProcessController extends JeecgController<TableProcess, ITableProcessService> {
	@Autowired
	private ITableProcessService tableProcessService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tableProcess
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "table_process-分页列表查询")
	@ApiOperation(value="table_process-分页列表查询", notes="table_process-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TableProcess tableProcess,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TableProcess> queryWrapper = QueryGenerator.initQueryWrapper(tableProcess, req.getParameterMap());
		Page<TableProcess> page = new Page<TableProcess>(pageNo, pageSize);
		IPage<TableProcess> pageList = tableProcessService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tableProcess
	 * @return
	 */
	@AutoLog(value = "table_process-添加")
	@ApiOperation(value="table_process-添加", notes="table_process-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TableProcess tableProcess) {
		tableProcessService.save(tableProcess);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tableProcess
	 * @return
	 */
	@AutoLog(value = "table_process-编辑")
	@ApiOperation(value="table_process-编辑", notes="table_process-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TableProcess tableProcess) {
		tableProcessService.updateById(tableProcess);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "table_process-通过id删除")
	@ApiOperation(value="table_process-通过id删除", notes="table_process-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tableProcessService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "table_process-批量删除")
	@ApiOperation(value="table_process-批量删除", notes="table_process-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tableProcessService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "table_process-通过id查询")
	@ApiOperation(value="table_process-通过id查询", notes="table_process-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TableProcess tableProcess = tableProcessService.getById(id);
		if(tableProcess==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tableProcess);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tableProcess
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TableProcess tableProcess) {
        return super.exportXls(request, tableProcess, TableProcess.class, "table_process");
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
        return super.importExcel(request, response, TableProcess.class);
    }

}
