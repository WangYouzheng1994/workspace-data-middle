package org.jeecg.yqwl.datamiddle.util;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/11/4 9:38
 * @Version: V1.0
 */
public class ConstantsUtil {
    /**
     * 删除标识： 正常
     */
    public static final Integer DEL_FLAG_NORMAL = 0;

    /**
     * 删除标识： 已删除
     */
    public static final Integer DEL_FLAG_DEL = 1;

    /**
     * 是否可用 : 可用/存在
     */
    public static final String USE_FLAG_NORMAL = "0";
    /**
     * 是否可用 : 禁用/删除
     */
    public static final String USE_FLAG_DISABLED = "1";

    /**
     * 文件类型
     */
    public static final String FILE_XLS = "xls";

    /**
     * 文件类型
     */
    public static final String FILE_XLSX = "xlsx";

    /**
     * 文件类型
     */
    public static final String FILE_DOC = "doc";

    /**
     * 文件类型
     */
    public static final String FILE_DOCX = "docx";

    /**
     * 文件类型
     */
    public static final String FILE_APK = "apk";


    // 人员管理
    /**
     * 陆港
     */
    public static final String COMPANY_TYPE_LUGANG = "0";
    /**
     * 客户
     */
    public static final String COMPANY_TYPE_CUSTOM = "1";
    /**
     * 堆场
     */
    public static final String COMPANY_TYPE_FACTORY = "2";
    /**
     * 承运商
     */
    public static final String COMPANY_TYPE_TRANSPORT = "3";
    /**
     * 代理公司
     */
    public static final String COMPANY_TYPE_PROXY = "4";




    // 提箱单管理------------------------------------START
    /**
     * 提箱单管理 -- 提箱单类型： 用箱
     */
    public static final String ORDER_TYPE_ORDER = "0";
    /**
     * 提箱单管理 -- 提箱单类型： 调运
     */
    public static final String ORDER_TYPE_TRANSPORT = "1";

    /**
     * 提箱单管理 -- 提箱单类型： 转调运
     */
    public static final String ORDER_TYPE_ORDERTOTRANSPORT = "2";

    /**
     * 提箱单管理 -- 提箱单状态： 待提箱
     */
    public static final String EXTRACT_STATE_0 = "0";

    /**
     * 提箱单管理 -- 提箱单状态： 部分提箱
     */
    public static final String EXTRACT_STATE_1 = "1";

    /**
     * 提箱单管理 -- 提箱单状态： 已提箱
     */
    public static final String EXTRACT_STATE_2 = "2";

    /**
     * 提箱单管理 -- 提箱单状态： 过期作废
     */
    public static final String EXTRACT_STATE_3 = "3";

    /**
     * 提箱单管理 -- 提箱单状态： 已取消
     */
    public static final String EXTRACT_STATE_4 = "4";

    // 提箱单管理------------------------------------END

    // 提箱方式------------------------------------START
    /**
     * 提箱方式 -- 提箱方式： 汽车
     */
    public static final String  TRAFFIC_TOOLS_BUS= "汽车";

    /**
     * 提箱单方式 -- 提箱单方式： 火车
     */
    public static final String  TRAFFIC_TOOLS_TRAIN= "火车";

