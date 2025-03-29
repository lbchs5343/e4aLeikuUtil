package com.wind.action.e4a;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/8 13:34
 */

public enum AnnotationEnume {

    SIMPLE_FUNCTION("SimpleFunction", "8", "方法"),
    SIMPLE_EVENT("SimpleEvent", "7", "事件"),
    SIMPLE_PROPERTY("SimpleProperty", "6", "属性"),
    SIMPLE_OBJECT("SimpleObject", "-1", "对象");
    private String annotationName;
    private String type;
    private String typeName;
    private static Map<String, AnnotationEnume> enmuMap = new HashMap<>();
    static {
        for (AnnotationEnume item : values()) {
            enmuMap.put(item.getAnnotationName(), item);
        }
    }
    AnnotationEnume(String annotationName, String type, String typeName) {
        this.annotationName = annotationName;
        this.type = type;
        this.typeName = typeName;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }
    public static AnnotationEnume getAnnotationEnume(String annotationName) {
       String key;
        if (annotationName.startsWith("@")){
            key=annotationName.substring(1);
        }else if(annotationName.contains(".")){
            key=annotationName.substring(annotationName.lastIndexOf(".")+1);
        }else{
            key=annotationName;
        }
        return enmuMap.get(key);
    }

}
