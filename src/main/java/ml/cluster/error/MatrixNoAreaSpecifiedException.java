package ml.cluster.error;

public class MatrixNoAreaSpecifiedException extends MatrixException {

	private final static String MESSAGE = "Matrix calculated area is zero: width %s, height %s";

	public MatrixNoAreaSpecifiedException(final double width, final double height) {
		super(String.format(MESSAGE, width, height));
	}
}
