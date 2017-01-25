package ml.cluster.datastructure.optics;

import java.util.List;

public interface OpticsSummary {

	List<Cluster> getClusters();

	List<Point> getOutlierPoints();

	boolean clustersExist();

	int getClusterCount();

	boolean outlierPointExist();

	int getOutlierPointCount();
}
