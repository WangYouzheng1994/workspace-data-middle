package org.jeecg.yqwl.datamiddle.ads.order.controller;

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
   * @param
   * @param dwmVlmsOneOrderToEnd
   */
  @RequestMapping(value = "/exportXls")
  public Object exportXls(HttpServletRequest request,
								DwmVlmsOneOrderToEnd dwmVlmsOneOrderToEnd,
								@RequestBody GetQueryCriteria queryCriteria,
								HttpServletResponse response) throws IOException {
	  //创建工作簿
	  SXSSFWorkbook wb = new SXSSFWorkbook();
	  //在工作簿中创建sheet页
	  SXSSFSheet sheet = wb.createSheet("sheet1");
	  //创建行,从0开始
	  SXSSFRow row = sheet.createRow(0);
	  //获取数据列表
	  List<DwmVlmsOneOrderToEnd> dataList = dwmVlmsOneOrderToEndService.getDwmVlmsOneOrderToEnd(queryCriteria);
	  //获取表头(前端页面一共有37个字段,entity一共是57个字段)
//	  Field[] fileds = DwmVlmsOneOrderToEnd.class.getDeclaredFields();
	  String[] headers = {"底盘号","品牌","基地","车型","CP9下线接车日期","出厂日期","入库日期","入库仓库","任务单号","配板日期","配板单号","运输方式","指派日期","指派承运商名称","出库日期","起运日期-公路/铁路","运输车号","轿运车车位数","始发城市","目的城市","经销商代码","始发港口名称","到达始发港口时间/入港时间","始发港口水运离港时间","目的港/站名称","到达目的港/站时间","卸车/船时间（铁路水路到目的站）","目的港/站缓存区入库时间","港/站分拨指派时间","港/站分拨承运商","港/站分拨承运轿运车车牌号","目的港/站缓存区出库时间","港/站分拨起运时间","送达时间-DCS到货时间","经销商确认到货时间","整车物流接收STD日期","同板数量","港/站分拨承运轿运车车位数"};
	  //循环遍历表头,作为sheet页的第一行数据
	  for(int i = 0 ; i < headers.length; i++){
		  //获取表头列
		  SXSSFCell cell = row.createCell(i);
		  HSSFRichTextString text = new HSSFRichTextString(headers[i]);
		  //为单元格设置值
		  cell.setCellValue(text);
	  }
	  //设置新增数据行,从第一行开始
	  int rowNum = 1;
	  //将列表的数据获取到Excel表中
	  for ( DwmVlmsOneOrderToEnd item : dataList ) {
		  SXSSFRow row1 = sheet.createRow(rowNum);
		  row1.createCell(0).setCellValue(item.getVin());//vin
		  row1.createCell(1).setCellValue(item.getBrand());//brand
		  row1.createCell(2).setCellValue(item.getBaseName());//baseName
		  row1.createCell(3).setCellValue(item.getVehicleName());//vehicleName
		  row1.createCell(4).setCellValue(item.getCp9OfflineTime());//cp9OfflineTime
		  row1.createCell(5).setCellValue(item.getLeaveFactoryTime());//leaveFactoryTime
		  row1.createCell(6).setCellValue(item.getInSiteTime());//inSiteTime
		  row1.createCell(7).setCellValue(item.getInWarehouseName());//inWarehouseName
		  row1.createCell(8).setCellValue(item.getTaskNo());//taskNo
		  row1.createCell(9).setCellValue(item.getStowageNoteTime());//stowageNoteTime
		  row1.createCell(10).setCellValue(item.getStowageNoteNo());//stowageNoteNo
		  row1.createCell(11).setCellValue(item.getTrafficType());//trafficType
		  row1.createCell(12).setCellValue(item.getAssignTime());//assignTime
		  row1.createCell(13).setCellValue(item.getCarrierName());//carrierName
		  row1.createCell(14).setCellValue(item.getActualOutTime());//actualOutTime
		  row1.createCell(15).setCellValue(item.getShipmentTime());//shipmentTime
		  row1.createCell(16).setCellValue(item.getTransportVehicleNo());//transportVehicleNo
		  row1.createCell(17).setCellValue(item.getVehicleNum());//vehicleNum
		  row1.createCell(18).setCellValue(item.getStartCityName());//startCityName
		  row1.createCell(19).setCellValue(item.getEndCityName());//endCityName
		  row1.createCell(20).setCellValue(item.getVdwdm());//vdwdm
		  row1.createCell(21).setCellValue(item.getStartWaterwayName());//startWaterwayName
		  row1.createCell(22).setCellValue(item.getInStartWaterwayTime());//inStartWaterwayTime
		  row1.createCell(23).setCellValue(item.getEndStartWaterwayTime());//endStartWaterwayTime
		  row1.createCell(24).setCellValue(item.getEndWaterwayName());//endWaterwayName
		  row1.createCell(25).setCellValue(item.getInEndWaterwayTime());//inEndWaterwayTime
		  row1.createCell(26).setCellValue(item.getUnloadShipTime());//unloadShipTime
		  row1.createCell(27).setCellValue(item.getInDistributeTime());//inDistributeTime
		  row1.createCell(28).setCellValue(item.getDistributeAssignTime());//distributeAssignTime
		  row1.createCell(29).setCellValue(item.getDistributeCarrierName());//distributeCarrierName
		  row1.createCell(30).setCellValue(item.getDistributeVehicleNo());//distributeVehicleNo
		  row1.createCell(31).setCellValue(item.getOutDistributeTime());//outDistributeTime
		  row1.createCell(32).setCellValue(item.getDistributeShipmentTime());//distributeShipmentTime
		  row1.createCell(33).setCellValue(item.getDotSiteTime());//dotSiteTime
		  row1.createCell(34).setCellValue(item.getFinalSiteTime());//finalSiteTime
		  row1.createCell(35).setCellValue(item.getVehicleReceivingTime());//vehicleReceivingTime
		  row1.createCell(36).setCellValue(item.getSamePlateNum());//samePlateNum
		  row1.createCell(37).setCellValue(item.getDistributeVehicleNum());//distributeVehicleNum
		  rowNum ++;
	  }
	  ServletOutputStream outputStream = response.getOutputStream();
	  wb.write(outputStream);
	  outputStream.close();

	return null;
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