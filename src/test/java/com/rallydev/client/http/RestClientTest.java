package com.rallydev.client.http;

import org.apache.http.message.BasicHeader;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.rallydev.client.http.RestClient.CONTENT_LENGTH_HEADER;
import static com.rallydev.client.http.RestClient.X_TENANT_HEADER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;

@Test
public class RestClientTest {

    public static final String HEADER_NAME = "x-balls";
    private RestClient client;

    @BeforeMethod
    public void beforeMethod() {
        client = new RestClient("http://google.com:8080");
    }

    public void shouldGetTenant() {
        assertThat(RestClient.getTenant("http://google.com"), is("google"));
        assertThat(RestClient.getTenant("http://google.com/"), is("google"));
        assertThat(RestClient.getTenant("http://google.com:8080"), is("google"));
        assertThat(RestClient.getTenant("google.com"), is("google"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldChuckAnErrorIfGivenNullHostsString() {
        new RestClient(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldChuckAnErrorIfGivenEmptyHostsString() {
        new RestClient();
    }

    public void shouldBuildResponseCorrectly() {
        HttpRequest request = HttpRequest.get("/foo");
        DefaultHttpRequest finagleRequest = client.toRequest(request);
        assertThat(finagleRequest.getMethod(), is(GET));
        assertThat(finagleRequest.getUri(), is("/foo"));
    }

    public void shouldAddHeaders() {
        String headerValue = "2";
        HttpRequest request = HttpRequest.get("/bar").headers(new BasicHeader(HEADER_NAME, headerValue));
        DefaultHttpRequest finagleRequest = client.toRequest(request);
        assertThat(finagleRequest.getHeader(CONTENT_LENGTH_HEADER), is(notNullValue()));
        assertThat(finagleRequest.getHeader(X_TENANT_HEADER), is("google"));
        assertThat(finagleRequest.getHeader(HEADER_NAME), is(headerValue));
    }

    public void shouldPutLeadingSlashOnUri() {
        HttpRequest request = HttpRequest.get("foo");
        DefaultHttpRequest finagleRequest = client.toRequest(request);
        assertThat(finagleRequest.getUri(), is("/foo"));
    }

    public void shouldNotAddHeadersWithNullValues() {
        HttpRequest request = HttpRequest.get("/bar").headers(new BasicHeader(HEADER_NAME, null));
        DefaultHttpRequest finagleRequest = client.toRequest(request);
        assertThat(finagleRequest.getHeader(HEADER_NAME), is(nullValue()));
    }

    public void shouldConvertLocalhostTenantToAlmJdbcUsername() {
        assertThat(RestClient.getTenant("http://localhost"), not("localhost"));
        assertThat(RestClient.getTenant("http://localhost:1234"), not("localhost"));
    }
}