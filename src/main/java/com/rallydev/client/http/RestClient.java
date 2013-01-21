package com.rallydev.client.http;

import com.rallydev.net.SharedResourcePool;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class RestClient {
    private final RallyHttpClient httpClient;
    private final SharedResourcePool<String> urlPool;

    public RestClient(String... urls) {
        this(new RallyHttpClient(), asList(urls));
    }

    public RestClient(List<String> urls) {
        this(new RallyHttpClient(), urls);
    }

    public RestClient(RallyHttpClient httpClient, String... urls) {
        this(httpClient, asList(urls));
    }

    public RestClient(RallyHttpClient httpClient, List<String> urls) {
        this.httpClient = httpClient;
        this.urlPool = SharedResourcePool.of(urls);
    }

    public HttpResponse execute(HttpRequest request) {
        return urlPool.use(new RequestTransaction(httpClient, request));
    }

    public void close() throws IOException {
        httpClient.close();
    }

}
