package ml.cluster.datastructure.optics.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;

public final class OpticsImpl implements Optics {

	private final long radius;
	private final int minPts;
	private final List<Point> orderedLocationPoints;

	private OpticsImpl(final OpticsBuilder opticsBuilder) {
		this.radius = opticsBuilder.radius;
		this.minPts = opticsBuilder.minPts;
		this.orderedLocationPoints = opticsBuilder.orderedLocationPoints;
	}

	@Override
	public long getRadius() {
		return radius;
	}

	@Override
	public int getMinPts() {
		return minPts;
	}

	@Override
	public List<Point> getOrderedLocationPoints() {
		return Collections.unmodifiableList(orderedLocationPoints);
	}

	public void addToOrderedLocationPoints(final Point point) {
		Validate.notNull(point, "Pick location point is not defined");
		this.orderedLocationPoints.add(point);
	}

	public void addAllToOrderedLocationPoints(final List<Point> points) {
		Validate.notEmpty(points, "Pick location points are not defined");
		this.orderedLocationPoints.addAll(points);
	}

	public final static class OpticsBuilder {

		private long radius = 5;
		private int minPts = 5;
		private final List<Point> orderedLocationPoints;

		public OpticsBuilder() {
			this.orderedLocationPoints = new LinkedList<>();
		}

		public OpticsBuilder radius(final long radius) {
			this.radius = radius;
			return this;
		}

		public OpticsBuilder minPts(final int minPts) {
			this.minPts = minPts;
			return this;
		}

		public OpticsImpl build() {
			return new OpticsImpl(this);
		}
	}

	@Override
	public String toString() {
		return "OpticsImpl {" + "radius=" + radius + ", minPts=" + minPts + ", orderedLocationPoints=" + orderedLocationPoints + '}';
	}
}
