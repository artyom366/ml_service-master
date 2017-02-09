package ml.cluster.error.index;

public class DunnIndexException extends ClusterIndexException {

	private static final String MESSAGE = "Dunn index evaluation error: %s";

	public DunnIndexException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
