package com.itheima.demo.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程变量
 * Create by CK on 2021/02/28.
 **/
@Data
public class Evection implements Serializable {
    /** 主键 */
    private Long id;

    /** 出差单名字 */
    private String evectionName;

    /** 主键 */
    private Double num;

    /** 开始时间 */
    private Date startDate;

    /** 结束时间 */
    private Date endDate;

    /** 目的地 */
    private String destination;

    /** 原因 */
    private String reson;



}
