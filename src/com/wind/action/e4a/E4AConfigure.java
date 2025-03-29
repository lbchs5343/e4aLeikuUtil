package com.wind.action.e4a;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.wind.action.ReadFactory;
import com.wind.action.e4a.util.AndroidManifestUtil;
import com.wind.action.e4a.util.Log_e4a;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;
import com.wind.action.util.FileUtil;
import com.wind.action.util.ResourceUtils;
import com.wind.action.xml.XmlE4Aini;
import com.wind.lib.util.DependencyUtil;
import com.wind.lib.util.MyArrayList;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/6 21:24
 */

public class E4AConfigure extends Configure {
    private String packageName;
    private List<File> aarFileList;
    private List<File> javaSourceFile = null;
    private String jniLibs;
    private String libs;
    private String assets;
    private XmlE4Aini xmlE4Aini;
    public File interfaceFile;


    public E4AConfigure(Project project, AnAction action, String modelName, String moduleChineseName, @NonNull Log_e4a log) throws Exception {
        super(log,project);

        this.projectPath = project.getBasePath().replace("\\", "/");

        File file = new File(projectPath + "/" + modelName + "/e4aIni.xml");
        if (!file.exists()) {
            log.println("为类库创建配置文件...e4aIni.xml");
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "e4aIni.xml"), file);
        }

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
                virtualFile.refresh(false, true);
                new OpenFileDescriptor(project, virtualFile, 3 , -1).navigate(true);
            }
        });

        log.println("解析配置...e4aIni.xml");
        xmlE4Aini = new XmlE4Aini(new File(projectPath + "/" + modelName + "/e4aIni.xml"), log);
        if(AssertionUtil.notEmpty(xmlE4Aini.mavenList)){
            if(AssertionUtil.isEmpty(xmlE4Aini.mavenRepositoryDirectory)){
                throw new NullPointerException("dependencys 未正确配置! 找不到属性:mavenRepositoryDirectory");
            }
            File file1 = new File(xmlE4Aini.mavenRepositoryDirectory);
            if(!file1.exists()){
                throw new NullPointerException("dependencys 未正确配置! 属性:mavenRepositoryDirectory 错误! 目录不存在");
            }
            if(file1.exists() && !file1.isDirectory()){
                throw new NullPointerException("dependencys 未正确配置! 属性:mavenRepositoryDirectory 错误! 非文件");
            }
            DependencyUtil.init(xmlE4Aini.mavenRepositoryDirectory);
            DependencyUtil.excludeList=xmlE4Aini.excludeList;
        }
        log.println("解析配置...完毕");
        initConfigure(projectPath, xmlE4Aini.e4aGen, modelName, moduleChineseName);
        pathValidation();
        //初始化冲突class
        if(classList==null){
            classList = DependencyUtil.getJarClass(new File(androidSupportV4Jar));
        }
        if (xmlE4Aini.introduceV7) {
            //排除依赖
            classList.addAll(DependencyUtil.getJarClass(ResourceUtils.getResourceAsStreamFile("/static", "support-annotations-28.0.0.jar")));
        }
        initLizeDependencies();
        if (xmlE4Aini.introduceV7) {
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "support-annotations-28.0.0.jar")
                    , new File(resourceLibs + "/support-annotations-28.0.0.jar"));
            log.d("添加V7注解:support-annotations-28.0.0.jar", ColorEnume.灰色, ColorEnume.白色);
        }
        if (xmlE4Aini.SuiyuanUtil) {
            FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "SuiyuanUtil-release.jar")
                    , new File(resourceLibs + "/SuiyuanUtil-release.jar"));
            log.d("随缘工具包:SuiyuanUtil-release.jar", ColorEnume.灰色, ColorEnume.白色);
        }
        FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "sx.bmp")
                , new File(elbDirectory + "/" + moduleChineseName + "/设计区图标.bmp"));
        FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "sx.bmp")
                , new File(elbDirectory + "/" + moduleChineseName + "/属性区图标.bmp"));
        FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "sx.bmp")
                , new File(elbDirectory + "/" + moduleChineseName + "/组件区图标.bmp"));

    }

    @Override
    public void initPath() {
        super.initPath();
        jniLibs = main + "/jniLibs";
        libs = projectPath + "/" + modelName + "/libs";
        assets = main + "/assets";
        interfaceFile = new File(projectPath + "/" + modelName + "/src/main/java/com/e4a/runtime/components/impl/android/" +
                moduleChineseName + "/" + moduleChineseName.replace("类库", "") + ".java");
        FileUtil.deleteDirectory(build);
        FileUtil.deleteDirectory(resourceDirectory);
        FileUtil.deleteDirectory(elbDirectory);
    }

    @Override
    public String getAndroidManifest() {
        return main + "/AndroidManifest.xml";
    }

    @Override
    public String getNewRjar() {
        return modelName + "_R.jar";
    }

    @Override
    public String getNewModelJar() {
        return modelName + ".jar";
    }


    public void initLizeDependencies() throws IOException {
        aarFileList = new ArrayList<>();
        File file = new File(libs);
        File[] files = file.listFiles();
        if (AssertionUtil.notEmpty(files)) {
            for (File listFile : file.listFiles()) {
                if (listFile.isFile()) {
                    if (listFile.getName().endsWith(".jar")) {
                        log.d(moduleChineseName + " jar->>>>>>>:" + listFile.getName(), ColorEnume.灰色, ColorEnume.白色);
                        addResourceJar(listFile);
                    } else if (listFile.getName().endsWith(".aar")) {
                        log.d("检索到aar依赖:" + listFile.getName(), ColorEnume.灰色, ColorEnume.白色);
                        aarFileList.add(listFile);
                    }
                }
            }
        }
        //maven依赖拷贝
        if(AssertionUtil.notEmpty(xmlE4Aini.mavenList)){
            log.d("", ColorEnume.红色, ColorEnume.白色);
            log.d("", ColorEnume.红色, ColorEnume.白色);
            log.d(moduleChineseName + "开始遍历本地仓库依赖", ColorEnume.灰色, ColorEnume.白色);
            List<File> fileList=new MyArrayList<>();
            for (String key : xmlE4Aini.mavenList) {
                DependencyUtil.inquiryDependency(key,fileList,xmlE4Aini.strictMode);
            }
            if(fileList.size()>0) {
                for (File listFile : fileList) {
                    if (listFile.isFile()) {
                        log.d(listFile.getName(), ColorEnume.灰色, ColorEnume.白色);
                        if (listFile.getName().endsWith(".jar")) {
                            log.d(moduleChineseName + " jar->>>>>>>:" + listFile.getName(), ColorEnume.灰色, ColorEnume.白色);
                            addResourceJar(listFile);
                        } else if (listFile.getName().endsWith(".aar")) {
                            log.d("检索到aar依赖:" + listFile.getName(), ColorEnume.灰色, ColorEnume.白色);
                            aarFileList.add(listFile);
                        }
                    }
                }
            }
            log.d("", ColorEnume.红色, ColorEnume.白色);
            log.d("", ColorEnume.红色, ColorEnume.白色);
        }
    }
    /**
     * 创建工程配置
     */
    public void createProjectInformation(boolean isItVisible) {
        String ini = "[工程属性]\n" +
                "类库名称=" + moduleChineseName.replace("类库", "") + "\n" +
                "类库类型=" + (isItVisible ? "可视类库" : "不可视类库") + "\n" +
                "英文jar名=" + modelName + "\n" +
                "类库版本=" + xmlE4Aini.类库版本 + "\n" +
                "自定义属性名称1=" + xmlE4Aini.属性1 + "\n" +
                "自定义属性名称2=" + xmlE4Aini.属性2 + "\n" +
                "自定义属性名称3=" + xmlE4Aini.属性3;
        log.i("写出  工程.txt");
        FileUtil.writeOut(elbDirectory + "/" + moduleChineseName + "/工程.ini", ini, "GBK");
    }

    /**
     * 创建jar清单
     */
    public void createAChecklist() {
        File file = new File(resourceLibs);
        StringBuilder listOfItems = new StringBuilder();
        if (AssertionUtil.notEmpty(file.listFiles())) {
            for (File listFile : file.listFiles()) {
                if (listFile.isFile() && listFile.getName().endsWith(".jar")) {
                    listOfItems.append(listFile.getName()).append("\n");
                }
            }
        }
        log.i("写出 jar包清单.txt ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓<br />" + listOfItems.toString().replace("\n", "<br />"));
        FileUtil.writeOut(elbDirectory + "/" + moduleChineseName + "/jar包清单.txt", FileUtil.adaptiveCoding(listOfItems.toString()), "GBK");
    }


    @Override
    public String getBuildInterimR() {
        return buildR + "/interim/" + modelName;
    }

    @Override
    public String getBuildRclass() {
        return buildR + "/class/" + modelName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void copySo() throws IOException {
        fileCopy(new File(jniLibs), resourceLibs, ".so", false, true);
    }

    @Override
    public void copyAssets() throws IOException {
        fileCopy(new File(assets), resourceAssets, "", false, false);
    }

    @Override
    public void copyRes() throws IOException {
        fileCopy(new File(res), resourceRes, "", true, true);
    }

    public void copyAdded() {
        FileUtil.writeOut(elbDirectory + "/" + moduleChineseName + "/Manifest.xml", xmlE4Aini.manifest.toString(), "GBK");
        FileUtil.writeOut(elbDirectory + "/" + moduleChineseName + "/Permission.xml", xmlE4Aini.permission.toString(), "UTF8");
        FileUtil.writeOut(elbDirectory + "/" + moduleChineseName + "/mainActivity.xml", xmlE4Aini.mainActivity.toString(), "UTF8");
    }

    @Override
    public String getNewValueFileName() {
        return "";
    }
    @Override
    public XmlE4Aini getXmlE4Aini() {
        return xmlE4Aini;
    }

    private void pathValidation() throws Exception {
        if (!new File(getAndroidManifest()).exists()) {
            throw new NullPointerException(getAndroidManifest() + "文件不存在!");
        }
        createADirectory(buildR);
        createADirectory(getBuildInterimR());
        createADirectory(getBuildRclass());
        createADirectory(resourceDirectory);
        createADirectory(resourceLibs);
        createADirectory(resourceRes);
        createADirectory(resourceAssets);
        createADirectory(modelGen);
        createADirectory(elbDirectory);
        parsePackageName();
        //创建打包存放路径
        createADirectory(elbDirectory + "/" + moduleChineseName);
    }


    private void parsePackageName() throws Exception {
        packageName = AndroidManifestUtil.parsePackageName(new File(getAndroidManifest()));
        log.d("获取类库R包名:" + packageName, ColorEnume.浅绿, ColorEnume.白色);
        if (AssertionUtil.isEmpty(packageName)) {
            throw new Exception(getAndroidManifest() + " 数据格式异常!");
        }
        buildRjavaFile = getBuildInterimR() + "/" + packageName.replace(".", "/") + "/R.java";
        buildRjavaAbsolutePath = getBuildInterimR() + "/" + packageName.replace(".", "/");
    }

    public List<File> getAarFileList() {
        return aarFileList;
    }

    @Override
    public List<File> getJavaSourceFile() {
        if (AssertionUtil.isEmpty(javaSourceFile)) {
            javaSourceFile = FileUtil.iterateOverFiles(java, ".java");
        }
        return javaSourceFile;
    }


    @Nullable
    @Override
    public String getJniLibsDirectory() {
        return jniLibs;
    }

    @Nullable
    @Override
    public List<File> getCompileDependencies() {
        File[] fs = new File(resourceLibs).listFiles();
        List<File> jarList = fs != null ? Stream.of(fs).collect(Collectors.toList()) : new ArrayList<>();
        jarList.add(new File(androidJar));
        jarList.add(new File(androidSupportV4Jar));
        jarList.add(new File(e4ARuntimeJar));
        jarList.addAll(xmlE4Aini.jarList);
        return jarList;
    }
}
