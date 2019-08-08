package com.sixi.gateway.checksigncommon.oauth.method.impl;

import com.sixi.gateway.checksigncommon.oauth.Auth;
import com.sixi.gateway.checksigncommon.oauth.AuthMessage;
import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksigncommon.oauth.method.ISignatureMethod;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:08
 * @Version 1.0
 * @Description: 签名方法抽象实现类,实现通用方法
 */
public abstract class AbstractAuthSignatureMethod implements ISignatureMethod {
    /**
     * 签名方案字段
     */
    private String signatureMethodName = Auth.OAUTH_SIGN_TYPE;

    /**
     * 签名字段名
     */
    private String signatureName = Auth.OAUTH_SIGNATURE;


    Logger logger = LoggerFactory.getLogger(AbstractAuthSignatureMethod.class);


    /**
     * 对待签名串进行签名
     * @param baseString 待签名串
     * @param key         接入应用签名钥密
     * @return 签名
     * @throws AuthException 签名异常，可能签名失败
     */
    protected abstract String getSignature(String baseString, AuthConsumer key) throws AuthException;

    /**
     * 将待签名串的签名值进行验签判断
     * @param signature  签名值
     * @param baseString 待签名串
     * @param key        接入应用的签名密钥
     * @return  验签是否通过
     * @throws AuthException 签名计算异常
     */
    protected abstract boolean isValid(String signature, String baseString, AuthConsumer key) throws AuthException;


    @Override
    public void sign(AuthMessage message, AuthConsumer consumer) throws AuthException {
        message.addParameter(getSignatureName(), getSignature(message, consumer));
    }


    @Override
    public String getSignature(AuthMessage message, AuthConsumer consumer) throws AuthException {
        String baseString = getBaseString(message, consumer);
        String signature = getSignature(baseString, consumer);
        if (logger.isDebugEnabled()) {
            logger.debug("baseString:" + baseString);
            logger.debug("signature:" + signature);
        }
        return signature;
    }


    @Override
    public void validate(AuthMessage message, AuthConsumer key) throws AuthException {
        message.requireParameters(getSignatureName());
        String signature = message.getParameter(getSignatureName());
        String baseString = getBaseString(message, key);
        if (!isValid(signature, baseString, key)) {
            AuthProblemException problem = new AuthProblemException("signature_invalid");
            problem.setParameter("signature", signature);
            problem.setParameter("signature_base_string", baseString);
            problem.setParameter("signature_method", message.getParameter(getSignatureMethodName()));
            throw problem;
        }
    }



    /**
     * 获取签名名称
     * @return
     */
    public String getSignatureName(){
        return signatureName;
    }

    /**
     * 设置签名名称
     * @param signatureName
     */
    public void setSignatureName(String signatureName){
        this.signatureName = signatureName;
    }

    /**
     * 获取签名方法名称
     * @return
     */
    public String getSignatureMethodName(){
        return signatureMethodName;
    }

    /**
     * 设置签名方法名称
     * @param methodName
     */
    public void setSignatureMethodName(String methodName){
        this.signatureMethodName = methodName;
    }


    /**
     * getBaseString:组装待签名串. <br/>
     *
     * @param message 消息体
     * @param key     接入应用密钥
     * @return 待签名串
     * @since JDK 1.6
     */
    public String getBaseString(AuthMessage message, AuthConsumer key) {
        List<Map.Entry<String, String>> parameters = message.getParameters();
        try {
            return normalizeParameters(parameters, key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * normalizeParameters 参数串进行排序拼接成待签名字串
     *
     * @param parameters 参数列表
     * @param key        接应用密钥
     * @return           待签名串
     * @throws IOException  拼装字串IO异常
     */
    protected String normalizeParameters(Collection<? extends Map.Entry<String, String>> parameters,
                                         AuthConsumer key) throws IOException{
        if (parameters == null) {
            return "";
        }
        List<ComparableParameter> p = new ArrayList<ComparableParameter>(parameters.size());
        for (Map.Entry<String, String> parameter : parameters) {
            //sign的字段不需要加入到待签名串
            if(!parameter.getKey().equals(getSignatureName())){
                p.add(new ComparableParameter(parameter));
            }
        }
        Collections.sort(p);
        return Auth.formEncode(getParameters(p));
    }




    private List<Map.Entry<String, String>> getParameters(Collection<ComparableParameter> parameters) {
        if (parameters == null) {
            return null;
        }
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(parameters.size());
        for (ComparableParameter parameter : parameters) {
            list.add(parameter.value);
        }
        return list;
    }

    private static final String BASE64_ENCODING = "ISO-8859-1";


    public byte[] decodeBase64(byte[] s) {

        return Base64.decodeBase64(s);
    }

    /**
     * 将Base64字串转化成byte[]
     *
     * @param s  Base64字符串
     * @return  byte[]
     */
    public byte[] decodeBase64(String s) {
        byte[] b = null;
        try {
            b = s.getBytes(BASE64_ENCODING);
        } catch (UnsupportedEncodingException e) {
            System.err.println(e + "");
            b = s.getBytes();
        }
        return Base64.decodeBase64(b);
    }

    /**
     * 将byte[] 转成Base64编码字符串
     *
     * @param b 待转化byte[]
     * @return   Base64串
     */
    public String base64Encode(byte[] b) {
        byte[] b2 = Base64.encodeBase64(b);
        try {
            return new String(b2, BASE64_ENCODING);
        } catch (UnsupportedEncodingException e) {
            System.err.println(e + "");
        }
        return new String(b2);
    }


    /**
     * 比较两个数组是否相同
     *
     * @param a
     * @param b
     * @return 比较结果
     */
    public boolean equals(byte[] a, byte[] b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        } else if (b.length <= 0) {
            return a.length <= 0;
        }
        byte diff = (byte) ((a.length == b.length) ? 0 : 1);
        int j = 0;

        for (int i = 0; i < a.length; ++i) {
            diff |= a[i] ^ b[j];
            j = (j + 1) % b.length;
        }
        return diff == 0;
    }


    /**
     * 比较两个字串是否相同
     * @param x
     * @param y
     * @return
     */
    public boolean equals(String x, String y) {
        if (x == null) {
            return y == null;
        }else if (y == null) {
            return false;
        }else if (y.length() <= 0) {
            return x.length() <= 0;
        }
        char[] a = x.toCharArray();
        char[] b = y.toCharArray();
        char diff = (char) ((a.length == b.length) ? 0 : 1);
        int j = 0;
        for (int i = 0; i < a.length; ++i) {
            diff |= a[i] ^ b[j];
            j = (j + 1) % b.length;
        }
        return diff == 0;
    }


    private static class ComparableParameter implements Comparable<ComparableParameter> {

        final Map.Entry<String, String> value;

        private final String key;

        ComparableParameter(Map.Entry<String, String> value) {
            this.value = value;
            this.key = toString(value.getKey());
        }

        private static String toString(Object from) {
            return (from == null) ? null : from.toString();
        }

        @Override
        public int compareTo(ComparableParameter that) {
            return this.key.compareTo(that.key);
        }

        @Override
        public String toString() {
            return key;
        }

    }
}
