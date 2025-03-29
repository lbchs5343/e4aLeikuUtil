package com.wind.action.e4a.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/8/6 23:50
 */
public class ClassLoaderExpand extends ClassLoader {


    @Override
    public Class<?> findClass(String name) {
        byte[] datas = loadClassData(name);
        return defineClass(name, datas, 0, datas.length);
    }

    // 指定文件目录
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location.endsWith("/")) {
            this.location = location;
        } else {
            this.location = location + "/";
        }
    }


    protected byte[] loadClassData(String name) {
        FileInputStream fis = null;
        byte[] datas = null;
        try {
            if (name.split("\\.").length > 0) {
                fis = new FileInputStream(location + name.substring(name.lastIndexOf(".")+1) + ".class");
            }else{
                fis = new FileInputStream(location + name + ".class");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = fis.read()) != -1) {
                bos.write(b);
            }
            datas = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return datas;

    }
}