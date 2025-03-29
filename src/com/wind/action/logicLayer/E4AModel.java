package com.wind.action.logicLayer;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.wind.action.ui.Typenum;
import com.wind.action.util.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/3 20:23
 */

public class E4AModel {
    public static final String BUILD = "plugins {\n" +
            "    id 'com.android.library'\n" +
            "}\n" +
            "\n" +
            "android {\n" +
            "    compileSdkVersion 30\n" +
            "    buildToolsVersion \"30.0.3\"\n" +
            "\n" +
            "    defaultConfig {\n" +
            "        minSdkVersion 19\n" +
            "        targetSdkVersion 30\n" +
            "        versionCode 1\n" +
            "        versionName \"1.0\"\n" +
            "        repositories {\n" +
            "            flatDir {\n" +
            "                dirs 'libs'\n" +
            "            }\n" +
            "        }\n" +
            "        ndk {\n" +
            "            // add support lib\n" +
            "            //abiFilters 'armeabi-v7a' //, 'arm64-v8a'//, \"mips\"  //,'armeabi''x86',, 'x86_64',\n" +
            "        }\n" +
            "        testInstrumentationRunner \"android.support.test.runner.AndroidJUnitRunner\"\n" +
            "        consumerProguardFiles \"consumer-rules.pro\"\n" +
            "    }\n" +
            "\n" +
            "    buildTypes {\n" +
            "        release {\n" +
            "            minifyEnabled false\n" +
            "            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'\n" +
            "        }\n" +
            "    }\n" +
            "    compileOptions {\n" +
            "        sourceCompatibility JavaVersion.VERSION_1_8\n" +
            "        targetCompatibility JavaVersion.VERSION_1_8\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "dependencies {\n" +
            "\n" +
            "    //此为公共依赖 注意不要修改compileOnly修饰否则多项目容易导致依赖冲突\n" +
            "    //注意AS默认是v7包,在打包过程不会自动导v7或v4 , 需要将jar放入libs目录下\n" +
            "    //公共依赖目录 ../libs 请勿更改.\n" +
            "    //项目依赖请自行放入项目下libs\n" +
            "    //此为公共依赖\n" +
            "    compileOnly files('../libs/E4ARuntime.jar')\n" +
            "    compileOnly files('../libs/SuiyuanUtil-release.jar')\n" +
            "    implementation 'com.android.support:appcompat-v7:28.0.0'\n" +
            "\t\n" +
            "\t//glide-3.7.0.jar 如果需要图片加载框架,请统一使用此版本\n" +
            "\t\n" +
            "\t\n" +
            "%s\n" +
            "\n" +
            "}";


    public static final String AndroidManifest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"com.e4a.res.%s\">\n" +
            "</manifest>";
    public static final String e4ainitxml = "<配置 xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "      xmlns:tools=\"http://schemas.android.com/tools\">\n" +
            "    <编译配置>\n" +
            "        <sdk>C:/e4a</sdk>\n" +
            "        <!--如果为true 则会自动注入E4A中缺失的注解!-->\n" +
            "        <引入V7注解>false</引入V7注解>\n" +
            "        <!--由于更新频繁 随缘工具包不建议使用,-->\n" +
            "        <引入随缘工具包>false</引入随缘工具包>\n" +
            "    </编译配置>\n" +
            "    <module\n" +
            "        说明=\"如果你的项目有依赖其他module,请在此列出,注意你附加的module必须是唯一完整且没有在嵌套别的module,否则可能无法正常为你编译!\n" +
            "        model_libs下可以有jar但不能有aar\">\n" +
            "        <moduleName 说明=\"如果此moduleName不存在,默认跳过\">VideoAccelerate</moduleName>\n" +
            "    </module>\n" +
            "    <额外的JAR 说明=\"注意此配置下的jar,只在编译时使用,并不会打包进elb中\">\n" +
            "        <flie 说明=\"jar包的绝对路径\">d:/xxxxx/xxxx.jar</flie>\n" +
            "    </额外的JAR>\n" +
            "\n" +
            "    <!--AS 本地仓库遍历 注意:本地仓库路径格式  strictMode 严谨模式下,必须存在依赖否则异常-->\n" +
            "    <dependencys 说明=\"递归AS 本地仓库\" mavenRepositoryDirectory=\"C:/Users/${userName}/.gradle/caches/modules-2/files-2.1\" strictMode=\"true\">\n" +
            "        <!--如果:引入V7注解节点为true 则会自动排除以下包中冲突的注解-->\n" +
            "        <!--你编写的每一个库注意依赖版本-->\n" +
            "        <!--implementation com.android.support:appcompat-v7:28.0.0-->\n" +
            "        <dependency>appcompat-v7:28.0.0</dependency>\n" +
            "        <dependency>annotation:1.2.0</dependency>\n" +
            "        <exclude>annotation:1.0.0</exclude>\n" +
            "    </dependencys>\n" +
            "    <!--关于资源xml文件中属性冲突解决方案-->\n" +
            "    <!-- E:/E4A_Demo/suiyuan_tupianliebiaokuangTV6/build/e4a/resource/res/values/supportcompat28_0_0values.xml:6-->\n" +
            "    <values_err 说明=\"本地仓库资源属性可能存在冲突,通过此设置进行删除处理\">\n" +
            "        <err>supportcompat28_0_0values.xml:9</err>\n" +
            "        <err>supportcompat28_0_0values.xml:7</err>\n" +
            "    </values_err>\n" +
            "    <类库信息>\n" +
            "        <类库版本>1.0</类库版本>\n" +
            "        <作者>随缘</作者>\n" +
            "        <QQ>874334395</QQ>\n" +
            "        <QQ群>476412098</QQ群>\n" +
            "        <描述>.......................</描述>\n" +
            "    </类库信息>\n" +
            "    <附加权限>\n" +
            "        %s\n" +
            "    </附加权限>\n" +
            "    <Manifest 说明=\"在AndroidManifest: activity, servicev, reciver, meta-data\">\n" +
            "        %s\n" +
            "    </Manifest>\n" +
            "    <mainActivity 说明=\"在mainActivity:intent-filter,meta-data\">\n" +
            "        %s\n" +
            "    </mainActivity>\n" +
            "    <自定义属性 说明=\"固定节点勿修改节点名称\">\n" +
            "        <属性1 说明=\"★\">\n" +
            "            %s\n" +
            "        </属性1>\n" +
            "        <属性2 说明=\"◆\">\n" +
            "            %s\n" +
            "        </属性2>\n" +
            "        <属性3 说明=\"■\">\n" +
            "            %s\n" +
            "        </属性3>\n" +
            "    </自定义属性>\n" +
            "    <V7魔改版开发类库说明>\n" +
            "        <开发注意事项>\n" +
            "            如果你已经开始开发V7类库或安卓X类库了!\n" +
            "            你必须先到QQ:476412098 下载E4A魔改资料魔改你的E4A,\n" +
            "            否则你编译的类库无法正常使用甚至无法完成类库编译会.\n" +
            "            QQ群中魔改版向下兼容,但你必须确保你的jdk版本必须>=8\n" +
            "        </开发注意事项>\n" +
            "        <内置依赖>\n" +
            "            以下包包含资源文件或R文件 ,在 dependency节点下只要配置了对应坐标则会自动为其生成资源及R\n" +
            "            关于注解 无任何内置,依旧只有E4A已经有的注解\n" +
            "            为了补全注解,你可以设置 节点:引入V7注解=true\n" +
            "            implementation 'androidx.versionedparcelable:versionedparcelable:1.0.0'\n" +
            "            implementation 'com.android.support:appcompat-v7:28.0.0'\n" +
            "            implementation 'android.arch.lifecycle:runtime:1.1.1'\n" +
            "            implementation 'android.arch.core:runtime:1.1.1'\n" +
            "            implementation 'android.arch.lifecycle:common:1.1.1'\n" +
            "            implementation 'com.android.support.constraint:constraint-layout:1.1.3'\n" +
            "        </内置依赖>\n" +
            "        <推荐统一版本>\n" +
            "            implementation 'com.android.support:appcompat-v7:28.0.0'\n" +
            "            implementation 'com.android.support:recyclerview-v7:28.0.0'\n" +
            "            implementation 'androidx.versionedparcelable:versionedparcelable:1.0.0'\n" +
            "            implementation 'com.android.support:appcompat-v7:28.0.0'\n" +
            "            implementation 'android.arch.lifecycle:runtime:1.1.1'\n" +
            "            implementation 'android.arch.core:runtime:1.1.1'\n" +
            "            implementation 'android.arch.lifecycle:common:1.1.1'\n" +
            "            implementation 'com.android.support.constraint:constraint-layout:1.1.3'\n" +
            "        </推荐统一版本>\n" +
            "    </V7魔改版开发类库说明>\n" +
            "</配置>";


    AnActionEvent anActionEvent;
    Project project;
    String projectPath;
    PsiElement appDependenciesElement, settingsElement;

    public E4AModel(AnActionEvent anActionEvent, PsiElement appDependenciesElement, PsiElement settingsElement) {
        this.appDependenciesElement = appDependenciesElement;
        this.settingsElement = settingsElement;
        this.anActionEvent = anActionEvent;
        project = anActionEvent.getData(CommonDataKeys.PROJECT);
        projectPath = project.getBasePath().replace("\\", "/");

    }

    private void createADirectory(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public Project getProject() {
        return project;
    }

    private void createADirectory(String fileStr) {
        createADirectory(new File(fileStr));
    }

    public void createClassLibrary(Typenum type, String classLibraryName, ArrayList<String> libList) throws Exception {
        String MODULE_NAME = PinyinUtil.getPinyin(classLibraryName, "");
        avermentModel(MODULE_NAME);
        initModelDirectory(MODULE_NAME);
        createBUILD(MODULE_NAME);
        addSettings(MODULE_NAME);
        addAppBuild(MODULE_NAME);
        createAndroidManifest(MODULE_NAME);

        String javaFileDirectory = projectPath + "/" + MODULE_NAME + "/src/main/java/com/e4a/runtime/components/impl/android/" + classLibraryName + "类库";
        createADirectory(javaFileDirectory);
        String interfaceText, interfaceImplementText;
        if (type == Typenum.LIB_VISIBLE) {
            interfaceText = String.format(JavaE4AText.可视接口, "com.e4a.runtime.components.impl.android." + classLibraryName + "类库", classLibraryName);
            interfaceImplementText = String.format(JavaE4AText.可视实现, "com.e4a.runtime.components.impl.android." + classLibraryName + "类库",
                    MODULE_NAME, classLibraryName, classLibraryName, classLibraryName);
        } else if (type == Typenum.LIB_ON_VISIBLE) {
            interfaceText = String.format(JavaE4AText.不可视接口, "com.e4a.runtime.components.impl.android." + classLibraryName + "类库", classLibraryName);
            interfaceImplementText = String.format(JavaE4AText.不可视实现, "com.e4a.runtime.components.impl.android." + classLibraryName + "类库",
                    classLibraryName, classLibraryName, classLibraryName);
        } else {
            interfaceImplementText = interfaceText = "";
        }
        if (type != Typenum.LIB_SIMPLE) {
            FileUtil.写出文件(interfaceText, javaFileDirectory + "/" + classLibraryName + ".java", StandardCharsets.UTF_8);
            FileUtil.写出文件(interfaceImplementText, javaFileDirectory + "/" + classLibraryName + "Impl.java", StandardCharsets.UTF_8);
            FileUtil.写出文件(JavaE4AText.E4ASTYPE, projectPath + "/" + MODULE_NAME + "/src/main/res/values/e4astyle.xml", StandardCharsets.UTF_8);
        }
        createBUILD(MODULE_NAME, libList);
    }

    //断言是否存在
    private void avermentModel(String MODULE_NAME) throws Exception {
        File modelFile = new File(projectPath + "/" + MODULE_NAME);
        if (modelFile.exists()) {
            throw new Exception("项目已经存在!,无法继续");
        }
        modelFile.mkdirs();

    }

    private void forceDelete(String directory) {
        try {
            File file = new File(directory);
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.isDirectory() && file1.listFiles().length > 0) {
                    forceDelete(file1.getAbsolutePath());
                }
                file1.delete();
            }
        } catch (Exception e) {

        }
    }


