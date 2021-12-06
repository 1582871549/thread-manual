package service;

import java.util.concurrent.ExecutionException;

public interface Computable<K, V> {

    V compute(K key) throws InterruptedException, ExecutionException;
}
