package com.rallydev.net;

public interface SharedResource<T> {
    boolean isAvailable();

    <O> O execute(Transaction<T, O> transaction) throws Exception;
}
