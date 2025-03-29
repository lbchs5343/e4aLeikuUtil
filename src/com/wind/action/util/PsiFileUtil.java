package com.wind.action.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.File;

/**
 * 文件工具
 *
 * @author ：zhuYi
 * @date ：Created in 2022/8/5 11:27
 */

public class PsiFileUtil {

    /**
     * @param project  当前项目
     * @param fileName 文件名 如:UsageRights.java
     * @return 返回搜索的文件数组
     */
    public static PsiFile[] getFilesByName(Project project, String fileName) {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
        return psiFiles;
    }

    /**
     * @param project  当前项目
     * @param fileName 文件名 如:UsageRights.java
     * @return 返回搜索的文件数组
     */
    public static PsiFile getFilesByName(Project project, String fileName, String directoryName) {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
        if (AssertionUtil.notEmpty(psiFiles)) {
            for (PsiFile psiFile : psiFiles) {
                if (psiFile.getParent().getName().equals(directoryName)) {
                    return psiFile;
                }
            }
        }
        return null;
    }

    /**
     * @param project 当前项目
     * @return 返回搜索的文件数组
     */
    public static PsiFile getFilesByName(Project project, File file) throws Exception {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, file.getName(), GlobalSearchScope.allScope(project));
        String finalDirectory = file.getParent().replace("\\", "/").trim();
        if (AssertionUtil.notEmpty(psiFiles)) {
            for (PsiFile psiFile : psiFiles) {
                String directory = psiFile.getParent().toString();
                directory = directory.substring(directory.indexOf(":") + 1).trim().replace("\\", "/");
                if (directory.equals(finalDirectory)) {
                    return psiFile;
                }
            }
        }
        return null;
    }

    /**
     * 如果是类库结构 则返回 类库modelName 否则返回null
     */
    public static String getModelName(AnActionEvent event) {
        VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
        return getModelName(virtualFile);
    }

    /**
     * 如果是类库结构 则返回 类库modelName 否则返回null
     */
    public static String getModelName(VirtualFile virtualFile) {
        if (virtualFile != null) {
            File file = new File(virtualFile.getPath());
            if (file.isDirectory()) {
                File classLibraryDirectory = new File(file.getAbsolutePath() + "/src/main/java/com/e4a/runtime/components/impl/android");
                if (classLibraryDirectory.isDirectory() && classLibraryDirectory.exists()) {
                    return file.getName();
                }
            }
        }
        return null;
    }

}
