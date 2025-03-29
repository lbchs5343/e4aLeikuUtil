package com.wind.action.ui;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/4 4:54
 */

public enum Typenum {
    LIB_VISIBLE(1, "工程属性.可视库"),
    LIB_ON_VISIBLE(2, "工程属性.不可视库"),
    LIB_SIMPLE(3, "工程属性.纯依赖库");

    private Integer code;
    private String name;

    Typenum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
