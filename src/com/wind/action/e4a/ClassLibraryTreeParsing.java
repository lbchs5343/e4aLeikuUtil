package com.wind.action.e4a;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;
import com.wind.action.util.FileUtil;
import com.wind.action.util.PsiFileUtil;
import com.wind.action.xml.XmlE4Aini;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/8 12:50
 */

public class ClassLibraryTreeParsing {

    PsiJavaFile psiJavaFile;
    private Document document;
    private Element xmlMain;
    private String moduleChineseName;
    private String createTime;
    public int visible = -1;
    public static final Map<String, String> typeMap = new HashMap<>();

    static {
        typeMap.put("String", "为 文本型");
        typeMap.put("String[]", "为 文本型[]");
        typeMap.put("int", "为 整数型");
        typeMap.put("int[]", "为 整数型[]");
        typeMap.put("long", "为 长整数型");
        typeMap.put("long[]", "为 长整数型[]");
        typeMap.put("boolean[]", "为 逻辑型[]");
        typeMap.put("boolean", "为 逻辑型");
        typeMap.put("byte", "为 字节型");
        typeMap.put("byte[]", "为 字节型[]");
        typeMap.put("double", "为 双精度小数型");
        typeMap.put("double[]", "为 双精度小数型[]");
        typeMap.put("float", "为 单精度小数型");
        typeMap.put("float[]", "为 单精度小数型[]");
        typeMap.put("short", "为 短整数型");
        typeMap.put("short[]", "为 短整数型[]");
        typeMap.put("Date", "为 日期时间型");
        typeMap.put("IntegerReferenceParameter", "为 整数型");
        typeMap.put("BooleanReferenceParameter", "为 逻辑型");
        typeMap.put("ByteReferenceParameter", "为 字节型");
        typeMap.put("DoubleReferenceParameter", "为 双精度小数型");
        typeMap.put("LongReferenceParameter", "为 长整数型");
        typeMap.put("ObjectReferenceParameter", "为 对象");
        typeMap.put("StringReferenceParameter", "为 文本型");
        typeMap.put("IntegerReferenceParameter[]", "为 整数型[]");
        typeMap.put("BooleanReferenceParameter[]", "为 逻辑型[]");
        typeMap.put("ByteReferenceParameter[]", "为 字节型[]");
        typeMap.put("DoubleReferenceParameter[]", "为 双精度小数型[]");
        typeMap.put("LongReferenceParameter[]", "为 长整数型[]");
        typeMap.put("ObjectReferenceParameter[]", "为 对象[]");
        typeMap.put("StringReferenceParameter[]", "为 文本型[]");
        typeMap.put("集合", "为 集合");
        typeMap.put("哈希表", "为 哈希表");
        typeMap.put("void", "");
        typeMap.put("Object", "为 对象");
        typeMap.put("Object[]", "为 对象[]");
        typeMap.put("Variant[]", "为 通用型[]");
        typeMap.put("Variant", "为 通用型");
    }

    private String directions;
    private E4AConfigure e4AConfigure;

    public ClassLibraryTreeParsing(E4AConfigure e4AConfigure) throws Exception {
        this.e4AConfigure = e4AConfigure;
        //开始解析类库树
        psiJavaFile = (PsiJavaFile) PsiFileUtil.getFilesByName(e4AConfigure.project, e4AConfigure.interfaceFile);
        this.moduleChineseName = e4AConfigure.moduleChineseName;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //格式化然后放入字符串中
        createTime = dateFormat.format(System.currentTimeMillis());
        directions = "%s：%s★分类：" + moduleChineseName + " ★版本号:" + e4AConfigure.getXmlE4Aini().类库版本 + "★" + "★编译时间：" +
                createTime +
                "★★作者：" + e4AConfigure.getXmlE4Aini().作者.replace("\n", "") +
                "★QQ：" + e4AConfigure.getXmlE4Aini().QQ.replace("\n", "") +
                "★QQ群：" + e4AConfigure.getXmlE4Aini().QQ群.replace("\n", "")+
                "★" + e4AConfigure.getXmlE4Aini().描述.replace("\n", "★")
                + "★文档注释：%s";
        init();
        parse();
    }

    private void init() {
        try {
            // 创建解析器工厂
            document = DocumentHelper.createDocument();
            document.setXMLEncoding("gb18030");
            //创建节点文档树
            Element bookstore = document.addElement("类库");
            bookstore.addAttribute("tag", "类库的根文档树;");
            bookstore.addAttribute("source", "此xml由随缘E4A类库工具制作");
            bookstore.addAttribute("类库定制", "接VCN_E4A类库定制!联系QQ:874334395");
            Element 主表 = bookstore.addElement(moduleChineseName);//xxxxx类库
            主表.addAttribute("说明", "此库由Android Studio(随缘E4A类库开发插件,插件作者:随缘)封装编译");
            主表.addAttribute("版本", "10");
            主表.addText("0");
            xmlMain = 主表.addElement(moduleChineseName.replace("类库", ""));
            xmlMain.addText("4");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成类库树.xml失败");
        }
    }

