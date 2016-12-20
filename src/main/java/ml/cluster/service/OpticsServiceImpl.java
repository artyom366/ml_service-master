package ml.cluster.service;

import ml.cluster.comparator.CoreDistanceComparator;
import ml.cluster.comparator.ReachabilityDistanceComparator;
import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.PickSegment;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

    private final PriorityQueue<Point> queue;

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

		final List<PickLocationViewDO> neighboringLocations = getNeighboringLocations(cell.getNeighboringCells(), matrix);

		currentCellLocations.forEach(currentLocation -> {
			final boolean isCorePoint = isCorePoint(matrix, optics, neighboringLocations, currentLocation);
			if (isCorePoint) {
				addToCluster(matrix, cell, optics);
			}
		});


	}

	private boolean processNextNeighboringPoint(final FixedRadiusMatrix matrix, final Optics optics, final List<PickLocationViewDO> neighboringLocations, final Point currentPoint) {
		return isCorePoint(matrix, optics, neighboringLocations, currentPoint.getLocation());
	}

	private boolean isCorePoint(final FixedRadiusMatrix matrix, final Optics optics, final List<PickLocationViewDO> neighboringLocations, final PickLocationViewDO currentLocation) {
		final List<Point> nearestNeighbours = getNearestNeighbours(currentLocation, neighboringLocations, matrix.getRadius());
		final double coreDistance = getCoreDistance(nearestNeighbours, optics.getMinPts());


		final Point centerPoint = new Point(currentLocation);
		centerPoint.setCoreDistance(coreDistance);
		centerPoint.setProcessed(true);

		optics.addToOrderedLocations(centerPoint);

		return isCorePoint(optics, nearestNeighbours, centerPoint);
	}

	private boolean isCorePoint(final Optics optics, final List<Point> nearestNeighbours, final Point centerPoint) {
		final boolean isCoreObject = centerPoint.getCoreDistance() != Double.NaN;

		if (isCoreObject) {
            updateClusterInfo(centerPoint, nearestNeighbours, optics);

        }

		return isCoreObject;
	}

	protected List<PickLocationViewDO> getNeighboringLocations(final Set<Pair<Long, Long>> neighboringCells, final FixedRadiusMatrix matrix) {
		return matrix.getSegmentPickCells().entrySet().stream().filter(map -> neighboringCells.contains(map.getKey())).map(Map.Entry::getValue)
			.collect(Collectors.toList()).stream().map(MatrixCell::getLocations).collect(Collectors.toList()).stream().flatMap(List::stream)
			.collect(Collectors.toList());
	}

	//todo separate nn to separate class
    private List<Point> getNearestNeighbours(final Point currentLocation, final List<PickLocationViewDO> neighboringLocations, final long radius) {
        return getNearestNeighbours(currentLocation.getLocation(), neighboringLocations, radius);
    }

	protected List<Point> getNearestNeighbours(final PickLocationViewDO currentLocation, final List<PickLocationViewDO> neighboringLocations, final long radius) {

		final List<Point> nearestNeighbours = new ArrayList<>();

		neighboringLocations.forEach(neighboringLocation -> {

			final double reachabilityDistance = getReachabilityDistance(currentLocation, neighboringLocation, radius);
			if (reachabilityDistance <= radius) {
                final Point point = new Point(neighboringLocation);
                nearestNeighbours.add(point);
			}

		});

		return nearestNeighbours;
	}

	private double getReachabilityDistance(final PickLocationViewDO currentLocation, final PickLocationViewDO neighboringLocation, final long radius) {

		final double distance = getLocationsDistance(currentLocation, neighboringLocation);
		if (isDirectlyDensityReachable(distance, radius)) {
			return distance;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}


	private double getLocationsDistance(final PickLocationViewDO currentLocation, final PickLocationViewDO neighboringLocation) {
		final double x = currentLocation.getX();
		final double y = currentLocation.getY();
		final double x1 = neighboringLocation.getX();
		final double y1 = neighboringLocation.getY();
		return Math.hypot(x - x1, y - y1);
	}

    private double getLocationsDistance(final Point currentLocation, final Point neighboringLocation) {
        return getLocationsDistance(currentLocation.getLocation(), neighboringLocation.getLocation());
	}

	private boolean isDirectlyDensityReachable(final double distance, final long radius) {
		return distance <= radius;
	}

    protected double getCoreDistance(final List<Point> nearestLocations, final int minPts) {
        if (nearestLocations.size() < minPts - 1) {
            return Double.NaN;

        } else {

            nearestLocations.sort(new CoreDistanceComparator());
            return nearestLocations.get(minPts - 1).getCoreDistance();
        }
    }

    private void updateClusterInfo(final Point centerPoint, final List<Point> nearestNeighbours, final Optics optics) {

        nearestNeighbours.forEach(neighbour -> {
            final double neighbourReachabilityDistance = getNeighbourReachabilityDistance(centerPoint, neighbour);

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

    private double getNeighbourReachabilityDistance(final Point centerPoint, final Point neighbourPoint) {
        return Math.max(centerPoint.getCoreDistance(), getLocationsDistance(centerPoint, neighbourPoint));
    }

    private void addToCluster(final FixedRadiusMatrix matrix, final MatrixCell cell, final Optics optics) {

		final List<PickLocationViewDO> neighboringLocations = getNeighboringLocations(cell.getNeighboringCells(), matrix);

        while (true) {
			final Point centerPoint = queue.poll();

			if (centerPoint == null) {
				break;
			}

			processNextNeighboringPoint(matrix, optics, neighboringLocations, centerPoint);
		}
    }


}