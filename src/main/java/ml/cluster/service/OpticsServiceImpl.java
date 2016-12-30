package ml.cluster.service;

import ml.cluster.comparator.ReachabilityDistanceComparator;
import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

    private final PriorityQueue<Point> nearestNeighboursQueue;

	@Autowired
	private OpticsNeighboursService opticsNeighboursService;

    public OpticsServiceImpl() {
        nearestNeighboursQueue = new PriorityQueue<>(new ReachabilityDistanceComparator());
    }

    @Override
	public void getOrderingPoints(final Set<Segment> segmentMatrices) {
		Validate.notEmpty(segmentMatrices, "Segments are not defined");

		final Optics optics = new Optics.OpticsBuilder().minPts(5).build();
		processSegmentMatrices(segmentMatrices, optics);
	}

	private void processSegmentMatrices(final Set<Segment> segmentMatrices, final Optics optics) {
		segmentMatrices.forEach((segment) -> {
			final FixedRadiusMatrix matrix = segment.getMatrix();
			processMatrixCells(matrix, optics);
		});
	}

	private void processMatrixCells(final FixedRadiusMatrix matrix, final Optics optics) {
		final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getCells();
		cells.forEach((position, cell) -> {
			final List<Point> locationPoints = cell.getLocationPoints();
			processCellLocationPoints(locationPoints, matrix, optics);
		});
	}

	private void processCellLocationPoints(final List<Point> locationPoints, final FixedRadiusMatrix matrix, final Optics optics) {

		for (final Point currentLocationPoint : locationPoints) {

			final List<Point> nearestNeighbours = getNearestNeighbours(currentLocationPoint, matrix);
			if (nearestNeighbours.isEmpty()) {
				currentLocationPoint.setProcessed(true);
				optics.addToOrderedLocationPoints(currentLocationPoint);
			}

			final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

			if (coreDistance < Double.POSITIVE_INFINITY) {
				currentLocationPoint.setCoreDistance(coreDistance);

				if (!currentLocationPoint.isProcessed()) {
					currentLocationPoint.setProcessed(true);
					optics.addToOrderedLocationPoints(currentLocationPoint);
				}
			}

			updateNeighboursInfo(currentLocationPoint, nearestNeighbours);
			processNeighbours(matrix, optics);
		}
	}

	private void updateNeighboursInfo(final Point coreLocationPoint, final List<Point> nearestNeighbours) {

		nearestNeighbours.forEach(currentLocationPoint -> {
			if (!currentLocationPoint.isProcessed()) {

				final double neighbourReachabilityDistance = opticsNeighboursService.getNeighbourReachabilityDistance(coreLocationPoint, currentLocationPoint);

				if (currentLocationPoint.getReachabilityDistance() == Double.POSITIVE_INFINITY) {
					currentLocationPoint.setReachabilityDistance(neighbourReachabilityDistance);
					nearestNeighboursQueue.add(currentLocationPoint);

				} else if (neighbourReachabilityDistance < currentLocationPoint.getReachabilityDistance()) {
					currentLocationPoint.setReachabilityDistance(neighbourReachabilityDistance);
				}
			}
		});

	}

	private void processNeighbours(final FixedRadiusMatrix matrix, final Optics optics) {

		while (true) {

			final Point currentLocationPoint = nearestNeighboursQueue.poll();

			if (currentLocationPoint == null) {
				break;
			}

			final List<Point> nearestNeighbours = getNearestNeighbours(currentLocationPoint, matrix);
			final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

			currentLocationPoint.setCoreDistance(coreDistance);

			if (!currentLocationPoint.isProcessed()) {
				currentLocationPoint.setProcessed(true);
				optics.addToOrderedLocationPoints(currentLocationPoint);
			}

			if (coreDistance < Double.POSITIVE_INFINITY) {
				updateNeighboursInfo(currentLocationPoint, nearestNeighbours);
			}
		}
	}

	private List<Point> getNearestNeighbours(final Point currentLocationPoint, final FixedRadiusMatrix matrix) {
		final Pair<Long, Long> currentCellPosition = currentLocationPoint.getCell();
		final MatrixCell currentCell = matrix.getCells().get(currentCellPosition);
		final List<Point> neighboringLocationPoints = opticsNeighboursService.getNeighboringLocationPoints(currentCell.getNeighboringCells(), matrix);
		return opticsNeighboursService.getNearestNeighbours(currentLocationPoint, neighboringLocationPoints, matrix.getRadius());
	}
}