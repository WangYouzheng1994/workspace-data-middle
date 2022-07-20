package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
/**
 * @version 1.0
 * @Description
 * @ClassName OdsVlmsSptb22Dq
 * @Author YULUO
 * @Date 2022/7/20
 */

@Data
@TableName("ods_vlms_sptb22_dq")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ods_vlms_sptb22_dq对象", description = "OdsVlmsSptb22Dq")
public class OdsVlmsSptb22Dq {
    /**idnum*/
    @Excel(name = "idnum", width = 15)
    @ApiModelProperty(value = "idnum")
    private java.lang.Integer idnum;
    /**id*/
    @Excel(name = "id", width = 15)
    @ApiModelProperty(value = "id")
    private java.lang.Integer id;
    /**cpzh*/
    @Excel(name = "cpzh", width = 15)
    @ApiModelProperty(value = "cpzh")
    private java.lang.String cpzh;
    /**vwz*/
    @Excel(name = "vwz", width = 15)
    @ApiModelProperty(value = "vwz")
    private java.lang.String vwz;
    /**dcjsj*/
    @Excel(name = "dcjsj", width = 15)
    @ApiModelProperty(value = "dcjsj")
    private java.lang.Long dcjsj;
    /**vsd*/
    @Excel(name = "vsd", width = 15)
    @ApiModelProperty(value = "vsd")
    private java.lang.String vsd;
    /**cjsdbh*/
    @Excel(name = "cjsdbh", width = 15)
    @ApiModelProperty(value = "cjsdbh")
    private java.lang.String cjsdbh;
    /**csqdm*/
    @Excel(name = "csqdm", width = 15)
    @ApiModelProperty(value = "csqdm")
    private java.lang.String csqdm;
    /**csxdm*/
    @Excel(name = "csxdm", width = 15)
    @ApiModelProperty(value = "csxdm")
    private java.lang.String csxdm;
    /**dczrq*/
    @Excel(name = "dczrq", width = 15)
    @ApiModelProperty(value = "dczrq")
    private java.lang.Long dczrq;
    /**cdhbs*/
    @Excel(name = "cdhbs", width = 15)
    @ApiModelProperty(value = "cdhbs")
    private java.lang.String cdhbs;
    /**0 gps/ 1 lbs*/
    @Excel(name = "0 gps/ 1 lbs", width = 15)
    @ApiModelProperty(value = "0 gps/ 1 lbs")
    private java.lang.String clybs;
    /**离长时间*/
    @Excel(name = "离长时间", width = 15)
    @ApiModelProperty(value = "离长时间")
    private java.lang.Integer dlcsj;
    /**csq*/
    @Excel(name = "csq", width = 15)
    @ApiModelProperty(value = "csq")
    private java.lang.String csq;
    /**csx*/
    @Excel(name = "csx", width = 15)
    @ApiModelProperty(value = "csx")
    private java.lang.String csx;
    /**ddksj*/
    @Excel(name = "ddksj", width = 15)
    @ApiModelProperty(value = "ddksj")
    private java.lang.Long ddksj;
    /**车辆滞留标识（两次取得数据相同认为是滞留）*/
    @Excel(name = "车辆滞留标识（两次取得数据相同认为是滞留）", width = 15)
    @ApiModelProperty(value = "车辆滞留标识（两次取得数据相同认为是滞留）")
    private java.lang.String czlbs;
    /**上次位置*/
    @Excel(name = "上次位置", width = 15)
    @ApiModelProperty(value = "上次位置")
    private java.lang.String vywz;
    /**cbz*/
    @Excel(name = "cbz", width = 15)
    @ApiModelProperty(value = "cbz")
    private java.lang.String cbz;
    /**csjh*/
    @Excel(name = "csjh", width = 15)
    @ApiModelProperty(value = "csjh")
    private java.lang.String csjh;
    /**dlbssj*/
    @Excel(name = "dlbssj", width = 15)
    @ApiModelProperty(value = "dlbssj")
    private java.lang.Long dlbssj;
    /**vlbswz*/
    @Excel(name = "vlbswz", width = 15)
    @ApiModelProperty(value = "vlbswz")
    private java.lang.String vlbswz;
    /**采点时段形如'20091004 02'*/
    @Excel(name = "采点时段形如'20091004 02'", width = 15)
    @ApiModelProperty(value = "采点时段形如'20091004 02'")
    private java.lang.String csd;
    /**滞留小时*/
    @Excel(name = "滞留小时", width = 15)
    @ApiModelProperty(value = "滞留小时")
    private java.lang.Integer nljzt;
    /**经度*/
    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private java.math.BigDecimal ccbjd;
    /**纬度*/
    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private java.math.BigDecimal ccbwd;
    /**创建时间*/
    @Excel(name = "创建时间", width = 15)
    @ApiModelProperty(value = "创建时间")
    private java.lang.Long warehouseCreatetime;
    /**更新时间*/
    @Excel(name = "更新时间", width = 15)
    @ApiModelProperty(value = "更新时间")
    private java.lang.Long warehouseUpdatetime;

}
