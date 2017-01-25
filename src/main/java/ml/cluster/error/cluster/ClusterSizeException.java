package ml.cluster.error.cluster;

import ml.cluster.error.OpticsException;

public class ClusterSizeException extends OpticsException {

	private static final String MESSAGE = "Not enough points in cluster with respect to minPts, current :%d, required minimum: %d";

	public ClusterSizeException(final int quantity, final int minPts) {
		super(String.format(MESSAGE, quantity, minPts));
	}
}
