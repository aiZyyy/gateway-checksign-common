package com.sixi.gateway.checksigncommon.oauth.method.impl;

import com.sixi.gateway.checksigncommon.oauth.Auth;
import com.sixi.gateway.checksigncommon.oauth.AuthMessage;
import com.sixi.gateway.checksigncommon.oauth.SignerBuilder;
import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksigncommon.oauth.method.AuthNonces;
import com.sixi.gateway.checksigncommon.oauth.method.AuthValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:27
 * @Version 1.0
 * @Description: 校验请求授权是合法
 */
public class SimpleAuthValidator implements AuthValidator {

    /**
     * 客户端与服务端最大允许的时间差
     **/
    protected final long maxTimestampAgeMsec;

    /**
     * 最大默认最大最时间差为30分钟
     * 发现有部份用户的时间不正确（证实用户估计调快手机时间）
     * 请求的时间差 由原来的5分钟修改为为前后各30分钟,
     */
    public static final long DEFAULT_MAX_TIMESTAMP_AGE = 15 * 60 * 1000L;
    public static final long DEFAULT_TIMESTAMP_AGE = DEFAULT_MAX_TIMESTAMP_AGE;


    public static final String[] SINGLE_PARAMETERS = constructSingleParameters().toArray(new String[1]);


    @Autowired
    AuthNonces authNonces;


    private static Set<String> constructSingleParameters() {
        Set<String> s = new HashSet<String>();
        for (String p : new String[]{Auth.OAUTH_APP_ID, Auth.OAUTH_SIGNATURE,
                Auth.OAUTH_TIMESTAMP, Auth.OAUTH_SIGN_TYPE, Auth.OAUTH_SEQUENCE}) {
            s.add(p);
        }
        return Collections.unmodifiableSet(s);
    }

    /**
     * 构造方法
     */
    public SimpleAuthValidator() {
        this(new SimpleAuthNonces(), DEFAULT_TIMESTAMP_AGE);
    }

    /**
     * 构造方式
     *
     * @param maxTimestampAgeMsec 最大允许时间差
     */
    public SimpleAuthValidator(long maxTimestampAgeMsec) {
        this(new SimpleAuthNonces(), maxTimestampAgeMsec);
    }

    /**
     * 构造方式
     *
     * @param nonces              authNonces
     * @param maxTimestampAgeMsec 最大允许时间差
     */
    public SimpleAuthValidator(AuthNonces nonces, long maxTimestampAgeMsec) {
        this.authNonces = nonces;
        this.maxTimestampAgeMsec = maxTimestampAgeMsec;
    }

    @Override
    public void validateMessage(AuthMessage message, AuthConsumer consumer, RedisTemplate<String, String> redisTemplate) throws AuthException {
        checkSingleParameters(message);
        validateTimestampAndNonce(message, redisTemplate);
        validateSignature(message, consumer);
    }

    /**
     * 检查message 必备参数
     *
     * @param message 授权消息体
     * @throws AuthException 授权失败原因
     */
    private void checkSingleParameters(AuthMessage message) throws AuthException {
        message.requireParameters(SINGLE_PARAMETERS);
    }


    /**
     * 检查时间和Nonce 是否存在重放
     *
     * @param message 消息体
     * @throws AuthProblemException 客户端时间误差超过最大允许区间，或nonce 在最大允许时间区间内存在重放，则抛出异常
     */
    private void validateTimestampAndNonce(AuthMessage message, RedisTemplate<String, String> redisTemplate) throws AuthProblemException {

        long timestamp = Long.parseLong(message.getParameter(Auth.OAUTH_TIMESTAMP));
        long now = currentTimeMsec();
        validateTimestamp(message, timestamp, now);
        validateNonce(message, timestamp, now, redisTemplate);
    }


    /**
     * 检查请求有效期
     *
     * @param message         消息体
     * @param timestamp       请求发起时间截
     * @param currentTimeMsec 当时时间截
     * @throws AuthProblemException 请求超过最大区别时抛出异常
     */
    protected void validateTimestamp(AuthMessage message, long timestamp, long currentTimeMsec) throws
            AuthProblemException {
        long min = (currentTimeMsec - maxTimestampAgeMsec);
        long max = (currentTimeMsec + maxTimestampAgeMsec);
        if (timestamp < min || max < timestamp) {
            AuthProblemException problem = new AuthProblemException(Auth.Problems.TIMESTAMP_REFUSED);
            problem.setParameter(Auth.Problems.OAUTH_ACCEPTABLE_TIMESTAMPS, min + "-" + max);
            throw problem;
        }
    }


    /**
     * 检查nonce是否存在重放
     *
     * @param message         消息体
     * @param timestamp       客户端请求发起时间
     * @param currentTimeMsec 当前时间
     * @throws AuthProblemException nonce 在最大允许时间区间内存在重放，则抛出异常
     */
    protected void validateNonce(AuthMessage message, long timestamp, long currentTimeMsec, RedisTemplate<String, String> redisTemplate) throws
            AuthProblemException {
        String appid = message.getParameter(Auth.OAUTH_APP_ID);
        String sequence = message.getParameter(Auth.OAUTH_SEQUENCE);
        authNonces.validateNonce(timestamp, appid, sequence, redisTemplate);
    }


    /**
     * 检查sign值是否正确
     *
     * @param message  消息体
     * @param consumer 应用授权密钥
     * @throws AuthException 消息签名非法时，抛出异常
     */
    protected void validateSignature(AuthMessage message, AuthConsumer consumer) throws AuthException {
        SignerBuilder.newSigner(message).validate(message, consumer);
    }

    /**
     * 获取服务器当前时间截
     *
     * @return 当时时间截
     */
    protected long currentTimeMsec() {
        return System.currentTimeMillis();
    }


    protected AuthNonces getAuthNonces() {
        return authNonces;
    }

    public void setAuthNonces(AuthNonces authNonces) {
        this.authNonces = authNonces;
    }
}