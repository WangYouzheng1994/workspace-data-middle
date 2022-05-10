package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 销售大区。销售代表处属于销售组织，销售大区属于销售区域，所以销售大区不属于部门级次中。	但大区与销售代表处有对照关系，主要用于查询
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac01 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 大区代码
     */
          
    private String cdqdm;

      /**
     * 大区名称
     */
          
    private String vdqmc;

      /**
     * 大区联系人
     */
          
    private String clxrdm;

      /**
     * 大区联系电话
     */
          
    private String vdh;

      /**
     * 大区负责人
     */
          
    private String cfzrdm;

      /**
     * 省区代码
     */
          
    private String csqdm;

      /**
     * 市县代码
     */
          
    private String csxdm;

      /**
     * 地址
     */
          
    private String vdz;

      /**
     * 传真
     */
          
    private String vcz;

      /**
     * e_mail
     */
          
    private String veMail;

      /**
     * 邮编
     */
          
    private String cyb;

      /**
     * 没有可录入项，但需要特别说明的信息
     */
          
    private String vbz;

      /**
     * 0、在用，1、停用
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