    private void parse() throws Exception {
        if (AssertionUtil.notEmpty(psiJavaFile.getClasses())) {
            PsiClass psiClasses = psiJavaFile.getClasses()[0];
            PsiReferenceList psiReferenceList = psiClasses.getExtendsList();
            if (AssertionUtil.isEmpty(psiReferenceList)) {
                throw new Exception("找不到类库接口");
            }
            PsiClassType[] psiClassType = psiReferenceList.getReferencedTypes();
            for (int i = 0; i < psiClassType.length; i++) {
                PsiClassType classType = psiClassType[i];
                if ("VisibleComponent".equals(classType.getName())) {
                    xmlMain.addAttribute("说明", "可视组件");
                    visible = 1;
                } else if ("Component".equals(classType.getName())) {
                    xmlMain.addAttribute("说明", "不可视组件");
                    visible = 0;
                }
            }
            if (visible < 0) {
                throw new Exception("找不到类库接口");
            }
            Set<MethodNode> meansSet = new TreeSet<>(new MyComparator());
            Set<MethodNode> incidentSet = new TreeSet<>(new MyComparator());
            Set<MethodNode> attrSet = new TreeSet<>(new MyComparator());
            for (PsiMethod method : psiClasses.getMethods()) {
                AnnotationEnume annotationEnume = null;
                for (PsiAnnotation annotation : method.getAnnotations()) {
                    annotationEnume = AnnotationEnume.getAnnotationEnume(annotation.getQualifiedName());
                    if (AssertionUtil.notEmpty(annotationEnume)) {
                        break;
                    }
                }
                if (AssertionUtil.notEmpty(annotationEnume)) {
                    String returnType = typeMap.get(method.getReturnType().getPresentableText());
                    String docText = "";
                    if (AssertionUtil.notEmpty(returnType)) {
                        PsiDocComment docComment = method.getDocComment();
                        if (AssertionUtil.notEmpty(docComment)) {
                            String text = docComment.getText();
                            text = text.substring(1, text.length() - 1);
                            text = text.replace("*", "");
                            text = text.replace("\n", "★");
                            text = text.replace("@param", "参数:");
                            text = text.replace("@return", "返回:");
                            text = text.replace("<p>", "返回:");
                            docText = text;
                        }
                    } else {
                        throw new ClassCastException("在解析类库接口时发现不支持的数据类型" + method.getReturnType()+" 方法名:"+method.getName());
                    }
                    //获取参数列表
                    PsiParameter[] parametersArr = method.getParameterList().getParameters();
                    StringBuilder parameter = new StringBuilder();
                    if (AssertionUtil.notEmpty(parametersArr)) {
                        parameter = new StringBuilder();
                        for (int i = 0; i < parametersArr.length; i++) {
                            PsiParameter psiParameter = parametersArr[i];
                            String type = typeMap.get(psiParameter.getType().getPresentableText().trim().replace(" ",""));
                            if (AssertionUtil.isEmpty(type)) {
                                throw new ClassCastException("____在解析类库接口时发现不支持的数据类型" + method.getReturnType()+" 方法名:"+method.getName());
                            }
                            if (psiParameter.getType().getPresentableText().contains("ReferenceParameter")) {
                                parameter.append("传址 ").append(psiParameter.getName()).append(" ").append(type).append(",");
                            } else {
                                parameter.append(psiParameter.getName()).append(" ").append(type).append(",");
                            }
                        }
                        parameter = new StringBuilder("(" + parameter.substring(0, parameter.length() - 1) + ") " + returnType);


                    } else if (annotationEnume != AnnotationEnume.SIMPLE_PROPERTY) {
                        parameter.append("()").append(returnType);
                    }else {
                        parameter.append(" ").append(returnType);
                    }
                    String nodeValue = String.format(directions, annotationEnume.getTypeName(),
                            method.getName() + parameter, docText);
                    MethodNode methodNode = new MethodNode(method.getName(), nodeValue, annotationEnume.getType());
                    if (annotationEnume == AnnotationEnume.SIMPLE_FUNCTION) {
                        meansSet.add(methodNode);
                    } else if (annotationEnume == AnnotationEnume.SIMPLE_EVENT) {
                        incidentSet.add(methodNode);
                    } else {
                        attrSet.add(methodNode);
                    }
                }
            }
            if (attrSet.size() > 0) {
                attrSet.forEach((methodNode) -> addNode(methodNode));
            }
            if (meansSet.size() > 0) {
                meansSet.forEach((methodNode) -> addNode(methodNode));
            }
            if (incidentSet.size() > 0) {
                incidentSet.forEach((methodNode) -> addNode(methodNode));
            }
            if (visible == 1) {
                addNode("顶边", AnnotationEnume.SIMPLE_PROPERTY, "为 整数型", "");
                addNode("左边", AnnotationEnume.SIMPLE_PROPERTY, "为 整数型", "");
                addNode("高度", AnnotationEnume.SIMPLE_PROPERTY, "为 整数型", "");
                addNode("宽度", AnnotationEnume.SIMPLE_PROPERTY, "为 整数型", "");
                addNode("可视", AnnotationEnume.SIMPLE_PROPERTY, "为 逻辑型", "");
                addNode("可用", AnnotationEnume.SIMPLE_PROPERTY, "为 逻辑型", "");
                addNode("背景颜色", AnnotationEnume.SIMPLE_PROPERTY, "为 整数型", "");
                addNode("可停留焦点", AnnotationEnume.SIMPLE_PROPERTY, "为 逻辑型", "");

                addNode("移动", AnnotationEnume.SIMPLE_FUNCTION, "", "(左边 为 整数型,顶边 为 整数型,宽度 为 整数型,高度 为 整数型)");
                addNode("销毁", AnnotationEnume.SIMPLE_FUNCTION, "", "()");
                addNode("刷新", AnnotationEnume.SIMPLE_FUNCTION, "", "()");
                addNode("取宽度", AnnotationEnume.SIMPLE_FUNCTION, "为 整数型", "()");
                addNode("取高度", AnnotationEnume.SIMPLE_FUNCTION, "为 整数型", "()");
                addNode("到顶层", AnnotationEnume.SIMPLE_FUNCTION, "", "()");
                addNode("获取焦点", AnnotationEnume.SIMPLE_FUNCTION, "", "()");
                addNode("清除焦点", AnnotationEnume.SIMPLE_FUNCTION, "", "()");

                addNode("开启特效", AnnotationEnume.SIMPLE_FUNCTION, "", "(特效类型 为 整数型,特效时间 为 整数型,特效停留 为 逻辑型)");
                addNode("旋转特效", AnnotationEnume.SIMPLE_FUNCTION, "", "(起始角度 为 单精度小数型,终止角度 为 单精度小数型,特效时间 为 整数型,特效停留 为 逻辑型)");
                addNode("移动特效", AnnotationEnume.SIMPLE_FUNCTION, "", "(起点横坐标 为 整数型,终点横坐标 为 整数型,起点纵坐标 为 整数型,终点纵坐标 为 整数型,特效时间 为 整数型,特效停留 为 逻辑型)");
                addNode("监听绘制", AnnotationEnume.SIMPLE_FUNCTION, "", "()");

                addNode("获得焦点", AnnotationEnume.SIMPLE_EVENT, "", "()");
                addNode("失去焦点", AnnotationEnume.SIMPLE_EVENT, "", "()");
                addNode("绘制完毕", AnnotationEnume.SIMPLE_EVENT, "", "(宽度 为 整数型,高度 为 整数型)");
                addNode("创建完毕", AnnotationEnume.SIMPLE_EVENT, "", "()");
                addNode("移动特效完毕", AnnotationEnume.SIMPLE_EVENT, "", "()");
                addNode("开启特效完毕", AnnotationEnume.SIMPLE_EVENT, "", "()");
            }
        }
    }


