package com.bilibili.dao;

import com.bilibili.pojo.Department;
import org.omg.CORBA.MARSHAL;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DepartmentDao {

    //模拟数据
    private static Map<Integer, Department> departments = null;

    static {
        departments = new HashMap<>();//创建一个部门表

        departments.put(101,new Department(101, "教学部"));
        departments.put(102,new Department(102, "教研部"));
        departments.put(103,new Department(103, "运营部"));
        departments.put(104,new Department(104, "市场部"));
        departments.put(105,new Department(105, "后勤部"));
    }

    //获取所有部门
    public Collection<Department> getDepartments(){
        return departments.values();
    }

    //通过ID获取部门
    public Department getDepartmentById(Integer id){
        return departments.get(id);
    }



}
