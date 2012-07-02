package com.rallydev.client;

import org.testng.annotations.Test;

import java.util.Map;

import static com.rallydev.client.MapUtils.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.fail;

@Test
public class MapUtilsTest {

    public void toMapShouldThrowExceptionWhenVarargsIsNotEven() {
        try {
            toMap("foo", "bar", "baz");
            fail("Should throw Exception");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void shouldCreateAMapFromAVaragsSetOfObjects() {
        Map map = toMap("abc", 123);

        assertThat((Integer)map.get("abc"), is(123));
    }

    public void toMapShouldConvertParametersToAMap() {
        Map map = toMap("foo", "bar", "baz", "ban");

        assertThat((String) map.get("foo"), is("bar"));
        assertThat((String)map.get("baz"), is("ban"));
    }
}
