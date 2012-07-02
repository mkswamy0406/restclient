package com.rallydev.client.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.concurrent.Callable;

public class RequestTask implements Callable<HttpResponse> {
    private final HttpClient client;
    private final HttpUriRequest method;

    public RequestTask(HttpClient client, HttpUriRequest method) {
        this.client = client;
        this.method = method;
    }

    @Override
    public HttpResponse call() throws Exception {
        return client.execute(method);
    }
}
