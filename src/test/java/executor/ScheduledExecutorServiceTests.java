package executor;

import com.rice.meng.common.threadpool.ThreadHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledExecutorServiceTests {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = ThreadHelper.createScheduledExecutor(1, "scheduled");

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
    public void testTask() throws ExecutionException, InterruptedException {
        ScheduledFuture<String> scheduledFuture = SCHEDULED_EXECUTOR.schedule(
                () -> {
                    log.info("working...");
                    return System.currentTimeMillis() + "";
                },
                2,
                TimeUnit.SECONDS
        );
        log.info("value: [{}]", scheduledFuture.get());
    }

}
