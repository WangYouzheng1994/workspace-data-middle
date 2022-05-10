package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 运单主表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb02")
public class Sptb02 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 结算单编号 主键

     */
      @TableField("CJSDBH")
    private String cjsdbh;

      /**
     * 运单日期 形成运输计划时产生  date 统一时间戳 bigint
     */
      @TableField("DDJRQ")
    private Long ddjrq;

      /**
     * 运单费用  number 转 int
     */
      @TableField("NYDFY")
    private Integer nydfy;

      /**
     * 卡车费用  number 转 int
     */
      @TableField("NKCFY")
    private Integer nkcfy;

      /**
     * 里程        number 转 int
     */
      @TableField("NSFFY")
    private Integer nsffy;

      /**
     * 结算时间    date 统一时间戳 bigint
     */
      @TableField("DJSSJ")
    private Long djssj;

      /**
     * 运输车牌照号
     */
      @TableField("VJSYDM")
    private String vjsydm;

      /**
     * 字典（SPTJHZT） 22形成计划 24指派运输商 7在途 8到货 9返单，A待结算 B可结算 C已经算 D确认BM单 F2生成支付报表 G提交支付报表 H2支付报表签许中 H6支付报表签许完毕 I支付报表记账
     */
      @TableField("CZT")
    private String czt;

      /**
     * 暂不用
     */
      @TableField("NJSDYCS")
    private Integer njsdycs;

      /**
     * 返单大序号
     */
      @TableField("NTCDYCS")
    private Integer ntcdycs;

      /**
     * 备注
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 主机公司运输方式。来自SPTB01C.CZLDBH
     */
      @TableField("CYSFS_ZJGS")
    private String cysfsZjgs;

      /**
     * 清单号
     */
      @TableField("VWXDWDM")
    private String vwxdwdm;

      /**
     * 运输车代码
     */
      @TableField("VYSCDM")
    private String vyscdm;

      /**
     * 主司机代码
     */
      @TableField("VZSJDM")
    private String vzsjdm;

      /**
     * 付司机代码
     */
      @TableField("VFSJDM")
    private String vfsjdm;

      /**
     * 运输类型
     */
      @TableField("CYSLX")
    private String cyslx;

      /**
     * 形成运输计划的人员
     */
      @TableField("CCZYDM")
    private String cczydm;

      /**
     * 指车时间   date 统一时间戳 bigint
     */
      @TableField("DZPSJ")
    private Long dzpsj;

      /**
     * 指车操作员
     */
      @TableField("CZPYDM")
    private String czpydm;

      /**
     * 返程登记时间  date 统一时间戳 bigint
     */
      @TableField("DFCDJSJ")
    private Long dfcdjsj;

      /**
     * 返单操作员代码
     */
      @TableField("CDJYDM")
    private String cdjydm;

      /**
     * 承运商代码 3-20
     */
      @TableField("CCYSDM")
    private String ccysdm;

      /**
     * 运输商代码
     */
      @TableField("CYSSDM")
    private String cyssdm;

      /**
     * 承运商指派时间  date 统一时间戳 bigint
     */
      @TableField("DCYSZPSJ")
    private Long dcyszpsj;

      /**
     * 运输商指派时间  date 统一时间戳 bigint
     */
      @TableField("DYSSZPSJ")
    private Long dysszpsj;

      /**
     * 承运商指派操作员代码
     */
      @TableField("CCYZPYDM")
    private String ccyzpydm;

      /**
     * 运输商指派操作员代码
     */
      @TableField("CYSZPYDM")
    private String cyszpydm;

      /**
     * 大众到货时间    date 统一时间戳 bigint
     */
      @TableField("DDHSJ")
    private Long ddhsj;

      /**
     * 配车单编号
     */
      @TableField("CPCDBH")
    private String cpcdbh;

      /**
     * 铁水批次号
     */
      @TableField("NPHID")
    private String nphid;

      /**
     * date 统一时间戳 bigint
     */
      @TableField("DZCSJ")
    private Long dzcsj;

      /**
     * 暂不用 （解放-最迟送达时间）  date 统一时间戳 bigint
     */
      @TableField("DYJWCSJ")
    private Long dyjwcsj;

      /**
     * LJS码。MDAC33.CLJSDM
     */
      @TableField("CFWM")
    private String cfwm;

      /**
     * 中继标识。0非中继，Z0中继主运单、Z1中继运单
     */
      @TableField("CZJBS")
    private String czjbs;

      /**
     * 出库日期    date 统一时间戳 bigint
     */
      @TableField("DCKRQ")
    private Long dckrq;

      /**
     * 支付表号。SPTB7.CZFBH
     */
      @TableField("CZFBH")
    private String czfbh;

      /**
     * 返单确认时间   date 统一时间戳 bigint
     */
      @TableField("DBCSJ")
    private Long dbcsj;

      /**
     * 暂不用
     */
      @TableField("VCKKGYDM")
    private String vckkgydm;

      /**
     * 运输方式
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * 暂不用(铁水存值啦) 实际存的是：发车的产权仓库 暂不用这个概念！
     */
      @TableField("CFYDD")
    private String cfydd;

      /**
     * 同SPTC34 物理仓库代码
     */
      @TableField("VWLCKDM")
    private String vwlckdm;

      /**
     * 运单类型
     */
      @TableField("CYDLX")
    private String cydlx;

      /**
     * 经销商代码
     */
      @TableField("VDWDM")
    private String vdwdm;

      /**
     * 收车地址 100-200 20220309 
     */
      @TableField("VSCDZ")
    private String vscdz;

      /**
     * 到货地代码
     */
      @TableField("CDHDDM")
    private String cdhddm;

      /**
     * 陆捷用作业务接口数据传递标识
     */
      @TableField("CCLBS")
    private String cclbs;

      /**
     * 入中转库时间  date 统一时间戳 bigint
     */
      @TableField("DCKQRSJ")
    private Long dckqrsj;

      /**
     * 暂不用
     */
      @TableField("CJKBS")
    private String cjkbs;

      /**
     * 暂不用        date 统一时间戳 bigint
     */
      @TableField("DJKRQ")
    private Long djkrq;

      /**
     * 任务单号 20-30
     */
      @TableField("CJHDH")
    private String cjhdh;

      /**
     * 合同编号
     */
      @TableField("CHTCODE")
    private String chtcode;

      /**
     * 暂不用
     */
      @TableField("VQRBZ")
    private String vqrbz;

      /**
     * 存储区域公司
     */
      @TableField("CQRR")
    private String cqrr;

      /**
     * 集运时间    date 统一时间戳 bigint
     */
      @TableField("DJYSJ")
    private Long djysj;

      /**
     * 集运车号
     */
      @TableField("CJYCH")
    private String cjych;

      /**
     * 暂不用 (存的是牌照号)
     */
      @TableField("CXSQRR")
    private String cxsqrr;

      /**
     * 陆捷用作常态到货时间   date 统一时间戳 bigint
     */
      @TableField("DXSQRSJ")
    private Long dxsqrsj;

      /**
     * 暂不用
     */
      @TableField("VXSBZ")
    private String vxsbz;

      /**
     * 周五到货时间   date 统一时间戳 bigint
     */
      @TableField("DYSXD")
    private Long dysxd;

      /**
     * 出库结算时间   date 统一时间戳 bigint
     */
      @TableField("DCKJSSJ")
    private Long dckjssj;

      /**
     * 调度中心返单时间  date 统一时间戳 bigint
     */
      @TableField("DCKXD")
    private Long dckxd;

      /**
     * 结算排序，用于结算表  number 转 int
     */
      @TableField("NFJYF")
    private Integer nfjyf;

      /**
     * 暂不用
     */
      @TableField("CPZBS")
    private String cpzbs;

      /**
     * 建单日期 （SPTB01C.DDJRQ）date 统一时间戳 bigint
     */
      @TableField("DPZRQ")
    private Long dpzrq;

      /**
     * 结算表号。SPTB16.CJSBH。年月+3位流水号
     */
      @TableField("CPZCZY")
    private String cpzczy;

      /**
     * 配载单编号
     */
      @TableField("CPZDBH")
    private String cpzdbh;

      /**
     * 配载单序号 20210109 2-10
     */
      @TableField("CPZDXH")
    private String cpzdxh;

      /**
     * 拆板标识2010-06-09
     */
      @TableField("CFPBS")
    private String cfpbs;

      /**
     * 暂不用   date 统一时间戳 bigint
     */
      @TableField("DDCDKSJ")
    private Long ddcdksj;

      /**
     * GPS到货时间  date 统一时间戳 bigint
     */
      @TableField("DGPSDHSJ")
    private Long dgpsdhsj;

      /**
     * 实际离长时间  date 统一时间戳 bigint
     */
      @TableField("DSJCFSJ")
    private Long dsjcfsj;

      /**
     * 异常状态 0：与R3出库车辆一致，2：与R3出库车辆不致，1：TMS未指车但R3已出库
     */
      @TableField("CYCZT")
    private String cyczt;

      /**
     * 经销商（大客户）代码
     */
      @TableField("CDKHDM")
    private String cdkhdm;

      /**
     * 预计到货时间 陆捷理论到货时间＝离长时间＋运输时间   date 统一时间戳 bigint
     */
      @TableField("DYJDHSJ")
    private Long dyjdhsj;

      /**
     * 实际牌照号
     */
      @TableField("VSJPZH")
    private String vsjpzh;

      /**
     * 备注 （针对中途换车）
     */
      @TableField("VBZ1")
    private String vbz1;

      /**
     * BM单号。
     */
      @TableField("VDHPJ")
    private String vdhpj;

      /**
     * GPS回长时间   date 统一时间戳 bigint
     */
      @TableField("DGPSHCSJ")
    private Long dgpshcsj;

      /**
     * 新P号：做为指派运输车的前一步
     */
      @TableField("VPH")
    private String vph;

      /**
     * 新P号指生成时间    date 统一时间戳 bigint
     */
      @TableField("DPHSCSJ")
    private Long dphscsj;

      /**
     * 配板标识
     */
      @TableField("VPBBS")
    private String vpbbs;

      /**
     * 大客户单据确认标识1确认  0未确认
     */
      @TableField("VDKHQRBS")
    private String vdkhqrbs;

      /**
     * 理论离长时间  date 统一时间戳 bigint
     */
      @TableField("DLLLCSJ")
    private Long dlllcsj;

      /**
     * 管理费确认人
     */
      @TableField("CGLFQRCZY")
    private String cglfqrczy;

      /**
     * 管理费确认时间   date 统一时间戳 bigint
     */
      @TableField("DGLFQRSJ")
    private Long dglfqrsj;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

      /**
     * 备用金标识
     */
      @TableField("CBYJQRBS")
    private String cbyjqrbs;

      /**
     * 备用金确认人
     */
      @TableField("CBYJQRCZY")
    private String cbyjqrczy;

      /**
     * 备用金确认时间   date 统一时间戳 bigint
     */
      @TableField("DBYJQRSJ")
    private Long dbyjqrsj;

      /**
     * 原承运商代码2010-06-09
     */
      @TableField("CCYSDM_O")
    private String ccysdmO;

      /**
     * 原运输商代码2010-06-09
     */
      @TableField("CYSSDM_O")
    private String cyssdmO;

      /**
     * 管理费备注
     */
      @TableField("CGLFBZ")
    private String cglfbz;

      /**
     * GPS异常原因 SYSC09D.GPSYCYY
     */
      @TableField("CGPSYCYY")
    private String cgpsycyy;

      /**
     * 指派时异常类型 SYSC09D.ZPYCLX
     */
      @TableField("CZPYCLX")
    private String czpyclx;

      /**
     * 返单大序号  number 转 int
     */
      @TableField("NFDDXH")
    private Integer nfddxh;

      /**
     * 发车站台
     */
      @TableField("VFCZT")
    private String vfczt;

      /**
     * 收车站台
     */
      @TableField("VSCZT")
    private String vsczt;

      /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */
      @TableField("CQWH")
    private String cqwh;

      /**
     * 主机公司代码  字典（WTDW）
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

      /**
     * 支付运输商表号。SPTB7.CZFBH
     */
      @TableField("CZFBH2")
    private String czfbh2;

      /**
     * 分段的关连主键
     */
      @TableField("CJSDBH2")
    private String cjsdbh2;

      /**
     * 支付运输商补偿表号
     */
      @TableField("CZFBH3")
    private String czfbh3;

      /**
     * 支付基地补偿表号
     */
      @TableField("CZFBH4")
    private String czfbh4;

      /**
     * TVS交车时间   date 统一时间戳 bigint
     */
      @TableField("DTVSJCSJ")
    private Long dtvsjcsj;

      /**
     * TVS出发时间   date 统一时间戳 bigint
     */
      @TableField("DTVSCFSJ")
    private Long dtvscfsj;

      /**
     * TVS到货时间    date 统一时间戳 bigint
     */
      @TableField("DTVSDHSJ")
    private Long dtvsdhsj;

      /**
     * TVS返回时间。没啥意义  date 统一时间戳 bigint
     */
      @TableField("DTVSFCSJ")
    private Long dtvsfcsj;

      /**
     * TVS批次号
     */
      @TableField("CTVSPH")
    private String ctvsph;

      /**
     * 铁路车厢号、水路船舶号
     */
      @TableField("VEHID")
    private String vehid;

      /**
     * TVS中途换车时间   date 统一时间戳 bigint
     */
      @TableField("DTVSJCSJ2")
    private Long dtvsjcsj2;

      /**
     * 时间戳。BI提数据   timestamp 转 bigint
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 交车位置省份
     */
      @TableField("CJCSF")
    private String cjcsf;

      /**
     * 交车位置城市
     */
      @TableField("CJCCS")
    private String cjccs;

      /**
     * 路径点数  number 转 int
     */
      @TableField("NLJDS")
    private Integer nljds;

      /**
     * 总部项目成本单
     */
      @TableField("CXMCBD_ZB")
    private String cxmcbdZb;

      /**
     * 基地项目成本单
     */
      @TableField("CXMCBD_JD")
    private String cxmcbdJd;

      /**
     * 20180523 add by dtf 大众估价支付部分 税率特殊处理（支付价/1.11*1.1） ='1' 代表需要特殊处理！
     */
      @TableField("CBS_GJZF")
    private String cbsGjzf;

      /**
     * 上一段结算单号
     */
      @TableField("CJSDBH_LAST")
    private String cjsdbhLast;

      /**
     * 储运备注 解放使用，王大军提
     */
      @TableField("CCYBZ")
    private String ccybz;

      /**
     * 经销商备注 解放使用，王大军提
     */
      @TableField("CJXSBZ")
    private String cjxsbz;

      /**
     * 销售备注 解放使用，王大军提
     */
      @TableField("CXSBZ")
    private String cxsbz;

      /**
     * 销售订单编号，解放使用，王大军提
     */
      @TableField("CXSDDBH")
    private String cxsddbh;

      /**
     * B平台标识
     */
      @TableField("PTBS")
    private String ptbs;

      /**
     * B平台运输商
     */
      @TableField("CYYSDM_PT")
    private String cyysdmPt;

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
     * 轿车分段序号  number 转 int
     */
      @TableField("NJCFDXH")
    private Integer njcfdxh;

      /**
     * 实车交接表示 DSS APP 1已交接；0未交接；
     */
      @TableField("CBS_SCJJ")
    private String cbsScjj;

      /**
     * 2018补偿报表 比例和定额即直发和分拨 标识 11 12  1干线；2分拨；
     */
      @TableField("CBS_BCBBLX")
    private String cbsBcbblx;

      /**
     * 实车交接时间 DSS实车到货时间 20190408  date 统一时间戳 bigint
     */
      @TableField("DATE_SCJJ")
    private Long dateScjj;

      /**
     * 客服确认标记
     */
      @TableField("CBS_CUSCONFIRM")
    private String cbsCusconfirm;

      /**
     * 运营部确认标记
     */
      @TableField("CBS_OPERDEPART")
    private String cbsOperdepart;

      /**
     * 装车完成时间 20191020  date 统一时间戳 bigint
     */
      @TableField("DZCWCSJ")
    private Long dzcwcsj;

      /**
     * 到达站台时间 20191022  date 统一时间戳 bigint
     */
      @TableField("DDDZTSJ")
    private Long dddztsj;

      /**
     * 同步标识 0未同步，1已同步
     */
      @TableField("IS_SYNC")
    private String isSync;

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
     * 发货时间           date 统一时间戳 bigint
     */
      @TableField("REQ_DATE")
    private Long reqDate;

      /**
     * 要求送达时间     date 统一时间戳 bigint
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
     * 支付明细管理生成-操作人代码
     */
      @TableField("ZF_CONFIRM_CCZYDM")
    private String zfConfirmCczydm;

      /**
     * 支付明细管理生成-操作员名称
     */
      @TableField("ZF_CONFIRM_CCZYMC")
    private String zfConfirmCczymc;

      /**
     * 支付明细管理生成-操作日期    date 统一时间戳 bigint
     */
      @TableField("ZF_CONFIRM_DCZRQ")
    private Long zfConfirmDczrq;

      /**
     * 铁路列号更新时间                 date 统一时间戳 bigint
     */
      @TableField("DTLLHSCSJ")
    private Long dtllhscsj;

      /**
     * GPS路径点数    number 转 int
     */
      @TableField("NLJDS_GPS")
    private Integer nljdsGps;

      /**
     * 标准GPS到货时间         date 统一时间戳 bigint
     */
      @TableField("DDHSJ_GPS")
    private Long ddhsjGps;

      /**
     * 标准系统到货时间         date 统一时间戳 bigint
     */
      @TableField("DDHSJ_XT")
    private Long ddhsjXt;

      /**
     * 铁路卸车时间 20200525    date 统一时间戳 bigint
     */
      @TableField("DZTXCSJ")
    private Long dztxcsj;

      /**
     * 20200708 奔马终段预计到货时间   date 统一时间戳 bigint
     */
      @TableField("DYJDHSJ_BM")
    private Long dyjdhsjBm;

      /**
     * 20200708 奔马分段预计到货时间   date 统一时间戳 bigint
     */
      @TableField("DFDYJDHSJ_BM")
    private Long dfdyjdhsjBm;

      /**
     * 20201014 保密车支付表号
     */
      @TableField("CZFBH_BMCBC")
    private String czfbhBmcbc;

      /**
     * 20201014 保密车结算表号
     */
      @TableField("CJSBH_BMCBC")
    private String cjsbhBmcbc;

      /**
     * 保密车补位报表 BM单号
     */
      @TableField("VDHPJ_BMCBC")
    private String vdhpjBmcbc;

      /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */
      @TableField("SV_TYPE")
    private String svType;

      /**
     * 20210224 大众铁水结算状态
     */
      @TableField("CZT_JS_TS")
    private String cztJsTs;

      /**
     * 20210224 大众铁水支付状态
     */
      @TableField("CZT_ZF_TS")
    private String cztZfTs;

      /**
     * 20210422 DTF 大众标准物流时间   number 转  bigint
     */
      @TableField("NBZWLSJ_DZ")
    private Long nbzwlsjDz;

      /**
     * 20210422 DTF 大众标准到货时间   date 统一时间戳 bigint
     */
      @TableField("DBZDHSJ_DZ")
    private Long dbzdhsjDz;

      /**
     * STD同城标识
     */
      @TableField("CTCBS")
    private String ctcbs;

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
