package com.wind.action.service;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/4 18:50
 */


import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ThrowableRunnable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class WriterService {
    private static final Logger LOGGER = Logger.getInstance(WriterService.class);

    public void write(Project project, PsiElement psiElement, PsiDocComment comment) {
        try {
            WriteCommandAction.writeCommandAction(project).run(
                    (ThrowableRunnable<Throwable>) () -> {
                        if (psiElement.getContainingFile() == null) {
                            return;
                        }
                        // 写入文档注释
                        if (psiElement instanceof PsiJavaDocumentedElement) {
                            PsiDocComment psiDocComment = ((PsiJavaDocumentedElement) psiElement).getDocComment();
                            if (psiDocComment == null) {
                                psiElement.getNode().addChild(comment.getNode(), psiElement.getFirstChild().getNode());
                            } else {
                                psiDocComment.replace(comment);
                            }
                        }
                        // 格式化文档注释
                        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
                        PsiElement javadocElement = psiElement.getFirstChild();
                        int startOffset = javadocElement.getTextOffset();
                        int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                        codeStyleManager.reformatText(psiElement.getContainingFile(), startOffset, endOffset + 1);
                    });
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }

    public void writeDocumentationNoThread(Project project, PsiElement psiElement, String documentation) {
        if (psiElement.getContainingFile() == null) {
            return;
        }
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiDocComment comment = factory.createDocCommentFromText("/**" + documentation + "*/");
        // 写入文档注释
        if (psiElement instanceof PsiJavaDocumentedElement) {
            PsiDocComment psiDocComment = ((PsiJavaDocumentedElement) psiElement).getDocComment();
            if (psiDocComment == null) {
                psiElement.getNode().addChild(comment.getNode(), psiElement.getFirstChild().getNode());
            } else {
                psiDocComment.replace(comment);
            }
        }
        // 格式化文档注释
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
        PsiElement javadocElement = psiElement.getFirstChild();
        int startOffset = javadocElement.getTextOffset();
        int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
        codeStyleManager.reformatText(psiElement.getContainingFile(), startOffset, endOffset + 1);
    }

    public void write(Project project, Editor editor, String text) {
        if (project == null || editor == null || (text == null || text.length() == 0)) {
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run(
                    (ThrowableRunnable<Throwable>) () -> {
                        int start = editor.getSelectionModel().getSelectionStart();
                        EditorModificationUtil.insertStringAtCaret(editor, text);
                        editor.getSelectionModel().setSelection(start, start + text.length());
                    });
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }

    public void write(Project project, PsiParameter[] psiParameters, String[] parameterNames) {
        if (project == null || psiParameters == null || parameterNames == null || psiParameters.length != parameterNames.length) {
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run(
                    (ThrowableRunnable<Throwable>) () -> {
                        for (int i = 0; i < psiParameters.length; i++) {
                            if (parameterNames[i] != null && !"".equals(parameterNames[i])) {
                                //修改参数名称
                                psiParameters[i].setName(parameterNames[i]);
                            }
                        }
                    });
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }
}
