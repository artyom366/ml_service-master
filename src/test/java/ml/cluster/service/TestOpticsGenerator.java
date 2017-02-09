package ml.cluster.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.optics.impl.ClusterImpl;
import ml.cluster.datastructure.optics.impl.OpticsImpl;
import ml.cluster.datastructure.optics.impl.PointImpl;
import ml.cluster.to.PickLocationViewDO;

public class TestOpticsGenerator {

	private final static double DELTA = 5d;
	private final static double X = 5d;
	private final static double Y = 5d;

	public static Optics generateOptics() {
		final PickLocationViewDO location = prepareDummyLocation();
		final List<Point> points = preparePoints(location);
		final OpticsImpl optics = new OpticsImpl.OpticsBuilder().build();

		optics.addAllToOrderedLocationPoints(points);
		return optics;
	}

	public static Optics generateOpticsWithDistanceErrorInCluster() {
		final PickLocationViewDO location = prepareDummyLocation();
		final List<Point> points = preparePointsWithErrorInCluster(location);
		final OpticsImpl optics = new OpticsImpl.OpticsBuilder().build();

		optics.addAllToOrderedLocationPoints(points);
		return optics;
	}

	public static Optics generateOpticsWithSizeErrorInCluster() {
		final PickLocationViewDO location = prepareDummyLocation();
		final List<Point> points = preparePointsForSmallCluster(location);
		final OpticsImpl optics = new OpticsImpl.OpticsBuilder().build();

		optics.addAllToOrderedLocationPoints(points);
		return optics;
	}

	private static PickLocationViewDO prepareDummyLocation() {
		return prepareLocation(X, Y);
	}

	private static PickLocationViewDO prepareLocation(final double x, final double y) {
		final PickLocationViewDO location = new PickLocationViewDO();
		location.setX(x);
		location.setY(y);
		location.setLine("AA");
		return location;
	}

	private static List<Point> preparePoints(final PickLocationViewDO location) {
		final List<Point> points = new LinkedList<>();
		points.add(prepareCorePoint(location, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareBorderPoint(location, 5d));
		points.add(prepareBorderPoint(location, 5d));
		points.add(prepareBorderPoint(location, 5d));
		points.add(prepareOutlierPoint(location));

		points.add(prepareCorePoint(location, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareBorderPoint(location, 5d));
		points.add(prepareBorderPoint(location, 5d));

		points.add(prepareCorePoint(location, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareBorderPoint(location, 5d));
		points.add(prepareOutlierPoint(location));

		return Collections.unmodifiableList(points);
	}

	private static List<Point> preparePointsWithErrorInCluster(final PickLocationViewDO location) {
		final List<Point> points = new LinkedList<>();

		points.add(prepareCorePoint(location, 5d + DELTA));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareBorderPoint(location, 5d));
		points.add(prepareOutlierPoint(location));

		return Collections.unmodifiableList(points);
	}

	private static List<Point> preparePointsForSmallCluster(final PickLocationViewDO location) {
		final List<Point> points = new LinkedList<>();

		points.add(prepareCorePoint(location, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));

		return Collections.unmodifiableList(points);
	}

	public static Cluster prepareValidClusterWithStatingLocationAndLimitation(final int x, final int y, final int limit) {
		return new ClusterImpl(preparePointsForValidClusterWithStatingLocationAndLimitation(x, y, limit));
	}

	private static List<Point> preparePointsForValidClusterWithStatingLocationAndLimitation(final int x, final int y, final int limit) {
		final List<Point> points = new ArrayList<>();

		for (int i = x; i < x + limit; i++) {
			for (int j = y; j < y + limit; j++) {
				points.add(PointImpl.newInstance(prepareLocation(i, j)));
			}
		}

		return points;
	}

	private static Point prepareCorePoint(final PickLocationViewDO location, final double coreDistance) {
		final Point point = PointImpl.newInstance(location);
		point.setCoreDistance(coreDistance);
		return point;
	}

	private static Point prepareClusterPoint(final PickLocationViewDO location, final double coreDistance, final double reachabilityDistance) {
		final Point point = PointImpl.newInstance(location);
		point.setCoreDistance(coreDistance);
		point.setReachabilityDistance(reachabilityDistance);
		return point;
	}

	private static Point prepareBorderPoint(final PickLocationViewDO location, final double reachabilityDistance) {
		final Point point = PointImpl.newInstance(location);
		point.setReachabilityDistance(reachabilityDistance);
		return point;
	}

	private static Point prepareOutlierPoint(final PickLocationViewDO location) {
		return PointImpl.newInstance(location);
	}

}
