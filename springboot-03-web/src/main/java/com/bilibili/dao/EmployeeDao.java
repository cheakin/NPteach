package com.bilibili.dao;

import com.bilibili.pojo.Department;
import com.bilibili.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//员工
@Repository
public class EmployeeDao {
    //模式数据
    private static Map<Integer, Employee> employees = null;
    //员工有所属的部门
    @Autowired
    private DepartmentDao departmentDao;

    static {
        employees = new HashMap<>();//创建一个员工表

        employees.put(1001,new Employee(1001,"AA","a123@test.com",0,new Department(101,"教学部")));
        employees.put(1002,new Employee(1002,"AB","b123@test.com",1,new Department(102,"教研部")));
        employees.put(1003,new Employee(1003,"AC","c123@test.com",0,new Department(103,"运营部")));
        employees.put(1004,new Employee(1004,"AD","d123@test.com",1,new Department(104,"市场部")));
        employees.put(1005,new Employee(1005,"AE","3123@test.com",0,new Department(105,"后勤部")));
    }

    //主键自增
    private static Integer initId = 1006;

    //增
    public void addEmployee(Employee employee){
        if (employee.getId()==null){
            employee.setId(initId);
        }

        employee.setDepartment(departmentDao.getDepartmentById(employee.getDepartment().getId()));

        employees.put(employee.getId(),employee);
    }

    //查询所有员工
    public Collection<Employee> getAll(){
        return employees.values();
    }

    //通过ID查询员工
    public Employee getEmployeeById(Integer id){
        return employees.get(id);
    }

    //删除员工
    public void deleteEmployee(Integer id){
        employees.remove(id);
    }
}
