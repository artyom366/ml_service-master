package ml.cluster.error.matrix;

public class MatrixException extends Exception {

	private final static String MESSAGE = "Fixed radius matrix construction failed: %s";

	public MatrixException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
