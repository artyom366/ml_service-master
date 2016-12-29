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

    public static List<Point> generateLocations(final int quantity) {
        return generateLocations(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    public static List<Point> generateLocations(final int quantity, final int maxX, final int maxY) {
        final List<Point> locations = new ArrayList<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[RandomGenerator.generateUniformInt(LINES.length)]);
            setCoordinate(location, maxX, maxY);

            final Point point = Point.newInstance(location);
            locations.add(point);
        });

        return Collections.unmodifiableList(locations);
    }

    public static Map<String, List<Point>> generateGroupedLocations(final int quantity, final int maxX, final int maxY) {
        final List<Point> locations = new ArrayList<>();
        final Map<String, List<Point>> groupedLocations = new HashMap<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[0]);
            setCoordinate(location, maxX, maxY);

            final Point point = Point.newInstance(location);
            locations.add(point);
        });

        groupedLocations.put(LINES[0], locations);
        return Collections.unmodifiableMap(groupedLocations);
    }

    public static Map<String, List<Point>> generateGroupedLocations(final int quantity) {
        return generateGroupedLocations(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    private static void setCoordinate(final PickLocationViewDO location, final int maxX, final int maxY) {
        location.setX(RandomGenerator.generateUniformDouble(maxX));
        location.setY(RandomGenerator.generateUniformDouble(maxY));
    }

    public static Point createSingleLocation(final double x, final double y) {
        final PickLocationViewDO location = new PickLocationViewDO();
        location.setX(x);
        location.setY(y);
        return Point.newInstance(location);
    }

    public static List<Point> generateLocationWithDistance(final int quantity, final int maxX, final int maxY, final int radius) {
        final List<Point> locations = generateLocations(quantity, maxX, maxY);
        return setReachabilityDistance(locations, radius);
    }

    private static List<Point> setReachabilityDistance(final List<Point> locations, final int radius) {
        final List<Point> points = new ArrayList<>();

        locations.forEach(location -> {
            setReachabilityDistance(location, radius);
            points.add(location);
        });

        return points;
    }

    private static void setReachabilityDistance(final Point point, final int radius) {
        point.setReachabilityDistance(RandomGenerator.generateUniformDouble(radius));
    }
}
