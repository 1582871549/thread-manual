package com.rice.meng.common.timer;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计接口的请求次数及响应时间
 */
@Slf4j
public class RequestTimer {

    private final Map<String, VisitorCounter> monitors = new ConcurrentHashMap<>();

    public void collect(String url, long timeCost) {
        // 应使用缓存构建, 配合定时任务将当天统计值累加到数据库
        VisitorCounter counter = monitors.computeIfAbsent(url, v -> new VisitorCounter());
        log.info("url: {}, timeCost: {}, counter: {}", url, timeCost, System.identityHashCode(counter));

        counter.getCount().increment();
        counter.getTotalTime().add(timeCost);
    }

    public void print() {
        monitors.forEach((k, v) -> log.info("k: [{}], v: [{}]", k, v));
    }

}
