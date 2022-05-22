package com.yqwl.datamiddle.realtime.bean;

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
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb01c implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 移动计划
     */

    private String CPCDBH;

    /**
     * 单据日期
     */

    private Long DDJRQ;

    /**
     * 操作员代码·
     */

    private String VCZYDM;

    /**
     * 物理仓库
     */

    private String VWLCKDM;

    /**
     * 状态 2下达 3运输确认 4实现 9作废
     */

    private String CZT;

    /**
     * 主机公司的运输方式，且通过它来区分是否联运
     */

    private String CZLDBH;

    /**
     * 备注 100 20190403 100-500
     */

    private String VBZ;

    /**
     * 运输类型
     */

    private String CYSLX;

    /**
     * 指令单上带回的移动类型
     */

    private String CPCDYDLX;

    /**
     * 单位代码
     */

    private String VDWDM;

    /**
     * 收车地址   100 - 200
     */

    private String VSCDZ;

    /**
     * 到货地代码
     */

    private String CDHDDM;

    /**
     * 发运地点
     */

    private String CFYDD;

    /**
     * 类型 0/自动形成，1/手工维护
     */

    private String CLY;

    /**
     * V平台的运输方式。根据主机公司的转换而来
     */

    private String VYSFS;

    /**
     * 对应单编号/任务单　20-30
     */

    private String CDYDBH;

    /**
     * 清单号 20-30
     */

    private String CJHDH;

    /**
     * 计划类型
     */

    private String CJHLX;

    /**
     * 所属销售公司 2-36
     */

    private String CSSGS;

    /**
     * 移动计划下达时间
     */

    private Long DXDSJ;

    /**
     * 运输计划员接收时间
     */

    private Long DYSJSSJ;

    /**
     * R3运单号 20-30
     */

    private String CDYDBH1;

    /**
     * 调度员
     */

    private String CDDY;

    /**
     * 导入时间
     */

    private Long DDRSJ;

    /**
     * 大客户代码
     */

    private String CDKHDM;

    /**
     * 承运商代码
     */

    private String CCYSDM;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String CGS;

    /**
     * 大众传来的辅板的运输代码(SPG号),但尚未与MDAC52_SPG表关联
     */

    private String CSPGDM;

    /**
     * 大众传来的辅板的运输商简称
     */

    private String CSPGNM;

    /**
     * 0不是、1是A2车壳计划
     */

    private String CA2JHBS;

    /**
     * 发车站台
     */

    private String VFCZT;

    /**
     * 收车站台 20-100 20220315
     */

    private String VSCZT;

    /**
     * 批次号（铁路计划）
     */

    private String VPH;

    /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */

    private String CQWH;

    /**
     * 到货地省份。如江苏、南京，确保新增地点在TMS系统能直接定位到城市，提高系统运行效率
     */

    private String VSQMC;

    /**
     * 到货地城市
     */

    private String VSXMC;

    /**
     * 区域公司。用于计划盲分，E表示未确定区域公司
     */

    private String VQYGS;

    /**
     * 主机公司代码  字典（WTDW）
     */

    private String CZJGSDM;

    /**
     * 运输车。用于客车
     */

    private String VYSCDM;

    /**
     * 最远里程。回头置SPTB02.NSFFY
     */

    private Long NLC;

    /**
     * 线中代码。资源自动分配，来源于sptb_xl01
     */

    private String CXLDM;

    /**
     * 预分区域。资源自动分配
     */

    private String VQYGS_YF;

    /**
     * 预分运输商。资源自动分配
     */

    private String CYSSDM_YF;

    /**
     * 预分区域。资源自动分配，且不变
     */

    private String VQYGS_YF1;

    /**
     * 预分运输商。资源自动分配，且不变
     */

    private String CYSSDM_YF1;

    /**
     * 资源分配参考日期
     */

    private Long DZYRQ;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 实际运输商。主要针对轿运
     */

    private String CYSSDM_SJ;

    /**
     * 解放长春 经销商对应线路包代码
     */

    private String CXLDM_JFCC;

    /**
     * 解放成都 经销商对应线路包代码
     */

    private String CXLDM_JFCD;

    /**
     * 解放青岛 经销商对应线路包代码
     */

    private String CXLDM_JFQD;

    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */

    private String CXSGSDM;

    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */

    private String VXSGSMC;

    /**
     * 移动类型描述
     */

    private String CYDLXMC;

    /**
     * 区分解放青岛轻卡和重卡 add by lwx 20181206
     */

    private String VCYSDM;

    /**
     * 是否J6
     */

    private String QYCX;


    private Long DPPSJ_JF;

    /**
     * 运输类型描述
     */

    private String CYSLXMS;

    /**
     * 单价 解放青岛用
     */

    private BigDecimal NDJ;

    /**
     * 20190927 马自达和轿车品牌 联运计划最大段数
     */

    private Integer N_ZDXH;

    /**
     * 20191217 QDC X传统能源；D纯电；F油电混动；
     */

    private String EV_FLAG;

    /**
     * 特殊发运车辆标识
     */

    private String SV_FLAG;

    /**
     * 内部流水号
     */

    private String INTER_CODE;

    /**
     * 发车单位名称城市
     */

    private String NAME_FROM;

    /**
     * 收车单位名称城市
     */

    private String NAME_TO;

    /**
     * 发车人及联系方式
     */

    private String SEN_PERSON;

    /**
     * 收车人及联系方式
     */

    private String REC_PERSON;

    /**
     * 完整经办人及联系方式
     */

    private String PERSON_PHONE;

    /**
     * 发货单位地址
     */

    private String ADDRESS_FROM;

    /**
     * 收货单位地址
     */

    private String ADDRESS_TO;

    /**
     * 发货时间
     */

    private Long REQ_DATE;

    /**
     * 要求送达时间
     */

    private Long FH_DATE;

    /**
     * 车辆类型
     */

    private String SUBMI;

    /**
     * 经办单位
     */

    private String DEPTM;

    /**
     * 具体要求说明
     */

    private String INFO;

    /**
     * 2位车型
     */

    private String CARTYPE;

    /**
     * 品牌
     */

    private String BRAND;

    /**
     * 车型描述
     */

    private String NAME2;

    /**
     * 移动类型 奔腾和马自达用 20200513
     */

    private String CYDLX;

    /**
     * 20200518 富裕资源标识 默认0
     */

    private String CBS_FYZY;

    /**
     * 20200601 DTF 资源分配日期  实际分配
     */

    private Long DZYFPRQ;

    /**
     * 20200604 DTF 资源预分日期  预分配
     */

    private Long DZYYFRQ;

    /**
     * 20200608 富余资源分配 预分运输商代码
     */

    private String CYSSDM_YF_FY;

    /**
     * 20200718 DTF 配板标识
     */

    private String CPBBS;

    /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */

    private String SV_TYPE;

    /**
     * 20210712 DTF 解放资源类型（牵引/载货/自卸）
     */

    private String VZYLX_JF;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
