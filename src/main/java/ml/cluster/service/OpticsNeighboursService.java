package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public interface OpticsNeighboursService {

    double getCoreDistance(OpticsPoint currentLocation, List<OpticsPoint> nearestLocations, int minPts);

    double getNeighbourReachabilityDistance(OpticsPoint centerPoint, OpticsPoint neighbourPoint);

    List<OpticsPoint> getNearestNeighbours(OpticsPoint currentLocationPoint, FixedRadiusMatrix matrix);
}
