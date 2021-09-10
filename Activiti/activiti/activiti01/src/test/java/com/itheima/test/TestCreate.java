package com.itheima.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

/**
 * activiti创建测试
 * Create by CK on 2021/02/24.
 **/
public class TestCreate {

    /**
     * 使用activiti默认的方式创建mssql表
     */
    @Test
    public void testCreateDbTable() {
        //需要使用activiti提供的工具类ProcessEngines
        //会读取默认的配置文件,创建processEngine时就会创建表

        //默认方式
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        System.out.println("processEngine = " + processEngine);


        /**
         * 使用自定义方式创建
         *      此时就可以自定bean的名字
         */
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");
        ProcessEngine processEngine = engineConfiguration.buildProcessEngine();
        System.out.println("processEngine = " + processEngine);
    }
}
