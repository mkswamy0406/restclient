package com.rallydev.client.http;

import com.rallydev.net.NoResourcesCanBeUsedException;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
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

public class RestClient {
    private Service<org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.handler.codec.http.HttpResponse> client;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
    private String tenant;

    public RestClient(String... hosts) {
        if (hosts == null || hosts.length == 0) {
            throw new RuntimeException("Empty list of host names passed to RestClient is not helpful");
        }

        tenant = getTenant(hosts[0]);

        StringBuilder builder = new StringBuilder();
        for(String host : hosts) {
            builder.append(host.replace("http://", "").replace("/", "") + " ");
        }
        String parsedHosts = builder.toString().trim();

        client = ClientBuilder.safeBuild(ClientBuilder.get()
                .codec(Http.get())
                .hosts(parsedHosts)
                .retries(3)
                .hostConnectionLimit(100)
                .timeout(Duration.fromSeconds(2))
                .keepAlive(true));
    }

    private String getTenant(String url) {
        String host = url.replace("http://", "");
        return host.split(".")[0];
    }

    public HttpResponse execute(HttpRequest request) {
        HttpResponse httpResponse;
        try {
            Future<org.jboss.netty.handler.codec.http.HttpResponse> responseFuture = client.apply(toRequest(request));
            Try<org.jboss.netty.handler.codec.http.HttpResponse> responseTry = responseFuture.get(Duration.fromSeconds(2));
            httpResponse = toResponse(responseTry.apply());
        } catch (Exception e) {
            LOGGER.error("NoResourcesCanBeUsed", e);
            throw new NoResourcesCanBeUsedException(e);
        }
        return httpResponse;
    }

    public DefaultHttpRequest toRequest(HttpRequest request) {
        DefaultHttpRequest nettyRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, getMethod(request), request.getURI());
        nettyRequest.setContent(ChannelBuffers.copiedBuffer(request.getBody(), Charset.defaultCharset()));
        for(Header header : request.getHeaders()) {
            nettyRequest.addHeader(header.getName(), header.getValue());
        }
        nettyRequest.addHeader("Content-Length", ((Integer)request.getBody().getBytes().length).toString());
        nettyRequest.addHeader("x-tenant", tenant);
        return nettyRequest;
    }

    public HttpResponse toResponse(org.jboss.netty.handler.codec.http.HttpResponse response) {
        return new HttpResponse(response.getStatus().getCode(), response.getContent().toString(Charset.defaultCharset()));
    }

    public void close() throws IOException {
        client.release();
    }

    public HttpMethod getMethod(HttpRequest request) {
        return HttpMethod.valueOf(request.getMethod().name());
    }
}
