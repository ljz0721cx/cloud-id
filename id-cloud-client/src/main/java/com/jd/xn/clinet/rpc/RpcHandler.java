package com.jd.xn.clinet.rpc;

import java.lang.reflect.InvocationHandler;

/**
 * @author lijizhen1@jd.com
 * @date 2018/8/4 11:19
 */
public abstract class RpcHandler implements InvocationHandler {
    protected String token;


    /**
     * 获得调用者,比如是http或者是jsf
     * 这里可以统一实现在抽象类层
     *
     * @return
     */
    Object getRpcInvoker() {
        return null;
    }


}
