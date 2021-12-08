package com.rice.meng.service.decorator;

/**
 * 装饰器模式
 */
public interface Computable<K, V> {

    V compute(K key);
}
