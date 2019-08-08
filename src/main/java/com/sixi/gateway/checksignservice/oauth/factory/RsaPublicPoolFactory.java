package com.sixi.gateway.checksignservice.oauth.factory;

import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;

import java.security.Signature;

/**
 * @Author: ZY
 * @Date: 2019/8/7 9:41
 * @Version 1.0
 * @Description: rsa公钥池工厂类
 */
public class RsaPublicPoolFactory extends AbstractRsaSignaturePoolFactory{

    public RsaPublicPoolFactory(){
        super();
    }

    public RsaPublicPoolFactory(String algorithm){
        super(algorithm);
    }

    @Override
    public Signature createSignature(AuthConsumer authConsumer) {
        return createPublicSignature(authConsumer);
    }
}
