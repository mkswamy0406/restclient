package com.rallydev.net;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Test
public class BackoffSharedResourceTest {
    private static final Transaction<String, String> IDENTITY = new IdentityTransaction<String>();

    public void shouldAllowUseAsLongAsNoErrors() throws Exception {
        SharedResource<String> resource = new BackoffSharedResource<String>(new StaticTicker(2L), "A");

        assertThat(resource.isAvailable(), is(true));
        assertThat(resource.execute(IDENTITY), is("A"));
    }

    public void shouldBackoffWhenFailureRecorded() throws Exception {
        SharedResource<String> resource = new BackoffSharedResource<String>(new StaticTicker(3L, 4L), "A");

        executeBadTransaction(resource);

        assertThat(resource.isAvailable(), is(false));
    }

    public void shouldAllowUseAfterBackoutTimeIsElapsed() throws Exception {
        StaticTicker ticker = new StaticTicker(3L, 4L + BackoffSharedResource.BASE_BACKOFF_TIME);
        SharedResource<String> resource = new BackoffSharedResource<String>(ticker, "A");

        executeBadTransaction(resource);

        assertThat(resource.isAvailable(), is(true));
    }

    public void shouldIncreaseBackoffEveryFailure() throws Exception {
        StaticTicker ticker = new StaticTicker(
                3L,
                4L,
                5L + (BackoffSharedResource.BASE_BACKOFF_TIME * 2));
        SharedResource<String> resource = new BackoffSharedResource<String>(ticker, "A");

        executeBadTransaction(resource);
        executeBadTransaction(resource);

        assertThat(resource.isAvailable(), is(true));
    }

    public void shouldResetBackoffFactorAfterASuccessfulUse() throws Exception {
        StaticTicker ticker = new StaticTicker(
                3L,
                4L,
                5L,
                6L + (BackoffSharedResource.BASE_BACKOFF_TIME));
        SharedResource<String> resource = new BackoffSharedResource<String>(ticker, "A");

        executeBadTransaction(resource);
        executeBadTransaction(resource);

        resource.execute(IDENTITY);

        executeBadTransaction(resource);

        assertThat(resource.isAvailable(), is(true));
    }

    private void executeBadTransaction(SharedResource<String> resource) {
        try {
            resource.execute(new ExceptionalTransaction<String>());
        } catch (Exception e) {
        }
    }

    public static class StaticTicker implements Ticker {

        private int current = 0;
        private long[] times;

        public StaticTicker(long... times) {
            this.times = times;
        }

        @Override
        public long read() {
            return times[current++];
        }
    }

}

