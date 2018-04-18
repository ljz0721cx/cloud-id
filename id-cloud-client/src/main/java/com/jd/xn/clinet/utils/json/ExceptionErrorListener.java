package com.jd.xn.clinet.utils.json;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 9:09
 */
public class ExceptionErrorListener extends BufferErrorListener {
    @Override
    public void error(String type, int col) {
        super.error(type, col);
        throw new IllegalArgumentException(this.buffer.toString());
    }
}
