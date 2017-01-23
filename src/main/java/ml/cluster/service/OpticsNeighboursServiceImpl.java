package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("neighboursService")
public class OpticsNeighboursServiceImpl implements OpticsNeighboursService {

    protected List<OpticsPoint> getNeighboringLocationPoints(final Set<Pair<Long, Long>> neighboringCells, final FixedRadiusMatrix matrix) {
        return Collections.unmodifiableList(matrix.getCells().entrySet().stream().filter(map -> neighboringCells.contains(map.getKey())).map(Map.Entry::getValue)
                .collect(Collectors.toList()).stream().map(MatrixCell::getLocationPoints).collect(Collectors.toList()).stream().flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    protected List<OpticsPoint> getNearestNeighbours(final OpticsPoint currentLocationPoint, final List<OpticsPoint> neighboringLocationPoints, final long radius) {
        final List<OpticsPoint> nearestNeighbours = new ArrayList<>();

        neighboringLocationPoints.forEach(neighboringLocationPoint -> {

            final double reachabilityDistance = getReachabilityDistance(currentLocationPoint, neighboringLocationPoint, radius);
            if (reachabilityDistance <= radius) {
                nearestNeighbours.add(neighboringLocationPoint);
            }
        });

        return Collections.unmodifiableList(nearestNeighbours);
    }

    @Override
    public double getCoreDistance(final OpticsPoint currentLocationPoint, final List<OpticsPoint> nearestLocationPoints, final int minPts) {
        Validate.notNull(currentLocationPoint, "Current location point is not defined");
        Validate.notEmpty(nearestLocationPoints, "Nearest location point are not defined");

        if (nearestLocationPoints.size() < minPts) {
            return Double.POSITIVE_INFINITY;

        } else {
            return getMinPtsReachabilityDistance(currentLocationPoint, nearestLocationPoints, minPts);
        }
    }

    private double getMinPtsReachabilityDistance(final OpticsPoint currentLocationPoint, final List<OpticsPoint> nearestLocationsPoints, final int minPts) {
        final List<Double> distances = new ArrayList<>();

        nearestLocationsPoints.forEach(nearestLocation -> {
            final double reachabilityDistance = getReachabilityDistance(currentLocationPoint, nearestLocation, minPts);
            distances.add(reachabilityDistance);
        });

        distances.sort(Double::compareTo);
        return distances.get(minPts - 1);
    }

    private double getReachabilityDistance(final OpticsPoint currentLocationPoint, final OpticsPoint neighboringLocationPoints, final long radius) {
        final double distance = getLocationsDistance(currentLocationPoint, neighboringLocationPoints);
        if (isDirectlyDensityReachable(distance, radius)) {
            return distance;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    private double getLocationsDistance(final OpticsPoint currentLocationPoint, final OpticsPoint neighboringLocationPoint) {
        final double x = currentLocationPoint.getX();
        final double y = currentLocationPoint.getY();
        final double x1 = neighboringLocationPoint.getX();
        final double y1 = neighboringLocationPoint.getY();
        return Math.hypot(x - x1, y - y1);
    }

    private boolean isDirectlyDensityReachable(final double distance, final long radius) {
        return distance <= radius;
    }

    @Override
    public double getNeighbourReachabilityDistance(final OpticsPoint currentLocationPoint, final OpticsPoint neighbourLocationPoint) {
        Validate.notNull(currentLocationPoint, "Current location point is not defined");
        Validate.notNull(neighbourLocationPoint, "Neighbour location point is not defined");
        return Math.max(currentLocationPoint.getCoreDistance(), getLocationsDistance(currentLocationPoint, neighbourLocationPoint));
    }

    @Override
    public List<OpticsPoint> getNearestNeighbours(final OpticsPoint currentLocationPoint, final FixedRadiusMatrix matrix) {
        Validate.notNull(currentLocationPoint, "Current location point is not defined");
        Validate.notNull(matrix, "Fixed radius matrix is not defined");

        final Pair<Long, Long> currentCellPosition = ((Point)currentLocationPoint).getCell();
        final MatrixCell currentCell = matrix.getCells().get(currentCellPosition);

        final List<OpticsPoint> neighboringLocationPoints = getNeighboringLocationPoints(currentCell.getNeighboringCells(), matrix);
        final List<OpticsPoint> neighbourhood = new ArrayList<>(currentCell.getLocationPoints());
        neighbourhood.addAll(neighboringLocationPoints);

        return getNearestNeighbours(currentLocationPoint, neighbourhood, matrix.getRadius());
    }
}
