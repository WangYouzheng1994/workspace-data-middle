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
	private Integer idnum;

	/**站点代码*/
	private String vwlckdm;

	/**站点名称*/
	private String vwlckmc;

	/**库房ID*/
	private Integer warehouse_Id;

	/**库房代码*/
	private String warehouse_Code;

	/**库房名称*/
	private String warehouse_Name;

	/**备注*/
	private String cbz;

	/**操作日期*/
	private Long dczrq;

	/**操作员*/
	private String cczydm;

	/**类型：对照contrast；仓库：warehouse*/
	private String type;

	/**id*/
	private Integer id;

	/**归属基地*/
	private String part_Base;

	/**库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）*/
	private String warehouse_Type;

	/**站台对应的城市*/
	private String city;

	/**默认在库量  number 转 int*/
	private Integer defalut_Value;

	/**标准库容     number 转 int*/
	private Integer bzkr;

	/**最大库容     number 转 int*/
	private Integer zdkr;

	/**数据中台代码*/
	private String datacenter_Code;

	/**顺序 2019 12 23 宋琳添加   number 转 int*/
	private Integer sequence;

	/**省份2019 12 23 宋琳添加*/
	private String province;

	/**整车数据代码*/
	private String zc_Code;

	/**主机公司代码 1 大众  2 奔腾 3解放  17 红旗  29 马自达*/
	private String czjgsdm;

	/**数据中台代码备份*/
	private String datacenter_Code_Bzk;

	/**所属省份*/
	private String provice;

	/**warehouseCreatetime*/
	private Long warehouse_Createtime;

	/**warehouseUpdatetime*/
	private Long warehouse_Updatetime;

	@TransientSink
	private Long ts;

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
			this.ts = rfidWarehouse.getTs();
			this.warehouse_Createtime=rfidWarehouse.getTs();

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
