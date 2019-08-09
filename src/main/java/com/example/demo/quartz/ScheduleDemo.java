package com.example.demo.quartz;

import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;

/**
 * Description：调度任务测试
 * Author；JinHuatao
 * Date: 2019/8/9 11:18
 */
public class ScheduleDemo {

    /**
     *  Quartz与Spring集成
     *
     * */
    public static void demo01() throws SchedulerException {
        //step01: 使用spring-context-support-5.0.6.RELEASE.jar 中的任务调度工厂
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
//        schedulerFactory.setDataSource(dataSource);       //?
//        schedulerFactory.setAutoStartup(true);                  // 自动启动
//        schedulerFactory.setStartupDelay(20);                   // 延时启动，应用启动成功后在启动
//        schedulerFactory.setOverwriteExistingJobs(true);        // 覆盖DB中JOB：true、以数据库中已经存在的为准：false
//        schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
//        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        //step02: 创建任务调度器
        Scheduler scheduler = schedulerFactory.getScheduler();


        //-------------------------在下面任务启动之前，需要初始化任务调度工厂，然后来创建任务调度器，然后使用任务调度器执行下面操作------------------------------
        String jobName = "1";
        String jobGroup = "1";
        String cronExpression = "0/10 * * * * ?";

        // 1、job key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        // 2、valid
        if (scheduler.checkExists(triggerKey)) {
            return;    // PASS
        }

        // 3、corn trigger
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // 4、job detail
        Class<? extends Job> jobClass_ = RemoteHttpJobBean.class;   // Class.forName(jobInfo.getJobClass());
        JobDetail jobDetail = JobBuilder.newJob(jobClass_).withIdentity(jobKey).build();

        // 5、schedule job
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);

        System.out.println(date);

    }

    public static void main(String[] args) throws SchedulerException {
        demo01();
    }
}
