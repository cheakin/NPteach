package com.biibili.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * department
 * @author 
 */
@Data
public class Department implements Serializable {
    private Integer id;

    private String departmentName;

    private static final long serialVersionUID = 1L;
}