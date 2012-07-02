package com.rallydev.client.http;

import com.rallydev.client.json.Json;

import static com.rallydev.client.json.JsonSerializer.deserializeJson;
import static com.rallydev.client.json.JsonSerializer.toJson;
import static org.apache.http.HttpStatus.SC_OK;

public class HttpResponse {
    private final String body;
    private final int code;
    
    public HttpResponse(int code, Object... body) {
        this.code = code;
        this.body = toJson(body);
    }

    public HttpResponse(int code, String body) {
        this.body = body;
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public Json getBodyAsJson() {
        return deserializeJson(getBody());
    }

    public boolean isOk() {
        return is(SC_OK);
    }

    public boolean isNotOk() {
        return isNot(SC_OK);
    }

    public boolean is(int code) {
        return this.code == code;
    }

    public boolean isNot(int code) {
        return this.code != code;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "body='" + body + '\'' +
                ", code=" + code +
                '}';
    }
}
