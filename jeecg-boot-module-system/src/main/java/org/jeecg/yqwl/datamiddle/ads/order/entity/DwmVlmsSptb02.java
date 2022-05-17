package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date: 2022-05-12
 * @Version: V1.0
 */
@Data
@TableName("dwm_vlms_sptb02_view")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "dwm_vlms_sptb02对象", description = "DwmVlmsSptb02")
public class DwmVlmsSptb02 {

    /**
     * idnum
     */
    @Excel(name = "idnum", width = 15)
    @ApiModelProperty(value = "idnum")
    private Integer idnum;
    /**
     * 结算单编号 主键
     */
    @Excel(name = "结算单编号 主键 ", width = 15)
    @ApiModelProperty(value = "结算单编号 主键 ")
    private java.lang.String cjsdbh;
    /**
     * 运单日期 形成运输计划时产生  date 统一时间戳 bigint
     */
    @Excel(name = "运单日期 形成运输计划时产生  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "运单日期 形成运输计划时产生  date 统一时间戳 bigint")
    private java.lang.Long ddjrq;
    /**
     * 运单费用  number 转 int
     */
    @Excel(name = "运单费用  number 转 int", width = 15)
    @ApiModelProperty(value = "运单费用  number 转 int")
    private java.lang.Integer nydfy;
    /**
     * 卡车费用  number 转 int
     */
    @Excel(name = "卡车费用  number 转 int", width = 15)
    @ApiModelProperty(value = "卡车费用  number 转 int")
    private java.lang.Integer nkcfy;
    /**
     * 里程        number 转 int
     */
    @Excel(name = "里程        number 转 int", width = 15)
    @ApiModelProperty(value = "里程        number 转 int")
    private java.lang.Integer nsffy;
    /**
     * 结算时间    date 统一时间戳 bigint
     */
    @Excel(name = "结算时间    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "结算时间    date 统一时间戳 bigint")
    private java.lang.Long djssj;
    /**
     * 运输车牌照号
     */
    @Excel(name = "运输车牌照号", width = 15)
    @ApiModelProperty(value = "运输车牌照号")
    private java.lang.String vjsydm;
    /**
     * 字典（SPTJHZT） 22形成计划 24指派运输商 7在途 8到货 9返单，A待结算 B可结算 C已经算 D确认BM单 F2生成支付报表 G提交支付报表 H2支付报表签许中 H6支付报表签许完毕 I支付报表记账
     */
    @Excel(name = "字典（SPTJHZT） 22形成计划 24指派运输商 7在途 8到货 9返单，A待结算 B可结算 C已经算 D确认BM单 F2生成支付报表 G提交支付报表 H2支付报表签许中 H6支付报表签许完毕 I支付报表记账", width = 15)
    @ApiModelProperty(value = "字典（SPTJHZT） 22形成计划 24指派运输商 7在途 8到货 9返单，A待结算 B可结算 C已经算 D确认BM单 F2生成支付报表 G提交支付报表 H2支付报表签许中 H6支付报表签许完毕 I支付报表记账")
    private java.lang.String czt;
    /**
     * 暂不用
     */
    @Excel(name = "暂不用", width = 15)
    @ApiModelProperty(value = "暂不用")
    private java.lang.Integer njsdycs;
    /**
     * 返单大序号
     */
    @Excel(name = "返单大序号", width = 15)
    @ApiModelProperty(value = "返单大序号")
    private java.lang.Integer ntcdycs;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String vbz;
    /**
     * 主机公司运输方式。来自SPTB01C.CZLDBH
     */
    @Excel(name = "主机公司运输方式。来自SPTB01C.CZLDBH", width = 15)
    @ApiModelProperty(value = "主机公司运输方式。来自SPTB01C.CZLDBH")
    private java.lang.String cysfsZjgs;
    /**
     * 清单号
     */
    @Excel(name = "清单号", width = 15)
    @ApiModelProperty(value = "清单号")
    private java.lang.String vwxdwdm;
    /**
     * 运输车代码
     */
    @Excel(name = "运输车代码", width = 15)
    @ApiModelProperty(value = "运输车代码")
    private java.lang.String vyscdm;
    /**
     * 主司机代码
     */
    @Excel(name = "主司机代码", width = 15)
    @ApiModelProperty(value = "主司机代码")
    private java.lang.String vzsjdm;
    /**
     * 付司机代码
     */
    @Excel(name = "付司机代码", width = 15)
    @ApiModelProperty(value = "付司机代码")
    private java.lang.String vfsjdm;
    /**
     * 运输类型
     */
    @Excel(name = "运输类型", width = 15)
    @ApiModelProperty(value = "运输类型")
    private java.lang.String cyslx;
    /**
     * 形成运输计划的人员
     */
    @Excel(name = "形成运输计划的人员", width = 15)
    @ApiModelProperty(value = "形成运输计划的人员")
    private java.lang.String cczydm;
    /**
     * 指车时间   date 统一时间戳 bigint
     */
    @Excel(name = "指车时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "指车时间   date 统一时间戳 bigint")
    private java.lang.Long dzpsj;
    /**
     * 指车操作员
     */
    @Excel(name = "指车操作员", width = 15)
    @ApiModelProperty(value = "指车操作员")
    private java.lang.String czpydm;
    /**
     * 返程登记时间  date 统一时间戳 bigint
     */
    @Excel(name = "返程登记时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "返程登记时间  date 统一时间戳 bigint")
    private java.lang.Long dfcdjsj;
    /**
     * 返单操作员代码
     */
    @Excel(name = "返单操作员代码", width = 15)
    @ApiModelProperty(value = "返单操作员代码")
    private java.lang.String cdjydm;
    /**
     * 承运商代码 3-20
     */
    @Excel(name = "承运商代码 3-20", width = 15)
    @ApiModelProperty(value = "承运商代码 3-20")
    private java.lang.String ccysdm;
    /**
     * 运输商代码
     */
    @Excel(name = "运输商代码", width = 15)
    @ApiModelProperty(value = "运输商代码")
    private java.lang.String cyssdm;
    /**
     * 承运商指派时间  date 统一时间戳 bigint
     */
    @Excel(name = "承运商指派时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "承运商指派时间  date 统一时间戳 bigint")
    private java.lang.Long dcyszpsj;
    /**
     * 运输商指派时间  date 统一时间戳 bigint
     */
    @Excel(name = "运输商指派时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "运输商指派时间  date 统一时间戳 bigint")
    private java.lang.Long dysszpsj;
    /**
     * 承运商指派操作员代码
     */
    @Excel(name = "承运商指派操作员代码", width = 15)
    @ApiModelProperty(value = "承运商指派操作员代码")
    private java.lang.String ccyzpydm;
    /**
     * 运输商指派操作员代码
     */
    @Excel(name = "运输商指派操作员代码", width = 15)
    @ApiModelProperty(value = "运输商指派操作员代码")
    private java.lang.String cyszpydm;
    /**
     * 大众到货时间    date 统一时间戳 bigint
     */
    @Excel(name = "大众到货时间    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "大众到货时间    date 统一时间戳 bigint")
    private java.lang.Long ddhsj;
    /**
     * 配车单编号
     */
    @Excel(name = "配车单编号", width = 15)
    @ApiModelProperty(value = "配车单编号")
    private java.lang.String cpcdbh;
    /**
     * 铁水批次号
     */
    @Excel(name = "铁水批次号", width = 15)
    @ApiModelProperty(value = "铁水批次号")
    private java.lang.String nphid;
    /**
     * date 统一时间戳 bigint
     */
    @Excel(name = "date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "date 统一时间戳 bigint")
    private java.lang.Long dzcsj;
    /**
     * 暂不用 （解放-最迟送达时间）  date 统一时间戳 bigint
     */
    @Excel(name = "暂不用 （解放-最迟送达时间）  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "暂不用 （解放-最迟送达时间）  date 统一时间戳 bigint")
    private java.lang.Long dyjwcsj;
    /**
     * LJS码。MDAC33.CLJSDM
     */
    @Excel(name = "LJS码。MDAC33.CLJSDM", width = 15)
    @ApiModelProperty(value = "LJS码。MDAC33.CLJSDM")
    private java.lang.String cfwm;
    /**
     * 中继标识。0非中继，Z0中继主运单、Z1中继运单
     */
    @Excel(name = "中继标识。0非中继，Z0中继主运单、Z1中继运单", width = 15)
    @ApiModelProperty(value = "中继标识。0非中继，Z0中继主运单、Z1中继运单")
    private java.lang.String czjbs;
    /**
     * 出库日期    date 统一时间戳 bigint
     */
    @Excel(name = "出库日期    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "出库日期    date 统一时间戳 bigint")
    private java.lang.Long dckrq;
    /**
     * 支付表号。SPTB7.CZFBH
     */
    @Excel(name = "支付表号。SPTB7.CZFBH", width = 15)
    @ApiModelProperty(value = "支付表号。SPTB7.CZFBH")
    private java.lang.String czfbh;
    /**
     * 返单确认时间   date 统一时间戳 bigint
     */
    @Excel(name = "返单确认时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "返单确认时间   date 统一时间戳 bigint")
    private java.lang.Long dbcsj;
    /**
     * 暂不用
     */
    @Excel(name = "暂不用", width = 15)
    @ApiModelProperty(value = "暂不用")
    private java.lang.String vckkgydm;
    /**
     * 运输方式
     */
    @Excel(name = "运输方式", width = 15)
    @ApiModelProperty(value = "运输方式")
    private java.lang.String vysfs;
    /**
     * 暂不用(铁水存值啦) 实际存的是：发车的产权仓库 暂不用这个概念！
     */
    @Excel(name = "暂不用(铁水存值啦) 实际存的是：发车的产权仓库 暂不用这个概念！", width = 15)
    @ApiModelProperty(value = "暂不用(铁水存值啦) 实际存的是：发车的产权仓库 暂不用这个概念！")
    private java.lang.String cfydd;
    /**
     * 同SPTC34 物理仓库代码
     */
    @Excel(name = "同SPTC34 物理仓库代码", width = 15)
    @ApiModelProperty(value = "同SPTC34 物理仓库代码")
    private java.lang.String vwlckdm;
    /**
     * 运单类型
     */
    @Excel(name = "运单类型", width = 15)
    @ApiModelProperty(value = "运单类型")
    private java.lang.String cydlx;
    /**
     * 经销商代码
     */
    @Excel(name = "经销商代码", width = 15)
    @ApiModelProperty(value = "经销商代码")
    private java.lang.String vdwdm;
    /**
     * 收车地址 100-200 20220309
     */
    @Excel(name = "收车地址 100-200 20220309 ", width = 15)
    @ApiModelProperty(value = "收车地址 100-200 20220309 ")
    private java.lang.String vscdz;
    /**
     * 到货地代码
     */
    @Excel(name = "到货地代码", width = 15)
    @ApiModelProperty(value = "到货地代码")
    private java.lang.String cdhddm;
    /**
     * 陆捷用作业务接口数据传递标识
     */
    @Excel(name = "陆捷用作业务接口数据传递标识", width = 15)
    @ApiModelProperty(value = "陆捷用作业务接口数据传递标识")
    private java.lang.String cclbs;
    /**
     * 入中转库时间  date 统一时间戳 bigint
     */
    @Excel(name = "入中转库时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "入中转库时间  date 统一时间戳 bigint")
    private java.lang.Long dckqrsj;
    /**
     * 暂不用
     */
    @Excel(name = "暂不用", width = 15)
    @ApiModelProperty(value = "暂不用")
    private java.lang.String cjkbs;
    /**
     * 暂不用        date 统一时间戳 bigint
     */
    @Excel(name = "暂不用        date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "暂不用        date 统一时间戳 bigint")
    private java.lang.Long djkrq;
    /**
     * 任务单号 20-30
     */
    @Excel(name = "任务单号 20-30", width = 15)
    @ApiModelProperty(value = "任务单号 20-30")
    private java.lang.String cjhdh;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private java.lang.String chtcode;
    /**
     * 暂不用
     */
    @Excel(name = "暂不用", width = 15)
    @ApiModelProperty(value = "暂不用")
    private java.lang.String vqrbz;
    /**
     * 存储区域公司
     */
    @Excel(name = "存储区域公司", width = 15)
    @ApiModelProperty(value = "存储区域公司")
    private java.lang.String cqrr;
    /**
     * 集运时间    date 统一时间戳 bigint
     */
    @Excel(name = "集运时间    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "集运时间    date 统一时间戳 bigint")
    private java.lang.Long djysj;
    /**
     * 集运车号
     */
    @Excel(name = "集运车号", width = 15)
    @ApiModelProperty(value = "集运车号")
    private java.lang.String cjych;
    /**
     * 暂不用 (存的是牌照号)
     */
    @Excel(name = "暂不用 (存的是牌照号)", width = 15)
    @ApiModelProperty(value = "暂不用 (存的是牌照号)")
    private java.lang.String cxsqrr;
    /**
     * 陆捷用作常态到货时间   date 统一时间戳 bigint
     */
    @Excel(name = "陆捷用作常态到货时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "陆捷用作常态到货时间   date 统一时间戳 bigint")
    private java.lang.Long dxsqrsj;
    /**
     * 暂不用
     */
    @Excel(name = "暂不用", width = 15)
    @ApiModelProperty(value = "暂不用")
    private java.lang.String vxsbz;
    /**
     * 周五到货时间   date 统一时间戳 bigint
     */
    @Excel(name = "周五到货时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "周五到货时间   date 统一时间戳 bigint")
    private java.lang.Long dysxd;
    /**
     * 出库结算时间   date 统一时间戳 bigint
     */
    @Excel(name = "出库结算时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "出库结算时间   date 统一时间戳 bigint")
    private java.lang.Long dckjssj;
    /**
     * 调度中心返单时间  date 统一时间戳 bigint
     */
    @Excel(name = "调度中心返单时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "调度中心返单时间  date 统一时间戳 bigint")
    private java.lang.Long dckxd;
    /**
     * 结算排序，用于结算表  number 转 int
     */
    @Excel(name = "结算排序，用于结算表  number 转 int", width = 15)
    @ApiModelProperty(value = "结算排序，用于结算表  number 转 int")
    private java.lang.Integer nfjyf;
    /**
     * 暂不用
     */
    @Excel(name = "暂不用", width = 15)
    @ApiModelProperty(value = "暂不用")
    private java.lang.String cpzbs;
    /**
     * 建单日期 （SPTB01C.DDJRQ）date 统一时间戳 bigint
     */
    @Excel(name = "建单日期 （SPTB01C.DDJRQ）date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "建单日期 （SPTB01C.DDJRQ）date 统一时间戳 bigint")
    private java.lang.Long dpzrq;
    /**
     * 结算表号。SPTB16.CJSBH。年月+3位流水号
     */
    @Excel(name = "结算表号。SPTB16.CJSBH。年月+3位流水号", width = 15)
    @ApiModelProperty(value = "结算表号。SPTB16.CJSBH。年月+3位流水号")
    private java.lang.String cpzczy;
    /**
     * 配载单编号
     */
    @Excel(name = "配载单编号", width = 15)
    @ApiModelProperty(value = "配载单编号")
    private java.lang.String cpzdbh;
    /**
     * 配载单序号 20210109 2-10
     */
    @Excel(name = "配载单序号 20210109 2-10", width = 15)
    @ApiModelProperty(value = "配载单序号 20210109 2-10")
    private java.lang.String cpzdxh;
    /**
     * 拆板标识2010-06-09
     */
    @Excel(name = "拆板标识2010-06-09", width = 15)
    @ApiModelProperty(value = "拆板标识2010-06-09")
    private java.lang.String cfpbs;
    /**
     * 暂不用   date 统一时间戳 bigint
     */
    @Excel(name = "暂不用   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "暂不用   date 统一时间戳 bigint")
    private java.lang.Long ddcdksj;
    /**
     * GPS到货时间  date 统一时间戳 bigint
     */
    @Excel(name = "GPS到货时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "GPS到货时间  date 统一时间戳 bigint")
    private java.lang.Long dgpsdhsj;
    /**
     * 实际离长时间  date 统一时间戳 bigint
     */
    @Excel(name = "实际离长时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "实际离长时间  date 统一时间戳 bigint")
    private java.lang.Long dsjcfsj;
    /**
     * 异常状态 0：与R3出库车辆一致，2：与R3出库车辆不致，1：TMS未指车但R3已出库
     */
    @Excel(name = "异常状态 0：与R3出库车辆一致，2：与R3出库车辆不致，1：TMS未指车但R3已出库", width = 15)
    @ApiModelProperty(value = "异常状态 0：与R3出库车辆一致，2：与R3出库车辆不致，1：TMS未指车但R3已出库")
    private java.lang.String cyczt;
    /**
     * 经销商（大客户）代码
     */
    @Excel(name = "经销商（大客户）代码", width = 15)
    @ApiModelProperty(value = "经销商（大客户）代码")
    private java.lang.String cdkhdm;
    /**
     * 预计到货时间 陆捷理论到货时间＝离长时间＋运输时间   date 统一时间戳 bigint
     */
    @Excel(name = "预计到货时间 陆捷理论到货时间＝离长时间＋运输时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "预计到货时间 陆捷理论到货时间＝离长时间＋运输时间   date 统一时间戳 bigint")
    private java.lang.Long dyjdhsj;
    /**
     * 实际牌照号
     */
    @Excel(name = "实际牌照号", width = 15)
    @ApiModelProperty(value = "实际牌照号")
    private java.lang.String vsjpzh;
    /**
     * 备注 （针对中途换车）
     */
    @Excel(name = "备注 （针对中途换车）", width = 15)
    @ApiModelProperty(value = "备注 （针对中途换车）")
    private java.lang.String vbz1;
    /**
     * BM单号。
     */
    @Excel(name = "BM单号。", width = 15)
    @ApiModelProperty(value = "BM单号。")
    private java.lang.String vdhpj;
    /**
     * GPS回长时间   date 统一时间戳 bigint
     */
    @Excel(name = "GPS回长时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "GPS回长时间   date 统一时间戳 bigint")
    private java.lang.Long dgpshcsj;
    /**
     * 新P号：做为指派运输车的前一步
     */
    @Excel(name = "新P号：做为指派运输车的前一步", width = 15)
    @ApiModelProperty(value = "新P号：做为指派运输车的前一步")
    private java.lang.String vph;
    /**
     * 新P号指生成时间    date 统一时间戳 bigint
     */
    @Excel(name = "新P号指生成时间    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "新P号指生成时间    date 统一时间戳 bigint")
    private java.lang.Long dphscsj;
    /**
     * 配板标识
     */
    @Excel(name = "配板标识", width = 15)
    @ApiModelProperty(value = "配板标识")
    private java.lang.String vpbbs;
    /**
     * 大客户单据确认标识1确认  0未确认
     */
    @Excel(name = "大客户单据确认标识1确认  0未确认", width = 15)
    @ApiModelProperty(value = "大客户单据确认标识1确认  0未确认")
    private java.lang.String vdkhqrbs;
    /**
     * 理论离长时间  date 统一时间戳 bigint
     */
    @Excel(name = "理论离长时间  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "理论离长时间  date 统一时间戳 bigint")
    private java.lang.Long dlllcsj;
    /**
     * 管理费确认人
     */
    @Excel(name = "管理费确认人", width = 15)
    @ApiModelProperty(value = "管理费确认人")
    private java.lang.String cglfqrczy;
    /**
     * 管理费确认时间   date 统一时间戳 bigint
     */
    @Excel(name = "管理费确认时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "管理费确认时间   date 统一时间戳 bigint")
    private java.lang.Long dglfqrsj;
    /**
     * 多公司模式下的公司-SPTC60
     */
    @Excel(name = "多公司模式下的公司-SPTC60", width = 15)
    @ApiModelProperty(value = "多公司模式下的公司-SPTC60")
    private java.lang.String cgs;
    /**
     * 备用金标识
     */
    @Excel(name = "备用金标识", width = 15)
    @ApiModelProperty(value = "备用金标识")
    private java.lang.String cbyjqrbs;
    /**
     * 备用金确认人
     */
    @Excel(name = "备用金确认人", width = 15)
    @ApiModelProperty(value = "备用金确认人")
    private java.lang.String cbyjqrczy;
    /**
     * 备用金确认时间   date 统一时间戳 bigint
     */
    @Excel(name = "备用金确认时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "备用金确认时间   date 统一时间戳 bigint")
    private java.lang.Long dbyjqrsj;
    /**
     * 原承运商代码2010-06-09
     */
    @Excel(name = "原承运商代码2010-06-09", width = 15)
    @ApiModelProperty(value = "原承运商代码2010-06-09")
    private java.lang.String ccysdmO;
    /**
     * 原运输商代码2010-06-09
     */
    @Excel(name = "原运输商代码2010-06-09", width = 15)
    @ApiModelProperty(value = "原运输商代码2010-06-09")
    private java.lang.String cyssdmO;
    /**
     * 管理费备注
     */
    @Excel(name = "管理费备注", width = 15)
    @ApiModelProperty(value = "管理费备注")
    private java.lang.String cglfbz;
    /**
     * GPS异常原因 SYSC09D.GPSYCYY
     */
    @Excel(name = "GPS异常原因 SYSC09D.GPSYCYY", width = 15)
    @ApiModelProperty(value = "GPS异常原因 SYSC09D.GPSYCYY")
    private java.lang.String cgpsycyy;
    /**
     * 指派时异常类型 SYSC09D.ZPYCLX
     */
    @Excel(name = "指派时异常类型 SYSC09D.ZPYCLX", width = 15)
    @ApiModelProperty(value = "指派时异常类型 SYSC09D.ZPYCLX")
    private java.lang.String czpyclx;
    /**
     * 返单大序号  number 转 int
     */
    @Excel(name = "返单大序号  number 转 int", width = 15)
    @ApiModelProperty(value = "返单大序号  number 转 int")
    private java.lang.Integer nfddxh;
    /**
     * 发车站台
     */
    @Excel(name = "发车站台", width = 15)
    @ApiModelProperty(value = "发车站台")
    private java.lang.String vfczt;
    /**
     * 收车站台
     */
    @Excel(name = "收车站台", width = 15)
    @ApiModelProperty(value = "收车站台")
    private java.lang.String vsczt;
    /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）
     */
    @Excel(name = "区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）", width = 15)
    @ApiModelProperty(value = "区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）")
    private java.lang.String cqwh;
    /**
     * 主机公司代码  字典（WTDW）
     */
    @Excel(name = "主机公司代码  字典（WTDW）", width = 15)
    @ApiModelProperty(value = "主机公司代码  字典（WTDW）")
    private java.lang.String czjgsdm;
    /**
     * 支付运输商表号。SPTB7.CZFBH
     */
    @Excel(name = "支付运输商表号。SPTB7.CZFBH", width = 15)
    @ApiModelProperty(value = "支付运输商表号。SPTB7.CZFBH")
    private java.lang.String czfbh2;
    /**
     * 分段的关连主键
     */
    @Excel(name = "分段的关连主键", width = 15)
    @ApiModelProperty(value = "分段的关连主键")
    private java.lang.String cjsdbh2;
    /**
     * 支付运输商补偿表号
     */
    @Excel(name = "支付运输商补偿表号", width = 15)
    @ApiModelProperty(value = "支付运输商补偿表号")
    private java.lang.String czfbh3;
    /**
     * 支付基地补偿表号
     */
    @Excel(name = "支付基地补偿表号", width = 15)
    @ApiModelProperty(value = "支付基地补偿表号")
    private java.lang.String czfbh4;
    /**
     * TVS交车时间   date 统一时间戳 bigint
     */
    @Excel(name = "TVS交车时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "TVS交车时间   date 统一时间戳 bigint")
    private java.lang.Long dtvsjcsj;
    /**
     * TVS出发时间   date 统一时间戳 bigint
     */
    @Excel(name = "TVS出发时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "TVS出发时间   date 统一时间戳 bigint")
    private java.lang.Long dtvscfsj;
    /**
     * TVS到货时间    date 统一时间戳 bigint
     */
    @Excel(name = "TVS到货时间    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "TVS到货时间    date 统一时间戳 bigint")
    private java.lang.Long dtvsdhsj;
    /**
     * TVS返回时间。没啥意义  date 统一时间戳 bigint
     */
    @Excel(name = "TVS返回时间。没啥意义  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "TVS返回时间。没啥意义  date 统一时间戳 bigint")
    private java.lang.Long dtvsfcsj;
    /**
     * TVS批次号
     */
    @Excel(name = "TVS批次号", width = 15)
    @ApiModelProperty(value = "TVS批次号")
    private java.lang.String ctvsph;
    /**
     * 铁路车厢号、水路船舶号
     */
    @Excel(name = "铁路车厢号、水路船舶号", width = 15)
    @ApiModelProperty(value = "铁路车厢号、水路船舶号")
    private java.lang.String vehid;
    /**
     * TVS中途换车时间   date 统一时间戳 bigint
     */
    @Excel(name = "TVS中途换车时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "TVS中途换车时间   date 统一时间戳 bigint")
    private java.lang.Long dtvsjcsj2;
    /**
     * 时间戳。BI提数据   timestamp 转 bigint
     */
    @Excel(name = "时间戳。BI提数据   timestamp 转 bigint", width = 15)
    @ApiModelProperty(value = "时间戳。BI提数据   timestamp 转 bigint")
    private java.lang.Long dstamp;
    /**
     * 交车位置省份
     */
    @Excel(name = "交车位置省份", width = 15)
    @ApiModelProperty(value = "交车位置省份")
    private java.lang.String cjcsf;
    /**
     * 交车位置城市
     */
    @Excel(name = "交车位置城市", width = 15)
    @ApiModelProperty(value = "交车位置城市")
    private java.lang.String cjccs;
    /**
     * 路径点数  number 转 int
     */
    @Excel(name = "路径点数  number 转 int", width = 15)
    @ApiModelProperty(value = "路径点数  number 转 int")
    private java.lang.Integer nljds;
    /**
     * 总部项目成本单
     */
    @Excel(name = "总部项目成本单", width = 15)
    @ApiModelProperty(value = "总部项目成本单")
    private java.lang.String cxmcbdZb;
    /**
     * 基地项目成本单
     */
    @Excel(name = "基地项目成本单", width = 15)
    @ApiModelProperty(value = "基地项目成本单")
    private java.lang.String cxmcbdJd;
    /**
     * 20180523 add by dtf 大众估价支付部分 税率特殊处理（支付价/1.11*1.1） ='1' 代表需要特殊处理！
     */
    @Excel(name = "20180523 add by dtf 大众估价支付部分 税率特殊处理（支付价/1.11*1.1） ='1' 代表需要特殊处理！", width = 15)
    @ApiModelProperty(value = "20180523 add by dtf 大众估价支付部分 税率特殊处理（支付价/1.11*1.1） ='1' 代表需要特殊处理！")
    private java.lang.String cbsGjzf;
    /**
     * 上一段结算单号
     */
    @Excel(name = "上一段结算单号", width = 15)
    @ApiModelProperty(value = "上一段结算单号")
    private java.lang.String cjsdbhLast;
    /**
     * 储运备注 解放使用，王大军提
     */
    @Excel(name = "储运备注 解放使用，王大军提", width = 15)
    @ApiModelProperty(value = "储运备注 解放使用，王大军提")
    private java.lang.String ccybz;
    /**
     * 经销商备注 解放使用，王大军提
     */
    @Excel(name = "经销商备注 解放使用，王大军提", width = 15)
    @ApiModelProperty(value = "经销商备注 解放使用，王大军提")
    private java.lang.String cjxsbz;
    /**
     * 销售备注 解放使用，王大军提
     */
    @Excel(name = "销售备注 解放使用，王大军提", width = 15)
    @ApiModelProperty(value = "销售备注 解放使用，王大军提")
    private java.lang.String cxsbz;
    /**
     * 销售订单编号，解放使用，王大军提
     */
    @Excel(name = "销售订单编号，解放使用，王大军提", width = 15)
    @ApiModelProperty(value = "销售订单编号，解放使用，王大军提")
    private java.lang.String cxsddbh;
    /**
     * B平台标识
     */
    @Excel(name = "B平台标识", width = 15)
    @ApiModelProperty(value = "B平台标识")
    private java.lang.String ptbs;
    /**
     * B平台运输商
     */
    @Excel(name = "B平台运输商", width = 15)
    @ApiModelProperty(value = "B平台运输商")
    private java.lang.String cyysdmPt;
    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */
    @Excel(name = "销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910", width = 15)
    @ApiModelProperty(value = "销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910")
    private java.lang.String cxsgsdm;
    /**
     * 销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910
     */
    @Excel(name = "销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910", width = 15)
    @ApiModelProperty(value = "销售公司代码 区分解放青岛 轻汽厂和贸易公司 add by lwx 20180910")
    private java.lang.String vxsgsmc;
    /**
     * 轿车分段序号  number 转 int
     */
    @Excel(name = "轿车分段序号  number 转 int", width = 15)
    @ApiModelProperty(value = "轿车分段序号  number 转 int")
    private java.lang.Integer njcfdxh;
    /**
     * 实车交接表示 DSS APP 1已交接；0未交接；
     */
    @Excel(name = "实车交接表示 DSS APP 1已交接；0未交接；", width = 15)
    @ApiModelProperty(value = "实车交接表示 DSS APP 1已交接；0未交接；")
    private java.lang.String cbsScjj;
    /**
     * 2018补偿报表 比例和定额即直发和分拨 标识 11 12  1干线；2分拨；
     */
    @Excel(name = "2018补偿报表 比例和定额即直发和分拨 标识 11 12  1干线；2分拨；", width = 15)
    @ApiModelProperty(value = "2018补偿报表 比例和定额即直发和分拨 标识 11 12  1干线；2分拨；")
    private java.lang.String cbsBcbblx;
    /**
     * 实车交接时间 DSS实车到货时间 20190408  date 统一时间戳 bigint
     */
    @Excel(name = "实车交接时间 DSS实车到货时间 20190408  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "实车交接时间 DSS实车到货时间 20190408  date 统一时间戳 bigint")
    private java.lang.Long dateScjj;
    /**
     * 客服确认标记
     */
    @Excel(name = "客服确认标记", width = 15)
    @ApiModelProperty(value = "客服确认标记")
    private java.lang.String cbsCusconfirm;
    /**
     * 运营部确认标记
     */
    @Excel(name = "运营部确认标记", width = 15)
    @ApiModelProperty(value = "运营部确认标记")
    private java.lang.String cbsOperdepart;
    /**
     * 装车完成时间 20191020  date 统一时间戳 bigint
     */
    @Excel(name = "装车完成时间 20191020  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "装车完成时间 20191020  date 统一时间戳 bigint")
    private java.lang.Long dzcwcsj;
    /**
     * 到达站台时间 20191022  date 统一时间戳 bigint
     */
    @Excel(name = "到达站台时间 20191022  date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "到达站台时间 20191022  date 统一时间戳 bigint")
    private java.lang.Long dddztsj;
    /**
     * 同步标识 0未同步，1已同步
     */
    @Excel(name = "同步标识 0未同步，1已同步", width = 15)
    @ApiModelProperty(value = "同步标识 0未同步，1已同步")
    private java.lang.String isSync;
    /**
     * 20191217 QDC X传统能源；D纯电；F油电混动；
     */
    @Excel(name = "20191217 QDC X传统能源；D纯电；F油电混动；", width = 15)
    @ApiModelProperty(value = "20191217 QDC X传统能源；D纯电；F油电混动；")
    private java.lang.String evFlag;
    /**
     * 特殊发运车辆标识
     */
    @Excel(name = "特殊发运车辆标识", width = 15)
    @ApiModelProperty(value = "特殊发运车辆标识")
    private java.lang.String svFlag;
    /**
     * 内部流水号
     */
    @Excel(name = "内部流水号", width = 15)
    @ApiModelProperty(value = "内部流水号")
    private java.lang.String interCode;
    /**
     * 发车单位名称城市
     */
    @Excel(name = "发车单位名称城市", width = 15)
    @ApiModelProperty(value = "发车单位名称城市")
    private java.lang.String nameFrom;
    /**
     * 收车单位名称城市
     */
    @Excel(name = "收车单位名称城市 ", width = 15)
    @ApiModelProperty(value = "收车单位名称城市 ")
    private java.lang.String nameTo;
    /**
     * 发车人及联系方式
     */
    @Excel(name = "发车人及联系方式", width = 15)
    @ApiModelProperty(value = "发车人及联系方式")
    private java.lang.String senPerson;
    /**
     * 收车人及联系方式
     */
    @Excel(name = "收车人及联系方式", width = 15)
    @ApiModelProperty(value = "收车人及联系方式")
    private java.lang.String recPerson;
    /**
     * 完整经办人及联系方式
     */
    @Excel(name = "完整经办人及联系方式", width = 15)
    @ApiModelProperty(value = "完整经办人及联系方式")
    private java.lang.String personPhone;
    /**
     * 发货单位地址
     */
    @Excel(name = "发货单位地址", width = 15)
    @ApiModelProperty(value = "发货单位地址")
    private java.lang.String addressFrom;
    /**
     * 收货单位地址
     */
    @Excel(name = "收货单位地址", width = 15)
    @ApiModelProperty(value = "收货单位地址")
    private java.lang.String addressTo;
    /**
     * 发货时间           date 统一时间戳 bigint
     */
    @Excel(name = "发货时间           date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "发货时间           date 统一时间戳 bigint")
    private java.lang.Long reqDate;
    /**
     * 要求送达时间     date 统一时间戳 bigint
     */
    @Excel(name = "要求送达时间     date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "要求送达时间     date 统一时间戳 bigint")
    private java.lang.Long fhDate;
    /**
     * 车辆类型
     */
    @Excel(name = "车辆类型", width = 15)
    @ApiModelProperty(value = "车辆类型")
    private java.lang.String submi;
    /**
     * 经办单位
     */
    @Excel(name = "经办单位", width = 15)
    @ApiModelProperty(value = "经办单位")
    private java.lang.String deptm;
    /**
     * 具体要求说明
     */
    @Excel(name = "具体要求说明", width = 15)
    @ApiModelProperty(value = "具体要求说明")
    private java.lang.String info;
    /**
     * 2位车型
     */
    @Excel(name = "2位车型", width = 15)
    @ApiModelProperty(value = "2位车型")
    private java.lang.String cartype;
    /**
     * 品牌
     */
    @Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
    private java.lang.String brand;
    /**
     * 车型描述
     */
    @Excel(name = "车型描述", width = 15)
    @ApiModelProperty(value = "车型描述")
    private java.lang.String name2;
    /**
     * 支付明细管理生成-操作人代码
     */
    @Excel(name = "支付明细管理生成-操作人代码", width = 15)
    @ApiModelProperty(value = "支付明细管理生成-操作人代码")
    private java.lang.String zfConfirmCczydm;
    /**
     * 支付明细管理生成-操作员名称
     */
    @Excel(name = "支付明细管理生成-操作员名称", width = 15)
    @ApiModelProperty(value = "支付明细管理生成-操作员名称")
    private java.lang.String zfConfirmCczymc;
    /**
     * 支付明细管理生成-操作日期    date 统一时间戳 bigint
     */
    @Excel(name = "支付明细管理生成-操作日期    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "支付明细管理生成-操作日期    date 统一时间戳 bigint")
    private java.lang.Long zfConfirmDczrq;
    /**
     * 铁路列号更新时间                 date 统一时间戳 bigint
     */
    @Excel(name = "铁路列号更新时间                 date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "铁路列号更新时间                 date 统一时间戳 bigint")
    private java.lang.Long dtllhscsj;
    /**
     * GPS路径点数    number 转 int
     */
    @Excel(name = "GPS路径点数    number 转 int", width = 15)
    @ApiModelProperty(value = "GPS路径点数    number 转 int")
    private java.lang.Integer nljdsGps;
    /**
     * 标准GPS到货时间         date 统一时间戳 bigint
     */
    @Excel(name = "标准GPS到货时间         date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "标准GPS到货时间         date 统一时间戳 bigint")
    private java.lang.Long ddhsjGps;
    /**
     * 标准系统到货时间         date 统一时间戳 bigint
     */
    @Excel(name = "标准系统到货时间         date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "标准系统到货时间         date 统一时间戳 bigint")
    private java.lang.Long ddhsjXt;
    /**
     * 铁路卸车时间 20200525    date 统一时间戳 bigint
     */
    @Excel(name = "铁路卸车时间 20200525    date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "铁路卸车时间 20200525    date 统一时间戳 bigint")
    private java.lang.Long dztxcsj;
    /**
     * 20200708 奔马终段预计到货时间   date 统一时间戳 bigint
     */
    @Excel(name = "20200708 奔马终段预计到货时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "20200708 奔马终段预计到货时间   date 统一时间戳 bigint")
    private java.lang.Long dyjdhsjBm;
    /**
     * 20200708 奔马分段预计到货时间   date 统一时间戳 bigint
     */
    @Excel(name = "20200708 奔马分段预计到货时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "20200708 奔马分段预计到货时间   date 统一时间戳 bigint")
    private java.lang.Long dfdyjdhsjBm;
    /**
     * 20201014 保密车支付表号
     */
    @Excel(name = "20201014 保密车支付表号", width = 15)
    @ApiModelProperty(value = "20201014 保密车支付表号")
    private java.lang.String czfbhBmcbc;
    /**
     * 20201014 保密车结算表号
     */
    @Excel(name = "20201014 保密车结算表号", width = 15)
    @ApiModelProperty(value = "20201014 保密车结算表号")
    private java.lang.String cjsbhBmcbc;
    /**
     * 保密车补位报表 BM单号
     */
    @Excel(name = "保密车补位报表 BM单号", width = 15)
    @ApiModelProperty(value = "保密车补位报表 BM单号")
    private java.lang.String vdhpjBmcbc;
    /**
     * 20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览
     */
    @Excel(name = "20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览", width = 15)
    @ApiModelProperty(value = "20210113 DTF 特殊发运类型 BM-保密,QT-其他,SP-索赔,ZL-展览")
    private java.lang.String svType;
    /**
     * 20210224 大众铁水结算状态
     */
    @Excel(name = "20210224 大众铁水结算状态", width = 15)
    @ApiModelProperty(value = "20210224 大众铁水结算状态")
    private java.lang.String cztJsTs;
    /**
     * 20210224 大众铁水支付状态
     */
    @Excel(name = "20210224 大众铁水支付状态", width = 15)
    @ApiModelProperty(value = "20210224 大众铁水支付状态")
    private java.lang.String cztZfTs;
    /**
     * 20210422 DTF 大众标准物流时间   number 转  bigint
     */
    @Excel(name = "20210422 DTF 大众标准物流时间   number 转  bigint", width = 15)
    @ApiModelProperty(value = "20210422 DTF 大众标准物流时间   number 转  bigint")
    private java.lang.Long nbzwlsjDz;
    /**
     * 20210422 DTF 大众标准到货时间   date 统一时间戳 bigint
     */
    @Excel(name = "20210422 DTF 大众标准到货时间   date 统一时间戳 bigint", width = 15)
    @ApiModelProperty(value = "20210422 DTF 大众标准到货时间   date 统一时间戳 bigint")
    private java.lang.Long dbzdhsjDz;
    /**
     * STD同城标识
     */
    @Excel(name = "STD同城标识", width = 15)
    @ApiModelProperty(value = "STD同城标识")
    private java.lang.String ctcbs;
    /**
     * 新增-创建时间
     */
    @Excel(name = "新增-创建时间", width = 15)
    @ApiModelProperty(value = "新增-创建时间")
    private java.lang.Integer warehouseCreatetime;
    /**
     * 新增-更新时间
     */
    @Excel(name = "新增-更新时间", width = 15)
    @ApiModelProperty(value = "新增-更新时间")
    private java.lang.Integer warehouseUpdatetime;
    /**
     * dwd新增-运输方式 公路 G 水路 S 铁路 T
     */
    @Excel(name = "dwd新增-运输方式 公路 G 水路 S 铁路 T", width = 15)
    @ApiModelProperty(value = "dwd新增-运输方式 公路 G 水路 S 铁路 T")
    private java.lang.String trafficType;
    /**
     * dwd新增-起运时间 公路-取DTVSCFSJ(TVS出发时间)的值;铁水-取DSJCFSJ(实际离长时间)的值
     */
    @Excel(name = "dwd新增-起运时间 公路-取DTVSCFSJ(TVS出发时间)的值;铁水-取DSJCFSJ(实际离长时间)的值", width = 15)
    @ApiModelProperty(value = "dwd新增-起运时间 公路-取DTVSCFSJ(TVS出发时间)的值;铁水-取DSJCFSJ(实际离长时间)的值")
    private java.lang.Integer shipmentTime;
    /**
     * dwd新增-计划下达时间
     */
    @Excel(name = "dwd新增-计划下达时间", width = 15)
    @ApiModelProperty(value = "dwd新增-计划下达时间")
    private java.lang.Integer planReleaseTime;
    /**
     * dwd新增-运单指派时间
     */
    @Excel(name = "dwd新增-运单指派时间", width = 15)
    @ApiModelProperty(value = "dwd新增-运单指派时间")
    private java.lang.Integer assignTime;
    /**
     * dwd新增-打点到货时间
     */
    @Excel(name = "dwd新增-打点到货时间", width = 15)
    @ApiModelProperty(value = "dwd新增-打点到货时间")
    private java.lang.Integer dotSiteTime;
    /**
     * dwd新增-最终到货时间
     */
    @Excel(name = "dwd新增-最终到货时间", width = 15)
    @ApiModelProperty(value = "dwd新增-最终到货时间")
    private java.lang.Integer finalSiteTime;
    /**
     * dwd新增-运单生成时间
     */
    @Excel(name = "dwd新增-运单生成时间", width = 15)
    @ApiModelProperty(value = "dwd新增-运单生成时间")
    private java.lang.Integer orderCreateTime;
    /**
     * dwd基地代码转换 适配 lc_spec_config
     */
    @Excel(name = "dwd基地代码转换 适配 lc_spec_config", width = 15)
    @ApiModelProperty(value = "dwd基地代码转换 适配 lc_spec_config")
    private java.lang.String baseCode;
    /**
     * dwd运输方式代码 适配 lc_spec_config
     */
    @Excel(name = "dwd运输方式代码 适配 lc_spec_config", width = 15)
    @ApiModelProperty(value = "dwd运输方式代码 适配 lc_spec_config")
    private java.lang.String transModeCode;
    /**
     * dwd主机公司代码转换 适配 lc_spec_config
     */
    @Excel(name = "dwd主机公司代码转换 适配 lc_spec_config", width = 15)
    @ApiModelProperty(value = "dwd主机公司代码转换 适配 lc_spec_config")
    private java.lang.String hostComCode;
    /**
     * dwm新增-理论起运时间
     */
    @Excel(name = "dwm新增-理论起运时间", width = 15)
    @ApiModelProperty(value = "dwm新增-理论起运时间")
    private java.lang.Integer theoryShipmentTime;
    /**
     * dwm新增-理论出库时间
     */
    @Excel(name = "dwm新增-理论出库时间", width = 15)
    @ApiModelProperty(value = "dwm新增-理论出库时间")
    private java.lang.Integer theoryOutTime;
    /**
     * dwm新增-理论到货时间
     */
    @Excel(name = "dwm新增-理论到货时间", width = 15)
    @ApiModelProperty(value = "dwm新增-理论到货时间")
    private java.lang.Integer theorySiteTime;
    /**
     * dwm新增-实际出库时间
     */
    @Excel(name = "dwm新增-实际出库时间", width = 15)
    @ApiModelProperty(value = "dwm新增-实际出库时间")
    private java.lang.Integer actualOutTime;
    /**
     * dwm新增-入目标库时间
     */
    @Excel(name = "dwm新增-入目标库时间", width = 15)
    @ApiModelProperty(value = "dwm新增-入目标库时间")
    private java.lang.Integer enterTargetTime;
    /**
     * dwm新增-车架号
     */
    @Excel(name = "dwm新增-车架号", width = 15)
    @ApiModelProperty(value = "dwm新增-车架号")
    private java.lang.String vvin;
    /**
     * dwm新增 运单状态-1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    @Excel(name = "dwm新增 运单状态-1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店", width = 15)
    @ApiModelProperty(value = "dwm新增 运单状态-1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店")
    private java.lang.Integer trafficStatus;
    /**
     * dwm新增 运单状态名称 1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    @Excel(name = "dwm新增 运单状态名称 1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店", width = 15)
    @ApiModelProperty(value = "dwm新增 运单状态名称 1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店")
    private java.lang.String trafficStatusName;
}
