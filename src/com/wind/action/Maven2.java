package com.wind.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.wind.action.util.FileUtil;

import java.io.File;

public class Maven2 extends AnAction {
    String projectPath;
    Project project;

    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getData(CommonDataKeys.PROJECT);
        //F:/gong/umei_backend
        projectPath = project.getBasePath();
        try {
            FileUtil.setFile(new File(projectPath + "/build.gradle"), Configure.ALI_BUILD.replace("替换","classpath 'com.android.tools.build:gradle:4.2.1'"));
            FileUtil.setFile(new File(projectPath + "/gradle/wrapper/gradle-wrapper.properties"), Configure.GRADLE_WRAPPER);
            Messages.showMessageDialog(project, "Maven修改成功!请点击AS右上角小乌龟进行同步更新", "", Messages.getWarningIcon());
        }catch (Exception e){
            Messages.showMessageDialog(project, "Maven修改失败!", "", Messages.getWarningIcon());
        }
    }
}
