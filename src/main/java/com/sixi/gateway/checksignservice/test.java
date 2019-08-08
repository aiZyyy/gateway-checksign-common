package com.sixi.gateway.checksignservice;

import com.sixi.gateway.checksignservice.oauth.AuthMessage;
import com.sixi.gateway.checksignservice.oauth.RsaSigner;
import com.sixi.gateway.checksignservice.oauth.SignerBuilder;
import com.sixi.gateway.checksignservice.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksignservice.oauth.method.ISignatureMethod;
import com.sixi.gateway.checksignservice.oauth.method.impl.SimpleAuthNonces;
import com.sixi.gateway.checksignservice.oauth.method.impl.SimpleAuthValidator;

/**
 * @Author: ZY
 * @Date: 2019/8/7 14:03
 * @Version 1.0
 * @Description:
 */
public class test {

    public static void main(String[] args) {
//
//        AuthMessage authMessage = new AuthMessage();
        AuthConsumer authConsumer = AuthConsumer.builder().key("app83446232671981568")
                .secret("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCF8NZkJTr75XgvmSHbRBPuqI/6X7AB1OvytG6r0oKRM9SPE99pQ2welGsEQbHCKGRjjpXJ63/I/u5k/Ujr568jp2M7OUS0/3MgIQr5o6gplsCAg2fLqU9uopZSCK0UoVRzuI5c49Q9Ms6XHSkXZOuHHWNP3Bn5kv/Qesm6UyWq/PHIqJLkRGn4M3MJzGw2MgqKyAlH9Xcc20Kl0FDKOijsT0veTZrhE4lHoBx1GGl+0k+9CkY98ZOHxEtpvjvlZnmvi6oRLAuPd7k6j/1LIYxQmNF7MnfgOKgXBfJ4aJvgc3K8SyacIW18L4wBpYKvn2w37+4O5kRfZKEAA8QBiOwRAgMBAAECggEADxjdYX5czqXHzT2qdkePI5h2WhdNBHfWfI3f55gbxXgdHl90EC5cUnoSMfGhsqvQ3PPmaFMWHTa28kUmYgjEUfV9FuDWoLwmYtm2nfrXapCmdBXbrjYQUP0cinSV5bxlg4Gq8kdv1dITPS85QXzLm3gxiH/boajzErG0aT8UANjFwTAApldZ0SODKiTLaOMddEBV2a5+L6LPaKwwqjVJxm7XFmYMP8gqVQ7JjkFy2XO9k3Wn+wCZAYp8JOjBkQFohAvdzJQZQuc9TEsTH8XQjFjSb09+vUj7MIc71hlApQOfL81RBAMDP2bk+xzCWOx8NR3GmSDRUXdjz+umBwd9UQKBgQDXqGP7978QAW4ulHQfBw4NTSbb0v10TnbE3qJMHZ7S7ut08PJGGjcJKIoZcmnF8VwtXTpMI/X/dZJbHGFLz/f/70En0DHlKzPwXgxVehUfqDys3an/EKdhgJo89pjRw7ty09CwHJ6OWH2ZdnH7QQK74MQZb0z7W2S8v0BF4H9hlQKBgQCe/xxt8wBfdQsxEC/fwNBjbVKlDWgFpLyeX0MK3SBGXq5mbV6Q+rEJ3oTIt+4fwuE3/gqQagRPsOZvcIHUVTuv78F8hS7DNSaQJYU8N3UkZTgtnwaU2s434EHDZHSVzs3TXHPrWFCcuaT5F+lZsqWFfEmtxykxpeKq2k5b5o45jQKBgBPxydVqsyvaxgLezeJ0t5L5WKep2rDf9NH+OQpLDv45BGObSPy8vexF6Iez8h5RVRII9jRGeyC2EeQFOuZVBH2R9felpIeqODJ5sDBTixKTb5EZKvig5Kn6x8jtkXKM5JAZEMrIys0lV/BSaPYwq/0OK3Siu37fk/B0y4/q1D0FAoGADkSET5WDzg0ZwQBb229ZA+MWjHskf4Ul6+xOftYTBF2Qtt5PoyDr7B/MuJpQwwbg4P5qAkWRN3l/ZDarX9t6LLKOIVduN9Q4Lq0/RIURfP+cR/PCSm9MHBWCsT79e0gAjlv4hwD40ueR10x/Ay6VzeG2VuLNXogtS/mlGHgbAS0CgYEAxMirbOZRMGY8Hedzr9iHO3rv+RJn7Ul6yCIfrTepWTibnOvIwyrgdJdH0xsNVGa7zZMA0kLuyHZ/VwKrwpEoeg4DScI4dtk6wmvgZ0O8Tg0t/RTSAmil2o5eV8sqjctZ5LQVF2s/UkiFmhCmuRj8vfSYg8rzxwd7W3GeGnlTod0=").build();
//        RsaPrivatePoolFactory rsaPrivatePoolFactory = new RsaPrivatePoolFactory("SHA1WithRSA");
//        Signature signature = rsaPrivatePoolFactory.createSignature(authConsumer);
//        System.out.println(signature.toString());

        String base = "app_id=2014072300007148&biz_content={\"button\":[{\"actionParam\":\"ZFB_HFCZ\",\"actionType\":\"out\",\"name\":\"话费充值\"},{\"name\":\"查询\",\"subButton\":[{\"actionParam\":\"ZFB_YECX\",\"actionType\":\"out\",\"name\":\"余额查询\"},{\"actionParam\":\"ZFB_LLCX\",\"actionType\":\"out\",\"name\":\"流量查询\"},{\"actionParam\":\"ZFB_HFCX\",\"actionType\":\"out\",\"name\":\"话费查询\"}]},{\"actionParam\":\"http://m.alipay.com\",\"actionType\":\"link\",\"name\":\"最新优惠\"}]}&charset=GBK&method=alipay.mobile.public.menu.add&sign_type=RSA2&timestamp=2014-07-24 03:07:50&version=1.0";
        AuthMessage authMessage = new AuthMessage();
        authMessage.addParameter("app_id", "app83446232671981568");
        authMessage.addParameter("biz_content", "[{\"actionParam\":\"ZFB_HFCZ\",\"actionType\":\"out\",\"name\":\"话费充值\"},{\"actionParam\":\"http://m.alipay.com\",\"actionType\":\"link\",\"name\":\"最新优惠\"}]}");
        long timeMillis = System.currentTimeMillis();
        System.out.println(timeMillis);
        authMessage.addParameter("timestamp", String.valueOf(timeMillis));

        authMessage.addParameter("sign_type", "RSA");
        authMessage.addParameter("charset", "utf-8");
        authMessage.addParameter("method", "alipay.mobile.public.menu.add");
        ISignatureMethod rsa = SignerBuilder.newSigner("RSA");
        RsaSigner rsaSigner = new RsaSigner();
        String signature = rsa.getSignature(authMessage, authConsumer);
        System.out.println("****************************************************************************************************************");
        System.out.println(rsa);
        System.out.println("****************************************************************************************************************");
        AuthMessage authMessage1 = new AuthMessage();
        authMessage1.addParameter("app_id", "app83446232671981568");
        authMessage1.addParameter("biz_content", "[{\"actionParam\":\"ZFB_HFCZ\",\"actionType\":\"out\",\"name\":\"话费充值\"},{\"actionParam\":\"http://m.alipay.com\",\"actionType\":\"link\",\"name\":\"最新优惠\"}]}");
        long timeMillis1 = System.currentTimeMillis();
        System.out.println(timeMillis1);
        authMessage1.addParameter("timestamp", String.valueOf(timeMillis1));
        authMessage1.addParameter("sign_type", "RSA");
        authMessage1.addParameter("charset", "utf-8");
        authMessage1.addParameter("method", "alipay.mobile.public.menu.add");
        authMessage1.addParameter("sign", signature);
        System.out.println(authMessage1.toString());

        AuthConsumer authConsumer1 = new AuthConsumer();
        authConsumer1.setKey("app83446232671981568");
        authConsumer1.setSecret("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhfDWZCU6++V4L5kh20QT7qiP+l+wAdTr8rRuq9KCkTPUjxPfaUNsHpRrBEGxwihkY46Vyet/yP7uZP1I6+evI6djOzlEtP9zICEK+aOoKZbAgINny6lPbqKWUgitFKFUc7iOXOPUPTLOlx0pF2Trhx1jT9wZ+ZL/0HrJulMlqvzxyKiS5ERp+DNzCcxsNjIKisgJR/V3HNtCpdBQyjoo7E9L3k2a4ROJR6AcdRhpftJPvQpGPfGTh8RLab475WZ5r4uqESwLj3e5Oo/9SyGMUJjRezJ34DioFwXyeGib4HNyvEsmnCFtfC+MAaWCr59sN+/uDuZEX2ShAAPEAYjsEQIDAQAB");

        SimpleAuthNonces authNonces = new SimpleAuthNonces();

        SimpleAuthValidator simpleAuthValidator = new SimpleAuthValidator(authNonces,30*60*100);
        simpleAuthValidator.validateMessage(authMessage1, authConsumer1);


    }
}