    /**
     * 生成xml方法
     */
    public void writeOut() {
        XMLWriter xmlWriter = null;
        try {
            OutputFormat xmlFormat = new OutputFormat();
            xmlFormat.setEncoding("gb18030");
            xmlFormat.setNewlines(true);
            // 生成缩进
            xmlFormat.setIndent(true);
            //使用4个空格进行缩进, 可以兼容文本编辑器
            xmlFormat.setIndent("\t");
            xmlWriter = new XMLWriter(xmlFormat);
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            FileUtil.writeOut(e4AConfigure.elbDirectory + "/" + e4AConfigure.moduleChineseName + "/类库树.xml",
                    FileUtil.adaptiveCoding(xmlWriter.toString()), "GBK");
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addNode(MethodNode methodNode) {
        Element functionNode = xmlMain.addElement(methodNode.methodName);
        functionNode.addText(methodNode.type);
        functionNode.addAttribute("说明", methodNode.directions);
    }

    private void addNode(String methodName, AnnotationEnume annotationEnume, String returnType, String directions) {
        Element functionNode = xmlMain.addElement(methodName);
        functionNode.addText(annotationEnume.getType());
        functionNode.addAttribute("说明", annotationEnume.getTypeName()+"：" + methodName+ directions+" " + returnType);
    }

    class MethodNode {
        private String methodName;
        private String directions;
        private String type;

        public MethodNode(String methodName, String directions, String type) {
            this.methodName = methodName;
            this.directions = directions;
            this.type = type;
        }

        @Override
        public String toString() {
            return "MethodNode{" +
                    "methodName='" + methodName + '\'' +
                    ", directions='" + directions + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    class MyComparator implements Comparator<MethodNode> {
        @Override
        public int compare(MethodNode o1, MethodNode o2) {
            return o2.methodName.compareTo(o1.methodName);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }


}
