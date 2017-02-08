package ml.cluster.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.OpticsException;
import ml.cluster.error.index.SilhouetteIndexException;
import ml.cluster.service.ClusterValidatorService;
import ml.cluster.service.OpticsNeighboursService;

@Service("clusterValidatorService")
public class ClusterValidatorServiceImpl implements ClusterValidatorService {

	@Autowired
	private OpticsNeighboursService neighboursService;

	@Override
	public double getSilhouetteIndex(final Cluster local, final Cluster remote, final int minPts) throws OpticsException {
		Validate.notNull(local, "Local cluster is not defined");
		Validate.notNull(remote, "Remote cluster is not defined");

		final List<Point> localPoints = Validate.notEmpty(local.getClusterPoints(), "Local cluster points are not defined");
		final List<Point> remotePoints = Validate.notEmpty(remote.getClusterPoints(), "Remote cluster points are not defined");

		if (localPoints.size() < minPts && remotePoints.size() < minPts) {
			throw new SilhouetteIndexException("Cluster size is not valid with respect to MinPts");
		}

		return getSilhouetteIndex(localPoints, remotePoints);
	}

	private double getSilhouetteIndex(final List<Point> localPoints, final List<Point> remotePoints) {
		final double a = getAveragePointsToPointsLocalDistance(localPoints, localPoints);
		final double b = getAveragePointsToPointsRemoteDistance(localPoints, remotePoints);
		final double norm = Math.max(a, b);
		return (b - a) / norm;
	}

	private double getAveragePointsToPointsLocalDistance(final List<Point> sourcePoints, final List<Point> targetPoints) {
		final List<Double> distances = new ArrayList<>();

		sourcePoints
			.forEach((sourcePoint) -> targetPoints.forEach((targetPoint) -> distances.add(neighboursService.getLocationsDistance(sourcePoint, targetPoint))));

		return distances.stream().mapToDouble(distance -> distance).average().orElse(Double.POSITIVE_INFINITY);
	}

	private double getAveragePointsToPointsRemoteDistance(final List<Point> sourcePoints, final List<Point> targetPoints) {
		final List<Double> distances = new ArrayList<>();
		final List<Double> pointDistances = new ArrayList<>();

		sourcePoints.forEach((sourcePoint) -> {
			targetPoints.forEach((targetPoint) -> {

				pointDistances.add(neighboursService.getLocationsDistance(sourcePoint, targetPoint));
			});

			distances.add(pointDistances.stream().mapToDouble(distance -> distance).average().orElse(Double.POSITIVE_INFINITY));
		});

		return distances.stream().mapToDouble(distance -> distance).min().orElse(Double.POSITIVE_INFINITY);
	}
}
