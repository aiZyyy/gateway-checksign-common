package com.sixi.gateway.checksigncommon.oauth.method.impl;

import com.sixi.gateway.checksigncommon.oauth.Auth;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksigncommon.oauth.method.AuthNonces;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author: ZY
 * @Date: 2019/8/19 10:23
 * @Version 1.0
 * @Description: 使用Redis 来做重启攻击检查. <br/>
 * 当app_id + 时间+ nonce 做为key放入redis，
 * 挡住单位时间内的重放攻击，超时时间则由时间检查机制保护 <br/>
 */
public class RedisAuthNonces implements AuthNonces {

    /**
     * 前端时间差最大允许30分钟，单位秒
     **/
    public static final long DEFAULT_MAX_TIMESTAMP_AGE = 30 * 60;


    /**
     * redis key 串模版
     */
    private String format = "SIXI_NONCES:#%s#%s#%s";

    protected long maxTimestampAgeMsec;

    public RedisAuthNonces() {
        this(DEFAULT_MAX_TIMESTAMP_AGE);
    }

    public RedisAuthNonces(long maxAge) {
        maxTimestampAgeMsec = maxAge;
        System.out.println("create redis auth nonce");

    }


    @Override
    public void validateNonce(long timestamp, String appId, String nonce, RedisTemplate<String, String> redisTemplate) throws AuthProblemException {

        String nonceName = String.format(format, appId, nonce,timestamp);
        Boolean isValid = redisTemplate.opsForValue().setIfAbsent(nonceName, "n", maxTimestampAgeMsec, TimeUnit.SECONDS);
        if (!isValid) {
            throw new AuthProblemException(Auth.Problems.NONCE_USED);
        }
    }
}