    // 提箱方式------------------------------------END
    //用箱申请----------------------------------START
    /**
     * 用箱申请--订单状态： 待审核
     */
    public static final Integer CHECK_STATUE = 0;
    /**
     * 用箱申请--订单状态： 审核通过
     */
    public static final Integer CHECK_STATUE_1 = 1;
    /**
     * 用箱申请--订单状态： 审核驳回
     */
    public static final Integer CHECK_STATUE_2 = 2;
    /**
     * 用箱申请--订单状态： 过期作废
     */
    public static final Integer CHECK_STATUE_3= 3;
    /**
     * 用箱申请--订单状态： 已取消
     */
    public static final Integer CHECK_STATUE_4 = 4;
    /**
     * 用箱申请--订单状态： 取消待审核
     */
    public static final Integer CHECK_STATUE_5 = 5;
    /**
     * 用箱申请--订单状态： 取消驳回
     */
    public static final Integer CHECK_STATUE_6 = 6;
    /**
     * 用箱申请--订单状态： 系统通过
     */
    public static final Integer CHECK_STATUE_7 = 7;
    /**
     * 用箱申请--订单状态： 任务完成
     */
    public static final Integer CHECK_STATUE_8 = 8;
    /**
     * 用箱申请--订单（预约）状态： 未预约提箱
     */
    public static final Integer ORDER_STATUE = 0;
    /**
     * 用箱申请--订单（预约）状态： 已预约提箱
     */
    public static final Integer ORDER_STATUE_1 = 1;
    /**
     * 用箱申请--订单（预约）状态： 未预约还箱
     */
    public static final Integer ORDER_STATUE_2 = 2;
    /**
     * 用箱申请--订单（预约）状态： 已预约还箱
     */
    public static final Integer ORDER_STATUE_3 = 3;
    /**
     * 用箱申请--订单（预约）状态： 任务完成
     */
    public static final Integer ORDER_STATUE_4 = 4;
    /**
     * 用箱申请--业务类型： 转调运
     */
    public static final String ORDER_TYPE = "2";
    /**
     * 集装箱状态： 可用
     */
    public static final String CONTAINER_STATUS = "0";

    /**
     * 业务类型： 去程
     */
    public static final String ROUTE_TYPE = "0";
    /**
     * 业务类型： 回程
     */
    public static final String ROUTE_TYPE_1 = "1";
    /**
     * 业务类型： 转调运
     */
    public static final String ROUTE_TYPE_2 = "2";


    //用箱申请----------------------------------END

    // 调运管理 ------------------------------------START
    /**
     * 调运管理 审批状态： 待审批
     */
    public static final String TRANSPORT_APPROVE_0 = "0";
    /**
     * 调运管理 审批状态：陆港国内业务通过
     */
    public static final String TRANSPORT_APPROVE_1 = "1";
    /**
     * 调运管理 审批状态：陆港国内业务驳回
     */
    public static final String TRANSPORT_APPROVE_2 = "2";

    /**
     * 调运管理 审批状态： 财务审批通过
     */
    public static final String TRANSPORT_APPROVE_3 = "3";
    /**
     * 调运管理 审批状态： 财务审批驳回
     */
    public static final String TRANSPORT_APPROVE_4 = "4";
    /**
     * 调运管理 审批状态： 副总通过
     */
    public static final String TRANSPORT_APPROVE_5 = "5";

    /**
     * 调运管理 审批状态： 副总驳回
     */
    public static final String TRANSPORT_APPROVE_6 = "6";
    /**
     * 调运管理 审批状态： 常务副总审批通过
     */
    public static final String TRANSPORT_APPROVE_7 = "7";
    /**
     * 调运管理 审批状态： 常务副总审批驳回
     */
    public static final String TRANSPORT_APPROVE_8 = "8";
    /**
     * 调运管理 审批状态： 总经理审批通过
     */
    public static final String TRANSPORT_APPROVE_9 = "9";
    /**
     * 调运管理 审批状态： 总经理审批驳回
     */
    public static final String TRANSPORT_APPROVE_10 = "10";

    /**
     * 调运管理 审批状态： 决策通过
     */
    public static final String TRANSPORT_APPROVE_11 = "11";
    /**
     * 调运管理 审批状态： 决策驳回
     */
    public static final String TRANSPORT_APPROVE_12 = "12";
    /**
     * 调运管理 审批状态： 待上会决策
     */
    public static final String TRANSPORT_APPROVE_13 = "13";
    /**
     * 调运管理 审批状态： 结束
     */
    public static final String TRANSPORT_APPROVE_OVER = "9999";
    /**
     * 调运管理 审批状态前端：通过
     */
    public static final String TRANSPORT_APPROVE_STATUS_0 = "0";
    /**
     * 调运管理 审批状态前端: 驳回
     */
    public static final String TRANSPORT_APPROVE_STATUS_1 = "1";
    /**
     * 调运管理 下发状态： 待下发
     */
    public static final String TRANSPORT_TRANSMIT_0 = "0";

