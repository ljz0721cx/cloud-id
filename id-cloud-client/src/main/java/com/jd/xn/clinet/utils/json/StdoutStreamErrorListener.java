package com.jd.xn.clinet.utils.json;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 9:44
 */
public class StdoutStreamErrorListener extends BufferErrorListener
{
    @Override
    public void end()
    {
        System.out.print(this.buffer.toString());
    }
}