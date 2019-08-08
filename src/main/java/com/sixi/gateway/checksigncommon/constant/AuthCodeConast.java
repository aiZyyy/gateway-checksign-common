/*
  Project Name:sixipay-platform
  File Name:AuthCodeConast.java
  Package Name:com.sixi.pay.gateway.constant
  Date:2019/6/26 1:25 PM
  Copyright (c) 2019, www.sixi.com All Rights Reserved.
 */
package com.sixi.gateway.checksigncommon.constant;

/**
 * ClassName:AuthCodeConast <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2019/6/26 1:25 PM <br/>
 *
 * @author luoyi
 * @version 1.0
 * @see
 * @since JDK 1.6
 */
public class AuthCodeConast {

    /**
     * 无效的签名方法
     */
    public static final String RESP_CD_INVALID_SIGNATURE_TYPE = "100021";

    /**
     * 缺少必要的签名参数
     */
    public static final String RESP_CD_MISSING_SIGNATURE_PARAM = "100022";

    /**
     * 无效签名
     */
    public static final String RESP_CD_INVALID_SIGNATURE = "100023";

    /**
     * 非法时间截
     */
    public static final String RESP_CD_INVALID_TIMESTAMP = "100024";

    /**
     * nonce 已使用
     */
    public static final String RESP_CD_NONCE_USED = "100025";

    /**
     * 非法的APPID
     */
    public static final String RESP_CD_INVALID_APP_ID="100026";

    /**
     * APPID已停止授权
     */
    public static final String RESP_CD_REFUSED_APP_ID="100027";


    /**
     * 无效签名类型
     */
    public static final String RESP_MSG_INVALID_SIGNATURE_TYPE= "无效的签名类型";

    /**
     * 缺少必要签名参数
     */
    public static final String RESP_MSG_MISSING_SIGNATURE_PARAM = "缺少必要签名参数 %s";
    /**
     * 无效签名
     */
    public static final String RESP_MSG_INVALID_SIGNATURE = "无效签名，请检查签名算法或公私钥";

    /**
     * 时间非法
     */
    public static final String RESP_MSG_INVALID_TIMESTAMP = "系统时间不正确,请检查手机时间是否正确";


    public static final String RESP_MSG_NONCE_USED = "nonce已使用";

    public static final String RESP_MSG_INVALID_APP_ID = "不存在的APP_ID";

    public static final String RESP_MSG_REFUSED_APP_ID = "APP_ID已停止授权";




}
