package org.jeecg.yqwl.datamiddle.config.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: table_process
 * @Author: jeecg-boot
 * @Date:   2022-05-07
 * @Version: V1.0
 */
@Data
@TableName("table_process")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="table_process对象", description="table_process")
public class TableProcess implements Serializable {
    private static final long serialVersionUID = 1L;

	/**来源表*/
	@Excel(name = "来源表", width = 15)
    @ApiModelProperty(value = "来源表")
    private String sourceTable;
	/**操作类型 insert,update,delete*/
	@Excel(name = "操作类型 insert,update,delete", width = 15)
    @ApiModelProperty(value = "操作类型 insert,update,delete")
    private String operateType;
	/**输出类型 hbase kafka*/
	@Excel(name = "输出类型 hbase kafka", width = 15)
    @ApiModelProperty(value = "输出类型 hbase kafka")
    private String sinkType;
	/**输出表(主题)*/
	@Excel(name = "输出表(主题)", width = 15)
    @ApiModelProperty(value = "输出表(主题)")
    private String sinkTable;
	/**输出字段*/
	@Excel(name = "输出字段", width = 15)
    @ApiModelProperty(value = "输出字段")
    private String sinkColumns;
	/**主键字段*/
	@Excel(name = "主键字段", width = 15)
    @ApiModelProperty(value = "主键字段")
    private String sinkPk;
	/**建表扩展*/
	@Excel(name = "建表扩展", width = 15)
    @ApiModelProperty(value = "建表扩展")
    private String sinkExtend;
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Integer id;
}
