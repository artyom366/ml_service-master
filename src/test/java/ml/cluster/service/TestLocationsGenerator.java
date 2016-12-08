package ml.cluster.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import ml.cluster.generator.RandomGenerator;
import ml.cluster.to.PickLocationViewDO;

public class TestLocationsGenerator {

    private final static int MAX_X_AXIS_VALUE = 50;
    private final static int MAX_Y_AXIS_VALUE = 50;
    private final static String[] LINES = {"AA", "BB", "CC"};

    public static int getLocationLinesQuantity() {
        return LINES.length;
    }

    public static List<PickLocationViewDO> generate(final int quantity) {
        final List<PickLocationViewDO> locations = new ArrayList<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[RandomGenerator.generateUniformInt(LINES.length)]);
            setCoordinate(location, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
            locations.add(location);
        });

        return Collections.unmodifiableList(locations);
    }

    public static Map<String, List<PickLocationViewDO>> generateGrouped(final int quantity, final int maxX, final int maxY) {
        final List<PickLocationViewDO> locations = new ArrayList<>();
        final Map<String, List<PickLocationViewDO>> groupedLocations = new HashMap<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[0]);
            setCoordinate(location, maxX, maxY);
            locations.add(location);
        });

        groupedLocations.put(LINES[0], locations);
        return Collections.unmodifiableMap(groupedLocations);
    }

    public static Map<String, List<PickLocationViewDO>> generateGrouped(final int quantity) {
        return generateGrouped(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    private static void setCoordinate(final PickLocationViewDO location, final int maxX, final int maxY) {
        location.setX(RandomGenerator.generateUniformDouble(maxX));
        location.setY(RandomGenerator.generateUniformDouble(maxY));
    }
}
