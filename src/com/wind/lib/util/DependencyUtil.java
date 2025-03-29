package com.wind.lib.util;

import com.google.common.base.Joiner;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.FileUtil;
import com.wind.lib.Dependency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/9/3 19:54
 */

public class DependencyUtil {
    final static String userName;
    public static List<String> excludeList = new ArrayList<>();

    static {
        Map<String, String> map = System.getenv();
        userName = map.get("USERNAME");//获取用户名
    }

    public static Map<String, List<Dependency>> dependencyMap = new HashMap<>();
    static Map<String, List<File>> repeatDependency = new HashMap<>();

    static Map<String, List<String>> artifactIdDependency = new HashMap<>();
    static Map<String, List<File>> aarDependency = new HashMap<>();
    static String path = "C:/Users/${userName}/.gradle/caches/modules-2/files-2.1";

    public static void init() {
//        excludeList.add("annotation-1.0.0");
        init(path);
    }

    public static void init2(String path, Runnable runnable) {
        init(path);
        new Thread() {
            @Override
            public void run() {
                dependencyMap.forEach((key, list) -> {
                    list.forEach(dependency -> {
                        try {
                            if (dependency.isValid() && !dependency.getMavenCoordinate().contains("ideaIC-")) {
                                dependency.setClassList(getKeyClass(dependency.getJarAar()));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                if (runnable != null) {
                    runnable.run();
                }
            }
        }.start();
    }

    public static String getUserName() {
        return userName;
    }

    public static void init(String path) {
        dependencyMap.clear();
        repeatDependency.clear();
        artifactIdDependency.clear();
        aarDependency.clear();
        String absolutePath = path.replace(" ", "").replace("${userName}", userName);
        traverseDirectory(new File(absolutePath));
    }


    private static void traverseDirectory(File fileDirectory) {
        if (validDirectory(fileDirectory) && !fileDirectory.getName().equals("e4a")
                && !fileDirectory.getName().equals("maven")
                && !fileDirectory.getName().equals("plugins")
        ) {
            File[] fileArr = fileDirectory.listFiles();
            if (AssertionUtil.notEmpty(fileArr)) {
                for (File file : fileArr) {
                    if (validDirectory(file)) {
                        //递归目录
                        traverseDirectory(file);
                    } else if (file.isFile()) {
                        String name = file.getName();
                        //排除不需要的文件
                        if (!name.contains("-sources.") && !name.contains("-javadoc.")
                                && (name.endsWith(".jar")
                                || name.endsWith(".pom")
                                || name.endsWith(".aar"))
                        ) {
                            String key = name.substring(0, name.lastIndexOf("."));
                            if (name.endsWith(".jar")) {
                                List<File> jarList = repeatDependency.get(name);
                                if (AssertionUtil.isEmpty(jarList)) {
                                    jarList = new MyArrayList<>();
                                    repeatDependency.put(name, jarList);
                                }
                                if (!jarList.contains(file)) {
                                    jarList.add(file);
                                }
                            } else if (name.endsWith(".aar")) {
                                List<File> aarList = aarDependency.get(name);
                                if (AssertionUtil.isEmpty(aarList)) {
                                    aarList = new MyArrayList<>();
                                    aarDependency.put(name, aarList);
                                }
                                if (!aarList.contains(file)) {
                                    aarList.add(file);
                                }
                            }
                            List<Dependency> dependencyList = dependencyMap.get(key);
                            if (AssertionUtil.isEmpty(dependencyList)) {
                                dependencyList = new ArrayList<>();
                                Dependency dependency = new Dependency(file);
                                dependencyList.add(dependency);
                                dependencyMap.put(key, dependencyList);
                                List<String> artifactIdList = artifactIdDependency.get(dependency.getArtifactId());
                                if (AssertionUtil.isEmpty(artifactIdList)) {
                                    artifactIdList = new ArrayList<>();
                                    artifactIdDependency.put(dependency.getArtifactId(), artifactIdList);
                                }
                                if (!artifactIdList.contains(dependency.getExclude())) {
                                    artifactIdList.add(dependency.getExclude());
                                }
                            } else {
                                boolean exist = false;
                                for (Dependency dependency : dependencyList) {
                                    if (dependency.equals(file)) {
                                        dependency.setFile(file);
                                        exist = true;
                                        break;
                                    }
                                }
                                if (!exist) {
                                    Dependency dependency = new Dependency(file);
                                    dependencyList.add(dependency);
                                    List<String> artifactIdList = artifactIdDependency.get(dependency.getArtifactId());
                                    if (AssertionUtil.isEmpty(artifactIdList)) {
                                        artifactIdList = new ArrayList<>();
                                        artifactIdDependency.put(dependency.getArtifactId(), artifactIdList);
                                    }
                                    if (!artifactIdList.contains(dependency.getExclude())) {
                                        artifactIdList.add(dependency.getExclude());
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    /**
     * @param key      recyclerview-v7-28.0.0 || recyclerview-v7:28.0.0
     * @param rigorous 如果子依赖不全则会抛异常
     */
    public static List<File> inquiryDependency(String key, boolean rigorous) throws NullPointerException, IOException {
        final List<File> fileList = new ArrayList<>();
        key = key.replace(":", "-");
        inquiryDependency(key, fileList, rigorous);
        return fileList;
    }


    /**
     * <dependency>
     * <groupId>com.android.support</groupId>
     * <artifactId>support-annotations</artifactId>
     * <version>28.0.0</version>
     * <scope>compile</scope>
     * </dependency>
     */
    public static List<File> inquiryDependency(String artifactId, String version, boolean rigorous) throws NullPointerException, IOException {
        final List<File> fileList = new MyArrayList<>();
        inquiryDependency(artifactId + "-" + version, fileList, rigorous);
        return fileList;
    }

    public static void inquiryDependency(String key, List<File> fileList, boolean rigorous) throws NullPointerException, IOException {
        List<Dependency> dependencyList = dependencyMap.get(key);
        if (dependencyList != null) {
            for (Dependency dependency : dependencyList) {
                if (dependency != null) {
                    List<Dependency.Configure> configureList = dependency.getConfigureList();
                    if (AssertionUtil.notEmpty(configureList)) {
                        main:
                        for (Dependency.Configure configure : configureList) {
                            if (!configure.getScope().equals("test") && !configure.getType().equals("pom") && configure.getVersion() != null
                                    && !configure.getVersion().contains("]")
                                    && !configure.getVersion().contains("}")) {
                                for (String exclude : excludeList) {
                                    if (configure.getKey().equals(exclude)) {
                                        continue main;
                                    }
                                }
                                if (rigorous && dependencyMap.get(configure.getKey()) == null) {
                                    List<String> artifactIdList = artifactIdDependency.get(configure.getArtifactId());
                                    String related = artifactIdList == null ? "" : "\n____________________________________\n本地已存在的相关版本:\n" + Joiner.on("\n").join(artifactIdList).replace(configure.getExclude(),"") + "\n____________________________________";
                                    throw new NullPointerException("找不到依赖: 解决方案:在你的项目中引入一次  " + configure.getMavenCoordinate() +
                                            "\n 又或者在e4aIni.xml dependencys节点下添加exclude节点排除此依赖 " + configure.getExclude() + related);
                                } else if (dependencyMap.get(configure.getKey()) != null) {
                                    inquiryDependency(configure.getKey(), fileList, rigorous);
                                }
                            }
                        }
                    }
                    if (dependency.isValid() && !fileList.contains(dependency.getJarAar())) {
                        File file = dependency.getJarAar();
                        String name = file.getName();
                        if (name.endsWith(".jar")) {
                            if (!file.getParentFile().getName().equals("e4a")) {
                                List<File> jarList = repeatDependency.get(name);
                                if (AssertionUtil.notEmpty(jarList)) {
                                    if (jarList.size() == 1) {
                                        fileList.add(dependency.getJarAar());
                                    } else {
                                        //重复jar包名处理方案
                                        String rootFileDirectory = file.getParent().replace("\\", "/");
                                        File e4aFileDirectory = new File(rootFileDirectory + "/e4a");
                                        if (!e4aFileDirectory.exists()) {
                                            e4aFileDirectory.mkdirs();
                                        }
                                        File jarFile = new File(rootFileDirectory + "/e4a/" + file.getName());
                                        jarCoalesce(jarFile, jarList);
                                        fileList.add(jarFile);
                                        dependency.setJar(jarFile);
                                    }
                                }
                            }
                        } else {
                            List<File> aarList = aarDependency.get(name);
                            fileList.addAll(aarList);
                        }
                    } else if (!dependency.isValid() && rigorous) {
                        for (String exclude : excludeList) {
                            if (dependency.getKey().equals(exclude)) {
                                return;
                            }
                        }
                        List<String> artifactIdList = artifactIdDependency.get(dependency.getArtifactId());
                        String related = artifactIdList == null ? "" : "\n____________________________________\n本地已存在的相关版本:\n" + Joiner.on("\n").join(artifactIdList).replace(dependency.getExclude(),"") + "\n____________________________________";
                        //依赖未下载完整
                        throw new NullPointerException("找不到依赖: 解决方案:在你的项目中引入一次  " + dependency.getMavenCoordinate() +
                                "\n 又或者在e4aIni.xml dependencys节点下添加exclude节点排除此依赖 " + dependency.getExclude() + related);
                    }
                }


            }

        }


    }

    /**
     * 获取jar中所有class
     */
    public static List<String> getJarClass(File jarPath) throws IOException {
        List<String> stringList = new ArrayList<>();
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarPath);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            System.out.println(entry.getName());
            stringList.add(entry.getName());
        }
        zipFile.close();
        return stringList;
    }

    /**
     * @param saveToFile 新的文件
     * @param jarFile    欲修改的jar文件
     * @param classList  需要排除的Class
     */
    public static void ruleOutClass(File jarFile, File saveToFile, List<String> classList) throws IOException {
        if (AssertionUtil.notEmpty(classList)) {
            //合并到的路径
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(saveToFile));
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarFile);
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                System.out.println(entry.getName());
                if (classList.contains(entry.getName())) {
                    continue;
                }
                zos.putNextEntry(entry);
                InputStream is = zipFile.getInputStream(entry);
                int count;
                byte[] buffer = new byte[10240];
                while ((count = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, count);
                }
                is.close();
                zos.closeEntry();
            }
            zos.close();
        } else {
            FileUtil.copy(jarFile, saveToFile);
        }
    }

    /**
     * @param saveToFile 新的文件
     * @param jarList    欲合并的jar文件
     * @param classList  需要排除的Class
     */
    public static void ruleOutClass(List<File> jarList, File saveToFile, List<String> classList) throws IOException {
        if (AssertionUtil.notEmpty(classList)) {
            List<String> nameList = new ArrayList<>();
            //合并到的路径
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(saveToFile));
            for (int i = jarList.size() - 1; i >= 0; i--) {
                java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarList.get(i));
                Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                while (enumeration.hasMoreElements()) {
                    ZipEntry entry = enumeration.nextElement();
                    if (nameList.contains(entry.getName()) || classList.contains(entry.getName())) {
                        continue;
                    }
                    nameList.add(entry.getName());
                    zos.putNextEntry(entry);
                    InputStream is = zipFile.getInputStream(entry);
                    int count;
                    byte[] buffer = new byte[10240];
                    while ((count = is.read(buffer)) != -1) {
                        zos.write(buffer, 0, count);
                    }
                    is.close();
                    zos.closeEntry();
                }
            }
            zos.close();
        } else {
            jarCoalesce(saveToFile, jarList);
        }
    }

    public static List<String> getKeyClass(File jarAarFile) throws IOException {
        List<String> list = new ArrayList<>();
        if (jarAarFile != null && jarAarFile.exists()) {
            if (jarAarFile.getName().endsWith(".aar")) {
                try (ZipFile zipFile = new java.util.zip.ZipFile(jarAarFile);
                     InputStream inputStream = getClassJarInputStream(zipFile)) {
                    ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {

//                        System.out.println(entry.getName());
                        if (entry.getName().endsWith(".class")) {
                            list.add(entry.getName().replace("/", "."));
                        }
                    }
                } catch (NullPointerException n) {
                    n.printStackTrace();
                }
            } else if (jarAarFile.getName().endsWith(".jar")) {
                list.addAll(getClassList(jarAarFile));
            }
        }
        return list;
    }


    /**
     * 获取CLASS.jar
     */
    private static List<String> getClassList(File jarFile) throws IOException {
        List<String> list = new ArrayList<>();
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarFile)) {
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    list.add(entry.getName().replace("/", "."));
                }
            }
        }
        return list;
    }


    /**
     * 获取CLASS.jar
     */
    private static InputStream getClassJarInputStream(ZipFile zipFile) throws IOException {
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            if (entry.getName().endsWith("classes.jar")) {
                return zipFile.getInputStream(entry);
            }
        }

        return null;
    }


    /**
     * @param saveToFile 新的文件
     * @param jarList    欲合并的jar包
     */
    public static void jarCoalesce(File saveToFile, List<File> jarList) throws IOException {
        List<String> nameList = new ArrayList<>();
        //合并到的路径
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(saveToFile));
        for (int i = jarList.size() - 1; i >= 0; i--) {
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarList.get(i));
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                if (nameList.contains(entry.getName())) {
                    continue;
                }
                nameList.add(entry.getName());
                zos.putNextEntry(entry);
                InputStream is = zipFile.getInputStream(entry);
                int count;
                byte[] buffer = new byte[10240];
                while ((count = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, count);
                }
                is.close();
                zos.closeEntry();
            }
        }
        zos.close();
    }


    private static boolean validDirectory(File fileDirectory) {
        return fileDirectory.exists() && fileDirectory.isDirectory();
    }

}
