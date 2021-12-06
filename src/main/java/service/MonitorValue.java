package service;

import lombok.Data;

@Data
public class MonitorValue {
    private Integer count = 0;
    private Long totalTime = 0L;
}
