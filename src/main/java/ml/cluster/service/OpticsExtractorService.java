package ml.cluster.service;

import java.util.List;

import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.OpticsSummary;
import ml.cluster.datastructure.optics.impl.ClusterImpl;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.OpticsException;

public interface OpticsExtractorService {

	OpticsSummary extractClusters(Optics optics) throws OpticsException;
}
