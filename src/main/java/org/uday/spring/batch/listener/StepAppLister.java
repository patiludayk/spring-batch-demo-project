package org.uday.spring.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StepAppLister extends StepExecutionListenerSupport {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //before step
        log.info("before STEP...");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        //after step
        log.info("after STEP...");
        return ExitStatus.COMPLETED;
    }
}
