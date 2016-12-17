package ml.cluster.error;

public class CellNeighborsInconsistencyException extends MatrixException {

	private final static String MESSAGE = "Matrix cell neighbors are not consistent, quantity of neighboring cells should be %d instead of %d";

	public CellNeighborsInconsistencyException(final int correctQuantity, final int wrongQuantity) {
		super(String.format(MESSAGE, correctQuantity, wrongQuantity));
	}

}
