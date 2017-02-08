package ml.cluster.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.optics.impl.PointImpl;
import ml.cluster.service.OpticsNeighboursService;

@Service("neighboursService")
public class OpticsNeighboursServiceImpl implements OpticsNeighboursService {

	protected List<Point> getNeighboringLocationPoints(final Set<Pair<Long, Long>> neighboringCells, final FixedRadiusMatrix matrix) {
		return Collections.unmodifiableList(
			matrix.getCells().entrySet().stream().filter(map -> neighboringCells.contains(map.getKey())).map(Map.Entry::getValue).collect(Collectors.toList())
				.stream().map(MatrixCell::getLocationPoints).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()));
	}

	protected List<Point> getNearestNeighbours(final Point currentLocationPoint, final List<Point> neighboringLocationPoints, final long radius) {
		final List<Point> nearestNeighbours = new ArrayList<>();

		neighboringLocationPoints.forEach(neighboringLocationPoint -> {

			final double reachabilityDistance = getReachabilityDistance(currentLocationPoint, neighboringLocationPoint, radius);
			if (reachabilityDistance <= radius) {
				nearestNeighbours.add(neighboringLocationPoint);
			}
		});

		return Collections.unmodifiableList(nearestNeighbours);
	}

	@Override
	public double getCoreDistance(final Point currentLocationPoint, final List<Point> nearestLocationPoints, final int minPts) {
		Validate.notNull(currentLocationPoint, "Current location point is not defined");
		Validate.notEmpty(nearestLocationPoints, "Nearest location point are not defined");

		if (nearestLocationPoints.size() < minPts) {
			return Double.POSITIVE_INFINITY;

		} else {
			return getMinPtsReachabilityDistance(currentLocationPoint, nearestLocationPoints, minPts);
		}
	}

	private double getMinPtsReachabilityDistance(final Point currentLocationPoint, final List<Point> nearestLocationsPoints, final int minPts) {
		final List<Double> distances = new ArrayList<>();

		nearestLocationsPoints.forEach(nearestLocation -> {
			final double reachabilityDistance = getReachabilityDistance(currentLocationPoint, nearestLocation, minPts);
			distances.add(reachabilityDistance);
		});

		distances.sort(Double::compareTo);
		return distances.get(minPts - 1);
	}

	private double getReachabilityDistance(final Point currentLocationPoint, final Point neighboringLocationPoints, final long radius) {
		final double distance = getLocationsDistance(currentLocationPoint, neighboringLocationPoints);
		if (isDirectlyDensityReachable(distance, radius)) {
			return distance;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

	@Override
	public double getLocationsDistance(final Point currentLocationPoint, final Point neighboringLocationPoint) {
		Validate.notNull(currentLocationPoint, "Current location point is not defined");
		Validate.notNull(neighboringLocationPoint, "Neighboring location point is not defined");

		final double x = Validate.notNull(currentLocationPoint.getX(), "Current location point X is not defined");
		final double y = Validate.notNull(currentLocationPoint.getY(), "Current location point Y is not defined");
		final double x1 = Validate.notNull(neighboringLocationPoint.getX(), "Neighboring location point X is not defined");
		final double y1 = Validate.notNull(neighboringLocationPoint.getY(), "Neighboring location point Y is not defined");

		return Math.hypot(x - x1, y - y1);
	}

	private boolean isDirectlyDensityReachable(final double distance, final long radius) {
		return distance <= radius;
	}

	@Override
	public double getNeighbourReachabilityDistance(final Point currentLocationPoint, final Point neighbourLocationPoint) {
		Validate.notNull(currentLocationPoint, "Current location point is not defined");
		Validate.notNull(neighbourLocationPoint, "Neighbour location point is not defined");
		return Math.max(currentLocationPoint.getCoreDistance(), getLocationsDistance(currentLocationPoint, neighbourLocationPoint));
	}

	@Override
	public List<Point> getNearestNeighbours(final Point currentLocationPoint, final FixedRadiusMatrix matrix) {
		Validate.notNull(currentLocationPoint, "Current location point is not defined");
		Validate.notNull(matrix, "Fixed radius matrix is not defined");

		final Pair<Long, Long> currentCellPosition = ((PointImpl)currentLocationPoint).getCell();
		final MatrixCell currentCell = matrix.getCells().get(currentCellPosition);

		final List<Point> neighboringLocationPoints = getNeighboringLocationPoints(currentCell.getNeighboringCells(), matrix);
		final List<Point> neighbourhood = new ArrayList<>(currentCell.getLocationPoints());
		neighbourhood.addAll(neighboringLocationPoints);

		return getNearestNeighbours(currentLocationPoint, neighbourhood, matrix.getRadius());
	}
}
