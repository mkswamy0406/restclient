package com.rallydev.client.http;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.testng.annotations.Test;

import java.util.Map;

import static com.rallydev.client.MapUtils.toMap;
import static com.rallydev.client.json.JsonSerializer.toJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Test
public class HttpRequestTest {
    public void shouldCreateRequestWithURI() {
        HttpRequest request = HttpRequest.put("user.js");
        assertThat(request.getURI(), is("user.js"));
    }
    
    public void bodyShouldConvertBodyMapToJsonString() {
        HttpRequest request = HttpRequest.put("whocares").body("foo", "bar", "baz", "ban");

        assertThat(request.getBody(), is(toJson(toMap("foo", "bar", "baz", "ban"))));
    }
    
    public void bodyShouldConvertMapToJsonString() {
        Map body = toMap("foo", "bar", "baz", "ban");
        HttpRequest request = HttpRequest.put("whocares").body(body);
        
        assertThat(request.getBody(), is(toJson(body)));
    }

    public void shouldAcceptBodyParameter() {
        HttpRequest request = HttpRequest.put("user.js").body("some stuff");
        assertThat(request.getBody(), is("some stuff"));
    }
    
    public void shouldAcceptHeadersParameter() {
        Header[] headers = new Header[] {
            new BasicHeader("header1", "value1"),
            new BasicHeader("header2", "value2")
        };

        HttpRequest request = HttpRequest.put("user.js").headers(headers);
        assertThat(request.getHeaders(), is(headers));
    }
}
