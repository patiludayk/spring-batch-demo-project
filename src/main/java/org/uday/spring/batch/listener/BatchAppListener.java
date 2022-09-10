package org.uday.spring.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchAppListener extends JobExecutionListenerSupport {

    public static int counter = 0;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        //before job
        log.info("before job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        //after job
        log.info("after job.");
        counter = 0;
    }
}
