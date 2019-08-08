package com.sixi.gateway.checksignservice.oauth.factory;


import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksignservice.oauth.exception.AuthException;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author: ZY
 * @Date: 2019/8/7 9:17
 * @Version 1.0
 * @Description: rsa签名类抽象工厂类
 */
public abstract class AbstractRsaSignaturePoolFactory extends AbstractSignaturePoolFactory {

    /**
     * SHA1WithRSA 签名算法
     */
    public static final String SHA1_WITH_RSA = "SHA1WithRSA";

    /**
     * SHA256WithRSA签名算法
     */
    public static final String SHA256_WITH_RSA = "SHA256WithRSA";

    /**
     * 签名算法
     */
    private String algorithm;

    /**
     * Base64编码
     */
    private static final String BASE64_ENCODING = "ISO-8859-1";

    public AbstractRsaSignaturePoolFactory() {
        this(SHA1_WITH_RSA);
    }

    public AbstractRsaSignaturePoolFactory(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 创建RSA私钥签名类 使用PKCS8格式私钥，用于对数据进行签名. <br/>
     *
     * @param consumer 应用密钥
     * @return private signature
     * @throws AuthException 密钥不正确
     */
    public Signature createRSAPrivateSignature(AuthConsumer consumer) throws AuthException {
        Signature signature;
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", consumer.getSecret());
            signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            return signature;
        } catch (Exception e) {
            throw new AuthException("New private RSASign ConsumerKey = " + consumer.getKey(), e);
        }
    }


    /**
     * 构建RSA公钥签名，用于校验签名是否正确
     *
     * @param consumer 应用密钥
     * @return public signature
     * @throws AuthException 密钥不正确
     */
    protected Signature createPublicSignature(AuthConsumer consumer) throws AuthException {
        Signature signature;
        try {
            PublicKey priKey = getPublicKeyFromX509("RSA", consumer.getSecret());
            signature = Signature.getInstance(algorithm);
            signature.initVerify(priKey);
            return signature;
        } catch (Exception e) {
            throw new AuthException("New public RSASign ConsumerKey = " + consumer.getKey(), e);
        }
    }


    /**
     * 通过pkc8私钥转化为privateKey
     *
     * @param algorithm  算法
     * @param privateKey 密钥
     * @return 私钥
     * @throws NoSuchAlgorithmException 密钥非法异常
     * @throws InvalidKeySpecException  密钥非法异常
     */
    protected PrivateKey getPrivateKeyFromPKCS8(String algorithm, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if ((privateKey == null) || algorithm == null) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] encodedKey = decodeBase64(privateKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    /**
     * 将x509格式公钥转化为publicKey
     *
     * @param algorithm 算法
     * @param publicKey 密钥
     * @return 公钥
     * @throws NoSuchAlgorithmException 密钥非法异常
     * @throws InvalidKeySpecException  密钥非法异常
     */
    protected PublicKey getPublicKeyFromX509(String algorithm, String publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if ((publicKey == null) || algorithm == null) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        //public key 使用Base64形式保存
        byte[] encodedKey = decodeBase64(publicKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }


    private byte[] decodeBase64(byte[] s) {
        return Base64.decodeBase64(s);
    }

    /**
     * 将Base64字串转化成byte[]
     *
     * @param s Base64字符串
     * @return byte[]
     */
    private byte[] decodeBase64(String s) {
        byte[] b;
        try {
            b = s.getBytes(BASE64_ENCODING);
        } catch (UnsupportedEncodingException e) {
            System.err.println(e + "");
            b = s.getBytes();
        }
        return Base64.decodeBase64(b);
    }
}
