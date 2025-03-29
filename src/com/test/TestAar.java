package com.test;

import com.wind.action.util.FileUtil;
import com.wind.action.util.ZipUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/10/21 23:55
 */

public class TestAar {

    public static void main(String[] args) {
        new File("D:/123").mkdirs();
//        ZipUtils.unZip(new File("D:/dialog-3.2.5.aar"),"D:/123","UTF8");
//        com.intellij.execution.impl.DefaultJavaProgramRunner
        ZipUtils.unZip(new File("D:/BLE蓝牙类库源码.elp"), "D:/123", "GBK");
        String xml=FileUtil.读取文件(new File("E:\\E4A_Demo\\suiyuan_V7hengxiangsxTV\\build\\e4a\\appcompat-v7-28.0.0\\res\\layout\\abc_dialog_title_material.xml"));

        String regexPattern = "<!-[\\s\\S]*?->";
        Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(xml);

        xml = matcher.replaceAll("");

        System.out.println(xml);







    }

}
