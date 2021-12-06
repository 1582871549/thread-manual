import com.google.common.util.concurrent.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FutureTests {

    private final ExecutorService service = Executors.newFixedThreadPool(4);

    /**
     * 伪异步
     *
     * JDK 1.5 Callable
     */
    @Test
    public void testCallableMethod() throws ExecutionException, InterruptedException {

        Future<String> future = service.submit(() -> {
            System.out.println("关注why技术");
            return "这次一定！";
        });
        System.out.println("future的内容:" + future.get());

        TimeUnit.SECONDS.sleep(2);
        System.out.println("------------");
    }

    /**
     * 伪异步
     *
     * JDK 1.5 Runnable
     */
    @Test
    public void testRunnableMethod() throws ExecutionException, InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger();

        Future<AtomicInteger> future = service.submit(() -> {
            System.out.println("关注why技术");
            // 在这里进行计算逻辑
            atomicInteger.set(5201314);
        }, atomicInteger);

        System.out.println("future的内容:" + future.get());
        System.out.println("object equals: " + (atomicInteger.equals(future.get())));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("------------");
    }

    /**
     * 异步回调
     *
     * JDK 1.5 Guava Listener
     *
     * 从运行结果可以看出: 获取运行结果是在另外的线程里面执行的(thread-2)，完全没有阻塞主线程。
     */
    @Test
    public void testListenerMethod() throws InterruptedException {

        ListeningExecutorService executor = MoreExecutors.listeningDecorator(service);

        ListenableFuture<String> listenableFuture = executor.submit(() -> {
            System.out.println(Thread.currentThread().getName()+"-女神：我开始化妆了，好了我叫你。");
            TimeUnit.SECONDS.sleep(5);
            return "化妆完毕了。";
        });

        listenableFuture.addListener(() -> {
            try {
                System.out.println(Thread.currentThread().getName()+"-future的内容:" + listenableFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);

        System.out.println(Thread.currentThread().getName()+"-等女神化妆的时候可以干点自己的事情。");

        TimeUnit.SECONDS.sleep(6);
        System.out.println("------------");
    }

    /**
     * 异步回调
     *
     * JDK 1.5 Guava FutureCallback
     *
     * 从运行结果可以看出: 获取运行结果是在另外的线程里面执行的(thread-2)，完全没有阻塞主线程。
     */
    @Test
    public void testFutureCallbackMethod() throws InterruptedException {

        ListeningExecutorService executor = MoreExecutors.listeningDecorator(service);

        ListenableFuture<String> listenableFuture = executor.submit(() -> {
            System.out.println(Thread.currentThread().getName()+"-女神：我开始化妆了，好了我叫你。");
            TimeUnit.SECONDS.sleep(5);
            return "化妆完毕了。";
        });

        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(Thread.currentThread().getName()+"-future的内容:" + result);
            }
            @Override
            public void onFailure(Throwable t) {
                System.out.println(Thread.currentThread().getName()+"-女神放你鸽子了。");
                t.printStackTrace();
            }
        }, executor);

        System.out.println(Thread.currentThread().getName()+"-等女神化妆的时候可以干点自己的事情。");
        TimeUnit.SECONDS.sleep(6);
        System.out.println("------------");
    }

    /**
     * 异步回调
     *
     * JDK 1.8 CompletableFuture
     *
     * 从运行结果可以看出: 获取运行结果是在另外的线程里面执行的(thread-2)，完全没有阻塞主线程。
     */
    @Test
    public void testCompletableFutureMethod() throws InterruptedException {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "-女神：我开始化妆了，好了我叫你。");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "化妆完毕了。";
        }, service);

        completableFuture.whenComplete((returnStr, exception) -> {
            if (exception == null) {
                System.out.println(Thread.currentThread().getName() + returnStr);
            } else {
                System.out.println(Thread.currentThread().getName() + "女神放你鸽子了。");
                exception.printStackTrace();
            }
        });

        System.out.println(Thread.currentThread().getName()+"-等女神化妆的时候可以干点自己的事情。");
        TimeUnit.SECONDS.sleep(6);
        System.out.println("------------");
    }







}
