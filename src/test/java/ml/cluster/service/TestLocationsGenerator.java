package ml.cluster.service;

import ml.cluster.generator.RandomGenerator;
import ml.cluster.to.PickLocationViewDO;

import java.util.*;
import java.util.stream.IntStream;

public class TestLocationsGenerator {

    private final static int MAX_Y_AXIS_VALUE = 100;
    private final static int MAX_X_AXIS_VALUE = 100;
    private final static String[] LINES = {"AA", "BB", "CC"};

    public static int getLocationLinesQuantity() {
        return LINES.length;
    }

    public static List<PickLocationViewDO> generate(final int quantity) {
        final List<PickLocationViewDO> locations = new ArrayList<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[RandomGenerator.generateUniformInt(LINES.length)]);
            location.setX(RandomGenerator.generateUniformDouble(MAX_Y_AXIS_VALUE));
            location.setY(RandomGenerator.generateUniformDouble(MAX_X_AXIS_VALUE));
            locations.add(location);
        });

        return Collections.unmodifiableList(locations);
    }

    public static Map<String, List<PickLocationViewDO>> generateGrouped() {
        final Map<String, List<PickLocationViewDO>> segmentGroup = new HashMap<>();


        return segmentGroup;
    }
}
