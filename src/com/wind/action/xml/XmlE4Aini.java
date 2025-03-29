
package com.wind.action.xml;

import com.wind.action.e4a.util.Log_e4a;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;
import com.wind.lib.util.DependencyUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

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
public class XmlE4Aini {
    public String e4aGen, 类库版本 = "1.0", 作者 = "", QQ = "", 描述 = "", QQ群 = "", 属性1 = "", 属性2 = "", 属性3 = "";
    public List<String> modelList = new ArrayList<>();
    public List<File> jarList = new ArrayList<>();
    public List<String> mavenList = new ArrayList<>();
    public List<String> errList = new ArrayList<>();
    public List<String> excludeList = new ArrayList<>();
    public boolean introduceV7 = false;
    public boolean SuiyuanUtil = false;
    public boolean strictMode = true;
    public String mavenRepositoryDirectory;
    public StringBuilder permission = new StringBuilder(), manifest = new StringBuilder(), mainActivity = new StringBuilder();

    public XmlE4Aini(File file, Log_e4a log) throws ParserConfigurationException, IOException, SAXException {
        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        Document d = builder.parse(file.getAbsoluteFile());
        NodeList list = d.getElementsByTagName("配置");
        log.i("开始读取配置文件:e4aIni.xml");
        parse(list);
        log.d(toString().replace("\n", "<br/>"), ColorEnume.浅绿, ColorEnume.白色);
        if (e4aGen == null) {
            throw new NullPointerException("未配置SDK路径!");
        }
        log.i("配置解析完毕:e4aIni.xml");
    }

