package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb01c")
public class Sptb01c implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 移动计划
     */
      @TableField("CPCDBH")
    private String cpcdbh;

      /**
     * 单据日期
     */
      @TableField("DDJRQ")
    private Long ddjrq;

      /**
     * 操作员代码·
     */
      @TableField("VCZYDM")
    private String vczydm;

      /**
     * 物理仓库
     */
      @TableField("VWLCKDM")
    private String vwlckdm;

      /**
     * 状态 2下达 3运输确认 4实现 9作废
     */
      @TableField("CZT")
    private String czt;

      /**
     * 主机公司的运输方式，且通过它来区分是否联运
     */
      @TableField("CZLDBH")
    private String czldbh;

      /**
     * 备注 100 20190403 100-500
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 运输类型
     */
      @TableField("CYSLX")
    private String cyslx;

      /**
     * 指令单上带回的移动类型
     */
      @TableField("CPCDYDLX")
    private String cpcdydlx;

      /**
     * 单位代码
     */
      @TableField("VDWDM")
    private String vdwdm;

      /**
     * 收车地址   100 - 200
     */
      @TableField("VSCDZ")
    private String vscdz;

      /**
     * 到货地代码
     */
      @TableField("CDHDDM")
    private String cdhddm;

      /**
     * 发运地点
     */
      @TableField("CFYDD")
    private String cfydd;

      /**
     * 类型 0/自动形成，1/手工维护
     */
      @TableField("CLY")
    private String cly;

      /**
     * V平台的运输方式。根据主机公司的转换而来
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * 对应单编号/任务单　20-30
     */
      @TableField("CDYDBH")
    private String cdydbh;

      /**
     * 清单号 20-30
     */
      @TableField("CJHDH")
    private String cjhdh;

      /**
     * 计划类型
     */
      @TableField("CJHLX")
    private String cjhlx;

      /**
     * 所属销售公司 2-36
     */
      @TableField("CSSGS")
    private String cssgs;

      /**
     * 移动计划下达时间
     */
      @TableField("DXDSJ")
    private Long dxdsj;

      /**
     * 运输计划员接收时间
     */
      @TableField("DYSJSSJ")
    private Long dysjssj;

      /**
     * R3运单号 20-30
     */
      @TableField("CDYDBH1")
    private String cdydbh1;

      /**
     * 调度员
     */
      @TableField("CDDY")
    private String cddy;

      /**
     * 导入时间
     */
      @TableField("DDRSJ")
    private Long ddrsj;

      /**
     * 大客户代码
     */
      @TableField("CDKHDM")
    private String cdkhdm;

      /**
     * 承运商代码
     */
      @TableField("CCYSDM")
    private String ccysdm;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

      /**
     * 大众传来的辅板的运输代码(SPG号),但尚未与MDAC52_SPG表关联
     */
      @TableField("CSPGDM")
    private String cspgdm;

      /**
     * 大众传来的辅板的运输商简称
     */
      @TableField("CSPGNM")
    private String cspgnm;

      /**
     * 0不是、1是A2车壳计划
     */
      @TableField("CA2JHBS")
    private String ca2jhbs;

      /**
     * 发车站台
     */
      @TableField("VFCZT")
    private String vfczt;

      /**
     * 收车站台 20-100 20220315
     */
      @TableField("VSCZT")
    private String vsczt;

      /**
     * 批次号（铁路计划）
     */
      @TableField("VPH")
    private String vph;

      /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */
      @TableField("CQWH")
    private String cqwh;

      /**
     * 到货地省份。如江苏、南京，确保新增地点在TMS系统能直接定位到城市，提高系统运行效率
     */
      @TableField("VSQMC")
    private String vsqmc;

      /**
     * 到货地城市
     */
      @TableField("VSXMC")
    private String vsxmc;

      /**
     * 区域公司。用于计划盲分，E表示未确定区域公司
     */
      @TableField("VQYGS")
    private String vqygs;

      /**
     * 主机公司代码  字典（WTDW）
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

      /**
     * 运输车。用于客车
     */
      @TableField("VYSCDM")
    private String vyscdm;

      /**
     * 最远里程。回头置SPTB02.NSFFY
     */
      @TableField("NLC")
    private Long nlc;

      /**
     * 线中代码。资源自动分配，来源于sptb_xl01
     */
      @TableField("CXLDM")
    private String cxldm;

      /**
     * 预分区域。资源自动分配
     */
      @TableField("VQYGS_YF")
    private String vqygsYf;

      /**
     * 预分运输商。资源自动分配
     */
      @TableField("CYSSDM_YF")
    private String cyssdmYf;

      /**
     * 预分区域。资源自动分配，且不变
     */
      @TableField("VQYGS_YF1")
    private String vqygsYf1;

      /**
     * 预分运输商。资源自动分配，且不变
     */
      @TableField("CYSSDM_YF1")
    private String cyssdmYf1;

      /**
     * 资源分配参考日期
     */
      @TableField("DZYRQ")
    private Long dzyrq;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 实际运输商。主要针对轿运
     */
      @TableField("CYSSDM_SJ")
    private String cyssdmSj;

      /**
     * 解放长春 经销商对应线路包代码 
     */
      @TableField("CXLDM_JFCC")
    private String cxldmJfcc;

      /**
     * 解放成都 经销商对应线路包代码
     */
      @TableField("CXLDM_JFCD")
    private String cxldmJfcd;

      /**
     * 解放青岛 经销商对应线路包代码
     */
      @TableField("CXLDM_JFQD")
    private String cxldmJfqd;

      /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */
      @TableField("CXSGSDM")
    private String cxsgsdm;

      /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */
      @TableField("VXSGSMC")
    private String vxsgsmc;

      /**
     * 移动类型描述
     */
      @TableField("CYDLXMC")
    private String cydlxmc;

      /**
     * 区分解放青岛轻卡和重卡 add by lwx 20181206
     */
      @TableField("VCYSDM")
    private String vcysdm;

      /**
     * 是否J6
     */
      @TableField("QYCX")
    private String qycx;

    @TableField("DPPSJ_JF")
    private Long dppsjJf;

      /**
     * 运输类型描述
     */
      @TableField("CYSLXMS")
    private String cyslxms;

      /**
     * 单价 解放青岛用
     */
      @TableField("NDJ")
    private BigDecimal ndj;

      /**
     * 20190927 马自达和轿车品牌 联运计划最大段数
     */
      @TableField("N_ZDXH")
    private Integer nZdxh;

      /**
     * 20191217 QDC X传统能源；D纯电；F油电混动；
     */
      @TableField("EV_FLAG")
    private String evFlag;

      /**
     * 特殊发运车辆标识
     */
      @TableField("SV_FLAG")
    private String svFlag;

      /**
     * 内部流水号
     */
      @TableField("INTER_CODE")
    private String interCode;

      /**
     * 发车单位名称城市
     */
      @TableField("NAME_FROM")
    private String nameFrom;

      /**
     * 收车单位名称城市 
     */
      @TableField("NAME_TO")
    private String nameTo;

      /**
     * 发车人及联系方式
     */
      @TableField("SEN_PERSON")
    private String senPerson;

      /**
     * 收车人及联系方式
     */
      @TableField("REC_PERSON")
    private String recPerson;

      /**
     * 完整经办人及联系方式
     */
      @TableField("PERSON_PHONE")
    private String personPhone;

      /**
     * 发货单位地址
     */
      @TableField("ADDRESS_FROM")
    private String addressFrom;

      /**
     * 收货单位地址
     */
      @TableField("ADDRESS_TO")
    private String addressTo;

      /**
     * 发货时间
     */
      @TableField("REQ_DATE")
    private Long reqDate;

      /**
     * 要求送达时间
     */
      @TableField("FH_DATE")
    private Long fhDate;

      /**
     * 车辆类型
     */
      @TableField("SUBMI")
    private String submi;

      /**
     * 经办单位
     */
      @TableField("DEPTM")
    private String deptm;

      /**
     * 具体要求说明
     */
      @TableField("INFO")
    private String info;

      /**
     * 2位车型
     */
      @TableField("CARTYPE")
    private String cartype;

      /**
     * 品牌
     */
      @TableField("BRAND")
    private String brand;

      /**
     * 车型描述
     */
      @TableField("NAME2")
    private String name2;

      /**
     * 移动类型 奔腾和马自达用 20200513
     */
      @TableField("CYDLX")
    private String cydlx;

      /**
     * 20200518 富裕资源标识 默认0
     */
      @TableField("CBS_FYZY")
    private String cbsFyzy;

      /**
     * 20200601 DTF 资源分配日期  实际分配
     */
      @TableField("DZYFPRQ")
    private Long dzyfprq;

      /**
     * 20200604 DTF 资源预分日期  预分配
     */
      @TableField("DZYYFRQ")
    private Long dzyyfrq;

      /**
     * 20200608 富余资源分配 预分运输商代码
     */
      @TableField("CYSSDM_YF_FY")
    private String cyssdmYfFy;

      /**
     * 20200718 DTF 配板标识
     */
      @TableField("CPBBS")
    private String cpbbs;

      /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */
      @TableField("SV_TYPE")
    private String svType;

      /**
     * 20210712 DTF 解放资源类型（牵引/载货/自卸）
     */
      @TableField("VZYLX_JF")
    private String vzylxJf;

      /**
     * 创建时间
     */
      @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
      @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
