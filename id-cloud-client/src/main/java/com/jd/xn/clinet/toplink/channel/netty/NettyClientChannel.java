package com.jd.xn.clinet.toplink.channel.netty;

import com.jd.xn.clinet.toplink.ClientChannel;
import org.jboss.netty.channel.Channel;


/**
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:44
 */

public abstract interface NettyClientChannel extends ClientChannel
{
    public abstract void setChannel(Channel paramChannel);
}