package com.jd.xn.clinet.internal.parser.json;

import com.jd.xn.clinet.ApiException;
import com.jd.xn.clinet.JdParser;
import com.jd.xn.clinet.TFResponse;
import com.jd.xn.clinet.internal.mapping.Converter;

/**
 *  单个JSON对象解释器。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/17 9:33
 */
public class ObjectJsonParser<T extends TFResponse>
        implements JdParser<T> {
    private Class<T> clazz;
    private boolean simplify;

    public ObjectJsonParser(Class<T> clazz, boolean simplify) {
        this.clazz = clazz;
        this.simplify = simplify;
    }

    @Override
    public T parse(String rsp)
            throws ApiException
    {
        Converter converter;
        if (this.simplify) {
            converter = new SimplifyJsonConverter();
        } else {
            converter = new JsonConverter();
        }
        return (T)converter.toResponse(rsp, this.clazz);
    }

    @Override
    public Class<T> getResponseClass() {
        return this.clazz;
    }
}
