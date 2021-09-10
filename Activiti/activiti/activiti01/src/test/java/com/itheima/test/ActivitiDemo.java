package com.itheima.test;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import sun.misc.FDBigInteger;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 流程测试
 * Create by CK on 2021/02/25.
 **/
public class ActivitiDemo {

    /**
     * 流程部署1
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
                .name("出差申请流程")
                .addClasspathResource("bpmn/evection.bpmn")
                .addClasspathResource("bpmn/evection.png")
                .deploy();
        System.out.println("流程部署-id = " + deploy.getId());    //4
        System.out.println("流程部署-name = " + deploy.getName());    //4
    }


    /**
     * 启动流程实例
     *  1.创建ProcessEngine
     *  2.获取RuntimeService
     *  3.根据流程定义的id启动流程
     *  4.输出内容
     */
    @Test
    public void testStartProcess() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); //1
        RuntimeService runtimeService = processEngine.getRuntimeService();  //2
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("evection");
        System.out.println("流程定义ID = " + instance.getProcessInstanceId());
        System.out.println("流程实例ID = " + instance.getId());
        System.out.println("当前活动ID = " + instance.getActivityId());


    }

    /**
     * 查询个人待执行的任务
     *  1.创建ProcessEngine
     *  2.获取RepositoryService
     *  3.根据条件查询
     *  4.输出
     */
    @Test
    public void testFindPersonalTaskList() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("evection")
                .list();
        System.out.println("taskList = " + taskList);

    }

    /**
     * 完成个人任务
     *  1.创建流程引擎
     *  2.获取TaskService
     *  3.根据条件完成任务
     */
    @Test
    public void completeTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
//        taskService.complete("2505");   //1)根据任务id完成

        //2)更具条件查询到任务，由此完成任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("evection")
                .singleResult();    //获取单个
        System.out.println(task);
//        taskService.complete(task.getId());

    }


    /**
     * 流程部署2，使用zip包的方式批量部署
     *  1.创建ProcessEngine
     *  2.获取RepositoryService
     *  3.使用service进行zip包流程部署，
     *  4.输出部署信息
     */
    @Test
    public void deployProcessByZip() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //使用资源创建流，以此构建zip流
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //使用zip流部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署-id = " + deploy.getId());    //4
        System.out.println("流程部署-name = " + deploy.getName());    //4
    }

    /**
     * 查询流程定义
     *  1.获取流引擎
     *  2.获取RepositoryService
     *  3.获取ProcessDefinitionQuery
     *  4.查询当前所有流程定义
     *  5.输出
     */
    @Test
    public void queryProcessDefinition() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> definitionList = definitionQuery.processDefinitionKey("evection")    //查找evection
                .orderByProcessDefinitionVersion()  //对结果排序
                .desc()    //倒叙
                .list();    //数组（可能是多个）
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义-ID = " + processDefinition.getId());
            System.out.println("流程定义-NAME = " + processDefinition.getName());
            System.out.println("流程定义-KEY = " + processDefinition.getKey());
            System.out.println("流程定义-VERSION = " + processDefinition.getVersion());
            System.out.println("流程部署-ID = " + processDefinition.getDeploymentId());
        }
    }


    /**
     * 流程定义删除
     *  1.获取流引擎
     *  2.获取RepositoryService
     *  3.通过部署ID删除部署流程
     *      当前流程如果存存在未完成实例，则会删除失败
     *      此时需要特殊的删除方式-》级联删除
     */
    @Test
    public void deleDeployMent() {
        String deploymentId = "40001";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        repositoryService.deleteDeployment(deploymentId);   //一般删除方式
        repositoryService.deleteDeployment(deploymentId, true);     //特殊删除，是否开启级联删除
    }

    /**
     * 删除资源文件（commons-io.jar）
     *  1.获取流引擎
     *  2.获取RepositoryService
     *  3.获取查询对象ProcessDefinitionQuery
     *  4.获取部署ID
     *  5.获取资源信息构造成流
     *  6.输出
     */
    public void getDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evection")
                .singleResult();
        String deploymentId = processDefinition.getDeploymentId();

        //获取资源流
        String pngName = processDefinition.getResourceName();   //图片资源名
        InputStream pngStream = repositoryService.getResourceAsStream(deploymentId, pngName); //构造png流
        String bpmnName = processDefinition.getResourceName();   //图片资源名
        InputStream bpmnStream = repositoryService.getResourceAsStream(deploymentId, bpmnName); //构造bpmn流

        //未完待续。。。。。P36
    }

    /**
     * 查看历史信息
     *  1.获取流引擎
     *  2.获取HistoryService
     *  3.获取查询对象HistoricActivityInstanceQuery
     *  4.输出查询
     */
    @Test
    public void findHistoryInfo() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.processInstanceId("5001") //以id查询actinst表
                .orderByHistoricActivityInstanceStartTime() //根据开始时间排序
                .asc()  //升序
                .list();
        for (HistoricActivityInstance hi : activityInstanceList) {
            System.out.println("历史操作活动ID = " + hi.getActivityId());
            System.out.println("历史操作活动NAME = " + hi.getActivityName());
            System.out.println("历史操作定义ID = " + hi.getProcessDefinitionId());
            System.out.println("历史操作实例ID = " + hi.getProcessInstanceId());
            System.out.println("hi ============================= ");
        }
    }


}
