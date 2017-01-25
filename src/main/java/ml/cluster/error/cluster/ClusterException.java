package ml.cluster.error.cluster;

import ml.cluster.error.OpticsException;

public class ClusterException extends OpticsException {

	private static final String MESSAGE = "Cluster extraction failed: %s";

	public ClusterException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
