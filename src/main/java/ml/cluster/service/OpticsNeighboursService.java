package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.optics.Point;

import java.util.List;

public interface OpticsNeighboursService {

    double getCoreDistance(Point currentLocation, List<Point> nearestLocations, int minPts);

    double getNeighbourReachabilityDistance(Point centerPoint, Point neighbourPoint);

    List<Point> getNearestNeighbours(Point currentLocationPoint, FixedRadiusMatrix matrix);
}
