package com.rice.meng.service.decorator;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class Memoizer<K, V> implements Computable<K, V> {

    private final Map<K, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<K, V> c;

    public Memoizer(Computable<K, V> c) {
        this.c = c;
    }

    /**
     * 1、在map中搜索有没有已经其他线程已经创建好的future
     * 2、如果有（不等于null），那就调用future的get方法
     * 3、如果已经计算完成，get方法会立刻返回计算结果
     * 4、否则，get方法会阻塞，直到结果计算出来再将其返回。
     * 5、如果没有已经创建的future，那么就自己创建future，进行计算
     *
     * compute是一个计算很费时的方法，所以这里把计算的结果缓存起来，
     * 但是有个问题就是如果两个线程同时进入此方法中怎么保证只计算一次，
     * 这里最核心的地方在于使用了ConcurrentHashMap的putIfAbsent方法，同时只会写入一个FutureTask；
     *
     * 问题:
     * 1、为什么使用putIfAbsent
     * 如果有两个都是计算userID=1的线程，同时调用put方法，那么返回的结果都不会为null，后面还是会创建两个任务去计算相同的值。
     * 而putIfAbsent，当map里面已经有对应的值了，则会返回已有的值，否则，会返回null，这样就可以解决相同的值计算两次的问题。
     * 2、为什么使用while (true)
     * 这是因为future的get方法会由于线程被中断而抛出CancellationException。
     * 我们对于CancellationException的处理是cache.remove(arg, f);
     * 也就是把缓存清理掉，然后进入下一个循环，重新计算，直到计算成功，或者抛出ExecutionException。
     *
     * @param key key
     * @return value
     */
    @Override
    public V compute(K key) {
        // use loop for retry when CancellationException
        while (true) {
            Future<V> f = cache.get(key);

            if (f == null) {
                FutureTask<V> ft = new FutureTask<>(() -> c.compute(key));
                // use putIfAbsent to avoid multi thread compute the same value
                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            }

            log.info("get cache");
            try {
                return f.get();
            } catch (CancellationException e) {
                // remove cache and go into next loop to retry
                cache.remove(key, f);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
