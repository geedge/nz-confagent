package com.nis.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ConfagentJobConfig {
	
	@Value("${confagent.jobCron}")
    private String confagentJobCron;
	
	@Bean
    public JobDetail confagentCronJobDetail() {
        return JobBuilder.newJob(ConfagentJob.class).withIdentity("confagentJob").storeDurably().build();
    }

    @Bean
    public Trigger confagentCronJobTrigger() {
    	
    	if(confagentJobCron.isEmpty()) {
    		//如果配置文件没有配置定时周期 默认为15秒执行一次
    		confagentJobCron="0/30 * * * * ?";
    	}
    	
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(confagentJobCron);

        return TriggerBuilder.newTrigger()
                .forJob(confagentCronJobDetail())
                .withSchedule(cronScheduleBuilder)
                .build();
    }
    
    @Bean
    public JobDetail confagentUptimeCronJobDetail() {
        return JobBuilder.newJob(ConfagentUptimeJob.class).withIdentity("confagentUptimeJob").storeDurably().build();
    }

    @Bean
    public Trigger confagentUptimeCronJobTrigger() {
    	
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/1 * * * * ?");

        return TriggerBuilder.newTrigger()
                .forJob(confagentUptimeCronJobDetail())
                .withSchedule(cronScheduleBuilder)
                .build();
    }
    
}
