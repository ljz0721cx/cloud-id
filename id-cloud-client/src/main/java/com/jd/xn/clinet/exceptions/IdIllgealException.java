package com.jd.xn.clinet.exceptions;

/**
 * @author lijizhen1@jd.com
 * @date 2018/9/18 18:38
 */
public class IdIllgealException extends RuntimeException {

    public IdIllgealException(String message) {
        super(message);
    }

    public IdIllgealException(Throwable cause) {
        super(cause);
    }
}
