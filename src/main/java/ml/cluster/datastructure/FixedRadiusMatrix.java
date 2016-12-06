package ml.cluster.datastructure;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

public final class FixedRadiusMatrix {

	private final double matrixHeight;
	private final double matrixWidth;
	private final double cellHeight;
	private final double cellWidth;
	private final double radius;
	private final double verticalCoefficient;
	private final double horizontalCoefficient;
	private final Map<Pair<Long, Long>, MatrixCell> segmentPickCells;
	private final long rows;
	private final long columns;

	private FixedRadiusMatrix(final MatrixBuilder matrixBuilder) {
		this.radius = matrixBuilder.radius;
		this.verticalCoefficient = matrixBuilder.verticalCoefficient;
		this.horizontalCoefficient = matrixBuilder.horizontalCoefficient;
		this.segmentPickCells = matrixBuilder.segmentPickCells;
		this.matrixHeight = matrixBuilder.matrixHeight;
		this.matrixWidth = matrixBuilder.matrixWidth;
		this.cellHeight = setCellHeight();
		this.cellWidth = setCellWidth();
		this.rows = setRows();
		this.columns = setColumns();
	}

	private double setCellHeight() {
		return this.radius * this.verticalCoefficient;
	}

	private double setCellWidth() {
		return this.radius * this.horizontalCoefficient;
	}

	private long setRows() {
		return this.cellHeight > 0 ? (long)Math.ceil(this.matrixHeight / cellHeight) : 0;
	}

    private long setColumns() {
        return this.cellWidth > 0 ? (long)Math.ceil(this.matrixWidth / cellWidth) : 0;
    }

	public double getMatrixHeight() {
		return matrixHeight;
	}

	public double getMatrixWidth() {
		return matrixWidth;
	}

	public double getCellHeight() {
		return cellHeight;
	}

	public double getCellWidth() {
		return cellWidth;
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

	public Map<Pair<Long, Long>, MatrixCell> getSegmentPickCells() {
		return segmentPickCells;
	}

	public void addToSegmentPickLocations(final Pair<Long, Long> coordinates, final MatrixCell matrixCell) {
		this.segmentPickCells.put(coordinates, matrixCell);
	}

	public final static class MatrixBuilder {

		private double radius = 5;
		private double verticalCoefficient = 1;
		private double horizontalCoefficient = 1;
		private final Map<Pair<Long, Long>, MatrixCell> segmentPickCells;
		private double matrixHeight;
		private double matrixWidth;

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

		public MatrixBuilder height(final double height) {
			this.matrixHeight = height;
			return this;
		}

		public MatrixBuilder width(final double width) {
			this.matrixWidth = width;
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
