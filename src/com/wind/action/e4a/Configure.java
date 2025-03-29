package com.wind.action.e4a;

import com.intellij.openapi.project.Project;
import com.wind.action.ReadFactory;
import com.wind.action.e4a.util.Log_e4a;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.FileUtil;
import com.wind.action.util.PinyinUtil;
import com.wind.action.xml.XmlE4Aini;
import com.wind.lib.util.DependencyUtil;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/7 2:27
 */

public abstract class Configure {
    public static List<String> classList;
    public static Map<String, File> fileMap = new HashMap<>();
    public String e4aGen;
    public String modelName;
    public String res;
    public String main;
    public String java;
    public String javaSdkBin;
    public String src;
    public String projectPath;
    public String build;
    public String buildR;
    public String modelGen;
    public String moduleChineseName;
    public String buildRjavaFile;
    public String buildRjavaAbsolutePath;
    public String androidJar;
    public String e4ARuntimeJar;
    public String elbDirectory;
    public String androidSupportV4Jar;
    public String aapt;
    public String resourceDirectory;
    public String resourceLibs;
    public String resourceRes;
    public String resourceAssets;
    public String modelDirectory;
    private File e4astyle;

    public @Nullable
    Project project;
    public @NonNull
    Log_e4a log;




    public Configure(@NonNull Log_e4a log,Project project) {
        this.project=project;
        this.log = log;

    }

    public Configure(String projectPath, String e4aGen, String modelName, String moduleChineseName, @NonNull Log_e4a log) throws Exception {
        this.log = log;
        this.modelName = modelName;
        this.e4aGen = e4aGen;
        this.projectPath = projectPath;
        this.moduleChineseName = moduleChineseName;
        initPath();
        pathValidation();

    }

    public void initConfigure(String projectPath, String e4aGen, String modelName, String moduleChineseName) throws Exception {
        this.modelName = modelName;
        this.e4aGen = e4aGen;
        this.projectPath = projectPath;
        this.moduleChineseName = moduleChineseName;
        initPath();
        pathValidation();
    }

    public void initPath() {
        androidJar = e4aGen + "/sdk/platforms/android-2.2/android.jar";
        aapt = e4aGen + "/sdk/tools/lib";
        e4ARuntimeJar = e4aGen + "/JDK6/jre/lib/ext/E4ARuntime.jar";
        androidSupportV4Jar = e4aGen + "/JDK6/jre/lib/ext/android-support-v4.jar";
        src = projectPath + "/" + modelName + "/src";
        modelDirectory = projectPath + "/" + modelName;
        main = src + "/main";
        res = main + "/res";
        build = projectPath + "/" + modelName + "/build/e4a";
        elbDirectory = build + "/elb";
        buildR = build + "/R";
        modelGen = build + "/" + modelName;
        java = main + "/java";
        javaSdkBin = e4aGen + "/JDK6/bin";
        resourceDirectory = build + "/resource";
        resourceLibs = build + "/resource/libs";
        resourceRes = build + "/resource/res";
        resourceAssets = build + "/resource/assets";

    }

    public @Nullable
    abstract String getJniLibsDirectory();


    protected void createADirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    public abstract XmlE4Aini getXmlE4Aini();
    public abstract String getAndroidManifest();

    private void pathValidation() throws Exception {
        if (!new File(e4aGen).exists()) {
            throw new Exception("E4A路径错误!");
        }
        if (!new File(aapt + "/aapt.exe").exists()) {
            throw new Exception("此E4A缺少aapt.exe!");
        }
        if (!new File(androidJar).exists()) {
            throw new Exception(androidJar + "文件不存在");
        }
        if (!new File(javaSdkBin).exists()) {
            throw new Exception(javaSdkBin + "SDK不存在");
        }
        if (!new File(e4ARuntimeJar).exists()) {
            throw new Exception(e4ARuntimeJar + "文件不存在");
        }
        if (!new File(androidSupportV4Jar).exists()) {
            throw new Exception(androidSupportV4Jar + "文件不存在");
        }

    }


    public abstract String getBuildInterimR();

    public abstract String getNewRjar();

    public abstract String getNewModelJar();

    public abstract String getBuildRclass();

    public abstract String getPackageName();

    public void addResourceJar(File jar) throws IOException {
        if (jar.exists() && jar.getName().endsWith(".jar") && !"E4ARuntime.jar".equals(jar.getName())) {
            String newJar = resourceLibs + "/" + jar.getName();
            if ("classes.jar".equals(jar.getName()) && getNewModelJar() != null) {
                newJar = resourceLibs + "/" + getNewModelJar();
            }
            DependencyUtil.ruleOutClass(jar, new File(newJar), classList);
//            DependencyUtil.ruleOutClass(jar, new File(newJar), null);
        }
    }

    public abstract void copySo() throws IOException;

    public abstract void copyRes() throws IOException;

    public abstract void copyAssets() throws IOException;

    public void fileCopy(File directory, String writeOutDirectory, String suffix, boolean valuesDiscern, boolean whetherToCreateADirectory) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (AssertionUtil.notEmpty(files)) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(suffix)) {
                        File parent = whetherToCreateADirectory
                                ? new File(writeOutDirectory + "/" + file.getParentFile().getName())
                                : new File(writeOutDirectory);
                        if (!parent.exists() && whetherToCreateADirectory) {
                            parent.mkdirs();
                        }
                        if (valuesDiscern && file.getParentFile().getName().startsWith("values")) {
                            copy(file, new File(parent.getAbsolutePath() + "/" + ("e4astyle.xml".equals(file.getName()) ? "" : getNewValueFileName()) + file.getName()));
                        } else {
                            copy(file, new File(parent.getAbsolutePath() + "/" + file.getName()));
                        }
                    } else {
                        fileCopy(file, writeOutDirectory, suffix, valuesDiscern, whetherToCreateADirectory);
                    }
                }
            }
        }
    }


    private void copy(File file1, File file2) throws IOException {
        if ("e4astyle.xml".equals(file1.getName())) {
            e4astyle = file2;
        }
        if (file1.getName().endsWith(".xml")) {
            fileMap.put(file2.getName(), file1);
            XmlE4Aini xmlE4Aini=getXmlE4Aini();
            List<String> errList=xmlE4Aini.errList;
            if(AssertionUtil.notEmpty(errList)){
                List<Integer> indexList=new ArrayList<>();
                for (String err : errList) {
                    if(err.contains(":")){
                        String name=err.substring(0,err.indexOf(":"));
                        if(file2.getName().equals(name)){
                            indexList.add(Integer.valueOf(err.substring(err.indexOf(":")+1)));
                        }

                    }
                }
                FileUtil.copyXml(file1, file2,indexList);

            }else{
                FileUtil.copyXml(file1, file2);
            }

//            supportcompat28_0_0values.xml:6





        } else {
            FileUtil.copy(file1, file2);
        }
    }

    /**
     * 删除冲突文件
     */
    public void deleteConflict() {
        if (e4astyle != null) {
            e4astyle.delete();
        }
    }


    public abstract String getNewValueFileName();

    public static String getModuleChineseName(String projectPath, String modelName) {
        File file = new File(projectPath + "/" + modelName + "/src/main/java/com/e4a/runtime/components/impl/android");
        if (file.exists() && file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if (listFile.isDirectory() && listFile.getName().endsWith("类库")) {
                    return listFile.getName();
                }
            }
        }
        return null;
    }

    /**
     * java源文件数据
     */
    public @Nullable
    abstract List<File> getJavaSourceFile();

    /**
     * 编译所需要的依赖
     */
    public @Nullable
    abstract List<File> getCompileDependencies() throws IOException;


}
