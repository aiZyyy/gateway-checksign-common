package com.sixi.gateway.checksigncommon.oauth.exception;

/**
 * @Author: ZY
 * @Date: 2019/8/6 17:44
 * @Version 1.0
 * @Description:  签名或鉴签异常时，抛出该异常
 */
public class AuthException extends RuntimeException {

    protected AuthException() {
    }

    /**
     * 构造方法
     * @param message 异常描述
     */
    public AuthException(String message) {
        super(message);
    }


    /**
     * 构造方法
     * @param cause 异常
     */
    public AuthException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造方式
     * @param message 异常描述
     * @param cause   异常
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;
}
