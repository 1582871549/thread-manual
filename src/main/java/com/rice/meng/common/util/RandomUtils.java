package com.rice.meng.common.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public static String nextString(int origin, int bound) {
        return String.valueOf(nextInt(origin, bound));
    }

    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

}
