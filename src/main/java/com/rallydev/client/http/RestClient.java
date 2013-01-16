package com.rallydev.client.http;

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

import java.io.IOException;
import java.nio.charset.Charset;

public class RestClient {
    private Service<org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.handler.codec.http.HttpResponse> client;

    public RestClient(String hosts) {
        client = ClientBuilder.safeBuild(ClientBuilder.get()
                .codec(Http.get())
                .hosts(hosts)
                .hostConnectionLimit(100));
    }

    public HttpResponse execute(HttpRequest request) {
        Future<org.jboss.netty.handler.codec.http.HttpResponse> responseFuture = client.apply(toRequest(request));
        Try<org.jboss.netty.handler.codec.http.HttpResponse> responseTry = responseFuture.get(Duration.fromSeconds(2));
        return toResponse(responseTry.apply());
    }

    public DefaultHttpRequest toRequest(HttpRequest request) {
        DefaultHttpRequest nettyRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, getMethod(request), request.getURI());
        nettyRequest.setContent(ChannelBuffers.copiedBuffer(request.getBody(), Charset.defaultCharset()));
        for(Header header : request.getHeaders()) {
            nettyRequest.addHeader(header.getName(), header.getValue());
        }
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
