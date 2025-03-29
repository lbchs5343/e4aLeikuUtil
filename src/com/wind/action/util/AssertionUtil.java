package com.wind.action.util;

import org.w3c.dom.NodeList;

import java.util.List;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/6 16:08
 */

public class AssertionUtil {

    /**
     * 断言对象是否不为空
     */
    public static boolean notEmpty(Object[] objects) {
        return objects != null && objects.length > 0;
    }

    /**
     * 断言对象是否不为空
     */
    public static boolean notEmpty(Object objects) {
        return objects != null;
    }

    /**
     * 断言对象是否不为空
     */
    public static boolean notEmpty(NodeList list) {
        return list != null && list.getLength() > 0;
    }

    /**
     * 断言对象是否不为空
     */
    public static boolean notEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    /**
     * 断言对象是否为空
     */
    public static boolean isEmpty(Object objects) {
        return objects == null;
    }
    /**
     * 断言对象是否为空
     */
    public static boolean isEmpty(String objects) {
        return objects == null || objects.length()==0;
    }

    /**
     * 断言对象是否为空
     */
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

}
