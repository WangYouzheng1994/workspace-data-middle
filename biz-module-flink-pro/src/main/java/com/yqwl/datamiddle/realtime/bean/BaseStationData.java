package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出入库记录表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@TableName("ods_vlms_base_station_data")
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseStationData {

    //private static final long serialVersionUID = 1L;


    /**
     * 消息主键Id  原字符长度2000，需要看实际数据
     */

    private String MSG_ID;

    /**
     * 采样完成时间  date 统一时间戳 bigint
     */

    private Long SAMPLE_U_T_C;

    /**
     * 采样状态 0-完成，1-超时(不完整)  number 转 int
     */

    private Integer SAMPLE_STATUS;

    /**
     * 节点编号
     */

    private String SHOP_NO;

    /**
     * 批次号
     */

    private String BATCH_CODE;

    /**
     * 车架号（员工停车场：车牌号
     */

    private String VIN;

    /**
     * 站点代码
     */

    private String STATION_CODE;

    /**
     * 终端类型：固定-固定   TypeA 移动-固定   TypeB 移动-移动   TypeC 移动-移动（无驳运员） TypeD
     */

    private String STATION_TYPE;

    /**
     * 驳运员ID（员工停车场，员工号，来自车牌号）
     */

    private String DRIVER_ID;

    /**
     * 是否识别正确，默认1代表正确，0代表错误  number 转 int
     */

    private Integer IS_CORRECT;

    /**
     * 操作员工号（固定设备时也有）
     */

    private String OPERATOR_ID;

    /**
     * 操作类型入库  InStock  出库  OutStock
     */

    private String OPERATE_TYPE;

    /**
     * 视频文件上传状态(-2无,-1未上传,0已上传 )  number 转 int
     */

    private Integer FILE_STATUS;

    /**
     * 入表时间          date 统一时间戳 bigint
     */

    private Long CREATE_TIMESTAMP;

    /**
     * 数据更新时间    date 统一时间戳 bigint
     */

    private Long LAST_UPDATE_DATE;

    /**
     * 视频播放地址   varchar2-2000   转 text
     */

    private String FILE_URL;

    /**
     * 司机姓名         varchar2-2000   转 text
     */

    private String DRIVER_NAME;

    /**
     * 运输商名称
     */

    private String SHIPPER;

    /**
     * 文件类型 0-视频，10-图片  number 转 int
     */

    private Integer FILE_TYPE;

    /**
     * 图片地址
     */

    private String PHOTO_URL;


    private String LONGITUDE;


    private String LATITUDE;

    /**
     * 有效标识  0 无效  1 有效
     */

    private String EFFECT_FLAG;

    /**
     * 车型代码
     */

    private String MOTORCYCLETYPE_CODE;

    /**
     * 车型名称
     */

    private String MOTORCYCLETYPE_NAME;

    /**
     * 品牌代码
     */

    private String BRAND_CODE;

    /**
     * 品牌名称
     */

    private String BRAND_NAME;

    /**
     * 库位编码
     */

    private String LOCATIONCODE;

    /**
     * 推送数据批次
     */

    private String PUSH_BATCH;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;

    /**
     * 仓库对应类型
     */

    private String WAREHOUSE_TYPE;

    /**
     * 物理仓库代码
     */

    private String PHYSICAL_CODE;

}
