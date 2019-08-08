package com.sixi.gateway.checksigncommon.oauth;

import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksigncommon.oauth.method.ISignatureMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:06
 * @Version 1.0
 * @Description:
 */
public class SignerBuilder {
    static SignerBuilder instance;

    private Map<String, Class<? extends ISignatureMethod>> signerClassMap =
            new ConcurrentHashMap<String, Class<? extends ISignatureMethod>>();


    private final Map<String, ISignatureMethod> cache =
            new ConcurrentHashMap<String, ISignatureMethod>();

    static{
        instance = new SignerBuilder();
        registerMethodClass("MD5", MD5Signer.class);
        registerMethodClass("SHA1", Sha1Signer.class);
        registerMethodClass("RSA", RsaSigner.class);
    }

    private SignerBuilder() {

    }


    private ISignatureMethod getSigner(String signMethodName) {
        ISignatureMethod signer = null;
        if (!cache.containsKey(signMethodName)) {
            synchronized (cache) {
                if (!cache.containsKey(signMethodName)) {
                    signer = createSigner(signMethodName);
                    cache.put(signMethodName, signer);
                }
            }
        } else {
            signer = cache.get(signMethodName);
        }
        return signer;
    }

    private ISignatureMethod createSigner(String signMethodName) {
        try {
            Class<?> clazz = signerClassMap.get(signMethodName);
            if (clazz != null) {
                return (ISignatureMethod) clazz.newInstance();
            }
            AuthProblemException problem = new AuthProblemException(Auth.Problems.SIGNATURE_METHOD_REJECTED);
            String acceptable = Auth.percentEncode(signerClassMap.keySet());
            if (acceptable.length() > 0) {
                problem.setParameter("oauth_acceptable_signature_methods", acceptable);
            }
            throw problem;
        } catch (InstantiationException e) {
            throw new AuthException(e);
        } catch (IllegalAccessException e) {
            throw new AuthException(e);
        }
    }


    public static ISignatureMethod newSigner(String methodName) throws AuthException {
        return instance.getSigner(methodName);
    }

    /**
     * 获取authMessage的签名类
     * @param message 消息
     * @return 对应的签名类
     * @throws AuthException 不支持的signType
     */
    public static ISignatureMethod newSigner(AuthMessage message) throws AuthException {
        message.requireParameters(Auth.OAUTH_SIGN_TYPE);
        String signMethodName = message.getParameter(Auth.OAUTH_SIGN_TYPE);
        return instance.getSigner(signMethodName);
    }



    /**
     * Subsequently, newMethod(name) will attempt to instantiate the given
     * class, with no constructor parameters.
     */
    public static void registerMethodClass(String name, Class<? extends ISignatureMethod> clazz) {
        if(instance == null){
            instance = new SignerBuilder();
        }
        if (clazz == null) {
            unregisterMethod(name);
        } else {
            instance.signerClassMap.put(name, clazz);
        }
    }

    /**
     * Subsequently, newMethod(name) will fail.
     */
    public static void unregisterMethod(String name) {
        if(instance == null){
            instance = new SignerBuilder();
        }
        instance.signerClassMap.remove(name);
    }
}
