package com.wind.action.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期： 2021/6/1
 *
 * @author 随缘_QQ:874334395
 * @version 1.0
 * @since JDK 1.8.0_79
 * <p>
 * 类说明：   $
 */
public class XmlBl {
    private File file;

    private List<LibNode> xmlNodes = new ArrayList<>();

    public XmlBl(File file) {
        this.file = file;
        try {
            //1.创建DocumentBuilderFactory对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            Document d = builder.parse(file.getAbsoluteFile());
            NodeList list = d.getElementsByTagName("类库");
            xmlNodes.addAll(element(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LibNode> element(NodeList list) throws UnsupportedEncodingException {
        List<LibNode> libNodes = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String code = node.getTextContent().replace("\\r\\n", "").replace(" ", "");
                code = new String(new byte[]{code.getBytes()[0]});
                if (code.getBytes()[0] != 10) {
                    if (Integer.valueOf(code) > 4) {
                        String directions = node.getAttributes().item(0).toString();
                        String documentationNotes = directions.substring(directions.indexOf("★注释：") + "★注释：".length(),
                                        directions.lastIndexOf("\""))
                                .replace("★", "\n").replace("■", " ")
                                .replace("*", "")
                                .replace("<p>", "").replace("参数:","@param");
                        String[] parameterName = null;
                        if (Integer.valueOf(code) != 6) {
                            String parameter = directions.substring(directions.indexOf("(") + 1, directions.indexOf(")"));
                            if (!"".equals(parameter.replace(" ", ""))) {
                                if (parameter.contains(",")) {
                                    String[] str = parameter.split(",");
                                    parameterName = new String[str.length];
                                    for (int x = 0; x < str.length; x++) {
                                        String parameterCode=str[x].substring(0, str[x].indexOf(" 为")).trim();
                                        if (parameterCode.startsWith("传址 ")) {
                                            parameterCode=parameterCode.replace("传址 ","");
                                        }
                                        parameterName[x] = parameterCode;
                                    }
                                } else {
                                    String parameterCode=parameter.substring(0, parameter.indexOf(" 为")).trim();
                                    if (parameterCode.startsWith("传址 ")) {
                                        parameterCode=parameterCode.replace("传址 ","");
                                    }
                                    parameterName = new String[]{parameterCode};
                                }
                            }
                        }
                        LibNode libNode = new LibNode();
                        libNode.methodName = node.getNodeName();
                        libNode.documentationNotes = documentationNotes;
                        libNode.parameterName = parameterName;
                        libNode.inputParameter = parameterName == null ? 0 : parameterName.length;
                        libNode.xmlTypenum = XmlTypenum.getCodeItem(code);
                        libNodes.add(libNode);
                    } else {
                        LibNode libNode = new LibNode();
                        libNode.methodName = node.getNodeName();
                        libNode.xmlTypenum = XmlTypenum.getCodeItem(code);
                        libNodes.add(libNode);
                    }
                }
                libNodes.addAll(element(node.getChildNodes()));
            }
        }
        return libNodes;
    }

    public List<LibNode> getXmlNodes() {
        return xmlNodes;
    }
}
