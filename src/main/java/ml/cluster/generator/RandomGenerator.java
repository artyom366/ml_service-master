package ml.cluster.generator;

import java.util.Random;

public class RandomGenerator {

    private static final Random RANDOM = new Random();

    public static int generateUniformInt(final int max) {
        return RANDOM.nextInt(max);
    }

    public static double generateUniformDouble(final int max) {
        return RANDOM.nextDouble() * generateUniformInt(max);
    }
}