    /**
     * 调运管理 下发状态： 已下发
     */
    public static final String TRANSPORT_TRANSMIT_1 = "1";

    /**
     * 调运管理 承接状态： 待承接
     */
    public static final String TRANSPORT_UNDERTAKE_0 = "0";

    /**
     * 调运管理 承接状态： 已承接
     */
    public static final String TRANSPORT_UNDERTAKE_1 = "1";

    /**
     * 调运管理 承接状态： 拒绝承接
     */
    public static final String TRANSPORT_UNDERTAKE_2 = "2";
    /**
     * 调运管理 预约状态： 待预约提箱
     */
    public static final String TRANSPORT_SUBSCRIBE_EXTRACT_0 = "0";
    /**
     * 调运管理 预约状态： 已预约提箱
     */
    public static final String TRANSPORT_SUBSCRIBE_EXTRACT_1 = "1";
    /**
     * 调运管理 预约状态：未预约还箱
     */
    public static final String TRANSPORT_SUBSCRIBE_EXTRACT_2 = "2";
    /**
     * 调运管理 预约状态：已预约还箱
     */
    public static final String TRANSPORT_SUBSCRIBE_EXTRACT_3 = "3";
    /**
     * 调运管理 预约状态：无预约
     */
    public static final String TRANSPORT_SUBSCRIBE_EXTRACT_4 = "4";


    /**
     * 调运管理  运输状态： 待运输
     */
    public static final String TRANSPORT_PLAN_STATUS_0="0";
    /**
     * 调运管理  运输状态： 运输中
     */
    public static final String TRANSPORT_PLAN_STATUS_1="1";
    /**
     * 调运管理  运输状态： 部分完成
     */
    public static final String TRANSPORT_PLAN_STATUS_2="2";
    /**
     * 调运管理  运输状态： 运输完成
     */
    public static final String TRANSPORT_PLAN_STATUS_3="3";


    // 调运管理 ------------------------------------END

    // 修箱管理 ------------------------------------START
    /**
     * 修箱管理 审批状态： 待审批
     */
    public static final String REPAIR_APPROVE_0 = "0";

    /**
     * 修箱管理 审批状态： 陆港内部审批通过
     */
    public static final String REPAIR_APPROVE_1 = "1";
    /**
     * 修箱管理 审批状态： 陆港内部审批驳回
     */
    public static final String REPAIR_APPROVE_2 = "2";


    /**
     * 修箱管理 审批状态： 财务审批通过
     */
    public static final String REPAIR_APPROVE_3 = "3";
    /**
     * 修箱管理 审批状态： 财务审批驳回
     */
    public static final String REPAIR_APPROVE_4 = "4";
    /**
     * 修箱管理 审批状态： 副总通过
     */
    public static final String REPAIR_APPROVE_5 = "5";

    /**
     * 修箱管理 审批状态： 副总驳回
     */
    public static final String REPAIR_APPROVE_6 = "6";
    /**
     * 修箱管理 审批状态： 常务副总审批通过
     */
    public static final String REPAIR_APPROVE_7= "7";
    /**
     * 修箱管理 审批状态： 常务副总审批驳回
     */
    public static final String REPAIR_APPROVE_8 = "8";
    /**
     * 修箱管理 审批状态： 总经理审批通过
     */
    public static final String REPAIR_APPROVE_9 = "9";
    /**
     * 修箱管理 审批状态： 总经理审批驳回
     */
    public static final String REPAIR_APPROVE_10 = "10";

    /**
     * 修箱管理 审批状态： 审批结束
     */
    public static final String REPAIR_APPROVE_11 = "11";

