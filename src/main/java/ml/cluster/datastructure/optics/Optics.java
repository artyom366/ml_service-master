package ml.cluster.datastructure.optics;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

public final class Optics {

	private final long minPts;
	private final List<Point> orderedPoints;

	private Optics(final OpticsBuilder opticsBuilder) {
		this.minPts = opticsBuilder.minPts;
		this.orderedPoints = opticsBuilder.orderedPoints;
	}

	public long getMinPts() {
		return minPts;
	}

	public List<Point> getOrderedPoints() {
		return orderedPoints;
	}

	public void addToOrderedLocations(final Point point) {
		Validate.notNull(point, "Pick location point is not defined");
		this.orderedPoints.add(point);
	}

	public final static class OpticsBuilder {

		private long minPts = 5;
		private final List<Point> orderedPoints;

		public OpticsBuilder() {
			this.orderedPoints = new LinkedList<>();
		}

		public OpticsBuilder minPts(final long minPts) {
			this.minPts = minPts;
			return this;
		}

		public Optics build() {
			return new Optics(this);
		}
	}
}
