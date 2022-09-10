package org.uday.spring.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.uday.spring.batch.dto.WriterInfo;

import java.util.List;

@Slf4j
@Configuration
public class Writer implements ItemWriter<WriterInfo> {

    @Override
    public void write(List<? extends WriterInfo> list) throws Exception {
        for (WriterInfo writerInfo : list ) {
            log.info("writer: {}, count: {}", writerInfo.getName(), writerInfo.getCount());
        }
        log.info("done writitng...");
    }
}
