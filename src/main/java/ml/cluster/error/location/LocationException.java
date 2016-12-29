package ml.cluster.error.location;

public class LocationException extends Exception {

    private static final String MESSAGE = "Location data exception: %s";

    public LocationException(final String message) {
        super(String.format(MESSAGE, message));
    }
}
