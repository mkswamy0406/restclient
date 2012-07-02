package com.rallydev.net;

public interface Transaction<I, O> {
    O with(I shared) throws Exception;
}
