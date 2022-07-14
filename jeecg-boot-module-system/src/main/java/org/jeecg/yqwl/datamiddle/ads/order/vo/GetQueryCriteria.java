package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description   全节点查询从前端传过来的数据
 * @ClassName GetQueryCriteria
 * @Author YULUO
 * @Date 2022/6/6
 * * @version 1.0
 */
@Data
public class GetQueryCriteria {

    /**
     * 底盘号   one_order_to_end 表字段
     */
    private String vin;

    /**
     * 底盘号   dwm_vlms_sptb02 字段
     */
    private  String vvin;

    /**
     * 基地
     */
    private String baseName;

    /**
     * 任务单号
     */
    private String taskNo;

    /**
     * 配载单号
     */
    private String cpzdbh;

    /**
     * 出厂日期_开始
     */
    private Long leaveFactoryTimeStart;

    /**
     * 出厂日期_结束
     */
    private Long leaveFactoryTimeEnd;

    /**
     * 始发城市
     */
    private String startCityName;

    /**
     * 目标城市
     */
    private String endCityName;

    /**
     * 入库日期_开始
     */
    private Long inSiteTimeStart;

    /**
     * 入库日期_结束
     */
    private Long inSiteTimeEnd;

    /**
     * cp9下线接车日期_开始
     */
    private Long cp9OfflineTimeStart;

    /**
     * cp9下线接车日期_结束
     */
    private Long cp9OfflineTimeEnd;

    /**
     * 分页：页码
     */
    private Integer pageNo = 1;

    /**
     * 分页：每页大小
     */
    private Integer pageSize = 20;

    /**
     * one_order_to_end vin列表
     */
    private List<String> vinList;

    /**
     * sptb02 vvin列表
     */
    private List<String> vvinList;

    private Integer limitStart;

    private Integer limitEnd;

    /**
     * 过滤条件
     */
    private String selections;

    /**
     * docs二级菜单运输类型:只要公路
     */
    private  String  vysfs;

    /**
     * DCS到货开始时间
     */
    private Long dotSiteTimeStart;

    /**
     * DCS到货结束时间
     */
    private Long dotSiteTimeEnd;

    /**
     *经销商确认到货开始时间
     */
    private Long finalSiteTimeStart;

    /**
     *经销商确认到货结束时间
     */
    private Long finalSiteTimeEnd;

    /**sptb02出库日期开始*/
    private Long actualOutTimeStart;

    /**
     * sptb02出库日期结束
     */
    private Long actualOutTimeEnd;

    /**
     *车辆标识 识别是否为新能源汽车(E6,E7)
     */
    private String ccxdl;

    /**
     * 车辆标识(E6,E7)
     */
    private List<String> ccxdlList;


}
