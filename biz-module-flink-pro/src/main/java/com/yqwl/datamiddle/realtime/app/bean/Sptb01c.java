package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

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
public class Sptb01c implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 移动计划
     */
          
    private String cpcdbh;

      /**
     * 单据日期
     */
          
    private Long ddjrq;

      /**
     * 操作员代码·
     */
          
    private String vczydm;

      /**
     * 物理仓库
     */
          
    private String vwlckdm;

      /**
     * 状态 2下达 3运输确认 4实现 9作废
     */
          
    private String czt;

      /**
     * 主机公司的运输方式，且通过它来区分是否联运
     */
          
    private String czldbh;

      /**
     * 备注 100 20190403 100-500
     */
          
    private String vbz;

      /**
     * 运输类型
     */
          
    private String cyslx;

      /**
     * 指令单上带回的移动类型
     */
          
    private String cpcdydlx;

      /**
     * 单位代码
     */
          
    private String vdwdm;

      /**
     * 收车地址   100 - 200
     */
          
    private String vscdz;

      /**
     * 到货地代码
     */
          
    private String cdhddm;

      /**
     * 发运地点
     */
          
    private String cfydd;

      /**
     * 类型 0/自动形成，1/手工维护
     */
          
    private String cly;

      /**
     * V平台的运输方式。根据主机公司的转换而来
     */
          
    private String vysfs;

      /**
     * 对应单编号/任务单　20-30
     */
          
    private String cdydbh;

      /**
     * 清单号 20-30
     */
          
    private String cjhdh;

      /**
     * 计划类型
     */
          
    private String cjhlx;

      /**
     * 所属销售公司 2-36
     */
          
    private String cssgs;

      /**
     * 移动计划下达时间
     */
          
    private Long dxdsj;

      /**
     * 运输计划员接收时间
     */
          
    private Long dysjssj;

      /**
     * R3运单号 20-30
     */
          
    private String cdydbh1;

      /**
     * 调度员
     */
          
    private String cddy;

      /**
     * 导入时间
     */
          
    private Long ddrsj;

      /**
     * 大客户代码
     */
          
    private String cdkhdm;

      /**
     * 承运商代码
     */
          
    private String ccysdm;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String cgs;

      /**
     * 大众传来的辅板的运输代码(SPG号),但尚未与MDAC52_SPG表关联
     */
          
    private String cspgdm;

      /**
     * 大众传来的辅板的运输商简称
     */
          
    private String cspgnm;

      /**
     * 0不是、1是A2车壳计划
     */
          
    private String ca2jhbs;

      /**
     * 发车站台
     */
          
    private String vfczt;

      /**
     * 收车站台 20-100 20220315
     */
          
    private String vsczt;

      /**
     * 批次号（铁路计划）
     */
          
    private String vph;

      /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */
          
    private String cqwh;

      /**
     * 到货地省份。如江苏、南京，确保新增地点在TMS系统能直接定位到城市，提高系统运行效率
     */
          
    private String vsqmc;

      /**
     * 到货地城市
     */
          
    private String vsxmc;

      /**
     * 区域公司。用于计划盲分，E表示未确定区域公司
     */
          
    private String vqygs;

      /**
     * 主机公司代码  字典（WTDW）
     */
          
    private String czjgsdm;

      /**
     * 运输车。用于客车
     */
          
    private String vyscdm;

      /**
     * 最远里程。回头置SPTB02.NSFFY
     */
          
    private Long nlc;

      /**
     * 线中代码。资源自动分配，来源于sptb_xl01
     */
          
    private String cxldm;

      /**
     * 预分区域。资源自动分配
     */
          
    private String vqygsYf;

      /**
     * 预分运输商。资源自动分配
     */
          
    private String cyssdmYf;

      /**
     * 预分区域。资源自动分配，且不变
     */
          
    private String vqygsYf1;

      /**
     * 预分运输商。资源自动分配，且不变
     */
          
    private String cyssdmYf1;

      /**
     * 资源分配参考日期
     */
          
    private Long dzyrq;

      /**
     * 时间戳。BI提数据
     */
          
    private Long dstamp;

      /**
     * 实际运输商。主要针对轿运
     */
          
    private String cyssdmSj;

      /**
     * 解放长春 经销商对应线路包代码 
     */
          
    private String cxldmJfcc;

      /**
     * 解放成都 经销商对应线路包代码
     */
          
    private String cxldmJfcd;

      /**
     * 解放青岛 经销商对应线路包代码
     */
          
    private String cxldmJfqd;

      /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */
          
    private String cxsgsdm;

      /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */
          
    private String vxsgsmc;

      /**
     * 移动类型描述
     */
          
    private String cydlxmc;

      /**
     * 区分解放青岛轻卡和重卡 add by lwx 20181206
     */
          
    private String vcysdm;

      /**
     * 是否J6
     */
          
    private String qycx;

        
    private Long dppsjJf;

      /**
     * 运输类型描述
     */
          
    private String cyslxms;

      /**
     * 单价 解放青岛用
     */
          
    private BigDecimal ndj;

      /**
     * 20190927 马自达和轿车品牌 联运计划最大段数
     */
          
    private Integer nZdxh;

      /**
     * 20191217 QDC X传统能源；D纯电；F油电混动；
     */
          
    private String evFlag;

      /**
     * 特殊发运车辆标识
     */
          
    private String svFlag;

      /**
     * 内部流水号
     */
          
    private String interCode;

      /**
     * 发车单位名称城市
     */
          
    private String nameFrom;

      /**
     * 收车单位名称城市 
     */
          
    private String nameTo;

      /**
     * 发车人及联系方式
     */
          
    private String senPerson;

      /**
     * 收车人及联系方式
     */
          
    private String recPerson;

      /**
     * 完整经办人及联系方式
     */
          
    private String personPhone;

      /**
     * 发货单位地址
     */
          
    private String addressFrom;

      /**
     * 收货单位地址
     */
          
    private String addressTo;

      /**
     * 发货时间
     */
          
    private Long reqDate;

      /**
     * 要求送达时间
     */
          
    private Long fhDate;

      /**
     * 车辆类型
     */
          
    private String submi;

      /**
     * 经办单位
     */
          
    private String deptm;

      /**
     * 具体要求说明
     */
          
    private String info;

      /**
     * 2位车型
     */
          
    private String cartype;

      /**
     * 品牌
     */
          
    private String brand;

      /**
     * 车型描述
     */
          
    private String name2;

      /**
     * 移动类型 奔腾和马自达用 20200513
     */
          
    private String cydlx;

      /**
     * 20200518 富裕资源标识 默认0
     */
          
    private String cbsFyzy;

      /**
     * 20200601 DTF 资源分配日期  实际分配
     */
          
    private Long dzyfprq;

      /**
     * 20200604 DTF 资源预分日期  预分配
     */
          
    private Long dzyyfrq;

      /**
     * 20200608 富余资源分配 预分运输商代码
     */
          
    private String cyssdmYfFy;

      /**
     * 20200718 DTF 配板标识
     */
          
    private String cpbbs;

      /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */
          
    private String svType;

      /**
     * 20210712 DTF 解放资源类型（牵引/载货/自卸）
     */
          
    private String vzylxJf;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
