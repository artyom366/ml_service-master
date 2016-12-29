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
			processCellLocationPoints(matrix, cell, optics);
		});
	}

	private void processCellLocationPoints(final FixedRadiusMatrix matrix, final MatrixCell cell, final Optics optics) {

		final List<Point> currentCellLocationPoints = cell.getLocations();
		if (currentCellLocationPoints.isEmpty()) {
			return;
		}

		final List<Point> neighboringLocationPoints = opticsNeighboursService.getNeighboringLocationPoints(cell.getNeighboringCells(), matrix);

		currentCellLocationPoints.forEach(currentLocationPoint -> {

			final boolean isCorePoint = isCorePoint(currentLocationPoint, neighboringLocationPoints, matrix, optics);
			if (isCorePoint) {
				addToCluster(matrix, cell, optics);
			}
		});
	}

	private boolean isCorePoint(final Point currentLocationPoint, final List<Point> neighboringLocationPoints, final FixedRadiusMatrix matrix, final Optics optics) {

		final List<Point> nearestNeighbours = opticsNeighboursService.getNearestNeighbours(currentLocationPoint, neighboringLocationPoints, matrix.getRadius());
		final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

		currentLocationPoint.setCoreDistance(coreDistance);
		currentLocationPoint.setProcessed(true);

		optics.addToOrderedLocationPoints(currentLocationPoint);

		final boolean isCorePoint = currentLocationPoint.getCoreDistance() != Double.NaN;
		if (isCorePoint) {
			updateClusterInfo(currentLocationPoint, nearestNeighbours);
		}

		return isCorePoint;
	}

    private void updateClusterInfo(final Point currentLocationPoint, final List<Point> nearestNeighbours) {

        nearestNeighbours.forEach(neighbour -> {

			if (!neighbour.isProcessed()) {
				final double neighbourReachabilityDistance = opticsNeighboursService.getNeighbourReachabilityDistance(currentLocationPoint, neighbour);

				if (neighbour.getReachabilityDistance() == Double.POSITIVE_INFINITY) {
					neighbour.setReachabilityDistance(neighbourReachabilityDistance);
					nearestNeighboursQueue.add(neighbour);

				} else {
					if (neighbour.getReachabilityDistance() > neighbourReachabilityDistance) {
						neighbour.setReachabilityDistance(neighbourReachabilityDistance);
					}
				}
			}
        });
    }

    private void addToCluster(final FixedRadiusMatrix matrix, final MatrixCell cell, final Optics optics) {

		final List<Point> neighboringLocationPoints = opticsNeighboursService.getNeighboringLocationPoints(cell.getNeighboringCells(), matrix);

        while (true) {
			final Point centerPoint = nearestNeighboursQueue.poll();

			if (centerPoint == null) {
				break;
			}

			isCorePoint(centerPoint, neighboringLocationPoints, matrix, optics);
		}
    }
}