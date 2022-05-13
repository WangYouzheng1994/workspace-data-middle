package com.yqwl.datamiddle.realtime.bean;


import com.yqwl.datamiddle.realtime.enums.TransientSink;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
public class DwmSptb02 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算单编号 主键
     */

    private String cjsdbh;

    /**
     * 运单日期 形成运输计划时产生  date 统一时间戳 bigint
     */

    private Long ddjrq;

    /**
     * 运单费用  number 转 int
     */

    private Integer nydfy;

    /**
     * 卡车费用  number 转 int
     */

    private Integer nkcfy;

    /**
     * 里程        number 转 int
     */

    private Integer nsffy;

    /**
     * 结算时间    date 统一时间戳 bigint
     */

    private Long djssj;

    /**
     * 运输车牌照号
     */

    private String vjsydm;

    /**
     * 字典（SPTJHZT） 22形成计划 24指派运输商 7在途 8到货 9返单，A待结算 B可结算 C已经算 D确认BM单 F2生成支付报表 G提交支付报表 H2支付报表签许中 H6支付报表签许完毕 I支付报表记账
     */

    private String czt;

    /**
     * 暂不用
     */

    private Integer njsdycs;

    /**
     * 返单大序号
     */

    private Integer ntcdycs;

    /**
     * 备注
     */

    private String vbz;

    /**
     * 主机公司运输方式。来自SPTB01C.CZLDBH
     */

    private String cysfsZjgs;

    /**
     * 清单号
     */

    private String vwxdwdm;

    /**
     * 运输车代码
     */

    private String vyscdm;

    /**
     * 主司机代码
     */

    private String vzsjdm;

    /**
     * 付司机代码
     */

    private String vfsjdm;

    /**
     * 运输类型
     */

    private String cyslx;

    /**
     * 形成运输计划的人员
     */

    private String cczydm;

    /**
     * 指车时间   date 统一时间戳 bigint
     */

    private Long dzpsj;

    /**
     * 指车操作员
     */

    private String czpydm;

    /**
     * 返程登记时间  date 统一时间戳 bigint
     */

    private Long dfcdjsj;

    /**
     * 返单操作员代码
     */

    private String cdjydm;

    /**
     * 承运商代码 3-20
     */

    private String ccysdm;

    /**
     * 运输商代码
     */

    private String cyssdm;

    /**
     * 承运商指派时间  date 统一时间戳 bigint
     */

    private Long dcyszpsj;

    /**
     * 运输商指派时间  date 统一时间戳 bigint
     */

    private Long dysszpsj;

    /**
     * 承运商指派操作员代码
     */

    private String ccyzpydm;

    /**
     * 运输商指派操作员代码
     */

    private String cyszpydm;

    /**
     * 大众到货时间    date 统一时间戳 bigint
     */

    private Long ddhsj;

    /**
     * 配车单编号
     */

    private String cpcdbh;

    /**
     * 铁水批次号
     */

    private String nphid;

    /**
     * date 统一时间戳 bigint
     */

    private Long dzcsj;

    /**
     * 暂不用 （解放-最迟送达时间）  date 统一时间戳 bigint
     */

    private Long dyjwcsj;

    /**
     * LJS码。MDAC33.CLJSDM
     */

    private String cfwm;

    /**
     * 中继标识。0非中继，Z0中继主运单、Z1中继运单
     */

    private String czjbs;

    /**
     * 出库日期    date 统一时间戳 bigint
     */

    private Long dckrq;

    /**
     * 支付表号。SPTB7.CZFBH
     */

    private String czfbh;

    /**
     * 返单确认时间   date 统一时间戳 bigint
     */

    private Long dbcsj;

    /**
     * 暂不用
     */

    private String vckkgydm;

    /**
     * 运输方式
     */

    private String vysfs;

    /**
     * 暂不用(铁水存值啦) 实际存的是：发车的产权仓库 暂不用这个概念！
     */

    private String cfydd;

    /**
     * 同SPTC34 物理仓库代码
     */

    private String vwlckdm;

    /**
     * 运单类型
     */

    private String cydlx;

    /**
     * 经销商代码
     */

    private String vdwdm;

    /**
     * 收车地址 100-200 20220309
     */

    private String vscdz;

    /**
     * 到货地代码
     */

    private String cdhddm;

    /**
     * 陆捷用作业务接口数据传递标识
     */

    private String cclbs;

    /**
     * 入中转库时间  date 统一时间戳 bigint
     */

    private Long dckqrsj;

    /**
     * 暂不用
     */

    private String cjkbs;

    /**
     * 暂不用        date 统一时间戳 bigint
     */

    private Long djkrq;

    /**
     * 任务单号 20-30
     */

    private String cjhdh;

    /**
     * 合同编号
     */

    private String chtcode;

    /**
     * 暂不用
     */

    private String vqrbz;

    /**
     * 存储区域公司
     */

    private String cqrr;

    /**
     * 集运时间    date 统一时间戳 bigint
     */

    private Long djysj;

    /**
     * 集运车号
     */

    private String cjych;

    /**
     * 暂不用 (存的是牌照号)
     */

    private String cxsqrr;

    /**
     * 陆捷用作常态到货时间   date 统一时间戳 bigint
     */

    private Long dxsqrsj;

    /**
     * 暂不用
     */

    private String vxsbz;

    /**
     * 周五到货时间   date 统一时间戳 bigint
     */

    private Long dysxd;

    /**
     * 出库结算时间   date 统一时间戳 bigint
     */

    private Long dckjssj;

    /**
     * 调度中心返单时间  date 统一时间戳 bigint
     */

    private Long dckxd;

    /**
     * 结算排序，用于结算表  number 转 int
     */

    private Integer nfjyf;

    /**
     * 暂不用
     */

    private String cpzbs;

    /**
     * 建单日期 （SPTB01C.DDJRQ）date 统一时间戳 bigint
     */

    private Long dpzrq;

    /**
     * 结算表号。SPTB16.CJSBH。年月+3位流水号
     */

    private String cpzczy;

    /**
     * 配载单编号
     */

    private String cpzdbh;

    /**
     * 配载单序号 20210109 2-10
     */

    private String cpzdxh;

    /**
     * 拆板标识2010-06-09
     */

    private String cfpbs;

    /**
     * 暂不用   date 统一时间戳 bigint
     */

    private Long ddcdksj;

    /**
     * GPS到货时间  date 统一时间戳 bigint
     */

    private Long dgpsdhsj;

    /**
     * 实际离长时间  date 统一时间戳 bigint
     */

    private Long dsjcfsj;

    /**
     * 异常状态 0：与R3出库车辆一致，2：与R3出库车辆不致，1：TMS未指车但R3已出库
     */

    private String cyczt;

    /**
     * 经销商（大客户）代码
     */

    private String cdkhdm;

    /**
     * 预计到货时间 陆捷理论到货时间＝离长时间＋运输时间   date 统一时间戳 bigint
     */

    private Long dyjdhsj;

    /**
     * 实际牌照号
     */

    private String vsjpzh;

    /**
     * 备注 （针对中途换车）
     */

    private String vbz1;

    /**
     * BM单号。
     */

    private String vdhpj;

    /**
     * GPS回长时间   date 统一时间戳 bigint
     */

    private Long dgpshcsj;

    /**
     * 新P号：做为指派运输车的前一步
     */

    private String vph;

    /**
     * 新P号指生成时间    date 统一时间戳 bigint
     */

    private Long dphscsj;

    /**
     * 配板标识
     */

    private String vpbbs;

    /**
     * 大客户单据确认标识1确认  0未确认
     */

    private String vdkhqrbs;

    /**
     * 理论离长时间  date 统一时间戳 bigint
     */

    private Long dlllcsj;

    /**
     * 管理费确认人
     */

    private String cglfqrczy;

    /**
     * 管理费确认时间   date 统一时间戳 bigint
     */

    private Long dglfqrsj;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String cgs;

    /**
     * 备用金标识
     */

    private String cbyjqrbs;

    /**
     * 备用金确认人
     */

    private String cbyjqrczy;

    /**
     * 备用金确认时间   date 统一时间戳 bigint
     */

    private Long dbyjqrsj;

    /**
     * 原承运商代码2010-06-09
     */

    private String ccysdmO;

    /**
     * 原运输商代码2010-06-09
     */

    private String cyssdmO;

    /**
     * 管理费备注
     */

    private String cglfbz;

    /**
     * GPS异常原因 SYSC09D.GPSYCYY
     */

    private String cgpsycyy;

    /**
     * 指派时异常类型 SYSC09D.ZPYCLX
     */

    private String czpyclx;

    /**
     * 返单大序号  number 转 int
     */

    private Integer nfddxh;

    /**
     * 发车站台
     */

    private String vfczt;

    /**
     * 收车站台
     */

    private String vsczt;

    /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */

    private String cqwh;

    /**
     * 主机公司代码  字典（WTDW）
     */

    private String czjgsdm;

    /**
     * 支付运输商表号。SPTB7.CZFBH
     */

    private String czfbh2;

    /**
     * 分段的关连主键
     */

    private String cjsdbh2;

    /**
     * 支付运输商补偿表号
     */

    private String czfbh3;

    /**
     * 支付基地补偿表号
     */

    private String czfbh4;

    /**
     * TVS交车时间   date 统一时间戳 bigint
     */

    private Long dtvsjcsj;

    /**
     * TVS出发时间   date 统一时间戳 bigint
     */

    private Long dtvscfsj;

    /**
     * TVS到货时间    date 统一时间戳 bigint
     */

    private Long dtvsdhsj;

    /**
     * TVS返回时间。没啥意义  date 统一时间戳 bigint
     */

    private Long dtvsfcsj;

    /**
     * TVS批次号
     */

    private String ctvsph;

    /**
     * 铁路车厢号、水路船舶号
     */

    private String vehid;

    /**
     * TVS中途换车时间   date 统一时间戳 bigint
     */

    private Long dtvsjcsj2;

    /**
     * 时间戳。BI提数据   timestamp 转 bigint
     */

    private Long dstamp;

    /**
     * 交车位置省份
     */

    private String cjcsf;

    /**
     * 交车位置城市
     */

    private String cjccs;

    /**
     * 路径点数  number 转 int
     */

    private Integer nljds;

    /**
     * 总部项目成本单
     */

    private String cxmcbdZb;

    /**
     * 基地项目成本单
     */

    private String cxmcbdJd;

    /**
     * 20180523 add by dtf 大众估价支付部分 税率特殊处理（支付价/1.11*1.1） ='1' 代表需要特殊处理！
     */

    private String cbsGjzf;

    /**
     * 上一段结算单号
     */

    private String cjsdbhLast;

    /**
     * 储运备注 解放使用，王大军提
     */

    private String ccybz;

    /**
     * 经销商备注 解放使用，王大军提
     */

    private String cjxsbz;

    /**
     * 销售备注 解放使用，王大军提
     */

    private String cxsbz;

    /**
     * 销售订单编号，解放使用，王大军提
     */

    private String cxsddbh;

    /**
     * B平台标识
     */

    private String ptbs;

    /**
     * B平台运输商
     */

    private String cyysdmPt;

    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */

    private String cxsgsdm;

    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */

    private String vxsgsmc;

    /**
     * 轿车分段序号  number 转 int
     */

    private Integer njcfdxh;

    /**
     * 实车交接表示 DSS APP 1已交接；0未交接；
     */

    private String cbsScjj;

    /**
     * 2018补偿报表 比例和定额即直发和分拨 标识 11 12  1干线；2分拨；
     */

    private String cbsBcbblx;

    /**
     * 实车交接时间 DSS实车到货时间 20190408  date 统一时间戳 bigint
     */

    private Long dateScjj;

    /**
     * 客服确认标记
     */

    private String cbsCusconfirm;

    /**
     * 运营部确认标记
     */

    private String cbsOperdepart;

    /**
     * 装车完成时间 20191020  date 统一时间戳 bigint
     */

    private Long dzcwcsj;

    /**
     * 到达站台时间 20191022  date 统一时间戳 bigint
     */

    private Long dddztsj;

    /**
     * 同步标识 0未同步，1已同步
     */

    private String isSync;

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
     * 发货时间           date 统一时间戳 bigint
     */

    private Long reqDate;

    /**
     * 要求送达时间     date 统一时间戳 bigint
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
     * 支付明细管理生成-操作人代码
     */

    private String zfConfirmCczydm;

    /**
     * 支付明细管理生成-操作员名称
     */

    private String zfConfirmCczymc;

    /**
     * 支付明细管理生成-操作日期    date 统一时间戳 bigint
     */

    private Long zfConfirmDczrq;

    /**
     * 铁路列号更新时间                 date 统一时间戳 bigint
     */

    private Long dtllhscsj;

    /**
     * GPS路径点数    number 转 int
     */

    private Integer nljdsGps;

    /**
     * 标准GPS到货时间         date 统一时间戳 bigint
     */

    private Long ddhsjGps;

    /**
     * 标准系统到货时间         date 统一时间戳 bigint
     */

    private Long ddhsjXt;

    /**
     * 铁路卸车时间 20200525    date 统一时间戳 bigint
     */

    private Long dztxcsj;

    /**
     * 20200708 奔马终段预计到货时间   date 统一时间戳 bigint
     */

    private Long dyjdhsjBm;

    /**
     * 20200708 奔马分段预计到货时间   date 统一时间戳 bigint
     */

    private Long dfdyjdhsjBm;

    /**
     * 20201014 保密车支付表号
     */

    private String czfbhBmcbc;

    /**
     * 20201014 保密车结算表号
     */

    private String cjsbhBmcbc;

    /**
     * 保密车补位报表 BM单号
     */

    private String vdhpjBmcbc;

    /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */

    private String svType;

    /**
     * 20210224 大众铁水结算状态
     */

    private String cztJsTs;

    /**
     * 20210224 大众铁水支付状态
     */

    private String cztZfTs;

    /**
     * 20210422 DTF 大众标准物流时间   number 转  bigint
     */

    private Long nbzwlsjDz;

    /**
     * 20210422 DTF 大众标准到货时间   date 统一时间戳 bigint
     */

    private Long dbzdhsjDz;

    /**
     * STD同城标识
     */

    private String ctcbs;

    /**
     * 创建时间
     */

    private Long warehouseCreatetime;

    /**
     * 更新时间
     */

    private Long warehouseUpdatetime;


    /**
     * dwd新增-运输方式 公路 G 水路 S 铁路 T
     */
    private String trafficType;

    /**
     * dwd新增-起运时间 公路-取DTVSCFSJ(TVS出发时间)的值;铁水-取DSJCFSJ(实际离长时间)的值
     */
    private Long shipmentTime;

    /**
     * dwd新增-计划下达时间
     */
    private Long planReleaseTime;

    /**
     * dwd新增-运单指派时间
     */
    private Long assignTime;

    /**
     * dwd新增-打点到货时间
     */
    private Long dotSiteTime;

    /**
     * dwd新增-最终到货时间
     */
    private Long finalSiteTime;

    /**
     * dwd新增-运单生成时间
     */
    private Long orderCreateTime;


    /**
     * 适配 lc_spec_config
     * 基地代码转换
     * 区位号。
     * 0431、 -> 1
     * 022、  -> 5
     * 027、
     * 028、  -> 2
     * 0757   -> 3
     * <p>
     * 表示生产的基地（2013-10-12储运部会议上确定）
     */
    private String baseCode;

    /**
     * 运输方式 适配 lc_spec_config
     * 公路 1
     * 铁路 2
     * 水运 3
     */
    private String transModeCode;

    /**
     * 主机公司代码 适配 lc_spec_config
     * 1  一汽大众
     * 2  一汽红旗
     * 3   一汽马自达
     */
    private String hostComCode;

    /**
     * dwm新增-理论起运时间
     */
    private Long theoryShipmentTime;

    /**
     * dwm新增-理论出库时间
     */
    private Long theoryOutTime;

    /**
     * dwm新增-理论到货时间
     */
    private Long theorySiteTime;

    /**
     * dwm新增-实际出库时间
     */
    private Long actualOutTime;

    /**
     * dwm新增-入目标库时间
     */
    private Long enterTargetTime;

    /**
     * dwm新增-车架号
     */
    private String vvin;

    /**
     * dwm新增 运单状态-1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    private Integer trafficStatus;

    /**
     * dwm新增 运单状态名称 1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    private String trafficStatusName;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
