package ml.cluster.comparator;

import ml.cluster.datastructure.optics.Point;

import java.util.Comparator;

public class CoreDistanceComparator implements Comparator<Point> {

    @Override
    public int compare(final Point o1, final Point o2) {
        return o1.getCoreDistance() < o2.getCoreDistance() ? -1 : o1.getCoreDistance() == o2.getCoreDistance() ? 0 : 1;
    }
}
