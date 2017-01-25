package ml.cluster.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ml.cluster.comparator.ReachabilityDistanceComparator;
import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.optics.impl.OpticsImpl;
import ml.cluster.datastructure.segment.Segment;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

	private final PriorityQueue<Point> nearestNeighboursQueue;

	@Autowired
	private OpticsNeighboursService opticsNeighboursService;

	public OpticsServiceImpl() {
		nearestNeighboursQueue = new PriorityQueue<>(new ReachabilityDistanceComparator());
	}

	@Override
	public List<Point> getOrderingPoints(final Set<Segment> segmentMatrices) {
		Validate.notEmpty(segmentMatrices, "Segments are not defined");

		final long radius = getRadius(segmentMatrices);

		final OpticsImpl optics = new OpticsImpl.OpticsBuilder().radius(radius).build();
		processSegmentMatrices(segmentMatrices, optics);

		return Collections.unmodifiableList(optics.getOrderedLocationPoints());
	}

	private long getRadius(final Set<Segment> segmentMatrices) {
		final FixedRadiusMatrix matrix = getMatrix(segmentMatrices.iterator().next());
		return matrix.getRadius();
	}

	private void processSegmentMatrices(final Set<Segment> segmentMatrices, final OpticsImpl optics) {
		segmentMatrices.forEach((segment) -> {
			final FixedRadiusMatrix matrix = getMatrix(segment);
			processMatrixCells(matrix, optics);
		});
	}

	private FixedRadiusMatrix getMatrix(final Segment segment) {
		return Validate.notNull(segment.getMatrix(), "Segment matrix is not defined");
	}

	private void processMatrixCells(final FixedRadiusMatrix matrix, final OpticsImpl optics) {
		final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getCells();
		cells.forEach((position, cell) -> {
			final List<Point> locationPoints = cell.getLocationPoints();
			processCellLocationPoints(locationPoints, matrix, optics);
		});
	}

	private void processCellLocationPoints(final List<Point> locationPoints, final FixedRadiusMatrix matrix, final OpticsImpl optics) {

		for (final Point currentLocationPoint : locationPoints) {

			final List<Point> nearestNeighbours = opticsNeighboursService.getNearestNeighbours(currentLocationPoint, matrix);
			if (nearestNeighbours.isEmpty()) {
				saveProcessedLocationPoint(currentLocationPoint, optics);
				continue;
			}

			processCellLocationPoint(currentLocationPoint, nearestNeighbours, optics);
			updateNeighboursInfo(currentLocationPoint, nearestNeighbours);
			processNeighbours(matrix, optics);
		}
	}

	private void processCellLocationPoint(final Point currentLocationPoint, final List<Point> nearestNeighbours, final OpticsImpl optics) {
		final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

		if (coreDistance < Double.POSITIVE_INFINITY) {
			currentLocationPoint.setCoreDistance(coreDistance);
			saveProcessedLocationPoint(currentLocationPoint, optics);
		}

	}

	private void updateNeighboursInfo(final Point coreLocationPoint, final List<Point> nearestNeighbours) {
		nearestNeighbours.forEach(currentLocationPoint -> {
			if (!currentLocationPoint.isProcessed()) {
				updateNeighbourInfo(coreLocationPoint, currentLocationPoint);
			}
		});
	}

	private void updateNeighbourInfo(final Point coreLocationPoint, final Point currentLocationPoint) {
		final double neighbourReachabilityDistance = opticsNeighboursService.getNeighbourReachabilityDistance(coreLocationPoint, currentLocationPoint);

		if (currentLocationPoint.getReachabilityDistance() == Double.POSITIVE_INFINITY) {
			currentLocationPoint.setReachabilityDistance(neighbourReachabilityDistance);
			nearestNeighboursQueue.add(currentLocationPoint);

		} else if (neighbourReachabilityDistance < currentLocationPoint.getReachabilityDistance()) {
			currentLocationPoint.setReachabilityDistance(neighbourReachabilityDistance);
		}
	}

	private void processNeighbours(final FixedRadiusMatrix matrix, final OpticsImpl optics) {

		while (true) {
			final Point currentLocationPoint = nearestNeighboursQueue.poll();

			if (currentLocationPoint == null) {
				break;
			}

			final List<Point> nearestNeighbours = opticsNeighboursService.getNearestNeighbours(currentLocationPoint, matrix);
			final double coreDistance = opticsNeighboursService.getCoreDistance(currentLocationPoint, nearestNeighbours, optics.getMinPts());

			currentLocationPoint.setCoreDistance(coreDistance);
			saveProcessedLocationPoint(currentLocationPoint, optics);

			if (coreDistance < Double.POSITIVE_INFINITY) {
				updateNeighboursInfo(currentLocationPoint, nearestNeighbours);
			}
		}
	}

	private void saveProcessedLocationPoint(final Point currentLocationPoint, final OpticsImpl optics) {
		if (!currentLocationPoint.isProcessed()) {
			currentLocationPoint.setProcessed(true);
			optics.addToOrderedLocationPoints(currentLocationPoint);
		}
	}
}