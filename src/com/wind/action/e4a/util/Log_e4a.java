package com.wind.action.e4a.util;

import com.wind.action.util.ColorEnume;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/8 23:46
 */

public interface Log_e4a {
    void i(String str);

    void println(String str);

    void d(String str);

    /**
     * @param str
     * @param backgroundColor 背景颜色
     * @param textColor 文字颜色
     */
    void d(String str, ColorEnume backgroundColor, ColorEnume textColor);


    void printlnErr(Throwable e);

    void printlnErr(String err);
}
