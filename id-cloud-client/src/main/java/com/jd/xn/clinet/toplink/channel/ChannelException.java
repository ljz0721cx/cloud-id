package com.jd.xn.clinet.toplink.channel;

import com.jd.xn.clinet.toplink.TopLinkException;

import java.io.Serializable;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 10:49
 */
public class ChannelException extends TopLinkException {
    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Exception innerException) {
        super(message, innerException);
    }

    public ChannelException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
