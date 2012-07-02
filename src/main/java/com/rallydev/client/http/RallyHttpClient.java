package com.rallydev.client.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RallyHttpClient implements Closeable {
    private static final int DEFAULT_CXN_PER_HOST = 30;
    private static final int DEFAULT_TIMEOUT = 2;

    private final ThreadSafeClientConnManager connectionManager;
    private final HttpClient client;
    private final ExecutorService executor;
    private final Integer timeout;
    private final TimeUnit timeoutUnit;

    public RallyHttpClient() {
        this(DEFAULT_TIMEOUT, SECONDS);
    }

    public RallyHttpClient(Integer timeout, TimeUnit timeoutUnit) {
        connectionManager = new ThreadSafeClientConnManager();
        connectionManager.setDefaultMaxPerRoute(DEFAULT_CXN_PER_HOST);
        connectionManager.setMaxTotal(DEFAULT_CXN_PER_HOST * 2);

        client = new DefaultHttpClient(connectionManager);
        executor = Executors.newCachedThreadPool();

        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public HttpResponse execute(String url, HttpRequest request) throws IOException {
        final HttpUriRequest method = createRequest(url, request);

        try {
            org.apache.http.HttpResponse response = timedRequest(method);
            HttpEntity entity = response.getEntity();

            if (entity == null) throw new UnexpectedResponseException();

            return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity));
        } catch (Exception e) {
            method.abort();
            throw new HttpClientException(e);
        }
    }

    @Override
    public void close() throws IOException {
        connectionManager.shutdown();
        executor.shutdown();
    }

    private HttpUriRequest createRequest(String url, HttpRequest request) throws IOException {
        String uri = url + request.getURI();

        switch (request.getMethod()) {
            case GET:
                return applyHeaders(new HttpGet(uri), request);
            case DELETE:
                return applyHeaders(new HttpDelete(uri), request);
            case PUT:
                return applyHeaders(applyEntity(new HttpPut(uri), request), request);
            case POST:
                return applyHeaders(applyEntity(new HttpPost(uri), request), request);
            default:
                throw new IllegalArgumentException("Unknown request type: " + request.getMethod().name());
        }
    }

    private HttpUriRequest applyEntity(HttpEntityEnclosingRequestBase httpRequest, HttpRequest request) throws IOException {
        httpRequest.setEntity(new StringEntity(request.getBody()));
        return httpRequest;
    }

    private HttpUriRequest applyHeaders(HttpUriRequest httpRequest, HttpRequest request) {
        httpRequest.setHeaders(request.getHeaders());
        return httpRequest;
    }

    private org.apache.http.HttpResponse timedRequest(final HttpUriRequest method) throws Exception {
        Future<org.apache.http.HttpResponse> responseFuture = executor.submit(new RequestTask(client, method));

        try {
            return responseFuture.get(timeout, timeoutUnit);
        } finally {
            responseFuture.cancel(true);
        }
    }
}
