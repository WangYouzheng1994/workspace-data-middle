package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 运单主表-数据拉宽，增加相关字段
 * 更新字段的源头
 * @author XiaoFeng
 * @since 2022-08-29
 * @Version: V2.0
 */
@CamelUnderline(isChange = false)
@TableName("dwm_vlms_sptb02")
@Data
@EqualsAndHashCode(callSuper = false)
public class DwmSptb02 implements Serializable {

    //private static final long serialVersionUID = 1L;

    /**
     * 结算单编号 主键
     */

    private String CJSDBH;

    /**
     * 运单日期 形成运输计划时产生  date 统一时间戳 bigint
     */

    private Long DDJRQ;

    /**
     * 运单费用  number 转 int
     */

    private Integer NYDFY;

    /**
     * 卡车费用  number 转 int
     */

    private Integer NKCFY;

    /**
     * 里程        number 转 int
     */

    private Integer NSFFY;

    /**
     * 结算时间    date 统一时间戳 bigint
     */

    private Long DJSSJ;

    /**
     * 运输车牌照号
     */

    private String VJSYDM;

    /**
     * 字典（SPTJHZT） 22形成计划 24指派运输商 7在途 8到货 9返单，A待结算 B可结算 C已经算 D确认BM单 F2生成支付报表 G提交支付报表 H2支付报表签许中 H6支付报表签许完毕 I支付报表记账
     */

    private String CZT;

    /**
     * 暂不用
     */

    private Integer NJSDYCS;

    /**
     * 返单大序号
     */

    private Integer NTCDYCS;

    /**
     * 备注
     */

    private String VBZ;

    /**
     * 主机公司运输方式。来自SPTB01C.CZLDBH
     */

    private String CYSFS_ZJGS;

    /**
     * 清单号
     */

    private String VWXDWDM;

    /**
     * 运输车代码
     */

    private String VYSCDM;

    /**
     * 主司机代码
     */

    private String VZSJDM;

    /**
     * 付司机代码
     */

    private String VFSJDM;

    /**
     * 运输类型
     */

    private String CYSLX;

    /**
     * 形成运输计划的人员
     */

    private String CCZYDM;

    /**
     * 指车时间   date 统一时间戳 bigint
     */

    private Long DZPSJ;

    /**
     * 指车操作员
     */

    private String CZPYDM;

    /**
     * 返程登记时间  date 统一时间戳 bigint
     */

    private Long DFCDJSJ;

    /**
     * 返单操作员代码
     */

    private String CDJYDM;

    /**
     * 承运商代码 3-20
     */

    private String CCYSDM;

    /**
     * 运输商代码
     */

    private String CYSSDM;

    /**
     * 承运商指派时间  date 统一时间戳 bigint
     */

    private Long DCYSZPSJ;

    /**
     * 运输商指派时间  date 统一时间戳 bigint
     */

    private Long DYSSZPSJ;

    /**
     * 承运商指派操作员代码
     */

    private String CCYZPYDM;

    /**
     * 运输商指派操作员代码
     */

    private String CYSZPYDM;

    /**
     * 大众到货时间    date 统一时间戳 bigint
     */

    private Long DDHSJ;

    /**
     * 配车单编号
     */

    private String CPCDBH;

    /**
     * 铁水批次号
     */

    private String NPHID;

    /**
     * date 统一时间戳 bigint
     */

    private Long DZCSJ;

    /**
     * 暂不用 （解放-最迟送达时间）  date 统一时间戳 bigint
     */

    private Long DYJWCSJ;

    /**
     * LJS码。MDAC33.CLJSDM
     */

    private String CFWM;

    /**
     * 中继标识。0非中继，Z0中继主运单、Z1中继运单
     */

    private String CZJBS;

    /**
     * 出库日期    date 统一时间戳 bigint
     */

    private Long DCKRQ;

    /**
     * 支付表号。SPTB7.CZFBH
     */

    private String CZFBH;

    /**
     * 返单确认时间   date 统一时间戳 bigint
     */

    private Long DBCSJ;

    /**
     * 暂不用
     */

