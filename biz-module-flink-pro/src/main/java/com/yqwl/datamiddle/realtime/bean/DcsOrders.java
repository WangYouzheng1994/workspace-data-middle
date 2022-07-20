package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class DcsOrders implements Serializable  {
    /**pk*/

    private Integer pk;
    /**vinNo*/

    private String vinNo;
    /**idnum*/

    private Integer idnum;
    /**yNo*/

    private String yNo;
    /**statementsNo*/

    private String statementsNo;
    /**taskNo*/

    private String taskNo;
    /**trafficType*/

    private String trafficType;
    /**dealersCode*/

    private String dealersCode;
    /**dealersName*/

    private String dealersName;
    /**dealersAddress*/

    private String dealersAddress;
    /**shippersCode*/

    private String shippersCode;
    /**shippersName*/

    private String shippersName;
    /**startSiteName*/

    private String startSiteName;
    /**startAddress*/

    private String startAddress;
    /**startCityName*/

    private String startCityName;
    /**startLatitude*/

    private String startLatitude;
    /**startLongitude*/

    private String startLongitude;
    /**endSiteName*/

    private String endSiteName;
    /**endAddress*/

    private String endAddress;
    /**endCityName*/

    private String endCityName;
    /**endLatitude*/

    private String endLatitude;
    /**endLongitude*/

    private String endLongitude;
    /**driverId*/

    private Integer driverId;
    /**diverName*/

    private String diverName;
    /**driverPhone*/

    private String driverPhone;
    /**vehicleId*/

    private String vehicleId;
    /**vehiclePlate*/

    private String vehiclePlate;
    /**loadingDriverId*/

    private Integer loadingDriverId;
    /**loadingDiverName*/

    private String loadingDiverName;
    /**loadingDriverPhone*/

    private String loadingDriverPhone;
    /**loadingDate*/

    private Long loadingDate;
    /**loadingAddress*/

    private String loadingAddress;
    /**loadingCityName*/

    private String loadingCityName;
    /**loadingLatitude*/

    private String loadingLatitude;
    /**loadingLongitude*/

    private String loadingLongitude;
    /**estimatedDriverId*/

    private Integer estimatedDriverId;
    /**estimatedDiverName*/

    private String estimatedDiverName;
    /**estimatedDriverPhone*/

    private String estimatedDriverPhone;
    /**estimatedDate*/

    private Long estimatedDate;
    /**estimatedAddress*/

    private String estimatedAddress;
    /**estimatedCityName*/

    private String estimatedCityName;
    /**estimatedLatitude*/

    private String estimatedLatitude;
    /**estimatedLongitude*/

    private String estimatedLongitude;
    /**arriveDriverId*/

    private Integer arriveDriverId;
    /**arriveDiverName*/

    private String arriveDiverName;
    /**arriveDriverPhone*/

    private String arriveDriverPhone;
    /**arriveDate*/

    private Long arriveDate;
    /**arriveAddress*/

    private String arriveAddress;
    /**arriveCityName*/

    private String arriveCityName;
    /**arriveLatitude*/

    private String arriveLatitude;
    /**arriveLongitude*/

    private String arriveLongitude;
    /**deliveryDriverId*/

    private Integer deliveryDriverId;
    /**deliveryDiverName*/

    private String deliveryDiverName;
    /**deliveryDriverPhone*/

    private String deliveryDriverPhone;
    /**deliveryDate*/

    private Long deliveryDate;
    /**deliveryAddress*/

    private String deliveryAddress;
    /**deliveryCityName*/

    private String deliveryCityName;
    /**deliveryLatitude*/

    private String deliveryLatitude;
    /**deliveryLongitude*/

    private String deliveryLongitude;
    /**unloadingDriverId*/

    private Integer unloadingDriverId;
    /**unloadingDiverName*/

    private String unloadingDiverName;
    /**unloadingDriverPhone*/

    private String unloadingDriverPhone;
    /**unloadingDeliveryDate*/

    private Long unloadingDeliveryDate;
    /**unloadingAddress*/

    private String unloadingAddress;
    /**unloadingCityName*/

    private String unloadingCityName;
    /**unloadingLatitude*/

    private String unloadingLatitude;
    /**unloadingLongitude*/

    private String unloadingLongitude;
    /**reverseSyncFlag*/

    private String reverseSyncFlag;
    /**expectedDate*/

    private Long expectedDate;
    /**deviationFlag*/

    private String deviationFlag;
    /**arriveCauses*/

    private String arriveCauses;
    /**arriveDesc*/

    private String arriveDesc;
    /**deliveryCauses*/

    private String deliveryCauses;
    /**deliveryDesc*/

    private String deliveryDesc;
    /**status*/

    private String status;
    /**domainName*/

    private String domainName;
    /**insertUser*/

    private String insertUser;
    /**insertUserName*/

    private String insertUserName;
    /**insertDate*/

    private Long insertDate;
    /**updateUser*/

    private String updateUser;
    /**updateUserName*/

    private String updateUserName;
    /**updateDate*/

    private Long updateDate;
    /**deleteSource*/

    private String deleteSource;
    /**effectFlag*/

    private String effectFlag;
    /**ddjrq*/

    private Long ddjrq;
    /**vwxdwdm*/

    private String vwxdwdm;
    /**cqwh*/

    private String cqwh;
    /**czjgsdm*/

    private String czjgsdm;
    /**startSiteCode*/

    private String startSiteCode;
    /**vph*/

    private String vph;
    /**dphscsj*/

    private Long dphscsj;
    /**deliveryFlag*/

    private String deliveryFlag;
    /**arriveGoodsDriverId*/

    private Integer arriveGoodsDriverId;
    /**arriveGoodsDiverName*/

    private String arriveGoodsDiverName;
    /**arriveGoodsDriverPhone*/

    private String arriveGoodsDriverPhone;
    /**arriveGoodsDate*/

    private Long arriveGoodsDate;
    /**arriveGoodsAddress*/

    private String arriveGoodsAddress;
    /**arriveGoodsCityName*/

    private String arriveGoodsCityName;
    /**arriveGoodsLatitude*/

    private String arriveGoodsLatitude;
    /**arriveGoodsLongitude*/

    private String arriveGoodsLongitude;
    /**ccpdm*/

    private String ccpdm;
    /**startProviceCode*/

    private String startProviceCode;
    /**startCityCode*/

    private String startCityCode;
    /**endProviceCode*/

    private String endProviceCode;
    /**endCityCode*/

    private String endCityCode;
    /**shipmentAbnormalFlag*/

    private String shipmentAbnormalFlag;
    /**shipmentAbnormalReason*/

    private String shipmentAbnormalReason;
    /**shipmentDriverId*/

    private Integer shipmentDriverId;
    /**shipmentDiverName*/

    private String shipmentDiverName;
    /**shipmentDriverPhone*/

    private String shipmentDriverPhone;
    /**shipmentLatitude*/

    private String shipmentLatitude;
    /**shipmentLongitude*/

    private String shipmentLongitude;
    /**shipmentProvinces*/

    private String shipmentProvinces;
    /**shipmentCityName*/

    private String shipmentCityName;
    /**shipmentDate*/

    private Long shipmentDate;
    /**cqrr*/

    private String cqrr;
    /**dckrq*/

    private Long dckrq;
    /**starrEndDis*/

    private Integer starrEndDis;
    /**njcfdxh*/

    private Integer njcfdxh;
    /**vfczt*/

    private String vfczt;
    /**vsczt*/

    private String vsczt;
    /**transportTime*/

    private Long transportTime;
    /**cdhddm*/

    private String cdhddm;
    /**nowShippersCode*/

    private String nowShippersCode;
    /**nowShippersName*/

    private String nowShippersName;
    /**nowDate*/

    private Long nowDate;
    /**nowAddress*/

    private String nowAddress;
    /**nowCityName*/

    private String nowCityName;
    /**nowLatitude*/

    private String nowLatitude;
    /**nowLongitude*/

    private String nowLongitude;
    /**loadingShippersCode*/

    private String loadingShippersCode;
    /**loadingShippersName*/

    private String loadingShippersName;
    /**shipmentShippersCode*/

    private String shipmentShippersCode;
    /**shipmentShippersName*/

    private String shipmentShippersName;
    /**zeroAmReportNum*/

    private Integer zeroAmReportNum;
    /**oneAmReportNum*/

    private Integer oneAmReportNum;
    /**twoAmEportNum*/

    private Integer twoAmEportNum;
    /**threeAmReportNum*/

    private Integer threeAmReportNum;
    /**fourAmReportNum*/

    private Integer fourAmReportNum;
    /**fiveAmReportNum*/

    private Integer fiveAmReportNum;
    /**sixAmReportNum*/

    private Integer sixAmReportNum;
    /**sevenAmReportNum*/

    private Integer sevenAmReportNum;
    /**eightAmReportNum*/

    private Integer eightAmReportNum;
    /**nineAmReportNum*/

    private Integer nineAmReportNum;
    /**tenAmReportNum*/

    private Integer tenAmReportNum;
    /**elevenAmReportNum*/

    private Integer elevenAmReportNum;
    /**twelveAmReportNum*/

    private Integer twelveAmReportNum;
    /**onePmReportNum*/

    private Integer onePmReportNum;
    /**twoPmEportNum*/

    private Integer twoPmEportNum;
    /**threePmReportNum*/

    private Integer threePmReportNum;
    /**fourPmReportNum*/

    private Integer fourPmReportNum;
    /**fivePmReportNum*/

    private Integer fivePmReportNum;
    /**sixPmReportNum*/

    private Integer sixPmReportNum;
    /**sevenPmReportNum*/

    private Integer sevenPmReportNum;
    /**eightPmReportNum*/

    private Integer eightPmReportNum;
    /**ninePmReportNum*/

    private Integer ninePmReportNum;
    /**tenPmReportNum*/

    private Integer tenPmReportNum;
    /**elevenPmReportNum*/

    private Integer elevenPmReportNum;
    /**reportNum*/

    private Integer reportNum;
    /**badNum*/

    private Integer badNum;
    /**abnormalNum*/

    private Integer abnormalNum;
    /**deliveryShippersCode*/

    private String deliveryShippersCode;
    /**deliveryShippersName*/

    private String deliveryShippersName;
    /**unloadingShippersCode*/

    private String unloadingShippersCode;
    /**unloadingShippersName*/

    private String unloadingShippersName;
    /**sameChangeNum*/

    private Integer sameChangeNum;
    /**diffierentChangeNum*/

    private Integer diffierentChangeNum;
    /**changeNum*/

    private Integer changeNum;
    /**estimatedShippersCode*/

    private String estimatedShippersCode;
    /**estimatedShippersName*/

    private String estimatedShippersName;
    /**arriveShippersCode*/

    private String arriveShippersCode;
    /**arriveShippersName*/

    private String arriveShippersName;
    /**arriveGoodsShippersCode*/

    private String arriveGoodsShippersCode;
    /**arriveGoodsShippersName*/

    private String arriveGoodsShippersName;
    /**ddhsj*/

    private Long ddhsj;
    /**userArriveGoodsDate*/

    private Long userArriveGoodsDate;
    /**userArriveGoodsId*/

    private String userArriveGoodsId;
    /**userArriveGoodsName*/

    private String userArriveGoodsName;
    /**userArriveGoodsOperateDate*/

    private Long userArriveGoodsOperateDate;
    /**sptb02Czt*/

    private String sptb02Czt;
    /**isAllVisual*/

    private String isAllVisual;
    /**cpcdbh*/

    private String cpcdbh;
    /**sptb02Diss*/

    private Integer sptb02Diss;
    /**nbzwlsjDz*/

    private Integer nbzwlsjDz;
    /**sptb02d1Nsl*/

    private Integer sptb02d1Nsl;
    /**vehicleModels*/

    private String vehicleModels;
    /**vehicleBrand*/

    private String vehicleBrand;
    /**sptb01cDdjrq*/

    private Long sptb01cDdjrq;
    /**shippersJc*/

    private String shippersJc;
    /**endRegionName*/

    private String endRegionName;
    /**lineName*/

    private String lineName;
    /**dueShippersCode*/

    private String dueShippersCode;
    /**dueShippersName*/

    private String dueShippersName;
    /**dueShippersJc*/

    private String dueShippersJc;
    /**rfidDckrq*/

    private Long rfidDckrq;
    /**vehicleNcyde*/

    private Integer vehicleNcyde;
    /**auditDdhsj*/

    private Long auditDdhsj;
    /**fawAuditDdhsj*/

    private Long fawAuditDdhsj;
    /**dcsType*/

    private String dcsType;
    /**warehouseCreatetime*/

    private Long warehouseCreatetime;
    /**warehouseUpdatetime*/

    private Long warehouseUpdatetime;

}
