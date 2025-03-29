package com.wind.action.e4a.util;

import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.wind.action.ReadFactory;
import com.wind.action.e4a.*;
import com.wind.action.util.*;
import com.wind.lib.util.DependencyUtil;
import com.wind.lib.util.MyArrayList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/8 23:58
 */

public class ProjectCompilation extends AnAction {
    private  boolean packingStatus = false;
    private Project project;
    boolean globalErr = false;
    E4AConfigure e4AConfigure;
    Log_e4a log;
    List<Configure> configureList;
    CommandUtil mainCommandUtil;
    String modelName;


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        this.project = anActionEvent.getData(LangDataKeys.PROJECT);
        modelName = PsiFileUtil.getModelName(anActionEvent);
        String projectPath = project.getBasePath().replace("\\", "/");
        if (AssertionUtil.notEmpty(modelName)) {
            Configure.fileMap.clear();

            compilation(modelName, projectPath);
        } else {
            Messages.showMessageDialog(project, "你选择的Module藐视不正确!!!!!", "", Messages.getWarningIcon());
        }
    }

    @Override
    public void update(AnActionEvent event) {//根据扩展名是否是jar，显示隐藏此Action
        try {
            event.getPresentation().setEnabledAndVisible(AssertionUtil.notEmpty(PsiFileUtil.getModelName(event)));
        } catch (Exception ignored) {

        }
    }

    public synchronized void compilation(String modelName, String projectPath) {
        ReadFactory.show(project, () -> {
            ReadFactory.clear(project);
            ReadFactory.println(project,"项目名:" + modelName);
            ReadFactory.println(project,"刷新文件修改...");
            FileDocumentManager.getInstance().saveAllDocuments();
            ReadFactory.println(project,"同步刷新文件...");
            VirtualFileManager.getInstance().syncRefresh();
            ReadFactory.setDisplayName(project,"检索中....");
            ReadFactory.println(project,"准备编译所需资源");
            new Thread() {
                @Override
                public void run() {
                    try {
                        packingStatus = true;
                        Thread.sleep(2000);
                        packingStatus = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    init(modelName, projectPath);
                }
            }.start();

        });

    }

    private synchronized void init(String modelName, String projectPath) {
        globalErr = false;
        log = new Log_e4a() {
            @Override
            public void i(String str) {
                System.out.println(str);
                ReadFactory.println(project,str);
            }

            @Override
            public void d(String str) {
                System.out.println(str);
                ReadFactory.printlnErr(project,str);
            }

            @Override
            public void d(String str, ColorEnume backgroundColor, ColorEnume textColor) {
                System.out.println(str);
                ReadFactory.println(project,str, backgroundColor, textColor);
            }

            @Override
            public void printlnErr(Throwable e) {
                ReadFactory.printlnErr(project,e);
            }

            @Override
            public void printlnErr(String err) {
                ReadFactory.printlnErr(project,err);
            }

            @Override
            public void println(String str) {
                ReadFactory.println(project,str);
            }

        };
        try {
            String moduleChineseName = Configure.getModuleChineseName(projectPath, modelName);
            if (AssertionUtil.notEmpty(moduleChineseName)) {
                File filesss = new File(projectPath + "/" + modelName +
                        "/src/main/java/com/e4a/runtime/components/impl/android/" + moduleChineseName + "/"
                        + moduleChineseName.replace("类库", ".java"));
                if (!filesss.exists()) {
                    log.d("编译失败找不到类库接口文件->>>" + moduleChineseName.replace("类库", ".java"), ColorEnume.红色, ColorEnume.白色);
                    return;
                }
                log.i("开始解析类库接口");
                e4AConfigure = new E4AConfigure(project, this,
                        modelName, moduleChineseName, log);

                WriteCommandAction.runWriteCommandAction(project, () -> {
                    ReadFactory.setDisplayName(project,e4AConfigure.modelName + "|" + e4AConfigure.moduleChineseName + "|Log");
                });

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.i("开始解析类库接口");
                            ClassLibraryTreeParsing classLibraryTreeParsing = new ClassLibraryTreeParsing(e4AConfigure);
                            //写出类库树
                            classLibraryTreeParsing.writeOut();
                            log.i("类库接口解析完毕");
                            //写工程配置
                            e4AConfigure.createProjectInformation(classLibraryTreeParsing.visible == 1);

                        } catch (Exception e) {
                            log.printlnErr(e);
                            log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
                        }
                    }
                };
                WriteCommandAction.runWriteCommandAction(project, runnable);
                configureList = new ArrayList<>();
                try {
                    //生成R文件
                    mainCommandUtil = new CommandUtil(e4AConfigure,log);
                    configureList.add(e4AConfigure);
                    Map<String, String> packageNamesMap = new HashMap<>();
                    Map<String, List<File> >aarNamesMap = new HashMap<>();
                    //aar重复处理
                    //以文件名先进行分组
                    for (File file : e4AConfigure.getAarFileList()) {
                        List<File> aarList=aarNamesMap.get(file.getName());
                        if(AssertionUtil.isEmpty(aarList)){
                            aarList=new MyArrayList<>();
                            aarNamesMap.put(file.getName(),aarList);
                        }
                        aarList.add(file);
                    }
                    for (String key : aarNamesMap.keySet()) {
                        List<File> aarList=aarNamesMap.get(key);
                        if(aarList.size()==1){
                            //如果只有一个则直接复制处理
                            AarConfigure aarConfigure = new AarConfigure(e4AConfigure.projectPath, e4AConfigure.e4aGen, e4AConfigure.modelName, moduleChineseName, aarList.get(0), "",e4AConfigure.getXmlE4Aini(), e4AConfigure.log);
                            aarConfigure.addClass();
                            configureList.add(aarConfigure);
                        }else {
                            //如果存在多个,则进行jar包合并处理
                            List<File> classJarList=new MyArrayList<>();
                            for (int i = 0; i < aarList.size(); i++) {
                                AarConfigure aarConfigure = new AarConfigure(e4AConfigure.projectPath, e4AConfigure.e4aGen, e4AConfigure.modelName, moduleChineseName, aarList.get(i), "_"+i,e4AConfigure.getXmlE4Aini(), e4AConfigure.log);
                                File classJar=aarConfigure.getClassJar();
                                if(classJar.exists()){
                                    classJarList.add(aarConfigure.getClassJar());
                                }
                                configureList.add(aarConfigure);
                            }
                            //合并重复的AAR
                            String newClassName=key.replace(".aar",".jar");
                            DependencyUtil.ruleOutClass(classJarList,new File(e4AConfigure.resourceLibs+"/"+newClassName),Configure.classList);
                        }
                    }
                    //解决R.jar冲突方案,同包则使用包名做R名称
                    for (Configure configure : configureList) {
                        if(configure instanceof AarConfigure){
                            AarConfigure aarConfigure= (AarConfigure) configure;
                            if (packageNamesMap.get(aarConfigure.packageName) == null) {
                                if(aarConfigure.packageName.equals("android.support.graphics.drawable")){
                                    packageNamesMap.put(aarConfigure.packageName, "android-support-graphics-drawable_R.jar");
                                }else{
                                    packageNamesMap.put(aarConfigure.packageName, aarConfigure.getNewRjar());
                                }
                            } else {
                                String newRjar = aarConfigure.packageName.replace(".", "-") + "_R.jar";
                                packageNamesMap.put(aarConfigure.packageName, newRjar);
                            }
                        }

                    }
                    //获取依赖
                    for (Configure configure : configureList) {
                        if(configure instanceof AarConfigure){
                            AarConfigure aarConfigure= (AarConfigure) configure;
                            aarConfigure.setNewRjar(packageNamesMap.get(aarConfigure.packageName));
                            CommandUtil commandUtil2 = new CommandUtil(aarConfigure,log);
                            commandUtil2.createRjava();
                            log.i("编译 " + aarConfigure.aarName + " R.java");
                        }
                    }


                    List<ModelConfigure> modelConfigureList = new ArrayList<>();
                    //开始编译其他model
                    if (e4AConfigure.getXmlE4Aini().modelList.size() > 0) {
                        log.d("", ColorEnume.绿色, ColorEnume.绿色);
                        for (String module : e4AConfigure.getXmlE4Aini().modelList) {
                            log.d("开始编译 附加module:" + module, ColorEnume.浅绿, ColorEnume.白色);
                            File file = new File(e4AConfigure.projectPath + "/" + module);
                            if (!file.exists() || !file.isDirectory()) {
                                log.d("跳过无效的moduleName" + module, ColorEnume.白色, ColorEnume.黑色);
                                continue;
                            }
                            ModelConfigure modelConfigure = new ModelConfigure(
                                    e4AConfigure.projectPath, e4AConfigure.e4aGen,
                                    e4AConfigure.modelName, e4AConfigure.moduleChineseName,
                                    module, e4AConfigure.getXmlE4Aini(), log,e4AConfigure.resourceLibs);
                            configureList.add(modelConfigure);
                            modelConfigure.state = false;
                            modelConfigureList.add(modelConfigure);
                            CommandUtil commandUtil2 = new CommandUtil(modelConfigure,log);
                            commandUtil2.createModuleRjava();
                            try {
                                commandUtil2.compileJava(modelConfigure.getCompileDependencies(), new MyRunnable() {
                                    @Override
                                    public void ok() {
                                        String err = commandUtil2.getError();
                                        if (!"".equals(err)) {
                                            if (err.contains("^") || err.contains("个错误")) {
                                                globalErr = true;
                                                log.d("附加module出现错误!", ColorEnume.红色, ColorEnume.白色);
                                                return;
                                            }
                                            if (!err.contains("请使用 -Xlint:deprecation 重新编译") && !err.contains("请使用 -Xlint:unchecked 重新编译")) {
                                                globalErr = true;
                                                log.d("附加module出现错误!", ColorEnume.红色, ColorEnume.白色);
                                                return;
                                            }
                                        }
                                        try {
                                            commandUtil2.jarcvfNewModel();
                                            modelConfigure.state = true;
                                        } catch (Exception e) {
                                            log.printlnErr(e);
                                            e.printStackTrace();
                                            globalErr = true;
                                        }
                                    }

                                    @Override
                                    public void err() {
                                        globalErr = true;
                                    }

                                    @Override
                                    public void mNull() {
                                        globalErr = true;
                                    }
                                });
                            } catch (Exception e) {
                                log.printlnErr(e);
                                globalErr = true;
                            }
                        }
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                int x = 0;
                                while (true) {
                                    if (packingStatus) {
                                        // 编译被强制阻断
                                        return;
                                    }
                                    log.d("----------------------阻塞中" + (x++) + "-------------------------", ColorEnume.白色, ColorEnume.黑色);
                                    Thread.sleep(1000);
                                    if (globalErr) {
                                        log.d("附加module异常!编译失败", ColorEnume.红色, ColorEnume.白色);
                                        throw new Exception("编译失败!!!!!!!!");
                                    }
                                    if (modelConfigureList.size() == 0) {
                                        break;
                                    }
                                    int i = 0;
                                    for (ModelConfigure modelConfigure : modelConfigureList) {
                                        if (modelConfigure.state) {
                                            i++;
                                        }
                                    }
                                    if (i == modelConfigureList.size()) {
                                        break;
                                    }
                                }
                                log.d("", ColorEnume.绿色, ColorEnume.绿色);
                                continueToCompile(e4AConfigure, mainCommandUtil, log, configureList);
                            } catch (Exception e) {
                                log.printlnErr(e);
                                log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
                            }

                        }
                    }.start();
                    //耗时程序
                } catch (Throwable e) {
                    log.printlnErr(e);
                    log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
                }

            } else {
                log.d("编译失败找不到类库接口文件->>>null", ColorEnume.红色, ColorEnume.白色);
            }
        } catch (Exception t) {
            t.printStackTrace();
            log.printlnErr(t);
            log.d(t.getMessage(), ColorEnume.红色, ColorEnume.白色);

        }
    }


    public void continueToCompile(E4AConfigure e4AConfigure, CommandUtil commandUtil, Log_e4a log, List<Configure> configureList) throws Exception {
        log.i("res资源拷贝中...........");
        for (Configure configure : configureList) {
            //拷贝so
            try {
                configure.copyRes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFileManager.getInstance().syncRefresh();
        });
        log.i("res资源拷贝成功...........");
        log.d("编译类库:" + e4AConfigure.modelName + "_R.java", ColorEnume.浅绿, ColorEnume.白色);
        mainCommandUtil.createRjava(e4AConfigure.resourceRes);
        for (Configure configure : configureList) {
            //删除冲突文件
            configure.deleteConflict();
        }
        //开始编译model_Jar
        log.i("编译类库:" + e4AConfigure.modelName + ".jar");
        commandUtil.compileJava(e4AConfigure.getCompileDependencies(), new MyRunnable() {
            @Override
            public void ok() {
                try {
                    String err = commandUtil.getError();
                    if (!"".equals(err)) {
                        if (err.contains("^") || err.contains("个错误")) {
                            throw new IOException("jar编译失败!");
                        }
                        if (!err.contains("请使用 -Xlint:deprecation 重新编译") && !err.contains("请使用 -Xlint:unchecked 重新编译")) {
                            throw new IOException("jar编译失败!");
                        }
                    }
                    //开始编译jar包
                    commandUtil.jarcvfModel();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                long time = System.currentTimeMillis();
                                //开发复制资源文件
                                log.d("", ColorEnume.黄色, ColorEnume.黄色);
                                log.i("资源拷贝中...........");
                                for (Configure configure : configureList) {
                                    //拷贝so
                                    try {
                                        configure.copySo();
                                        configure.copyAssets();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                log.i("资源拷贝结束...........耗时" + (System.currentTimeMillis() - time));
                                System.gc();
                                //创建jar清单文件
                                e4AConfigure.createAChecklist();
                                //写出附加信息
                                e4AConfigure.copyAdded();
                                log.d("", ColorEnume.黄色, ColorEnume.黄色);
                                log.d("压缩 resource.zip");
                                //开始压缩资源
                                ZipUtils.zip(e4AConfigure.resourceDirectory + "/",
                                        e4AConfigure.elbDirectory + "/" + e4AConfigure.moduleChineseName + "/resource.zip");

                                log.d("压缩 " + e4AConfigure.moduleChineseName + ".elb");
                                ZipUtils.zip(e4AConfigure.elbDirectory, e4AConfigure.elbDirectory + "/" + e4AConfigure.moduleChineseName + ".elb");
                                log.d("安装 " + e4AConfigure.moduleChineseName + ".elb");
                                //开始安装
                                installTheClassLibrary(e4AConfigure);
                                log.d(e4AConfigure.elbDirectory + "/" + e4AConfigure.moduleChineseName + ".elb ");
                                log.d("", ColorEnume.黄色, ColorEnume.黄色);
                                log.d("E4A类库封装插件  作者:随缘  QQ:874334395   QQ群:476412098", ColorEnume.黑色, ColorEnume.黄色);
                                log.d("", ColorEnume.黄色, ColorEnume.黄色);
                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                    project.getBaseDir().refresh(false, true);
                                    FileDocumentManager.getInstance().saveAllDocuments();
                                    VirtualFileManager.getInstance().syncRefresh();
                                    SaveAndSyncHandler.getInstance().refreshOpenFiles();
                                    VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
                                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Messages.showMessageDialog(project, modelName+"\n恭喜你!离成功又进了一步......\n" +
                                                    "\n插件作者:随缘 QQ:874334395\n" +
                                                    "QQ群:476412098", "", Messages.getWarningIcon());
                                        }
                                    });
                                });
                            } catch (Throwable e) {
                                log.printlnErr(e);
                                log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
                            }
                        }
                    }.start();
                } catch (Throwable e) {
                    log.printlnErr(e);
                    log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
                }

            }

            @Override
            public void err() {
                log.printlnErr("打包失败 err");
            }

            @Override
            public void mNull() {
                log.printlnErr("打包失败 null");
            }


        });
    }

    public void installTheClassLibrary(E4AConfigure e4AConfigure) throws IOException {
        FileUtil.deleteDirectory(e4AConfigure.e4aGen + "/libs/" + e4AConfigure.moduleChineseName);
        File directory = new File(e4AConfigure.elbDirectory + "/" + e4AConfigure.moduleChineseName);
        copyFile(directory, e4AConfigure.e4aGen + "/libs/" + e4AConfigure.moduleChineseName);
    }

    public void copyFile(File directory, String sDirectory) throws IOException {
        File toDirectory = new File(sDirectory);
        if (!toDirectory.exists()) {
            toDirectory.mkdirs();
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                copyFile(file, sDirectory + "/" + file.getName());
            } else {
                FileUtil.copy(file, new File(sDirectory + "/" + file.getName()));
            }
        }
    }


}
