package com.rallydev.client.http;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

@Test
public class RequestTransactionTest {
    private RallyHttpClient client;
    private HttpRequest request;
    private RequestTransaction transaction;

    @BeforeMethod
    protected void setUp() throws Exception {
        client = mock(RallyHttpClient.class);
        request = mock(HttpRequest.class);
        transaction = new RequestTransaction(client, request);
    }
    
    public void shouldCallClientWithRequest() throws Exception {
        when(client.execute(anyString(), any(HttpRequest.class))).thenReturn(new HttpResponse(200, ""));

        String url = "http://foo.bar.com";
        transaction.with(url);
        verify(client).execute(url, request);
    }

    public void shouldThrowExceptionWhenClientReceivesA500OrGreaterResponseCode() throws Exception {
        when(client.execute(anyString(), any(HttpRequest.class))).thenReturn(new HttpResponse(501, ""));

        try {
            transaction.with("http://foo.bar.com/");
            fail("should have thrown an exception");
        } catch(UnexpectedResponseException e) {}
    }
}
