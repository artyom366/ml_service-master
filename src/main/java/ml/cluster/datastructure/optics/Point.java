package ml.cluster.datastructure.optics;

import ml.cluster.to.PickLocationViewDO;

public final class Point {

	private boolean isProcessed;
	private double coreDistance;
	private double reachabilityDistance;
	private final PickLocationViewDO location;

	public Point(final PickLocationViewDO location) {
		this.coreDistance = Double.NaN;
		this.reachabilityDistance = Double.POSITIVE_INFINITY;
		this.isProcessed = false;
		this.location = location;
	}

	public PickLocationViewDO getLocation() {
		return location;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean processed) {
		isProcessed = processed;
	}

	public double getCoreDistance() {
		return coreDistance;
	}

	public void setCoreDistance(double coreDistance) {
		this.coreDistance = coreDistance;
	}

	public double getReachabilityDistance() {
		return reachabilityDistance;
	}

	public void setReachabilityDistance(double reachabilityDistance) {
		this.reachabilityDistance = reachabilityDistance;
	}
}