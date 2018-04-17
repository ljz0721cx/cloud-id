package com.jd.xn.clinet.internal.parser.xml;

import com.jd.xn.clinet.ApiException;
import com.jd.xn.clinet.internal.mapping.Converter;
import com.jd.xn.clinet.internal.mapping.Converters;
import com.jd.xn.clinet.internal.mapping.Reader;
import com.jd.xn.clinet.utils.StringUtils;
import com.jd.xn.clinet.utils.XmlUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/16 18:06
 */
public class XmlConverter implements Converter {

    /**
     * 转换数据为响应对象
     *
     * @param rsp
     * @param clazz
     * @param <T>
     * @return
     * @throws ApiException
     */
    @Override
    public <T> T toResponse(String rsp, Class<T> clazz) throws ApiException {
        //获得根节点的元素
        Element root = XmlUtils.getRootElementFromString(rsp);
        return getModelFromXML(root, clazz);
    }


    /**
     * 获得
     *
     * @param element
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T getModelFromXML(final Element element,
                                  Class<T> clazz) throws ApiException {
        //如果元素为空返回空
        if (element == null) {
            return null;
        }
        return Converters.convert(clazz, new Reader() {
            //判断是否有该属性
            @Override
            public boolean hasReturnField(Object name) {
                Element childE = XmlUtils.getChildElement(element, (String) name);
                return childE != null;
            }

            /**
             * 读取单个基本对象。
             * @param name
             * @return
             */
            @Override
            public Object getPrimitiveObject(Object name) {
                return XmlUtils.getChildElementValue(element, (String) name);
            }

            @Override
            public Object getObject(Object name, Class<?> type) throws ApiException {
                Element childE = XmlUtils.getChildElement(element, (String) name);
                if (childE != null) {
                    return XmlConverter.this.getModelFromXML(childE, type);
                }
                return null;
            }

            @Override
            public List<?> getListObjects(Object listName, Object itemName, Class<?> subType) throws ApiException {
                List list = null;
                Element listE = XmlUtils.getChildElement(element, (String) listName);
                if (listE != null) {
                    list = new ArrayList();
                    List<Element> itemEs = XmlUtils.getChildElements(listE, (String) itemName);
                    for (Element itemE : itemEs) {
                        Object obj = null;
                        boolean isObject = false;
                        String value = XmlUtils.getElementValue(itemE);

                        if (String.class.isAssignableFrom(subType)) {
                            obj = value;
                        } else if (Long.class.isAssignableFrom(subType)) {
                            obj = Long.valueOf(value);
                        } else if (Integer.class.isAssignableFrom(subType)) {
                            obj = Integer.valueOf(value);
                        } else if (Boolean.class.isAssignableFrom(subType)) {
                            obj = Boolean.valueOf(value);
                        } else if (Date.class.isAssignableFrom(subType)) {
                            obj = StringUtils.parseDateTime(value);
                        } else {
                            isObject = true;
                            obj = XmlConverter.this.getModelFromXML(itemE, subType);
                        }
                        if (isObject) {
                            if (obj != null) {
                                list.add(obj);
                            }
                        } else {
                            list.add(obj);
                        }
                    }
                }
                return list;
            }
        });

    }
}
