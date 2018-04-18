package com.jd.xn.clinet.utils.json;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/18 8:51
 */
public class JSONValidatingReader extends JSONReader {
    public static final Object INVALID = new Object();
    private JSONValidator validator;

    public JSONValidatingReader(JSONValidator validator) {
        this.validator = validator;
    }

    public JSONValidatingReader(JSONErrorListener listener) {
        this(new JSONValidator(listener));
    }

    public JSONValidatingReader() {
        this(new StdoutStreamErrorListener());
    }

    @Override
    public Object read(String string) {
        if (!this.validator.validate(string)) {
            return INVALID;
        }
        return super.read(string);
    }
}