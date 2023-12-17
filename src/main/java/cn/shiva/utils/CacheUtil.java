package cn.shiva.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 *
 * @author shiva   2023-12-17 20:12
 */
public class CacheUtil {
    /**
     * 缓存的最大容量
     */
    private static final int MAXIMUM_SIZE = 10000;

    /**
     * 缓存项的写入后过期时间，300分钟
     */
    private static final int EXPIRE_AFTER_WRITE_DURATION = 300;

    /**
     * 过期时间单位（分钟）
     */
    private static final TimeUnit EXPIRE_AFTER_WRITE_TIMEUNIT = TimeUnit.MINUTES;

    private static Cache<String, Object> CACHE;

    /**
     * 初始化Caffeine缓存配置
     */
    static {
        CACHE = Caffeine.newBuilder()
                .maximumSize(MAXIMUM_SIZE)
                .expireAfterWrite(EXPIRE_AFTER_WRITE_DURATION, EXPIRE_AFTER_WRITE_TIMEUNIT)
                .build();
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public static Object get(String key) {
        return CACHE.getIfPresent(key);
    }

    /**
     * 设置缓存值
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void put(String key, Object value) {
        CACHE.put(key, value);
    }

    /**
     * 移除缓存项
     *
     * @param key 缓存键
     */
    public static void remove(String key) {
        CACHE.invalidate(key);
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        CACHE.invalidateAll();
    }

    /**
     * 获取缓存中的所有值
     *
     * @return 缓存中的所有值集合
     */
    public static Collection<Object> getAllValues() {
        return CACHE.asMap().values();
    }

    /**
     * 清空缓存中的所有值
     */
    public static void removeAllValues() {
        CACHE.invalidateAll();
    }
}
