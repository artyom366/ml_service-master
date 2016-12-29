package ml.cluster.service;

import ml.cluster.comparator.ReachabilityDistanceComparator;
import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;
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
    public List<Point> getNeighboringLocations(final Set<Pair<Long, Long>> neighboringCells, final FixedRadiusMatrix matrix) {
        return matrix.getSegmentPickCells().entrySet().stream().filter(map -> neighboringCells.contains(map.getKey())).map(Map.Entry::getValue)
                .collect(Collectors.toList()).stream().map(MatrixCell::getLocations).collect(Collectors.toList()).stream().flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Point> getNearestNeighbours(final Point currentLocation, final List<Point> neighboringLocations, final long radius) {
        final List<Point> nearestNeighbours = new ArrayList<>();

        neighboringLocations.forEach(neighboringLocation -> {

            final double reachabilityDistance = getReachabilityDistance(currentLocation, neighboringLocation, radius);
            if (reachabilityDistance <= radius) {
                neighboringLocation.setReachabilityDistance(reachabilityDistance);
                nearestNeighbours.add(neighboringLocation);
            }
        });

        return nearestNeighbours;
    }

    private double getReachabilityDistance(final Point currentLocation, final Point neighboringLocation, final long radius) {

        final double distance = getLocationsDistance(currentLocation, neighboringLocation);
        if (isDirectlyDensityReachable(distance, radius)) {
            return distance;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    private double getLocationsDistance(final Point currentLocation, final Point neighboringLocation) {
        final double x = currentLocation.getX();
        final double y = currentLocation.getY();
        final double x1 = neighboringLocation.getX();
        final double y1 = neighboringLocation.getY();
        return Math.hypot(x - x1, y - y1);
    }

    private boolean isDirectlyDensityReachable(final double distance, final long radius) {
        return distance <= radius;
    }

    @Override
    public double getCoreDistance(final List<Point> nearestLocations, final int minPts) {
        if (nearestLocations.size() < minPts) {
            return Double.NaN;

        } else {

            nearestLocations.sort(new ReachabilityDistanceComparator());
            return nearestLocations.get(minPts - 1).getReachabilityDistance();
        }
    }

    @Override
    public double getNeighbourReachabilityDistance(final Point centerPoint, final Point neighbourPoint) {
        return Math.max(centerPoint.getCoreDistance(), getLocationsDistance(centerPoint, neighbourPoint));
    }
}
