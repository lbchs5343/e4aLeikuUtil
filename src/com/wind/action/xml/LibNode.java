package com.wind.action.xml;

import java.util.Arrays;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/4 16:23
 */

public class LibNode {
    public String methodName;
    public XmlTypenum xmlTypenum;
    public int inputParameter=0;
    public String[] parameterName;
    public String documentationNotes="";

    @Override
    public String toString() {
        return "LibNode{" +
                "methodName='" + methodName + '\'' +
                ", xmlTypenum=" + xmlTypenum +
                ", inputParameter=" + inputParameter +
                ", parameterName=" + Arrays.toString(parameterName) +
                ", documentationNotes='" + documentationNotes + '\'' +
                '}';
    }
}
