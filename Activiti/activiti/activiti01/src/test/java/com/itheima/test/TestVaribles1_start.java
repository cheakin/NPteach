package com.itheima.test;

import com.itheima.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;

/**
 * 测试流程变量
 * Create by CK on 2021/02/28.
 **/
public class TestVaribles1_start {

    /**
     * 流程部署
     *  1.创建ProcessEngine
     *  2.获取RepositoryService
     *  3.使用service进行流程部署，bpmn和png部署到数据中
     *  4.输出部署信息
     */
    @Test
    public void testDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); //1
        RepositoryService repositoryService = processEngine.getRepositoryService(); //2
        Deployment deploy = repositoryService.createDeployment()    //3
                .name("出差申请流程-varible")
                .addClasspathResource("bpmn/evection-varible.bpmn")
                .deploy();
        System.out.println("流程部署-id = " + deploy.getId());    //4
        System.out.println("流程部署-name = " + deploy.getName());    //4
    }

    /**
     * 启动流程
     *  1.创建流程引擎
     *  2.获取RuntimeService
     *  3.通过流程定义key设置流程变量，包括流程变量和负责人
     *  4.启动流程实例
     */
    @Test
    public void testStartProcess() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); //1
        RuntimeService runtimeService = processEngine.getRuntimeService();      //2

        //流程定义key
        String key = "evection-varible";
        //流程变量map
        HashMap<String, Object> varibls = new HashMap<>();

        //设置流程变量    //3.1
        Evection evection = new Evection();
        evection.setNum(3d);    //出差日期-分支
        varibls.put("evection", evection);  //放入map,出差信息

        //设置任务负责人   //3.2
        varibls.put("assignee0", "张三");
        varibls.put("assignee1", "李经理");
        varibls.put("assignee2", "王总经理");
        varibls.put("assignee3", "赵财务");

        //启动流程  //4
        runtimeService.startProcessInstanceByKey(key, varibls);
        System.out.println(key+"流程已启动");
    }

    /**
     * 完成任务
     *  1.创建流程引擎
     *  2.获取TaskService
     *  3.根据条件查询任务
     *  4.完成查询到的任务
     */
    @Test
    public void completTask() {
        String assignee = "张三";
//        String assignee = "李经理";
//        String assignee = "王总经理";
//        String assignee = "赵财务";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); //1
        TaskService taskService = processEngine.getTaskService();   //2
        Task task = taskService.createTaskQuery()   //3
                .processDefinitionKey("evection-varible")
                .taskAssignee(assignee)
                .singleResult();    //获取单个
        System.out.println("task = " + task);
        if (task != null) {
//            taskService.complete(task.getId());     //4
            System.out.println(task.getId()+"任务已完成");
        }

        /*List<Task> list = taskService.createTaskQuery()   //3
                .processDefinitionKey("evection-varible")
                .list();
        for (Task task : list) {
            System.out.println("task = " + task);
            taskService.complete(task.getId());
        }*/
    }

}
