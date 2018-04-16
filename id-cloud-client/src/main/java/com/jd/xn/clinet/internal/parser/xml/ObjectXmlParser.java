package com.jd.xn.clinet.internal.parser.xml;

import com.jd.xn.clinet.ApiException;
import com.jd.xn.clinet.JdParser;
import com.jd.xn.clinet.TFResponse;
import com.jd.xn.clinet.internal.mapping.Converter;


/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 18:04
 */
public class ObjectXmlParser<T extends TFResponse>
        implements JdParser<T> {
    private Class<T> clazz;

    public ObjectXmlParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(String rsp) throws ApiException {
        Converter converter = new XmlConverter();
        return converter.toResponse(rsp, this.clazz);
    }

    @Override
    public Class<T> getResponseClass() {
        return this.clazz;
    }

}
