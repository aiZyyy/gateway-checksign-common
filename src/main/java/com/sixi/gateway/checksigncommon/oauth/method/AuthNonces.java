package com.sixi.gateway.checksigncommon.oauth.method;

import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:18
 * @Version 1.0
 * @Description: auth 随机变量,校验nonce是否重复
 */
public interface AuthNonces {

    /**
     * 校验nonce 是否存在重复
     * @param timestamp 时间戳
     * @param appId appId
     * @param nonce 随机变量
     * @throws AuthProblemException nonce 失败原因
     */
    void validateNonce(long timestamp, String appId, String nonce) throws AuthProblemException;
}
