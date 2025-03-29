package com.wind.action.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/4 4:54
 */

public enum XmlTypenum {
    ClASS_ATTRIBUTES("6", "属性"),
    ClASS_FASHION("8", "方法"),
    ClASS_INCIDENT("7", "事件"),
    ClASS_INTERFACE_NAME("4", "类库接口名称"),
    ClASS_LEGEND("0", "类库目录"),
    ClASS_GEN(" ", "根");
    private String code;
    private String name;
    private static Map<String, XmlTypenum> enmuMap = new HashMap<>();

    static {
        for (XmlTypenum item : values()) {
            enmuMap.put(item.getCode(), item);
        }
    }

    XmlTypenum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getName(String code) {
        return enmuMap.get(code).getName();
    }
    public static XmlTypenum getCodeItem(String code) {
        return enmuMap.get(code);
    }


}
