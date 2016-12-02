package ml.cluster.generator;

import java.util.Random;

public class RandomGenerator {

    private static final Random RANDOM = new Random();

    public static int generateUniform(final int max) {
        return RANDOM.nextInt(max);
    }
}
