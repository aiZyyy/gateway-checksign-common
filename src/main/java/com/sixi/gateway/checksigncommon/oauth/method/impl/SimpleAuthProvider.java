package com.sixi.gateway.checksigncommon.oauth.method.impl;

import com.sixi.gateway.checksigncommon.oauth.Auth;
import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksigncommon.oauth.method.AuthProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:25
 * @Version 1.0
 * @Description: 简单OAuthProvider实现, APP ID与APP Secret 由Spring配置
 * 校验请求授权是合法
 */
public class SimpleAuthProvider implements AuthProvider {


    private Map<String, AuthConsumer> consumeMap;

    public SimpleAuthProvider(Map<String, String> cache) {
        consumeMap = new ConcurrentHashMap<>();
        if(cache != null){
            for (Map.Entry<String, String> entry : cache.entrySet()) {
                AuthConsumer consumer = new AuthConsumer(entry.getKey(), entry.getValue());
                consumeMap.put(entry.getKey(), consumer);
            }
        }
    }

    @Override
    public AuthConsumer getAccessConsumer(String appId) throws AuthException {
        AuthConsumer consumer = consumeMap.get(appId);
        if (consumer == null) {
            AuthProblemException problem = new AuthProblemException(Auth.Problems.CONSUMER_KEY_UNKNOWN);
            throw problem;
        }
        return consumer;
    }

}