    /**
     * 修箱管理 审批状态： 决策驳回
     */
    public static final String REPAIR_APPROVE_12 = "12";
    /**
     * 修箱管理 审批状态： 决策通过
     */
    public static final String REPAIR_APPROVE_13 = "13";

    /**
     * 修箱管理 审批状态： 结束
     */
    public static final String REPAIR_APPROVE_14 = "9999";

    /**
     * 修箱管理 审批状态： 审核通过
     */
    public static final String REPAIR_APPROVE_STATUS_0 = "0";
    /**
     * 修箱管理 审批状态： 审核驳回
     */
    public static final String REPAIR_APPROVE_STATUS_1 = "1";

    /**
     * 修箱管理 审批状态： 待审核
     */
    public static final String REPAIR_APPROVE_STATUS_2 = "2";

    /**
     * 修箱管理 审批状态： 系统自动通过
     */
    public static final String REPAIR_APPROVE_STATUS_3= "3";


    // 修箱管理 ------------------------------------END





    //还箱单管理 -------------------------------------START
    /**
     * 还箱单列表 还箱状态： 0未还箱
     */
    public static final String RECEIVE_STATUS = "0";

    /**
     * 还箱单列表 还箱状态： 1部分还箱
     */
    public static final String RECEIVE_STATUS_1 = "1";

    /**
     * 还箱单列表 还箱状态： 2已还箱
     */
    public static final String RECEIVE_STATUS_2 = "2";

    /**
     * 还箱单 还箱状态： 1已还箱
     */
    public static final String RECEIVE_STATUS_3 = "1";

    /**
     * 还箱单 还箱状态： 4 过期作废
     */
    public static final String RECEIVE_STATUS_4 = "3";

    /**
     * 还箱单列表 审核状态： 待审核
     */
    public static final String EXAMINE_STATUS_0 = "0";

    /**
     * 还箱单列表 审核状态： 审核通过
     */
    public static final String EXAMINE_STATUS_1 = "1";


    /**
     * 还箱单列表 审核状态： 未变更
     */
    public static final String EXAMINE_STATUS_2 = "2";

    /**
     * 还箱单列表 审核状态： 审核驳回
     */
    public static final String EXAMINE_STATUS_3 = "3";

    /**
     * 还箱单列表 变更状态： 待审核
     */
    public static final String EXAMINE_CHANGE_STATUS_0 = "0";

    // 还箱单管理 ------------------------------------END

    //集装箱状态 -------------------------------------START
    /**
     * 集装箱状态 可用： 0可用
     */
    public static final String STATUS = "0";

    /**
     * 集装箱状态 在途： 1在途
     */
    public static final String STATUS_1 = "1";

    /**
     * 集装箱状态 锁定： 2锁定
     */
    public static final String STATUS_2 = "2";

    /**
     * 集装箱状态 维修： 3维修
     */
    public static final String STATUS_3 = "3";
    /**
     * 集装箱状态 灭失： 4灭失
     */
    public static final String STATUS_4 = "4";
    // 集装箱状态 ------------------------------------END

    //灭失状态 -------------------------------------START0
    /**
     * 灭失管理 灭失状态： 0待审核
     */
    public static final String FORSAKE_STATUS = "0";

    /**
     * 灭失管理 灭失状态： 1审核通过
     */
    public static final String FORSAKE_STATUS_1 = "1";

    /**
     * 灭失管理 灭失状态： 2审核驳回
     */
    public static final String FORSAKE_STATUS_2 = "2";
    // 灭失状态 ------------------------------------END

    //用箱类型企业 -------------------------------------START
    /**
     * 用箱类型企业 企业类型： 0客户
     */
    public static final String ENTERPRISE_TYPE_0 = "0";

    /**
     * 用箱类型企业 企业类型： 1承运商
     */
    public static final String ENTERPRISE_TYPE_1 = "1";


    // 用箱类型企业 ------------------------------------END

    //账单的确认状态 -------------------------------------START
    /**
     * 账单的确认状态： 0：待确认
     */
    public static final String CONFIRM_STATUS_0 = "0";

