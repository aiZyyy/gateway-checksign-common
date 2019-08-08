/*
  Project Name:sixipay-platform
  File Name:Response.java
  Package Name:com.sixi.pay.webapi.domain
  Date:2019/6/25 1:22 PM
  Copyright (c) 2019, www.sixi.com All Rights Reserved.
 */
package com.sixi.gateway.checksignservice.constant;

import lombok.Data;

/**
 * ClassName:Response <br/>
 * Function: 响应报文结构. <br/>
 * Reason:	 响应报文结构. <br/>
 * Date:     2019/6/25 1:22 PM <br/>
 *
 * @author luoyi
 * @version 1.0
 * @see
 * @since JDK 1.6
 */
@Data
public abstract class Response {

    private static final long serialVersionUID = 1L;

    private String code;

    private String msg;


    public Response(int code, String msg) {
        this.code = String.format("%06d", code);
        this.msg = msg;
    }

    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
