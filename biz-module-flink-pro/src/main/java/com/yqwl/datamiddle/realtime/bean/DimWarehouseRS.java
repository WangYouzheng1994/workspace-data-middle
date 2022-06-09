package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;


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
	private java.lang.Integer warehouse_Id;

	/**库房代码*/
	private java.lang.String warehouse_Code;

	/**库房名称*/
	private java.lang.String warehouse_Name;

	/**备注*/
	private java.lang.String cbz;

	/**操作日期*/
	private java.lang.Long dczrq;

	/**操作员*/
	private java.lang.String cczydm;

	/**类型：对照contrast；仓库：warehouse*/
	private java.lang.String type;

	/**id*/
	private java.lang.Integer id;

	/**归属基地*/
	private java.lang.String part_Base;

	/**库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）*/
	private java.lang.String warehouse_Type;

	/**站台对应的城市*/
	private java.lang.String city;

	/**默认在库量  number 转 int*/
	private java.lang.Integer defalut_Value;

	/**标准库容     number 转 int*/
	private java.lang.Integer bzkr;

	/**最大库容     number 转 int*/
	private java.lang.Integer zdkr;

	/**数据中台代码*/
	private java.lang.String datacenter_Code;

	/**顺序 2019 12 23 宋琳添加   number 转 int*/
	private java.lang.Integer sequence;

	/**省份2019 12 23 宋琳添加*/
	private java.lang.String province;

	/**整车数据代码*/
	private java.lang.String zc_Code;

	/**主机公司代码 1 大众  2 奔腾 3解放  17 红旗  29 马自达*/
	private java.lang.String czjgsdm;

	/**数据中台代码备份*/
	private java.lang.String datacenter_Code_Bzk;

	/**所属省份*/
	private java.lang.String provice;

	/**warehouseCreatetime*/
	private java.lang.Long warehouse_Createtime;

	/**warehouseUpdatetime*/
	private java.lang.Long warehouse_Updatetime;

	@TransientSink
	private Timestamp ts;

	public DimWarehouseRS(RfidWarehouse rfidWarehouse,SiteWarehouse siteWarehouse){
		mergeRfid(rfidWarehouse);
		mergeSite(siteWarehouse);
	}

	public void mergeSite(SiteWarehouse siteWarehouse){
		if (siteWarehouse!=null){
			if (StringUtils.isNotBlank(siteWarehouse.getVWLCKDM())){
				this.vwlckdm=siteWarehouse.getVWLCKDM();
			}
			if (StringUtils.isNotBlank(siteWarehouse.getVWLCKMC())){
				this.vwlckmc=siteWarehouse.getVWLCKMC();
			}
			if (siteWarehouse.getWAREHOUSE_ID()!=null){
				this.warehouse_Id=siteWarehouse.getWAREHOUSE_ID();
			}
			if (StringUtils.isNotBlank(siteWarehouse.getCBZ())){
				this.cbz=siteWarehouse.getCBZ();
			}
			if (siteWarehouse.getDCZRQ()!=null){
				this.dczrq=siteWarehouse.getDCZRQ();
			}
			if (StringUtils.isNotBlank(siteWarehouse.getCCZYDM())){
				this.cczydm=siteWarehouse.getCCZYDM();
			}
			if (StringUtils.isNotBlank(siteWarehouse.getTYPE())){
				this.type=siteWarehouse.getTYPE();
			}


		}
	}
	public void mergeRfid(RfidWarehouse rfidWarehouse){
		if (rfidWarehouse !=null){
			this.ts=rfidWarehouse.getTs();
			Timestamp ts=rfidWarehouse.getTs();
			Long time = ts.getTime();
			this.warehouse_Createtime=time;

			if (rfidWarehouse.getID()!=null){
				this.id=rfidWarehouse.getID();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getWAREHOUSE_CODE())){
				this.warehouse_Code=rfidWarehouse.getWAREHOUSE_CODE();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getWAREHOUSE_NAME())){
				this.warehouse_Name=rfidWarehouse.getWAREHOUSE_NAME();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getPART_BASE())){
				this.part_Base=rfidWarehouse.getPART_BASE();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getWAREHOUSE_TYPE())){
				this.warehouse_Type=rfidWarehouse.getWAREHOUSE_TYPE();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getCITY())){
				this.city=rfidWarehouse.getCITY();
			}
			if (rfidWarehouse.getDEFALUT_VALUE()!=null){
				this.defalut_Value=rfidWarehouse.getDEFALUT_VALUE();
			}
			if (rfidWarehouse.getBZKR()!=null){
				this.bzkr=rfidWarehouse.getBZKR();
			}
			if (rfidWarehouse.getZDKR()!=null){
				this.zdkr=rfidWarehouse.getZDKR();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getDATACENTER_CODE())){
				this.datacenter_Code=rfidWarehouse.getDATACENTER_CODE();
			}
			if (rfidWarehouse.getSEQUENCE()!=null){
				this.sequence=rfidWarehouse.getSEQUENCE();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getPROVINCE())){
				this.province=rfidWarehouse.getPROVICE();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getZC_CODE())){
				this.zc_Code=rfidWarehouse.getZC_CODE();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getCZJGSDM())){
				this.czjgsdm=rfidWarehouse.getCZJGSDM();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getDATACENTER_CODE_BZK())){
				this.datacenter_Code_Bzk=rfidWarehouse.getDATACENTER_CODE_BZK();
			}
			if (StringUtils.isNotBlank(rfidWarehouse.getPROVICE())){
				this.provice=rfidWarehouse.getPROVICE();
			}







		}
	}
}
