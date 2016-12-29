package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public interface OpticsNeighboursService {
    List<Point> getNeighboringLocations(Set<Pair<Long, Long>> neighboringCells, FixedRadiusMatrix matrix);

    List<Point> getNearestNeighbours(Point currentLocation, List<Point> neighboringLocations, long radius);

    double getCoreDistance(List<Point> nearestLocations, int minPts);

    double getNeighbourReachabilityDistance(Point centerPoint, Point neighbourPoint);
}
