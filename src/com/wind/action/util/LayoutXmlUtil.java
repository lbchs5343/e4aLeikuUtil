package com.wind.action.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/10/15 0:19
 */

public class LayoutXmlUtil {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        Map<String, String> idMap = parse(new File("D://item_tuplb1014.xml"));
        System.out.println(getViewHolder(idMap));
        System.out.println(getBringInCode(idMap, "item_tuplb1014"));

    }

    //    ViewHolder holder = null;
//    Item item = itemList.get(position);
//        if (cv == null) {
//        cv = View.inflate(ctx,R.layout.item_tuplb1014, null);
//        holder = new ViewHolder();
//        holder.gen1=cv.findViewById(R.id.gen_1);
//        holder.gen2=cv.findViewById(R.id.gen_2);
//        holder.angleOfThe=cv.findViewById(R.id.angleOfThe);
//        holder.image = cv.findViewById(R.id.image);
//        holder.series=cv.findViewById(R.id.series);
//        holder.subtitle=cv.findViewById(R.id.subtitle);
//        holder.title=cv.findViewById(R.id.title);
//        cv.setTag(holder);
//    } else {
//        holder = (ViewHolder) cv.getTag();
//    }
    public static String getBringInCode(Map<String, String> idMap, String fileName) {
        if (idMap.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("ViewHolder holder = null;\nif (cv == null) {\nholder = new ViewHolder();\n");
            stringBuilder.append(String.format("cv = View.inflate(ctx,R.layout.%s, null);\n", fileName));
            idMap.forEach((k, v) -> {
                stringBuilder.append(String.format("holder.%s=cv.findViewById(R.id.%s);\n", humpConvert(k), k));
            });
            stringBuilder.append("cv.setTag(holder);\n}else{\nholder = (ViewHolder) cv.getTag();\n}");
            return stringBuilder.toString();
        }
        return "";
    }

    public static String getBringInCode(File file) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> idMap = parse(file);
        String name = file.getName();
        return getBringInCode(idMap, name.substring(0, name.indexOf(".")));
    }

    public static String getVariableNameCode(File file) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> idMap = parse(file);
        if (idMap.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            idMap.forEach((k, v) -> {
                stringBuilder.append(String.format("public %s %s;\n", convertValue(v), humpConvert(k)));
                if (convertValue(v).equals("TextView")) {
                    stringBuilder.append(String.format("public @ColorInt int %sColor;\n", humpConvert(k)));
                    stringBuilder.append(String.format("public float %sSize;\n", humpConvert(k)));
                    stringBuilder.append(String.format("public @ColorInt int %sBackgroundColor;\n", humpConvert(k)));
                }
                stringBuilder.append("\n");
            });
            stringBuilder.append("\n\n\n\n");
            idMap.forEach((k, v) -> {
                stringBuilder.append(String.format("private %s %s;\n", convertValue(v), humpConvert(k)));
                if (convertValue(v).equals("TextView")) {
                    stringBuilder.append(String.format("private @ColorInt int %sColor;\n", humpConvert(k)));
                    stringBuilder.append(String.format("private float %sSize;\n", humpConvert(k)));
                    stringBuilder.append(String.format("private @ColorInt int %sBackgroundColor;\n", humpConvert(k)));
                }
            });

            return stringBuilder.toString();
        }
        return "";
    }

    public static String getVariableNameAssignmentCode(File file) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> idMap = parse(file);
        if (idMap.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder(String.format("View v=View.inflate(getContext(),R.layout.%s, this);\n",file.getName().replace(".xml","").trim()));
            idMap.forEach((k, v) -> stringBuilder.append(String.format("%s=v.findViewById(R.id.%s);\n", humpConvert(k), k)));
            return stringBuilder.toString();
        }
        return "";
    }

    public static String getViewHolder(Map<String, String> idMap) {
        if (idMap.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("class ViewHolder {\n");
            idMap.forEach((k, v) -> stringBuilder.append("    ").append("public ").append(convertValue(v)).append(" ").append(humpConvert(k)).append(";\n"));
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
        return "";
    }

    public static String getViewHolder(File file) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> idMap = parse(file);
        return getViewHolder(idMap);
    }


    public static String convertValue(String value) {
        return value.contains(".") ? value.substring(value.lastIndexOf(".") + 1) : value;
    }

    public static String humpConvert(String key) {

        if (isUpperCase(key) && !key.contains("_")) {
            return key.toLowerCase();
        } else {
            String[] keyArray = key.toLowerCase().split("_");
            if (keyArray.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < keyArray.length; i++) {
                    String ks = keyArray[i];
                    if (!"".equals(ks)) {
                        if (i == 0) {
                            sb.append(ks);
                        } else {
                            int c = ks.charAt(0);
                            if (c >= 97 && c <= 122) {
                                int v = c - 32;
                                sb.append((char) v);
                                if (ks.length() > 1) {
                                    sb.append(ks.substring(1));
                                }
                            } else {
                                sb.append(ks);
                            }
                        }
                    }
                }
                return sb.toString();
            } else {
                return key;


            }
        }


    }

    /**
     * @param str
     * @Description 判断字符串字母是否为大写
     * @Throws
     * @Return boolean
     * @Date 2021-09-17 17:03:50
     * @Author WangKun
     **/
    public static boolean isUpperCase(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 97 && c <= 122) {
                return false;
            }
        }
        return true;
    }


    public static Map<String, String> parse(File file) throws
            ParserConfigurationException, IOException, SAXException {
        System.out.println("+++++++++++++++++++++++++++++++++++2");
        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        Document d = builder.parse(file.getAbsoluteFile());
        NodeList list = d.getChildNodes();
        return parse(list);
    }

    private static Map<String, String> parse(NodeList mainNode) {
        Map<String, String> idMap = new HashMap<>();
        if (AssertionUtil.notEmpty(mainNode) && mainNode.getLength() > 0) {
            if (AssertionUtil.notEmpty(mainNode) && mainNode.getLength() > 0) {
                for (int i = 0; i < mainNode.getLength(); i++) {
                    Node childNode = mainNode.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE && !childNode.getNodeName().startsWith("#")) {
                        Node node = childNode.getAttributes().getNamedItem("android:id");
                        if (node != null) {
                            String idStr = node.getTextContent();
                            String id = idStr.substring(idStr.indexOf("/") + 1).trim();
                            idMap.put(id, childNode.getNodeName().trim());
                        }
                        idMap.putAll(parse(childNode.getChildNodes()));
                    }
                }
            }
        }
        return idMap;
    }
}
