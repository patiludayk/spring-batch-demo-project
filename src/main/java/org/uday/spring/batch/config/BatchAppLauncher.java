package org.uday.spring.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
public class BatchAppLauncher extends QuartzJobBean implements InterruptableJob {

    private Thread thread;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        if(null != thread){
            thread.interrupt();
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();

        try {
            final JobKey jobKey = context.getJobDetail().getKey();
            thread = Thread.currentThread();
            jobLauncher.run(jobLocator.getJob(jobKey.getName()), jobParameters);
        } catch (Exception e){

        }
    }
}
