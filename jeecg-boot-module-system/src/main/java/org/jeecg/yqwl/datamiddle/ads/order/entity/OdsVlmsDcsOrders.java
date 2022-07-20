package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @version 1.0
 * @Description
 * @ClassName OdsVlmsDcsOrders
 * @Author YULUO
 * @Date 2022/7/20
 */

@Data
@TableName("ods_vlms_dcs_orders")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ods_vlms_dcs_orders对象", description = "OdsVlmsDcsOrders")
public class OdsVlmsDcsOrders {

    /**pk*/
    @Excel(name = "pk", width = 15)
    @ApiModelProperty(value = "pk")
    private java.lang.Integer pk;
    /**vinNo*/
    @Excel(name = "vinNo", width = 15)
    @ApiModelProperty(value = "vinNo")
    private java.lang.String vinNo;
    /**idnum*/
    @Excel(name = "idnum", width = 15)
    @ApiModelProperty(value = "idnum")
    private java.lang.Integer idnum;
    /**yNo*/
    @Excel(name = "yNo", width = 15)
    @ApiModelProperty(value = "yNo")
    private java.lang.String yNo;
    /**statementsNo*/
    @Excel(name = "statementsNo", width = 15)
    @ApiModelProperty(value = "statementsNo")
    private java.lang.String statementsNo;
    /**taskNo*/
    @Excel(name = "taskNo", width = 15)
    @ApiModelProperty(value = "taskNo")
    private java.lang.String taskNo;
    /**trafficType*/
    @Excel(name = "trafficType", width = 15)
    @ApiModelProperty(value = "trafficType")
    private java.lang.String trafficType;
    /**dealersCode*/
    @Excel(name = "dealersCode", width = 15)
    @ApiModelProperty(value = "dealersCode")
    private java.lang.String dealersCode;
    /**dealersName*/
    @Excel(name = "dealersName", width = 15)
    @ApiModelProperty(value = "dealersName")
    private java.lang.String dealersName;
    /**dealersAddress*/
    @Excel(name = "dealersAddress", width = 15)
    @ApiModelProperty(value = "dealersAddress")
    private java.lang.String dealersAddress;
    /**shippersCode*/
    @Excel(name = "shippersCode", width = 15)
    @ApiModelProperty(value = "shippersCode")
    private java.lang.String shippersCode;
    /**shippersName*/
    @Excel(name = "shippersName", width = 15)
    @ApiModelProperty(value = "shippersName")
    private java.lang.String shippersName;
    /**startSiteName*/
    @Excel(name = "startSiteName", width = 15)
    @ApiModelProperty(value = "startSiteName")
    private java.lang.String startSiteName;
    /**startAddress*/
    @Excel(name = "startAddress", width = 15)
    @ApiModelProperty(value = "startAddress")
    private java.lang.String startAddress;
    /**startCityName*/
    @Excel(name = "startCityName", width = 15)
    @ApiModelProperty(value = "startCityName")
    private java.lang.String startCityName;
    /**startLatitude*/
    @Excel(name = "startLatitude", width = 15)
    @ApiModelProperty(value = "startLatitude")
    private java.lang.String startLatitude;
    /**startLongitude*/
    @Excel(name = "startLongitude", width = 15)
    @ApiModelProperty(value = "startLongitude")
    private java.lang.String startLongitude;
    /**endSiteName*/
    @Excel(name = "endSiteName", width = 15)
    @ApiModelProperty(value = "endSiteName")
    private java.lang.String endSiteName;
    /**endAddress*/
    @Excel(name = "endAddress", width = 15)
    @ApiModelProperty(value = "endAddress")
    private java.lang.String endAddress;
    /**endCityName*/
    @Excel(name = "endCityName", width = 15)
    @ApiModelProperty(value = "endCityName")
    private java.lang.String endCityName;
    /**endLatitude*/
    @Excel(name = "endLatitude", width = 15)
    @ApiModelProperty(value = "endLatitude")
    private java.lang.String endLatitude;
    /**endLongitude*/
    @Excel(name = "endLongitude", width = 15)
    @ApiModelProperty(value = "endLongitude")
    private java.lang.String endLongitude;
    /**driverId*/
    @Excel(name = "driverId", width = 15)
    @ApiModelProperty(value = "driverId")
    private java.lang.Integer driverId;
    /**diverName*/
    @Excel(name = "diverName", width = 15)
    @ApiModelProperty(value = "diverName")
    private java.lang.String diverName;
    /**driverPhone*/
    @Excel(name = "driverPhone", width = 15)
    @ApiModelProperty(value = "driverPhone")
    private java.lang.String driverPhone;
    /**vehicleId*/
    @Excel(name = "vehicleId", width = 15)
    @ApiModelProperty(value = "vehicleId")
    private java.lang.String vehicleId;
    /**vehiclePlate*/
    @Excel(name = "vehiclePlate", width = 15)
    @ApiModelProperty(value = "vehiclePlate")
    private java.lang.String vehiclePlate;
    /**loadingDriverId*/
    @Excel(name = "loadingDriverId", width = 15)
    @ApiModelProperty(value = "loadingDriverId")
    private java.lang.Integer loadingDriverId;
    /**loadingDiverName*/
    @Excel(name = "loadingDiverName", width = 15)
    @ApiModelProperty(value = "loadingDiverName")
    private java.lang.String loadingDiverName;
    /**loadingDriverPhone*/
    @Excel(name = "loadingDriverPhone", width = 15)
    @ApiModelProperty(value = "loadingDriverPhone")
    private java.lang.String loadingDriverPhone;
    /**loadingDate*/
    @Excel(name = "loadingDate", width = 15)
    @ApiModelProperty(value = "loadingDate")
    private java.lang.Long loadingDate;
    /**loadingAddress*/
    @Excel(name = "loadingAddress", width = 15)
    @ApiModelProperty(value = "loadingAddress")
    private java.lang.String loadingAddress;
    /**loadingCityName*/
    @Excel(name = "loadingCityName", width = 15)
    @ApiModelProperty(value = "loadingCityName")
    private java.lang.String loadingCityName;
    /**loadingLatitude*/
    @Excel(name = "loadingLatitude", width = 15)
    @ApiModelProperty(value = "loadingLatitude")
    private java.lang.String loadingLatitude;
    /**loadingLongitude*/
    @Excel(name = "loadingLongitude", width = 15)
    @ApiModelProperty(value = "loadingLongitude")
    private java.lang.String loadingLongitude;
    /**estimatedDriverId*/
    @Excel(name = "estimatedDriverId", width = 15)
    @ApiModelProperty(value = "estimatedDriverId")
    private java.lang.Integer estimatedDriverId;
    /**estimatedDiverName*/
    @Excel(name = "estimatedDiverName", width = 15)
    @ApiModelProperty(value = "estimatedDiverName")
    private java.lang.String estimatedDiverName;
    /**estimatedDriverPhone*/
    @Excel(name = "estimatedDriverPhone", width = 15)
    @ApiModelProperty(value = "estimatedDriverPhone")
    private java.lang.String estimatedDriverPhone;
    /**estimatedDate*/
    @Excel(name = "estimatedDate", width = 15)
    @ApiModelProperty(value = "estimatedDate")
    private java.lang.Long estimatedDate;
    /**estimatedAddress*/
    @Excel(name = "estimatedAddress", width = 15)
    @ApiModelProperty(value = "estimatedAddress")
    private java.lang.String estimatedAddress;
    /**estimatedCityName*/
    @Excel(name = "estimatedCityName", width = 15)
    @ApiModelProperty(value = "estimatedCityName")
    private java.lang.String estimatedCityName;
    /**estimatedLatitude*/
    @Excel(name = "estimatedLatitude", width = 15)
    @ApiModelProperty(value = "estimatedLatitude")
    private java.lang.String estimatedLatitude;
    /**estimatedLongitude*/
    @Excel(name = "estimatedLongitude", width = 15)
    @ApiModelProperty(value = "estimatedLongitude")
    private java.lang.String estimatedLongitude;
    /**arriveDriverId*/
    @Excel(name = "arriveDriverId", width = 15)
    @ApiModelProperty(value = "arriveDriverId")
    private java.lang.Integer arriveDriverId;
    /**arriveDiverName*/
    @Excel(name = "arriveDiverName", width = 15)
    @ApiModelProperty(value = "arriveDiverName")
    private java.lang.String arriveDiverName;
    /**arriveDriverPhone*/
    @Excel(name = "arriveDriverPhone", width = 15)
    @ApiModelProperty(value = "arriveDriverPhone")
    private java.lang.String arriveDriverPhone;
    /**arriveDate*/
    @Excel(name = "arriveDate", width = 15)
    @ApiModelProperty(value = "arriveDate")
    private java.lang.Long arriveDate;
    /**arriveAddress*/
    @Excel(name = "arriveAddress", width = 15)
    @ApiModelProperty(value = "arriveAddress")
    private java.lang.String arriveAddress;
    /**arriveCityName*/
    @Excel(name = "arriveCityName", width = 15)
    @ApiModelProperty(value = "arriveCityName")
    private java.lang.String arriveCityName;
    /**arriveLatitude*/
    @Excel(name = "arriveLatitude", width = 15)
    @ApiModelProperty(value = "arriveLatitude")
    private java.lang.String arriveLatitude;
    /**arriveLongitude*/
    @Excel(name = "arriveLongitude", width = 15)
    @ApiModelProperty(value = "arriveLongitude")
    private java.lang.String arriveLongitude;
    /**deliveryDriverId*/
    @Excel(name = "deliveryDriverId", width = 15)
    @ApiModelProperty(value = "deliveryDriverId")
    private java.lang.Integer deliveryDriverId;
    /**deliveryDiverName*/
    @Excel(name = "deliveryDiverName", width = 15)
    @ApiModelProperty(value = "deliveryDiverName")
    private java.lang.String deliveryDiverName;
    /**deliveryDriverPhone*/
    @Excel(name = "deliveryDriverPhone", width = 15)
    @ApiModelProperty(value = "deliveryDriverPhone")
    private java.lang.String deliveryDriverPhone;
    /**deliveryDate*/
    @Excel(name = "deliveryDate", width = 15)
    @ApiModelProperty(value = "deliveryDate")
    private java.lang.Long deliveryDate;
    /**deliveryAddress*/
    @Excel(name = "deliveryAddress", width = 15)
    @ApiModelProperty(value = "deliveryAddress")
    private java.lang.String deliveryAddress;
    /**deliveryCityName*/
    @Excel(name = "deliveryCityName", width = 15)
    @ApiModelProperty(value = "deliveryCityName")
    private java.lang.String deliveryCityName;
    /**deliveryLatitude*/
    @Excel(name = "deliveryLatitude", width = 15)
    @ApiModelProperty(value = "deliveryLatitude")
    private java.lang.String deliveryLatitude;
    /**deliveryLongitude*/
    @Excel(name = "deliveryLongitude", width = 15)
    @ApiModelProperty(value = "deliveryLongitude")
    private java.lang.String deliveryLongitude;
    /**unloadingDriverId*/
    @Excel(name = "unloadingDriverId", width = 15)
    @ApiModelProperty(value = "unloadingDriverId")
    private java.lang.Integer unloadingDriverId;
    /**unloadingDiverName*/
    @Excel(name = "unloadingDiverName", width = 15)
    @ApiModelProperty(value = "unloadingDiverName")
    private java.lang.String unloadingDiverName;
    /**unloadingDriverPhone*/
    @Excel(name = "unloadingDriverPhone", width = 15)
    @ApiModelProperty(value = "unloadingDriverPhone")
    private java.lang.String unloadingDriverPhone;
    /**unloadingDeliveryDate*/
    @Excel(name = "unloadingDeliveryDate", width = 15)
    @ApiModelProperty(value = "unloadingDeliveryDate")
    private java.lang.Long unloadingDeliveryDate;
    /**unloadingAddress*/
    @Excel(name = "unloadingAddress", width = 15)
    @ApiModelProperty(value = "unloadingAddress")
    private java.lang.String unloadingAddress;
    /**unloadingCityName*/
    @Excel(name = "unloadingCityName", width = 15)
    @ApiModelProperty(value = "unloadingCityName")
    private java.lang.String unloadingCityName;
    /**unloadingLatitude*/
    @Excel(name = "unloadingLatitude", width = 15)
    @ApiModelProperty(value = "unloadingLatitude")
    private java.lang.String unloadingLatitude;
    /**unloadingLongitude*/
    @Excel(name = "unloadingLongitude", width = 15)
    @ApiModelProperty(value = "unloadingLongitude")
    private java.lang.String unloadingLongitude;
    /**reverseSyncFlag*/
    @Excel(name = "reverseSyncFlag", width = 15)
    @ApiModelProperty(value = "reverseSyncFlag")
    private java.lang.String reverseSyncFlag;
    /**expectedDate*/
    @Excel(name = "expectedDate", width = 15)
    @ApiModelProperty(value = "expectedDate")
    private java.lang.Long expectedDate;
    /**deviationFlag*/
    @Excel(name = "deviationFlag", width = 15)
    @ApiModelProperty(value = "deviationFlag")
    private java.lang.String deviationFlag;
    /**arriveCauses*/
    @Excel(name = "arriveCauses", width = 15)
    @ApiModelProperty(value = "arriveCauses")
    private java.lang.String arriveCauses;
    /**arriveDesc*/
    @Excel(name = "arriveDesc", width = 15)
    @ApiModelProperty(value = "arriveDesc")
    private java.lang.String arriveDesc;
    /**deliveryCauses*/
    @Excel(name = "deliveryCauses", width = 15)
    @ApiModelProperty(value = "deliveryCauses")
    private java.lang.String deliveryCauses;
    /**deliveryDesc*/
    @Excel(name = "deliveryDesc", width = 15)
    @ApiModelProperty(value = "deliveryDesc")
    private java.lang.String deliveryDesc;
    /**status*/
    @Excel(name = "status", width = 15)
    @ApiModelProperty(value = "status")
    private java.lang.String status;
    /**domainName*/
    @Excel(name = "domainName", width = 15)
    @ApiModelProperty(value = "domainName")
    private java.lang.String domainName;
    /**insertUser*/
    @Excel(name = "insertUser", width = 15)
    @ApiModelProperty(value = "insertUser")
    private java.lang.String insertUser;
    /**insertUserName*/
    @Excel(name = "insertUserName", width = 15)
    @ApiModelProperty(value = "insertUserName")
    private java.lang.String insertUserName;
    /**insertDate*/
    @Excel(name = "insertDate", width = 15)
    @ApiModelProperty(value = "insertDate")
    private java.lang.Long insertDate;
    /**updateUser*/
    @Excel(name = "updateUser", width = 15)
    @ApiModelProperty(value = "updateUser")
    private java.lang.String updateUser;
    /**updateUserName*/
    @Excel(name = "updateUserName", width = 15)
    @ApiModelProperty(value = "updateUserName")
    private java.lang.String updateUserName;
    /**updateDate*/
    @Excel(name = "updateDate", width = 15)
    @ApiModelProperty(value = "updateDate")
    private java.lang.Long updateDate;
    /**deleteSource*/
    @Excel(name = "deleteSource", width = 15)
    @ApiModelProperty(value = "deleteSource")
    private java.lang.String deleteSource;
    /**effectFlag*/
    @Excel(name = "effectFlag", width = 15)
    @ApiModelProperty(value = "effectFlag")
    private java.lang.String effectFlag;
    /**ddjrq*/
    @Excel(name = "ddjrq", width = 15)
    @ApiModelProperty(value = "ddjrq")
    private java.lang.Long ddjrq;
    /**vwxdwdm*/
    @Excel(name = "vwxdwdm", width = 15)
    @ApiModelProperty(value = "vwxdwdm")
    private java.lang.String vwxdwdm;
    /**cqwh*/
    @Excel(name = "cqwh", width = 15)
    @ApiModelProperty(value = "cqwh")
    private java.lang.String cqwh;
    /**czjgsdm*/
    @Excel(name = "czjgsdm", width = 15)
    @ApiModelProperty(value = "czjgsdm")
    private java.lang.String czjgsdm;
    /**startSiteCode*/
    @Excel(name = "startSiteCode", width = 15)
    @ApiModelProperty(value = "startSiteCode")
    private java.lang.String startSiteCode;
    /**vph*/
    @Excel(name = "vph", width = 15)
    @ApiModelProperty(value = "vph")
    private java.lang.String vph;
    /**dphscsj*/
    @Excel(name = "dphscsj", width = 15)
    @ApiModelProperty(value = "dphscsj")
    private java.lang.Long dphscsj;
    /**deliveryFlag*/
    @Excel(name = "deliveryFlag", width = 15)
    @ApiModelProperty(value = "deliveryFlag")
    private java.lang.String deliveryFlag;
    /**arriveGoodsDriverId*/
    @Excel(name = "arriveGoodsDriverId", width = 15)
    @ApiModelProperty(value = "arriveGoodsDriverId")
    private java.lang.Integer arriveGoodsDriverId;
    /**arriveGoodsDiverName*/
    @Excel(name = "arriveGoodsDiverName", width = 15)
    @ApiModelProperty(value = "arriveGoodsDiverName")
    private java.lang.String arriveGoodsDiverName;
    /**arriveGoodsDriverPhone*/
    @Excel(name = "arriveGoodsDriverPhone", width = 15)
    @ApiModelProperty(value = "arriveGoodsDriverPhone")
    private java.lang.String arriveGoodsDriverPhone;
    /**arriveGoodsDate*/
    @Excel(name = "arriveGoodsDate", width = 15)
    @ApiModelProperty(value = "arriveGoodsDate")
    private java.lang.Long arriveGoodsDate;
    /**arriveGoodsAddress*/
    @Excel(name = "arriveGoodsAddress", width = 15)
    @ApiModelProperty(value = "arriveGoodsAddress")
    private java.lang.String arriveGoodsAddress;
    /**arriveGoodsCityName*/
    @Excel(name = "arriveGoodsCityName", width = 15)
    @ApiModelProperty(value = "arriveGoodsCityName")
    private java.lang.String arriveGoodsCityName;
    /**arriveGoodsLatitude*/
    @Excel(name = "arriveGoodsLatitude", width = 15)
    @ApiModelProperty(value = "arriveGoodsLatitude")
    private java.lang.String arriveGoodsLatitude;
    /**arriveGoodsLongitude*/
    @Excel(name = "arriveGoodsLongitude", width = 15)
    @ApiModelProperty(value = "arriveGoodsLongitude")
    private java.lang.String arriveGoodsLongitude;
    /**ccpdm*/
    @Excel(name = "ccpdm", width = 15)
    @ApiModelProperty(value = "ccpdm")
    private java.lang.String ccpdm;
    /**startProviceCode*/
    @Excel(name = "startProviceCode", width = 15)
    @ApiModelProperty(value = "startProviceCode")
    private java.lang.String startProviceCode;
    /**startCityCode*/
    @Excel(name = "startCityCode", width = 15)
    @ApiModelProperty(value = "startCityCode")
    private java.lang.String startCityCode;
    /**endProviceCode*/
    @Excel(name = "endProviceCode", width = 15)
    @ApiModelProperty(value = "endProviceCode")
    private java.lang.String endProviceCode;
    /**endCityCode*/
    @Excel(name = "endCityCode", width = 15)
    @ApiModelProperty(value = "endCityCode")
    private java.lang.String endCityCode;
    /**shipmentAbnormalFlag*/
    @Excel(name = "shipmentAbnormalFlag", width = 15)
    @ApiModelProperty(value = "shipmentAbnormalFlag")
    private java.lang.String shipmentAbnormalFlag;
    /**shipmentAbnormalReason*/
    @Excel(name = "shipmentAbnormalReason", width = 15)
    @ApiModelProperty(value = "shipmentAbnormalReason")
    private java.lang.String shipmentAbnormalReason;
    /**shipmentDriverId*/
    @Excel(name = "shipmentDriverId", width = 15)
    @ApiModelProperty(value = "shipmentDriverId")
    private java.lang.Integer shipmentDriverId;
    /**shipmentDiverName*/
    @Excel(name = "shipmentDiverName", width = 15)
    @ApiModelProperty(value = "shipmentDiverName")
    private java.lang.String shipmentDiverName;
    /**shipmentDriverPhone*/
    @Excel(name = "shipmentDriverPhone", width = 15)
    @ApiModelProperty(value = "shipmentDriverPhone")
    private java.lang.String shipmentDriverPhone;
    /**shipmentLatitude*/
    @Excel(name = "shipmentLatitude", width = 15)
    @ApiModelProperty(value = "shipmentLatitude")
    private java.lang.String shipmentLatitude;
    /**shipmentLongitude*/
    @Excel(name = "shipmentLongitude", width = 15)
    @ApiModelProperty(value = "shipmentLongitude")
    private java.lang.String shipmentLongitude;
    /**shipmentProvinces*/
    @Excel(name = "shipmentProvinces", width = 15)
    @ApiModelProperty(value = "shipmentProvinces")
    private java.lang.String shipmentProvinces;
    /**shipmentCityName*/
    @Excel(name = "shipmentCityName", width = 15)
    @ApiModelProperty(value = "shipmentCityName")
    private java.lang.String shipmentCityName;
    /**shipmentDate*/
    @Excel(name = "shipmentDate", width = 15)
    @ApiModelProperty(value = "shipmentDate")
    private java.lang.Long shipmentDate;
    /**cqrr*/
    @Excel(name = "cqrr", width = 15)
    @ApiModelProperty(value = "cqrr")
    private java.lang.String cqrr;
    /**dckrq*/
    @Excel(name = "dckrq", width = 15)
    @ApiModelProperty(value = "dckrq")
    private java.lang.Long dckrq;
    /**starrEndDis*/
    @Excel(name = "starrEndDis", width = 15)
    @ApiModelProperty(value = "starrEndDis")
    private java.lang.Integer starrEndDis;
    /**njcfdxh*/
    @Excel(name = "njcfdxh", width = 15)
    @ApiModelProperty(value = "njcfdxh")
    private java.lang.Integer njcfdxh;
    /**vfczt*/
    @Excel(name = "vfczt", width = 15)
    @ApiModelProperty(value = "vfczt")
    private java.lang.String vfczt;
    /**vsczt*/
    @Excel(name = "vsczt", width = 15)
    @ApiModelProperty(value = "vsczt")
    private java.lang.String vsczt;
    /**transportTime*/
    @Excel(name = "transportTime", width = 15)
    @ApiModelProperty(value = "transportTime")
    private java.lang.Long transportTime;
    /**cdhddm*/
    @Excel(name = "cdhddm", width = 15)
    @ApiModelProperty(value = "cdhddm")
    private java.lang.String cdhddm;
    /**nowShippersCode*/
    @Excel(name = "nowShippersCode", width = 15)
    @ApiModelProperty(value = "nowShippersCode")
    private java.lang.String nowShippersCode;
    /**nowShippersName*/
    @Excel(name = "nowShippersName", width = 15)
    @ApiModelProperty(value = "nowShippersName")
    private java.lang.String nowShippersName;
    /**nowDate*/
    @Excel(name = "nowDate", width = 15)
    @ApiModelProperty(value = "nowDate")
    private java.lang.Long nowDate;
    /**nowAddress*/
    @Excel(name = "nowAddress", width = 15)
    @ApiModelProperty(value = "nowAddress")
    private java.lang.String nowAddress;
    /**nowCityName*/
    @Excel(name = "nowCityName", width = 15)
    @ApiModelProperty(value = "nowCityName")
    private java.lang.String nowCityName;
    /**nowLatitude*/
    @Excel(name = "nowLatitude", width = 15)
    @ApiModelProperty(value = "nowLatitude")
    private java.lang.String nowLatitude;
    /**nowLongitude*/
    @Excel(name = "nowLongitude", width = 15)
    @ApiModelProperty(value = "nowLongitude")
    private java.lang.String nowLongitude;
    /**loadingShippersCode*/
    @Excel(name = "loadingShippersCode", width = 15)
    @ApiModelProperty(value = "loadingShippersCode")
    private java.lang.String loadingShippersCode;
    /**loadingShippersName*/
    @Excel(name = "loadingShippersName", width = 15)
    @ApiModelProperty(value = "loadingShippersName")
    private java.lang.String loadingShippersName;
    /**shipmentShippersCode*/
    @Excel(name = "shipmentShippersCode", width = 15)
    @ApiModelProperty(value = "shipmentShippersCode")
    private java.lang.String shipmentShippersCode;
    /**shipmentShippersName*/
    @Excel(name = "shipmentShippersName", width = 15)
    @ApiModelProperty(value = "shipmentShippersName")
    private java.lang.String shipmentShippersName;
    /**zeroAmReportNum*/
    @Excel(name = "zeroAmReportNum", width = 15)
    @ApiModelProperty(value = "zeroAmReportNum")
    private java.lang.Integer zeroAmReportNum;
    /**oneAmReportNum*/
    @Excel(name = "oneAmReportNum", width = 15)
    @ApiModelProperty(value = "oneAmReportNum")
    private java.lang.Integer oneAmReportNum;
    /**twoAmEportNum*/
    @Excel(name = "twoAmEportNum", width = 15)
    @ApiModelProperty(value = "twoAmEportNum")
    private java.lang.Integer twoAmEportNum;
    /**threeAmReportNum*/
    @Excel(name = "threeAmReportNum", width = 15)
    @ApiModelProperty(value = "threeAmReportNum")
    private java.lang.Integer threeAmReportNum;
    /**fourAmReportNum*/
    @Excel(name = "fourAmReportNum", width = 15)
    @ApiModelProperty(value = "fourAmReportNum")
    private java.lang.Integer fourAmReportNum;
    /**fiveAmReportNum*/
    @Excel(name = "fiveAmReportNum", width = 15)
    @ApiModelProperty(value = "fiveAmReportNum")
    private java.lang.Integer fiveAmReportNum;
    /**sixAmReportNum*/
    @Excel(name = "sixAmReportNum", width = 15)
    @ApiModelProperty(value = "sixAmReportNum")
    private java.lang.Integer sixAmReportNum;
    /**sevenAmReportNum*/
    @Excel(name = "sevenAmReportNum", width = 15)
    @ApiModelProperty(value = "sevenAmReportNum")
    private java.lang.Integer sevenAmReportNum;
    /**eightAmReportNum*/
    @Excel(name = "eightAmReportNum", width = 15)
    @ApiModelProperty(value = "eightAmReportNum")
    private java.lang.Integer eightAmReportNum;
    /**nineAmReportNum*/
    @Excel(name = "nineAmReportNum", width = 15)
    @ApiModelProperty(value = "nineAmReportNum")
    private java.lang.Integer nineAmReportNum;
    /**tenAmReportNum*/
    @Excel(name = "tenAmReportNum", width = 15)
    @ApiModelProperty(value = "tenAmReportNum")
    private java.lang.Integer tenAmReportNum;
    /**elevenAmReportNum*/
    @Excel(name = "elevenAmReportNum", width = 15)
    @ApiModelProperty(value = "elevenAmReportNum")
    private java.lang.Integer elevenAmReportNum;
    /**twelveAmReportNum*/
    @Excel(name = "twelveAmReportNum", width = 15)
    @ApiModelProperty(value = "twelveAmReportNum")
    private java.lang.Integer twelveAmReportNum;
    /**onePmReportNum*/
    @Excel(name = "onePmReportNum", width = 15)
    @ApiModelProperty(value = "onePmReportNum")
    private java.lang.Integer onePmReportNum;
    /**twoPmEportNum*/
    @Excel(name = "twoPmEportNum", width = 15)
    @ApiModelProperty(value = "twoPmEportNum")
    private java.lang.Integer twoPmEportNum;
    /**threePmReportNum*/
    @Excel(name = "threePmReportNum", width = 15)
    @ApiModelProperty(value = "threePmReportNum")
    private java.lang.Integer threePmReportNum;
    /**fourPmReportNum*/
    @Excel(name = "fourPmReportNum", width = 15)
    @ApiModelProperty(value = "fourPmReportNum")
    private java.lang.Integer fourPmReportNum;
    /**fivePmReportNum*/
    @Excel(name = "fivePmReportNum", width = 15)
    @ApiModelProperty(value = "fivePmReportNum")
    private java.lang.Integer fivePmReportNum;
    /**sixPmReportNum*/
    @Excel(name = "sixPmReportNum", width = 15)
    @ApiModelProperty(value = "sixPmReportNum")
    private java.lang.Integer sixPmReportNum;
    /**sevenPmReportNum*/
    @Excel(name = "sevenPmReportNum", width = 15)
    @ApiModelProperty(value = "sevenPmReportNum")
    private java.lang.Integer sevenPmReportNum;
    /**eightPmReportNum*/
    @Excel(name = "eightPmReportNum", width = 15)
    @ApiModelProperty(value = "eightPmReportNum")
    private java.lang.Integer eightPmReportNum;
    /**ninePmReportNum*/
    @Excel(name = "ninePmReportNum", width = 15)
    @ApiModelProperty(value = "ninePmReportNum")
    private java.lang.Integer ninePmReportNum;
    /**tenPmReportNum*/
    @Excel(name = "tenPmReportNum", width = 15)
    @ApiModelProperty(value = "tenPmReportNum")
    private java.lang.Integer tenPmReportNum;
    /**elevenPmReportNum*/
    @Excel(name = "elevenPmReportNum", width = 15)
    @ApiModelProperty(value = "elevenPmReportNum")
    private java.lang.Integer elevenPmReportNum;
    /**reportNum*/
    @Excel(name = "reportNum", width = 15)
    @ApiModelProperty(value = "reportNum")
    private java.lang.Integer reportNum;
    /**badNum*/
    @Excel(name = "badNum", width = 15)
    @ApiModelProperty(value = "badNum")
    private java.lang.Integer badNum;
    /**abnormalNum*/
    @Excel(name = "abnormalNum", width = 15)
    @ApiModelProperty(value = "abnormalNum")
    private java.lang.Integer abnormalNum;
    /**deliveryShippersCode*/
    @Excel(name = "deliveryShippersCode", width = 15)
    @ApiModelProperty(value = "deliveryShippersCode")
    private java.lang.String deliveryShippersCode;
    /**deliveryShippersName*/
    @Excel(name = "deliveryShippersName", width = 15)
    @ApiModelProperty(value = "deliveryShippersName")
    private java.lang.String deliveryShippersName;
    /**unloadingShippersCode*/
    @Excel(name = "unloadingShippersCode", width = 15)
    @ApiModelProperty(value = "unloadingShippersCode")
    private java.lang.String unloadingShippersCode;
    /**unloadingShippersName*/
    @Excel(name = "unloadingShippersName", width = 15)
    @ApiModelProperty(value = "unloadingShippersName")
    private java.lang.String unloadingShippersName;
    /**sameChangeNum*/
    @Excel(name = "sameChangeNum", width = 15)
    @ApiModelProperty(value = "sameChangeNum")
    private java.lang.Integer sameChangeNum;
    /**diffierentChangeNum*/
    @Excel(name = "diffierentChangeNum", width = 15)
    @ApiModelProperty(value = "diffierentChangeNum")
    private java.lang.Integer diffierentChangeNum;
    /**changeNum*/
    @Excel(name = "changeNum", width = 15)
    @ApiModelProperty(value = "changeNum")
    private java.lang.Integer changeNum;
    /**estimatedShippersCode*/
    @Excel(name = "estimatedShippersCode", width = 15)
    @ApiModelProperty(value = "estimatedShippersCode")
    private java.lang.String estimatedShippersCode;
    /**estimatedShippersName*/
    @Excel(name = "estimatedShippersName", width = 15)
    @ApiModelProperty(value = "estimatedShippersName")
    private java.lang.String estimatedShippersName;
    /**arriveShippersCode*/
    @Excel(name = "arriveShippersCode", width = 15)
    @ApiModelProperty(value = "arriveShippersCode")
    private java.lang.String arriveShippersCode;
    /**arriveShippersName*/
    @Excel(name = "arriveShippersName", width = 15)
    @ApiModelProperty(value = "arriveShippersName")
    private java.lang.String arriveShippersName;
    /**arriveGoodsShippersCode*/
    @Excel(name = "arriveGoodsShippersCode", width = 15)
    @ApiModelProperty(value = "arriveGoodsShippersCode")
    private java.lang.String arriveGoodsShippersCode;
    /**arriveGoodsShippersName*/
    @Excel(name = "arriveGoodsShippersName", width = 15)
    @ApiModelProperty(value = "arriveGoodsShippersName")
    private java.lang.String arriveGoodsShippersName;
    /**ddhsj*/
    @Excel(name = "ddhsj", width = 15)
    @ApiModelProperty(value = "ddhsj")
    private java.lang.Long ddhsj;
    /**userArriveGoodsDate*/
    @Excel(name = "userArriveGoodsDate", width = 15)
    @ApiModelProperty(value = "userArriveGoodsDate")
    private java.lang.Long userArriveGoodsDate;
    /**userArriveGoodsId*/
    @Excel(name = "userArriveGoodsId", width = 15)
    @ApiModelProperty(value = "userArriveGoodsId")
    private java.lang.String userArriveGoodsId;
    /**userArriveGoodsName*/
    @Excel(name = "userArriveGoodsName", width = 15)
    @ApiModelProperty(value = "userArriveGoodsName")
    private java.lang.String userArriveGoodsName;
    /**userArriveGoodsOperateDate*/
    @Excel(name = "userArriveGoodsOperateDate", width = 15)
    @ApiModelProperty(value = "userArriveGoodsOperateDate")
    private java.lang.Long userArriveGoodsOperateDate;
    /**sptb02Czt*/
    @Excel(name = "sptb02Czt", width = 15)
    @ApiModelProperty(value = "sptb02Czt")
    private java.lang.String sptb02Czt;
    /**isAllVisual*/
    @Excel(name = "isAllVisual", width = 15)
    @ApiModelProperty(value = "isAllVisual")
    private java.lang.String isAllVisual;
    /**cpcdbh*/
    @Excel(name = "cpcdbh", width = 15)
    @ApiModelProperty(value = "cpcdbh")
    private java.lang.String cpcdbh;
    /**sptb02Diss*/
    @Excel(name = "sptb02Diss", width = 15)
    @ApiModelProperty(value = "sptb02Diss")
    private java.lang.Integer sptb02Diss;
    /**nbzwlsjDz*/
    @Excel(name = "nbzwlsjDz", width = 15)
    @ApiModelProperty(value = "nbzwlsjDz")
    private java.lang.Integer nbzwlsjDz;
    /**sptb02d1Nsl*/
    @Excel(name = "sptb02d1Nsl", width = 15)
    @ApiModelProperty(value = "sptb02d1Nsl")
    private java.lang.Integer sptb02d1Nsl;
    /**vehicleModels*/
    @Excel(name = "vehicleModels", width = 15)
    @ApiModelProperty(value = "vehicleModels")
    private java.lang.String vehicleModels;
    /**vehicleBrand*/
    @Excel(name = "vehicleBrand", width = 15)
    @ApiModelProperty(value = "vehicleBrand")
    private java.lang.String vehicleBrand;
    /**sptb01cDdjrq*/
    @Excel(name = "sptb01cDdjrq", width = 15)
    @ApiModelProperty(value = "sptb01cDdjrq")
    private java.lang.Long sptb01cDdjrq;
    /**shippersJc*/
    @Excel(name = "shippersJc", width = 15)
    @ApiModelProperty(value = "shippersJc")
    private java.lang.String shippersJc;
    /**endRegionName*/
    @Excel(name = "endRegionName", width = 15)
    @ApiModelProperty(value = "endRegionName")
    private java.lang.String endRegionName;
    /**lineName*/
    @Excel(name = "lineName", width = 15)
    @ApiModelProperty(value = "lineName")
    private java.lang.String lineName;
    /**dueShippersCode*/
    @Excel(name = "dueShippersCode", width = 15)
    @ApiModelProperty(value = "dueShippersCode")
    private java.lang.String dueShippersCode;
    /**dueShippersName*/
    @Excel(name = "dueShippersName", width = 15)
    @ApiModelProperty(value = "dueShippersName")
    private java.lang.String dueShippersName;
    /**dueShippersJc*/
    @Excel(name = "dueShippersJc", width = 15)
    @ApiModelProperty(value = "dueShippersJc")
    private java.lang.String dueShippersJc;
    /**rfidDckrq*/
    @Excel(name = "rfidDckrq", width = 15)
    @ApiModelProperty(value = "rfidDckrq")
    private java.lang.Long rfidDckrq;
    /**vehicleNcyde*/
    @Excel(name = "vehicleNcyde", width = 15)
    @ApiModelProperty(value = "vehicleNcyde")
    private java.lang.Integer vehicleNcyde;
    /**auditDdhsj*/
    @Excel(name = "auditDdhsj", width = 15)
    @ApiModelProperty(value = "auditDdhsj")
    private java.lang.Long auditDdhsj;
    /**fawAuditDdhsj*/
    @Excel(name = "fawAuditDdhsj", width = 15)
    @ApiModelProperty(value = "fawAuditDdhsj")
    private java.lang.Long fawAuditDdhsj;
    /**dcsType*/
    @Excel(name = "dcsType", width = 15)
    @ApiModelProperty(value = "dcsType")
    private java.lang.String dcsType;
    /**warehouseCreatetime*/
    @Excel(name = "warehouseCreatetime", width = 15)
    @ApiModelProperty(value = "warehouseCreatetime")
    private java.lang.Long warehouseCreatetime;
    /**warehouseUpdatetime*/
    @Excel(name = "warehouseUpdatetime", width = 15)
    @ApiModelProperty(value = "warehouseUpdatetime")
    private java.lang.Long warehouseUpdatetime;
}
