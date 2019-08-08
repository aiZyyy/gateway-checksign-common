package com.sixi.gateway.checksignservice.oauth.exception;


import com.sixi.gateway.checksignservice.oauth.Auth;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ZY
 * @Date: 2019/8/6 17:46
 * @Version 1.0
 * @Description: 标准签名异常类,用于定义标准签名异常
 */
public class AuthProblemException extends AuthException{

    public static final String OAUTH_PROBLEM = "oauth_problem";
    /** The name of a parameter whose value is the HTTP request. */
    public static final String HTTP_REQUEST = "HTTP request";
    /** The name of a parameter whose value is the HTTP response. */
    public static final String HTTP_RESPONSE = "HTTP response";
    /** The name of a parameter whose value is the HTTP resopnse status code. */
    public static final String HTTP_STATUS_CODE = "HTTP status";
    /** The name of a parameter whose value is the response Location header. */
    public static final String HTTP_LOCATION = "Location";
    /**
     * The name of a parameter whose value is the Auth signature base string.
     */
    public static final String SIGNATURE_BASE_STRING = " base string";

    /** The name of a parameter whose value is the request URL. */
    public static final String URL = "URL";

    private final Map<String, Object> parameters = new HashMap<>();

    public AuthProblemException() {

    }

    public AuthProblemException(String problem) {
        this(null, problem);
    }

    public AuthProblemException(Integer httpCode, String problem) {

        super(problem);
        if (problem != null) {
            parameters.put(OAUTH_PROBLEM, problem);
        }
        if (httpCode != null) {
            parameters.put(HTTP_STATUS_CODE, httpCode);
        }

    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message != null) {
            return message;
        }
        message = getProblem();
        if (message != null) {
            return message;
        }
        Object response = getParameters().get(HTTP_RESPONSE);
        if (response != null) {
            message = response.toString();
            int eol = message.indexOf("\n");
            if (eol < 0) {
                eol = message.indexOf("\r");
            }
            if (eol >= 0) {
                message = message.substring(0, eol);
            }
            message = message.trim();
            if (message.length() > 0) {
                return message;
            }
        }
        response = getHttpStatusCode();
        return HTTP_STATUS_CODE + " " + response;
    }

    /**
     *
     * @param name
     * @param value
     */
    public void setParameter(String name, Object value) {
        getParameters().put(name, value);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * 获取OAuth授权失败的原因
     * @return 原因
     */
    public String getProblem() {
        return (String) getParameters().get(OAUTH_PROBLEM);
    }

    /**
     * 获取OAuth授权结果的的Http状态码
     * @return httpStatus
     */
    public int getHttpStatusCode() {
        Object code = getParameters().get(HTTP_STATUS_CODE);
        if (code == null) {
            return 400;
        } else if (code instanceof Number) {
            // the usual case
            return ((Number) code).intValue();
        } else {
            return Integer.parseInt(code.toString());
        }
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        try {
            final String eol = System.getProperty("line.separator", "\n");
            final Map<String, Object> parameters = getParameters();
            for (String key : new String[] { Auth.Problems.OAUTH_PROBLEM_ADVICE, URL, SIGNATURE_BASE_STRING }) {
                Object value = parameters.get(key);
                if (value != null) {
                    s.append(eol + key + ": " + value);
                }
            }
            Object msg = parameters.get(HTTP_REQUEST);
            if ((msg != null)) {
                s.append(eol + ">>>>>>>> " + HTTP_REQUEST + ":" + eol + msg);
            }
            msg = parameters.get(HTTP_RESPONSE);
            if (msg != null) {
                s.append(eol + "<<<<<<<< " + HTTP_RESPONSE + ":" + eol + msg);
            } else {
                for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                    String key = parameter.getKey();
                    if (Auth.Problems.OAUTH_PROBLEM_ADVICE.equals(key) || URL.equals(key)
                            || SIGNATURE_BASE_STRING.equals(key) || HTTP_REQUEST.equals(key)
                            || HTTP_RESPONSE.equals(key)) {
                        continue;
                    }
                    s.append(eol + key + ": " + parameter.getValue());
                }
            }
        } catch (Exception ignored) {
        }
        return s.toString();
    }

    private static final long serialVersionUID = 1L;
}
