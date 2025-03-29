package com.wind.action.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：zhuYi
 * @date ：Created in 2022/10/22 13:10
 */

public class XmlUtil {
    private static final String REGEX_PATTERN = "<!-[\\s\\S]*?->";



    public static String deleteNote(Writer in){
        try {
            return deleteNote(in.toString());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String deleteNote(InputStream in){
        return deleteNote(getStringByInputStream(in));
    }
    public static String deleteNote(String xml){
        Pattern pattern = Pattern.compile(REGEX_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(xml);
        return matcher.replaceAll("");
    }

    public static String getStringByInputStream(InputStream inputStream){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[10240];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
        } catch (Exception e) {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e1) {
            }
        }
        return outputStream.toString();
    }


}
