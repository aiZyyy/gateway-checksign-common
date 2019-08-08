package com.sixi.gateway.checksignservice.oauth.method.impl;

import com.sixi.gateway.checksignservice.oauth.method.AuthNonces;
import com.sixi.gateway.checksignservice.oauth.Auth;
import com.sixi.gateway.checksignservice.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksignservice.oauth.kits.RedisKit;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:19
 * @Version 1.0
 * @Description: 当app_id + 时间+ nonce 做为key放入redis，
 * 挡住单位时间内的重放攻击，超时时间则由时间检查机制保护
 */
public class RedisAuthNonces implements AuthNonces {

    /** 前端时间差最大允许15分钟，单位秒  **/
    public static final long DEFAULT_MAX_TIMESTAMP_AGE = 15 * 60;


    /**
     *  redis key 串模版
     */
    private String format = "SIXI_NONCES#$s#%s#%s";

    protected long maxTimestampAgeMsec;

    @Autowired
    RedisKit redisKit;

    public RedisAuthNonces(){
        this(DEFAULT_MAX_TIMESTAMP_AGE);
    }

    public RedisAuthNonces(long  maxAge){
        maxTimestampAgeMsec = maxAge;
        System.out.println("create redis auth nonce");

    }


    @Override
    public void validateNonce(long timestamp, String appId, String nonce) throws AuthProblemException {

        String nonceName = String.format(format,String.valueOf(timestamp),appId,nonce);
        boolean isValid = redisKit.setIfAbsent(nonceName,"n",maxTimestampAgeMsec);
        if (!isValid) {
            throw new AuthProblemException(Auth.Problems.NONCE_USED);
        }
    }
}
