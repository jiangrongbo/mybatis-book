package com.blog4java.mybatis.cache;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.*;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.mapping.CacheBuilder;
import org.junit.Test;

public class CacheExample {

    @Test
    public void testCache() {
        final int N = 100000;
        Cache cache = new PerpetualCache("default");
        cache = new LruCache(cache);
        cache = new FifoCache(cache);
        cache = new SoftCache(cache);
        cache = new WeakCache(cache);
        cache = new ScheduledCache(cache);
        cache = new SerializedCache(cache);
        cache = new SynchronizedCache(cache);
        cache = new TransactionalCache(cache);
        for (int i = 0; i < N; i++) {
            cache.putObject(i, i);
            ((TransactionalCache) cache).commit();
        }
        System.out.println(cache.getSize());
    }

    @Test
    public void testCacheBuilder() {
        final int N = 100000;
        Cache cache = new CacheBuilder("com.blog4java.mybatis.example.mapper.UserMapper")
                .implementation( PerpetualCache.class)
                .addDecorator(LruCache.class)
                .clearInterval(10 * 60L)
                .size(1024)
                .readWrite(false)
                .blocking(false)
                .properties(null)
                .build();
        for (int i = 0; i < N; i++) {
            cache.putObject(i, i);
        }
        System.out.println(cache.getSize());
    }
}
