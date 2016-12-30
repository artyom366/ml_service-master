package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public interface OpticsNeighboursService {

    double getCoreDistance(Point currentLocation, List<Point> nearestLocations, int minPts);

    double getNeighbourReachabilityDistance(Point centerPoint, Point neighbourPoint);

    List<Point> getNearestNeighbours(Point currentLocationPoint, FixedRadiusMatrix matrix);
}
