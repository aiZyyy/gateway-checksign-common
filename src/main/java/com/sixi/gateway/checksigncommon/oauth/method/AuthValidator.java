package com.sixi.gateway.checksigncommon.oauth.method;

import com.sixi.gateway.checksigncommon.oauth.AuthMessage;
import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:26
 * @Version 1.0
 * @Description: 校验请求授权是合法
 */
public interface AuthValidator {
    /**
     * 校验OAuthMessage 的授权是否合法
     * @param message 授权请求消息体
     * @param consumer 授权接入应用
     * @throws AuthException 非法授权
     */
    void validateMessage(AuthMessage message, AuthConsumer consumer, RedisTemplate<String, String> redisTemplate) throws AuthException;
}
