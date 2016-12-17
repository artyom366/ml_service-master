package ml.cluster.error;

public class CellNeighborsInconsistencyException extends MatrixException {

	private final static String MESSAGE = "Matrix cell neighbors are not consistent";

	public CellNeighborsInconsistencyException() {
		super(MESSAGE);
	}

}
