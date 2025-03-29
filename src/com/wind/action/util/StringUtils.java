package com.wind.action.util;

public class StringUtils {

    public static String substring(String content, String prefix) {
        return substring(content, prefix, null);
    }

    public static String substring(String content, String prefix, String suffix) {
        int length = prefix.length();
        int start = content.indexOf(prefix);
        if (suffix == null) {
            return content.substring(start + length);
        }
        return content.substring(start + length, content.indexOf(suffix, start + length));
    }
}
