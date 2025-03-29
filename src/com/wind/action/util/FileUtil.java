package com.wind.action.util;


import com.google.common.base.Joiner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/6/29 18:08
 */

public class FileUtil {

    public static void copy(File file1, File file2) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file1));
        copy(is, file2);

    }

    public static void copyXml(File file1, File file2) throws IOException {
        copyXml(file1, file2, null);

    }
    public static void copyXml(File file1, File file2, List<Integer> errRow) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file1), "utf-8");
             BufferedReader bufferedReader = new BufferedReader(isr);
             OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file2), "utf-8")) {
            String str;
            StringBuilder builder = new StringBuilder();
            boolean intercept = false;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains("<!--") && str.contains("-->")) {
                    String process = str.substring(str.indexOf("<!--"), str.indexOf("-->") + 3);
                    builder.append(str.replace(process, "")).append("\n");
                    continue;
                }
                if (str.contains("<!--") && !str.contains("-->")) {
                    builder.append(str, 0, str.indexOf("<!--")).append("\n");
                    intercept = true;
                    continue;
                }
                if (!intercept) {
                    builder.append(str).append("\n");
                } else {
                    if (str.contains("-->")) {
                        intercept = false;
                        String data = str.substring(str.lastIndexOf("-->") + 3);
                        builder.append(data).append("\n");
                    }
                }
            }
            if (AssertionUtil.notEmpty(errRow)) {
                String[] arrStr = builder.toString().split("\n");
                for (Integer integer : errRow) {
                    if(integer>0 && integer<arrStr.length){
                        arrStr[integer-1] = "";
                    }
                }
                builder = new StringBuilder(Joiner.on("\n").join(arrStr));
            }
            isr.close();
            os.write(builder.toString());
            //强制刷出数据
            os.flush();
            //关闭流，先开后关
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void copy(InputStream is, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        OutputStream os = new BufferedOutputStream(outputStream);
        //文件拷贝u，-- 循环+读取+写出
        //缓冲大小
        byte[] b = new byte[2048 * 5];
        //接收长度
        int len = 0;
        //读取文件
        while (-1 != (len = is.read(b))) {
            //读入多少，写出多少，直到读完为止。
            os.write(b, 0, len);
        }
        //强制刷出数据
        os.flush();
        //关闭流，先开后关
        os.close();
        is.close();

    }

    public static InputStream getResource(String filePath) throws IOException {
        return FileUtil.class.getClassLoader().getResourceAsStream(filePath);

    }


    public static void setFile(File path, String str) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);
        out.write(str);
        out.flush();
    }

    public static String getBuildClasspath(File file) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.trim().startsWith("classpath")) {
                    return str.trim();
                }
            }
        }
        return null;
    }

    /**
     * 读取文本文件
     *
     * @param filePath 文件位置
     */
    public static String readTextFile(String filePath, String coding) {
        StringBuilder builder = new StringBuilder("");
        try (FileInputStream out = new FileInputStream(new File(filePath));
             InputStreamReader isr = coding == null ? new InputStreamReader(out) : new InputStreamReader(out, coding)) {

            int ch = 0;
            while ((ch = isr.read()) != -1) {
                builder.append((char) ch);
            }

        } catch (Exception e) {

        }
        return builder.toString();
    }


    public static void writeOut(String filePath, String data, String coding) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath), coding)) {
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeOut(String filePath, String data, Charset 编码) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath), 编码)) {
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void 写出文件(String 数据, String 写到位置) {
        写出文件(数据, 写到位置, Charset.forName("GBK"));

    }

    public static void 写出文件s(String 数据, String 写到位置) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(写到位置))) {
            out.write(数据);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 针对E4A特定编码处理
     */
    public static String adaptiveCoding(String xmlStr) {
        //13 10
        byte[] buffer = xmlStr.getBytes();
        List<Byte> list = new ArrayList<>();
        for (Byte by : buffer) {
            if (by == 10) {
                list.add((byte) 13);
                list.add((byte) 10);
            } else if (by != 13) {
                list.add(by);
            }
        }
        byte[] b = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            b[i] = list.get(i);
        }
        return new String(b);
    }

    public static void 写出文件(String 数据, String 写到位置, Charset 编码) {
        writeOut(写到位置, 数据, 编码);
    }

    public static String 取_文本中间尾倒找(String 数据, String top, String wei) {
        return 数据.substring(top.equals("") ? 0 : 数据.lastIndexOf(top) + top.length(), 数据.lastIndexOf(wei));
    }

    public static String 取_文本中间(String 数据, String top, String wei) {
        return 数据.substring(数据.lastIndexOf(top) + top.length(), 数据.indexOf(wei));
    }

    public static String 取_文本中间正常(String 数据, String top, String wei) {
        return 数据.substring(数据.indexOf(top) + top.length(), 数据.indexOf(wei, 数据.indexOf(top) + top.length()));
    }

    public static String 取_文本中间正常含头尾(String 数据, String top, String wei) {
        return 数据.substring(数据.indexOf(top), 数据.indexOf(wei, 数据.indexOf(top) + top.length()) + wei.length());
    }

    public static String 取_文本中间包含头尾(String 数据, String top, String wei) {
        try {
            return 数据.substring(数据.lastIndexOf(top), 数据.indexOf(wei) + wei.length());
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String 取_文本中间含头(String 数据, String top, String wei) {
        return 数据.substring(数据.lastIndexOf(top), 数据.indexOf(wei));
    }

    public static void 文件遍历拷贝(String 被扫描的目录, String 写出主目录) {
        文件遍历拷贝(被扫描的目录, 写出主目录, null);
    }

    public static void 文件遍历拷贝(String 被扫描的目录, String 写出主目录, String 指点后缀) {
        File file = new File(被扫描的目录);
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; ++i) {
                try {
                    if (!listFiles[i].isDirectory()) {
                        String wjName = listFiles[i].getName();
                        if (wjName.contains("e4astyle")) {
                            continue;
                        }
                        //跳过重复节点
                        if (wjName.startsWith("strings.")) {
                            continue;
                        }
                        //跳过重复节点
                        if (wjName.startsWith("styles.")) {
                            continue;
                        }
                        if (wjName.startsWith("color.")) {
                            continue;
                        }
                        if (指点后缀 != null) {
                            if (wjName.endsWith(指点后缀)) {
                                复制文件(listFiles[i], 写出主目录);
                            }
                        } else {
                            复制文件(listFiles[i], 写出主目录);
                        }
                    } else {
                        //递归一下
                        文件遍历拷贝(listFiles[i].getAbsolutePath(), 写出主目录, 指点后缀);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void 取libs公共依赖(String 被扫描的目录) {
        File file = new File(被扫描的目录);
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; ++i) {
                try {
                    if (!listFiles[i].isDirectory()) {
                        String wjName = listFiles[i].getName();
                        if (wjName.contains("e4astyle")) {
                            continue;
                        }
                        //跳过重复节点
                        if (wjName.startsWith("strings.")) {
                            continue;
                        }
                        //跳过重复节点
                        if (wjName.startsWith("styles.")) {
                            continue;
                        }
                        if (wjName.startsWith("color.")) {
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }


    }

    private static void 复制文件(File file, String 写出主目录) {
        if (!file.getAbsolutePath().endsWith(".aar")) {
            if (写出主目录 != null) {
                写出主目录 = 写出主目录.replace("/", "\\");
                String 目录识别 = 取_文本中间尾倒找(file.getAbsolutePath(), "", "\\" + file.getName());
                目录识别 = 目录识别.substring(目录识别.lastIndexOf("\\"));
                try {
                    if (!写出主目录.endsWith(目录识别)) {
                        mkdir(写出主目录 + 目录识别);
                    } else {
                        目录识别 = "";
                    }
                    if (!new File(写出主目录 + 目录识别 + "\\" + file.getName()).exists()) {
                        copy(file.getAbsolutePath(), 写出主目录 + 目录识别 + "\\" + file.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 利用字节流复制
     *
     * @param yuanPath 原路径
     * @param dest
     * @throws IOException
     */
    public static void copy(String yuanPath, String dest) throws IOException {
        if (yuanPath.endsWith(".xml")) {
            try (FileInputStream inputStream = new FileInputStream(yuanPath);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String str = null;
                int n = 0;
                StringBuilder builder = new StringBuilder("");
                boolean intercept = false;
                while ((str = bufferedReader.readLine()) != null) {
                    n++;
                    if (str.contains("<!--") && str.contains("-->")) {
                        String process = str.substring(str.indexOf("<!--"), str.indexOf("-->") + 3);
                        builder.append(str.replace(process, "\n")).append("\n");
                        continue;
                    }
                    if (str.contains("<!--") && !str.contains("-->")) {
                        builder.append(str.substring(0, str.indexOf("<!--"))).append("\n");
                        intercept = true;
                        continue;
                    }
                    if (!intercept) {
                        builder.append(str).append("\r\n");
                    } else {
                        if (str.contains("-->")) {
                            intercept = false;
                            String data = str.substring(str.lastIndexOf("-->") + 3);
                            builder.append(data).append("\r\n");
                        }
                    }
                }
                OutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
                os.write(builder.toString().getBytes());
                //强制刷出数据
                os.flush();
                //关闭流，先开后关
                os.close();
                return;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        InputStream is = new BufferedInputStream(new FileInputStream(yuanPath));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
        //文件拷贝u，-- 循环+读取+写出
        //缓冲大小
        byte[] b = new byte[2048 * 5];
        //接收长度
        int len = 0;
        //读取文件
        while (-1 != (len = is.read(b))) {
            //读入多少，写出多少，直到读完为止。
            os.write(b, 0, len);
        }
        //强制刷出数据
        os.flush();
        //关闭流，先开后关
        os.close();
        is.close();
    }


    /**
     * 利用字节流复制
     *
     * @param yuanPath 原路径
     * @param dest
     * @throws IOException
     */
    public static void copys(String yuanPath, String dest) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(yuanPath));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
        //文件拷贝u，-- 循环+读取+写出
        //缓冲大小
        byte[] b = new byte[2048 * 5];
        //接收长度
        int len = 0;
        //读取文件
        while (-1 != (len = is.read(b))) {
            //读入多少，写出多少，直到读完为止。
            os.write(b, 0, len);
        }
        //强制刷出数据
        os.flush();
        //关闭流，先开后关
        os.close();
        is.close();
    }


    /**
     * 删除单个文件
     *
     * @param fileName：要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
//        System.out.println("==>>清理垃圾文件:"+fileName.replace("\\","/"));
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    public synchronized static void deleteDirectory(String directory) {
        File file = new File(directory);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                if (listFile.isDirectory()) {
                    deleteDirectory(listFile.getAbsolutePath());
                }
                listFile.delete();
            }
        }
    }

    public synchronized static void deleteDirectoryAll(String directory) {
        File file = new File(directory);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                if (listFile.isDirectory()) {
                    deleteDirectory(listFile.getAbsolutePath());
                }
                listFile.delete();
            }
            file.delete();
        }
    }

    /**
     * 注意:此方法适用于文本流
     *
     * @param yuanPath    欲拷贝的文件路径
     * @param dest        新的文件位置
     * @param charsetNmae 编码
     */
    public static void copy(String yuanPath, String dest, String charsetNmae) throws IOException {
        if (!yuanPath.endsWith(".xml")) {
            return;
        }
        InputStream is = new BufferedInputStream(new FileInputStream(yuanPath));
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dest), charsetNmae);
        //文件拷贝u，-- 循环+读取+写出
        //缓冲大小
        byte[] b = new byte[2048 * 5];
        //接收长度
        int len = 0;
        //读取文件
        while (-1 != (len = is.read(b))) {
            //读入多少，写出多少，直到读完为止。
            String xml = new String(Arrays.copyOfRange(b, 0, len));
            do {
                try {
                    xml = xml.replace(FileUtil.取_文本中间正常含头尾(xml, "<!--", "-->"), "");
                } catch (Exception e) {
                    break;
                }
            } while (xml.contains("<!--"));
            out.write(xml);
        }
        //强制刷出数据
        out.flush();
        //关闭流，先开后关
        out.close();
        is.close();
    }


    /**
     * 创建文件夹
     */
    public static void mkdir(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
            //System.out.println("创建目录" + path);
        }
    }


    /**
     * @param filePath 文件位置
     */
    public static String 读文本文件(String filePath, String coding) {
        StringBuilder builder = new StringBuilder("");
        try (FileInputStream out = new FileInputStream(filePath);
             InputStreamReader isr = coding == null ? new InputStreamReader(out) : new InputStreamReader(out, coding)) {

            int ch = 0;
            while ((ch = isr.read()) != -1) {
                builder.append((char) ch);
            }

        } catch (Exception e) {

        }
        return builder.toString();
    }

    public static String getFile(FileInputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        StringBuilder sj = new StringBuilder();
        while ((str = bufferedReader.readLine()) != null) {
            sj.append(str).append("\r\n");
        }
        inputStream.close();
        bufferedReader.close();
        return sj.toString();
    }

    /**
     * @param filePath 文件位置
     */
    public static String 读文本文件(String filePath) {
        return 读文本文件(filePath, null);
    }

    public static String 读取文件(File file) {
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
            String str = null;
            int n = 0;
            StringBuilder builder = new StringBuilder("");
            while ((str = bufferedReader.readLine()) != null) {
                builder.append(str).append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String 读取文件2(File file) {
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
            String str;
            StringBuilder builder = new StringBuilder("");
            while ((str = bufferedReader.readLine()) != null) {
                builder.append(str).append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void 递归拷贝目录(String 被扫描的目录, String 拷贝到位置) {
        File file = new File(被扫描的目录);
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; ++i) {
                try {
                    if (!listFiles[i].isDirectory()) {
                        String wjName = listFiles[i].getName();
                        copys(listFiles[i].getAbsolutePath(), 获取新文件路径(listFiles[i], 拷贝到位置));
                    } else {
                        //递归一下
                        递归拷贝目录(listFiles[i].getAbsolutePath(), 获取新目录(listFiles[i], 拷贝到位置));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static String 获取新文件路径(File file, String 拷贝到位置) {
        File file1 = new File(拷贝到位置);
        if (file1.exists()) {
            return 拷贝到位置 + "/" + file.getName();
        } else {
            file1.mkdir();
        }
        return 获取新目录(file, 拷贝到位置) + "/" + file.getName();
    }

    private static String 获取新目录(File file, String 拷贝到位置) {
        String 文件夹名 = "";
        if (!file.isDirectory()) {
            String 路径 = file.getAbsolutePath().replace("\\", "/");
            路径 = 路径.substring(0, 路径.lastIndexOf("/"));
            文件夹名 = 路径.substring(路径.lastIndexOf("/"));
        } else {
            文件夹名 = file.getName();
        }
        new File(拷贝到位置 + "/" + 文件夹名).mkdir();
        return 拷贝到位置 + "/" + 文件夹名;
    }

    /**
     * 遍历目录指定类型文件
     */
    public static List<File> iterateOverFiles(String directory, String suffix) {
        List<File> fileList = new ArrayList<>();
        try {
            for (File file : new File(directory).listFiles()) {
                if (file.isDirectory()) {
                    fileList.addAll(iterateOverFiles(file.getAbsolutePath(), suffix));
                } else if (file.getName().endsWith(suffix)) {
                    fileList.add(file);
                }
            }
        } catch (Exception e) {

        }
        return fileList;
    }
}

