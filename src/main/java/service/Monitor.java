package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 统计接口的平均响应时间和启动以来的请求数
 */
public class Monitor {

    private static final Map<String, MonitorValue> monitors = new ConcurrentHashMap<>();

    public void visit(String url, long timeCost) {
        final MonitorValue value = monitors.computeIfAbsent(url, k -> new MonitorValue());
        System.out.println("key = " + url + ", value = " + value);
        synchronized (value) {
            compute(value, timeCost);
        }
    }

    private void compute(final MonitorValue value, long timeCost) {

        System.out.println("开始计算");
        try {
            TimeUnit.SECONDS.sleep(1);

            value.setCount(value.getCount() + 1);
            value.setTotalTime(value.getTotalTime() + timeCost);
            System.out.println("计算结束 ->");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void print() {
        monitors.forEach((k, v) -> System.out.println(k + " - " + v));
    }





}
