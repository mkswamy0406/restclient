package com.rallydev.client.http;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import static com.rallydev.client.http.Resource.isValidForType;

public abstract class Headers {
    public static final String ZUUL_AUTH_HEADER_NAME = "Authorization";

    public static Header ZuulAuthentication(String authKeyRef) {
        if(isValidForType("key", authKeyRef)) {
            String headerValue = String.format("Zuul authentication_key=\"%s\"", authKeyRef);
            return new BasicHeader(ZUUL_AUTH_HEADER_NAME, headerValue);
        }

        throw new IllegalArgumentException(String.format("Authorization key ref is invalid: %s", authKeyRef));
    }
}
