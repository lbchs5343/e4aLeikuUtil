package com.wind.action.e4a.util;


import com.google.common.base.Joiner;
import com.wind.action.e4a.AarConfigure;
import com.wind.action.e4a.Configure;
import com.wind.action.e4a.ModelConfigure;
import com.wind.action.logicLayer.JavaE4AText;
import com.wind.action.util.AssertionUtil;
import com.wind.action.util.ColorEnume;
import sun.tools.jar.Main;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * 创建日期： 2021/6/19
 *
 * @author 随缘_QQ:874334395
 * @version 1.0
 * @since JDK 1.8.0_79
 * <p>
 * 类说明：   $
 */
public class CommandUtil {
    private Configure configure;
    private ModelConfigure mC;
    //保存进程标准输入流信息
    private List<String> stdotList = new ArrayList<>();
    //保存进程标准错误流信息
    private List<String> errorList = new ArrayList<>();
    private Log_e4a log;
    public CommandUtil(Configure configure,Log_e4a log) {
        this.configure = configure;
        this.log=log;
    }

    /**
     * 将classPath路径下的所有文件，打成jar包。
     * jar包的路径是${binJarPath}\${binJarName}.jar
     * <p>
     * 主要还是里面工具包里面的jar命令部分的代码
     *
     * @param binJarPath 生成jar包的地址
     * @param binJarName 生成jar包的名字
     * @param classPath  所有的.class文件所在的路径
     */
    public void jarcvf(String binJarPath, String binJarName, String classPath) throws Exception {
        binJarName = binJarName.replace(" ", "");
        if (new File(classPath).exists()) {

            System.out.println(binJarPath + binJarName);
            PrintStream printStream;
            if (configure instanceof ModelConfigure) {
                printStream = new PrintStream(new FileOutputStream(configure.projectPath + "/" + mC.modelName + "/" + mC.modelName + ".txt"));
            } else {
                printStream = new PrintStream(new FileOutputStream(configure.projectPath + "/" + configure.modelName + "/" + configure.modelName + ".txt"));
            }
            sun.tools.jar.Main main = new Main(printStream, System.err, "jar");
            String[] args = new String[5];
            args[0] = "-cf";
            args[1] = binJarPath + binJarName;
            args[2] = "-C";
            args[3] = classPath;
            args[4] = ".";
            main.run(args);
        } else {
            throw new Exception("jar编译失败文件不存在");

        }
    }

    public void jarcvfR() throws Exception {
        jarcvf(configure.resourceLibs + "/", configure.getNewRjar(), configure.getBuildRclass());
    }

    public void jarcvfModel() throws Exception {
        jarcvf(configure.elbDirectory + "/" + configure.moduleChineseName + "/", configure.getNewModelJar(), configure.modelGen);
    }

    public void jarcvfNewModel() throws Exception {
        jarcvf(configure.resourceLibs + "/", configure.getNewModelJar(), configure.modelGen);
    }


