package com.user.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GenericCacheUtil<K, V> {
    private static LoadingCache<Object, Object> cache;
    private static final Integer EXPIRE_MINS = 10;

    static {
        cache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<Object, Object>() {
                    @Override
                    public Object load(Object key) throws Exception {
                        return null;
                    }
                });
    }

    private static Object loadFromDatabase(Object key) {
        // Simulate database access
        return "Value for " + key;
    }

    // Static method to get value from cache
    @SuppressWarnings("unchecked")
    public static <K, V> V getValue(K key) {
        try {
            return (V) cache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Static method to put value in cache
    public static <K, V> void putValue(K key, V value) {
        cache.put(key, value);
    }

    // Static method to delete value from cache
    public static <K> void deleteValue(K key) {
        cache.invalidate(key);
    }
}