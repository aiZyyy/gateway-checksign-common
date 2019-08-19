package com.sixi.gateway.checksigncommon.kits;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA
 * Redis简易工具类
 *
 * @author MiaoWoo
 * Created on 2018/7/18 18:43.
 */
@Configuration
@ConditionalOnProperty(value = "spring.redis.host")
@Component
public class RedisKit {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Getter
    private List list;
    @Getter
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
    public <T> void set(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }


    /**
     * 设置缓存并设置有效期长
     * @param key   key
     * @param value value
     * @param time  缓存有效期，单位:秒
     */
    public <T> void set(String key, String value, long time) {
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
    public <T> Boolean setIfAbsent(String key,String value){
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
    public Boolean setIfAbsent(String key, String value, long time){
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

}
