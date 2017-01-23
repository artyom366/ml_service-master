package ml.cluster.service;

import java.util.List;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.OpticsPoint;

public interface OpticsExtractorService {

	List<Cluster> extractClusters(List<OpticsPoint> optics);
}
