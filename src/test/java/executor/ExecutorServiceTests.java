package executor;

import com.rice.meng.common.threadpool.ThreadHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class ExecutorServiceTests {

    private final ExecutorService service = ThreadHelper.createFixedThreadPool(2, 100, "task");

    private long startTime;

    @BeforeEach
    void beforeEach() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void afterEach() {
        log.info("It takes [{}] milliseconds", (System.currentTimeMillis() - startTime));
    }

    @Test
    void testExecutorService() throws InterruptedException {
        int total = 10;
        CountDownLatch latch = new CountDownLatch(total);

        IntStream.range(0, total)
                .forEach(i -> service.execute(() -> task(i, latch)));

        log.info("waiting...");
        latch.await();
        log.info("task end");
    }

    private void task(int i, CountDownLatch latch) {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
            log.info("i: [{}], time: [{}]", i, System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }
}
