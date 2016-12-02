package ml.cluster.datastructure;

import java.util.Map;
import java.util.TreeMap;

public final class FixedRadiusMatrix {

	private final double radius;
	private final double verticalCoefficient;
	private final double horizontalCoefficient;
	private final long rows;
	private final long columns;
	private final Map<Integer, MatrixCell> segmentPickCells;

	private FixedRadiusMatrix(final MatrixBuilder matrixBuilder) {
		this.radius = matrixBuilder.radius;
		this.verticalCoefficient = matrixBuilder.verticalCoefficient;
		this.horizontalCoefficient = matrixBuilder.horizontalCoefficient;
		this.rows = matrixBuilder.rows;
		this.columns = matrixBuilder.columns;
		this.segmentPickCells = matrixBuilder.segmentPickCells;
	}

	public double getRadius() {
		return radius;
	}

	public double getVerticalCoefficient() {
		return verticalCoefficient;
	}

	public double getHorizontalCoefficient() {
		return horizontalCoefficient;
	}

	public long getRows() {
		return rows;
	}

	public long getColumns() {
		return columns;
	}

	public Map<Integer, MatrixCell> getSegmentPickCells() {
		return segmentPickCells;
	}

	public void addToSegmentPickLocations(final MatrixCell matrixCell) {
		this.segmentPickCells.put(this.segmentPickCells.size(), matrixCell);
	}

	public double getCellHeightRatio() {
		return this.radius * this.verticalCoefficient;
	}

	public double getCellWidthRatio() {
		return this.radius * this.horizontalCoefficient;
	}

	public static class MatrixBuilder {

		private double radius = 5;
		private double verticalCoefficient = 1;
		private double horizontalCoefficient = 1;
		private long rows;
		private long columns;
		private final Map<Integer, MatrixCell> segmentPickCells;

		public MatrixBuilder() {
			this.segmentPickCells = new TreeMap<>();
		}

		public MatrixBuilder radius(final double radius) {
			this.radius = radius;
			return this;
		}

		public MatrixBuilder verticalCoefficient(final double verticalCoefficient) {
			this.verticalCoefficient = verticalCoefficient;
			return this;
		}

		public MatrixBuilder horizontalCoefficient(final double horizontalCoefficient) {
			this.horizontalCoefficient = horizontalCoefficient;
			return this;
		}

		public MatrixBuilder rows(final long rows) {
			this.rows = rows;
			return this;
		}

		public MatrixBuilder columns(final long columns) {
			this.columns = columns;
			return this;
		}

		public FixedRadiusMatrix build() {
			return new FixedRadiusMatrix(this);
		}
	}

	@Override
	public String toString() {
		return "FixedRadiusMatrix {" + "radius=" + radius + ", verticalCoefficient=" + verticalCoefficient + ", horizontalCoefficient=" + horizontalCoefficient
			+ ", rows=" + rows + ", columns=" + columns + ", segmentPickCells=" + segmentPickCells + '}';
	}
}
