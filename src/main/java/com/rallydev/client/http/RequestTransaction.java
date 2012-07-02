package com.rallydev.client.http;

import com.rallydev.net.Transaction;

public class RequestTransaction implements Transaction<String, HttpResponse> {
    private final RallyHttpClient client;
    private final HttpRequest request;

    public RequestTransaction(RallyHttpClient client, HttpRequest request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public HttpResponse with(String url) throws Exception {
        HttpResponse response = client.execute(url, request);

        if(response.getCode() >= 500) throw new UnexpectedResponseException();

        return response;
    }
}
