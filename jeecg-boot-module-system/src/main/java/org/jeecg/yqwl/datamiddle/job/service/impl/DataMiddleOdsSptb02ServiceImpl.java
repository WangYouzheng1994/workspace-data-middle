package org.jeecg.yqwl.datamiddle.job.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.commons.collections.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.TableParams;
import org.jeecg.yqwl.datamiddle.job.mapper.DataMiddleOdsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleOdsSptb02Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/6/13 15:40
 * @Version: V1.0
 */
@Slf4j
@Service
public class DataMiddleOdsSptb02ServiceImpl implements DataMiddleOdsSptb02Service {
    @Resource
    private DataMiddleOdsSptb02Mapper dataMiddleOdsSptb02Mapper;

    /**
     * 定时查询Ods_sptb02数据
     */
    @DS("wareHouse")
    @Override
    public void getOdsVlmsSptb02() {
        log.info("开始查询ods_sptb02数据");

        //todo:15天以前到现在
        String begin = DateUtils.getToday4Dawn(); //2022-06-13 00:00:00
        String end = DateUtils.getToday4Night();  //2022-06-13 23:59:59
        boolean hasNext = true;
        Integer limit = 500;
        Integer rowNum=0;
        int interval = 1;
        do {
            log.info("开始循环, {}", interval++);
            List<Sptb02> rfidHandleList = this.dataMiddleOdsSptb02Mapper.getOdsVlmsSptb02(rowNum, begin, end, rowNum,  limit);

            log.info("开始组装数据进行维表拉宽");
        //-------------------------------------step1:自己的参数合自己开始---------------------------------------------------------------------------------------------//
            for (Sptb02 sptb02 : rfidHandleList) {
                DwdSptb02 dwdSptb02 = new DwdSptb02();
                BeanUtil.copyProperties(sptb02, dwdSptb02);
                log.info("Sptb02属性复制到DwdSptb02后数据:{}", dwdSptb02);
                //获取原数据的运输方式
                String vysfs = sptb02.getVYSFS();
                //System.out.println("运输方式：" + vysfs);
                log.info("运单运输方式数据:{}", vysfs);
                if (StringUtils.isNotEmpty(vysfs)) {
                    //1.处理 运输方式 ('J','TD','SD','G')='G'   (''L1'','T') ='T'    ('S') ='S'
                    //('J','TD','SD','G')='G'
                    if (vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) {
                        dwdSptb02.setTRAFFIC_TYPE("G");
                        //运输方式 适配 lc_spec_config
                        dwdSptb02.setTRANS_MODE_CODE("1");
                    }
                    //(''L1'','T') ='T'
                    if (vysfs.equals("L1") || vysfs.equals("T")) {
                        dwdSptb02.setTRAFFIC_TYPE("T");
                        dwdSptb02.setTRANS_MODE_CODE("2");
                    }
                    //('S') ='S'
                    if (vysfs.equals("S")) {
                        dwdSptb02.setTRAFFIC_TYPE("S");
                        dwdSptb02.setTRANS_MODE_CODE("3");
                    }
                    //2.处理 起运时间
                    //公路取sptb02.dtvscfsj，其他取sptb02取DSJCFSJ(实际离长时间)的值，实际起运时间， 实际出发时间
                    if ((vysfs.equals("J") || vysfs.equals("TD") || vysfs.equals("SD") || vysfs.equals("G")) && Objects.nonNull(sptb02.getDTVSCFSJ())) {
                        dwdSptb02.setSHIPMENT_TIME(sptb02.getDTVSCFSJ());
                    }
                    if ((vysfs.equals("L1") || vysfs.equals("T") || vysfs.equals("S")) && Objects.nonNull(sptb02.getDTVSCFSJ())) {
                        dwdSptb02.setSHIPMENT_TIME(sptb02.getDTVSCFSJ());
                    }
                }
                //3.处理 计划下达时间
                if (Objects.nonNull(sptb02.getDPZRQ())) {
                    dwdSptb02.setPLAN_RELEASE_TIME(sptb02.getDPZRQ());
                }
                //4.处理 运单指派时间
                if (Objects.nonNull(sptb02.getDYSSZPSJ())) {
                    dwdSptb02.setASSIGN_TIME(sptb02.getDYSSZPSJ());
                }
                //5.处理 打点到货时间
                if (Objects.nonNull(sptb02.getDGPSDHSJ())) {
                    dwdSptb02.setDOT_SITE_TIME(sptb02.getDGPSDHSJ());
                }
                //6.处理 最终到货时间
                if (Objects.nonNull(sptb02.getDDHSJ())) {
                    dwdSptb02.setFINAL_SITE_TIME(sptb02.getDDHSJ());
                }
                //7.处理 运单生成时间
                if (Objects.nonNull(sptb02.getDDJRQ())) {
                    dwdSptb02.setORDER_CREATE_TIME(sptb02.getDDJRQ());
                }
                //8.处理 基地代码 适配 lc_spec_config
                String cqwh = sptb02.getCQWH();
                if (Objects.nonNull(cqwh)) {
                    /**
                     * 0431、 -> 1  长春基地
                     * 022、  -> 5  天津基地
                     * 027、
                     * 028、  -> 2  成都基地
                     * 0757   -> 3  佛山基地
                     */
                    if ("0431".equals(cqwh)) {
                        dwdSptb02.setBASE_CODE("1");
                    }
                    if ("022".equals(cqwh)) {
                        dwdSptb02.setBASE_CODE("5");
                    }
                    if ("028".equals(cqwh)) {
                        dwdSptb02.setBASE_CODE("2");
                    }
                    if ("0757".equals(cqwh)) {
                        dwdSptb02.setBASE_CODE("3");
                    }
                }
                //9.处理 主机公司代码
                /**
                 * 主机公司代码 适配 lc_spec_config
                 *   1  一汽大众
                 *   2  一汽红旗
                 *   3  一汽马自达
                 */
                //获取主机公司代码
                //sptb02中原字段值含义： '大众','1','红旗','17','奔腾','2','马自达','29'
                String czjgsdm = sptb02.getCZJGSDM();
                if (StringUtils.isNotEmpty(czjgsdm)) {
                    if ("1".equals(czjgsdm)) {
                        dwdSptb02.setHOST_COM_CODE("1");
                    }
                    if ("17".equals(czjgsdm)) {
                        dwdSptb02.setHOST_COM_CODE("2");
                    }
                    if ("29".equals(czjgsdm)) {
                        dwdSptb02.setHOST_COM_CODE("3");
                    }
                }
                //添加新的处理逻辑(新加)
                //10.处理 ACTUAL_OUT_TIME(实际出库时间)  取 sptb02.dckrq字段
                if (Objects.nonNull(sptb02.getDCKRQ())) {
                    dwdSptb02.setACTUAL_OUT_TIME(sptb02.getDCKRQ());
                }

                //对保存的数据为null的填充默认值
                //DwdSptb02 bean = JsonPartUtil.getBean(dwdSptb02);
                //实际保存的值为after里的值
                log.info("处理完的数据填充后的值:" + dwdSptb02.toString());
    //-------------------------------------step1:自己的参数合自己开始---------------------------------------------------------------------------------------------//
    //-------------------------------------step2:合并维表开始----------------------------------------------------------------------------------------------------//
                /**
                 * 处理 经销商到货地 省区代码 市县代码
                 *  inner join mdac32 e on a.cdhddm = e.cdhddm
                 *  inner join v_sys_sysc07sysc08 v2 on e.csqdm = v2.csqdm and e.csxdm = v2.csxdm
                 */
                TableParams tableParams = new TableParams();
                tableParams.setTableName("ods_vlms_mdac32");
                tableParams.setField("CDHDDM");
                String cdhddm = sptb02.getCDHDDM();
                if (StringUtils.isNotBlank(cdhddm)){
                    tableParams.setValues(cdhddm);
                    String csqdm ="";
                    String csxdm="";
                    Map tablValues = this.dataMiddleOdsSptb02Mapper.getTablValues(tableParams);
                    if (tablValues !=null){
                        csqdm= (String) tablValues.get("CSQDM");
                        csxdm = (String) tablValues.get("CSXDM");
                    if (StringUtils.isNotBlank(csqdm)){
                        dwdSptb02.setEND_PROVINCE_CODE(csqdm);
                    }
                    if (StringUtils.isNotBlank(csxdm)){
                        dwdSptb02.setEND_CITY_CODE(csxdm);
                    }
                    }
                }

                /**
                 *  处理 发车站台 对应的仓库代码 仓库名称
                 *  from sptb02 a
                 *  inner join sptb02d1 b    on a.cjsdbh = b.cjsdbh
                 *  left join site_warehouse c    on a.vfczt = c.vwlckdm and c.type =  'CONTRAST'
                 */
                tableParams.setTableName("ods_vlms_site_warehouse");
                tableParams.setField("VWLCKDM");
                String vfczt = sptb02.getVFCZT();
                if (StringUtils.isNotBlank(vfczt)){
                    tableParams.setValues(vfczt);
                    tableParams.setType("TYPE");
                    tableParams.setTypeValues("CONTRAST");
                    String startWareHouseCode ="";
                    String startWareHouseName="";
                    Map tablValues = this.dataMiddleOdsSptb02Mapper.getTablValues(tableParams);
                    if (tablValues !=null){
                        startWareHouseCode= (String) tablValues.get("WAREHOUSE_CODE");
                        startWareHouseName = (String) tablValues.get("WAREHOUSE_NAME");
                        if (StringUtils.isNotBlank(startWareHouseCode)){
                            dwdSptb02.setSTART_WAREHOUSE_CODE(startWareHouseCode);
                        }
                        if (StringUtils.isNotBlank(startWareHouseName)){
                            dwdSptb02.setSTART_WAREHOUSE_NAME(startWareHouseName);
                        }
                    }
                }

                /**
                 *  处理 收车站台 对应的仓库代码 仓库名称
                 *  from sptb02 a
                 *  inner join sptb02d1 b    on a.cjsdbh = b.cjsdbh
                 *  left join site_warehouse c    on a.vfczt = c.vwlckdm and c.type =  'CONTRAST'
                 */
                tableParams.setTableName("ods_vlms_site_warehouse");
                tableParams.setField("VWLCKDM");
                String vsczt = sptb02.getVSCZT();
                if (StringUtils.isNotBlank(vsczt)){
                    tableParams.setValues(vsczt);
                    tableParams.setType("TYPE");
                    tableParams.setTypeValues("CONTRAST");
                    String endWareHouseCode ="";
                    String endWareHouseName="";
                    Map tablValues = this.dataMiddleOdsSptb02Mapper.getTablValues(tableParams);
                    if (tablValues !=null){
                        endWareHouseCode= (String) tablValues.get("WAREHOUSE_CODE");
                        endWareHouseName = (String) tablValues.get("WAREHOUSE_NAME");
                        if (StringUtils.isNotBlank(endWareHouseCode)){
                            dwdSptb02.setEND_WAREHOUSE_CODE(endWareHouseCode);
                        }
                        if (StringUtils.isNotBlank(endWareHouseName)){
                            dwdSptb02.setEND_WAREHOUSE_NAME(endWareHouseName);
                        }
                    }

                }

                /**
                 *  处理 公路单的对应的物理仓库代码对应的类型
                 *  left join site_warehouse c    on a.vfczt = c.vwlckdm and c.type =  'CONTRAST'
                 */
                tableParams.setTableName("dim_vlms_warehouse_rs");
                tableParams.setField("VWLCKDM");
                tableParams.setType(null);
                tableParams.setTypeValues(null);
                String vwlckdm = sptb02.getVWLCKDM();
                String HIGHWAY_WAREHOUSE_TYPE="";
                if (StringUtils.isNotBlank(vwlckdm)){
                    tableParams.setValues(vwlckdm);
                    Map tablValues = this.dataMiddleOdsSptb02Mapper.getTablValues(tableParams);
                    if (tablValues !=null){
                        HIGHWAY_WAREHOUSE_TYPE=(String) tablValues.get("WAREHOUSE_TYPE");
                        if (StringUtils.isNotBlank(HIGHWAY_WAREHOUSE_TYPE)){
                            dwdSptb02.setHIGHWAY_WAREHOUSE_TYPE(HIGHWAY_WAREHOUSE_TYPE);
                        }
                    }
                    String gld = dwdSptb02.toString();
                    log.info("加了公路单的sptb02: {}",gld);
                }

            }

            if (CollectionUtils.isNotEmpty(rfidHandleList)) {

                Integer rn = rfidHandleList.get(rfidHandleList.size() - 1).getRn();
                rowNum = rn; // 记录偏移量
                if (CollectionUtils.size(rfidHandleList) != limit) {
                    hasNext = false;
                }
            } else {
                hasNext = false;
            }
            rfidHandleList = null;
        } while(hasNext);

        log.info("查询ods_sptb02数据结束");

    }
}
