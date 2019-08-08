/*
  Project Name:sixipay-platform
  File Name:FailedResponse.java
  Package Name:com.sixi.pay.webapi.domain.vo
  Date:2019/6/25 1:47 PM
  Copyright (c) 2019, www.sixi.com All Rights Reserved.
 */
package com.sixi.gateway.checksigncommon.constant;


/**
 * ClassName:FailedResponse <br/>
 * Function: 失败响应. <br/>
 * Reason:	 失败响应，定义常规失败类型. <br/>
 * Date:     2019/6/25 1:47 PM <br/>
 *
 * @author luoyi
 * @version 1.0
 * @since JDK 1.6
 */
public class FailedResponse extends Response {

    /**
     * 服务不可用
     */
    public static FailedResponse UNKNOWN_ERROR =
            new FailedResponse(CodeConst.RSP_CD_UNKNOWN_ERROR,CodeConst.RSP_MSG_UNKNOWN_ERROR);
    /**
     * 网关服务不可用
     */
    public static FailedResponse GATEWAY_UNKNOWN_ERROR =
            new FailedResponse(CodeConst.RSP_CD_GATEWAY_UNKNOWN_ERROR,CodeConst.RSP_MSG_GATEWAY_UNKNOWN_ERROR);
    /**
     * 非法请求格式
     */
    public static FailedResponse INVALID_FORMAT =
            new FailedResponse(CodeConst.RSP_CD_INVALID_FORMAT,CodeConst.RSP_MSG_INVALID_FORMAT);
    /**
     * 不支持的media_type
     */
    public static FailedResponse INVALID_MEDIA_TYPE =
            new FailedResponse(CodeConst.RSP_CD_INVALID_MEDIA_TYPE,CodeConst.RSP_MSG_INVALID_MEDIA_TYPE);

    /**
     * 不存在的Method方法
     */
    public static FailedResponse INVALID_METHOD =
            new FailedResponse(CodeConst.RSP_CD_INVALID_METHOD,CodeConst.RSP_MSG_INVALID_METHOD);

    /**
     * 缺少method方法
     */
    public static FailedResponse MISSING_METHOD =
            new FailedResponse(CodeConst.RSP_CD_MISSING_METHOD,CodeConst.RSP_MSG_MISSING_METHOD);

    /**
     * 请求消息不可读
     */
    public static FailedResponse MESSAGE_NOT_READABLE =
            new FailedResponse(CodeConst.RSP_CD_NOT_READABLE,CodeConst.RSP_MSG_NOT_READABLE);


    /**
     * 请求消息不可读
     */
    public static FailedResponse INVALID_HTTP_METHOD =
            new FailedResponse(CodeConst.RSP_CD_INVALID_HTTP_METHOD,CodeConst.RSP_MSG_INVALID_HTTP_METHOD);


    /**
     * 缺少参数
     * @param paramName
     * @return
     */
    public static FailedResponse getMissParamResponse(String paramName){
        String msg = String.format(CodeConst.RSP_MSG_MISSING_PARAM,paramName);
        return new FailedResponse(CodeConst.RSP_CD_MISSING_PARAM,msg);
    }

    /**
     * 非法参数
     * @param paramName
     * @return
     */
    public static FailedResponse getInvalidParamResponse(String paramName){
        String msg = String.format(CodeConst.RSP_MSG_INVALID_PARAM,paramName);
        return new FailedResponse(CodeConst.RSP_CD_INVALID_PARAM,msg);
    }

    public FailedResponse(int code, String msg) {
        super(code, msg);
    }

    public FailedResponse(String code, String msg) {
        super(code, msg);
    }


}
