package com.yqwl.datamiddle.realtime.common;

/**
 * kafka topic 常量定义
 * 带有_LATEST_0701为事实表增量数据的抽取 by xiaofeng 2022.07.01
 */
public class KafkaTopicConst {


    //groupid
    public static final String ODS_VLMS_SPTC34_GROUP = "ods_vlms_sptc34_group";
    public static final String ODS_VLMS_SPTB02_GROUP = "ods_vlms_sptb02_group";
    public static final String ODS_VLMS_SPTB02_LATEST_0701_GROUP = "ods_vlms_sptb02_latest_0701_group";
    public static final String DWD_VLMS_SPTB02_GROUP = "dwd_vlms_sptb02_group";
    public static final String DWD_VLMS_SPTB02_GROUP_1 = "dwd_vlms_sptb02_group_1";
    public static final String ODS_VLMS_SYSC07_GROUP = "ods_vlms_sysc07_group";
    public static final String ODS_VLMS_SYSC08_GROUP = "ods_vlms_sysc08_group";
    public static final String ODS_VLMS_MDAC01_GROUP = "ods_vlms_mdac01_group";
    public static final String DWD_VLMS_BASE_STATION_DATA_GROUP_1 = "dwd_vlms_base_station_data_group_1";
    public static final String DWD_VLMS_BASE_STATION_DATA_GROUP_2 = "dwd_vlms_base_station_data_group_2";
    public static final String DWD_VLMS_BASE_STATION_DATA_EPC_GROUP = "dwd_vlms_base_station_data_epc_group";
    public static final String ODS_VLMS_BASE_STATION_DATA_EPC_GROUP = "ods_vlms_base_station_data_epc_group";
    public static final String DWM_VLMS_SPTB02_GROUP = "dwm_vlms_sptb02_group";
    public static final String ODS_VLMS_BASE_STATION_DATA_GROUP = "ods_vlms_base_station_data_group";
    public static final String ODS_VLMS_BASE_STATION_DATA_LATEST_0701_GROUP = "ods_vlms_base_station_data_latest_0701_group";


    public static final String ORACLE_TOPIC_GROUP = "oracle_source_group";


