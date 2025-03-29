package com.wind.action;

import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/5 22:58
 */

public class MyHyperlinkInfo implements HyperlinkInfo {
    String str;

    public MyHyperlinkInfo(String str) {
        this.str = str;
    }

    @Override
    public void navigate(@NotNull Project project) {
        //正则str
        //1.是否包含路径
        //2.是否包含行号
        //3.获取路径
        //4.如果有行号,获取行号

        //打开指定文件 跳转行号



    }
}
