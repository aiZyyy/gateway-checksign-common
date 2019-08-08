package com.sixi.gateway.checksignservice.oauth.method;

import com.sixi.gateway.checksignservice.oauth.AuthMessage;
import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksignservice.oauth.exception.AuthException;

/**
 * @Author: ZY
 * @Date: 2019/8/7 9:59
 * @Version 1.0
 * @Description: 签名方法接口定义.不同签名算法实现本接口
 */
public interface ISignatureMethod {

    /**
     * 对消息体进行签名
     * @param message 待签名消息串
     * @param consumer 接入应用钥密
     * @throws AuthException 签名异常
     */
    void sign(AuthMessage message, AuthConsumer consumer) throws AuthException;

    /**
     * getSignature: 计算签名值 <br/>
     * @param message  待签名消息体
     * @param consumer 接入应用的验签密钥
     * @return 签名计算值
     * @throws AuthException 签名异常
     */
    String getSignature(AuthMessage message, AuthConsumer consumer)
            throws AuthException;

    /**
     * 对请求进行鉴权
     * @param message 	待验签消息体
     * @param consumer	接入应用的验签密钥
     * @throws AuthException 签名异常
     */
    void validate(AuthMessage message, AuthConsumer consumer) throws AuthException;
}
