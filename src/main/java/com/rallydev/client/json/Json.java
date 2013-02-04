package com.rallydev.client.json;

import com.rallydev.client.http.Resource;

import java.io.Serializable;
import java.util.Map;

public class Json implements Serializable {
    private final Map jsonMap;
    private static final long serialVersionUID = -3823306229163397878L;

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
