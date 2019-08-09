package com.example.demo.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/9 13:50
 */
public class FileJob implements Job{

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("============>>>> 具体执行任务地方");
    }
}
