package com.rallydev.net;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.fail;

@SuppressWarnings("unchecked")
@Test
public class SharedResourcePoolTest {
    private static final Transaction<String, String> IDENTITY = new IdentityTransaction<String>();
    private static final Transaction<String, String> EXCEPTIONAL = new ExceptionalTransaction<String>();

    public void shouldCycleThroughEachSharedResourceBeforeStartingOver() {
        SharedResourcePool<String> pool = new SharedResourcePool<String>(new SimpleUsageStrategy(),
                new SimpleSharedResource<String>("A"),
                new SimpleSharedResource<String>("B"));

        assertThat(pool.use(IDENTITY), is("A"));
        assertThat(pool.use(IDENTITY), is("B"));
        assertThat(pool.use(IDENTITY), is("A"));
        assertThat(pool.use(IDENTITY), is("B"));
        assertThat(pool.use(IDENTITY), is("A"));
    }

    public void shouldOnlyUseSharedResourcesThatCanBeUsed() {
        SharedResourcePool<String> pool = new SharedResourcePool<String>(
                new SimpleUsageStrategy(),
                new SimpleSharedResource<String>("A", false),
                new SimpleSharedResource<String>("B"));

        assertThat(pool.use(IDENTITY), is("B"));
        assertThat(pool.use(IDENTITY), is("B"));
        assertThat(pool.use(IDENTITY), is("B"));
    }

    public void shouldThrowExceptionIfNoneOfTheResourcesCanBeUsed() {
        SharedResourcePool<String> pool = new SharedResourcePool<String>(
                new SimpleUsageStrategy(),
                new SimpleSharedResource<String>("A", false));

        try {
            pool.use(IDENTITY);
            fail("Exception should be thrown");
        } catch (NoResourcesCanBeUsedException e) {

        }
    }

    public void shouldTryNextResourceIfTransactionThrowsException() {
        SharedResourcePool<String> pool = new SharedResourcePool<String>(
                new SimpleUsageStrategy(),
                new SimpleSharedResource<String>("A"),
                new SimpleSharedResource<String>("B"));

        assertThat(pool.use(EXCEPTIONAL), is("B"));
    }

    public static class SimpleUsageStrategy implements UsageStrategy {
        private int next = 0;

        @Override
        public int next(int max) {
            return next++ % max;
        }
    }

}
