package com.jd.xn.clinet.rpc;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author lijizhen1@jd.com
 * @date 2018/9/19 9:07
 */
public class DefaultRpcHandler extends RpcHandler {
    /**
     * 每次获取step的范围
     * 建议200就足够单台机子使用了。如果是高秘籍的处理服务可以适当调整
     */
    private int step = 200;


    /**
     * 动态代理
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("command")) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        return null;
    }
}
