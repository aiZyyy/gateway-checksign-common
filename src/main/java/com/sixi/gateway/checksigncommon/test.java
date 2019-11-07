package com.sixi.gateway.checksigncommon;

import com.alibaba.fastjson.JSONObject;
import com.sixi.gateway.checksigncommon.oauth.AuthMessage;
import com.sixi.gateway.checksigncommon.oauth.SignerBuilder;
import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.method.ISignatureMethod;

/**
 * @Author: ZY
 * @Date: 2019/8/19 11:11
 * @Version 1.0
 * @Description:
 */
public class test {
    public static void main(String[] args) {
        AuthConsumer authConsumer = AuthConsumer.builder().key("app106196537394204672")
                .secret(
                        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYKYByKA77LDWOd+a8bBHxnWdyQWAnQ2Co6zk12BdYODB07DjugrOxBEnuEOxp/qmrcufuBsKz0FcsUm1sG1LqumLK202Ah97XlnNcfepehU5P27D6x1yuz7/jgXElcg0kc/23h2EBHiS9zuKMKla2otMbZAAXI7VKknCc3sDm6QIDAQAB"
                ).build();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sequence", "0123456789");
        jsonObject.put("app_id", "app106196537394204672");
        jsonObject.put("biz_content","{\"sixiId\":\"1010113588727210\",\"departmentId\":\"65\"}");
        long timeMillis = System.currentTimeMillis();
        System.out.println(timeMillis);
        jsonObject.put("timestamp", String.valueOf(timeMillis));
        jsonObject.put("sign_type", "MD5");
        jsonObject.put("charset", "utf-8");
        jsonObject.put("method", "staff.department.isdepartmentleader");
        jsonObject.put("version", "1.0");
        AuthMessage authMessage = new AuthMessage(jsonObject);
        ISignatureMethod rsa = SignerBuilder.newSigner(authMessage);
        String signature = rsa.getSignature(authMessage, authConsumer);
        System.out.println(signature);
    }
}
