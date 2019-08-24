package com.sixi.gateway.checksigncommon.oauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Author: ZY
 * @Date: 2019/8/6 17:27
 * @Version 1.0
 * @Description: OAuth工具类, AuthValidateFilter 使用OAuth对接入应用进行鉴权验证
 */
public class Auth {
    public static final String ENCODING = "UTF-8";

    public static final boolean URL_ENCODE = false;


    /**
     * 接入应用ID
     **/
    public static final String OAUTH_APP_ID = "app_id";
    /**
     * 签名串
     **/
    public static final String OAUTH_SIGNATURE = "sign";
    /**
     * 时间截
     **/
    public static final String OAUTH_TIMESTAMP = "timestamp";
    /**
     * 签名方法
     **/
    public static final String OAUTH_SIGN_TYPE = "sign_type";
    /**
     * 业务跟踪号
     **/
    public static final String OAUTH_SEQUENCE = "sequence";

    //public static final String OAUTH_NONCE = "oauth_nonce";
    //public static final String OAUTH_OPENID = "oauth_openid";


    public static final String HMAC_SHA1 = "HMAC-SHA1";
    public static final String RSA_SHA1 = "RSA-SHA1";

    public static String characterEncoding = ENCODING;
    public static boolean urlEncode = URL_ENCODE;

    public static class Problems {
        /**
         * 缺少参数
         */
        public static final String PARAMETER_ABSENT = "parameter_absent";
        /**
         * 签名方法被拒绝
         */
        public static final String SIGNATURE_METHOD_REJECTED = "signature_method_rejected";
        /**
         * 签名无效
         */
        public static final String SIGNATURE_INVALID = "signature_invalid";
        /**
         * 时间戳被拒绝
         */
        public static final String TIMESTAMP_REFUSED = "timestamp_refused";
        /**
         * 已使用
         */
        public static final String NONCE_USED = "nonce_used";
        /**
         * 消费端key未知
         */
        public static final String CONSUMER_KEY_UNKNOWN = "consumer_key_unknown";
        /**
         * 消费端key拒绝
         */
        public static final String CONSUMER_KEY_REJECTED = "consumer_key_rejected";
        /**
         * 消费者密钥拒绝
         */
        public static final String CONSUMER_KEY_REFUSED = "consumer_key_refused";
        /**
         * oauth可接受的时间戳
         */
        public static final String OAUTH_ACCEPTABLE_TIMESTAMPS = "oauth_acceptable_timestamps";
        /**
         * oauth问题申明
         */
        public static final String OAUTH_PROBLEM_ADVICE = "oauth_problem_advice";
        /**
         * 没有oauth参数
         */
        public static final String OAUTH_PARAMETERS_ABSENT = "oauth_parameters_absent";
        /**
         * 编码无效
         */
        public static final String CHARSET_INVALID = "charset_invalid";

        /**
         * A map from an
         * <a href="http://wiki.oauth.net/ProblemReporting">oauth_problem</a>
         * value to the appropriate HTTP response code.
         */
        public static final Map<String, Integer> TO_HTTP_CODE = mapToHttpCode();

        private static Map<String, Integer> mapToHttpCode() {
            Integer badRequest = new Integer(400);
            Integer unauthorized = new Integer(401);
            Integer serviceUnavailable = new Integer(503);
            Map<String, Integer> map = new HashMap<String, Integer>(10);

            map.put(Problems.PARAMETER_ABSENT, badRequest);
            map.put(Problems.SIGNATURE_METHOD_REJECTED, badRequest);
            map.put(Problems.TIMESTAMP_REFUSED, badRequest);

            map.put(Problems.NONCE_USED, unauthorized);
            map.put(Problems.SIGNATURE_INVALID, unauthorized);
            map.put(Problems.CONSUMER_KEY_UNKNOWN, unauthorized);
            map.put(Problems.CONSUMER_KEY_REJECTED, unauthorized);

            map.put(Problems.CONSUMER_KEY_REFUSED, serviceUnavailable);

            return Collections.unmodifiableMap(map);

        }
    }

    /**
     * 按默认编码格式 将byte[] 转成string
     *
     * @param from byte[]
     * @return string
     */
    public static String decodeCharacters(byte[] from) {
        if (characterEncoding != null) {
            try {
                return new String(from, characterEncoding);
            } catch (UnsupportedEncodingException e) {
                System.err.println(e + "");
            }
        }
        return new String(from);
    }

