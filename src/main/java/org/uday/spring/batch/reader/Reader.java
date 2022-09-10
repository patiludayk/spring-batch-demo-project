package org.uday.spring.batch.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Configuration;
import org.uday.spring.batch.dto.ReaderInfo;
import org.uday.spring.batch.listener.BatchAppListener;

@Slf4j
@Configuration
public class Reader implements ItemReader<ReaderInfo> {

    @Override
    public ReaderInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if( BatchAppListener.counter < 1 ) {
            log.info("reader reading count: {}", BatchAppListener.counter);
            return ReaderInfo.builder().name("reader").count(BatchAppListener.counter++).build();
        }
        return null;
    }
}
