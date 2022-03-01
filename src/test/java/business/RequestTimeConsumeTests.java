package business;

import com.rice.meng.common.threadpool.ThreadHelper;
import com.rice.meng.common.util.FutureUtil;
import com.rice.meng.common.util.RandomUtils;
import com.rice.meng.common.timer.RequestTimer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 测试请求耗时方案
 */
@Slf4j
public class RequestTimeConsumeTests {

    private static final ExecutorService POOL = ThreadHelper.createFixedThreadPool(10, 100, "timer");
    private long startTime;
    private RequestTimer timeCollector;

    @BeforeEach
    void beforeEach() {
        startTime = System.currentTimeMillis();
        timeCollector = new RequestTimer();
    }

    @AfterEach
    void afterEach() {
        // 查看统计结果
        timeCollector.print();
        // 打印耗时
        log.info("It takes [{}] milliseconds", (System.currentTimeMillis() - startTime));
    }

    @Test
    void testMultipleUrl() {
        // 布置任务
        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
                .mapToObj(i -> submit("test/" + i))
                .collect(Collectors.toList());
        // 等待所有任务完成
        FutureUtil.waitForAll(futures).join();
    }

    @Test
    void testSingleUrl() {
        // 布置任务
        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
                .mapToObj(i -> submit("test/1"))
                .collect(Collectors.toList());
        // 等待所有任务完成
        FutureUtil.waitForAll(futures).join();
    }

    private CompletableFuture<Void> submit(String url) {
        return CompletableFuture.runAsync(
                () -> timeCollector.collect(url, RandomUtils.nextInt(100, 200)),
                POOL
        );
    }

}
