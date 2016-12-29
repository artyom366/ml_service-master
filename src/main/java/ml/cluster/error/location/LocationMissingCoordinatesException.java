package ml.cluster.error.location;

public class LocationMissingCoordinatesException extends LocationException {

    private static final String MESSAGE = "Pick location coordinates are not defined: %s";

    public LocationMissingCoordinatesException(final String message) {
        super(String.format(MESSAGE, message));
    }
}
