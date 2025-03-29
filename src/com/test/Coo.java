package com.test;

import com.wind.action.util.XmlUtil;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Writer;
import java.util.Enumeration;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/11/9 2:29
 */

public class Coo {


    public static void main(String[] args) {
        File file = new File("C:\\E4A");
        mains(file);








    }
    public static void mains(File file) {
        if(file.isDirectory()) {

            for (File listFile : file.listFiles()) {
                if(listFile.isDirectory()){
                    mains(listFile);
                }else {
                    if (listFile.getName().endsWith(".jar")) {
                        unZip(listFile, null, "UTF8");
                    }
                }
            }
        }

    }

    /**
     * 调用org.apache.tools.zip实现解压缩，支持目录嵌套和中文名
     * 也可以使用java.util.zip不过如果是中文的话，解压缩的时候文件名字会是乱码。原因是解压缩软件的编码格式跟java.util.zip.ZipInputStream的编码字符集(固定是UTF-8)不同
     *
     * @param file            要解压缩的文件
     * @param outputDirectory 要解压到的目录
     * @throws Exception
     */
    public static boolean unZip(File file, String outputDirectory, String encoding) {
        boolean flag = false;
        try {
            ZipFile zipFile = encoding == null ? new ZipFile(file) : new ZipFile(file, encoding);

            Enumeration e = zipFile.getEntries();
            org.apache.tools.zip.ZipEntry zipEntry = null;
            while (e.hasMoreElements()) {
                zipEntry = (org.apache.tools.zip.ZipEntry) e.nextElement();
                //System.out.println("unziping " + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!zipEntry.getName().startsWith("aidl/")) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        File f = new File(outputDirectory + File.separator + name);
                        if (!f.exists()) {
                            f.mkdir();
                        }


                            System.out.println(name);


                    }

                } else {
                    if (zipEntry.getName().endsWith("annotations.zip") || zipEntry.getName().startsWith("aidl/")) {
                        continue;
                    }


                }
                flag = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flag;
    }



}
