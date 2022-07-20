package com.yqwl.datamiddle.realtime.bean;


import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("ods_vlms_dcs_orders")
@EqualsAndHashCode(callSuper = false)
public class DcsOrders implements Serializable  {
    /**pk*/

    private Integer PK;
    /**vinNo*/

    private String VIN_NO;
    /**idnum*/

    private Integer IDNUM;
    /**yNo*/

    private String Y_NO;
    /**statementsNo*/

    private String STATEMENTS_NO;
    /**taskNo*/

    private String TASK_NO;
    /**trafficType*/

    private String TRAFFIC_TYPE;
    /**dealersCode*/

    private String DEALERS_CODE;
    /**dealersName*/

    private String DEALERS_NAME;
    /**dealersAddress*/

    private String DEALERS_ADDRESS;
    /**shippersCode*/

    private String SHIPPERS_CODE;
    /**shippersName*/

    private String SHIPPERS_NAME;
    /**startSiteName*/

    private String START_SITE_NAME;
    /**startAddress*/

    private String START_ADDRESS;
    /**startCityName*/

    private String START_CITY_NAME;
    /**startLatitude*/

    private String START_LATITUDE;
    /**startLongitude*/

    private String START_LONGITUDE;
    /**endSiteName*/

    private String END_SITE_NAME;
    /**endAddress*/

    private String END_ADDRESS;
    /**endCityName*/

    private String END_CITY_NAME;
    /**endLatitude*/

    private String END_LATITUDE;
    /**endLongitude*/

    private String END_LONGITUDE;
    /**driverId*/

    private Integer DRIVER_ID;
    /**diverName*/

    private String DIVER_NAME;
    /**driverPhone*/

    private String DRIVER_PHONE;
    /**vehicleId*/

    private String VEHICLE_ID;
    /**vehiclePlate*/

    private String VEHICLE_PLATE;
    /**loadingDriverId*/

    private Integer LOADING_DRIVER_ID;
    /**loadingDiverName*/

    private String LOADING_DIVER_NAME;
    /**loadingDriverPhone*/

    private String LOADING_DRIVER_PHONE;
    /**loadingDate*/

    private Long LOADING_DATE;
    /**loadingAddress*/

    private String LOADING_ADDRESS;
    /**loadingCityName*/

    private String LOADING_CITY_NAME;
    /**loadingLatitude*/

    private String LOADING_LATITUDE;
    /**loadingLongitude*/

    private String LOADING_LONGITUDE;
    /**estimatedDriverId*/

    private Integer ESTIMATED_DRIVER_ID;
    /**estimatedDiverName*/

    private String ESTIMATED_DIVER_NAME;
    /**estimatedDriverPhone*/

    private String ESTIMATED_DRIVER_PHONE;
    /**estimatedDate*/

    private Long ESTIMATED_DATE;
    /**estimatedAddress*/

    private String ESTIMATED_ADDRESS;
    /**estimatedCityName*/

    private String ESTIMATED_CITY_NAME;
    /**estimatedLatitude*/

    private String ESTIMATED_LATITUDE;
    /**estimatedLongitude*/

    private String ESTIMATED_LONGITUDE;
    /**arriveDriverId*/

    private Integer ARRIVE_DRIVER_ID;
    /**arriveDiverName*/

    private String ARRIVE_DIVER_NAME;
    /**arriveDriverPhone*/

    private String ARRIVE_DRIVER_PHONE;
    /**arriveDate*/

    private Long ARRIVE_DATE;
    /**arriveAddress*/

    private String ARRIVE_ADDRESS;
    /**arriveCityName*/

    private String ARRIVE_CITY_NAME;
    /**arriveLatitude*/

    private String ARRIVE_LATITUDE;
    /**arriveLongitude*/

    private String ARRIVE_LONGITUDE;
    /**deliveryDriverId*/

    private Integer DELIVERY_DRIVER_ID;
    /**deliveryDiverName*/

    private String DELIVERY_DIVER_NAME;
    /**deliveryDriverPhone*/