    private String VCKKGYDM;

    /**
     * 运输方式
     */

    private String VYSFS;

    /**
     * 暂不用(铁水存值啦) 实际存的是：发车的产权仓库 暂不用这个概念！
     */

    private String CFYDD;

    /**
     * 同SPTC34 物理仓库代码
     */

    private String VWLCKDM;

    /**
     * 运单类型
     */

    private String CYDLX;

    /**
     * 经销商代码
     */

    private String VDWDM;

    /**
     * 收车地址 100-200 20220309
     */

    private String VSCDZ;

    /**
     * 到货地代码
     */

    private String CDHDDM;

    /**
     * 陆捷用作业务接口数据传递标识
     */

    private String CCLBS;

    /**
     * 入中转库时间  date 统一时间戳 bigint
     */

    private Long DCKQRSJ;

    /**
     * 暂不用
     */

    private String CJKBS;

    /**
     * 暂不用        date 统一时间戳 bigint
     */

    private Long DJKRQ;

    /**
     * 任务单号 20-30
     */

    private String CJHDH;

    /**
     * 合同编号
     */

    private String CHTCODE;

    /**
     * 暂不用
     */

    private String VQRBZ;

    /**
     * 存储区域公司
     */

    private String CQRR;

    /**
     * 集运时间    date 统一时间戳 bigint
     */

    private Long DJYSJ;

    /**
     * 集运车号
     */

    private String CJYCH;

    /**
     * 暂不用 (存的是牌照号)
     */

    private String CXSQRR;

    /**
     * 陆捷用作常态到货时间   date 统一时间戳 bigint
     */

    private Long DXSQRSJ;

    /**
     * 暂不用
     */

    private String VXSBZ;

    /**
     * 周五到货时间   date 统一时间戳 bigint
     */

    private Long DYSXD;

    /**
     * 出库结算时间   date 统一时间戳 bigint
     */

    private Long DCKJSSJ;

    /**
     * 调度中心返单时间  date 统一时间戳 bigint
     */

    private Long DCKXD;

    /**
     * 结算排序，用于结算表  number 转 int
     */

    private Integer NFJYF;

    /**
     * 暂不用
     */

    private String CPZBS;

    /**
     * 建单日期 （SPTB01C.DDJRQ）date 统一时间戳 bigint
     */

    private Long DPZRQ;

    /**
     * 结算表号。SPTB16.CJSBH。年月+3位流水号
     */

    private String CPZCZY;

    /**
     * 配载单编号
     */

    private String CPZDBH;

    /**
     * 配载单序号 20210109 2-10
     */

    private String CPZDXH;

    /**
     * 拆板标识2010-06-09
     */

    private String CFPBS;

    /**
     * 暂不用   date 统一时间戳 bigint
     */

    private Long DDCDKSJ;

    /**
     * GPS到货时间  date 统一时间戳 bigint
     */

    private Long DGPSDHSJ;

    /**
     * 实际离长时间  date 统一时间戳 bigint
     */

    private Long DSJCFSJ;

    /**
     * 异常状态 0：与R3出库车辆一致，2：与R3出库车辆不致，1：TMS未指车但R3已出库
     */

    private String CYCZT;

    /**
     * 经销商（大客户）代码
     */

    private String CDKHDM;

    /**
     * 预计到货时间 陆捷理论到货时间＝离长时间＋运输时间   date 统一时间戳 bigint
     */

    private Long DYJDHSJ;

    /**
     * 实际牌照号
     */

    private String VSJPZH;

    /**
     * 备注 （针对中途换车）
     */

    private String VBZ1;

    /**
     * BM单号。
     */

    private String VDHPJ;

    /**
     * GPS回长时间   date 统一时间戳 bigint
     */

    private Long DGPSHCSJ;

    /**
     * 新P号：做为指派运输车的前一步
     */

    private String VPH;

    /**
     * 新P号指生成时间    date 统一时间戳 bigint
     */

    private Long DPHSCSJ;

    /**
     * 配板标识
     */

    private String VPBBS;

