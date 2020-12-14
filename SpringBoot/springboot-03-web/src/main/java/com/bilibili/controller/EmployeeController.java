package com.bilibili.controller;

import com.bilibili.dao.DepartmentDao;
import com.bilibili.dao.EmployeeDao;
import com.bilibili.pojo.Department;
import com.bilibili.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class EmployeeController {

    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    DepartmentDao departmentDao;

    @RequestMapping("/emps")
    public String list(Model model){
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps",employees);
        return "emp/list";
    }

    /**
     * 以get的方式请求，跳转添加页面。与addEmp()方法成restful风格
     * @return
     */
    @GetMapping("/emp")
    public String toAddPage(Model model){
        System.out.println("-----toAddPage------");
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("departments",departments);
        return "emp/add";
    }

    /**
     * 以post的方式请求，返回到list页面。与toAddPage()方法成restful风格
     * @return
     */
    @PostMapping("/emp")
    public String addEmp(Employee employee){
        System.out.println("-----addEMP-----");
        employeeDao.addEmployee(employee);  //调用底层业务方法保存员工信息
        return "redirect:emps";
    }


    @GetMapping("/emp/{id}")
    public String getEmp(@PathVariable Integer id, Model model){
        Employee employee = employeeDao.getEmployeeById(id);
        model.addAttribute("emp", employee);
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("departments",departments);
        return "emp/edit";
    }

    @PostMapping("/update")
    public String saveEmp(Employee employee){
        employeeDao.addEmployee(employee);
        return "redirect:emps";
    }

    @GetMapping("/delEmp/{id}")
    public String deleteEmp(@PathVariable Integer id){
        employeeDao.deleteEmployee(id);
        return "redirect:emps";
    }
}