    private String DELIVERY_DRIVER_PHONE;
    /**deliveryDate*/

    private Long DELIVERY_DATE;
    /**deliveryAddress*/

    private String DELIVERY_ADDRESS;
    /**deliveryCityName*/

    private String DELIVERY_CITY_NAME;
    /**deliveryLatitude*/

    private String DELIVERY_LATITUDE;
    /**deliveryLongitude*/

    private String DELIVERY_LONGITUDE;
    /**unloadingDriverId*/

    private Integer UNLOADING_DRIVER_ID;
    /**unloadingDiverName*/

    private String UNLOADING_DIVER_NAME;
    /**unloadingDriverPhone*/

    private String UNLOADING_DRIVER_PHONE;
    /**unloadingDeliveryDate*/

    private Long UNLOADING_DELIVERY_DATE;
    /**unloadingAddress*/

    private String UNLOADING_ADDRESS;
    /**unloadingCityName*/

    private String UNLOADING_CITY_NAME;
    /**unloadingLatitude*/

    private String UNLOADING_LATITUDE;
    /**unloadingLongitude*/

    private String UNLOADING_LONGITUDE;
    /**reverseSyncFlag*/

    private String REVERSE_SYNC_FLAG;
    /**expectedDate*/

    private Long EXPECTED_DATE;
    /**deviationFlag*/

    private String DEVIATION_FLAG;
    /**arriveCauses*/

    private String ARRIVE_CAUSES;
    /**arriveDesc*/

    private String ARRIVE_DESC;
    /**deliveryCauses*/

    private String DELIVERY_CAUSES;
    /**deliveryDesc*/

    private String DELIVERY_DESC;
    /**status*/

    private String STATUS;
    /**domainName*/

    private String DOMAIN_NAME;
    /**insertUser*/

    private String INSERT_USER;
    /**insertUserName*/

    private String INSERT_USER_NAME;
    /**insertDate*/

    private Long INSERT_DATE;
    /**updateUser*/

    private String UPDATE_USER;
    /**updateUserName*/

    private String UPDATE_USER_NAME;
    /**updateDate*/

    private Long UPDATE_DATE;
    /**deleteSource*/

    private String DELETE_SOURCE;
    /**effectFlag*/

    private String EFFECT_FLAG;
    /**ddjrq*/

    private Long DDJRQ;
    /**vwxdwdm*/

    private String VWXDWDM;
    /**cqwh*/

    private String CQWH;
    /**czjgsdm*/

    private String CZJGSDM;
    /**startSiteCode*/

    private String START_SITE_CODE;
    /**vph*/

    private String VPH;
    /**dphscsj*/

    private Long DPHSCSJ;
    /**deliveryFlag*/

    private String DELIVERY_FLAG;
    /**arriveGoodsDriverId*/

    private Integer ARRIVE_GOODS_DRIVER_ID;
    /**arriveGoodsDiverName*/

    private String ARRIVE_GOODS_DIVER_NAME;
    /**arriveGoodsDriverPhone*/

    private String ARRIVE_GOODS_DRIVER_PHONE;
    /**arriveGoodsDate*/

    private Long ARRIVE_GOODS_DATE;
    /**arriveGoodsAddress*/

    private String ARRIVE_GOODS_ADDRESS;
    /**arriveGoodsCityName*/

    private String ARRIVE_GOODS_CITY_NAME;
    /**arriveGoodsLatitude*/

    private String ARRIVE_GOODS_LATITUDE;
    /**arriveGoodsLongitude*/

    private String ARRIVE_GOODS_LONGITUDE;
    /**ccpdm*/

    private String CCPDM;
    /**startProviceCode*/

    private String START_PROVICE_CODE;
    /**startCityCode*/

    private String START_CITY_CODE;
    /**endProviceCode*/

    private String END_PROVICE_CODE;
    /**endCityCode*/

    private String END_CITY_CODE;
    /**shipmentAbnormalFlag*/

