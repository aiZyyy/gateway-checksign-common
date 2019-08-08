package com.sixi.gateway.checksigncommon.oauth.method.impl;

import com.sixi.gateway.checksigncommon.oauth.Auth;
import com.sixi.gateway.checksigncommon.oauth.exception.AuthProblemException;
import com.sixi.gateway.checksigncommon.oauth.method.AuthNonces;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @Author: ZY
 * @Date: 2019/8/7 10:22
 * @Version 1.0
 * @Description: 简单OAuthNonces实现,UsedNonce记录在内存中，无法解决分部署问题
 */
public class SimpleAuthNonces implements AuthNonces {
    /** 默认最大间隔时间**/
    public static final long DEFAULT_MAX_TIMESTAMP_AGE = 300000L;

    protected final long maxTimestampAgeMsec;

    private final Set<UsedNonce> usedNonces = new TreeSet<UsedNonce>();

    public  SimpleAuthNonces() {
        this(DEFAULT_MAX_TIMESTAMP_AGE);
    }

    public SimpleAuthNonces(long maxTimestampAgeMsec) {
        this.maxTimestampAgeMsec = maxTimestampAgeMsec;
        System.out.println("create simple auth nonce");
    }

    @Override
    public void validateNonce(long timestamp, String appId,String nonce)
            throws AuthProblemException {

        UsedNonce usedNonce = new UsedNonce(timestamp,appId,nonce);
        boolean isValid = false;
        synchronized (this.usedNonces) {
            isValid = this.usedNonces.add(usedNonce);
        }
        if (!isValid) {
            throw new AuthProblemException(Auth.Problems.NONCE_USED);
        }
        removeOldNonces(timestamp);
    }


    private void removeOldNonces(long currentTimeMsec) {
        UsedNonce min = new UsedNonce((currentTimeMsec
                - this.maxTimestampAgeMsec + 500L) / 1000L, new String[0]);
        Iterator<UsedNonce> iter =  null;
        synchronized (usedNonces) {
            for (iter = usedNonces.iterator(); iter.hasNext();) {
                UsedNonce used = (UsedNonce) iter.next();
                if (min.compareTo(used) <= 0) {
                    break;
                }
                iter.remove();
            }
        }
    }



    public static class UsedNonce implements Serializable, Comparable<UsedNonce> {

        /**
         * serialVersionUID:TODO(用一句话描述这个变量表示什么).
         *
         * @since JDK 1.6
         */
        private static final long serialVersionUID = 1L;
        String sortKey;

        ThreadLocal<StringBuilder> buf = new ThreadLocal<StringBuilder>();

        public UsedNonce(long timestamp, String... nonceEtc) {
            StringBuilder builder = buf.get();
            if (builder == null) {
                builder = new StringBuilder();
                buf.set(builder);
            }
            try {
                builder.append(String.format("%20d", Long.valueOf(timestamp)));
                for (String etc : nonceEtc) {
                    builder.append("&").append(etc == null ? " " : Auth.percentEncode(etc));
                    // A null value is different from "" or any other String.
                }
                sortKey = builder.toString();
            } finally {
                buf.set(builder);
                builder.setLength(0);
            }

        }

        @Override
        public int compareTo(UsedNonce that) {
            return that == null ? 1 : this.sortKey.compareTo(that.sortKey);
        }

        @Override
        public int hashCode() {
            return this.sortKey.hashCode();
        }

        @Override
        public String toString() {
            return sortKey;
        }

        @Override
        public boolean equals(Object that) {
            if (that == null) {
                return false;
            }
            if (that == this) {
                return true;
            }
            if (that.getClass() != getClass()) {
                return false;
            }
            return this.sortKey.equals(((UsedNonce) that).sortKey);
        }
    }

}
