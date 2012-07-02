package com.rallydev.client.json;

import org.testng.annotations.Test;

import java.util.Map;

import static com.rallydev.client.MapUtils.toMap;
import static com.rallydev.client.json.JsonSerializer.toJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Test
public class JsonSerializerTest {

    public void shouldSerializeASingleObjectCorrectly() {
        Map map = toMap("abc", 123);
        
        String json = toJson(map);
        
        assertThat(json, is("{\"abc\":123}"));
    }

    public void shouldSerializeAVarArgObjectsCorrectly() {
        String json = toJson("abc", 123);

        assertThat(json, is("{\"abc\":123}"));
    }
}
