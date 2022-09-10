package org.uday.spring.batch.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WriterInfo {
    private String name;
    private int count;
}
