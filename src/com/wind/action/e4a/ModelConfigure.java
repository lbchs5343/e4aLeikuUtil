package com.wind.action.e4a;

import com.intellij.openapi.components.ServiceManager;
import com.wind.action.ReadFactory;
import com.wind.action.e4a.util.AndroidManifestUtil;
import com.wind.action.e4a.util.Log_e4a;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;
import com.wind.action.util.FileUtil;
import com.wind.action.xml.XmlE4Aini;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/9 7:58
 */

public class ModelConfigure extends Configure {
    public boolean state = false;
    public String m_modelName;
    private String packageName;
    public XmlE4Aini xmlE4Aini;
    public String m_jniLibs, m_main, m_libs, m_src, m_res, m_build, m_buildR, m_buildRjava, m_modelDirectory, m_assets, m_java;
    private List<File> javaSourceFile = null;
    private String mainResourceLibs;//这是类库打包依赖路径

    public ModelConfigure(String projectPath, String e4aGen, String modelName, String moduleChineseName, String m_modelName, XmlE4Aini xmlE4Aini, Log_e4a log, String mainResourceLibs) throws Exception {
        super(projectPath, e4aGen, modelName, moduleChineseName, log);
        this.m_modelName = m_modelName;
        this.xmlE4Aini = xmlE4Aini;
        m_src = projectPath + "/" + m_modelName + "/src";
        m_modelDirectory = projectPath + "/" + m_modelName;
        m_main = m_src + "/main";
        m_res = m_main + "/res";
        m_build = projectPath + "/" + m_modelName + "/build/e4a";
        m_buildR = m_build + "/jar";
        m_buildRjava = m_build + "/java";
        m_java = m_main + "/java";
        m_jniLibs = m_main + "/jniLibs";
        m_libs = projectPath + "/" + m_modelName + "/libs";
        m_assets = m_main + "/assets";
        modelGen = m_build + "/gen";
        this.mainResourceLibs = mainResourceLibs;
        log.println(m_src);
        log.println(m_main);
        FileUtil.deleteDirectory(m_build);
        pathValidation();
    }

    private void pathValidation() throws Exception {
        if (!new File(getAndroidManifest()).exists()) {
            throw new NullPointerException(getAndroidManifest() + "文件不存在!");
        }
        createADirectory(m_buildRjava);
        createADirectory(m_buildR);
        createADirectory(modelGen);
        createADirectory(m_buildR + "/class/" + m_modelName);
        createADirectory(m_buildR + "/interim/" + m_modelName);
        parsePackageName();
    }

    private void parsePackageName() throws Exception {
        packageName = AndroidManifestUtil.parsePackageName(new File(getAndroidManifest()));
        log.d("获取module R包名:" + packageName, ColorEnume.浅绿, ColorEnume.白色);
        if (AssertionUtil.isEmpty(packageName)) {
            throw new Exception(getAndroidManifest() + " 数据格式异常!");
        }
        buildRjavaFile = getBuildInterimR() + "/" + packageName.replace(".", "/") + "/R.java";
        buildRjavaAbsolutePath = getBuildInterimR() + "/" + packageName.replace(".", "/");
    }

    @Nullable
    @Override
    public String getJniLibsDirectory() {
        return m_jniLibs;
    }

    @Override
    public XmlE4Aini getXmlE4Aini() {
        return xmlE4Aini;
    }

    @Override
    public String getAndroidManifest() {
        return m_main + "/AndroidManifest.xml";
    }

    @Override
    public String getBuildInterimR() {
        return m_buildR + "/interim/" + m_modelName;
    }

    @Override
    public String getNewRjar() {
        return m_modelName + "_R.jar";
    }

    @Override
    public String getNewModelJar() {
        return m_modelName + ".jar";
    }

    @Override
    public String getBuildRclass() {
        return m_buildR + "/class/" + m_modelName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void copySo() throws IOException {
        fileCopy(new File(m_jniLibs), resourceLibs, ".so", false, true);
    }

    @Override
    public void copyRes() throws IOException {
        fileCopy(new File(m_res), resourceRes, "", true, true);
    }

    @Override
    public void copyAssets() throws IOException {
        fileCopy(new File(m_assets), resourceAssets, "", false, false);
    }

    @Override
    public String getNewValueFileName() {
        return m_modelName + "_";
    }

    @Nullable
    @Override
    public List<File> getJavaSourceFile() {
        if (AssertionUtil.isEmpty(javaSourceFile)) {
            javaSourceFile = FileUtil.iterateOverFiles(m_java, ".java");
        }
        return javaSourceFile;
    }

    @Nullable
    @Override
    public List<File> getCompileDependencies() throws IOException {
        File[] fs = new File(m_libs).listFiles();
        List<File> jarList = new ArrayList<>();
        for (File f : fs) {
            if (f.getName().endsWith(".jar")) {
                addResourceJar(f);
                jarList.add(f);
            }
        }
        File Rjar = new File(resourceLibs + "/" + getNewRjar());
        if (Rjar.exists()) {
            jarList.add(Rjar);
        }
        if (xmlE4Aini.SuiyuanUtil) {
            jarList.add(new File(resourceLibs + "/SuiyuanUtil-release.jar"));
        }
        if (xmlE4Aini.introduceV7) {
            jarList.add(new File(resourceLibs + "/support-annotations-28.0.0.jar"));
        }
        jarList.add(new File(androidJar));
        jarList.add(new File(androidSupportV4Jar));
        jarList.add(new File(e4ARuntimeJar));
        jarList.addAll(xmlE4Aini.jarList);
        //依赖合并
        File[] fileArr = new File(mainResourceLibs).listFiles();
        for (File file : fileArr) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                boolean isExist = false;
                for (File file1 : jarList) {
                    if (file.getName().equals(file1.getName())) {
                        isExist = true;
                    }
                }
                if (!isExist) {
                    jarList.add(file);
                }
            }
        }
        return jarList;
    }
}
