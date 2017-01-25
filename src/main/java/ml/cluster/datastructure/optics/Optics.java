package ml.cluster.datastructure.optics;

import java.util.List;

public interface Optics {

	long getRadius();

	int getMinPts();

	List<Point> getOrderedLocationPoints();
}
