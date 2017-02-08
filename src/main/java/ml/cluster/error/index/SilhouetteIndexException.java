package ml.cluster.error.index;

public class SilhouetteIndexException extends ClusterIndexException {

	private static final String MESSAGE = "Silhouette index evaluation error: %s";

	public SilhouetteIndexException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
