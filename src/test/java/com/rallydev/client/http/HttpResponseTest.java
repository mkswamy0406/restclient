package com.rallydev.client.http;

import org.testng.annotations.Test;

import java.util.Map;

import static com.rallydev.client.MapUtils.toMap;
import static com.rallydev.client.json.JsonSerializer.toJson;
import static org.apache.http.HttpStatus.SC_BAD_GATEWAY;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.fail;

@Test
public class HttpResponseTest {
    public void shouldParseBodyWhenRequested() {
        Map map = toMap("_ref", "/foo/1234.js");
        HttpResponse response = new HttpResponse(SC_OK, toJson(map));

        String ref = response.getBodyAsJson().get("_ref");
        assertThat(ref, is("/foo/1234.js"));
    }

    public void shouldThrowExceptionWhenBodyIsNotWellFormedJson() {
        HttpResponse response = new HttpResponse(SC_OK, "{");

        try {
            response.getBodyAsJson();
            fail("Should throw exception when body contains malformed json");
        } catch(Exception e) {
            // Expect this to happen.
        }
    }
    
    public void shouldSerializeBodyAsJson() {
        HttpResponse response = new HttpResponse(SC_OK, "_ref", "/foo/1234.js");

        assertThat(response.getBody(), is(toJson(toMap("_ref", "/foo/1234.js"))));
    }
    
    public void shouldBeAbleBeAbleToTellIfTheStatusIsOk() {
        HttpResponse response = new HttpResponse(SC_OK, "");
        
        assertThat(response.isOk(), is(true));
        assertThat(response.isNotOk(), is(false));
    }

    public void shouldBeAbleToTellIfTheStatusIsNotOk() {
        HttpResponse response = new HttpResponse(SC_CONFLICT, "");

        assertThat(response.isOk(), is(false));
        assertThat(response.isNotOk(), is(true));
    }
    
    public void shouldBeAbleToQueryTheStatusOfTheResponse() {
        HttpResponse response = new HttpResponse(SC_CONFLICT, "");

        assertThat(response.is(SC_CONFLICT), is(true));
        assertThat(response.isNot(SC_CONFLICT), is(false));
        assertThat(response.is(SC_BAD_GATEWAY), is(false));
        assertThat(response.isNot(SC_BAD_GATEWAY), is(true));
    }
}