    /**
     * 账单的确认状态： 1：已确认
     */
    public static final String CONFIRM_STATUS_1 = "1";

    /**
     * 账单的确认状态： 2：驳回
     */
    public static final String CONFIRM_STATUS_2 = "2";

    /**
     * 账单的确认状态： 3：系统确认
     */
    public static final String CONFIRM_STATUS_3 = "3";

    /**
     * 账单的确认状态： 3：系统确认（修洗箱收、支）
     */
    public static final String CONFIRM_STATUS_4 = "4";


    // 账单的确认状态 ------------------------------------END

    //账单的开票状态 -------------------------------------START
    /**
     * 账单的开票状态： 0：未开票
     */
    public static final String INVOICING_STATUS_0 = "0";

    /**
     * 账单的开票状态： 1：开票中
     */
    public static final String INVOICING_STATUS_1 = "1";

    /**
     * 账单的开票状态： 2：已开票
     */
    public static final String INVOICING_STATUS_2 = "2";

    // 账单的开票状态 ------------------------------------END

    //账单的付款状态 -------------------------------------START
    /**
     * 账单的付款状态： 0：待支付
     */
    public static final String PAYMENT_STATUS_0 = "0";

    /**
     * 账单的付款状态： 1：已支付
     */
    public static final String PAYMENT_STATUS_1 = "1";

    // 账单的付款状态 ------------------------------------END

    //账单的回款状态 -------------------------------------START
    /**
     * 账单的回款状态： 0：待回款
     */
    public static final String RECEIVABLE_STATUS_0 = "0";

    /**
     * 账单的回款状态： 1：已回款
     */
    public static final String RECEIVABLE_STATUS_1 = "1";

    // 账单的回款状态 ------------------------------------END

    //账单状态 -------------------------------------START
    /**
     * 账单状态： 0：取消
     */
    public static final String ORDER_STATUS_0 = "0";

    /**
     * 账单状态： 1：正常
     */
    public static final String ORDER_STATUS_1 = "1";

    // 账单状态 ------------------------------------END


    // 用户注册 ------------------------------------START
    /**
     * 陆港账号
     */
    public static final String USER_COMPANY_TYPE_0 = "0";

    /**
     * 客户
     */
    public static final String USER_COMPANY_TYPE_1 = "1";

    /**
     * 堆场
     */
    public static final String USER_COMPANY_TYPE_2 = "2";

    /**
     * 承运商
     */
    public static final String USER_COMPANY_TYPE_3 = "3";

    /**
     * 代管公司
     */
    public static final String USER_COMPANY_TYPE_4 = "4";

    /**
     * 供应商 (此类型作为用箱企业类型分类使用) 2022-2-14 剑兰添加
     */
    public static final String USER_COMPANY_TYPE_5 = "5";

    // 权限----START
    /**
     * 管理员
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * 行政
     */
    public static final String ROLE_LG_ADMINISTRATION = "lg_administration";

    /**
     * 陆港总经理
     */
    public static final String ROLE_LG_MANAGER = "lg_manager";
    /**
     * 陆港常务副总
     */
    public static final String ROLE_LG_DEPUTY_XECUTIVE_DIRECTOR="lg_deputy_xecutive_director";
    /**
     * 陆港副总
     */
    public static final String ROLE_LG_VICE_MANAGER = "lg_vice_manager";

    /**
     * 陆港财务
     */
    public static final String ROLE_LG_FINANCE = "lg_finance";

    /**
     * 陆港国内业务
     */
    public static final String ROLE_LG_BUSINESS = "lg_business";

    /**
     * 代理公司管理员
     */
    public static final String ROLE_PROXY_ADMIN = "proxy_company_admin";

    /**
     * 代理公司普通账户
     */
    public static final String ROLE_PROXY_NORMAL = "proxy_company_normal";

    /**
     * 客户管理账户
     */
    public static final String ROLE_CUSTOM_ADMIN = "custom_admin";

