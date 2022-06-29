package com.itheima.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * activiti-Uel部署
 * Create by CK on 2021/02/28.
 **/
public class TestAssigneeUel {

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
                .name("出差申请流程-uel")
                .addClasspathResource("bpmn/evection-uel.bpmn")
                .deploy();
        System.out.println("流程部署-id = " + deploy.getId());    //4
        System.out.println("流程部署-name = " + deploy.getName());    //4
    }

    /**
     * 启动流程
     *  1.创建流程引擎
     *  2.获取RuntimeService
     *  3.设定assignee的值，替换uel表达式
     *  4.启动流程实例
     *  5.输出
     */
    @Test
    public void startAssigneeUel() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        HashMap<String, Object> assigneeMap = new HashMap<>();
        assigneeMap.put("assignee0", "张三");
        assigneeMap.put("assignee1", "李经理");
        assigneeMap.put("assignee2", "王总经理");
        assigneeMap.put("assignee3", "赵财务");
        runtimeService.startProcessInstanceByKey("evection1", assigneeMap);
    }
}
