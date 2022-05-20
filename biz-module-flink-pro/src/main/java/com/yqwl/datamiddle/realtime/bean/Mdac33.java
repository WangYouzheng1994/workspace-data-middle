package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 运输车辆档案
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Mdac33 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 运输车代码
     */

    private String VYSCDM;

    /**
     * 牌照号
     */

    private String VPZH;

    /**
     * 电话
     */

    private String VDH;

    /**
     * 联系人
     */

    private String CLXR;

    /**
     * 联系电话
     */

    private String VLXDH;

    /**
     * 移动电话
     */

    private String VYDDH;

    /**
     * 运输车型代码
     */

    private String VYSCX;

    /**
     * 0、未停用，1、停用
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 备注
     */

    private String VBZ;

    /**
     * 0/可用，1/已分配，2/在途，3/返程，G/锁定
     */

    private String CZT;

    /**
     * 副司机代码（T）
     */

    private String VFSJDM;

    /**
     * 主司机代码(T)
     */

    private String VZSJDM;

    /**
     * 承运定额(J)
     */

    private Integer NCYDE;

    /**
     * 认证标识。0/未认证，1/认证(J)
     */

    private String CRZBS;

    /**
     * 承运商代码(J)
     */

    private String CCYSDM;

    /**
     * 承运队代码(J)
     */

    private String CCYDDM;

    /**
     * 挂车牌照号
     */

    private String VGCPZH;

    /**
     * 运输车规格型号
     */

    private String VGGXH;


    private String VCLQK;

    /**
     * 0/正常,1/临时
     */

    private String CCLSX;

    /**
     * 专线说明
     */

    private String CZXDM;

    /**
     * GPS锁定时间
     */

    private Long DGPSSDSJ;

    /**
     * GPS解锁时间
     */

    private Long DGPSJSSJ;

    /**
     * 管理费标识 1收管理费 0不收管理费
     */

    private String CGLFBS;

    /**
     * 运费标识 0不收运费  1正常收运费 2挂靠运费 3对流运费(返单登记306和业务接口tr_sptb02_yfcl用到)
     */

    private String CYFBS;

    /**
     * 新车标识。0旧车，1新车
     */

    private String CCYGB;

    /**
     * 车头登记日期   日期型有日期控件
     */

    private Long DCTDJRQ;

    /**
     * 制造厂家    下拉框，下拉内容为 一汽和其他厂
     */

    private String CZZCJ;

    /**
     * 发动机马力  下拉框，下拉内容为 180及以下、180~220、220~260、260~320、320及以上
     */

    private String CFDJML;

    /**
     * 发动机维修   下拉框，下拉内容为 无和更换
     */

    private String CFDJWX;

    /**
     * 挂车登记日期   日期型有日期控件
     */

    private Long DGCDJRQ;

    /**
     * 桥（含牵引头） 下拉框，下拉内容为 三桥、四桥、五桥、六桥
     */

    private String CQ;

    /**
     * 车辆属性      下拉框，下拉内容为 公司、个人
     */

    private String CCLSX1;

    /**
     * 车辆状态      下拉框，下拉内容为 内网车、外网车、加盟商
     */

    private String CCLZT;

    /**
     * 调度员        字符型，50字符长度
     */

    private String CDDYXM;

    /**
     * 核算员        字符型，50字符长度
     */

    private String CHSYXM;

    /**
     * 属性   下拉框，下拉内容为站队、一汽物流
     */

    private String CSX;

    /**
     * 运营模式 下拉框，下拉内容为半运费、对流1、对流2、对流3、挂靠(卓成)、挂靠1、挂靠2、挂靠3、挂靠4、挂靠5、管理费1、管理费2、管理费3、管理费4、里程工资制、买断、内大包、内大包1、外大包
     */

    private String CYYMS;

    /**
     * 支付标准
     */

    private String CZFBS;

    /**
     * 捆绑费 下拉框  下拉内容 是、否
     */

    private String CKBF;

    /**
     * 车厢牌照号
     */

    private String CCXPZH;

    /**
     * 车型        下拉菜单 下拉内容为 正常、A2、宽体、宽体+A2
     */

    private String CCX;

    /**
     * 发动机号    字符型  50字符长度
     */

    private String CFDJH;

    /**
     * 车辆厂牌型号 字符型 50字符长度
     */

    private String CCLCPXH;

    /**
     * 车厢品牌     下拉菜单 下拉内容为  解放牌、通华牌、金鸽牌、万荣牌、环达牌、汇达牌、神行牌、冀骏牌、东堡牌、劳尔牌、其他
     */

    private String CCXPP;

    /**
     * 连接全长（实际尺寸）字符型  50字符长度
     */

    private String CLJQC;

    /**
     * 挂车全长（行车证尺寸）
     */

    private String CGCQC1;

    /**
     * 挂车全长（实际尺寸）
     */

    private String CGCQC2;

    /**
     * 车厢宽度（行车证尺寸）
     */

    private String CCXKD1;

    /**
     * 车厢宽度（实际尺寸）
     */

    private String CCXKD2;

    /**
     * 车厢高度（行车证尺寸）
     */

    private String CCXGD1;

    /**
     * 车厢高度（实际尺寸）
     */

    private String CCXGD2;

    /**
     * 上梯实际尺寸
     */

    private String CSTSJCC;

    /**
     * 下梯实际尺寸
     */

    private String CXTSJCC;

    /**
     * 检车日期
     */

    private Long DJCRQ;

    /**
     * 入网日期
     */

    private Long DRWRQ;

    /**
     * 0、在网，1、清网
     */

    private String CQWBS;

    /**
     * 清网日期
     */

    private Long DQWRQ;

    /**
     * 运力状态
     */

    private String CYLZT;

    /**
     * 区域费率
     */

    private Integer NFL_QY;

    /**
     * 历史运输商简称
     */

    private String CYSSJC_LS;

    /**
     * VW卡号
     */

    private String CVW;

    /**
     * A2卡号
     */

    private String CA2;

    /**
     * 得分
     */

    private Integer NDF;

    /**
     * GPS状态
     */

    private String CGPSZT;

    /**
     * 不合格原因
     */

    private String CBHGYY;


    private String CCLSX2;

    /**
     * 检车结果
     */

    private String CJCJG;

    /**
     * 是否大循环
     */

    private String CSFDXH;

    /**
     * 费率
     */

    private Integer NFL;

    /**
     * 车辆归属：SYSC09D.CZDDM='CLDA-CLGS'
     */

    private String CCLGS;

    /**
     * 1表示非系统车辆
     */

    private String CFXTCL;

    /**
     * 停用原因
     */

    private String CTYYY;

    /**
     * 路线代码。来源于MDAC37C.LXDM
     */

    private String CLXDM;

    /**
     * 检车标识。0未通过，1通过
     */

    private String CKFP;

    /**
     * 序号
     */

    private String CXH;

    /**
     * 牵引车识别代码
     */

    private String CQYCSBDM;

    /**
     * 挂车识别代码
     */

    private String CGCSBDM;

    /**
     * 连接全长（行车证尺寸）字符型  50字符长度
     */

    private String CLJQC1;

    /**
     * 结算单位代码
     */

    private String CLJSDM;

    /**
     * 旧代码
     */

    private String CDM_O;

    /**
     * 物流标准编码。选择，来源于M平台
     */

    private String CWLBM;

    /**
     * 物流标准名称。来源于M平台
     */

    private String CWLMC;

    /**
     * 同步日期
     */

    private Long DTBRQ;

    /**
     * 版本号
     */

    private Integer BATCHNO;

    /**
     * 当后轴保养系统中的车，没有按时进行保养则在V-LMS中进行锁车
     */

    private String CBYSC;

    /**
     * 路线 （长春运力明细字段 统计）
     */

    private String VLX;

    /**
     * 组合 （长春运力明细字段 统计）
     */

    private String VZH;

    /**
     * 路线代码。来源于MDAC37C1.CLXDM
     */

    private String CLXDM1;

    /**
     * 是否上TVS标识：默认0 未上；1已上
     */

    private String CTVSBS;

    /**
     * 合规不合规标识 (默认不合规0；合规1)
     */

    private String CSFHG;

    /**
     * 安装稽查队
     */

    private String VAZJCD;

    /**
     * 区域监控人员
     */

    private String VQYJKRY;

    /**
     * 安装登记负责人
     */

    private String VAZDJFZR;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 绑定TVS日期
     */

    private Long DTVSBSBDRQ;

    /**
     * 20181215 add by dtf 是否分拨 标识
     */

    private String CSFFB;

    /**
     * 空闲定额
     */

    private Integer NCYDE_D;

    /**
     * 牵引车审批标识：0  未审批  1：已审批
     */

    private String APPROVAL_FLAG;

    /**
     * 牵引车审批人
     */

    private String APPROVAL_USER;

    /**
     * 牵引车审批日期
     */

    private Long APPROVAL_DATE;

    /**
     * 牵引车终审审批标识：0  未审批  1：已审批
     */

    private String FINAL_APPROVAL_FLAG;

    /**
     * 牵引车终审审批人
     */

    private String FINAL_APPROVAL_USER;

    /**
     * 牵引车终审审批日期
     */

    private Long FINAL_APPROVAL_DATE;

    /**
     * 初审是否合格
     */

    private String CCSYJ;

    /**
     * 初审时间
     */

    private Long DCSSJ;

    /**
     * 初审人代码
     */

    private String CCSRDM;

    /**
     * 头车车籍
     */

    private String CTCCJ;

    /**
     * 挂车车籍
     */

    private String CGCCJ;

    /**
     * 牵引车小组名称
     */

    private String CXZMC;

    /**
     * 牵引车组长名称
     */

    private String CZZMC;

    /**
     * 挂车审批标识：0  未审批  1：已审批
     */

    private String TRAILER_APPROVAL_FLAG;

    /**
     * 挂车审批人
     */

    private String TRAILER_APPROVAL_USER;

    /**
     * 挂车审批日期
     */

    private Long TRAILER_APPROVAL_DATE;

    /**
     * 挂车终审审批标识：0  未审批  1：已审批
     */

    private String TRAILER_FINAL_APPROVAL_FLAG;

    /**
     * 挂车终审审批人
     */

    private String TRAILER_FINAL_APPROVAL_USER;

    /**
     * 挂车终审审批日期
     */

    private Long TRAILER_FINAL_APPROVAL_DATE;

    /**
     * 挂车小组名称
     */

    private String TRAILER_CXZMC;

    /**
     * 挂车组长名称
     */

    private String TRAILER_CZZMC;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
