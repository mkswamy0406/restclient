package com.rallydev.net;

import java.util.Random;

public class RandomUsageStrategy implements UsageStrategy {
    private final Random random = new Random();

    @Override
    public int next(int max) {
        return random.nextInt(max);
    }
}
