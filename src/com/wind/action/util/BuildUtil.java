package com.wind.action.util;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.groovy.GroovyFileType;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/5 14:11
 */
public class BuildUtil {

    /**
     * @param directoryName build所在项目目录 如:app
     */
    public static PsiFile getBuildFilesByDirectory(Project project, String directoryName) {
        PsiFile[] psiFiles = PsiFileUtil.getFilesByName(project, "build.gradle");
        for (PsiFile psiFile : psiFiles) {
            if (psiFile.getParent().getName().equals(directoryName)
                    && psiFile.getFileType() instanceof GroovyFileType) {
                return psiFile;
            }
        }
        return null;
    }

    public static PsiElement getDependenciesElement(PsiElement psiFile) {
        for (PsiElement child : psiFile.getChildren()) {
            if (child.getText().startsWith("dependencies") && "dependencies".equals(child.getFirstChild().getText())) {
                return child;
            }
        }
        return null;
    }

    /**
     * 添加Dependencies，
     *
     * @param project
     * @param dependenciesElement 整个Dependencies父节点
     */
    public static void addDependencies(Project project, PsiElement dependenciesElement, List<String> depends) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (String depend : depends) {
                if (!dependenciesElement.getText().contains(depend)) {
                    GrStatement statement = GroovyPsiElementFactory.getInstance(project).createStatementFromText(depend);
                    PsiElement dependenciesClosableBlock = dependenciesElement.getLastChild();
                    //添加依赖项在 } 前，即在dependencies 末尾添加新的依赖项
                    dependenciesClosableBlock.addBefore(statement, dependenciesClosableBlock.getLastChild());
                }
            }
//            Loger.info("addDependencies success!");
        });
    }


    /**
     * 删除节点
     */
    public static void deleteDependenciesNode(Project project, PsiElement dependenciesElement, String nodeName) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiElement child : dependenciesElement.getChildren()) {
                for (PsiElement childChild : child.getChildren()) {
                    String node = childChild.getText().replace("\"", "'").replace(" ", "");
                    if (node.contains("':" + nodeName + "'")) {
                        childChild.delete();
                        return;
                    }
                }
            }
        });
    }

    /**
     * 添加Dependencies，
     *
     * @param project
     * @param dependenciesElement 整个Dependencies父节点
     */
    public static void addDependencies(Project project, PsiElement dependenciesElement, String depend) {
        List<String> depends = new ArrayList<>();
        depends.add(depend);
        addDependencies(project, dependenciesElement, depends);
    }


}
