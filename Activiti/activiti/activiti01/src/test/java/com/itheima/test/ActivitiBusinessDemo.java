package com.itheima.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * 业务关联测试
 * Create by CK on 2021/02/28.
 **/
public class ActivitiBusinessDemo {

    /**
     * 添加关联业务key 到activiti表中
     *  1.创建ProcessEngine
     *  2.获取RuntimeService
     *  4.通过实例key与businessKey关联
     */
    @Test
    public void addBusinessKey() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("evection", "1001");    //1001为自定义的businessKey
        System.out.println("实例BusinessKey = " + instance.getBusinessKey());
    }

    /**
     * 流程实例的挂起和激活（多个）
     *  1.获取流程引擎
     *  2.获取RepositoryService
     *  3.获取查询对象并设置查询条件
     *  4.获取当前流程定义的实例是否是挂起状态
     *  5.获取流程定义的id
     *  6.修改计划状态
     *      如果是挂起则激活
     *      如果激活则挂起
     */
    @Test
    public void suspendAllProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evection")
                .singleResult();
        boolean suspended = processDefinition.isSuspended();    //4
        String definitionId = processDefinition.getId();
        if (suspended) {    //6
            repositoryService.activateProcessDefinitionById(
                    definitionId,   //流程定义的ID
                    true,   //是否激活
                    null);  //激活时间
            System.out.println("流程定义ID = " + definitionId + "已激活");
        }else {
            repositoryService.suspendProcessDefinitionById(
                    definitionId,   //流程定义的ID
                    true,   //是否挂起
                    null);  ////激活时间
            System.out.println("流程定义ID = " + definitionId + "已挂起");
        }
    }

    /**
     * 流程实例的挂起和激活（多个）
     *  1.获取流程引擎
     *  2.获取RuntimeService
     *  3.获取当前流程实例对象
     *  4.获取当前流程实例挂起状态
     *  5.获取流程定义的id
     *  6.修改计划状态
     *      如果是挂起则激活
     *      如果激活则挂起
     */
    @Test
    public void suspendSingleProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("5001")  //测试，手工从数据库查
                .singleResult();
        boolean suspended = instance.isSuspended();    //4
        String instanceId = instance.getId();
        if (suspended) {    //6
            runtimeService.activateProcessInstanceById(instanceId);
            System.out.println("流程定义ID = " + instanceId + "已激活");
        }else {
            runtimeService.suspendProcessInstanceById(instanceId);
            System.out.println("流程定义ID = " + instanceId + "已挂起");
        }
    }

    /**
     * 完成个人任务
     *  1.获取流程引擎
     *  2.获取RuntimeService
     *  3.使用taskService获取任务
     *  4.根据任务ID完成任务
     *
     */
    @Test
    public void completTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("5001")  //测试，手工从数据库查
                .singleResult();
        taskService.complete(task.getId()); //如果是挂起状态，则无法完成   4
    }
}
