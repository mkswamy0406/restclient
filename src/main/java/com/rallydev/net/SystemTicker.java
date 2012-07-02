package com.rallydev.net;

public class SystemTicker implements Ticker {
    @Override
    public long read() {
        return System.currentTimeMillis();
    }
}
