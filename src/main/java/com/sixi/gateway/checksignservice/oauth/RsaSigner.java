package com.sixi.gateway.checksignservice.oauth;

import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksignservice.oauth.exception.AuthException;
import com.sixi.gateway.checksignservice.oauth.factory.RsaPrivatePoolFactory;
import com.sixi.gateway.checksignservice.oauth.factory.RsaPublicPoolFactory;
import com.sixi.gateway.checksignservice.oauth.method.impl.AbstractAuthSignatureMethod;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import java.io.UnsupportedEncodingException;
import java.security.Signature;
import java.security.SignatureException;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:16
 * @Version 1.0
 * @Description: 对请求体进行排序后得到BaseString，
 * 使用RSAWithSHA1对待签名串进行摘要和验证
 */
public class RsaSigner extends AbstractAuthSignatureMethod {

    /**
     * ISO-8859-1 or US-ASCII would work, too.
     */
    private static final String ENCODING = Auth.ENCODING;

    /**
     * SHA1WithRSA 签名算法
     */
    public static final String SHA1_WITH_RSA = "SHA1WithRSA";

    /**
     * SHA256WithRSA签名算法
     */
    public static final String SHA256_WITH_RSA = "SHA256WithRSA";

    /**
     * 公钥签名对象池
     */
    private GenericKeyedObjectPool<AuthConsumer, Signature> pubSignaturePool;

    /**
     * 私钥签名对象池
     */
    private GenericKeyedObjectPool<AuthConsumer, Signature> priSignaturePool;


    public RsaSigner() {
        this(SHA1_WITH_RSA);
    }

    public RsaSigner(String algorithm) {
        RsaPrivatePoolFactory priPoolFactory = new RsaPrivatePoolFactory(algorithm);
        RsaPublicPoolFactory pubPoolFactory = new RsaPublicPoolFactory(algorithm);
        pubSignaturePool = new GenericKeyedObjectPool<>(pubPoolFactory);
        priSignaturePool = new GenericKeyedObjectPool<>(priPoolFactory);
    }


    @Override
    protected String getSignature(String baseString, AuthConsumer consumer) throws AuthException {
        // 私钥用于签名
        Signature signer = null;
        try {
            signer = priSignaturePool.borrowObject(consumer);
            // 计算签名，并返回其base64编号的签名
            return computeSignature(baseString, signer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException("RSA sign exception, baseString = " + baseString + "; charset = " + ENCODING, e);
        } finally {
            if (signer != null) {
                try {
                    priSignaturePool.returnObject(consumer, signer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected boolean isValid(String signature, String baseString, AuthConsumer consumer) throws AuthException {
        Signature signer = null;
        try {
            signer = pubSignaturePool.borrowObject(consumer);
            signer.update(getBytes(baseString));
            byte[] signByte = paddingBase64(signature);
            return signer.verify(Base64.decodeBase64(signByte));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException("RSA verify exception,baseString = " + baseString + "; charset = " + ENCODING, e);
        } finally {
            if(signer != null){
                try {
                    pubSignaturePool.returnObject(consumer, signer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String computeSignature(String baseString, Signature signature) throws SignatureException {

        signature.update(getBytes(baseString));
        byte[] signed = signature.sign();
        return base64Encode(signed);

    }

    private byte[] getBytes(String string) {
        try {
            return string.getBytes(ENCODING);
        } catch (UnsupportedEncodingException e) {
            return string.getBytes();
        }
    }

    private byte[] paddingBase64(String sign) {
        int len = sign.length();
        int step = 4;
        if (len % step == 0) {
            return sign.getBytes();
        } else {
            byte[] b = sign.getBytes();
            int paddingLen = (len / 4 + 1) * 4;
            byte[] padding = new byte[paddingLen];
            System.arraycopy(b, 0, padding, 0, b.length);
            for (int i = b.length; i < paddingLen; i++) {
                padding[i] = 61;
            }
            return padding;
        }
    }

}
