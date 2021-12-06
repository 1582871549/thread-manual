import org.junit.jupiter.api.Test;
import service.Monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadTests {

    @Test
    public void method1() throws InterruptedException {

        long startTime = System.currentTimeMillis();
        Monitor monitor = new Monitor();

        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<Thread> threads = new ArrayList<>();

        for (int count = 0; count < taskCount; count++) {
            int finalCount = count;

            Thread thread = new Thread(() -> {
                monitor.visit("url-" + finalCount, 10);
                latch.countDown();
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);

        latch.await();

        monitor.print();
        long endTime = System.currentTimeMillis();
        long time = (endTime - startTime) / 1000;
        System.out.println("time = " + time);
    }

    @Test
    public void method2() throws InterruptedException {

        long startTime = System.currentTimeMillis();
        Monitor monitor = new Monitor();

        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<Thread> threads = new ArrayList<>();

        for (int count = 0; count < taskCount; count++) {

            Thread thread = new Thread(() -> {
                monitor.visit("url-1", 10);
                latch.countDown();
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);

        latch.await();
        monitor.print();
        long endTime = System.currentTimeMillis();
        long time = (endTime - startTime) / 1000;
        System.out.println("time = " + time);
    }

    @Test
    public void method3() throws InterruptedException {

        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key2";

        Thread thread1 = new Thread(() -> handle(key1));
        Thread thread2 = new Thread(() -> handle(key2));
        Thread thread3 = new Thread(() -> handle(key3));

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

    }

    private void handle(String key) {
        synchronized (key) {
            compute();
        }
    }

    private void compute() {
        System.out.println("----");
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("====");
    }

    @Test
    public void method4() {

        InnerClass innerClass = new InnerClass();

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                while (true) {
                    Future<String> future = innerClass.submit();

                    try {
                        String s = future.get();
                        System.out.println(s);
                    } catch (InterruptedException | ExecutionException | Error e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //子线程不停gc，模拟偶发的gc
        new Thread(() -> {
            while (true) {
                System.gc();
            }
        }).start();
    }

    static class InnerClass {

        /**
         * 异步执行任务
         */
        public Future<String> submit() {

            FutureTask<String> futureTask = new FutureTask<>(() -> {
                Thread.sleep(50);
                return System.currentTimeMillis() + "";
            });

            // 关键点，通过Executors.newSingleThreadExecutor创建一个单线程的线程池
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(futureTask);

            return futureTask;
        }
    }


}
