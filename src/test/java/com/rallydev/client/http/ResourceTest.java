package com.rallydev.client.http;

import org.testng.annotations.Test;

import static com.rallydev.client.http.Resource.isValid;
import static com.rallydev.client.http.Resource.isValidForType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.fail;

@Test
public class ResourceTest {
    public void shouldBeRepresentableAsAString() {
        Resource resource = new Resource("user", "1234abc");

        assertThat(resource.asString(), is("/user/1234abc.js"));
    }
    
    public void shouldConvertTypesAndIdsToResourceString() {
        assertThat(Resource.asString("user", "123abc"), is("/user/123abc.js"));
    }

    public void shouldBeAbleToDetermineIfAResourceStringIsValid() {
        assertThat(isValid(new Resource("user", "123jabs").asString()), is(true));
        assertThat(isValid("/user/.js"), is(false));
        assertThat(isValid("/user/"), is(false));
        assertThat(isValid("/123abc.js"), is(false));
        assertThat(isValid(null), is(false));
        assertThat(isValid(""), is(false));
    }
    
    public void shouldBeAbleToDetermineIfAResourceIsValidForASpecificType() {
        assertThat(isValidForType("user", "/user/123abc.js"), is(true));
        assertThat(isValidForType("user", "/key/1234.js"), is(false));
        assertThat(isValidForType("key", "/key"), is(false));
        assertThat(isValidForType("key", "/key.js"), is(false));
        
        assertThat(isValidForType("key", null), is(false));
        assertThat(isValidForType(null, "/key/123.js"), is(false));
        assertThat(isValidForType("key", ""), is(false));
        assertThat(isValidForType("", "/key/123.js"), is(false));
    }

    public void shouldBeAbleToParseAGoodResource() {
        Resource expected = new Resource("people", "1234rews");

        Resource resource = Resource.parse(expected.asString());

        assertThat(expected, is(resource));
    }

    public void shouldThrowInvalidResourceExceptionWhenResourceIsNotValid() {
        try {
            Resource.parse("/.js");
            fail("Should not parse invalid resources");
        } catch(Resource.InvalidResourceException e) {
            // Expected
        }
    }    
}