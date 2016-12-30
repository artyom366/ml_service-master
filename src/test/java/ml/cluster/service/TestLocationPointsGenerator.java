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

    private static List<Point> POINTS = generateSpecificLocationPoints();

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
        POINTS = generateSpecificLocationPoints();

        groupedPoints.put(LINES[0], POINTS);
        return Collections.unmodifiableMap(groupedPoints);
    }

    private static List<Point> generateSpecificLocationPoints() {

        //start of the cluster 1
        final Point point_0_0 = generateSingleLocationPoint(0, 0);
        point_0_0.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_0_1 = generateSingleLocationPoint(1, 1);
        point_0_1.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_0_2 = generateSingleLocationPoint(1, 2);
        point_0_2.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_0_3 = generateSingleLocationPoint(2, 3);
        point_0_3.setCell(new ImmutablePair<>(0L, 0L));
        //break of the cluster 1

        //outlier
        final Point point_0 = generateSingleLocationPoint(9, 3);
        point_0.setCell(new ImmutablePair<>(1L, 0L));

        //start of the cluster 2
        final Point point_1_0 = generateSingleLocationPoint(8, 8);
        point_1_0.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_1_2 = generateSingleLocationPoint(7, 8);
        point_1_2.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_1_3 = generateSingleLocationPoint(9, 9);
        point_1_3.setCell(new ImmutablePair<>(1L, 1L));

        //outlier
        final Point point_1 = generateSingleLocationPoint(1, 9);
        point_1.setCell(new ImmutablePair<>(0L, 1L));

        final Point point_1_4 = generateSingleLocationPoint(9, 8);
        point_1_4.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_1_5 = generateSingleLocationPoint(7, 9);
        point_1_5.setCell(new ImmutablePair<>(1L, 1L));

        final Point point_1_6 = generateSingleLocationPoint(8, 9);
        point_1_6.setCell(new ImmutablePair<>(1L, 1L));
        //end of the cluster 2

        //continuation of the cluster 1
        final Point point_0_4 = generateSingleLocationPoint(2, 2);
        point_0_4.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_0_5 = generateSingleLocationPoint(3, 2);
        point_0_5.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_0_6 = generateSingleLocationPoint(2, 1);
        point_0_6.setCell(new ImmutablePair<>(0L, 0L));

        final Point point_0_7 = generateSingleLocationPoint(2, 7);
        point_0_7.setCell(new ImmutablePair<>(0L, 1L));
        //end of the cluster 1

        //outlier
        final Point point_2 = generateSingleLocationPoint(0, 9);
        point_1.setCell(new ImmutablePair<>(0L, 1L));

        return Arrays.asList(point_0_0, point_0_1, point_0_2, point_0_3, point_0, point_1_0, point_1_2, point_1_3, point_1, point_1_4, point_1_5, point_1_6, point_0_4, point_0_5, point_0_6, point_0_7, point_2);
    }

    public static int getSpecificLocationPointsQuantity() {
        return POINTS.size();
    }
}
