package com.rallydev.client.http;

import org.apache.http.client.methods.HttpRequestBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.rallydev.client.http.HttpMethod.GET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Test
public class RallyHttpClientTest{

    private RallyHttpClient client;
    private String slug;
    private HttpRequest mockRequest;

    @BeforeMethod
    public void setUp() throws Exception {
        client = new RallyHttpClient();

        slug = "yourmom";
        mockRequest = mock(HttpRequest.class);

        when(mockRequest.getMethod()).thenReturn(GET);
        when(mockRequest.getURI()).thenReturn(slug);
    }

    public void shouldCreateRequestWithUrlThatIsMissingSlash() throws IOException {
        String url = "http://rallydev.com";

        HttpRequestBase requestBase = client.createRequest(url, mockRequest);

        assertThat(requestBase.getURI().toString(), is(url + "/" + slug));
    }

    public void shouldCreateRequestWhenUrlHasTrailingSlash() throws IOException {
        String url = "http://rallydev.com/";

        HttpRequestBase requestBase = client.createRequest(url, mockRequest);

        assertThat(requestBase.getURI().toString(), is(url + slug));
    }
}
