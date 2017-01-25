package ml.cluster.error.cluster;

public class ClusterDistanceException extends ClusterException {

	private static final String CORE_DISTANCE_MESSAGE = "%s of point is greater than radius: %d";

	public ClusterDistanceException(final String message, final long radius) {
		super(String.format(CORE_DISTANCE_MESSAGE, message, radius));
	}
}
