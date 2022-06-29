package com.itheima.demo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 工作流程监听器
 * Create by CK on 2021/02/28.
 **/
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //判当前任务是 创建申请 且是 create 事件
        if ("创建申请".equals(delegateTask.getName()) && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("张三");
        }
    }
}
