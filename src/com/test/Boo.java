package com.test;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/8 16:45
 */

public class Boo {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        System.out.println(prettyPrintByTransformer(null,3,false));
    }



    /**
     * 格式化xml
     *
     * @param xmlString xml内容
     * @param indent 向前缩进多少空格
     * @param ignoreDeclaration 是否忽略描述
     * @return 格式化后的xml
     */
    public static String prettyPrintByTransformer(String xmlString, int indent, boolean ignoreDeclaration) {

        try {
//            InputSource src = new InputSource(new StringReader(xmlString));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(new File("E:\\E4A_Demo\\v7xmlceshi\\build\\e4a\\constraint-layout-2.0.4\\res\\values\\values.xml")));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }





}
