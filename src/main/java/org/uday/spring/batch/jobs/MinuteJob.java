package org.uday.spring.batch.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.uday.spring.batch.config.BatchAppLauncher;
import org.uday.spring.batch.dto.ReaderInfo;
import org.uday.spring.batch.dto.WriterInfo;
import org.uday.spring.batch.listener.BatchAppListener;
import org.uday.spring.batch.listener.StepAppLister;
import org.uday.spring.batch.processor.Processor;
import org.uday.spring.batch.reader.Reader;
import org.uday.spring.batch.writer.Writer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@Slf4j
//@Profile("minute")
public class MinuteJob {

    private final String JOB_NAME = "minuteJob_jobName";
    private BatchAppListener batchAppListener;
    private StepBuilderFactory stepBuilderFactory;
    private StepAppLister stepAppLister;
    @Value("${CHUNK_SIZE:5}")
    private int chunkSize;
    private Reader reader;
    private Processor processor;
    private Writer writer;
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;
    //    @Value("${BATCH_JOB_CRON:0 */3 * ? * *}")   //every 3 minute
    @Value("${BATCH_JOB_CRON:0 0 9 ? * *}")   //daily 9 o'clock
    private String scheduler;

    @Autowired
    public MinuteJob(BatchAppListener batchAppListener,
                     StepBuilderFactory stepBuilderFactory,
                     StepAppLister stepAppLister,
                     Reader reader,
                     Processor processor,
                     Writer writer,
                     JobLauncher jobLauncher,
                     JobLocator jobLocator) {
        this.batchAppListener = batchAppListener;
        this.stepBuilderFactory = stepBuilderFactory;
        this.stepAppLister = stepAppLister;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
    }

    @Bean(JOB_NAME)
    public Job minuteJobBean(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(batchAppListener)
                .start(splitFlow())
                .end()
                .build();
    }

    @Bean
    public Flow splitFlow() {
        return new FlowBuilder<SimpleFlow>("simpleFlow").split(new SimpleAsyncTaskExecutor()).add(flow1(), flow2(), flow3()).build();
    }

    @Bean
    public Flow flow1() {
        return new FlowBuilder<SimpleFlow>("flow1").start(step1()).build();
    }

    @Bean
    public Flow flow2() {
        return new FlowBuilder<SimpleFlow>("flow1").start(step1()).build();
//        return new FlowBuilder<SimpleFlow>("flow1").start(step2()).build();
    }

    @Bean
    public Flow flow3() {
        return new FlowBuilder<SimpleFlow>("flow1").start(step1()).build();
//        return new FlowBuilder<SimpleFlow>("flow1").start(step3()).build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .listener(stepAppLister)
                .<ReaderInfo, WriterInfo>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean(name = "jobDataMap")
    public JobDataMap jobDataMap(@Qualifier(JOB_NAME) Job batchJob) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("jobName", batchJob.getName());
        dataMap.put("joblauncher", jobLauncher);
        dataMap.put("jobLocator", jobLocator);

        return new JobDataMap(dataMap);
    }

    @Bean(name = "jobDetailFactoryBean")
    public JobDetailFactoryBean jobDetailFactoryBean(@Qualifier("jobDataMap") JobDataMap jobDataMap) {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setGroup("myjobs");
        jobDetailFactoryBean.setName(JOB_NAME);
        jobDetailFactoryBean.setJobDataMap(jobDataMap);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setJobClass(BatchAppLauncher.class);
        return jobDetailFactoryBean;
    }

    @Bean(name = "cronTriggerFactoryBean")
    public CronTriggerFactoryBean cronTriggerFactoryBean(@Qualifier("jobDetailFactoryBean") JobDetailFactoryBean jobDetailFactoryBean) {
        JobDetail jobDetail = jobDetailFactoryBean.getObject();
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        cronTriggerFactoryBean.setGroup(jobDetail.getKey().getGroup());
        cronTriggerFactoryBean.setName(jobDetail.getKey().getName());
        cronTriggerFactoryBean.setCronExpression(scheduler);

        return cronTriggerFactoryBean;
    }
}