    /**
     * 大客户单据确认标识1确认  0未确认
     */

    private String VDKHQRBS;

    /**
     * 理论离长时间  date 统一时间戳 bigint
     */

    private Long DLLLCSJ;

    /**
     * 管理费确认人
     */

    private String CGLFQRCZY;

    /**
     * 管理费确认时间   date 统一时间戳 bigint
     */

    private Long DGLFQRSJ;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String CGS;

    /**
     * 备用金标识
     */

    private String CBYJQRBS;

    /**
     * 备用金确认人
     */

    private String CBYJQRCZY;

    /**
     * 备用金确认时间   date 统一时间戳 bigint
     */

    private Long DBYJQRSJ;

    /**
     * 原承运商代码2010-06-09
     */

    private String CCYSDM_O;

    /**
     * 原运输商代码2010-06-09
     */

    private String CYSSDM_O;

    /**
     * 管理费备注
     */

    private String CGLFBZ;

    /**
     * GPS异常原因 SYSC09D.GPSYCYY
     */

    private String CGPSYCYY;

    /**
     * 指派时异常类型 SYSC09D.ZPYCLX
     */

    private String CZPYCLX;

    /**
     * 返单大序号  number 转 int
     */

    private String NFDDXH;

    /**
     * 发车站台
     */

    private String VFCZT;

    /**
     * 收车站台
     */

    private String VSCZT;

    /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */

    private String CQWH;

    /**
     * 主机公司代码  字典（WTDW）
     */

    private String CZJGSDM;

    /**
     * 支付运输商表号。SPTB7.CZFBH
     */

    private String CZFBH2;

    /**
     * 分段的关连主键
     */

    private String CJSDBH2;

    /**
     * 支付运输商补偿表号
     */

    private String CZFBH3;

    /**
     * 支付基地补偿表号
     */

    private String CZFBH4;

    /**
     * TVS交车时间   date 统一时间戳 bigint
     */

    private Long DTVSJCSJ;

    /**
     * TVS出发时间   date 统一时间戳 bigint
     */

    private Long DTVSCFSJ;

    /**
     * TVS到货时间    date 统一时间戳 bigint
     * {@link Sptb02#DTVSDHSJ}
     */
    private Long DTVSDHSJ;

    /**
     * TVS返回时间。没啥意义  date 统一时间戳 bigint
     */

    private Long DTVSFCSJ;

    /**
     * TVS批次号
     */

    private String CTVSPH;

    /**
     * 铁路车厢号、水路船舶号
     */

    private String VEHID;

    /**
     * TVS中途换车时间   date 统一时间戳 bigint
     */

    private Long DTVSJCSJ2;

    /**
     * 时间戳。BI提数据   timestamp 转 bigint
     */

    private Long DSTAMP;

    /**
     * 交车位置省份
     */

    private String CJCSF;

    /**
     * 交车位置城市
     */

    private String CJCCS;

    /**
     * 路径点数  number 转 int
     */

    private Integer NLJDS;

    /**
     * 总部项目成本单
     */

    private String CXMCBD_ZB;

    /**
     * 基地项目成本单
     */

    private String CXMCBD_JD;

    /**
     * 20180523 add by dtf 大众估价支付部分 税率特殊处理（支付价/1.11*1.1） ='1' 代表需要特殊处理！
     */

    private String CBS_GJZF;

    /**
     * 上一段结算单号
     */

    private String CJSDBH_LAST;

    /**
     * 储运备注 解放使用，王大军提
     */

    private String CCYBZ;

    /**
     * 经销商备注 解放使用，王大军提
     */

    private String CJXSBZ;

    /**
     * 销售备注 解放使用，王大军提
     */

    private String CXSBZ;

    /**
     * 销售订单编号，解放使用，王大军提
     */

    private String CXSDDBH;

    /**
     * B平台标识
     */

    private String PTBS;

    /**
     * B平台运输商
     */

    private String CYYSDM_PT;

    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */

    private String CXSGSDM;

    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */

    private String VXSGSMC;

    /**
     * 轿车分段序号  number 转 int
     */

