package com.rallydev.client.http;

public class HttpClientException extends RuntimeException {
    public HttpClientException() {
    }

    public HttpClientException(Throwable throwable) {
        super(throwable);
    }
}
