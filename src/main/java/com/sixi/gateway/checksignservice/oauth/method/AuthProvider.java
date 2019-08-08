package com.sixi.gateway.checksignservice.oauth.method;

import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksignservice.oauth.exception.AuthException;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:24
 * @Version 1.0
 * @Description: 通过授权服务提供者获取接入应用的密钥
 */
public interface AuthProvider {

    /**
     * 根据appId获取接入应用的密钥
     * @param appId  接入应用ID
     * @return 授权应用的密钥
     * @throws AuthException 授权失败原因通过Exception统一返回
     */
    AuthConsumer getAccessConsumer(String appId) throws AuthException;
}
