package com.rallydev.client.http;

import com.rallydev.net.SharedResourcePool;

import java.io.IOException;

public class RestClient {
    private final RallyHttpClient httpClient;
    private final SharedResourcePool<String> urlPool;

    public RestClient(String... urls) {
        httpClient = new RallyHttpClient();
        this.urlPool = SharedResourcePool.of(urls);
    }

    public HttpResponse execute(HttpRequest request) {
        return urlPool.use(new RequestTransaction(httpClient, request));
    }

    public void close() throws IOException {
        httpClient.close();
    }

}
