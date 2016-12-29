package ml.cluster.datastructure.optics;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

public final class Optics {

	private final int minPts;
	private final List<Point> orderedLocationPoints;

	private Optics(final OpticsBuilder opticsBuilder) {
		this.minPts = opticsBuilder.minPts;
		this.orderedLocationPoints = opticsBuilder.orderedLocationPoints;
	}

	public int getMinPts() {
		return minPts;
	}

	public List<Point> getOrderedLocationPoints() {
		return orderedLocationPoints;
	}

	public void addToOrderedLocationPoints(final Point point) {
		Validate.notNull(point, "Pick location point is not defined");
		this.orderedLocationPoints.add(point);
	}

	public final static class OpticsBuilder {

		private int minPts = 5;
		private final List<Point> orderedLocationPoints;

		public OpticsBuilder() {
			this.orderedLocationPoints = new LinkedList<>();
		}

		public OpticsBuilder minPts(final int minPts) {
			this.minPts = minPts;
			return this;
		}

		public Optics build() {
			return new Optics(this);
		}
	}
}
