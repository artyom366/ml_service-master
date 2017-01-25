package ml.cluster.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.optics.impl.OpticsImpl;
import ml.cluster.datastructure.optics.impl.PointImpl;
import ml.cluster.to.PickLocationViewDO;

public class TestOpticsGenerator {

	private final static double DELTA = 5d;

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
		final List<Point> points = preparePointsWithUnderSizeCluster(location);
		final OpticsImpl optics = new OpticsImpl.OpticsBuilder().build();

		optics.addAllToOrderedLocationPoints(points);
		return optics;
	}

	private static PickLocationViewDO prepareDummyLocation() {
		final PickLocationViewDO LOCATION = new PickLocationViewDO();
		LOCATION.setX(5d);
		LOCATION.setY(5d);
		LOCATION.setLine("AA");
		return LOCATION;
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

	private static List<Point> preparePointsWithUnderSizeCluster(final PickLocationViewDO location) {
		final List<Point> points = new LinkedList<>();

		points.add(prepareCorePoint(location, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));
		points.add(prepareClusterPoint(location, 5d, 5d));

		return Collections.unmodifiableList(points);
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
