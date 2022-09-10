package org.uday.spring.batch.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;
import org.uday.spring.batch.dto.ReaderInfo;
import org.uday.spring.batch.dto.WriterInfo;

@Slf4j
@Configuration
public class Processor implements ItemProcessor<ReaderInfo, WriterInfo> {

    @Override
    public WriterInfo process(ReaderInfo readerInfo) throws Exception {
        log.info("processing count: {}", readerInfo.getCount());
        return WriterInfo.builder().name("processor writing").count(readerInfo.getCount()).build();
    }
}
