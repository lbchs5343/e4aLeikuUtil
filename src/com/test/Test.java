package com.test;

import com.wind.action.util.FileUtil;
import com.wind.lib.util.DependencyUtil;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/3 19:49
 */

public class Test {

    static String lib = "appcompat-v7-28.0.0";

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//        DependencyUtil.init2(,null);


//        查询依赖
//        search(lib);
//        search("mlkit-camera-core:1.0.3");
//        search("support-compat:28.0.0");

        //E4A原始V4包
        File androidSupportV4 = new File("D:/abc/android-support-v4.jar");
        //写出位置
        File afterTheMagicReform = new File("C:/E4A/JDK6/jre/lib/ext/android-support-v4.jar");
        List<File> classList = new ArrayList<>();
        //插件自带的V7注解
        classList.add(new File("D:/abc/support-annotations-28.0.0.jar"));
        List<File> jarList = new ArrayList<>();

        for (File file : new File("C:/Users/admin/Desktop/mg").listFiles()) {
            jarList.add(file);
        }
        ruleOutClass(jarList, classList, androidSupportV4, afterTheMagicReform);
        setClipboardString(afterTheMagicReform);
        System.out.println("+++++");
    }

    private static void search(String str) throws IOException {

        List<File> fileList = DependencyUtil.inquiryDependency(str, false);
        for (File item : fileList) {
            DependencyUtil.getKeyClass(item).forEach(key -> {
                System.out.println(key);
            });
        }
    }

    public static List<ZipEntry> getOutClass(File jarFile) throws IOException {
        List<ZipEntry> zipEntryList = new ArrayList<>();
        //合并到的路径
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarFile);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            zipEntryList.add(entry);
        }
        return zipEntryList;

    }

    /**
     * @param jarList 需要合并的jar
     * @param saveToFile 合并后的jar
     * @param classFileList 需要排除的CLASS
     * @param classFile 需要魔改的jar文件
     */
    public static void ruleOutClass(List<File> jarList, List<File> classFileList, File classFile, File saveToFile) throws IOException {
        List<ZipEntry> zipEntryList = new ArrayList<>();
        //合并到的路径
        java.util.zip.ZipFile zipFileMain = new java.util.zip.ZipFile(classFile);
        Enumeration<? extends ZipEntry> enumerationxxx = zipFileMain.entries();
        while (enumerationxxx.hasMoreElements()) {
            ZipEntry entry = enumerationxxx.nextElement();
            zipEntryList.add(entry);
        }
        List<String> repeated = new ArrayList<>();
        classFileList.forEach(file -> {
            try {
                for (ZipEntry outClass : getOutClass(file)) {
                    repeated.add(outClass.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //合并到的路径
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(saveToFile));

        jarList.forEach(file -> {
            try {
                ZipFile zipFile = getJarZipFile(file);
                Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                while (enumeration.hasMoreElements()) {
                    ZipEntry entry = enumeration.nextElement();
                    if (repeated.contains(entry.getName())) {
                        continue;
                    }
                    for (ZipEntry zipEntry : zipEntryList) {
                        if (zipEntry.getName().equals(entry.getName())) {
                            zipEntryList.remove(zipEntry);
                            break;
                        }
                    }
                    repeated.add(entry.getName());
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

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        for (ZipEntry zipEntry : zipEntryList) {
            zos.putNextEntry(zipEntry);
            InputStream is = zipFileMain.getInputStream(zipEntry);
            int count;
            byte[] buffer = new byte[10240];
            while ((count = is.read(buffer)) != -1) {
                zos.write(buffer, 0, count);
            }
            is.close();
            zos.closeEntry();
        }
        zos.close();
    }

    /**
     * 获取CLASS.jar
     */
    private static ZipFile getJarZipFile(File file) throws IOException {

        if (file.getName().endsWith(".aar")) {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith("classes.jar")) {
                    FileUtil.copy(zipFile.getInputStream(entry), new File("D:/CS.jar"));
                    zipFile.close();
                    return new ZipFile(new File("D:/CS.jar"));
                }
            }
        } else if (file.getName().endsWith(".jar")) {
            return new ZipFile(file);
        }
        return null;
    }

    public static void setClipboardString(Object object) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = null;
        if (object instanceof String) {
            trans = new StringSelection((String) object);
        } else if (object instanceof File) {
            trans = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.javaFileListFlavor.equals(flavor);
                }

                @NotNull
                @Override
                public Object getTransferData(DataFlavor flavor) {
                    List<File> l = new ArrayList<File>();
                    l.add((File) object);
                    return l;
                }
            };
        }
        if (trans != null) {
            clipboard.setContents(trans, null);
        }
    }

}

