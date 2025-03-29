package com.wind.action;


import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.wind.action.e4a.util.AndroidManifestUtil;
import com.wind.action.e4a.util.ProjectCompilation;
import com.wind.action.logicLayer.E4AModel;
import com.wind.action.logicLayer.JavaE4AText;
import com.wind.action.service.WriterService;
import com.wind.action.ui.E4AWindows;
import com.wind.action.util.*;
import com.wind.action.xml.LibNode;
import com.wind.action.xml.XmlBl;
import com.wind.action.xml.XmlTypenum;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class E4AELPUtil extends AnAction {
    private WriterService writerService = ServiceManager.getService(WriterService.class);
    PsiElement appDependenciesElement = null;
    PsiElement settingsElement = null;
    Project project;
    String projectPath, packageName;


    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getData(LangDataKeys.PROJECT);
        projectPath = project.getBasePath().replace("\\", "/");
        PsiElement psiElement = BuildUtil.getBuildFilesByDirectory(project, "app");
        if (psiElement != null) {
            appDependenciesElement = BuildUtil.getDependenciesElement(psiElement);
        }


        settingsElement = SettingsUtil.getSettingsFiles(project);
        String id = e.getActionManager().getId(this);
        if ("TestAction4".equals(id)) {
            importProject(e);
        }
        if ("TestAction7".equals(id)) {
            generateDoc(project);
        }
        if ("TestAction8".equals(id)) {
            String txt = Messages.showInputDialog(
                    project,
                    "作者:随缘 QQ:874334395\n QQ群:476412098",
                    "创建E4A调试窗口环境",
                    Messages.getQuestionIcon()
                    , "请输入要创建的窗口名称", null
            );
            try {
                if (AssertionUtil.notEmpty(txt) && txt.trim().length() > 0) {
                    packageName = AndroidManifestUtil.parsePackageName(new File(projectPath + "/app/src/main/AndroidManifest.xml"));
                    new File(projectPath + "/app/src/main/java/com/e4a/runtime/android").mkdirs();
                    new File(projectPath + "/app/src/main/java/" + packageName.replace(".", "/")).mkdirs();
                    new File(projectPath + "/app/src/main/res/xml").mkdirs();
                    FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "file_paths_android.xml"),
                            new File(projectPath + "/app/src/main/res/xml/file_paths_android.xml"));
                    FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "network_security_config.xml"),
                            new File(projectPath + "/app/src/main/res/xml/network_security_config.xml"));
                    FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "MyE4Aapplication.java"),
                            new File(projectPath + "/app/src/main/java/com/e4a/runtime/android/MyE4Aapplication.java"));
                    FileUtil.写出文件(JavaE4AText.E4ASTYPE, projectPath + "/app/src/main/res/values/e4astyle.xml", StandardCharsets.UTF_8);

                    String javaPath=String.format("%s/app/src/main/java/%s/%s.java",projectPath,packageName.replace(".", "/"),txt);
                    FileUtil.writeOut(javaPath, String.format(JavaE4AText.测试窗口,packageName,txt), "utf8");
                    String androidManifest = FileUtil.读文本文件(projectPath + "/app/src/main/AndroidManifest.xml");
                    String replaceStr = null;
                    try {
                        replaceStr = FileUtil.取_文本中间正常(androidManifest, "android:name=\"MainForm\"", "android:name=\"FontSize\"");
                    } catch (Exception ex) {

                    }
                    if (replaceStr != null && !"".equals(replaceStr.trim())) {
                        androidManifest = androidManifest.replace(replaceStr, "\n                android:value=\""+packageName+"." + txt + "\" />\n" +
                                "            <meta-data\n                ");
                        FileUtil.writeOut(projectPath + "/app/src/main/AndroidManifest.xml", androidManifest, "utf8");
                    } else {
                        FileUtil.writeOut(projectPath + "/app/src/main/AndroidManifest.xml", String.format(JavaE4AText.ANDROIDMANIFEST,packageName, packageName+"."+txt,packageName), "utf8");
                    }
                    VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(javaPath));
                    virtualFile.refresh(false, true);
                    new OpenFileDescriptor(project, virtualFile, 28, -1).navigate(true);
                }
            } catch (Exception ey) {

            }

        }
        if ("TestAction5".equals(id)) {
            createProject(e);
        }
        if ("TestAction6".equals(id)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("请在E4A lib目录下选择你需要引用的类库目录");
            chooser.setApproveButtonText("确定选择");
            int returnVal = chooser.showOpenDialog(chooser);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable runnable = () -> {
                    E4AModel e4AModel = new E4AModel(e, appDependenciesElement, settingsElement);
                    try {
                        String path = chooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
                        e4AModel.importDirectory(path);
                        Messages.showMessageDialog(e.getProject(), "架构创建完毕!请点击AS右上角小乌龟进行同步更新", "", Messages.getWarningIcon());
                    } catch (Exception err) {
                        err.printStackTrace();
                        Messages.showMessageDialog(e.getProject(), "错误:导入失败. 失败原因:" + err.getMessage(), "", Messages.getErrorIcon());
                    }
                };
                WriteCommandAction.runWriteCommandAction(e.getProject(), runnable);
            }
        }
        if ("TestAction9".equals(id)) {
            return;
        }

        getEventProject(e).getBaseDir().refresh(false, true);
        FileDocumentManager.getInstance().saveAllDocuments();
        SaveAndSyncHandler.getInstance().refreshOpenFiles();
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);

    }

    /**
     * 解析类库树,根据类库树生成方法参数名 及文档注释
     */
    private void generateDoc(Project project) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("类库树.xml", "xml");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("请选择你要附加的类库树,如果项目中存在此类库则会进行解析");
        chooser.setApproveButtonText("立即解析");
        int returnVal = chooser.showOpenDialog(chooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
            try {

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            ReadFactory.println(project,"开始解析XML......", ColorEnume.黑色, ColorEnume.白色);
                            XmlBl xmlBl = new XmlBl(new File(path));
                            LibNode libNode = null;
                            for (LibNode xmlNode : xmlBl.getXmlNodes()) {
                                if (xmlNode.xmlTypenum == XmlTypenum.ClASS_INTERFACE_NAME) {
                                    libNode = xmlNode;
                                    break;
                                }
                            }

                            ReadFactory.println(project,"开始查询对应类......", ColorEnume.黑色, ColorEnume.白色);
                            LibNode finalLibNode = libNode;

                            Runnable runnable = () -> {
                                //如果没有该类库则会异常 !比较懒
                                PsiJavaFile psiJavaFile = null;
                                PsiClass psiClasses = null;
                                PsiFile psiFile = PsiFileUtil.getFilesByName(project, finalLibNode.methodName + ".java", finalLibNode.methodName + "类库");
                                Map<String, PsiParameter[]> psiParameterMAP = new HashMap<>();
                                Map<String, PsiMethod> methodMap = new HashMap<>();
                                psiJavaFile = (PsiJavaFile) psiFile;
                                if (psiJavaFile.getClasses().length > 0) {
                                    psiClasses = psiJavaFile.getClasses()[0];
                                    for (PsiMethod method : psiClasses.getMethods()) {
                                        PsiParameter[] psiParameters = method.getParameterList().getParameters();
                                        int lenth = AssertionUtil.notEmpty(psiParameters) ? psiParameters.length : 0;
                                        psiParameterMAP.put(method.getName() + "|" + lenth, psiParameters);
                                        methodMap.put(method.getName() + "|" + lenth, method);
                                        ReadFactory.println(project,"检索到方法:" + method.getName(), ColorEnume.白色, ColorEnume.红色);
                                    }
                                }
                                for (LibNode xmlNode : xmlBl.getXmlNodes()) {
                                    PsiParameter[] psiParameters = psiParameterMAP.get(xmlNode.methodName + "|" + xmlNode.inputParameter);
                                    if (psiParameters != null) {
                                        if (AssertionUtil.notEmpty(psiParameters)) {
                                            for (int i = 0; i < psiParameters.length; i++) {
                                                if (xmlNode.parameterName[i] != null && !"".equals(xmlNode.parameterName[i])) {
                                                    //修改参数名称
                                                    psiParameters[i].setName(xmlNode.parameterName[i]);
                                                }
                                            }
                                        }
                                        if (AssertionUtil.notEmpty(xmlNode.documentationNotes)) {
                                            writerService.writeDocumentationNoThread(project, methodMap.get(xmlNode.methodName + "|" + xmlNode.inputParameter),
                                                    xmlNode.documentationNotes);
                                        }
                                    }
                                }
                                ReadFactory.println(project,"刷新文件结构");
                                FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, psiJavaFile.getVirtualFile()), true);
                            };
                            WriteCommandAction.runWriteCommandAction(project, runnable);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }.start();
            } catch (Throwable t) {
                t.printStackTrace();
                Messages.showMessageDialog(project, "错误:解析失败. 失败原因:" + t.getMessage(), "", Messages.getErrorIcon());

            }
        }


    }

    private void importProject(AnActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "*.elp", "elp");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("选择需要导入的elp文件");
        chooser.setApproveButtonText("导入");
        int returnVal = chooser.showOpenDialog(chooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Runnable runnable = () -> {
                try {
                    E4AModel e4AModel = new E4AModel(e, appDependenciesElement, settingsElement);
                    Messages.showMessageDialog(e.getProject(), "如果你的类库文件比较大的情况下,可能比较耗时", "", Messages.getWarningIcon());
                    e4AModel.importELP(new File(chooser.getSelectedFile().getAbsolutePath()));

                    Messages.showMessageDialog(e.getProject(), "导入完毕!请点击AS右上角小乌龟进行同步更新", "", Messages.getWarningIcon());
                } catch (Throwable t) {
                    t.printStackTrace();
                    Messages.showMessageDialog(e.getProject(), "错误:导入失败. 失败原因:" + t.getMessage(), "", Messages.getErrorIcon());
                }
            };
            WriteCommandAction.runWriteCommandAction(e.getProject(), runnable);
        }
    }

    private void createProject(AnActionEvent e) {
        new E4AWindows(e, appDependenciesElement, settingsElement);
    }


    /**
     * 生成方法Javadoc
     *
     * @param project     工程
     * @param psiMethod   当前方法
     * @param isGenMethod 是否生成方法
     */
    private void genMethodJavadoc(Project project, PsiMethod psiMethod, boolean isGenMethod) {
        if (isGenMethod) {
            saveJavadoc(project, psiMethod);
        }
    }

    /**
     * 生成属性Javadoc
     *
     * @param project    工程
     * @param psiField   当前属性
     * @param isGenField 是否生成属性
     */
    private void genFieldJavadoc(Project project, PsiField psiField, boolean isGenField) {
        if (isGenField) {
            saveJavadoc(project, psiField);
        }
    }

    /**
     * 保存Javadoc
     *
     * @param project    工程
     * @param psiElement 当前元素
     */
    private void saveJavadoc(Project project, PsiElement psiElement) {
        if (psiElement == null) {
            return;
        }
        try {
            PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
            PsiDocComment psiDocComment = factory.createDocCommentFromText("/**Acdfgdgdfg" + System.currentTimeMillis() + "*/");
            writerService.write(project, psiElement, psiDocComment);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 保存Javadoc
     *
     * @param project    工程
     * @param psiElement 当前元素
     */
    private void saveJavadoc(Project project, PsiElement psiElement, String documentation) {
        if (psiElement == null) {
            return;
        }
        try {
            PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
            PsiDocComment psiDocComment = factory.createDocCommentFromText("/**" + documentation + "*/");
            writerService.write(project, psiElement, psiDocComment);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
