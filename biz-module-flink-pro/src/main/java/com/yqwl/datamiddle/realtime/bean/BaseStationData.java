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
 * 出入库记录表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_base_station_data")
public class BaseStationData implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 消息主键Id  原字符长度2000，需要看实际数据
     */
      @TableField("MSG_ID")
    private String msgId;

      /**
     * 采样完成时间  date 统一时间戳 bigint
     */
      @TableField("SAMPLE_U_T_C")
    private Long sampleUTC;

      /**
     * 采样状态 0-完成，1-超时(不完整)  number 转 int
     */
      @TableField("SAMPLE_STATUS")
    private Integer sampleStatus;

      /**
     * 节点编号
     */
      @TableField("SHOP_NO")
    private String shopNo;

      /**
     * 批次号
     */
      @TableField("BATCH_CODE")
    private String batchCode;

      /**
     * 车架号（员工停车场：车牌号
     */
      @TableField("VIN")
    private String vin;

      /**
     * 站点代码
     */
      @TableField("STATION_CODE")
    private String stationCode;

      /**
     * 终端类型：固定-固定   TypeA 移动-固定   TypeB 移动-移动   TypeC 移动-移动（无驳运员） TypeD
     */
      @TableField("STATION_TYPE")
    private String stationType;

      /**
     * 驳运员ID（员工停车场，员工号，来自车牌号）
     */
      @TableField("DRIVER_ID")
    private String driverId;

      /**
     * 是否识别正确，默认1代表正确，0代表错误  number 转 int
     */
      @TableField("IS_CORRECT")
    private Integer isCorrect;

      /**
     * 操作员工号（固定设备时也有）
     */
      @TableField("OPERATOR_ID")
    private String operatorId;

      /**
     * 操作类型入库  InStock  出库  OutStock
     */
      @TableField("OPERATE_TYPE")
    private String operateType;

      /**
     * 视频文件上传状态(-2无,-1未上传,0已上传 )  number 转 int 
     */
      @TableField("FILE_STATUS")
    private Integer fileStatus;

      /**
     * 入表时间          date 统一时间戳 bigint
     */
      @TableField("CREATE_TIMESTAMP")
    private Long createTimestamp;

      /**
     * 数据更新时间    date 统一时间戳 bigint
     */
      @TableField("LAST_UPDATE_DATE")
    private Long lastUpdateDate;

      /**
     * 视频播放地址   varchar2-2000   转 text
     */
      @TableField("FILE_URL")
    private String fileUrl;

      /**
     * 司机姓名         varchar2-2000   转 text
     */
      @TableField("DRIVER_NAME")
    private String driverName;

      /**
     * 运输商名称
     */
      @TableField("SHIPPER")
    private String shipper;

      /**
     * 文件类型 0-视频，10-图片  number 转 int
     */
      @TableField("FILE_TYPE")
    private Integer fileType;

      /**
     * 图片地址
     */
      @TableField("PHOTO_URL")
    private String photoUrl;

    @TableField("LONGITUDE")
    private String longitude;

    @TableField("LATITUDE")
    private String latitude;

      /**
     * 有效标识  0 无效  1 有效
     */
      @TableField("EFFECT_FLAG")
    private String effectFlag;

      /**
     * 车型代码
     */
      @TableField("MOTORCYCLETYPE_CODE")
    private String motorcycletypeCode;

      /**
     * 车型名称
     */
      @TableField("MOTORCYCLETYPE_NAME")
    private String motorcycletypeName;

      /**
     * 品牌代码
     */
      @TableField("BRAND_CODE")
    private String brandCode;

      /**
     * 品牌名称
     */
      @TableField("BRAND_NAME")
    private String brandName;

      /**
     * 库位编码
     */
      @TableField("LOCATIONCODE")
    private String locationcode;

      /**
     * 推送数据批次
     */
      @TableField("PUSH_BATCH")
    private String pushBatch;

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