    public void executeCommand(String command, String[] envp, File file) {
        stdotList.clear();
        errorList.clear();
        Process p = null;
        try {
            if (envp == null && file == null) {
                p = Runtime.getRuntime().exec(command);
            } else {
                p = Runtime.getRuntime().exec(command, envp, file);
            }
            //创建两个线程  分别读取输入流缓冲区和错误流缓冲区
            ThreadUtil stdotThread = new ThreadUtil(stdotList, p.getInputStream());
            ThreadUtil errorThread = new ThreadUtil(errorList, p.getErrorStream());
            stdotThread.start();
            errorThread.start();
            p.waitFor();
            //一直挂起,直到子进程执行结束
            //返回值0表示正常退出
        } catch (IOException e) {
            log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public List<String> getStdotList() {
        return stdotList;
    }

    public String getError() {
        StringBuilder builder = new StringBuilder("");
        for (String str : errorList) {
            builder.append(str).append("\r\n");
        }
        return builder.toString();
    }

    public String getStdot() {
        StringBuilder builder = new StringBuilder("");
        for (String str : stdotList) {
            builder.append(str).append("\r\n");
        }
        return builder.toString();
    }

    class ThreadUtil implements Runnable {
        //属于单字节编码，最多能表示的字符范围是0-255，应用于英文系列。
        private String character1 = "ISO-8859-1";
        //汉子的国标码，专门用来表示汉字，是双字节编码，而英文字母和iso8859-1一致（兼容iso8859-1编码）。
        // 其中gbk编码能够用来同时表示繁体字和简体字，而gb2312只能表示简体字，gbk是兼容gb2312编码的。
        private String character2 = "GB2312";
        //最统一的编码，可以用来表示所有语言的字符，而且是定长双字节（也有四字节的）编码，包括英文字母在内。
        private String character3 = "unicode";
        //相比于unicode会使用更少的空间，但如果已经知道是汉字 推荐GB2312
        private String character4 = "utf-8";
        private List<String> list;
        private InputStream inputStream;
        private MyRunnable ok;

        public ThreadUtil(List<String> list, InputStream inputStream) {
            this.list = list;
            this.inputStream = inputStream;
        }

        public ThreadUtil(List<String> list, InputStream inputStream, MyRunnable ok) {
            this.list = list;
            this.inputStream = inputStream;
            this.ok = ok;
        }

        public void start() {
            Thread thread = new Thread(this);
            thread.setDaemon(true); //设置为守护线程
            //定义：守护线程--也称“服务线程”，在没有用户线程可服务时会自动离开。优先级：守护线程的优先级比较低，用于为系统中的其它对象和线程提供服务。
            thread.start();
        }

        @Override
        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream, character2));
                String line = null;
                while (null != (line = br.readLine())) {
                    list.add(line);
                    if (!line.trim().startsWith("已解压:") && !line.trim().startsWith("已创建:")) {
                        configure.log.d(line);
                    }
                }
                if (ok != null) {
                    ok.ok();
                }
            } catch (IOException e) {
                if (ok != null) {
                    ok.err();
                }
                e.printStackTrace();
                log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
            } finally {
                try {
                    inputStream.close();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createRjava() throws Exception {
        if(configure instanceof AarConfigure){
            AarConfigure aarConfigure= (AarConfigure) configure;
            if(AssertionUtil.notEmpty(aarConfigure.getRJAVA())){
                executeCommand(configure.javaSdkBin + "/javac -encoding UTF-8 " + aarConfigure.getRJAVA() + " -d " + configure.getBuildRclass(), null, null);
                jarcvfR();
            }
        }else{
            createRjava(configure.res);
        }

    }



    public void createRjava(String res) throws Exception {
        if (new File(res).exists()) {
            String android = configure.androidJar;
            String cmd = configure.aapt + "/aapt p -f -m -J " + configure.getBuildInterimR() + "/ -S " + res + " -I " + android + " -M " + configure.getAndroidManifest();
            log.println(cmd);
            executeCommand(cmd, null, null);
            System.gc();
            if (new File(configure.buildRjavaFile).exists()) {
                executeCommand(configure.javaSdkBin + "/javac -encoding UTF-8 " + configure.buildRjavaFile, null, null);
                //解析R.Java
                transitionJava();
            }
        }
    }

    public void createModuleRjava() throws Exception {
        mC = (ModelConfigure) configure;
        if (new File(mC.m_res).exists()) {
            String android = configure.androidJar;
            String cmd = configure.aapt + "/aapt p -f -m -J " + configure.getBuildInterimR() + "/ -S " + mC.m_res + " -I " + android + " -M " + configure.getAndroidManifest();
            executeCommand(cmd, null, null);
            System.gc();
            Thread.sleep(2000);
            if (new File(configure.buildRjavaFile).exists()) {
                executeCommand(configure.javaSdkBin + "/javac -encoding UTF-8 " + configure.buildRjavaFile, null, null);
                //解析R.Java
                transitionJava();
            }
        }
    }

    private void transitionJava() {
        ClassLoaderExpand clod = new ClassLoaderExpand();
        //指定java class 文件目录
        clod.setLocation(configure.buildRjavaAbsolutePath);
        Class cl = clod.findClass(configure.getPackageName() + ".R");
        StringBuilder stringBuilder = new StringBuilder();
        for (Class declaredClass : cl.getDeclaredClasses()) {
            String name = declaredClass.getName();
            String className = name.substring(name.lastIndexOf("$") + 1);
            stringBuilder.append("    ").append(String.format("public static final class %s{\n", className));
            for (Field field : declaredClass.getFields()) {
                if (!"styleable".equals(className)) {
                    stringBuilder.append("    ").append("    ").append(String.format(
                            "public static final int %s=getRsId(\"%s\",\"%s\");\n",
                            field.getName(),
                            className,
                            field.getName()));

                } else {
                    if ("int".equals(field.getType().getName())) {
                        stringBuilder.append("    ").append("    ").append(String.format(
                                "public static final int %s=getStyleableIntArrayIndex(\"%s\");\n",
                                field.getName(),
                                field.getName()));

                    } else {
                        stringBuilder.append("    ").append("    ").append(String.format(
                                "public static final int[] %s=getStyleableIntArray(\"%s\");\n",
                                field.getName(),
                                field.getName()));

                    }
                }
            }
            stringBuilder.append("    ").append("}\n");
        }
        if (!"".equals(stringBuilder.toString())) {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("package %s;\n", configure.getPackageName()))
                    .append(JavaE4AText.R_TEMPLET)
                    .append(stringBuilder)
                    .append("}");
            try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(configure.buildRjavaFile))) {
                out.write(builder.toString());
                out.close();
                executeCommand(configure.javaSdkBin + "/javac -encoding UTF-8 " + configure.buildRjavaFile + " -d " + configure.getBuildRclass(), null, null);
                jarcvfR();
            } catch (Exception e) {
                log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
            }


        }

    }

    public void compileJava(List<File> fileList, MyRunnable ok) throws FileNotFoundException {
        //源文件不存在,不需要编译
        if (AssertionUtil.isEmpty(configure.getJavaSourceFile())) {
            if (ok != null) {
                ok.mNull();
            }
            return;
        }
        log.d("",ColorEnume.黄色,ColorEnume.黄色);
        log.d("编译JAVA文件",ColorEnume.浅绿,ColorEnume.黄色);
        StringBuilder classpath = new StringBuilder();
        for (File file : fileList) {
            classpath.append(file.getAbsolutePath()).append(";");
        }
        StringBuilder javaSourceFilePath = new StringBuilder();
        for (File file : configure.getJavaSourceFile()) {
            javaSourceFilePath.append(file.getAbsolutePath()).append(" ");
        }
        javaSourceFilePath.deleteCharAt(javaSourceFilePath.toString().length() - 1);
        String[] cmdCode = new String[]{
                configure.javaSdkBin + "/javac",//javaSDK
                "-nowarn",//忽略警告
                "-encoding", "utf-8",//指定编码
                "-classpath", classpath.toString(),//jar依赖位置
                javaSourceFilePath.toString(),//需要编译的源文件位置
                "-d", configure.modelGen//编译后Class存放位置
        };
        log.d(classpath.toString().replace(";","<br/>"),ColorEnume.浅绿,ColorEnume.黄色);
        executeCommand(Joiner.on(" ").join(cmdCode).replace("\\", "/"), ok);
    }

    public void executeCommand(String command, MyRunnable ok) {
        stdotList.clear();
        errorList.clear();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command, null, null);
            //创建两个线程  分别读取输入流缓冲区和错误流缓冲区
            ThreadUtil stdotThread = new ThreadUtil(stdotList, p.getInputStream());
            ThreadUtil errorThread = new ThreadUtil(errorList, p.getErrorStream(), ok);
            stdotThread.start();
            errorThread.start();
            p.waitFor();
            //一直挂起,直到子进程执行结束
            //返回值0表示正常退出
        } catch (Exception e) {
            log.d(e.getMessage(), ColorEnume.红色, ColorEnume.白色);
            if (ok != null) {
                ok.mNull();
            }
            e.printStackTrace();
        }
    }


}