    /**
     * 客户普通账户
     */
    public static final String ROLE_CUSTOM_NORMAL = "custom_normal";

    /**
     * 承运商管理账户
     */
    public static final String ROLE_TRANSPORT_ADMIN = "transport_admin";

    /**
     * 承运商普通账户
     */
    public static final String ROLE_TRANSPORT_NORMAL = "transport_normal";

    /**
     * 堆场普通账户
     */
    public static final String ROLE_FACTORY_NORMAL = "factory_normal";


    // 权限 ------------------------------------END

    // 用户注册 ------------------------------------END

    // 计算时间差-------------------------------------START


    // 计算时间差-------------------------------------END
    // 调运单审批-------------------------------------START
    /**
     * 调运审批通过
     */
    public static final String APPROVE_PASS = "0";
    /**
     * 调运审批驳回
     */
    public static final String APPROVE_REFUSE = "1";
    // 调运单审批-------------------------------------END
    //修箱管理方案审核---------------------------------START
    /**
     * 方案待审核
     */
    public static final String PROGRAMME_STATUS_0 = "0";
    /**
     * 方案陆港内部审核通过
     */
    public static final String PROGRAMME_STATUS_1 = "1";
    /**
     * 方案陆港内部审核驳回
     */
    public static final String PROGRAMME_STATUS_2 = "2";

    /**
     * 方案陆港财务审核通过
     */
    public static final String PROGRAMME_STATUS_3 = "3";
    /**
     * 方案陆港财务审核驳回
     */
    public static final String PROGRAMME_STATUS_4 = "4";

    /**
     * 方案陆港副总审核通过
     */
    public static final String PROGRAMME_STATUS_5= "5";
    /**
     * 方案陆港副总审核驳回
     */
    public static final String PROGRAMME_STATUS_6 = "6";

    /**
     * 方案陆港常务副总审核通过
     */
    public static final String PROGRAMME_STATUS_7 = "7";
    /**
     * 方案陆港常务副总审核驳回
     */
    public static final String PROGRAMME_STATUS_8 = "8";

    /**
     * 方案陆港总经理审核通过
     */
    public static final String PROGRAMME_STATUS_9 = "9";
    /**
     * 方案陆港总经理审核驳回
     */
    public static final String PROGRAMME_STATUS_10 = "10";

    /**
     * 方案审核结束
     */
    public static final String PROGRAMME_STATUS_11 = "11";

    /**
     * 方案决策驳回
     */
    public static final String PROGRAMME_STATUS_12= "12";

    /**
     * 方案决策通过
     */
    public static final String PROGRAMME_STATUS_13 = "13";

    /**
     * 集装箱维修管理通过
     */
    public static final String PROGRAMME_STATUS_15 = "15";

    /**
     * 集装箱维修管理驳回
     */
    public static final String PROGRAMME_STATUS_16 = "16";
    /**
     * 结果待审核
     */
    public static final String RESULT_STATUS_0 = "0";
    /**
     * 结果审核通过
     */
    public static final String RESULT_STATUS_1 = "1";
    /**
     * 结果审核驳回
     */
    public static final String RESULT_STATUS_2 = "2";
    //修箱管理方案审核---------------------------------END

    //修箱管理修箱状态---------------------------------START
    /**
     * 待修箱
     */
    public static final String REPAIR_STATUS_0 = "0";
    /**
     * 修箱中
     */
    public static final String REPAIR_STATUS_1 = "1";
    /**
     * 修箱完成
     */
    public static final String REPAIR_STATUS_2 = "2";
    //修箱管理修箱状态---------------------------------END

    //根据审批role修改 修箱记录信息状态---------------------------------START

    /**
     *  集装箱维修管理
     */
    public static final String LG_XIANGGUAN = "Lg-xiangguan";

    /**
     *  陆港内部审核通过
     */
    public static final String LG_ADMIN = "lg_business";


    /**
     *  陆港财务审核通过
     */
    public static final String LG_CAIWU = "lg_finance";

