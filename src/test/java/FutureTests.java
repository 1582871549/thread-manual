import com.google.common.util.concurrent.*;
import com.rice.meng.common.threadpool.ThreadHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@Slf4j
public class FutureTests {

    private final ExecutorService service = ThreadHelper.createFixedThreadPool(4, 100, "future");

    /**
     * 伪异步
     * 
     * JDK 1.5 Callable
     */
    @Test
    public void testCallableMethod() throws ExecutionException, InterruptedException {
        log.info("start assign task.");
        Future<String> future = service.submit(() -> {
            log.info("working...");
            return "done.";
        });

        log.info("future: [{}]", future.get());
        log.info("do something...");
        log.info("get off work!");
        waiting(1);
        log.info("end.");
    }

    /**
     * 异步回调
     *
     * JDK 1.5 Guava Listener
     * 从运行结果可以看出: 获取运行结果是在另外的线程里面执行的(thread-2)，完全没有阻塞主线程。
     */
    @Test
    public void testListenerMethod() {
        log.info("start assign task.");
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(service);

        ListenableFuture<String> listenableFuture = executor.submit(() -> {
            log.info("working...");
            waiting(3);
            return "done.";
        });

        listenableFuture.addListener(() -> {
            try {
                log.info("future: [{}]", listenableFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);

        log.info("do something...");
        log.info("get off work!");
        waiting(4);
        log.info("end.");
    }

    /**
     * 异步回调
     *
     * JDK 1.5 Guava FutureCallback
     * 从运行结果可以看出: 获取运行结果是在另外的线程里面执行的(thread-2)，完全没有阻塞主线程。
     */
    @Test
    public void testFutureCallbackMethod() {
        log.info("start assign task.");
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(service);

        ListenableFuture<String> listenableFuture = executor.submit(() -> {
            log.info("working...");
            waiting(3);
            return "done.";
        });

        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                log.info("result: [{}]", result);
            }

            @Override
            public void onFailure(Throwable t) {
                log.info("failed");
                t.printStackTrace();
            }
        }, executor);

        log.info("do something...");
        log.info("get off work!");
        waiting(4);
        log.info("end.");
    }

    /**
     * 异步回调
     *
     * JDK 1.8 CompletableFuture
     */
    @Test
    public void testCompletableFutureMethod() {
        log.info("start assign task.");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            log.info("working...");
            waiting(3);
            return "done.";
        }, service);

        completableFuture.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("result: [{}]", result);
            } else {
                log.info("failed");
                exception.printStackTrace();
            }
        });

        log.info("do something...");
        log.info("get off work!");
        waiting(4);
        log.info("end.");
    }

    private void waiting(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
