import com.rice.meng.common.threadpool.ThreadHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
public class ThreadExampleTests {

    private static final int TOTAL = 10;
    private static final ExecutorService POOL = ThreadHelper.createFixedThreadPool(1, TOTAL, "task");

    @Test
    public void testTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(TOTAL);

        IntStream.range(0, TOTAL)
                .forEach(i -> submit(latch, i));

        log.info("waiting...");
        latch.await();
        log.info("task end");
    }

    private void submit(CountDownLatch latch, int i) {
        try {
            POOL.execute(() -> task(latch, i));
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            latch.countDown();
        }
    }

    private void task(CountDownLatch latch, int i) {
        try {
            log.info("name: [{}], i: [{}]", Thread.currentThread().getName(), i);
            if (i % 3 == 0) {
                throw new RuntimeException("我异常了");
            }
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

}
