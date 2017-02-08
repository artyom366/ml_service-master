package ml.cluster.service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.error.OpticsException;

public interface ClusterValidatorService {

	double getSilhouetteIndex(Cluster local, Cluster remote, int minPts) throws OpticsException;
}
