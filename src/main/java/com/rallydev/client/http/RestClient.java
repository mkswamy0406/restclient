package com.rallydev.client.http;

import com.rallydev.net.NoResourcesCanBeUsedException;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;
import com.twitter.util.Try;
import org.apache.http.Header;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.twitter.util.Duration.fromSeconds;

public class RestClient {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String X_TENANT_HEADER = "x-tenant";
    public static final int ERROR_RESPONSE = 500;
    private Service<org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.handler.codec.http.HttpResponse> client;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
    private String tenant;
    private int maximumRetries;
    private int waitBetweenRetries;

    public RestClient(String... hosts) {
        this(3, 200, hosts);
    }

    public RestClient(int maximumRetries, int waitBetweenRetries, String... hosts) {
        this.maximumRetries = maximumRetries;
        this.waitBetweenRetries = waitBetweenRetries;
        if (hosts == null || hosts.length == 0) {
            throw new IllegalArgumentException("Empty list of host names passed to RestClient is not helpful");
        }

        tenant = getTenant(hosts[0]);

        StringBuilder builder = new StringBuilder();
        for (String host : hosts) {
            builder.append(host.replace("http://", "").replace("/", "") + " ");
        }
        String parsedHosts = builder.toString().trim();

        client = ClientBuilder.safeBuild(ClientBuilder.get()
                .codec(Http.get())
                .hosts(parsedHosts)
                .retries(3)
                .hostConnectionLimit(100)
                .failFast(true)
                .timeout(fromSeconds(10))
                .keepAlive(true)
                .tcpConnectTimeout(fromSeconds(1)));
    }

    protected static String getTenant(String url) {
        String host = url.replace("http://", "");
        String tenant = host.split("\\.|:")[0];
        if ("localhost".equals(tenant)) {
            tenant = System.getProperty("ALM_JDBC_USERNAME");
        }
        return tenant;
    }

    public HttpResponse execute(HttpRequest request) {
        HttpResponse response = null;
        for (int i = 0; i < maximumRetries; i++) {
            if (i > 0) {
                sleep();
            }

            response = getHttpResponse(request, i);
            if (response != null && response.getCode() < ERROR_RESPONSE) {
                return response;
            }
        }
        throw new NoResourcesCanBeUsedException(new ArrayList<Exception>());
    }

    private void sleep() {
        try {
            Thread.sleep(waitBetweenRetries);
        } catch (InterruptedException e) {
            //Keep on trucking
        }
    }

    private HttpResponse getHttpResponse(HttpRequest request, int attempt) {
        HttpResponse httpResponse = null;
        try {
            Future<org.jboss.netty.handler.codec.http.HttpResponse> responseFuture = client.apply(toRequest(request));
            Try<org.jboss.netty.handler.codec.http.HttpResponse> responseTry = responseFuture.get(fromSeconds(10));
            httpResponse = toResponse(responseTry.apply());
        } catch (Exception e) {
            LOGGER.error(String.format("NoResourcesCanBeUsed attempt=%s", attempt), e);
        }
        return httpResponse;
    }

    protected DefaultHttpRequest toRequest(HttpRequest request) {
        DefaultHttpRequest nettyRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, getMethod(request), getUri(request));
        nettyRequest.setContent(ChannelBuffers.copiedBuffer(request.getBody(), Charset.defaultCharset()));
        for (Header header : request.getHeaders()) {
            if (header.getValue() != null) {
                nettyRequest.addHeader(header.getName(), header.getValue());
            }
        }
        nettyRequest.addHeader(CONTENT_LENGTH_HEADER, ((Integer) request.getBody().getBytes().length).toString());
        if (tenant != null && !nettyRequest.containsHeader(X_TENANT_HEADER)) {
            nettyRequest.addHeader(X_TENANT_HEADER, tenant);
        }
        return nettyRequest;
    }

    protected String getUri(HttpRequest request) {
        String uri = request.getURI();
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        return uri;
    }

    protected HttpResponse toResponse(org.jboss.netty.handler.codec.http.HttpResponse response) {
        HttpResponse httpResponse = new HttpResponse(
                response.getStatus().getCode(),
                response.getContent().toString(Charset.defaultCharset()));
        httpResponse.setLocation(response.getHeader("Location"));
        return httpResponse;
    }

    public void close() throws IOException {
        client.release();
    }

    protected HttpMethod getMethod(HttpRequest request) {
        return HttpMethod.valueOf(request.getMethod().name());
    }
}
