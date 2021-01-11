package com.sw.vote.util;

public class StringUtils extends org.apache.commons.lang.StringUtils {
    public static String getString(Object obj, String def) {
        if (obj == null || obj.toString().trim().length() == 0) {
            return def;
        }
        return obj.toString();
    }
}
