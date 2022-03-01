import com.rice.meng.common.threadpool.SerializingExecutor;
import com.rice.meng.common.threadpool.ThreadHelper;
import com.rice.meng.common.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * https://mp.weixin.qq.com/s/OKleJdGzbv6z3xexKCG-pA
 */
@Slf4j
public class SerializingExecutorTest {

    protected static SerializingExecutor serializingExecutor;

    @BeforeAll
    public static void before() {
        ExecutorService service = ThreadHelper.createFixedThreadPool(10, 100, "serializing");
        serializingExecutor = new SerializingExecutor(service);
    }

    @Test
    public void test1() throws InterruptedException {
        int n = 2;
        int eachCount = 50;
        int total = n * eachCount;
        int sleepTime = 10;
        String key = "key";

        Map<String, Integer> map = new HashMap<>();
        map.put(key, 0);

        CountDownLatch downLatch = new CountDownLatch(total);

        IntStream.range(0, total)
                .forEach(i -> {
                    // sleep(sleepTime);
                    serializingExecutor.execute(() -> {
                        // sleep(sleepTime);
                        int num = map.get(key);
                        log.info("value: [{}]", num);
                        map.put(key, num + 1);
                        downLatch.countDown();
                        Assertions.assertEquals(num, i);
                    });
                });
        downLatch.await();
        Assertions.assertEquals(total, map.get(key));
    }

    private void sleep(int sleepMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(0, sleepMillis));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
