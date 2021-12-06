package service;

import java.util.Map;
import java.util.concurrent.*;

public class Memoizerl<K, V> implements Computable<K, V> {

    private final Map<K, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<K, V> c;

    public Memoizerl(Computable<K, V> c) {
        this.c = c;
    }

    @Override
    public V compute(K key) throws InterruptedException, ExecutionException {
        while (true) {
            Future<V> f = cache.get(key);
            if (f == null) {
                FutureTask<V> ft = new FutureTask<>(() -> c.compute(key));
                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
                try {
                    return f.get();
                } catch (CancellationException e) {
                    cache.remove(key, f);
                }
            }
        }
    }
    // compute是一个计算很费时的方法，所以这里把计算的结果缓存起来，
    // 但是有个问题就是如果两个线程同时进入此方法中怎么保证只计算一次，
    // 这里最核心的地方在于使用了ConcurrentHashMap的putIfAbsent方法，同时只会写入一个FutureTask；

}
