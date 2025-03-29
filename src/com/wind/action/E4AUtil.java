package com.wind.action;

import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.wind.action.e4a.util.AndroidManifestUtil;
import com.wind.action.util.FileUtil;
import com.wind.action.util.ResourceUtils;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class E4AUtil extends AnAction {
    String projectPath, appPath, libsPath, projectLibsPath, packageName;
    Project project;

    @Override
    public void actionPerformed(AnActionEvent ev) {
        project = ev.getData(CommonDataKeys.PROJECT);
        //F:/gong/umei_backend
        projectPath = project.getBasePath();
        appPath = projectPath + "/app";
        libsPath = appPath + "/libs";
        projectLibsPath = projectPath + "/libs";
        try {
            packageName = AndroidManifestUtil.parsePackageName(new File(appPath + "/src/main/AndroidManifest.xml"));
            initDirectory();
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "E4ARuntime.jar")
                    , new File(libsPath + "/E4ARuntime.jar"));
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "E4ARuntime.jar")
                    , new File(projectLibsPath + "/E4ARuntime.jar"));
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "SuiyuanUtil-release.jar")
                    , new File(libsPath + "/SuiyuanUtil-release.jar"));
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "SuiyuanUtil-release.jar")
                    , new File(projectLibsPath + "/SuiyuanUtil-release.jar"));
            copyDependencies();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDirectory() {
        createADirectory(new File(libsPath));
        createADirectory(new File(projectLibsPath));
        createADirectory(new File(projectPath + "/libs"));
    }

    private void createADirectory(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void copyDependencies() {
        try {
            FileUtil.setFile(new File(appPath + "/build.gradle"), String.format(Configure.BUILE,packageName));
            FileUtil.setFile(new File(projectPath + "/build.gradle"), Configure.MAVEN_BUILD);
            FileUtil.setFile(new File(projectPath + "/gradle/wrapper/gradle-wrapper.properties"), Configure.GRADLE_WRAPPER);
            Messages.showMessageDialog(project, "环境初始化完毕!", "", Messages.getWarningIcon());
            project.getBaseDir().refresh(false, true);
            FileDocumentManager.getInstance().saveAllDocuments();
            SaveAndSyncHandler.getInstance().refreshOpenFiles();
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
        } catch (Exception e) {
            Messages.showMessageDialog(project, "在拷贝依赖时发生未知异常!请重试", "", Messages.getErrorIcon());
        }
    }


}
