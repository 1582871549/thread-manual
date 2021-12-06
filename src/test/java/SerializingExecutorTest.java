import com.rice.meng.common.threadpool.SerializingExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class SerializingExecutorTest {

    protected static SerializingExecutor serializingExecutor;

    @BeforeAll
    public static void before() {
        ExecutorService service = Executors.newFixedThreadPool(4);
        serializingExecutor = new SerializingExecutor(service);
    }

    @Test
    public void test1() throws InterruptedException {
        int n = 2;
        int eachCount = 1000;
        int total = n * eachCount;
        int sleepMillis = 10;

        Map<String, Integer> map = new HashMap<>();
        map.put("val", 0);

        CountDownLatch downLatch = new CountDownLatch(total);

        for (int i = 0; i < total; i++) {
            final int index = i;
            Thread.sleep(ThreadLocalRandom.current().nextInt(sleepMillis));
            serializingExecutor.execute(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(sleepMillis));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int num = map.get("val");
                map.put("val", num + 1);
                downLatch.countDown();
                Assertions.assertEquals(num, index);
            });
        }
        downLatch.await(3, TimeUnit.SECONDS);
        Assertions.assertEquals(total, map.get("val"));
    }
}
