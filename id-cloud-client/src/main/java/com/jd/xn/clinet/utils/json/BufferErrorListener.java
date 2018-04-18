package com.jd.xn.clinet.utils.json;

/**
 * 批量错误信息监听
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/18 9:23
 */
public class BufferErrorListener implements JSONErrorListener {
    protected StringBuffer buffer;
    private String input;

    public BufferErrorListener(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public BufferErrorListener() {
        this(new StringBuffer());
    }

    @Override
    public void start(String input) {
        this.input = input;
        this.buffer.setLength(0);
    }

    @Override
    public void error(String type, int col) {
        this.buffer.append("expected ");
        this.buffer.append(type);
        this.buffer.append(" at column ");
        this.buffer.append(col);
        this.buffer.append("\n");
        this.buffer.append(this.input);
        this.buffer.append("\n");
        indent(col - 1, this.buffer);
        this.buffer.append("^");
    }

    private void indent(int n, StringBuffer ret) {
        for (int i = 0; i < n; i++) {
            ret.append(' ');
        }
    }

    @Override
    public void end() {
    }
}