    /**
     *  陆港副总审核通过
     */
    public static final String LG_FUZONG = "lg_vice_manager";

    /**
     *  陆港常务副总审核通过
     */
    public static final String LG_CHANGWUFUZONG = "lg_deputy_xecutive_director";

    /**
     *  陆港总经理审核通过
     */
    public static final String LG_ZONGJINGLI = "lg_manager";

    //根据审批role修改 修箱记录信息状态---------------------------------END


    //修箱管理图片类型---------------------------------START
    /**
     * 箱损图片
     */
    public static final String PICTURE_TYPE_DAMAGED = "damaged";
    /**
     * 修复图片
     */
    public static final String PICTURE_TYPE_REPAIR = "repair";

    //修箱管理图片类型---------------------------------END


    // 修箱管理责任人---------------------------------START
    /**
     * 责任人为客户
     */
    public static final Integer RESPONSIBLE_ROLE_0 = 0;
    /**
     * 责任人为堆场
     */
    public static final Integer RESPONSIBLE_ROLE_1 = 1;
    /**
     * 责任人为承运商
     */
    public static final Integer RESPONSIBLE_ROLE_2 = 2;
    // 修箱管理责任人---------------------------------END



    // 统计类型---------------------------------START
    /**
     * 去程
     */
    public static final String ROUTETYPE_GO = "0";
    /**
     * 回程
     */
    public static final String ROUTETYPE_BACK = "1";
    // 统计类型---------------------------------START




    // 去回程明细---------------------------------START
    /**
     * 去回程统计明细：日报
     */
    public static final String STATISTICS_GOBACK_TYPE_DAY = "0";

    /**
     * 去回程统计明细：月报
     */
    public static final String STATISTICS_GOBACK_TYPE_MONTH = "1";
    /**
     * 去回程统计明细：季报
     */
    public static final String STATISTICS_GOBACK_TYPE_SEASON = "2";
    /**
     * 去回程统计明细：年报
     */
    public static final String STATISTICS_GOBACK_TYPE_YEAR = "3";

    // 去回程明细---------------------------------END

    // 集装箱属性---------------------------------START
    /**
     * 集装箱属性 0:SOC（租赁箱）
     */
    public static final String CONTAINER_ATTRIBUTE_TYPE_0 = "0";

    /**
     * 集装箱属性 1:自有箱    在长租箱账单中 1代表 长租+短租
     */
    public static final String CONTAINER_ATTRIBUTE_TYPE_1 = "1";
    /**
     * 集装箱属性 2:长租箱
     */
    public static final String CONTAINER_ATTRIBUTE_TYPE_2 = "2";
    /**
     * 集装箱属性 3:短租箱
     */
    public static final String CONTAINER_ATTRIBUTE_TYPE_3 = "3";

    // 集装箱属性---------------------------------END

    // 运输方式---------------------------------START
    /**
     * 运输方式 0:铁路
     */
    public static final String TRANSPORT_TYPE_0 = "0";

    /**
     * 运输方式 1:公路
     */
    public static final String TRANSPORT_TYPE_1 = "1";
    /**
     * 运输方式 2:海运
     */
    public static final String TRANSPORT_TYPE_2 = "2";
    // 运输方式---------------------------------END
    //成本账单账单类型---------------------------START
    /**
     * 账单类型 0 堆存费
     */
    public static final String COST_ORDER_TYPE_0 = "0";
    /**
     * 账单类型 1 修洗箱费（支）
     */
    public static final String COST_ORDER_TYPE_1 = "1";
    /**
     * 账单类型 2 二次搬移费（支）
     */
    public static final String COST_ORDER_TYPE_2 = "2";
    /**
     * 账单类型 3 上车费
     */
    public static final String COST_ORDER_TYPE_3 = "3";
    /**
     * 账单类型 4 下车费
     */
    public static final String COST_ORDER_TYPE_4 = "4";
    /**
     * 账单类型  5箱使费（转调运）
     */
    public static final String COST_ORDER_TYPE_5 = "5";
    /**
     * 账单类型 6 调运费
     */
    public static final String COST_ORDER_TYPE_6 = "6";
    /**
     * 账单类型 7租赁费（长/短租）
     */
    public static final String COST_ORDER_TYPE_7 = "7";
    //成本账单账单类型---------------------------END

