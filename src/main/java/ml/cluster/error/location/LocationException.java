package ml.cluster.error.location;

import ml.cluster.error.OpticsException;

public class LocationException extends OpticsException {

    private static final String MESSAGE = "Location data validation failed: %s";

    public LocationException(final String message) {
        super(String.format(MESSAGE, message));
    }
}
