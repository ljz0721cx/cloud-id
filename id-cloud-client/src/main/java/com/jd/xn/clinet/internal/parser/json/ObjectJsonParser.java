package com.jd.xn.clinet.internal.parser.json;

import com.jd.xn.clinet.ApiException;
import com.jd.xn.clinet.internal.mapping.Converter;

/**
 * JSON格式转换器。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/17 9:33
 */
public class ObjectJsonParser implements Converter {

    @Override
    public <T> T toResponse(String paramString, Class<T> paramClass) throws ApiException {
        //JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
        return null;
    }
}
