package com.rallydev.client.http;

import org.apache.http.Header;
import org.testng.annotations.Test;

import static com.rallydev.client.http.Headers.TraceId;
import static com.rallydev.client.http.Headers.ZUUL_AUTH_HEADER_NAME;
import static com.rallydev.client.http.Headers.ZuulAuthentication;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.fail;

@Test
public class HeadersTest {

    public void shouldThrowIllegalArgumentExceptionForNullInput() {
        try {
            ZuulAuthentication(null);
            fail("should have triggered an IllegalArgumentException");
        } catch(IllegalArgumentException e) {}
    }

    public void shouldThrowIllegalArgumentExceptionForMalformedString() {
        try {
            ZuulAuthentication("adsasdfa");
            fail("should have triggered an IllegalArgumentException");
        } catch(IllegalArgumentException e) {}
    }
    
    public void shouldThrowIllegalArgumentExceptionForStringThatIsNotAnAuthKeyRef() {
        try {
            ZuulAuthentication("/foo/123456678.js");
            fail("should have triggered an IllegalArgumentException");
        } catch(IllegalArgumentException e) {}
    }
    
    public void shouldThrowIllegalArgumentExceptionForAuthKeyRefWithMissingId() {
        try {
            ZuulAuthentication("/key/.js");
            fail("should have triggered an IllegalArgumentException");
        } catch(IllegalArgumentException e) {}
    }
    
    public void shouldThrowIllegalArgumentExceptionForAuthKeyRefThatIsWrongButContainsKey() {
        try {
            ZuulAuthentication(String.format("/keyballs/%s.js", randomUUID()));
            fail("should have triggered an IllegalArgumentException");
        } catch(IllegalArgumentException e) {}
    }
    
    public void shouldReturnHeaderForWellFormedAuthKeyRef() {
        String keyRef = String.format("/key/%s.js", randomUUID().toString().replace("-", ""));
        Header header = ZuulAuthentication(keyRef);

        assertThat(header.getName(), is(ZUUL_AUTH_HEADER_NAME));
        assertThat(header.getValue(), is(String.format("Zuul authentication_key=\"%s\"", keyRef)));
    }

    public static void shouldReturnTheCorrectHeaderWithAGoodTraceId() {
        String traceId = "fooobar";
        Header header = TraceId(traceId);

        assertThat(header.getName(), is(Headers.X_TRACE_ID));
        assertThat(header.getValue(), is(traceId));
    }

    public static void shouldGenerateTraceIdIfGivenTraceIdIsNull() {
        Header header = TraceId(null);

        assertThat(header.getName(), is(Headers.X_TRACE_ID));
        assertThat(header.getValue(), is(notNullValue()));
    }
}