    private String SHIPMENT_ABNORMAL_FLAG;
    /**shipmentAbnormalReason*/

    private String SHIPMENT_ABNORMAL_REASON;
    /**shipmentDriverId*/

    private Integer SHIPMENT_DRIVER_ID;
    /**shipmentDiverName*/

    private String SHIPMENT_DIVER_NAME;
    /**shipmentDriverPhone*/

    private String SHIPMENT_DRIVER_PHONE;
    /**shipmentLatitude*/

    private String SHIPMENT_LATITUDE;
    /**shipmentLongitude*/

    private String SHIPMENT_LONGITUDE;
    /**shipmentProvinces*/

    private String SHIPMENT_PROVINCES;
    /**shipmentCityName*/

    private String SHIPMENT_CITY_NAME;
    /**shipmentDate*/

    private Long SHIPMENT_DATE;
    /**cqrr*/

    private String CQRR;
    /**dckrq*/

    private Long DCKRQ;
    /**starrEndDis*/

    private Integer STARR_END_DIS;
    /**njcfdxh*/

    private Integer NJCFDXH;
    /**vfczt*/

    private String VFCZT;
    /**vsczt*/

    private String VSCZT;
    /**transportTime*/

    private Long TRANSPORT_TIME;
    /**cdhddm*/

    private String CDHDDM;
    /**nowShippersCode*/

    private String NOW_SHIPPERS_CODE;
    /**nowShippersName*/

    private String NOW_SHIPPERS_NAME;
    /**nowDate*/

    private Long NOW_DATE;
    /**nowAddress*/

    private String NOW_ADDRESS;
    /**nowCityName*/

    private String NOW_CITY_NAME;
    /**nowLatitude*/

    private String NOW_LATITUDE;
    /**nowLongitude*/

    private String NOW_LONGITUDE;
    /**loadingShippersCode*/

    private String LOADING_SHIPPERS_CODE;
    /**loadingShippersName*/

    private String LOADING_SHIPPERS_NAME;
    /**shipmentShippersCode*/

    private String SHIPMENT_SHIPPERS_CODE;
    /**shipmentShippersName*/

    private String SHIPMENT_SHIPPERS_NAME;
    /**zeroAmReportNum*/

    private Integer ZERO_AM_REPORT_NUM;
    /**oneAmReportNum*/

    private Integer ONE_AM_REPORT_NUM;
    /**twoAmEportNum*/

    private Integer TWO_AM_EPORT_NUM;
    /**threeAmReportNum*/

    private Integer THREE_AM_REPORT_NUM;
    /**fourAmReportNum*/

    private Integer FOUR_AM_REPORT_NUM;
    /**fiveAmReportNum*/

    private Integer FIVE_AM_REPORT_NUM;
    /**sixAmReportNum*/

    private Integer SIX_AM_REPORT_NUM;
    /**sevenAmReportNum*/

    private Integer SEVEN_AM_REPORT_NUM;
    /**eightAmReportNum*/

    private Integer EIGHT_AM_REPORT_NUM;
    /**nineAmReportNum*/

    private Integer NINE_AM_REPORT_NUM;
    /**tenAmReportNum*/

    private Integer TEN_AM_REPORT_NUM;
    /**elevenAmReportNum*/

    private Integer ELEVEN_AM_REPORT_NUM;
    /**twelveAmReportNum*/

    private Integer TWELVE_AM_REPORT_NUM;
    /**onePmReportNum*/

    private Integer ONE_PM_REPORT_NUM;
    /**twoPmEportNum*/

    private Integer TWO_PM_EPORT_NUM;
    /**threePmReportNum*/

    private Integer THREE_PM_REPORT_NUM;
    /**fourPmReportNum*/

    private Integer FOUR_PM_REPORT_NUM;
    /**fivePmReportNum*/

    private Integer FIVE_PM_REPORT_NUM;
    /**sixPmReportNum*/

    private Integer SIX_PM_REPORT_NUM;
    /**sevenPmReportNum*/

