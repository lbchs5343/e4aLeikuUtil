package com.wind.action.e4a.util;

import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.wind.action.util.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/8 23:58
 */

public class DeleteModule extends AnAction {
    private Project project;
    PsiElement appDependenciesElement = null;
    PsiElement settingsElement = null;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            this.project = event.getData(LangDataKeys.PROJECT);
            project = event.getData(LangDataKeys.PROJECT);
            String projectPath = project.getBasePath().replace("\\", "/");
            PsiElement psiElement = BuildUtil.getBuildFilesByDirectory(project, "app");
            if (psiElement != null) {
                appDependenciesElement = BuildUtil.getDependenciesElement(psiElement);
            }

            settingsElement = SettingsUtil.getSettingsFiles(project);
            VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
            File file = new File(virtualFile.getPath());
            if (file.exists()) {
                String mes;
                if (file.isDirectory()) {
                    mes = String.format("你确定要强制删除目录 \"%s\"", file.getName());
                } else {
                    mes = String.format("你确定要强制删除文件 \"%s\"", file.getName());
                }
                int index = Messages.showDialog(project, mes, "提示", new String[]{"确定", "取消"}, 1, Messages.getWarningIcon());
                if (index == 0) {
                    if (file.isDirectory()) {
                        //这可能是一个module
                        if (file.getAbsolutePath().replace("\\", "/").equals(projectPath + "/" + file.getName())) {
                            //解除配置绑定
                            SettingsUtil.delete(project, settingsElement, file.getName());
                            BuildUtil.deleteDependenciesNode(project, appDependenciesElement, file.getName());
                            FileUtil.deleteDirectoryAll(file.getAbsolutePath());
                            Messages.showMessageDialog(event.getProject(), "请点击AS右上角小乌龟进行同步更新", "", Messages.getWarningIcon());

                        }
                    } else {
                        file.delete();
                    }
                    FileDocumentManager.getInstance().saveAllDocuments();
                    SaveAndSyncHandler.getInstance().refreshOpenFiles();
                    VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void update(AnActionEvent event) {
        VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
        if (virtualFile != null) {
            File file = new File(virtualFile.getPath());
            //禁止强删以下文件
            if ("app".equals(file.getName())
                    || "build.gradle".equals(file.getName())
                    || "settings.gradle".equals(file.getName())
                    || "e4aIni.xml".equals(file.getName())
                    || "AndroidManifest.xml".equals(file.getName())
            ) {
                event.getPresentation().setEnabledAndVisible(false);
                return;
            }
            event.getPresentation().setEnabledAndVisible(true);
        }
    }


}
