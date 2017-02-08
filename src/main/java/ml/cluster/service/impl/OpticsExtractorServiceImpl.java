package ml.cluster.service.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.OpticsSummary;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.optics.impl.ClusterImpl;
import ml.cluster.datastructure.optics.impl.OpticsSummaryImpl;
import ml.cluster.error.OpticsException;
import ml.cluster.error.cluster.ClusterDistanceException;
import ml.cluster.error.cluster.ClusterSizeException;
import ml.cluster.service.OpticsExtractorService;
import ml.cluster.service.PointService;

@Service("opticsExtractorService")
public class OpticsExtractorServiceImpl implements OpticsExtractorService {

	@Autowired
	private PointService pointService;

	@Override
	public OpticsSummary extractClusters(final Optics optics) throws OpticsException {
		Validate.notNull(optics, "Optics is not defined");

		final List<Point> orderedLocationPoints = optics.getOrderedLocationPoints();
		Validate.notEmpty(orderedLocationPoints, "Ordering points are not defined");

		return createClusterSummary(orderedLocationPoints, optics.getMinPts(), optics.getRadius());
	}

	private OpticsSummary createClusterSummary(final List<Point> orderedLocationPoints, final int minPts, final long radius) throws OpticsException {
		final List<Point> outlierPoints = new LinkedList<>();
		final List<Cluster> clusters = new LinkedList<>();
		final Iterator<Point> iterator = orderedLocationPoints.iterator();

		while (iterator.hasNext()) {
			final Point point = iterator.next();
			startClusterFormation(point, iterator, clusters, outlierPoints, minPts, radius);
		}

		return new OpticsSummaryImpl(clusters, outlierPoints);
	}

	private void startClusterFormation(final Point point, final Iterator<Point> iterator, final List<Cluster> clusters, final List<Point> outlierPoints,
			final int minPts, final long radius) throws OpticsException {

		startClusterFormation(point, iterator, clusters, outlierPoints, radius);
		validateClusters(clusters, minPts);
	}

	private void startClusterFormation(final Point point, final Iterator<Point> iterator, final List<Cluster> clusters, final List<Point> outlierPoints,
			final long radius) throws OpticsException {

		if (pointService.isCorePoint(point)) {
			final Cluster cluster = extractCluster(iterator, point, clusters, outlierPoints, radius);
			clusters.add(cluster);
		}
	}

	private Cluster extractCluster(final Iterator<Point> iterator, final Point corePoint, final List<Cluster> clusters, final List<Point> outlierPoints,
			final long radius) throws OpticsException {

		final ClusterImpl cluster = new ClusterImpl();
		validateCorePointDistancesAndAddToCluster(corePoint, radius, cluster);

		while (iterator.hasNext()) {
			final Point point = iterator.next();

			if (pointService.isCorePoint(point)) {
				startClusterFormation(point, iterator, clusters, outlierPoints, radius);

			} else if (pointService.isClusterPoint(point)) {
				validateClusterPointDistancesAndAddToCluster(point, radius, cluster);

			} else if (pointService.isBorderPoint(point)) {
				validateBorderPointDistancesAndAddToCluster(point, radius, cluster);

			} else if (pointService.isOutlierPoint(point)) {
				outlierPoints.add(point);
				break;
			}
		}

		return cluster;
	}

	private void validateCorePointDistancesAndAddToCluster(final Point point, final long radius, final ClusterImpl cluster) throws OpticsException {
		if (point.getCoreDistance() > radius) {
			throw new ClusterDistanceException(String.format("Core distance: %s", point.getCoreDistance()), radius);

		} else {
			cluster.addToCluster(point);
		}
	}

	private void validateClusterPointDistancesAndAddToCluster(final Point point, final long radius, final ClusterImpl cluster) throws OpticsException {
		if (point.getCoreDistance() > radius && point.getReachabilityDistance() > radius) {
			throw new ClusterDistanceException(
				String.format("Core distance: %s or reachability distance: %s", point.getCoreDistance(), point.getReachabilityDistance()), radius);

		} else {
			cluster.addToCluster(point);
		}
	}

	private void validateBorderPointDistancesAndAddToCluster(final Point point, final long radius, final ClusterImpl cluster) throws OpticsException {
		if (point.getReachabilityDistance() > radius) {
			throw new ClusterDistanceException(String.format("Reachability distance: %s", point.getReachabilityDistance()), radius);

		} else {
			cluster.addToCluster(point);
		}
	}

	private void validateClusters(final List<Cluster> clusters, final int minPts) throws OpticsException {
		for (final Cluster cluster : clusters) {
			final List<Point> clusterPoints = cluster.getClusterPoints();
			validateClusterSize(clusterPoints, minPts);
		}
	}

	private void validateClusterSize(final List<Point> clusterPoints, final int minPts) throws OpticsException {
		if (clusterPoints.size() < minPts) {
			throw new ClusterSizeException(clusterPoints.size(), minPts);
		}
	}

}