    //ods topic
    //oracle cdc 统一进入topic
    public static final String CDC_VLMS_UNITE_ORACLE = "cdc_vlms_unite_oracle";
    public static final String CDC_VLMS_UNITE_ORACLE_ALL_0712 = "cdc_vlms_unite_oracle_all_0712";
    public static final String CDC_VLMS_UNITE_ORACLE_Latest_0804 = "cdc_vlms_unite_oracle_Latest_0804";
    public static final String CDC_VLMS_UNITE_ORACLE_GROUP = "cdc_vlms_unite_oracle_group";
    public static final String CDC_VLMS_UNITE_ORACLE_GROUP_ALL_0712 = "cdc_vlms_unite_oracle_group_all_0712";
    public static final String CDC_VLMS_UNITE_ORACLE_GROUP_Latest_0804 = "cdc_vlms_unite_oracle_Latest_0804";
    //source 表sysc07 cdc kafka topic name
    public static final String ODS_VLMS_SPTB02 = "ods_vlms_sptb02";
    public static final String ODS_VLMS_SPTB02_LATEST_0701 = "ods_vlms_sptb02_latest_0701";
    public static final String ODS_VLMS_SPTB02_02 = "ods_vlms_sptb02_02";
    public static final String ODS_VLMS_SPTC61 = "ods_vlms_sptc61";
    public static final String ODS_VLMS_SYSC09D = "ods_vlms_sysc09d";
    public static final String ODS_VLMS_SPTC62 = "ods_vlms_sptc62";
    public static final String ODS_VLMS_SPTC34 = "ods_vlms_sptc34";
    public static final String ODS_VLMS_SPTC34SDTD = "ods_vlms_sptc34sdtd";
    public static final String ODS_VLMS_MDAC52 = "ods_vlms_mdac52";
    public static final String ODS_VLMS_MDAC31 = "ods_vlms_mdac31";
    public static final String ODS_VLMS_SPTB02D1 = "ods_vlms_sptb02d1";
    public static final String ODS_VLMS_MDAC32 = "ods_vlms_mdac32";
    public static final String ODS_VLMS_SPTB01C = "ods_vlms_sptb01c";
    public static final String ODS_VLMS_SPTI32 = "ods_vlms_spti32";
    public static final String ODS_VLMS_MDAC01 = "ods_vlms_mdac01";
    public static final String ODS_VLMS_MDAC12 = "ods_vlms_mdac12";
    public static final String ODS_VLMS_MDAC12_GROUP = "ods_vlms_mdac12_group";
    public static final String ODS_VLMS_MDAC10 = "ods_vlms_mdac10";
    public static final String ODS_VLMS_MDAC10_GROUP = "ods_vlms_mdac10_group";
    public static final String ODS_VLMS_MDAC11 = "ods_vlms_mdac11";
    public static final String ODS_VLMS_MDAC14 = "ods_vlms_mdac14";
    public static final String ODS_VLMS_MDAC22 = "ods_vlms_mdac22";
    public static final String ODS_VLMS_SPTB02_RAILSEA_RK = "ods_vlms_sptb02_railsea_rk";
    public static final String ODS_VLMS_SPTB_XL01 = "ods_vlms_sptb_xl01";
    public static final String ODS_VLMS_SPTI15 = "ods_vlms_spti15";
    public static final String ODS_VLMS_SPTI30 = "ods_vlms_spti30";
    public static final String ODS_VLMS_SYSC07 = "ods_vlms_sysc07";
    public static final String ODS_VLMS_SYSC08 = "ods_vlms_sysc08";
    public static final String ODS_VLMS_SYSC09 = "ods_vlms_sysc09";
    public static final String ODS_VLMS_LC_SPEC_CONFIG = "ods_vlms_lc_spec_config";
    public static final String ODS_VLMS_SPTB_XL01D = "ods_vlms_sptb_xl01d";
    public static final String ODS_VLMS_MDAC33 = "ods_vlms_mdac33";
    public static final String ODS_VLMS_SITE_WAREHOUSE = "ods_vlms_site_warehouse";
    public static final String ODS_VLMS_SPTC03 = "ods_vlms_sptc03";
    public static final String ODS_VLMS_SPTI32_RAIL_SEA = "ods_vlms_spti32_rail_sea";
    public static final String ODS_VLMS_SPTB22_DQ = "ods_vlms_sptb22_dq";
    public static final String ODS_VLMS_SPTB23_DQ = "ods_vlms_sptb23_dq";
    public static final String ODS_VLMS_SPTB23_DQ_2020 = "ods_vlms_sptb23_dq_2020";
    public static final String ODS_VLMS_SPTB02_STD_IMPORT = "ods_vlms_sptb02_std_import";
    public static final String ODS_VLMS_SPTB013 = "ods_vlms_sptb013";
    public static final String ODS_VLMS_BASE_STATION_DATA = "ods_vlms_base_station_data";
    public static final String ODS_VLMS_BASE_STATION_DATA_LATEST_0701 = "ods_vlms_base_station_data_latest_0701";
    public static final String ODS_VLMS_BASE_STATION_DATA_EPC = "ods_vlms_base_station_data_epc";
    public static final String ODS_VLMS_BASE_STATION_DATA_EPC_LATEST_0701 = "ods_vlms_base_station_data_epc_latest_0701";
    public static final String ODS_VLMS_BASE_STATION_DATA_EPC_LATEST_0701_GROUP = "ods_vlms_base_station_data_epc_latest_0701_group";
    public static final String ODS_VLMS_SPTI32_DZ = "ods_vlms_spti32_dz";
    public static final String ODS_VLMS_RFID_WAREHOUSE = "ods_vlms_rfid_warehouse";

    //xiaofeng自建   cdc kafka topic name
    public static final String ORACLE_TOPIC_SYSC07 = "ORACLE_TOPIC_SYSC07";
    public static final String ORACLE_TOPIC_SYSC08 = "ORACLE_TOPIC_SYSC08";
    public static final String ORACLE_TOPIC_SYSC09 = "ORACLE_TOPIC_SYSC09";
    public static final String ORACLE_TOPIC_MDAC01 = "ORACLE_TOPIC_MDAC01";


    //dim topic
    //
    public static final String DIM_VLMS_PROVINCES = "dim_vlms_provinces";
    public static final String DIM_VLMS_WAREHOUSE_RS = "dim_vlms_warehouse_rs";
    public static final String DIM_VLMS_MDAC1210 = "dim_vlms_mdac1210";;


    //dwd topic
    public static final String DWD_VLMS_SPTB02 = "dwd_vlms_sptb02";
    public static final String DWD_VLMS_BASE_STATION_DATA = "dwd_vlms_base_station_data";
    public static final String DWD_VLMS_BASE_STATION_DATA_EPC = "dwd_vlms_base_station_data_epc";


    //dwm topic
    public static final String DWM_VLMS_SPTB02 = "dwm_vlms_sptb02";
    public static final String DWM_VLMS_SPTB02Test = "dwm_vlms_sptb02Test";
    public static final String DWM_VLMS_SPTB02_TEST1 = "dwm_vlms_sptb02_test1";

    public static final String DWM_VLMS_ONE_ORDER_TO_END ="dwm_vlms_one_order_to_end";


    //dws topic

    // test
    public static final String XIAO_FENG_TEST_22_DQ = "XIAO_FENG_TEST_22_DQ";


    public static final String ORACLE_SPTB02 = "oracle_sptb02";
    public static final String BASE_STATION_DATA = "base_station_data";
    public static final String BASE_STATION_DATA_EPC = "base_station_data_epc";
}
