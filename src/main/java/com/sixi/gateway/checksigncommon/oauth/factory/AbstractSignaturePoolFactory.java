package com.sixi.gateway.checksigncommon.oauth.factory;

import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

import java.security.Signature;

/**
 * @Author: ZY
 * @Date: 2019/8/7 9:20
 * @Version 1.0
 * @Description: 签名类对象池生成工厂类
 */
public abstract class AbstractSignaturePoolFactory extends BaseKeyedPoolableObjectFactory<AuthConsumer, Signature> {

    /**
     * 创建一个实例到对象池
     *
     * @param authConsumer 密钥
     * @return 签名类
     */
    @Override
    public Signature makeObject(AuthConsumer authConsumer) {
        return createSignature(authConsumer);
    }

    /**
     * 创建签名串抽象方法
     *
     * @param authConsumer 密钥
     * @return 签名处理类
     */
    public abstract Signature createSignature(AuthConsumer authConsumer);

}