    private Integer NJCFDXH;

    /**
     * 实车交接表示 DSS APP 1已交接；0未交接；
     */

    private String CBS_SCJJ;

    /**
     * 2018补偿报表 比例和定额即直发和分拨 标识 11 12  1干线；2分拨；
     */

    private String CBS_BCBBLX;

    /**
     * 实车交接时间 DSS实车到货时间 20190408  date 统一时间戳 bigint
     */

    private Long DATE_SCJJ;

    /**
     * 客服确认标记
     */

    private String CBS_CUSCONFIRM;

    /**
     * 运营部确认标记
     */

    private String CBS_OPERDEPART;

    /**
     * 装车完成时间 20191020  date 统一时间戳 bigint
     */

    private Long DZCWCSJ;

    /**
     * 到达站台时间 20191022  date 统一时间戳 bigint
     */

    private Long DDDZTSJ;

    /**
     * 同步标识 0未同步，1已同步
     */

    private String IS_SYNC;

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
     * 发货时间           date 统一时间戳 bigint
     */

    private Long REQ_DATE;

    /**
     * 要求送达时间     date 统一时间戳 bigint
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
     * 支付明细管理生成-操作人代码
     */

    private String ZF_CONFIRM_CCZYDM;

    /**
     * 支付明细管理生成-操作员名称
     */

    private String ZF_CONFIRM_CCZYMC;

    /**
     * 支付明细管理生成-操作日期    date 统一时间戳 bigint
     */

    private Long ZF_CONFIRM_DCZRQ;

    /**
     * 铁路列号更新时间                 date 统一时间戳 bigint
     */

    private Long DTLLHSCSJ;

    /**
     * GPS路径点数    number 转 int
     */

    private Integer NLJDS_GPS;

    /**
     * 标准GPS到货时间         date 统一时间戳 bigint
     */

    private Long DDHSJ_GPS;

    /**
     * 标准系统到货时间         date 统一时间戳 bigint
     */

    private Long DDHSJ_XT;

    /**
     * 铁路卸车时间 20200525    date 统一时间戳 bigint
     */

    private Long DZTXCSJ;

    /**
     * 20200708 奔马终段预计到货时间   date 统一时间戳 bigint
     */

    private Long DYJDHSJ_BM;

    /**
     * 20200708 奔马分段预计到货时间   date 统一时间戳 bigint
     */

    private Long DFDYJDHSJ_BM;

    /**
     * 20201014 保密车支付表号
     */

    private String CZFBH_BMCBC;

    /**
     * 20201014 保密车结算表号
     */

    private String CJSBH_BMCBC;

    /**
     * 保密车补位报表 BM单号
     */

    private String VDHPJ_BMCBC;

    /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */

    private String SV_TYPE;

    /**
     * 20210224 大众铁水结算状态
     */

    private String CZT_JS_TS;

    /**
     * 20210224 大众铁水支付状态
     */

    private String CZT_ZF_TS;

    /**
     * 20210422 DTF 大众标准物流时间   number 转  bigint
     */

    private Double NBZWLSJ_DZ;

    /**
     * 20210422 DTF 大众标准到货时间   date 统一时间戳 bigint
     */

    private Long DBZDHSJ_DZ;

    /**
     * STD同城标识
     */

    private String CTCBS;

    /**
     * 新增-创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 新增-更新时间
     */

    private Long WAREHOUSE_UPDATETIME;

    /**
     * dwd新增-运输方式 公路 G 水路 S 铁路 T
     */

    private String TRAFFIC_TYPE;

    /**
     * dwd新增-起运时间 公路-取DTVSCFSJ(TVS出发时间)的值;铁水-取DSJCFSJ(实际离长时间)的值
     * 公路取sptb02.dtvscfsj :
     * {@link Sptb02#DYSSZPSJ}
     * 铁水取sptb02取DSJCFSJ(实际离长时间)的值
     * {@link Sptb02#DSJCFSJ}
     */
    private Long SHIPMENT_TIME;

    /**
     * dwd新增-计划下达时间
     */

