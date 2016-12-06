package ml.cluster.datastructure;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.TreeMap;

public final class FixedRadiusMatrix {

	private final double matrixHeight;
	private final double matrixWidth;
	private final long cellHeight;
	private final long cellWidth;
	private final long radius;
	private final long verticalCoefficient;
	private final long horizontalCoefficient;
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

	private long setCellHeight() {
		return this.radius * this.verticalCoefficient;
	}

	private long setCellWidth() {
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

	public long getCellHeight() {
		return cellHeight;
	}

	public long getCellWidth() {
		return cellWidth;
	}

	public long getRadius() {
		return radius;
	}

	public long getVerticalCoefficient() {
		return verticalCoefficient;
	}

	public long getHorizontalCoefficient() {
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

		private long radius = 5;
		private long verticalCoefficient = 1;
		private long horizontalCoefficient = 1;
		private final Map<Pair<Long, Long>, MatrixCell> segmentPickCells;
		private double matrixHeight;
		private double matrixWidth;

		public MatrixBuilder() {
			this.segmentPickCells = new TreeMap<>();
		}

		public MatrixBuilder radius(final long radius) {
			this.radius = radius;
			return this;
		}

		public MatrixBuilder verticalCoefficient(final long verticalCoefficient) {
			this.verticalCoefficient = verticalCoefficient;
			return this;
		}

		public MatrixBuilder horizontalCoefficient(final long horizontalCoefficient) {
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
