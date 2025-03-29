package com.wind.action.util;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/6 20:05
 */

public enum ColorEnume {

    白色("#ffffff"),
    黑色("#000000"),
    黄色("#e5ec26"),
    浅黄("#F65E00"),
    红色("#f60707"),
    绿色("#54B33E"),
    浅绿("#000080"),
    灰色("#52555B");
    private String name;

    ColorEnume(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
