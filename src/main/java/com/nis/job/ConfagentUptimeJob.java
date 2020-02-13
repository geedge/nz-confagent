package com.nis.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nis.entity.ConfagentMetrics;

public class ConfagentUptimeJob extends QuartzJobBean {
	@Autowired
	private ConfagentMetrics confagentMetrics;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		confagentMetrics.confagentUpTimeCounter.increment();
	}

}
