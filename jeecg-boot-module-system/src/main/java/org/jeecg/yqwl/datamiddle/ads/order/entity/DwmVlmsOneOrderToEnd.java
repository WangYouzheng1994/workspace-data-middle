package org.jeecg.yqwl.datamiddle.ads.order.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
@Data
@TableName("dwm_vlms_one_order_to_end")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="dwm_vlms_one_order_to_end对象", description="一单到底")
public class DwmVlmsOneOrderToEnd {
    
//	/**idnum*/
//	@Excel(name = "idnum", width = 15)
//    @ApiModelProperty(value = "idnum")
//	private Long idnum;
	/**车架号 底盘号*/
	@Excel(name = "车架号 底盘号", width = 15)
    @ApiModelProperty(value = "车架号 底盘号")
	private String vin;
	/**车型*/
	@Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "车型")
	private String vehicleType;
	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private String brand;
	/**cp9下线接车时间*/
	@Excel(name = "cp9下线接车时间", width = 15)
    @ApiModelProperty(value = "cp9下线接车时间")
	private Long cp9OfflineTime;
	/**出厂日期*/
	@Excel(name = "出厂日期", width = 15)
    @ApiModelProperty(value = "出厂日期")
	private Long leaveFactoryTime;
	/**入库时间  及入一汽物流基地*/
	@Excel(name = "入库时间  及入一汽物流基地", width = 15)
    @ApiModelProperty(value = "入库时间  及入一汽物流基地")
	private Long inSiteTime;
	/**入库名称*/
	@Excel(name = "入库名称", width = 15)
    @ApiModelProperty(value = "入库名称")
	private String inWarehouseName;
	/**计划下达时间 取sptb02.dpzrq*/
	@Excel(name = "计划下达时间 取sptb02.dpzrq", width = 15)
    @ApiModelProperty(value = "计划下达时间 取sptb02.dpzrq")
	private Long planReleaseTime;
	/**任务单号 取sptb02.cjhdh*/
	@Excel(name = "任务单号 取sptb02.cjhdh", width = 15)
    @ApiModelProperty(value = "任务单号 取sptb02.cjhdh")
	private String taskNo;
	/**配载单编号 Y号  取sptb02.cpzdbh*/
	@Excel(name = "配载单编号 Y号  取sptb02.cpzdbh", width = 15)
    @ApiModelProperty(value = "配载单编号 Y号  取sptb02.cpzdbh")
	private String stowageNoteNo;
	/**配载单日期   sptb02.DPZRQ*/
	@Excel(name = "配载单日期   sptb02.DPZRQ", width = 15)
    @ApiModelProperty(value = "配载单日期   sptb02.DPZRQ")
	private Long stowageNoteTime;
	/**运单指派时间*/
	@Excel(name = "运单指派时间", width = 15)
    @ApiModelProperty(value = "运单指派时间")
	private Long assignTime;
	/**运输方式*/
	@Excel(name = "运输方式", width = 15)
    @ApiModelProperty(value = "运输方式")
	private String trafficType;
	/**承运商名称*/
	@Excel(name = "承运商名称", width = 15)
    @ApiModelProperty(value = "承运商名称")
	private String carrierName;
	/**实际出库时间*/
	@Excel(name = "实际出库时间", width = 15)
    @ApiModelProperty(value = "实际出库时间")
	private Long actualOutTime;
	/**实际起运时间*/
	@Excel(name = "实际起运时间", width = 15)
    @ApiModelProperty(value = "实际起运时间")
	private Long shipmentTime;
	/**运输车号*/
	@Excel(name = "运输车号", width = 15)
    @ApiModelProperty(value = "运输车号")
	private String transportVehicleNo;
	/**轿车数量 只计算公路运输*/
	@Excel(name = "轿车数量 只计算公路运输", width = 15)
    @ApiModelProperty(value = "轿车数量 只计算公路运输")
	private Integer vehicleNum;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15)
    @ApiModelProperty(value = "创建时间")
	private Long warehouseCreatetime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 15)
    @ApiModelProperty(value = "更新时间")
	private Long warehouseUpdatetime;
	/**始发城市*/
	@Excel(name = "始发城市", width = 15)
    @ApiModelProperty(value = "始发城市")
	private String startCityName;
	/**目的城市*/
	@Excel(name = "目的城市", width = 15)
    @ApiModelProperty(value = "目的城市")
	private String endCityName;
	/**经销商代码*/
	@Excel(name = "经销商代码", width = 15)
    @ApiModelProperty(value = "经销商代码")
	private String vdwdm;
	/**经销商名称*/
	@Excel(name = "经销商名称", width = 15)
    @ApiModelProperty(value = "经销商名称")
	private String dealerName;
	/**开始站台 应用于铁路*/
	@Excel(name = "开始站台 应用于铁路", width = 15)
    @ApiModelProperty(value = "开始站台 应用于铁路")
	private String startPlatformName;
	/**目的站台 应用于铁路*/
	@Excel(name = "目的站台 应用于铁路", width = 15)
    @ApiModelProperty(value = "目的站台 应用于铁路")
	private String endPlatformName;
	/**入开始站台时间 应用于铁路*/
	@Excel(name = "入开始站台时间 应用于铁路", width = 15)
    @ApiModelProperty(value = "入开始站台时间 应用于铁路")
	private Long inStartPlatformTime;
	/**出开始站台时间 应用于铁路*/
	@Excel(name = "出开始站台时间 应用于铁路", width = 15)
    @ApiModelProperty(value = "出开始站台时间 应用于铁路")
	private Long outStartPlatformTime;
	/**入目的站台时间 应用于铁路*/
	@Excel(name = "入目的站台时间 应用于铁路", width = 15)
    @ApiModelProperty(value = "入目的站台时间 应用于铁路")
	private Long inEndPlatformTime;
	/**卸车时间  应用于铁路*/
	@Excel(name = "卸车时间  应用于铁路", width = 15)
    @ApiModelProperty(value = "卸车时间  应用于铁路")
	private Long unloadRailwayTime;
	/**开始港口名称 应用于水路*/
	@Excel(name = "开始港口名称 应用于水路", width = 15)
    @ApiModelProperty(value = "开始港口名称 应用于水路")
	private String startWaterwayName;
	/**目的港口名称 应用于水路*/
	@Excel(name = "目的港口名称 应用于水路", width = 15)
    @ApiModelProperty(value = "目的港口名称 应用于水路")
	private String endWaterwayName;
	/**入开始港口时间 应用于水路*/
	@Excel(name = "入开始港口时间 应用于水路", width = 15)
    @ApiModelProperty(value = "入开始港口时间 应用于水路")
	private Long inStartWaterwayTime;
	/**出开始港口时间 应用于水路*/
	@Excel(name = "出开始港口时间 应用于水路", width = 15)
    @ApiModelProperty(value = "出开始港口时间 应用于水路")
	private Long endStartWaterwayTime;
	/**入目的港口时间 应用于水路*/
	@Excel(name = "入目的港口时间 应用于水路", width = 15)
    @ApiModelProperty(value = "入目的港口时间 应用于水路")
	private Long inEndWaterwayTime;
	/**卸船时间 应用水路*/
	@Excel(name = "卸船时间 应用水路", width = 15)
    @ApiModelProperty(value = "卸船时间 应用水路")
	private Long unloadShipTime;
	/**末端分拨中心 入库时间*/
	@Excel(name = "末端分拨中心 入库时间", width = 15)
    @ApiModelProperty(value = "末端分拨中心 入库时间")
	private Integer inDistributeTime;
	/**末端分拨中心 出库时间*/
	@Excel(name = "末端分拨中心 出库时间", width = 15)
    @ApiModelProperty(value = "末端分拨中心 出库时间")
	private Long outDistributeTime;
	/**末端分拨中心 指派时间*/
	@Excel(name = "末端分拨中心 指派时间", width = 15)
    @ApiModelProperty(value = "末端分拨中心 指派时间")
	private Long distributeAssignTime;
	/**末端分拨中心 承运商名称*/
	@Excel(name = "末端分拨中心 承运商名称", width = 15)
    @ApiModelProperty(value = "末端分拨中心 承运商名称")
	private String distributeCarrierName;
	/**末端分拨中心 承运轿车车牌号*/
	@Excel(name = "末端分拨中心 承运轿车车牌号", width = 15)
    @ApiModelProperty(value = "末端分拨中心 承运轿车车牌号")
	private String distributeVehicleNo;
	/**末端分拨中心 起运时间*/
	@Excel(name = "末端分拨中心 起运时间", width = 15)
    @ApiModelProperty(value = "末端分拨中心 起运时间")
	private Long distributeShipmentTime;
	/**公路打点到货时间*/
	@Excel(name = "公路打点到货时间", width = 15)
    @ApiModelProperty(value = "公路打点到货时间")
	private Long dotSiteTime;
	/**最终到货时间*/
	@Excel(name = "最终到货时间", width = 15)
    @ApiModelProperty(value = "最终到货时间")
	private Long finalSiteTime;
	/**结算单编号 多个逗号隔开*/
	@Excel(name = "结算单编号 多个逗号隔开", width = 15)
    @ApiModelProperty(value = "结算单编号 多个逗号隔开")
	private String settleNo;

	/**基地*/
	@Excel(name = "基地", width = 15)
	@ApiModelProperty(value = "基地")
	private String baseName;
}
