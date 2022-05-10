package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac32 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 到货地代码 6
     */
          
    private String cdhddm;

      /**
     * 经销商代码 10
     */
          
    private String cjxsdm;

      /**
     * 省区代码    4 
     */
          
    private String csqdm;

      /**
     * 市县代码    4
     */
          
    private String csxdm;

      /**
     * 联系人
     */
          
    private String clxr;

      /**
     * 电话
     */
          
    private String vdh;

      /**
     * 移动电话
     */
          
    private String vyddh;

      /**
     * e_mail
     */
          
    private String veMail;

      /**
     * 0、在用，1、停用
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

      /**
     * 地址 100   modify by dtf 20181113 100-200与TDS一致！
     */
          
    private String vdz;

      /**
     * 收车人
     */
          
    private String vscr;

      /**
     * 有效证件
     */
          
    private String vyxzj;

      /**
     * 证件号
     */
          
    private String vzjh;

      /**
     * 到货地名称 100
     */
          
    private String vdhdmc;

      /**
     * ID
     */
          
    private Integer id;

      /**
     * 传真
     */
          
    private String vcz;

      /**
     * ???1
     */
          
    private String clxr1;

      /**
     * ??1
     */
          
    private String vdh1;

      /**
     * ????1
     */
          
    private String vyddh1;

      /**
     * ???1
     */
          
    private String vscr1;

      /**
     * ???1
     */
          
    private String vzjh1;

        
    private String cssxt;

      /**
     * ????2
     */
          
    private String vyxzj1;

        
    private Long dczrq;

        
    private String cczydm;

      /**
     * 时间戳。BI提数据
     */
          
    private Long dstamp;

      /**
     * 20181009 add by dtf 备注
     */
          
    private String vbz;

      /**
     * 20181207 add by lwx 备注
     */
          
    private String vbz1;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
