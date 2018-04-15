package com.jd.xn.clinet.toplink;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 10:49
 */
public class TopLinkException extends Exception {
    private static final long serialVersionUID = 2528412120681347894L;
    private int errorCode;

    public int getErrorCode() {
        return this.errorCode;
    }

    public TopLinkException() {
        this("");
    }

    public TopLinkException(String message) {
        super(message);
    }

    public TopLinkException(String message, Exception innerException) {
        super(message, innerException);
    }

    public TopLinkException(String message, Throwable innerException) {
        super(message, innerException);
    }

    public TopLinkException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TopLinkException(int errorCode, String message, Exception innerException) {
        super(message, innerException);
        this.errorCode = errorCode;
    }

    public TopLinkException(int errorCode, String message, Throwable innerException) {
        super(message, innerException);
        this.errorCode = errorCode;
    }
}
