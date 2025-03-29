package com.wind.action.e4a.util;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.wind.action.util.BuildUtil;

import com.wind.action.util.SettingsUtil;

import java.io.File;

public class AddModule extends AnAction {
    private Project project;
    PsiElement appDependenciesElement = null;
    PsiElement settingsElement = null;
    String projectPath;

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            this.project = event.getData(LangDataKeys.PROJECT);
            projectPath = project.getBasePath().replace("\\", "/");
            PsiElement psiElement = BuildUtil.getBuildFilesByDirectory(project, "app");
            if (psiElement != null) {
                appDependenciesElement = BuildUtil.getDependenciesElement(psiElement);
            }
            settingsElement = SettingsUtil.getSettingsFiles(project);
            VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
            File file = new File(virtualFile.getPath());
            BuildUtil.addDependencies(project, appDependenciesElement, "implementation project(':" + file.getName() + "')");
            SettingsUtil.addInclude(project, settingsElement, file.getName());
            Messages.showMessageDialog(event.getProject(), "窗口创建完毕!请点击AS右上角小乌龟进行同步更新", "", Messages.getWarningIcon());

        } catch (Exception e) {

        }
    }

    @Override
    public void update(AnActionEvent event) {
        try {
            this.project = event.getData(LangDataKeys.PROJECT);
            project = event.getData(LangDataKeys.PROJECT);
            projectPath = project.getBasePath().replace("\\", "/");
            VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
            File file = new File(virtualFile.getPath());
            if (file.exists()) {
                if (file.isDirectory() && !"app".equals(file.getName())) {
                    //这可能是一个module
                    if (file.getAbsolutePath().replace("\\", "/").equals(projectPath + "/" + file.getName())) {
                        event.getPresentation().setEnabledAndVisible(true);
                        return;
                    }
                }

            }
        } catch (Exception e) {
        }
        event.getPresentation().setEnabledAndVisible(false);

    }


}
