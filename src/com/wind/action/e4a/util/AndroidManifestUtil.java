package com.wind.action.e4a.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/7 5:25
 */

public class AndroidManifestUtil {


    public static String parsePackageName(File androidManifest) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        NodeList list = builder.parse(androidManifest).getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("manifest".equals(node.getNodeName())) {
                    Node node1 = node.getAttributes().getNamedItem("package");
                    return node1.getTextContent().replace(" ","").trim();
                }
            }
        }
        return null;
    }

}