    private void initModelDirectory(String MODULE_NAME) {
        createADirectory(projectPath + "/" + MODULE_NAME);
        createADirectory(projectPath + "/" + MODULE_NAME + "/libs");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/java");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/java/com/sy/" + MODULE_NAME.replace("_", "").toLowerCase() + "/view");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/java/com/sy/" + MODULE_NAME.replace("_", "").toLowerCase() + "/adapter");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/java/com/sy/" + MODULE_NAME.replace("_", "").toLowerCase() + "/config");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/java/com/sy/" + MODULE_NAME.replace("_", "").toLowerCase() + "/util");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/java/com/sy/" + MODULE_NAME.replace("_", "").toLowerCase() + "/entity");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/res");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/res/drawable");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/res/layout");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/res/values");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/assets");
        createADirectory(projectPath + "/" + MODULE_NAME + "/src/main/jniLibs");
    }


    public void importDirectory(String path) throws Exception {
        if (!new File(path + "/工程.ini").exists()) {
            throw new Exception("未找到工程.ini等相关文件!");
        }
        String folder = projectPath + "/临时解压" + System.currentTimeMillis();
        //创建临时解压目录
        File temporaryPath = new File(folder);
        if (temporaryPath.exists()) {
            temporaryPath.delete();
        }
        temporaryPath.mkdirs();
        String projectStr = FileUtil.readTextFile(path + "/工程.ini", "GBK");
        String classLibraryName = StringUtils.substring(projectStr, "类库名称=", "\n").trim();
        String classLibraryType = StringUtils.substring(projectStr, "类库类型=", "\n").trim();
        String MODULE_NAME = PinyinUtil.getPinyin(classLibraryName, "");
        addSettings(MODULE_NAME);
        addAppBuild(MODULE_NAME);
        avermentModel(MODULE_NAME);
        initModelDirectory(MODULE_NAME);
        if (ZipUtils.unZip(new File(path + "/resource.zip"), folder, "GBK")) {
            //处理资源
            //拷贝jar
            FileUtil.文件遍历拷贝(folder + "/libs", projectPath + "/" + MODULE_NAME + "/libs", ".jar");
            //拷贝assets
            FileUtil.文件遍历拷贝(folder + "/assets", projectPath + "/" + MODULE_NAME + "/src/main/assets");
            //拷贝资源
            FileUtil.文件遍历拷贝(folder + "/res", projectPath + "/" + MODULE_NAME + "/src/main/res");
            //拷贝so
            FileUtil.文件遍历拷贝(folder + "/libs", projectPath + "/" + MODULE_NAME + "/src/main/jniLibs", ".so");
        }
        File file = new File(path);
        for (File listFile : file.listFiles()) {
            if (listFile.isFile() && listFile.getName().endsWith(".jar")) {
                FileUtil.copy(listFile.getAbsolutePath(), projectPath + "/" + MODULE_NAME + "/libs/" + listFile.getName());
            }
        }
        String javaFileDirectory = projectPath + "/" + MODULE_NAME + "/src/main/java/com/e4a/runtime/components/impl/android/" + classLibraryName + "类库";
        createADirectory(javaFileDirectory);
        createBUILD(MODULE_NAME);
        createAndroidManifest(MODULE_NAME);
        try {
            String mainActivity = FileUtil.读取文件2(new File(path + "/mainActivity.xml"));
            String Manifest = FileUtil.读取文件2(new File(path + "/Manifest.xml"));
            String Permission = FileUtil.读取文件2(new File(path + "/Permission.xml"));
            String[] 配置 = FileUtil.读取文件2(new File(path + "/工程.ini")).split("\n");
            String sx1 = "";
            String sx2 = "";
            String sx3 = "";
            for (String s : 配置) {
                String str = s.trim();
                if (str.startsWith("自定义属性名称1=")) {
                    sx1 = str.substring("自定义属性名称1=".length());
                }
                if (str.startsWith("自定义属性名称2=")) {
                    sx2 = str.substring("自定义属性名称2=".length());
                }
                if (str.startsWith("自定义属性名称3=")) {
                    sx3 = str.substring("自定义属性名称3=".length());
                }
            }
            FileUtil.写出文件( String.format(e4ainitxml, Permission, Manifest, mainActivity, sx1, sx2, sx3),projectPath + "/" + MODULE_NAME + "/e4aIni.xml", StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        forceDelete(folder);
    }



    public void importELP(File file) throws Exception {
        if (!file.exists() || file.isDirectory() || !(file.getName().toLowerCase().endsWith(".elp"))) {
            throw new Exception("必须是一个有效的ELP文件");
        }
        String folder = projectPath + "/临时解压" + System.currentTimeMillis();

        //创建临时解压目录
        File temporaryPath = new File(folder);
        if (temporaryPath.exists()) {
            temporaryPath.delete();
        }
        temporaryPath.mkdirs();
        if (ZipUtils.unZip(file, folder, "GBK")) {
            String projectStr = FileUtil.readTextFile(folder + "/工程.ini", "GBK");
            String classLibraryName = StringUtils.substring(projectStr, "类库名称=", "\n").trim();
            String classLibraryType = StringUtils.substring(projectStr, "类库类型=", "\n").trim();
            String MODULE_NAME = PinyinUtil.getPinyin(classLibraryName, "");
            addAppBuild(MODULE_NAME);
            addSettings(MODULE_NAME);
            avermentModel(MODULE_NAME);
            initModelDirectory(MODULE_NAME);
            //处理资源
            //拷贝jar
            FileUtil.文件遍历拷贝(folder + "/resource/libs", projectPath + "/" + MODULE_NAME + "/libs", ".jar");
            //拷贝assets
            FileUtil.文件遍历拷贝(folder + "/resource/assets", projectPath + "/" + MODULE_NAME + "/src/main/assets");
            //拷贝资源
            FileUtil.文件遍历拷贝(folder + "/resource/res", projectPath + "/" + MODULE_NAME + "/src/main/res");
            //拷贝so
            FileUtil.文件遍历拷贝(folder + "/resource/libs", projectPath + "/" + MODULE_NAME + "/src/main/jniLibs", ".so");
            copyJavaFile(folder, projectPath + "/" + MODULE_NAME + "/src/main/java", classLibraryName, "可视组件".equals(classLibraryType));
            createBUILD(MODULE_NAME);
            createAndroidManifest(MODULE_NAME);
            String mainActivity = FileUtil.读取文件2(new File(folder + "/mainActivity.xml"));
            String Manifest = FileUtil.读取文件2(new File(folder + "/Manifest.xml"));
            String Permission = FileUtil.读取文件2(new File(folder + "/Permission.xml"));
            String[] 配置 = projectStr.split("\n");
            String sx1 = "";
            String sx2 = "";
            String sx3 = "";
            for (String s : 配置) {
                String str = s.trim();
                if (str.startsWith("自定义属性名称1=")) {
                    sx1 = str.substring("自定义属性名称1=".length());
                }
                if (str.startsWith("自定义属性名称2=")) {
                    sx2 = str.substring("自定义属性名称2=".length());
                }
                if (str.startsWith("自定义属性名称3=")) {
                    sx3 = str.substring("自定义属性名称3=".length());
                }
            }
            FileUtil.写出文件( String.format(e4ainitxml, Permission, Manifest, mainActivity, sx1, sx2, sx3),projectPath + "/" + MODULE_NAME + "/e4aIni.xml", StandardCharsets.UTF_8);
            forceDelete(folder);
        } else {
            throw new Exception("在解压elp时发生未知错误");
        }

    }

    private void createAndroidManifest(String MODULE_NAME) {
        FileUtil.写出文件(String.format(AndroidManifest, MODULE_NAME), projectPath + "/" + MODULE_NAME + "/src/main/AndroidManifest.xml", StandardCharsets.UTF_8);
        FileUtil.写出文件("/build", projectPath + "/" + MODULE_NAME + "/.gitignore", StandardCharsets.UTF_8);
        FileUtil.写出文件("", projectPath + "/" + MODULE_NAME + "/consumer-rules.pro", StandardCharsets.UTF_8);
        FileUtil.写出文件("", projectPath + "/" + MODULE_NAME + "/proguard-rules.pro", StandardCharsets.UTF_8);
    }

    private void addSettings(String moduleName) throws IOException {
        if (settingsElement == null) {
            throw new NullPointerException("未找到:settings.gradle");
        }
        SettingsUtil.addInclude(project, settingsElement, moduleName);

    }

    private void addAppBuild(String moduleName) {
        if (appDependenciesElement == null) {
            throw new NullPointerException("未找到:app/build.gradle");
        }
        BuildUtil.addDependencies(project, appDependenciesElement, "implementation project(':" + moduleName + "')");
    }

    private void createBUILD(String MODULE_NAME) throws IOException {
        createBUILD(MODULE_NAME, null);
    }

    private void createBUILD(String MODULE_NAME, ArrayList<String> libList) throws IOException {
        File file = new File(projectPath + "/" + MODULE_NAME + "/libs");
        StringBuilder stringBuilder = new StringBuilder();
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File file1 : files) {
                    if (file1.getName().endsWith(".jar") && !"SuiyuanUtil-release.jar".equals(file1.getName())) {
                        stringBuilder.append(String.format("\timplementation files('libs/%s')", file1.getName())).append("\n");
                    }
                }
            }
        }
        if (libList != null && libList.size() > 0) {
            for (String s : libList) {
                stringBuilder.append(String.format("\timplementation \"%s\"", s)).append("\n");
            }
        }
        FileUtil.写出文件(String.format(BUILD, stringBuilder), projectPath + "/" + MODULE_NAME + "/build.gradle",
                StandardCharsets.UTF_8);
        FileUtil.copy(ResourceUtils.getResourceAsStream("/static", "e4aIni.xml"), new File(projectPath + "/" + MODULE_NAME + "/e4aIni.xml"));

    }


    /**
     * @param resourcePath 资源路径
     * @param moduleJava   JAVA 路径
     */
    private void copyJavaFile(String resourcePath, String moduleJava, String classLibraryName, boolean is可视类库) throws IOException {
        File mainFile = new File(resourcePath + "/src");
        File[] files = mainFile.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".java")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("GBK")));
                StringBuilder javaBder = new StringBuilder();
                String str;
                String javaDirectory = null;
                while ((str = br.readLine()) != null) {
                    javaBder.append(str).append("\n");
                    if (str.trim().startsWith("package ")) {
                        javaDirectory = str.substring(0, str.indexOf(";"))
                                .replace("package", "")
                                .replace(" ", "").replace(".", "/").trim();
                    }
                }
                if (javaDirectory != null) {
                    File newJavaParcel = new File(moduleJava + "/" + javaDirectory);
                    if (!newJavaParcel.exists()) {
                        newJavaParcel.mkdirs();
                    }
                    File javaFile = new File(newJavaParcel.getAbsolutePath() + "/" + file.getName());
                    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(javaFile), StandardCharsets.UTF_8);
                    osw.write(javaBder.toString());
                    osw.close();
                }
            }
        }
    }


}
