package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @Description: rfid+sitewarehouse宽表  dim_vlms_warehouse_rs
 * @Author: xiaofeng
 * @Date:   2022-06-09
 * @Version: V1.0
 */
@Data
@TableName("dim_vlms_warehouse_rs")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DimWarehouseRS {
    
	/**idnum*/
	private java.lang.Integer idnum;

	/**站点代码*/
	private java.lang.String vwlckdm;

	/**站点名称*/
	private java.lang.String vwlckmc;

	/**库房ID*/
	private java.lang.Integer warehouseId;

	/**库房代码*/
	private java.lang.String warehouseCode;

	/**库房名称*/
	private java.lang.String warehouseName;

	/**备注*/
	private java.lang.String cbz;

	/**操作日期*/
	private java.lang.Integer dczrq;

	/**操作员*/
	private java.lang.String cczydm;

	/**类型：对照contrast；仓库：warehouse*/
	private java.lang.String type;

	/**id*/
	private java.lang.Integer id;

	/**归属基地*/
	private java.lang.String partBase;

	/**库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）*/
	private java.lang.String warehouseType;

	/**站台对应的城市*/
	private java.lang.String city;

	/**默认在库量  number 转 int*/
	private java.lang.Integer defalutValue;

	/**标准库容     number 转 int*/
	private java.lang.Integer bzkr;

	/**最大库容     number 转 int*/
	private java.lang.Integer zdkr;

	/**数据中台代码*/
	private java.lang.String datacenterCode;

	/**顺序 2019 12 23 宋琳添加   number 转 int*/
	private java.lang.Integer sequence;

	/**省份2019 12 23 宋琳添加*/
	private java.lang.String province;

	/**整车数据代码*/
	private java.lang.String zcCode;

	/**主机公司代码 1 大众  2 奔腾 3解放  17 红旗  29 马自达*/
	private java.lang.String czjgsdm;

	/**数据中台代码备份*/
	private java.lang.String datacenterCodeBzk;

	/**所属省份*/
	private java.lang.String provice;

	/**warehouseCreatetime*/
	private java.lang.Integer warehouseCreatetime;

	/**warehouseUpdatetime*/
	private java.lang.Integer warehouseUpdatetime;
}
