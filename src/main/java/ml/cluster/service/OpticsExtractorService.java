package ml.cluster.service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Point;

import java.util.List;

public interface OpticsExtractorService {
    List<Cluster> extractClusters(List<Point> optics);
}
