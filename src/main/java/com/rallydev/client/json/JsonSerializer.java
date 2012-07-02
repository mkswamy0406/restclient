package com.rallydev.client.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;

import static com.rallydev.client.MapUtils.toMap;

public class JsonSerializer {
    private static final TypeReference<Map> MAP_TYPE = new TypeReference<Map>() {};
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String toJson(Object... map) {
        return toJson(toMap(map));
    }

    public static Map deserializeMap(String map) {
        return read(map, MAP_TYPE);
    }
    
    public static Json deserializeJson(String map) {
        return new Json(deserializeMap(map));
    }

    private static <T> T read(String s, TypeReference<T> type) {
        try {
            return mapper.readValue(s, type);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
