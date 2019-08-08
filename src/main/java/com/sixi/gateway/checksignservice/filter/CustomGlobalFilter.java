package com.sixi.gateway.checksignservice.filter;

import com.sixi.gateway.checksignservice.constant.AuthCodeConast;
import com.sixi.gateway.checksignservice.constant.FailedResponse;
import com.sixi.gateway.checksignservice.oauth.Auth;
import com.sixi.gateway.checksignservice.oauth.AuthMessage;
import com.sixi.gateway.checksignservice.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksignservice.oauth.json.SingleJSON;
import com.sixi.gateway.checksignservice.oauth.method.AuthValidator;
import io.netty.buffer.ByteBufAllocator;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: ZY
 * @Date: 2019/8/5 13:33
 * @Version 1.0
 * @Description: 全局过滤器
 */

@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    static final String OAUTH_APP_ID_NAME = "app_id";

    static final String OAUTH_SIGN_METHOD_NAME = "sign_type";

    static final String OAUTH_SIGN_NAME = "sign";

    @Resource
    AuthValidator validator;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String method = serverHttpRequest.getMethodValue();
        if ("POST".equals(method)) {
            //从请求里获取Post请求体
            String bodyStr = resolveBodyFromRequest(serverHttpRequest);
            //获取想要的消息

            System.out.println("***********************************************" + bodyStr + "          ********************");

            //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
            URI uri = serverHttpRequest.getURI();
            ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
            DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

            request = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };
            //封装request，传给下一级
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    private DataBuffer stringBuffer(String value) {
        if (StringUtils.isNotEmpty(value)) {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
            DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
            buffer.write(bytes);
            return buffer;
        }
        return null;
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     *
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }


    /**
     * 扫描请求数据, 按json 一级结构解析成authMessage
     *
     * @param in 数据流
     * @return AuthMessage对象
     * @throws AuthProblemException 解包错误
     * @throws IOException          请求数据读取错误
     */
    private AuthMessage readMessage(InputStream in) throws AuthProblemException, IOException {

        String postBody = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
        Object obj = SingleJSON.paser(postBody);
        if (obj instanceof Map) {
            return new AuthMessage(((Map<String, ?>) obj).entrySet());
        } else {
            AuthProblemException problem = new AuthProblemException(Auth.Problems.PARAMETER_ABSENT);
            problem.setParameter(Auth.Problems.OAUTH_PARAMETERS_ABSENT, Auth.OAUTH_SIGNATURE);
            throw problem;
        }
    }
    /**
     * AuthProblemException 转化成FailedResponse
     * 当缺少参数时，将具体的参数拼装成message,其他从全响的失败报文集合中获取。
     *
     * @param exception 异常
     * @return 响应
     */
    private FailedResponse getAuthFailedResponse(AuthProblemException exception) {
        String problem = exception.getProblem();
        FailedResponse failedResponse = getAuthFailedCodeMap().get(problem);
        if (failedResponse == null) {
            if (Auth.Problems.PARAMETER_ABSENT.equals(problem)) {
                String missParam = (String) exception.getParameters().get(Auth.Problems.OAUTH_PARAMETERS_ABSENT);
                String message = String.format(AuthCodeConast.RESP_MSG_MISSING_SIGNATURE_PARAM, missParam);
                return new FailedResponse(AuthCodeConast.RESP_CD_MISSING_SIGNATURE_PARAM, message);
            } else {
                return FailedResponse.UNKNOWN_ERROR;
            }
        }
        return failedResponse;
    }

    HashMap<String, FailedResponse> authFailedCodeMap = null;

    /**
     * 授权的失败的响应集合
     *
     * @return
     */
    private Map<String, FailedResponse> getAuthFailedCodeMap() {

        if (authFailedCodeMap == null) {
            HashMap map = new HashMap<String, FailedResponse>();

            map.put(Auth.Problems.NONCE_USED,
                    new FailedResponse(AuthCodeConast.RESP_CD_NONCE_USED, AuthCodeConast.RESP_MSG_NONCE_USED));

            map.put(Auth.Problems.SIGNATURE_INVALID,
                    new FailedResponse(AuthCodeConast.RESP_CD_INVALID_SIGNATURE, AuthCodeConast.RESP_MSG_INVALID_SIGNATURE));

            map.put(Auth.Problems.SIGNATURE_METHOD_REJECTED,
                    new FailedResponse(AuthCodeConast.RESP_CD_INVALID_SIGNATURE_TYPE, AuthCodeConast.RESP_MSG_INVALID_SIGNATURE_TYPE));

            map.put(Auth.Problems.CONSUMER_KEY_REFUSED,
                    new FailedResponse(AuthCodeConast.RESP_CD_REFUSED_APP_ID, AuthCodeConast.RESP_MSG_REFUSED_APP_ID));

            map.put(Auth.Problems.CONSUMER_KEY_REJECTED,
                    new FailedResponse(AuthCodeConast.RESP_CD_REFUSED_APP_ID, AuthCodeConast.RESP_MSG_REFUSED_APP_ID));

            map.put(Auth.Problems.CONSUMER_KEY_UNKNOWN,
                    new FailedResponse(AuthCodeConast.RESP_CD_INVALID_APP_ID, AuthCodeConast.RESP_MSG_INVALID_APP_ID));

            map.put(Auth.Problems.TIMESTAMP_REFUSED,
                    new FailedResponse(AuthCodeConast.RESP_CD_INVALID_TIMESTAMP, AuthCodeConast.RESP_MSG_INVALID_TIMESTAMP));


        }
        return authFailedCodeMap;
    }

    @Override
    public int getOrder() {
        // 最高级(数值最小)和最低级(数值最大)
        return Ordered.LOWEST_PRECEDENCE;
    }


}
