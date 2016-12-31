package ml.cluster.error.matrix;

public class CellNoAreaSpecifiedException extends MatrixException {

	private final static String MESSAGE = "Matrix cell calculated area is zero: width %s, height %s";

	public CellNoAreaSpecifiedException(final double width, final double height) {
		super(String.format(MESSAGE, width, height));
	}
}
