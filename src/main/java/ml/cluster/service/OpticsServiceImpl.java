package ml.cluster.service;

import ml.cluster.comparator.ReachabilityDistanceComparator;
import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

    private final PriorityQueue<OpticsPoint> nearestNeighboursQueue;

	@Autowired
	private OpticsNeighboursService opticsNeighboursService;

    public OpticsServiceImpl() {
        nearestNeighboursQueue = new PriorityQueue<>(new ReachabilityDistanceComparator());
    }

    @Override
	public List<OpticsPoint> getOrderingPoints(final Set<Segment> segmentMatrices) {
		Validate.notEmpty(segmentMatrices, "Segments are not defined");

		final Optics optics = new Optics.OpticsBuilder().build();
		processSegmentMatrices(segmentMatrices, optics);

		return Collections.unmodifiableList(optics.getOrderedLocationPoints());
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
			final List<OpticsPoint> locationPoints = cell.getLocationPoints();
			processCellLocationPoints(locationPoints, matrix, optics);
		});
	}

	private void processCellLocationPoints(final List<OpticsPoint> locationPoints, final FixedRadiusMatrix matrix, final Optics optics) {

		for (final OpticsPoint currentLocationPoint : locationPoints) {

			final List<OpticsPoint> nearestNeighbours = opticsNeighboursService.getNearestNeighbours(currentLocationPoint, matrix);
			if (nearestNeighbours.isEmpty()) {
				saveProcessedLocationPoint(currentLocationPoint, optics);
				continue;
			}

			processCellLocationPoint(currentLocationPoint, nearestNeighbours, optics);
			updateNeighboursInfo(currentLocationPoint, nearestNeighbours);
			processNeighbours(matrix, optics);
		}
	}

	private void processCellLocationPoint(final OpticsPoint currentLocationPoint, final List<OpticsPoint> nearestNeighbours, final Optics optics) {
		final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

		if (coreDistance < Double.POSITIVE_INFINITY) {
			currentLocationPoint.setCoreDistance(coreDistance);
			saveProcessedLocationPoint(currentLocationPoint, optics);
		}

	}

	private void updateNeighboursInfo(final OpticsPoint coreLocationPoint, final List<OpticsPoint> nearestNeighbours) {
		nearestNeighbours.forEach(currentLocationPoint -> {
			if (!currentLocationPoint.isProcessed()) {
				updateNeighbourInfo(coreLocationPoint, currentLocationPoint);
			}
		});
	}

	private void updateNeighbourInfo(final OpticsPoint coreLocationPoint, final OpticsPoint currentLocationPoint) {
		final double neighbourReachabilityDistance = opticsNeighboursService.getNeighbourReachabilityDistance(coreLocationPoint, currentLocationPoint);

		if (currentLocationPoint.getReachabilityDistance() == Double.POSITIVE_INFINITY) {
			currentLocationPoint.setReachabilityDistance(neighbourReachabilityDistance);
			nearestNeighboursQueue.add(currentLocationPoint);

		} else if (neighbourReachabilityDistance < currentLocationPoint.getReachabilityDistance()) {
			currentLocationPoint.setReachabilityDistance(neighbourReachabilityDistance);
		}
	}

	private void processNeighbours(final FixedRadiusMatrix matrix, final Optics optics) {

		while (true) {
			final OpticsPoint currentLocationPoint = nearestNeighboursQueue.poll();

			if (currentLocationPoint == null) {
				break;
			}

			final List<OpticsPoint> nearestNeighbours = opticsNeighboursService.getNearestNeighbours(currentLocationPoint, matrix);
			final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

			currentLocationPoint.setCoreDistance(coreDistance);
			saveProcessedLocationPoint(currentLocationPoint, optics);

			if (coreDistance < Double.POSITIVE_INFINITY) {
				updateNeighboursInfo(currentLocationPoint, nearestNeighbours);
			}
		}
	}

	private void saveProcessedLocationPoint(final OpticsPoint currentLocationPoint, final Optics optics) {
		if (!currentLocationPoint.isProcessed()) {
			currentLocationPoint.setProcessed(true);
			optics.addToOrderedLocationPoints(currentLocationPoint);
		}
	}
}