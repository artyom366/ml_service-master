package ml.cluster.error;

public class OpticsException extends Exception {

	private static final String MESSAGE = "Optics exception: %s";

	public OpticsException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
