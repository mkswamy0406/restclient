package com.rallydev.client.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.concurrent.Callable;

public class RequestTask implements Callable<HttpResponse> {
    public static final String TRANSACTION_HEADER = "X-Transaction-Id";
    private final HttpClient client;
    private final HttpUriRequest request;
    private final long enqueueTime;

    public RequestTask(HttpClient client, HttpUriRequest request) {
        this.client = client;
        this.request = request;
        enqueueTime = System.currentTimeMillis();
    }

    private String getTraceId(HttpUriRequest request) {
        Header[] headers = request.getHeaders(TRANSACTION_HEADER);
        if(headers.length > 0) {
            return headers[0].getValue();
        } else {
            return "NO_VALUE";
        }
    }

    @Override
    public HttpResponse call() throws Exception {
        long dequeueTime = System.currentTimeMillis();
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } finally {
            long completionTime = System.currentTimeMillis();
            long queueTime = dequeueTime - enqueueTime;
            long processingTime = completionTime - dequeueTime;
            long totalTime = completionTime - enqueueTime;

            if(totalTime > 100) {
                RallyHttpClient.LOGGER.info(String.format("RallyRestProcessingTime queueTime=%s processingTime=%s totalTime=%s traceId=%s uri=%s method=%s",
                        queueTime,
                        processingTime,
                        totalTime,
                        getTraceId(request),
                        request.getURI(),
                        request.getMethod()));
            }
        }
        return response;
    }
}
