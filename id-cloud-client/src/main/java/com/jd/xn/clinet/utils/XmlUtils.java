package com.jd.xn.clinet.utils;


import com.jd.xn.clinet.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.Properties;

/**
 * xml解析工具类
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/16 18:10
 */
public final class XmlUtils {
    private static final Logger log = LoggerFactory.getLogger(XmlUtils.class);
    private static final String XMLNS_XSI = "xmlns:xsi";
    private static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";
    private static final String LOGIC_YES = "yes";
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 创建document单实例
     *
     * @return
     * @throws ApiException
     */
    public static Document newDocument()
            throws ApiException {
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new ApiException(e);
        }
        return doc;
    }

    /**
     * 获得某个文件的document
     *
     * @param file
     * @return
     * @throws ApiException
     */
    public static Document getDocument(File file)
            throws ApiException {
        InputStream in = getInputStream(file);
        return getDocument(new InputSource(in), null);
    }


    /**
     * 通过Stirng加载根路径元素
     *
     * @param payload
     * @return
     * @throws ApiException
     */
    public static Element getRootElementFromString(String payload)
            throws ApiException {
        //判断是否为空
        if ((payload == null) || (payload.length() < 1)) {
            throw new ApiException("XML_PAYLOAD_EMPTY");
        }
        StringReader sr = new StringReader(escapeXml(payload));
        InputSource source = new InputSource(sr);
        return getDocument(source, null).getDocumentElement();
    }


    /**
     * 通过输入的xml和xsd声明获得xml的document
     *
     * @param xml
     * @param xsd
     * @return
     */
    private static Document getDocument(InputSource xml, InputStream xsd)
            throws ApiException {
        Document doc = null;
        try {
            //重新创建实例
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            if (xsd != null) {
                //需要xml文档签名和验签
                dbf.setNamespaceAware(true);
            }
            DocumentBuilder builder = dbf.newDocumentBuilder();
            //解析xml
            doc = builder.parse(xml);
            if (xsd != null) {
                validateXml(doc, xsd);
            }
        } catch (ParserConfigurationException e) {
            throw new ApiException(e);
        } catch (SAXException e) {
            throw new ApiException("XML_PARSE_ERROR", e);
        } catch (IOException e) {
            throw new ApiException("XML_READ_ERROR", e);
        } finally {
            closeStream(xml.getByteStream());
        }
        return doc;
    }

    private static void closeStream(InputStream byteStream) {

    }

    /**
     * 校验xml报文
     *
     * @param root
     * @param xsd
     * @throws ApiException
     */
    public static void validateXml(Node root,
                                   InputStream xsd)
            throws ApiException {
        try {
            Source source = new StreamSource(xsd);
            Schema schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
                    .newSchema(source);
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(root));
        } catch (SAXException e) {
            if (log.isErrorEnabled()) {
                log.error("验证XML文件出错：\n" + nodeToString(root));
            }
            throw new ApiException("XML_VALIDATE_ERROR", e);
        } catch (Exception e) {
            throw new ApiException("XML_READ_ERROR", e);
        } finally {
            closeStream(xsd);
        }
    }

    public static String nodeToString(Node node)
            throws ApiException {
        String payload = null;
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            Properties props = tf.getOutputProperties();
            props.setProperty("encoding", "UTF-8");
            props.setProperty("indent", "yes");
            tf.setOutputProperties(props);

            StringWriter writer = new StringWriter();
            tf.transform(new DOMSource(node), new StreamResult(writer));
            //
            payload = escapeXml(writer.toString());
        } catch (TransformerException e) {
            throw new ApiException("XML_TRANSFORM_ERROR", e);
        }
        return payload;
    }

    /**
     * 函数忽略XML标记
     *
     * @param payload
     * @return
     */
    public static String escapeXml(String payload) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < payload.length(); i++) {
            char c = payload.charAt(i);
            if ((c == '\t') || (c == '\n') || (c == '\r') || ((c >= ' ')
                    && (c <= 55295)) || ((c >= 57344) && (c <= 65533)) || ((c >= 65536) && (c <= 1114111))) {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * 获得文件的输入流
     *
     * @param file
     * @return
     * @throws ApiException
     */
    private static InputStream getInputStream(File file) throws ApiException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ApiException("XML_FILE_NOT_FOUND", e);
        }
        return in;
    }


}
