package com.sixi.gateway.checksignservice.oauth;


import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksignservice.oauth.exception.AuthException;
import com.sixi.gateway.checksignservice.oauth.method.impl.AbstractAuthSignatureMethod;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:14
 * @Version 1.0
 * @Description: 对请求体进行排序后得到BaseString，
 * BaseString拼上AppSecret得到待签名串,使用SHA1对待签名串进行摘要
 */
public class Sha1Signer extends AbstractAuthSignatureMethod {
    /**
     * ISO-8859-1 or US-ASCII would work, too.
     */
    private static final String ENCODING = Auth.ENCODING;

    @Override
    protected String getSignature(String baseString, AuthConsumer key) throws AuthException {
        baseString = baseString.concat(key.getSecret());
        return Hex.encodeHexString(computeSignature(baseString));

    }

    @Override
    protected boolean isValid(String signature, String baseString, AuthConsumer key) throws AuthException {
        baseString = baseString.concat(key.getSecret());
        byte[] expected = computeSignature(baseString);
        try {
            byte[] actual = Hex.decodeHex(signature);
            return equals(expected, actual);
        } catch (DecoderException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected byte[] computeSignature(String baseString) {
        byte[] b;
        try {
            b = baseString.getBytes(ENCODING);
        } catch (UnsupportedEncodingException e) {
            b = baseString.getBytes();
        }
        return DigestUtils.sha1(b);
    }
}

