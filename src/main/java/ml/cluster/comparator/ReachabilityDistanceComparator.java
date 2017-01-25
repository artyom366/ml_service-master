package ml.cluster.comparator;

import ml.cluster.datastructure.optics.Point;

import java.util.Comparator;

public class ReachabilityDistanceComparator implements Comparator<Point> {

    @Override
    public int compare(final Point o1, final Point o2) {
        return o1.getReachabilityDistance() < o2.getReachabilityDistance() ? -1 : o1.getReachabilityDistance() == o2.getReachabilityDistance() ? 0 : 1;
    }
}