    public void parse(NodeList mainNode) {
        if (AssertionUtil.notEmpty(mainNode) && mainNode.getLength() > 0) {
            if (AssertionUtil.notEmpty(mainNode) && mainNode.getLength() > 0) {
                for (int i = 0; i < mainNode.getLength(); i++) {
                    Node childNode = mainNode.item(i);
                    if ("#text".equals(childNode.getNodeName())) {
                        continue;
                    }
                    if ("配置".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("额外的JAR".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("dependencys".equals(childNode.getNodeName())) {
                        NamedNodeMap attr = childNode.getAttributes();
                        for (int i1 = 0; i1 < attr.getLength(); i1++) {
                            if (attr.item(i1).getNodeName().equals("mavenRepositoryDirectory")) {
                                mavenRepositoryDirectory = attr.item(i1).getTextContent().replace(" ", "").replace("${userName}", DependencyUtil.getUserName());
                                continue;
                            }
                            if (attr.item(i1).getNodeName().equals("strictMode")) {
                                strictMode = attr.item(i1).getTextContent().replace(" ", "").equals("true");
                            }
                        }
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("values_err".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("module".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("编译配置".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("类库信息".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("自定义属性".equals(childNode.getNodeName())) {
                        parse(childNode.getChildNodes());
                        continue;
                    }
                    if ("sdk".equals(childNode.getNodeName())) {
                        e4aGen = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        if (e4aGen.endsWith("/")) {
                            e4aGen = e4aGen.substring(0, e4aGen.length() - 1);
                        }
                        continue;
                    }

                    if ("引入V7注解".equals(childNode.getNodeName())) {
                        String code = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        introduceV7 = "true".equals(code);
                        continue;
                    }
                    if ("引入随缘工具包".equals(childNode.getNodeName())) {
                        String code = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        SuiyuanUtil = "true".equals(code);
                        continue;
                    }
                    if ("类库版本".equals(childNode.getNodeName())) {
                        类库版本 = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        continue;
                    }
                    if ("作者".equals(childNode.getNodeName())) {
                        作者 = childNode.getTextContent().replace("\n", "");
                        continue;
                    }
                    if ("QQ群".equals(childNode.getNodeName())) {
                        QQ群 = childNode.getTextContent().replace("\n", "");
                        continue;
                    }
                    if ("QQ".equals(childNode.getNodeName())) {
                        QQ = childNode.getTextContent().replace("\n", "");
                        continue;
                    }
                    if ("描述".equals(childNode.getNodeName())) {
                        描述 = childNode.getTextContent().trim();
                        continue;
                    }
                    if ("属性1".equals(childNode.getNodeName())) {
                        属性1 = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        continue;
                    }
                    if ("属性2".equals(childNode.getNodeName())) {
                        属性2 = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        continue;
                    }
                    if ("属性3".equals(childNode.getNodeName())) {
                        属性3 = childNode.getTextContent().replace("\n", "").replace(" ", "");
                        continue;
                    }
                    if ("附加权限".equals(childNode.getNodeName())) {
                        permission = parseAffixation(childNode);
                        continue;
                    }
                    if ("Manifest".equals(childNode.getNodeName())) {
                        manifest = parseAffixation(childNode);
                        continue;
                    }
                    if ("mainActivity".equals(childNode.getNodeName())) {
                        mainActivity = parseAffixation(childNode);
                        continue;
                    }
                    if ("moduleName".equals(childNode.getNodeName())) {
                        modelList.add(childNode.getTextContent().replace("\n", ""));
                    }
                    if ("dependency".equals(childNode.getNodeName())) {
                        mavenList.add(childNode.getTextContent().trim().replace("\n", "").replace(":", "-"));
                    }
                    if ("exclude".equals(childNode.getNodeName())) {
                        excludeList.add(childNode.getTextContent().trim().replace("\n", "").replace(":", "-"));
                    }
                    if ("flie".equals(childNode.getNodeName())) {
                        String path = childNode.getTextContent().replace("\n", "")
                                .replace(" ", "")
                                .replace("\\", "/");
                        File file = new File(path);
                        if (file.exists() && file.isFile() && file.getName().endsWith(".jar")) {
                            jarList.add(file);
                        }
                    }
                    if ("err".equals(childNode.getNodeName())) {
                        errList.add(childNode.getTextContent().replace("\n", "").trim());
                    }
                }
            } else {
                throw new NullPointerException("找不到编译配置");
            }
        }
    }

    private StringBuilder parseAffixation(Node childNode) {
        StringBuilder builder = new StringBuilder();
        recursiveParsing(builder, childNode.getChildNodes(), 0);
        return builder;
    }

    private String separator = "    ";

    private void recursiveParsing(StringBuilder builder, NodeList childNode, int increment) {
        String prefix = getSeparator(increment);
        if (childNode != null && childNode.getLength() > 0) {
            for (int i = 0; i < childNode.getLength(); i++) {
                Node node = childNode.item(i);
                if ("#text".equals(node.getNodeName())) {
                    continue;
                }
                builder.append(prefix).append("<" + node.getNodeName());
                NamedNodeMap attributeMap = node.getAttributes();
                if (attributeMap != null) {
                    for (int i1 = 0; i1 < attributeMap.getLength(); i1++) {
                        Node attrNode = attributeMap.item(i1);
                        builder.append(" ").append(attrNode.getNodeName()).append("=\"").append(attrNode.getTextContent()).append("\"");
                    }
                }
                String text = node.getTextContent();
                if (text != null) {
                    text = text.replace("\n", "").replace("\t", "").replace(" ", "");
                    if (!"".equals(text)) {
                        builder.append(">");
                        builder.append(text).append("\n");
                        if (node.getChildNodes() != null && node.getChildNodes().getLength() > 0) {
                            recursiveParsing(builder, node.getChildNodes(), increment + 1);
                        }
                        builder.append(prefix).append("</" + node.getNodeName() + ">\n");
                        continue;
                    }
                }
                if (node.getChildNodes() != null && node.getChildNodes().getLength() > 0) {
                    builder.append(">\n");
                    recursiveParsing(builder, node.getChildNodes(), increment + 1);
                    builder.append(prefix).append("</" + node.getNodeName() + ">\n");
                } else {
                    builder.append("/>\n");
                }

            }
        }
    }

    private String getSeparator(int increment) {
        String str = "";
        for (int i = 0; i < increment; i++) {
            str += separator;

        }
        return str;
    }

    @Override
    public String toString() {
        return "XmlE4Aini{" +
                "\n e4aGen='" + e4aGen + '\'' +
                "\n 类库版本='" + 类库版本 + '\'' +
                "\n 作者='" + 作者 + '\'' +
                "\n QQ='" + QQ + '\'' +
                "\n 描述='" + 描述 + '\'' +
                "\n QQ群='" + QQ群 + '\'' +
                "\n 属性1='" + 属性1 + '\'' +
                "\n 属性2='" + 属性2 + '\'' +
                "\n 属性3='" + 属性3 + '\'' +
                "\n modelList=" + modelList +
                "\n jarList=" + jarList +
                "\n mavenList=" + mavenList +
                "\n errList=" + errList +
                "\n excludeList=" + excludeList +
                "\n introduceV7=" + introduceV7 +
                "\n SuiyuanUtil=" + SuiyuanUtil +
                "\n strictMode=" + strictMode +
                "\n mavenRepositoryDirectory='" + mavenRepositoryDirectory + '\'' +
                '}';
    }
}