    private Integer SEVEN_PM_REPORT_NUM;
    /**eightPmReportNum*/

    private Integer EIGHT_PM_REPORT_NUM;
    /**ninePmReportNum*/

    private Integer NINE_PM_REPORT_NUM;
    /**tenPmReportNum*/

    private Integer TEN_PM_REPORT_NUM;
    /**elevenPmReportNum*/

    private Integer ELEVEN_PM_REPORT_NUM;
    /**reportNum*/

    private Integer REPORT_NUM;
    /**badNum*/

    private Integer BAD_NUM;
    /**abnormalNum*/

    private Integer ABNORMAL_NUM;
    /**deliveryShippersCode*/

    private String DELIVERY_SHIPPERS_CODE;
    /**deliveryShippersName*/

    private String DELIVERY_SHIPPERS_NAME;
    /**unloadingShippersCode*/

    private String UNLOADING_SHIPPERS_CODE;
    /**unloadingShippersName*/

    private String UNLOADING_SHIPPERS_NAME;
    /**sameChangeNum*/

    private Integer SAME_CHANGE_NUM;
    /**diffierentChangeNum*/

    private Integer DIFFIERENT_CHANGE_NUM;
    /**changeNum*/

    private Integer CHANGE_NUM;
    /**estimatedShippersCode*/

    private String ESTIMATED_SHIPPERS_CODE;
    /**estimatedShippersName*/

    private String ESTIMATED_SHIPPERS_NAME;
    /**arriveShippersCode*/

    private String ARRIVE_SHIPPERS_CODE;
    /**arriveShippersName*/

    private String ARRIVE_SHIPPERS_NAME;
    /**arriveGoodsShippersCode*/

    private String ARRIVE_GOODS_SHIPPERS_CODE;
    /**arriveGoodsShippersName*/

    private String ARRIVE_GOODS_SHIPPERS_NAME;
    /**ddhsj*/

    private Long DDHSJ;
    /**userArriveGoodsDate*/

    private Long USER_ARRIVE_GOODS_DATE;
    /**userArriveGoodsId*/

    private String USER_ARRIVE_GOODS_ID;
    /**userArriveGoodsName*/

    private String USER_ARRIVE_GOODS_NAME;
    /**userArriveGoodsOperateDate*/

    private Long USER_ARRIVE_GOODS_OPERATE_DATE;
    /**sptb02Czt*/

    private String SPTB02_CZT;
    /**isAllVisual*/

    private String IS_ALL_VISUAL;
    /**cpcdbh*/

    private String CPCDBH;
    /**sptb02Diss*/

    private Integer SPTB02_DISS;
    /**nbzwlsjDz*/

    private Integer NBZWLSJ_DZ;
    /**sptb02d1Nsl*/

    private Integer SPTB02D1_NSL;
    /**vehicleModels*/

    private String VEHICLE_MODELS;
    /**vehicleBrand*/

    private String VEHICLE_BRAND;
    /**sptb01cDdjrq*/

    private Long SPTB01C_DDJRQ;
    /**shippersJc*/

    private String SHIPPERS_JC;
    /**endRegionName*/

    private String END_REGION_NAME;
    /**lineName*/

    private String LINE_NAME;
    /**dueShippersCode*/

    private String DUE_SHIPPERS_CODE;
    /**dueShippersName*/

    private String DUE_SHIPPERS_NAME;
    /**dueShippersJc*/

    private String DUE_SHIPPERS_JC;
    /**rfidDckrq*/

    private Long RFID_DCKRQ;
    /**vehicleNcyde*/

    private Integer VEHICLE_NCYDE;
    /**auditDdhsj*/

    private Long AUDIT_DDHSJ;
    /**fawAuditDdhsj*/

    private Long FAW_AUDIT_DDHSJ;
    /**dcsType*/

    private String DCS_TYPE;
    /**warehouseCreatetime*/

    private Long WAREHOUSE_CREATETIME;
    /**warehouseUpdatetime*/

    private Long WAREHOUSE_UPDATETIME;

}
