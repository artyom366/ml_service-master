package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Point;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("neighboursService")
public class OpticsNeighboursServiceImpl implements OpticsNeighboursService {

    @Override
    public List<Point> getNeighboringLocationPoints(final Set<Pair<Long, Long>> neighboringCells, final FixedRadiusMatrix matrix) {
        return matrix.getCells().entrySet().stream().filter(map -> neighboringCells.contains(map.getKey())).map(Map.Entry::getValue)
                .collect(Collectors.toList()).stream().map(MatrixCell::getLocations).collect(Collectors.toList()).stream().flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Point> getNearestNeighbours(final Point currentLocationPoint, final List<Point> neighboringLocationPoints, final long radius) {
        final List<Point> nearestNeighbours = new ArrayList<>();

        neighboringLocationPoints.forEach(neighboringLocationPoint -> {

            final double reachabilityDistance = getReachabilityDistance(currentLocationPoint, neighboringLocationPoint, radius);
            if (reachabilityDistance <= radius) {
                nearestNeighbours.add(neighboringLocationPoint);
            }
        });

        return nearestNeighbours;
    }

    @Override
    public double getCoreDistance(final Point currentLocationPoint, final List<Point> nearestLocationPoints, final int minPts) {
        if (nearestLocationPoints.size() < minPts) {
            return Double.NaN;

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

    private double getLocationsDistance(final Point currentLocationPoint, final Point neighboringLocationPoint) {
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
    public double getNeighbourReachabilityDistance(final Point centerLocationPoint, final Point neighbourLocationPoint) {
        return Math.max(centerLocationPoint.getCoreDistance(), getLocationsDistance(centerLocationPoint, neighbourLocationPoint));
    }
}
