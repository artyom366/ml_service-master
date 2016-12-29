package ml.cluster.error.location;

public class LocationMissingDeliveryParametersException extends LocationException {

    private static final String MESSAGE = "Pick location delivery parameters are not defined: %s";

    public LocationMissingDeliveryParametersException(final String message) {
        super(String.format(MESSAGE, message));
    }
}
