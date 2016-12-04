package ml.cluster.error;

public class CellNoAreaSpecifiedException extends Exception {

    private final static String MESSAGE = "Matrix cell calculated area is zero: width %s, height %s";

    public CellNoAreaSpecifiedException(final double width, final double height) {
        super(String.format(MESSAGE, width, height));
    }
}
