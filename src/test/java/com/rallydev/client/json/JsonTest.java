package com.rallydev.client.json;

import com.rallydev.client.http.Resource;
import org.testng.annotations.Test;

import static com.rallydev.client.MapUtils.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Test
public class JsonTest {
    
    public void shouldAllowGetAccessWithoutCasting() {
        Json json = new Json(toMap("abc", 123, "gfh", "def"));
        
        Integer abc = json.get("abc");
        String gfh = json.get("gfh");

        assertThat(abc, is(123));
        assertThat(gfh, is("def"));
    }
    
    public void shouldGetSingleNestedValues() {
        Json json = new Json(toMap("abc", 123, "gfh", toMap("_ref", "abc123")));
        
        String ref = json.get("gfh", "_ref");
        
        assertThat(ref, is("abc123"));
    }

    public void shouldGetMultipleNestedValues() {
        Json json = new Json(toMap("abc", 123, "gfh", toMap("_ref", toMap("whocares", "abc123"))));

        String ref = json.get("gfh", "_ref", "whocares");

        assertThat(ref, is("abc123"));
    }
    
    public void shouldGetResourceForKey() {
        Resource resource = new Resource("user", "abc123");
        Json json = new Json(toMap("abc", 123, "gfh", resource.asString()));
        
        Resource found = json.getAsResource("gfh");
        
        assertThat(found, is(resource));        
    }
    
    public void shouldGetNestedResourceForKeys() {
        Resource resource = new Resource("user", "abc123");
        Json json = new Json(toMap("abc", 123, "gfh", toMap("_ref", resource.asString())));
        
        Resource found = json.getAsResource("gfh", "_ref");
        
        assertThat(found, is(resource));                
    }
    
}
