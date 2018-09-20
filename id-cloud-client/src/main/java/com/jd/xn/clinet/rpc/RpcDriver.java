package com.jd.xn.clinet.rpc;

import java.lang.reflect.Proxy;

/**
 * @author lijizhen1@jd.com
 * @date 2018/9/19 14:15
 */
public class RpcDriver {

    /**
     * 创建一个Connection代理，在commit时休眠100毫秒
     *
     * @return
     */
    public static final RpcConnection createConnection() {
        return (RpcConnection) Proxy.newProxyInstance(RpcDriver.class.getClassLoader(),
                new Class<?>[]{RpcConnection.class}, new DefaultRpcHandler());
    }
}
