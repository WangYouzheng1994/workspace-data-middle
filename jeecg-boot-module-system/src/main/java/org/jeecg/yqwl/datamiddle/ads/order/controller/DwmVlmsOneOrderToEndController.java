package org.jeecg.yqwl.datamiddle.ads.order.controller;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeecg.dingtalk.api.core.vo.PageResult;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;

import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.test.entity.JeecgDemo;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
@Slf4j
@Api(tags="一单到底")
@RestController
@RequestMapping("/ads/order/dwmVlmsOneOrderToEnd")
public class DwmVlmsOneOrderToEndController extends JeecgController<DwmVlmsOneOrderToEnd, IDwmVlmsOneOrderToEndService> {
	@Autowired
	private IDwmVlmsOneOrderToEndService dwmVlmsOneOrderToEndService;
	
	/**
	 * 分页列表查询
	 *
	 * @param dwmVlmsOneOrderToEnd
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "一单到底-分页列表查询")
	@ApiOperation(value="一单到底-分页列表查询", notes="一单到底-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DwmVlmsOneOrderToEnd> queryWrapper = QueryGenerator.initQueryWrapper(dwmVlmsOneOrderToEnd, req.getParameterMap());
		Page<DwmVlmsOneOrderToEnd> page = new Page<DwmVlmsOneOrderToEnd>(pageNo, pageSize);
		IPage<DwmVlmsOneOrderToEnd> pageList = dwmVlmsOneOrderToEndService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param dwmVlmsOneOrderToEnd
	 * @return
	 */
	@AutoLog(value = "一单到底-添加")
	@ApiOperation(value="一单到底-添加", notes="一单到底-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd) {
		dwmVlmsOneOrderToEndService.save(dwmVlmsOneOrderToEnd);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param dwmVlmsOneOrderToEnd
	 * @return
	 */
	@AutoLog(value = "一单到底-编辑")
	@ApiOperation(value="一单到底-编辑", notes="一单到底-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd) {
		dwmVlmsOneOrderToEndService.updateById(dwmVlmsOneOrderToEnd);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "一单到底-通过id删除")
	@ApiOperation(value="一单到底-通过id删除", notes="一单到底-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		dwmVlmsOneOrderToEndService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "一单到底-批量删除")
	@ApiOperation(value="一单到底-批量删除", notes="一单到底-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.dwmVlmsOneOrderToEndService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "一单到底-通过id查询")
	@ApiOperation(value="一单到底-通过id查询", notes="一单到底-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd = dwmVlmsOneOrderToEndService.getById(id);
		return Result.OK(dwmVlmsOneOrderToEnd);
	}

	/**
   * 导出excel
   *
   */
  @AutoLog(value = "导出")
  @ApiOperation(value="导出", notes="导出")
  @GetMapping(value = "/exportXls")
  public void list(@Validated GetQueryCriteria queryCriteria ,HttpServletResponse response) throws IOException {
	  String vin = queryCriteria.getVin();
	  if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
		  queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, ",")));
	  }
	  if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
		  queryCriteria.setSelectionsList(Arrays.asList(StringUtils.split(vin, ",")));
	  }
	  //导出Excel
	  SXSSFWorkbook export = dwmVlmsOneOrderToEndService.export(queryCriteria);
	  //设置内容类型
	  response.setContentType("application/vnd.ms-excel;charset=utf-8");
	  OutputStream outputStream = response.getOutputStream();
	  response.setHeader("Content-disposition", "attachment;filename=trainingRecord.xls");
	  export.write(outputStream);
	  outputStream.flush();
	  outputStream.close();
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
      return super.importExcel(request, response, DwmVlmsOneOrderToEnd.class);
  }

	 /**
	  * 按条件查询一单到底
	  * @param queryCriteria
	  * @return
	  */

  @AutoLog(value = "一单到底-按条件查询")
  @ApiOperation(value="一单到底-按条件查询", notes="一单到底-按条件查询")
  @PostMapping("/selectOneOrderToEndList")
  public Result<Page<DwmVlmsOneOrderToEnd>> selectOneOrderToEndList(@RequestBody GetQueryCriteria queryCriteria){
//	  Result<Page<DwmVlmsOneOrderToEnd>> result = new Result<Page<DwmVlmsOneOrderToEnd>>();

	  // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- START
	  String vin = queryCriteria.getVin();
	  if (StringUtils.isNotBlank(vin)) {
		if (StringUtils.contains(vin, ",") && StringUtils.contains(vin, "\n")) {
			return Result.error("vin码批量查询，分割模式只可以用英文逗号或者回车换行一种模式，不可混搭，请检查vin码查询条件", null);
		}
		// vin码批量模式： 0 逗号， 1 回车换行
		if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
			queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, ",")));
		} else if (StringUtils.length(vin) > 2 && StringUtils.contains(vin, "\n")) {
			queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, "\n")));
		}
	  }
	  // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- END
	  Page<DwmVlmsOneOrderToEnd> pageList = new Page<DwmVlmsOneOrderToEnd>(queryCriteria.getPageNo(), queryCriteria.getPageSize());
	  pageList = dwmVlmsOneOrderToEndService.selectOneOrderToEndList(queryCriteria, pageList);
	  return Result.OK(pageList);
  }
}