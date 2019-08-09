package com.example.demo.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/8/9 13:30
 */
@Controller
public class SchController {

    @Autowired
    private Scheduler scheduler;

    @RequestMapping("/start")
    public String start(@RequestParam String name, @RequestParam String group){
        //-------------------------在下面任务启动之前，需要初始化任务调度工厂，然后来创建任务调度器，然后使用任务调度器执行下面操作------------------------------
        String jobName = name;
        String jobGroup = group;
        String cronExpression = "0/1 * * * * ?";

        // 1、job key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        // 2、valid
        try {
            if (scheduler.checkExists(triggerKey)) {
                return "";    // PASS
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        // 3、corn trigger
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // 4、job detail
        Class<? extends Job> jobClass_ = RemoteHttpJobBean.class;   // Class.forName(jobInfo.getJobClass());
        //每10s调用FileJob的executor一次
        JobDetail jobDetail = JobBuilder.newJob(jobClass_).withIdentity(jobKey).build();

        // 5、schedule job
        Date date = null;
        try {
            date = scheduler.scheduleJob(jobDetail, cronTrigger);//新建任务
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        System.out.println(date);

        return "";
    }

    @RequestMapping("/pause")
    public String pause(@RequestParam String name, @RequestParam String group){
        String jobName = name;
        String jobGroup = group;

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.pauseTrigger(triggerKey);//新建任务
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping("/resume")
    public String resume(@RequestParam String name, @RequestParam String group){
        String jobName = name;
        String jobGroup = group;

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.resumeTrigger(triggerKey);//重新开始任务
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping("/remove")
    public String remove(@RequestParam String name, @RequestParam String group){
        String jobName = name;
        String jobGroup = group;

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);//移除任务
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return "";
    }

}
