package ml.cluster.service;

import ml.cluster.comparator.ReachabilityDistanceComparator;
import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.PickSegment;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

    private final PriorityQueue<Point> queue;

	@Autowired
	private OpticsNeighboursService opticsNeighboursService;

    public OpticsServiceImpl() {
        queue = new PriorityQueue<>(new ReachabilityDistanceComparator());
    }

    @Override
	public void getOrderingPoints(final Set<PickSegment> pickSegmentMatrices) {
		Validate.notEmpty(pickSegmentMatrices, "Pick locations are not defined");

		final Optics optics = new Optics.OpticsBuilder().minPts(5).build();
		processSegmentMatrices(pickSegmentMatrices, optics);
	}

	private void processSegmentMatrices(final Set<PickSegment> pickSegmentMatrices, final Optics optics) {
		pickSegmentMatrices.forEach((segment) -> {
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

		final List<PickLocationViewDO> neighboringLocations = opticsNeighboursService.getNeighboringLocations(cell.getNeighboringCells(), matrix);

		currentCellLocations.forEach(currentLocation -> {

			final Point centerPoint = new Point(currentLocation);
			final boolean isCorePoint = isCorePoint(centerPoint, neighboringLocations, matrix, optics);
			if (isCorePoint) {
				addToCluster(matrix, cell, optics);
			}
		});
	}

	private boolean isCorePoint(final Point centerPoint, final List<PickLocationViewDO> neighboringLocations, final FixedRadiusMatrix matrix, final Optics optics) {

		final List<Point> nearestNeighbours = opticsNeighboursService.getNearestNeighbours(centerPoint, neighboringLocations, matrix.getRadius());
		final double coreDistance = opticsNeighboursService.getCoreDistance(nearestNeighbours, optics.getMinPts());

		centerPoint.setCoreDistance(coreDistance);
		centerPoint.setProcessed(true);

		optics.addToOrderedLocations(centerPoint);

		final boolean isCorePoint = centerPoint.getCoreDistance() != Double.NaN;
		if (isCorePoint) {
			updateClusterInfo(centerPoint, nearestNeighbours, optics);
		}

		return isCorePoint;
	}

    private void updateClusterInfo(final Point centerPoint, final List<Point> nearestNeighbours, final Optics optics) {

        nearestNeighbours.forEach(neighbour -> {
            final double neighbourReachabilityDistance = opticsNeighboursService.getNeighbourReachabilityDistance(centerPoint, neighbour);

            if (neighbour.getReachabilityDistance() == Double.POSITIVE_INFINITY) {
                neighbour.setReachabilityDistance(neighbourReachabilityDistance);
                queue.add(neighbour);

            } else {
                if (neighbour.getReachabilityDistance() > neighbourReachabilityDistance) {
                    neighbour.setReachabilityDistance(neighbourReachabilityDistance);
                }
            }
        });
    }

    private void addToCluster(final FixedRadiusMatrix matrix, final MatrixCell cell, final Optics optics) {

		final List<PickLocationViewDO> neighboringLocations = opticsNeighboursService.getNeighboringLocations(cell.getNeighboringCells(), matrix);

        while (true) {
			final Point centerPoint = queue.poll();

			if (centerPoint == null) {
				break;
			}

			isCorePoint(centerPoint, neighboringLocations, matrix, optics);
		}
    }
}