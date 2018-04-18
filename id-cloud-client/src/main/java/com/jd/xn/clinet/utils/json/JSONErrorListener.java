package com.jd.xn.clinet.utils.json;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 9:05
 */
public interface JSONErrorListener {
    void start(String text);

    void error(String message, int column);

    void end();
}

