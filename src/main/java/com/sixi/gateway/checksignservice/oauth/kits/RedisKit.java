//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sixi.gateway.checksignservice.oauth.kits;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty({"spring.redis.host"})
public class RedisKit {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private List list;
    private Set set;


    public RedisKit(List list, Set set) {
        this.list = list;
        this.set = set;
    }

    /**
     * 判断key是否存在
     * @param key key
     * @return 是否存在key
     */
    public Boolean has(String key) {
        return this.redisTemplate.hasKey(key);
    }

    /**
     * 获取key的缓存
     * @param key key
     * @return value
     */
    public <T> T get(String key) {
        return (T)this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存
     * @param key key
     * @param value value
     */
    public <T> void set(String key, T value) {
        this.redisTemplate.opsForValue().set(key, value);
    }


    /**
     * 设置缓存并设置有效期长
     * @param key   key
     * @param value value
     * @param time  缓存有效期，单位:秒
     */
    public <T> void set(String key, T value, long time) {
        this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 设置key
     * 当没有key时，设置key,存在key时返回false
     *
     * @param key   key
     * @param value value
     * @return 设置是否成功
     */
    public <T> Boolean setIfAbsent(String key,T value){
       return this.redisTemplate.opsForValue().setIfAbsent(key,value);
    }

    /**
     * 设置key
     * 当没有key时，设置key,存在key时返回false
     *
     * @param key key
     * @param value value
     * @param time 缓存有效期，单位:秒
     * @return  设置是否成功
     */
    public <T> Boolean setIfAbsent(String key,T value,long time){
       return this.redisTemplate.opsForValue().setIfAbsent(key,value,time,TimeUnit.SECONDS);
    }

    /**
     * 获取key的缓存有效期，时长单位
     * @param key   key
     * @return 缓存过期时间
     */
    public Long getExpire(String key) {
        return this.redisTemplate.getExpire(key);
    }

    /**
     * 获取key的缓存有效期
     * @param key   key
     * @param timeUnit 时长单位
     * @return 缓存过期时间
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return this.redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 删除缓存
     * @param pattern 删除key,或使用正规
     */
    public void del(String pattern) {
        java.util.Set<String> keys = this.redisTemplate.keys(pattern);
        if (!Objects.isNull(keys)) {
            this.redisTemplate.delete(keys);
        }
    }

    /**
     * 缓存列表操作
     * @return 列表
     */
    public List getList() {
        return this.list;
    }

    /**
     * 缓存Set操作
     * @return set
     */
    public Set getSet() {
        return this.set;
    }

    @Configuration
    public static class Set {
        @Resource
        private RedisTemplate<String, Object> redisTemplate;

        public Set() {
        }

        /**
         * 向变量中批量添加值
         * @param key key
         * @param value 批量值
         */
        public void add(String key, Object... value) {
            this.redisTemplate.opsForSet().add(key, value);
        }

        /**
         * 向变量中批量删除值
         * @param key key
         * @param value 批量值
         */
        public <T> void del(String key, Object... value) {
            this.redisTemplate.opsForSet().remove(key, (Object[])value);
        }

        /**
         * 判断是否已具有key的向量
         * @param key key
         * @return contains结果
         */
        public boolean contains(String key) {
            java.util.Set<Object> ranges = this.redisTemplate.opsForSet().members(key);
            return !Objects.isNull(ranges) && !ranges.isEmpty();
        }
    }

    @Configuration
    public static class List {
        @Resource
        private RedisTemplate<String, Object> redisTemplate;

        public List() {
        }

        /**
         * 向列表的左边添加元素
         * @param key key
         * @param value value
         */
        public <T> void add(String key, T value) {
            this.redisTemplate.opsForList().leftPush(key, value);
        }

        /**
         * 在列表删除元素
         * @param key key
         * @param value value
         */
        public <T> void del(String key, T value) {
            this.redisTemplate.opsForList().remove(key, 1L, value);
        }

        /**
         * 判断是否已具有key的列表
         * @param key key
         * @return  contains结果
         */
        public boolean contains(String key) {
            java.util.List<Object> ranges = this.redisTemplate.opsForList().range(key, 0L, -1L);
            return !Objects.isNull(ranges) && !ranges.isEmpty();
        }
    }
}
