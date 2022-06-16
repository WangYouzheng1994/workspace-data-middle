package org.jeecg.yqwl.datamiddle.ads.order.controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.netty.util.internal.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
  @PostMapping(value = "/exportXls")
  public void exportXls(@RequestBody GetQueryCriteria queryCriteria ,HttpServletResponse response) throws IOException {
//	  System.out.println(System.currentTimeMillis());
	  //创建工作簿
	  SXSSFWorkbook wb = new SXSSFWorkbook();
	  //在工作簿中创建sheet页
	  SXSSFSheet sheet = wb.createSheet("sheet1");
	  //创建行,从0开始
	  SXSSFRow row = sheet.createRow(0);
	  //获取表头(前端页面一共有44个字段,entity一共是57个字段)
//	  Field[] fileds = DwmVlmsOneOrderToEnd.class.getDeclaredFields();
	  String[] headers = new String[]{"底盘号","品牌","基地","车型","CP9下线接车日期","出厂日期","入库日期","入库仓库","任务单号",
			  "整车物流接收STD日期","配板日期","配板单号","运输方式","指派日期","指派承运商名称","出库日期","起运日期-公路/铁路",
			  "运输车号","同板数量","轿运车车位数","始发城市","目的城市","经销商代码","始发港名称","到达始发港口时间/入港时间",
			  "始发港口水运离港时间","目的港名称","到达目的港时间","始发站名称","到达始发站时间/入站时间","始发站台铁路离站时间",
			  "目的站名称","到达目的站时间","卸船时间（水路到目的站）","卸车时间（铁路到目的站）","末端分拨中心入库时间",
			  "末端分拨中心指派时间","末端分拨承运商","分拨承运轿运车车牌号","港/站分拨承运轿运车车位数","分拨出库时间",
			  "分拨起运时间","送达时间-DCS到货时间","经销商确认到货时间"};
	  int i = 0;
	  //循环遍历表头,作为sheet页的第一行数据
	  for ( String header : headers ) {
		  //获取表头列
		  SXSSFCell cell = row.createCell(i++);
		  //为单元格赋值
		  cell.setCellValue(header);
	  }
	  //转换时间格式,将Long类型转换成date类型
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  //设置新增数据行,从第一行开始
	  int rowNum = 1;
	  String vin = queryCriteria.getVin();
	  if (StringUtil.length(vin) > 2 && StringUtils.contains(vin, ",")) {
		  queryCriteria.setVinList(Arrays.asList(StringUtils.split(vin, ",")));
	  }
	  // TODO:过滤选中的数据
	  String selections = queryCriteria.getSelections();
	  if ( StringUtil.length(selections) > 2  ) {
		  queryCriteria.setVinList(Arrays.asList(StringUtils.split(selections,",")));
	  }
	  formatQueryTime(queryCriteria);
	  Integer pageNo = 1;
	  Integer pageSize = 5000;

	  boolean intervalFlag = true;
	  queryCriteria.setPageSize(pageSize);

	  List<DwmVlmsOneOrderToEnd> pageList = null;

	  Integer maxSize = 200000;
	  Integer currentSize = 0;

	  do {
		  queryCriteria.setPageNo(pageNo);

		  //获取查询数据
		  pageList = dwmVlmsOneOrderToEndService.selectOneOrderToEndList(queryCriteria);
		  for ( DwmVlmsOneOrderToEnd item : pageList ) {
			  //时间字段转换成年月日时分秒类型
			  SXSSFRow row1 = sheet.createRow(rowNum);
			  //vin
			  row1.createCell(0).setCellValue(item.getVin());
			  //brand
			  row1.createCell(1).setCellValue(item.getBrand());
			  //baseName
			  row1.createCell(2).setCellValue(item.getBaseName());
			  //vehicleName
			  row1.createCell(3).setCellValue(item.getVehicleName());
			  //cp9OfflineTime
			  if ( item.getCp9OfflineTime() != 0 ) {
				  row1.createCell(4).setCellValue(sdf.format(item.getCp9OfflineTime()));
			  }
			  //leaveFactoryTime
			  if ( item.getLeaveFactoryTime() != 0 ) {
				  row1.createCell(5).setCellValue(sdf.format(item.getLeaveFactoryTime() ));
			  }
			  //inSiteTime
			  if ( item.getInSiteTime() != 0 ) {
				  row1.createCell(6).setCellValue(sdf.format(item.getInSiteTime()));
			  }
			  //inWarehouseName
			  row1.createCell(7).setCellValue(item.getInWarehouseName());
			  //taskNo
			  row1.createCell(8).setCellValue(item.getTaskNo());
			  //vehicleReceivingTime
			  if ( item.getVehicleReceivingTime() != 0 ) {
				  row1.createCell(9).setCellValue(sdf.format(item.getVehicleReceivingTime()));
			  }
			  //stowageNoteTime
			  if ( item.getStowageNoteTime() != 0 ) {
				  row1.createCell(10).setCellValue(sdf.format(item.getStowageNoteTime() ));
			  }
			  //stowageNoteNo
			  row1.createCell(11).setCellValue(item.getStowageNoteNo());
			  //trafficType
			  row1.createCell(12).setCellValue(item.getTrafficType());
			  //assignTime
			  if ( item.getAssignTime() != 0 ) {
				  row1.createCell(13).setCellValue(sdf.format(item.getAssignTime() ));
			  }
			  //carrierName
			  row1.createCell(14).setCellValue(item.getCarrierName());
			  //actualOutTime
			  if ( item.getActualOutTime() != 0 ) {
				  row1.createCell(15).setCellValue(sdf.format(item.getActualOutTime()));
			  }
			  //shipmentTime
			  if ( item.getShipmentTime() != 0 ) {
				  row1.createCell(16).setCellValue(sdf.format(item.getShipmentTime() ));
			  }
			  //transportVehicleNo
			  row1.createCell(17).setCellValue(item.getTransportVehicleNo());
			  //samePlateNum
			  row1.createCell(18).setCellValue(item.getSamePlateNum());
			  //vehicleNum
			  row1.createCell(19).setCellValue(item.getVehicleNum());
			  //startCityName
			  row1.createCell(20).setCellValue(item.getStartCityName());
			  //endCityName
			  row1.createCell(21).setCellValue(item.getEndCityName());
			  //vdwdm
			  row1.createCell(22).setCellValue(item.getVdwdm());
			  //startWaterwayName
			  row1.createCell(23).setCellValue(item.getStartWaterwayName());
			  //inStartWaterwayTime
			  if ( item.getInStartWaterwayTime() != 0 ) {
				  row1.createCell(24).setCellValue(sdf.format(item.getInStartWaterwayTime() ));
			  }
			  //endStartWaterwayTime
			  if ( item.getEndStartWaterwayTime() != 0 ) {
				  row1.createCell(25).setCellValue(sdf.format(item.getEndStartWaterwayTime() ));
			  }
			  //endWaterwayName
			  row1.createCell(26).setCellValue(item.getEndWaterwayName());
			  //inEndWaterwayTime
			  if ( item.getInEndWaterwayTime() != 0 ) {
				  row1.createCell(27).setCellValue(sdf.format(item.getInEndWaterwayTime() ));
			  }
			  //startPlatformName
			  row1.createCell(28).setCellValue(item.getStartPlatformName());
			  //inStartPlatformTime
			  if ( item.getInStartPlatformTime() != 0 ) {
				  row1.createCell(29).setCellValue(sdf.format(item.getInStartPlatformTime() ));
			  }
			  //outStartPlatformTime
			  if ( item.getOutStartPlatformTime() != 0 ) {
				  row1.createCell(30).setCellValue(sdf.format(item.getOutStartPlatformTime() ));
			  }
			  //endPlatformName
			  row1.createCell(31).setCellValue(item.getEndPlatformName());
			  //inEndPlatformTime
			  if ( item.getInEndPlatformTime() != 0 ) {
				  row1.createCell(32).setCellValue(sdf.format(item.getInEndPlatformTime()));
			  }
			  //unloadShipTime
			  if ( item.getUnloadShipTime() != 0 ) {
				  row1.createCell(33).setCellValue(sdf.format(item.getUnloadShipTime() ));
			  }
			  //unloadRailwayTime
			  if ( item.getUnloadRailwayTime() != 0 ) {
				  row1.createCell(34).setCellValue(sdf.format(item.getUnloadRailwayTime()));
			  }
			  //inDistributeTime
			  if ( item.getInDistributeTime() != 0 ) {
				  row1.createCell(35).setCellValue(sdf.format(item.getInDistributeTime() ));
			  }
			  //distributeAssignTime
			  if ( item.getDistributeAssignTime() != 0 ) {
				  row1.createCell(36).setCellValue(sdf.format(item.getDistributeAssignTime()));
			  }
			  //distributeCarrierName
			  row1.createCell(37).setCellValue(item.getDistributeCarrierName());
			  //distributeVehicleNo
			  row1.createCell(38).setCellValue(item.getDistributeVehicleNo());
			  //distributeVehicleNum
			  row1.createCell(39).setCellValue(item.getDistributeVehicleNum());
			  //outDistributeTime
			  if ( item.getOutDistributeTime() != 0 ) {
				  row1.createCell(40).setCellValue(sdf.format(item.getOutDistributeTime()));
			  }
			  //distributeShipmentTime
			  if ( item.getDistributeShipmentTime() != 0 ) {
				  row1.createCell(41).setCellValue(sdf.format(item.getDistributeShipmentTime()));
			  }
			  //dotSiteTime
			  if ( item.getDotSiteTime() != 0 ) {
				  row1.createCell(42).setCellValue(sdf.format(item.getDotSiteTime()));
			  }
			  //finalSiteTime
			  if ( item.getFinalSiteTime() != 0 ) {
				  row1.createCell(43).setCellValue(sdf.format(item.getFinalSiteTime()));
			  }
			  rowNum ++;
		  }

		  // 如果没有查询到数据  或者 分页查询的数量不符合pageSize 那么终止循环
		  if (CollectionUtils.isEmpty(pageList) || CollectionUtils.size(pageList) < pageSize ) {
			  intervalFlag = false;
		  } else {
			  pageNo++;
		  }
		  currentSize++;
		  if (currentSize >= maxSize) {
			  intervalFlag = false;
		  }
	  } while(intervalFlag);

	  //设置内容类型
	  response.setContentType("application/vnd.ms-excel;charset=utf-8");
	  OutputStream outputStream = response.getOutputStream();
	  response.setHeader("Content-disposition", "attachment;filename=trainingRecord.xls");
	  wb.write(outputStream);
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

	  formatQueryTime(queryCriteria);
	  // Add By WangYouzheng 2022年6月9日17:39:33 新增vin码批量查询功能。 根据英文逗号或者回车换行分割，只允许一种情况 --- END
	  Integer total = dwmVlmsOneOrderToEndService.countOneOrderToEndList(queryCriteria);
	  Page<DwmVlmsOneOrderToEnd> page = new Page<DwmVlmsOneOrderToEnd>(queryCriteria.getPageNo(), queryCriteria.getPageSize());
	  List<DwmVlmsOneOrderToEnd> pageList = dwmVlmsOneOrderToEndService.selectOneOrderToEndList(queryCriteria);
	  page.setRecords(pageList);
	  page.setTotal(total);
	  return Result.OK(page);
  }
	private void formatQueryTime(GetQueryCriteria queryCriteria) {
		if (queryCriteria.getLeaveFactoryTimeStart() != null) {
			queryCriteria.setLeaveFactoryTimeStart(queryCriteria.getLeaveFactoryTimeStart() + 28800000);
		}
		if (queryCriteria.getLeaveFactoryTimeEnd() != null) {
			queryCriteria.setLeaveFactoryTimeEnd(queryCriteria.getLeaveFactoryTimeEnd()+ 28800000);
		}
		if (queryCriteria.getInSiteTimeStart() != null) {
			queryCriteria.setInSiteTimeStart(queryCriteria.getInSiteTimeStart()+ 28800000);
		}
		if (queryCriteria.getInSiteTimeEnd() != null) {
			queryCriteria.setInSiteTimeEnd(queryCriteria.getInSiteTimeEnd()+ 28800000);
		}
		if (queryCriteria.getCp9OfflineTimeStart() != null) {
			queryCriteria.setCp9OfflineTimeStart(queryCriteria.getCp9OfflineTimeStart()+ 28800000);
		}
		if (queryCriteria.getCp9OfflineTimeEnd() != null) {
			queryCriteria.setCp9OfflineTimeEnd(queryCriteria.getCp9OfflineTimeEnd()+ 28800000);
		}
	}
}