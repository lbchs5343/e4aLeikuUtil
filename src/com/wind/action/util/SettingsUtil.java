package com.wind.action.util;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.groovy.GroovyFileType;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/5 15:53
 */

public class SettingsUtil {

    public static PsiFile getSettingsFiles(Project project) {
        PsiFile[] psiFiles = PsiFileUtil.getFilesByName(project, "settings.gradle");
        String projectPath = project.getBasePath().replace("\\", "/");
        String projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1);
        for (PsiFile psiFile : psiFiles) {
            if (psiFile.getParent().getName().equals(projectName) && psiFile.getFileType() instanceof GroovyFileType) {
                return psiFile;
            }
        }
        return null;
    }

    /**
     * 添加包括
     */
    public static void addInclude(Project project, PsiElement dependenciesElement, List<String> depends) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (String depend : depends) {
                String include = String.format("include ':%s'", depend);
                if (!dependenciesElement.getText().contains(include)) {
                    GrStatement statement = GroovyPsiElementFactory.getInstance(project).createStatementFromText(include);
                    dependenciesElement.add(statement);
                }
            }
        });
    }

    /**
     * 删除节点
     */
    public static void delete(Project project, PsiElement dependenciesElement, String moduleNmae) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiElement child : dependenciesElement.getChildren()) {
                String node = child.getText().replace("\"", "'").replace(" ","");
                if (node.contains("':"+moduleNmae+"'")) {
                    child.delete();
                    break;
                }
            }
        });
    }
    /**
     * 添加包括
     */
    public static void addInclude(Project project, PsiElement dependenciesElement, String depend) {
        List<String> depends = new ArrayList<>();
        depends.add(depend);
        addInclude(project, dependenciesElement, depends);
    }

}
