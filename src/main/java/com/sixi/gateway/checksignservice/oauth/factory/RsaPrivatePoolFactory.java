package com.sixi.gateway.checksignservice.oauth.factory;

import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;

import java.security.Signature;

/**
 * @Author: ZY
 * @Date: 2019/8/7 9:37
 * @Version 1.0
 * @Description: rsa私钥池工厂类
 */
public class RsaPrivatePoolFactory extends AbstractRsaSignaturePoolFactory{

    public RsaPrivatePoolFactory(){
        super();
    }

    public RsaPrivatePoolFactory(String algorithm){
        super(algorithm);
    }

    @Override
    public Signature createSignature(AuthConsumer authConsumer) {
        return createRSAPrivateSignature(authConsumer);
    }
}
