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
     * 底盘号
     */
    private String vin;

    /**
     * 基地
     */
    private String baseName;

    /**
     * 任务单号
     */
    private String taskNo;

    /**
     * 配板单号
     */
    private String stowageNoteNo;

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

    private List<String> vinList;

    /**
     * 导出选择的vin集合
     */
    private List<String> selectionsList;
}
