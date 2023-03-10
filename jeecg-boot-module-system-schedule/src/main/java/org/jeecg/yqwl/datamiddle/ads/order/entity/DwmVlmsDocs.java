package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 一单到底---DOCS查询
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
@Data
@TableName("dmw_vlms_sptb02")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DwmVlmsDocs {

	/**底盘号*/
	@Excel(name = "底盘号", width = 15)
	private String vvin;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
	private String hostComCode;

	/**基地*/
	@Excel(name = "基地", width = 15)
	private String baseName;

	/**车型名称*/
	@Excel(name = "车型代码", width = 15)
	private String vehicleCode;

	/**车型名称*/
	@Excel(name = "车型名称", width = 15)
	private String vehicleName;

	/**始发城市*/
	@Excel(name = "始发城市", width = 15)
	private String startCityName;

	/**经销商目标城市*/
	@Excel(name = "经销商目标城市", width = 15)
	private String endCityName;

	/**经销商代码*/
	@Excel(name = "经销商代码", width = 15)
	private String vdwdm;

	/**经销商名称*/
	@Excel(name = "经销商名称", width = 15)
	private String dealerName;

	/**计划下达日期*/
	@Excel(name = "计划下达日期", width = 15)
	private Long ddjrqR3;

	/**配板单号*/
	@Excel(name = "配板单号", width = 15)
	private String cpzdbh;

	/**指派日期*/
	@Excel(name = "指派日期", width = 15)
	private Long assignTime;

	/**指派承运商名称*/
	@Excel(name = "指派承运商名称", width = 15)
	private String transportName;

	/**出库日期*/
	@Excel(name = "出库日期", width = 15)
	private Long actualOutTime;

	/**起运日期*/
	@Excel(name = "起运日期", width = 15)
	private Long shipmentTime;

	/**运输车号*/
	@Excel(name = "运输车号", width = 15)
	private String vjsydm;

	/**同板数量*/
	@Excel(name = "同板数量", width = 15)
	private Integer samePlateNum;

	/**DCS到货时间*/
	@Excel(name = "DCS到货时间", width = 15)
	private Long dtvsdhsj;

	/**经销商确认到货时间*/
	@Excel(name = "经销商确认到货时间", width = 15)
	private Long finalSiteTime;

	/**
	 * 位置信息
	 */
	@Excel(name = "位置信息", width = 15)
	private String vwz;

	/**
	 * 汽车品牌名 取自MDAC10.VPPSM 20220825新增
	 */
	@Excel(name = "品牌", width = 15)
	private String brandName;

	/**
	 * 是否为源库数据 - 用于质量菜单详情页展示
	 */
	private Byte source;


}