    private Long PLAN_RELEASE_TIME;

    /**
     * dwd新增-运单指派时间 原字段
     * {@link Sptb02#DYSSZPSJ}
     */
    private Long ASSIGN_TIME;

    /**
     * dwd新增-打点到货时间 (GPS到货时间)
     * {@link Sptb02#DGPSDHSJ}
     */
    private Long DOT_SITE_TIME;

    /**
     * dwd新增-最终到货时间(大众到货时间)
     * {@link Sptb02#DDHSJ}
     */
    private Long FINAL_SITE_TIME;

    /**
     * dwd新增-运单生成时间
     */
    private Long ORDER_CREATE_TIME;

    /**
     * dwd基地代码转换 适配 lc_spec_config
     */
    private String BASE_CODE;

    /**
     * dwd运输方式代码 适配 lc_spec_config
     */
    private String TRANS_MODE_CODE;

    /**
     * dwd主机公司代码转换 适配 lc_spec_config
     */
    private String HOST_COM_CODE;

    /**
     * dwm新增-理论起运时间
     */
    private Long THEORY_SHIPMENT_TIME;

    /**
     * dwm新增-理论出库时间
     */
    private Long THEORY_OUT_TIME;

    /**
     * dwm新增-理论到货时间
     */

    private Long THEORY_SITE_TIME;

    /**
     * dwm新增-实际出库时间 系统出库时间 (非溯源出库时间)
     * 源:{@link Sptb02#DCKRQ}
     */
    private Long ACTUAL_OUT_TIME;

    /**
     * dwm新增-入目标库时间
     */
    private Long ENTER_TARGET_TIME;

    /**
     * dwm新增-车架号
     */
    private String VVIN;

    /**
     * dwm新增 运单状态-1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    private Integer TRAFFIC_STATUS;

    /**
     * dwm新增 运单状态名称 1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    private String TRAFFIC_STATUS_NAME;

    /**
     * dwm新增 主机公司名称
     */
    private String CUSTOMER_NAME;

    /**
     * dwm新增-发车基地名称
     */
    private String BASE_NAME;

    /**
     * dwm新增-发运仓库名称
     */
    private String SHIPMENT_WAREHOUSE_NAME;

    /**
     * dwm新增-到货地省区代码
     */
    private String END_PROVINCE_CODE;

    /**
     * dwm到货地省区名称
     */
    private String END_PROVINCE_NAME;

    /**
     * dwm到货地市县代码
     */
    private String END_CITY_CODE;

    /**
     * dwm到货地市县名称
     */
    private String END_CITY_NAME;

    /**
     * dwm起货地省区代码
     */
    private String START_PROVINCE_CODE;

    /**
     * dwm起货地省区名称
     */
    private String START_PROVINCE_NAME;

    /**
     * dwm起货地市县代码
     */
    private String START_CITY_CODE;

    /**
     * dwm起货地市县名称
     */
    private String START_CITY_NAME;

    /**
     * dwm运输商名称
     */
    private String TRANSPORT_NAME;

    /**
     * dwm经销商名称
     */
    private String DEALER_NAME;

    /**
     * dwm起运是否正常 1正常0异常
     */
    private Integer SHIPMENT_FLAG;

    /**
     * dwm出库是否正常 1正常0异常
     */
    private Integer OUT_FLAG;

    /**
     * dwm到货是否正常 1正常0异常
     */
    private Integer SITE_FLAG;

    /**
     * 铁路车箱列数类型
     * 1 (b3.nsjsl /10 >= 26)
     * 2 (b3.nsjsl /10 >= 15 and b3.nsjsl /10 <26)
     * 3 b3.nsjsl /10 <15
     */
    private Integer RAILWAY_TRAIN_TYPE;

    /**
     * 车型代码
     */
    private String VEHICLE_CODE;

    /**
     * 开始站台仓库代码
     */
    private String START_WAREHOUSE_CODE;

    /**
     * 到达站台仓库代码
     */
    private String END_WAREHOUSE_CODE;

    /**
     * 开始站台仓库名称
     */
    private String START_WAREHOUSE_NAME;

