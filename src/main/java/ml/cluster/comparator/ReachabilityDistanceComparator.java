package ml.cluster.comparator;

import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;

import java.util.Comparator;

public class ReachabilityDistanceComparator implements Comparator<OpticsPoint> {

    @Override
    public int compare(final OpticsPoint o1, final OpticsPoint o2) {
        return o1.getReachabilityDistance() < o2.getReachabilityDistance() ? -1 : o1.getReachabilityDistance() == o2.getReachabilityDistance() ? 0 : 1;
    }
}
