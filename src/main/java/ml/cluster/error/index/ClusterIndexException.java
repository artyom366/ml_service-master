package ml.cluster.error.index;

import ml.cluster.error.OpticsException;

public class ClusterIndexException extends OpticsException {

	private static final String MESSAGE = "Cluster index evaluation failed: %s";

	public ClusterIndexException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
