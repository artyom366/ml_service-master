package ml.cluster.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.OpticsException;
import ml.cluster.error.index.ClusterIndexException;
import ml.cluster.error.index.DunnIndexException;
import ml.cluster.error.index.SilhouetteIndexException;
import ml.cluster.service.ClusterValidatorService;
import ml.cluster.service.OpticsNeighboursService;

@Service("clusterValidatorService")
public class ClusterValidatorServiceImpl implements ClusterValidatorService {

	@Autowired
	private OpticsNeighboursService neighboursService;

	@Override
	public double getSilhouetteIndex(final Cluster local, final Cluster remote) throws OpticsException {
		Validate.notNull(local, "Local cluster is not defined");
		Validate.notNull(remote, "Remote cluster is not defined");

		final List<Point> localPoints = Validate.notEmpty(local.getClusterPoints(), "Local cluster points are not defined");
		final List<Point> remotePoints = Validate.notEmpty(remote.getClusterPoints(), "Remote cluster points are not defined");

		final double silhouetteIndex = getSilhouetteIndex(localPoints, remotePoints);
		validateSilhouetteIndex(silhouetteIndex);

		return silhouetteIndex;
	}

	private double getSilhouetteIndex(final List<Point> localPoints, final List<Point> remotePoints) {
		final double a = getAveragePointsToPointsLocalDistance(localPoints, localPoints);
		final double b = getMinimalAveragePointsToPointsRemoteDistance(localPoints, remotePoints);
		final double norm = Math.max(a, b);
		return (b - a) / norm;
	}

	private double getAveragePointsToPointsLocalDistance(final List<Point> sourcePoints, final List<Point> targetPoints) {
		final List<Double> inClusterPointDistances = new ArrayList<>();

		sourcePoints.forEach((sourcePoint) -> targetPoints
			.forEach((targetPoint) -> inClusterPointDistances.add(neighboursService.getLocationsDistance(sourcePoint, targetPoint))));

		return inClusterPointDistances.stream().mapToDouble(distance -> distance).average().orElse(Double.POSITIVE_INFINITY);
	}

	private double getMinimalAveragePointsToPointsRemoteDistance(final List<Point> sourcePoints, final List<Point> targetPoints) {
		final List<Double> betweenClusterPointDistances = new ArrayList<>();
		final List<Double> pointToPointsBetweenClusterAverageDistances = new ArrayList<>();

		sourcePoints.forEach((sourcePoint) -> {
			targetPoints.forEach((targetPoint) -> {

				betweenClusterPointDistances.add(neighboursService.getLocationsDistance(sourcePoint, targetPoint));
			});

			pointToPointsBetweenClusterAverageDistances
				.add(betweenClusterPointDistances.stream().mapToDouble(distance -> distance).average().orElse(Double.POSITIVE_INFINITY));
		});

		return pointToPointsBetweenClusterAverageDistances.stream().mapToDouble(distance -> distance).min().orElse(Double.POSITIVE_INFINITY);
	}

	private void validateSilhouetteIndex(final double index) throws OpticsException {
		verifyThatIndexValueIsNumber(index);

		if (index > 1 && index < 0) {
			throw new SilhouetteIndexException(String.format("Silhouette index: %s is not valid, should be a decimal between 0 and 1 inclusive", index));
		}
	}

	@Override
	public double getDunnIndex(final Cluster local, final Cluster remote) throws OpticsException {
		Validate.notNull(local, "Local cluster is not defined");
		Validate.notNull(remote, "Remote cluster is not defined");

		final List<Point> localPoints = Validate.notEmpty(local.getClusterPoints(), "Local cluster points are not defined");
		final List<Point> remotePoints = Validate.notEmpty(remote.getClusterPoints(), "Remote cluster points are not defined");

		final double dunnIndex = getDunnIndex(localPoints, remotePoints);
		validateDunnIndex(dunnIndex);

		return dunnIndex;
	}

	private double getDunnIndex(final List<Point> localPoints, final List<Point> remotePoints) {

		final double betweenClusterMinimalDistance = getBetweenClusterMinimalDistance(localPoints, remotePoints);
		final double localClusterDiameter = getClusterDiameter(localPoints);
		return betweenClusterMinimalDistance / localClusterDiameter;
	}

	private double getBetweenClusterMinimalDistance(final List<Point> localPoints, final List<Point> remotePoints) {
		final List<Double> betweenClusterPointDistances = new ArrayList<>();

		localPoints.forEach((localPoint) -> remotePoints
			.forEach((remotePoint) -> betweenClusterPointDistances.add(neighboursService.getLocationsDistance(localPoint, remotePoint))));

		return betweenClusterPointDistances.stream().mapToDouble(distance -> distance).min().orElse(Double.POSITIVE_INFINITY);
	}

	private double getClusterDiameter(final List<Point> points) {
		final List<Double> inClusterPointDistances = new ArrayList<>();

		points.forEach(
			(sourcePoint) -> points.forEach((targetPoint) -> inClusterPointDistances.add(neighboursService.getLocationsDistance(sourcePoint, targetPoint))));

		return inClusterPointDistances.stream().mapToDouble(distance -> distance).max().orElse(Double.POSITIVE_INFINITY);
	}

	private void validateDunnIndex(final double index) throws OpticsException {
		verifyThatIndexValueIsNumber(index);

		if (index < 0) {
			throw new DunnIndexException(String.format("Dunn index: %s is not valid, should be a positive value", index));
		}
	}

	private void verifyThatIndexValueIsNumber(final double index) throws OpticsException {
		if (Double.isInfinite(index) && Double.isNaN(index)) {
			throw new ClusterIndexException(String.format("Index value: %s is representing an undefined or unrepresentable value", index));
		}
	}

}
