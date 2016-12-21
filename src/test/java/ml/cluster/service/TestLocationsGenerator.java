package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.generator.RandomGenerator;
import ml.cluster.to.PickLocationViewDO;

import java.util.*;
import java.util.stream.IntStream;

public class TestLocationsGenerator {

    private final static int MAX_X_AXIS_VALUE = 50;
    private final static int MAX_Y_AXIS_VALUE = 50;
    private final static String[] LINES = {"AA", "BB", "CC"};
    private final static int RADIUS = 10;

    public static int getLocationLinesQuantity() {
        return LINES.length;
    }

    public static List<PickLocationViewDO> generateLocations(final int quantity) {
        return generateLocations(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    public static List<PickLocationViewDO> generateLocations(final int quantity, final int maxX, final int maxY) {
        final List<PickLocationViewDO> locations = new ArrayList<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[RandomGenerator.generateUniformInt(LINES.length)]);
            setCoordinate(location, maxX, maxY);
            locations.add(location);
        });

        return Collections.unmodifiableList(locations);
    }

    public static Map<String, List<PickLocationViewDO>> generateGroupedLocations(final int quantity, final int maxX, final int maxY) {
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

    public static Map<String, List<PickLocationViewDO>> generateGroupedLocations(final int quantity) {
        return generateGroupedLocations(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    private static void setCoordinate(final PickLocationViewDO location, final int maxX, final int maxY) {
        location.setX(RandomGenerator.generateUniformDouble(maxX));
        location.setY(RandomGenerator.generateUniformDouble(maxY));
    }

    public static PickLocationViewDO createSingleLocation(final double x, final double y) {
        final PickLocationViewDO location = new PickLocationViewDO();
        location.setX(x);
        location.setY(y);
        return location;
    }

    public static List<Point> generatePoints(final int quantity) {
        final List<PickLocationViewDO> locations = generateLocations(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
        return wrapLocations(locations, RADIUS);
    }

    public static List<Point> generatePoints(final int quantity, final int maxX, final int maxY, final int radius) {
        final List<PickLocationViewDO> locations = generateLocations(quantity, maxX, maxY);
        return wrapLocations(locations, radius);
    }

    private static List<Point> wrapLocations(final List<PickLocationViewDO> locations, final int radius) {
        final List<Point> points = new ArrayList<>();

        locations.forEach(location -> {
            final Point point = new Point(location);
            setReachabilityDistance(point, radius);
            points.add(point);
        });

        return points;
    }

    private static void setReachabilityDistance(final Point point, final int radius) {
        point.setReachabilityDistance(RandomGenerator.generateUniformDouble(radius));
    }
}
