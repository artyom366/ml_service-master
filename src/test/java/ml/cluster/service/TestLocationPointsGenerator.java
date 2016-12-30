package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.generator.RandomGenerator;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.IntStream;

public class TestLocationPointsGenerator {

    private final static int MAX_X_AXIS_VALUE = 50;
    private final static int MAX_Y_AXIS_VALUE = 50;
    private final static String[] LINES = {"AA", "BB", "CC"};
    private final static int RADIUS = 10;

    public static int getLocationLinesQuantity() {
        return LINES.length;
    }

    public static List<Point> generateLocationPoints(final int quantity) {
        return generateLocationPoints(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    public static List<Point> generateLocationPoints(final int quantity, final int maxX, final int maxY) {
        final List<Point> points = new ArrayList<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[RandomGenerator.generateUniformInt(LINES.length)]);
            setCoordinate(location, maxX, maxY);

            final Point point = Point.newInstance(location);
            points.add(point);
        });

        return Collections.unmodifiableList(points);
    }

    public static Map<String, List<Point>> generateGroupedLocationPoints(final int quantity, final int maxX, final int maxY) {
        final List<Point> points = new ArrayList<>();
        final Map<String, List<Point>> groupedPoints = new HashMap<>();

        IntStream.range(0, quantity).forEach(item -> {
            final PickLocationViewDO location = new PickLocationViewDO();
            location.setLine(LINES[0]);
            setCoordinate(location, maxX, maxY);

            final Point point = Point.newInstance(location);
            points.add(point);
        });

        groupedPoints.put(LINES[0], points);
        return Collections.unmodifiableMap(groupedPoints);
    }

    public static Map<String, List<Point>> generateGroupedLocationPoints(final int quantity) {
        return generateGroupedLocationPoints(quantity, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
    }

    private static void setCoordinate(final PickLocationViewDO location, final int maxX, final int maxY) {
        location.setX(RandomGenerator.generateUniformDouble(maxX));
        location.setY(RandomGenerator.generateUniformDouble(maxY));
    }

    public static Point generateSingleLocationPoint(final double x, final double y) {
        final PickLocationViewDO location = new PickLocationViewDO();
        location.setX(x);
        location.setY(y);
        return Point.newInstance(location);
    }

    public static List<Point> generateLocationPointWithDistance(final int quantity, final int maxX, final int maxY, final int radius) {
        final List<Point> points = generateLocationPoints(quantity, maxX, maxY);
        return setReachabilityDistance(points, radius);
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

    public static Map<String, List<Point>> generateGroupedSpecificLocationPoints() {
        final Map<String, List<Point>> groupedPoints = new HashMap<>();
        final List<Point> points = generateSpecificLocationPoints();

        groupedPoints.put(LINES[0], points);
        return Collections.unmodifiableMap(groupedPoints);
    }

    private static List<Point> generateSpecificLocationPoints() {
        final Point point_0 = generateSingleLocationPoint(0, 0);
        point_0.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_1 = generateSingleLocationPoint(1, 1);
        point_1.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_2 = generateSingleLocationPoint(1, 2);
        point_2.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_3 = generateSingleLocationPoint(2, 3);
        point_3.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_4 = generateSingleLocationPoint(8, 8);
        point_4.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_5 = generateSingleLocationPoint(7, 8);
        point_5.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_6 = generateSingleLocationPoint(9, 9);
        point_6.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_7 = generateSingleLocationPoint(2, 2);
        point_7.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_8 = generateSingleLocationPoint(3, 2);
        point_8.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_9 = generateSingleLocationPoint(2, 1);
        point_9.setCell(new ImmutablePair<>(0L, 0L));

        return Arrays.asList(point_0, point_1, point_2, point_3, point_4, point_5, point_6, point_7, point_8, point_9);
    }
}
