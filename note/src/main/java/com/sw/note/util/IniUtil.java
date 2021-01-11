package com.sw.note.util;

import java.util.Map;

public class IniUtil {

    public static String calc(Map<String, Double> map) {
        if (map.keySet().size() == 1) {
            Map.Entry<String, Double> entry = map.entrySet().iterator().next();
            return String.format("%.2f%s", entry.getValue(), entry.getKey());
        }
        return map.keySet().stream().reduce((k1, k2) ->
                String.format("%.2f%s", map.get(k1), k1) + " " + String.format("%.2f%s", map.get(k2), k2)).orElse("0");
    }
}
