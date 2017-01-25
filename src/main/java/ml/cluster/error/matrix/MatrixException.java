package ml.cluster.error.matrix;

import ml.cluster.error.OpticsException;

public class MatrixException extends OpticsException {

	private final static String MESSAGE = "Fixed radius matrix construction failed: %s";

	public MatrixException(final String message) {
		super(String.format(MESSAGE, message));
	}
}
