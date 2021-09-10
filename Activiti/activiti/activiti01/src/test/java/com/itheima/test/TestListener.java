package com.itheima.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

/**
 * 测试监听
 * Create by CK on 2021/02/28.
 **/
public class TestListener {
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
                .name("测试监听器")
                .addClasspathResource("bpmn/demo-listen.bpmn")
                .deploy();
        System.out.println("流程部署-id = " + deploy.getId());    //4
        System.out.println("流程部署-name = " + deploy.getName());    //4
    }


}