    /**
     * 按默认编码方式将字符串编码成byte[]
     * 默认使用utf -8
     *
     * @param from string
     * @return byte[]
     */
    public static byte[] encodeCharacters(String from) {
        return encodeCharacters(from, characterEncoding);
    }

    /**
     * 按指定编码格式编码string
     *
     * @param from   string
     * @param encode 编码方式
     * @return byte[]
     */
    public static byte[] encodeCharacters(String from, String encode) {
        if (characterEncoding != null) {
            try {
                return from.getBytes(encode);
            } catch (UnsupportedEncodingException e) {
                System.err.println(e + "");
            }
        }
        return from.getBytes();
    }

    /**
     * 将value 进行URL编码
     *
     * @param values 待编码字串列表
     * @return url
     */
    public static String percentEncode(Iterable<String> values) {
        StringBuilder p = new StringBuilder();
        for (Object v : values) {
            if (p.length() > 0) {
                p.append("&");
            }
            p.append(Auth.percentEncode(toString(v)));
        }
        return p.toString();
    }

    /**
     * url 编码
     *
     * @param s 待url编码串
     * @return url
     */
    public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        try {
            String uri = URLEncoder.encode(s, ENCODING);
            // Auth encodes some characters differently:
            return uri.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
            // This could be done faster with more hand-crafted code.
        } catch (UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }

    /**
     * url 解码
     *
     * @param s 待url解码串
     * @return 明文
     */
    public static String decodePercent(String s) {
        try {
            return URLDecoder.decode(s, ENCODING);
            // This implements http://oauth.pbwiki.com/FlexibleDecoding
        } catch (UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }

    /**
     * Construct a form-urlencoded document containing the given sequence of
     * name/value pairs. Use Auth percent encoding (not exactly the encoding
     * mandated by HTTP).
     */
    public static String formEncode(Iterable<? extends Map.Entry<String, String>> parameters) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        formEncode(parameters, urlEncode, characterEncoding, b);
        return decodeCharacters(b.toByteArray());
    }


    /**
     * Write a form-urlencoded document into the given stream, containing the
     * given sequence of name/value pairs.
     */
    public static void formEncode(Iterable<? extends Map.Entry<String, String>> parameters, boolean urlencode,
                                  String characterEncoding, OutputStream into)
            throws IOException {
        if (parameters != null) {
            String key = null;
            String value = null;
            boolean first = true;
            for (Map.Entry<String, String> parameter : parameters) {
                if (first) {
                    first = false;
                } else {
                    into.write('&');
                }
                // key转小写字母
                key = toString(parameter.getKey());
                value = toString(parameter.getValue());
                if (urlencode) {
                    value = percentEncode(value);
                }
                into.write(encodeCharacters(key, characterEncoding));
                into.write('=');
                into.write(encodeCharacters(value, characterEncoding));
            }
        }
    }

    /**
     * Construct a Map containing a copy of the given parameters. If several
     * parameters have the same name, the Map will contain the first value,
     * only.
     */
    public static Map<String, String> newMap(Iterable<? extends Map.Entry<String, ?>> from) {
        Map<String, String> map = new HashMap<String, String>(10);
        if (from != null) {
            for (Map.Entry<String, ?> f : from) {
                String key = toString(f.getKey());
                map.put(key, toString(f.getValue()));
            }
        }
        return map;
    }

    /**
     * Construct a list of Parameters from name, value, name, value...
     */
    public static List<Parameter> newList(String... parameters) {
        List<Parameter> list = new ArrayList<Parameter>(parameters.length / 2);
        int step = 2;
        for (int i = 0; i + 1 < parameters.length; i += step) {
            list.add(new Parameter(parameters[i], parameters[i + 1]));
        }
        return list;
    }

    private static final String toString(Object from) {
        return (from == null) ? null : from.toString();
    }

    /**
     * A name/value pair.
     */
    public static class Parameter implements Map.Entry<String, String> {

        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private final String key;

        private String value;

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            try {
                return this.value;
            } finally {
                this.value = value;
            }
        }

        @Override
        public String toString() {
            return percentEncode(getKey()) + '=' + percentEncode(getValue());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Parameter that = (Parameter) obj;
            if (key == null) {
                if (that.key != null) {
                    return false;
                }
            } else if (!key.equals(that.key)) {
                return false;
            }
            if (value == null) {
                if (that.value != null) {
                    return false;
                }
            } else if (!value.equals(that.value)) {
                return false;
            }
            return true;
        }
    }
}
