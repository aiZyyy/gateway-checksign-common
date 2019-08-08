/*
  Project Name:sixipay-platform
  File Name:CodeConst.java
  Package Name:com.sixi.pay.webapi.constant
  Date:2019/6/25 1:36 PM
  Copyright (c) 2019, www.sixi.com All Rights Reserved.
 */
package com.sixi.gateway.checksigncommon.constant;

/**
 * ClassName:CodeConst <br/>
 * Function: 错误码常量. <br/>
 * Reason:	 错误码常量. <br/>
 * Date:     2019/6/25 1:36 PM <br/>
 *
 * @author luoyi
 * @version 1.0
 * @since JDK 1.6
 */
public class CodeConst {


    /**
     * 服务不可用，未知错误
     */
    public static final String RSP_CD_UNKNOWN_ERROR = "100001";

    /**
     * 服务不可用，网关错误
     */
    public static final String RSP_CD_GATEWAY_UNKNOWN_ERROR = "100002";

    /**
     * 非法的请求格式
     */
    public static final String RSP_CD_INVALID_FORMAT = "100003";

    /**
     * 不支持的MediaType
     */
    public static final String RSP_CD_INVALID_MEDIA_TYPE = "100004";

    /**
     * 非法的请求参数
     */
    public static final String RSP_CD_INVALID_PARAM = "100005";

    /**
     * 缺少的必要请求参数
     */
    public static final String RSP_CD_MISSING_PARAM = "100006";

    /**
     * 缺少method
     */
    public static final String RSP_CD_MISSING_METHOD = "100007";

    /**
     * 消息不可读
     */
    public static final String RSP_CD_NOT_READABLE = "100008";

    /**
     * 不存在方法名
     */
    public static final String RSP_CD_INVALID_METHOD = "100009";

    /**
     * 非法的http method
     */
    public static final String RSP_CD_INVALID_HTTP_METHOD = "100010";



    public static final String RSP_MSG_UNKNOWN_ERROR = "服务不可用，请稍后再试";

    public static final String RSP_MSG_GATEWAY_UNKNOWN_ERROR = "网关服务不可用，请稍后再试";

    public static final String RSP_MSG_INVALID_FORMAT = "非法请求格式,请检查请求报文";

    public static final String RSP_MSG_INVALID_MEDIA_TYPE = "不支持的请求数据类型! 请使用application/json";

    public static final String RSP_MSG_INVALID_PARAM = "参数无效，检查参数，格式不对、非法值、越界等!%s";

    public static final String RSP_MSG_MISSING_PARAM = "检查请求参数，缺少%s参数";

    public static final String RSP_MSG_MISSING_METHOD = "请求参数里面必须要有method参数";

    public static final String RSP_MSG_NOT_READABLE = "无法读取请求数据 请检查入参数据是否正确!";

    public static final String RSP_MSG_INVALID_METHOD = "不存在方法名,检查入参method是否正确";

    public static final String RSP_MSG_INVALID_HTTP_METHOD = "不支持的Http Method方法";
}
