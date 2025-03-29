package com.wind.action.e4a;

import com.intellij.openapi.project.Project;
import com.wind.action.e4a.Configure;
import com.wind.action.e4a.util.AndroidManifestUtil;
import com.wind.action.e4a.util.Log_e4a;
import com.wind.action.logicLayer.JavaE4AText;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;
import com.wind.action.util.FileUtil;
import com.wind.action.util.ZipUtils;

import com.wind.action.xml.XmlE4Aini;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/6 21:24
 */

public class AarConfigure extends Configure {

    public String aarName;
    public String packageName;
    public String decompressDirectory;
    private File aar;
    private String jniLibs;
    private String libs;
    private String assets;
    private String newRjar;
    private String RJAVA;
    private XmlE4Aini xmlE4Aini;


    public AarConfigure(String projectPath, String e4aGen, String modelName, String moduleChineseName, File aar, String newSign, XmlE4Aini xmlE4Aini, Log_e4a log) throws Exception {
        super(projectPath, e4aGen, modelName, moduleChineseName, log);
        this.aarName = aar.getName().substring(0, aar.getName().lastIndexOf("."));
        this.aar = aar;
        this.xmlE4Aini = xmlE4Aini;
        decompressDirectory = build + "/" + aarName + newSign;
        newRjar = aarName + "_R.jar";
        FileUtil.deleteDirectory(decompressDirectory);
        pathValidation();
        initLizeDependencies();
    }


    public void initLizeDependencies() throws IOException {
        File file = new File(libs);
        File[] files = file.listFiles();
        if (AssertionUtil.notEmpty(files)) {
            for (File listFile : files) {
                if (listFile.isFile() && listFile.getName().endsWith(".jar")) {
                    log.d(aarName + ".aar jar->>>>>>>:" + listFile.getName(), ColorEnume.灰色, ColorEnume.白色);
                    addResourceJar(listFile);
                }
            }
        }
        log.d(aarName + ".aar jar->>>>>>>:" + getNewModelJar(), ColorEnume.灰色, ColorEnume.白色);

    }

    public void addClass() throws IOException {
        addResourceJar(new File(decompressDirectory + "/classes.jar"));
    }

    public File getClassJar() throws IOException {
        return new File(decompressDirectory + "/classes.jar");
    }

    @Override
    public String getNewRjar() {
        return newRjar;
    }

    public void setNewRjar(String newRjar) {
        this.newRjar = newRjar;
    }

    @Override
    public String getNewModelJar() {
        return aarName + ".jar";
    }

    @Override
    public String getPackageName() {
        return packageName;
    }


    @Override
    public String getBuildInterimR() {
        return buildR + "/interim/" + aarName;
    }

    @Override
    public String getBuildRclass() {
        return buildR + "/class/" + aarName;
    }

    @Override
    public String getAndroidManifest() {
        return decompressDirectory + "/AndroidManifest.xml";
    }

    public void pathValidation() throws Exception {
        createADirectory(decompressDirectory);
        createADirectory(getBuildRclass());
        createADirectory(getBuildInterimR());
        createADirectory(resourceDirectory);
        createADirectory(resourceLibs);
        createADirectory(resourceRes);
        createADirectory(resourceAssets);
        ZipUtils.unZip(aar, decompressDirectory, null);

        res = decompressDirectory + "/res";
        parsePackageName();
        //定义资源所在目录
        jniLibs = decompressDirectory + "/jni";
        libs = decompressDirectory + "/libs";
        assets = decompressDirectory + "/assets";
        if (new File(decompressDirectory + "/R.txt").exists()) {
            createRJAVA();
        }
    }

    private void createRJAVA() {
        String txt = FileUtil.读取文件(new File(decompressDirectory + "/R.txt"));
        if (txt != null && txt.length() > 10) {
            String[] node = txt.split("\n");
            Map<String, List<String>> rData = new HashMap<>();
            for (String row : node) {
                if (AssertionUtil.notEmpty(row.trim())) {
                    String[] data = row.split(" ");
                    List<String> origin = rData.get(data[1]);
                    if (origin == null) {
                        origin = new ArrayList<>();
                        rData.put(data[1], origin);
                    }
                    String order;
                    if (data[0].contains("[]") && data[1].contains("styleable")) {
                        order = String.format("public static final int[] %s=getStyleableIntArray(\"%s\");\n", data[2].trim(), data[2].trim());
                    } else if ("int".equals(data[0]) && data[1].contains("styleable")) {
                        order = String.format("public static final int %s=getStyleableIntArrayIndex(\"%s\");\n", data[2].trim(), data[2].trim());
                    } else {
                        order = String.format("public static final int %s=getRsId(\"%s\",\"%s\");\n", data[2].trim(), data[1].trim(), data[2].trim());
                    }
                    origin.add(order);
                }

            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("package %s;\n", packageName));
            stringBuilder.append(JavaE4AText.R_TEMPLET);
            rData.forEach((key, list) -> {
                String className = String.format("    public static final class %s{\n", key);
                stringBuilder.append(className);
                for (String s : list) {
                    stringBuilder.append("    ").append("    ").append(s);
                }
                stringBuilder.append("    }\n");
            });
            stringBuilder.append("}");
            RJAVA = decompressDirectory + "/R.java";
            FileUtil.写出文件(stringBuilder.toString(), RJAVA);
        }

    }

    public String getRJAVA() {
        return RJAVA;
    }

    private void parsePackageName() throws Exception {
        packageName = AndroidManifestUtil.parsePackageName(new File(getAndroidManifest()));
        log.d("获取 " + aarName + ".aar R包名:" + packageName, ColorEnume.浅绿, ColorEnume.白色);
        if (AssertionUtil.isEmpty(packageName)) {
            throw new Exception(getAndroidManifest() + " 数据格式异常!");
        }
        buildRjavaFile = getBuildInterimR() + "/" + packageName.replace(".", "/") + "/R.java";
        buildRjavaAbsolutePath = getBuildInterimR() + "/" + packageName.replace(".", "/");
    }

    @Override
    public void copySo() throws IOException {
        fileCopy(new File(jniLibs), resourceLibs, ".so", false, true);
    }

    @Override
    public void copyRes() throws IOException {
        fileCopy(new File(res), resourceRes, "", true, true);
    }

    @Override
    public XmlE4Aini getXmlE4Aini() {
        return xmlE4Aini;
    }

    @Override
    public void copyAssets() throws IOException {
        fileCopy(new File(assets), resourceAssets, "", false, false);
    }

    @Override
    public String getNewValueFileName() {
        return aarName.replace(".", "_").replace("-", "").replace("+", "");
    }

    @Nullable
    @Override
    public List<File> getJavaSourceFile() {
        return null;
    }

    @Nullable
    @Override
    public List<File> getCompileDependencies() {
        return null;
    }

    @Nullable
    @Override
    public String getJniLibsDirectory() {
        return jniLibs;
    }
}
