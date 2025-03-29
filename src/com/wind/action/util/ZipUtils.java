package com.wind.action.util;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.zip.ZipFile;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final int BUFFER_SIZE = 2 * 1024;
    private static String FILENAME = "";
    public final static String encoding = "GBK";

    /**
     * 压缩文件和文件夹
     *
     * @param srcPathname 需要被压缩的文件或文件夹路径
     * @param zipFilepath 将要生成的zip文件路径
     * @throws BuildException
     * @throws RuntimeException
     */
    public static void zip(String srcPathname, String zipFilepath) throws BuildException, RuntimeException {
        File file = new File(srcPathname);
        if (!file.exists()) {
            throw new RuntimeException("source file or directory " + srcPathname + " does not exist.");
        }

        Project proj = new Project();
        FileSet fileSet = new FileSet();
        fileSet.setProject(proj);
        // 判断是目录还是文件
        if (file.isDirectory()) {
            fileSet.setDir(file);
            // ant中include/exclude规则在此都可以使用
            // 比如:
            // fileSet.setExcludes("**/*.txt");
            fileSet.setIncludes("**/");
            fileSet.setIncludes("**.*");
            fileSet.setIncludes("**/.*");
            fileSet.setIncludes("**/**/*.*");
            fileSet.setIncludes("**/**/**/*.*");
        } else {
            fileSet.setFile(file);
        }
//        if (zipFilepath.endsWith(".elb")) {
//            fileSet.setExcludes("/gen/");
//            fileSet.setExcludes("/src/");
//        }
        Zip zip = new Zip();
        zip.setProject(proj);
        zip.setDestFile(new File(zipFilepath));

        zip.addFileset(fileSet);
        zip.setEncoding(encoding);
        zip.execute();
    }


    /**
     * 调用org.apache.tools.zip实现解压缩，支持目录嵌套和中文名
     * 也可以使用java.util.zip不过如果是中文的话，解压缩的时候文件名字会是乱码。原因是解压缩软件的编码格式跟java.util.zip.ZipInputStream的编码字符集(固定是UTF-8)不同
     *
     * @param zipFileName     要解压缩的文件
     * @param outputDirectory 要解压到的目录
     * @throws Exception
     */
    public static boolean unZip(String zipFileName, String outputDirectory) {
        return unZip(new File(zipFileName), outputDirectory, null);
    }

    public static boolean unZip(File file, File outputDirectory) {
        return unZip(file, outputDirectory.getAbsolutePath(), null);
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
            createDirectory(outputDirectory, "");
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
                    }
                } else {
                    if (zipEntry.getName().endsWith("annotations.zip") || zipEntry.getName().startsWith("aidl/")) {
                        continue;
                    }
                    File f = new File(outputDirectory + File.separator
                            + zipEntry.getName());
                    if (!f.getName().endsWith("package-info.java")) {
                        if (!f.getParentFile().exists()) {
                            f.getParentFile().mkdirs();
                        }
                        f.createNewFile();
                        InputStream in = zipFile.getInputStream(zipEntry);
                        FileOutputStream out = new FileOutputStream(f);
                        if (f.getName().endsWith(".xml")) {

                            Writer writer = prettyPrintByTransformer(in, 3, true);
                            if (writer == null) {
                                write(out, XmlUtil.deleteNote(zipFile.getInputStream(zipEntry)));
                            } else {
                                write(out, XmlUtil.deleteNote(writer));
                            }
                        } else {
                            write(out, in);
                        }
                    }
                }
                flag = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flag;
    }

    /**
     * 格式化xml
     *
     * @param indent            向前缩进多少空格
     * @param ignoreDeclaration 是否忽略描述
     * @return 格式化后的xml
     */
    public static Writer prettyPrintByTransformer(InputStream inputStream, int indent, boolean ignoreDeclaration) {

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out;
        } catch (ParserConfigurationException | IOException | TransformerException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void writeXml(FileOutputStream out, InputStream in) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
        byte[] by = new byte[1024];
        int c;
        while ((c = in.read(by)) != -1) {
            System.out.println(new String(by, StandardCharsets.UTF_8));
            outputStreamWriter.write(new String(by));
        }
        out.close();

        in.close();
    }

    private static void write(FileOutputStream out, InputStream in) throws IOException {
        byte[] by = new byte[1024];
        int c;
        while ((c = in.read(by)) != -1) {
            out.write(by, 0, c);
        }
        out.close();
        in.close();
    }

    private static void write(FileOutputStream out, Writer in) throws IOException {
        out.write(in.toString().getBytes());
        out.close();
        in.close();
    }

    private static void write(FileOutputStream out, String xml) throws IOException {
        out.write(xml.getBytes());
        out.close();
    }

    /**
     * 创建目录
     *
     * @param directory    父目录
     * @param subDirectory 子目录
     */
    private static void createDirectory(String directory, String subDirectory) {
        String dir[];
        File fl = new File(directory);
        try {
            if (subDirectory == "" && fl.exists() != true) {
                fl.mkdir();
            } else if (subDirectory != "") {
                dir = subDirectory.replace('\\', '/').split("/");
                for (int i = 0; i < dir.length; i++) {
                    File subFile = new File(directory + File.separator + dir[i]);
                    if (!subFile.exists()) {
                        subFile.mkdir();
                    }
                    directory += File.separator + dir[i];
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void jar合并(File 保存到文件, List<File> jarList) throws IOException {
        List<String> nameList = new ArrayList<>();
        //合并到的路径
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(保存到文件));
        for (int i = jarList.size() - 1; i >= 0; i--) {
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarList.get(i));
            System.out.println(jarList.get(i).getName());
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
        System.out.println("jar合并成功");
    }
}