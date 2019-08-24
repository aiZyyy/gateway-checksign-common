package com.sixi.gateway.checksigncommon.oauth;


import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import com.sixi.gateway.checksigncommon.oauth.method.impl.AbstractAuthSignatureMethod;
import com.sixi.gateway.checksigncommon.oauth.utils.RSAUtils;
import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:07
 * @Version 1.0
 * @Description: 对请求体进行排序后得到BaseString，
 * BaseString拼上AppSecret得到待签名串,使用MD5对待签名串进行摘要
 */
public class MD5Signer extends AbstractAuthSignatureMethod {
    /**
     * ISO-8859-1 or US-ASCII would work, too.
     */
    private static final String ENCODING = Auth.ENCODING;

    @Override
    protected String getSignature(String baseString, AuthConsumer authConsumer) {
        try {
            byte[] encryptMD5 = encryptMD5(baseString);
            RSAPrivateKey privateKey = RSAUtils.getPrivateKey(authConsumer.getSecret());
            byte[] encrtpyByPrivateKey = RSAUtils.encrtpyByPrivateKey(encryptMD5, privateKey);
            return Base64.encodeBase64String(encrtpyByPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("签名发生异常", e);
        }
    }

    @Override
    protected boolean isValid(String sign, String baseString, AuthConsumer authConsumer) throws AuthException {
        try {
            byte[] encryptMD5 = encryptMD5(baseString);
            RSAPublicKey publicKey = RSAUtils.getPublicKey(authConsumer.getSecret());
            byte[] decodePublicKey = RSAUtils.decodePublicKey(Base64.decodeBase64(sign), publicKey);
            return equals(Base64.encodeBase64String(encryptMD5), Base64.encodeBase64String(decodePublicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    //通过MD5加密
    private static byte[] encryptMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] digest2 = digest.digest(str.getBytes());
        return digest2;
    }

}
