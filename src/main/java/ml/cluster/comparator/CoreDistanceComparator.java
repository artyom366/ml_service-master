package ml.cluster.comparator;

import ml.cluster.datastructure.optics.impl.PointImpl;

import java.util.Comparator;

public class CoreDistanceComparator implements Comparator<PointImpl> {

    @Override
    public int compare(final PointImpl o1, final PointImpl o2) {
        return o1.getCoreDistance() < o2.getCoreDistance() ? -1 : o1.getCoreDistance() == o2.getCoreDistance() ? 0 : 1;
    }
}
