package com.rallydev.client.http;

import org.apache.http.Header;

import java.util.Arrays;
import java.util.Map;

import static com.rallydev.client.MapUtils.toMap;
import static com.rallydev.client.json.JsonSerializer.toJson;

public class HttpRequest {
    private HttpMethod method;
    private String uri;
    private String body;
    private Header[] headers;

    private HttpRequest(HttpMethod method, String uri) {
        this.method = method;
        this.uri = uri;
        this.headers = new Header[0];
        this.body = "";
    }

    public Header[] getHeaders() {
        return this.headers;
    }

    public String getURI() {
        return uri;
    }

    public String getBody() {
        return this.body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public static HttpRequest put(String uri) {
        return new HttpRequest(HttpMethod.PUT, uri);
    }
    
    public static HttpRequest post(String uri) {
        return new HttpRequest(HttpMethod.POST, uri);
    }

    public static HttpRequest post(Resource resource) {
        return new HttpRequest(HttpMethod.POST, resource.asString());
    }
    
    public static HttpRequest delete(String uri) {
        return new HttpRequest(HttpMethod.DELETE, uri);
    }
    
    public static HttpRequest delete(Resource resource) {
        return new HttpRequest(HttpMethod.DELETE, resource.asString());
    }

    public static HttpRequest get(String uri) {
        return new HttpRequest(HttpMethod.GET, uri);
    }

    public static HttpRequest get(Resource resource) {
        return new HttpRequest(HttpMethod.GET, resource.asString());
    }

    public HttpRequest body(String body) {
        this.body = body;
        return this;
    }

    public HttpRequest body(Object... map) {
        return body(toMap(map));
    }

    public HttpRequest body(Map body) {
        return body(toJson(body));
    }

    public HttpRequest headers(Header... headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpRequest that = (HttpRequest) o;

        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        if (!Arrays.equals(headers, that.headers)) return false;
        if (method != that.method) return false;
        if (!uri.equals(that.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(headers);
        return result;
    }
}


