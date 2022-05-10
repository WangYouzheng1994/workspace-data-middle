package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

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
public class BaseStationData implements Serializable {

    private static final long serialVersionUID = 1L;


      private Long idnum;

      /**
     * 消息主键Id  原字符长度2000，需要看实际数据
     */
        
    private String msgId;

      /**
     * 采样完成时间  date 统一时间戳 bigint
     */
        
    private Long sampleUTC;

      /**
     * 采样状态 0-完成，1-超时(不完整)  number 转 int
     */
        
    private Integer sampleStatus;

      /**
     * 节点编号
     */
        
    private String shopNo;

      /**
     * 批次号
     */
        
    private String batchCode;

      /**
     * 车架号（员工停车场：车牌号
     */
        
    private String vin;

      /**
     * 站点代码
     */
        
    private String stationCode;

      /**
     * 终端类型：固定-固定   TypeA 移动-固定   TypeB 移动-移动   TypeC 移动-移动（无驳运员） TypeD
     */
        
    private String stationType;

      /**
     * 驳运员ID（员工停车场，员工号，来自车牌号）
     */
        
    private String driverId;

      /**
     * 是否识别正确，默认1代表正确，0代表错误  number 转 int
     */
        
    private Integer isCorrect;

      /**
     * 操作员工号（固定设备时也有）
     */
        
    private String operatorId;

      /**
     * 操作类型入库  InStock  出库  OutStock
     */
        
    private String operateType;

      /**
     * 视频文件上传状态(-2无,-1未上传,0已上传 )  number 转 int 
     */
        
    private Integer fileStatus;

      /**
     * 入表时间          date 统一时间戳 bigint
     */
        
    private Long createTimestamp;

      /**
     * 数据更新时间    date 统一时间戳 bigint
     */
        
    private Long lastUpdateDate;

      /**
     * 视频播放地址   varchar2-2000   转 text
     */
        
    private String fileUrl;

      /**
     * 司机姓名         varchar2-2000   转 text
     */
        
    private String driverName;

      /**
     * 运输商名称
     */
        
    private String shipper;

      /**
     * 文件类型 0-视频，10-图片  number 转 int
     */
        
    private Integer fileType;

      /**
     * 图片地址
     */
        
    private String photoUrl;

      
    private String longitude;

      
    private String latitude;

      /**
     * 有效标识  0 无效  1 有效
     */
        
    private String effectFlag;

      /**
     * 车型代码
     */
        
    private String motorcycletypeCode;

      /**
     * 车型名称
     */
        
    private String motorcycletypeName;

      /**
     * 品牌代码
     */
        
    private String brandCode;

      /**
     * 品牌名称
     */
        
    private String brandName;

      /**
     * 库位编码
     */
        
    private String locationcode;

      /**
     * 推送数据批次
     */
        
    private String pushBatch;

      /**
     * 创建时间
     */
        
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
        
    private Long warehouseUpdatetime;


}
