package com.rallydev.client;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MapUtils {
    public static Map<Object, Object> toMap(Object... map) {
        if(map.length % 2 == 1) throw new IllegalArgumentException("Must pass an even number of arguments");

        Map<Object, Object> results = new LinkedHashMap<Object, Object>();
        for(int i = 0; i<map.length; i+=2) {
            results.put(map[i], map[i + 1]);
        }
        return results;
    }
}
