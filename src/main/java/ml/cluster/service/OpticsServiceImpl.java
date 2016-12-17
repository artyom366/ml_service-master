package ml.cluster.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.matrix.PickSegment;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

	@Override
	public void getOrderingPoints(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
		Validate.notEmpty(pickSegments, "Pick locations are not defined");

		final Optics optics = new Optics.OpticsBuilder().minPts(5).build();
		processSegmentMatrices(pickSegments, optics);
	}

	private void processSegmentMatrices(final Map<PickSegment, List<PickLocationViewDO>> pickSegments, final Optics optics) {
		pickSegments.forEach((segment, locations) -> {
			final FixedRadiusMatrix matrix = segment.getMatrix();
			processMatrixCells(matrix, optics);
		});
	}

	private void processMatrixCells(final FixedRadiusMatrix matrix, final Optics optics) {

		final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getSegmentPickCells();
		cells.forEach((position, cell) -> {
			processCellLocations(matrix, cell, optics);
		});
	}

	private void processCellLocations(final FixedRadiusMatrix matrix, final MatrixCell cell, final Optics optics) {

		final List<PickLocationViewDO> currentCellLocations = cell.getLocations();
		if (currentCellLocations.isEmpty()) {
			return;
		}

		final Set<Pair<Long, Long>> neighboringCells = cell.getNeighboringCells();
		final List<PickLocationViewDO> neighboringLocations = getNeighboringLocations(neighboringCells, matrix);

		currentCellLocations.forEach(currentLocation -> {

			final double coreDistance = getCurrentLocationCoreDistance(currentLocation, neighboringLocations, optics.getMinPts(), matrix.getRadius());

			final Point point = new Point(currentLocation);
			point.setCoreDistance(coreDistance);
			point.setProcessed(true);

		});
	}

	private List<PickLocationViewDO> getNeighboringLocations(final Set<Pair<Long, Long>> neighboringCells, final FixedRadiusMatrix matrix) {
		return matrix.getSegmentPickCells().entrySet().stream().filter(map -> neighboringCells.contains(map.getKey())).map(Map.Entry::getValue)
			.collect(Collectors.toList()).stream().map(MatrixCell::getLocations).collect(Collectors.toList()).stream().flatMap(List::stream)
			.collect(Collectors.toList());
	}

	private double getCurrentLocationCoreDistance(final PickLocationViewDO currentLocation, final List<PickLocationViewDO> neighboringLocations,
			final int minPts, final long radius) {

		if (neighboringLocations.size() < minPts) {
			return Double.NaN;
		}

		return getCoreDistance(currentLocation, neighboringLocations, minPts, radius);
	}

	private double getCoreDistance(final PickLocationViewDO currentLocation, final List<PickLocationViewDO> neighboringLocations, final int minPts,
			final long radius) {

		final List<Double> reachabilityDistances = new ArrayList<>();

		neighboringLocations.forEach(neighboringLocation -> {

			final double reachabilityDistance = getReachabilityDistance(currentLocation, neighboringLocation, radius);
			if (reachabilityDistance <= radius) {
				reachabilityDistances.add(reachabilityDistance);
			}

		});

		if (reachabilityDistances.size() < minPts - 1) {
			return Double.NaN;

		} else {
			reachabilityDistances.sort(Double::compareTo);
			return reachabilityDistances.get(minPts - 1);
		}
	}

	private double getReachabilityDistance(final PickLocationViewDO currentLocation, final PickLocationViewDO neighboringLocation, final long radius) {

		final double distance = getLocationsDistance(currentLocation, neighboringLocation);
		if (isDirectlyDensityReachable(distance, radius)) {
			return distance;
		} else {
			return Double.NaN;
		}
	}

	private double getLocationsDistance(final PickLocationViewDO currentLocation, final PickLocationViewDO neighboringLocation) {
		final double x = currentLocation.getX();
		final double y = currentLocation.getY();
		final double x1 = neighboringLocation.getX();
		final double y1 = neighboringLocation.getY();
		return Math.hypot(x - x1, y - y1);
	}

	private boolean isDirectlyDensityReachable(final double distance, final long radius) {
		return distance <= radius;
	}

}