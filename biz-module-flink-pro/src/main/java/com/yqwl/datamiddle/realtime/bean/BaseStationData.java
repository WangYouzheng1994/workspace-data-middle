package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.sql.Timestamp;

@Data
public class BaseStationData {

    private String msgId;

    private Long sampleUTC;

    private Integer sampleStatus;

    private String shopNo;

    private String batchCode;

    private String vin;

    private String stationCode;

    private String stationType;

    private String driverId;

    private Integer isCorrect;

    private String operatorId;

    private String operateType;

    private Integer fileStatus;

    private Long createTimestamp;

    private Long lastUpdateDate;

    private String fileUrl;

    private String driverName;

    private String shipper;

    private Integer fileType;

    private String photoUrl;

    private String longitude;

    private String latitude;

    private String effectFlag;

    private String motorcycletypeCode;

    private String motorcycletypeName;

    private String brandCode;

    private String brandName;

    private String locationcode;

    private String pushBatch;

    private Timestamp createTime;

    private Timestamp updateTime;

}
