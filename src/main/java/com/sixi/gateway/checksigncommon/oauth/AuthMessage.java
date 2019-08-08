package com.sixi.gateway.checksigncommon.oauth;


import com.sixi.gateway.checksigncommon.oauth.domain.AuthConsumer;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthException;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;

import java.net.URISyntaxException;
import java.util.*;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:04
 * @Version 1.0
 * @Description: 授权消息体 对请求进行组装
 */
public class AuthMessage {

    private final List<Map.Entry<String, String>> parameters;

    private Map<String, String> parameterMap;

    private boolean parametersAreComplete = false;

    public AuthMessage() {
        parameters = new ArrayList<Map.Entry<String, String>>();
    }

    public AuthMessage(Collection<? extends Map.Entry<String, ?>> parameters) {
        if (parameters == null) {
            this.parameters = new ArrayList<Map.Entry<String, String>>();
        } else {
            this.parameters = new ArrayList<Map.Entry<String, String>>(parameters.size());
            for (Map.Entry<String, ?> p : parameters) {
                this.parameters.add(new Auth.Parameter(toString(p.getKey()), toString(p.getValue())));
            }
        }
    }

    private void beforeGetParameter() {
        if (!parametersAreComplete) {
            completeParameters();
            parametersAreComplete = true;
        }
    }

    /**
     * Finish adding parameters; for example read an HTTP response body and
     * parse parameters from it.
     */
    protected void completeParameters() {

    }

    public void addParameter(String key, String value) {
        addParameter(new Auth.Parameter(key, value));
    }

    public void addParameter(Map.Entry<String, String> parameter) {
        parameters.add(parameter);
        parameterMap = null;
    }

    public void addParameters(Collection<? extends Map.Entry<String, String>> parameters) {
        this.parameters.addAll(parameters);
        parameterMap = null;
    }

    public String getParameter(String name) {
        return getParameterMap().get(name);
    }

    public int getIntParameter(String name){
        String value =  getParameterMap().get(name);
        if(value != null){
            return Integer.parseInt(value);
        }else{
            return 0;
        }
    }

    public long getLongParameter(String name){
        String value =  getParameterMap().get(name);
        if(value != null){
            return Long.parseLong(value);
        }else{
            return 0;
        }
    }

    public Map<String, String> getParameterMap() {
        beforeGetParameter();
        if (parameterMap == null) {
            parameterMap = Auth.newMap(parameters);
        }
        return parameterMap;
    }

    public List<Map.Entry<String, String>> getParameters() {
        beforeGetParameter();
        return Collections.unmodifiableList(parameters);
    }

    public void requireParameters(String... names) throws AuthProblemException {
        Set<String> present = getParameterMap().keySet();
        List<String> absent = new ArrayList<String>();
        for (String required : names) {
            if (!present.contains(required)) {
                absent.add(required);
            }
        }
        if (!absent.isEmpty()) {
            AuthProblemException problem = new AuthProblemException(Auth.Problems.PARAMETER_ABSENT);
            problem.setParameter(Auth.Problems.OAUTH_PARAMETERS_ABSENT, Auth.percentEncode(absent));
            throw problem;
        }
    }

    private static final String toString(Object from) {
        return (from == null) ? null : from.toString();
    }

    /**
     * Add a signature to the message.
     *
     * @throws URISyntaxException
     */
    public void sign(AuthConsumer consumer) throws AuthException {
        SignerBuilder.newSigner(this).sign(this, consumer);
    }
}