    /**
     * 到达站台仓库名称
     * 源: {@link DwdSptb02#END_WAREHOUSE_NAME}
     */
    private String END_WAREHOUSE_NAME;

    /**
     * 公路单物理仓库代码对应的仓库类型 rfid_warehouse表中类型 基础库 T1 分拨中心站 T2 港口 T3 站台 T4
     * 源:{@link DwdSptb02#HIGHWAY_WAREHOUSE_TYPE}
     */
    private String HIGHWAY_WAREHOUSE_TYPE;

    /**
     * 入开始站台时间 应用于铁路
     */
    private Long IN_START_PLATFORM_TIME;

    /**
     * 出开始站台时间 应用于铁路
     */
    private Long OUT_START_PLATFORM_TIME;

    /**
     * 入目的站台时间 应用于铁路
     */
    private Long IN_END_PLATFORM_TIME;

    /**
     * 卸车时间  应用于铁路
     */
    private Long UNLOAD_RAILWAY_TIME;

    /**
     * 入开始港口时间 应用于水路
     */
    private Long IN_START_WATERWAY_TIME;

    /**
     * 出开始港口时间 应用于水路
     */
    private Long END_START_WATERWAY_TIME;

    /**
     * 入目的港口时间 应用于水路
     */
    private Long IN_END_WATERWAY_TIME;

    /**
     * 卸船时间 应用水路
     */
    private Long UNLOAD_SHIP_TIME;

    /**
     * 开始物理仓库代码
     */
    private String START_PHYSICAL_CODE;

    /**
     * 结束物理仓库代码
     */
    private String END_PHYSICAL_CODE;

    /**
     *  车型名称
     */
    private String VEHICLE_NAME;

    /**
     * 车辆标识 识别是否为新能源汽车(E6,E7) 取自 mdac12表
     */
    private String CCXDL;

    /**
     * 是否同城异地 0无 1同城 2异地
     * 默认值为0  20220801添加
     */
    private Integer TYPE_TC;

    /**
     *发车站台的省区代码
     */
    private String VFCZT_PROVINCE_CODE;

    /**
     * 发车站台的市县代码
     */
    private String VFCZT_CITY_CODE;

    /**
     * 收车站台的省区代码
     */
    private String VSCZT_PROVINCE_CODE;

    /**
     *收车站台的市县代码
     */
    private String VSCZT_CITY_CODE;

    /** 计划下达日期
     * R3的配板下发日期 取自SPTB01C.DDJRQ 20220818新增
     * 源:{@link DwmSptb02No8TimeFields#DDJRQ_R3}
     */
    private Long DDJRQ_R3;

    /**
     * 汽车品牌名 取自MDAC10.VPPSM 20220825新增
     */
    private String BRAND_NAME;

    /**
     * 收车站台的名字 取自SPTC34.VWLCKMC 20221024新增
     */
    private String VSCZT_NAME;


    /**
     * 发车站台的名字 取自SPTC34.VWLCKMC 20221024新增
     */
    private String VFCZT_NAME;

    /**
     * 集港的 目的入港入站时间  集港集站计划(vsyfs为J)的显示发车站台(vfczt)的溯源入库时间 20221026 新增
     */
    private Long IN_END_J_TIME;

    /**
     * 核实到货时间
     */
    private Long ACCOUNTING_TIME;

    /**
     * 溯源入库名称
     */
    private String IN_WAREHOUSE_NAME;

    /**
     * 入库时间 表中没有此字段
     */
    @TransientSink
    private Long IN_SITE_TIME;

    /**
     * 逻辑删除字段,0表示未删除,1表示已删除
     * 20220906 此字段暂时搁置添加，后续上线此字段。
     * ALTER TABLE `data_middle_flink`.`dwm_vlms_sptb02`
     * ADD COLUMN `DELETE_FLAG` int NOT NULL DEFAULT 0 COMMENT '逻辑删除字段 0为没有删除 1为已删除' AFTER `BRAND_NAME`;
     */
    // private Integer DELETE_FLAG;
}
