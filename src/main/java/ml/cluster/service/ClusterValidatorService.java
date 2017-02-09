package ml.cluster.service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.error.OpticsException;
import ml.cluster.error.cluster.ClusterSizeException;

public interface ClusterValidatorService {

	double getSilhouetteIndex(Cluster local, Cluster remote) throws OpticsException;

	double getDunnIndex(Cluster local, Cluster remote) throws OpticsException;
}
