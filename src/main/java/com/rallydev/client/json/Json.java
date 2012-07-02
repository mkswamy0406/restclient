package com.rallydev.client.json;

import com.rallydev.client.http.Resource;

import java.util.Map;

public class Json {
    private final Map jsonMap;

    public Json(Map jsonMap) {
        this.jsonMap = jsonMap;
    }

    public <T> T get(Object... keys) {
        Map current = jsonMap;

        int lastKeyIndex = keys.length - 1;
        for(int i=0; i< lastKeyIndex; i++) {
            current = (Map)current.get(keys[i]);
        }
        
        return (T) current.get(keys[lastKeyIndex]);
    }

    public Resource getAsResource(Object... keys) {
        String resource = get(keys);
        return Resource.parse(resource);
    }
}
