package org.uday.spring.batch.config;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Collection;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean("scheduler")
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, ApplicationContext applicationContext){
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setSchedulerName("batch-job-scheduler");
        scheduler.setOverwriteExistingJobs(false);
        scheduler.setApplicationContext(applicationContext);
        scheduler.setWaitForJobsToCompleteOnShutdown(true);
        scheduler.setJobFactory(jobFactory);
        scheduler.setAutoStartup(true);

        Collection<Trigger> triggers = applicationContext.getBeansOfType(Trigger.class).values();
        scheduler.setTriggers(triggers.toArray(new Trigger[triggers.size()]));

        Collection<JobDetail> jobDetails = applicationContext.getBeansOfType(JobDetail.class).values();
        scheduler.setJobDetails(jobDetails.toArray(new JobDetail[jobDetails.size()]));

        return scheduler;
    }

    @Bean(name = "txManager")
    public ResourcelessTransactionManager transactionManager(){
        return new ResourcelessTransactionManager();
    }

    @Bean(name = "jobRegistryBeanPostProcessor")
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry){
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