    //调运,修箱是否自动审批-----------------------START
    /**
     *  调运,修箱是否自动审批:0,自动审批
     */
    public static final String AUTOMATIC_0 = "0";
    /**
     * 调运,修箱是否自动审批:1,非自动审批
     */
    public static final String AUTOMATIC_1 = "1";
    //调运,修箱是否自动审批-------------------------END

    //修箱账单（收）减免凭证------------------------START
    /**
     *
     */
    public static final String REPAIRWASH_EXIDENCE = "evidence";
    //修箱账单（收）减免凭证-------------------------END

    //账单类型 是否coc------------------------START
    /**
     * 账单类型 是否coc 1是
     */
    public static final String FINANCE_ISCOC_1 = "1";
    //账单类型 是否coc-------------------------END


    //账单统计类型-----------------------START
    /**
     *  账单统计类型 0:箱使费
     */
    public static final String FINANCE_TYPE_0 = "0";
    /**
     *  账单统计类型 1:堆存费
     */
    public static final String FINANCE_TYPE_1 = "1";
    /**
     *  账单统计类型 2:修洗箱账单(收)
     */
    public static final String FINANCE_TYPE_2 = "2";
    /**
     *  账单统计类型 3:修洗箱账单(支)
     */
    public static final String FINANCE_TYPE_3 = "3";
    /**
     *  账单统计类型 4:还箱超期账单
     */
    public static final String FINANCE_TYPE_4 = "4";
    /**
     *  账单统计类型 5:租赁箱成本账单
     */
    public static final String FINANCE_TYPE_5 = "5";
    /**
     *  账单统计类型 6:取消订单账单
     */
    public static final String FINANCE_TYPE_6 = "6";
    /**
     *  账单统计类型 7:占用费账单
     */
    public static final String FINANCE_TYPE_7 = "7";
    /**
     *  账单统计类型 8:上车费账单
     */
    public static final String FINANCE_TYPE_8 = "8";
    /**
     *  账单统计类型 9:下车费账单
     */
    public static final String FINANCE_TYPE_9 = "9";
    /**
     *  账单统计类型 10:灭失账单
     */
    public static final String FINANCE_TYPE_10 = "10";
    /**
     *  账单统计类型 11:调运账单
     */
    public static final String FINANCE_TYPE_11 = "11";

    /**
     *  账单统计类型 12:箱使费(转调运)
     */
    public static final String FINANCE_TYPE_12 = "12";
    //账单统计类型-------------------------END

    //账单收支类型-----------------------START
    /**
     *  账单收支类型 0:收
     */
    public static final String FINANCE_PAYMENT_TYPE_0 = "0";
    /**
     *  账单收支类型 1:支
     */
    public static final String FINANCE_PAYMENT_TYPE_1 = "1";
    //账单收支类型-------------------------END



    /**
     *  模板消息:用箱申请
     */
    public static final String TEMPLATE_INFO_13 = "13";
    /**
     *  模板消息:提箱申请
     */
    public static final String TEMPLATE_INFO_14 = "14";
    /**
     *  模板消息:用箱申请审核通过
     */
    public static final String TEMPLATE_INFO_16 = "16";
    /**
     *  模板消息:预约提箱
     */
    public static final String TEMPLATE_INFO_17 = "17";
    /**
     *  模板消息:用箱申请审核驳回
     */
    public static final String TEMPLATE_INFO_18 = "18";
    /**
     *  模板消息:上传箱号成功
     */
    public static final String TEMPLATE_INFO_19 = "19";

    /**
     *  1:消息
     */
    public static final String CATEGORY_1 = "1";
    /**
     *  2:系统消息
     */
    public static final String CATEGORY_2 = "2";

}
