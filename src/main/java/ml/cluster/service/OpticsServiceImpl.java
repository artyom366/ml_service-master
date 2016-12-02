package ml.cluster.service;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;

import ml.cluster.datastructure.FixedRadiusMatrix;
import ml.cluster.datastructure.MatrixCell;
import ml.cluster.datastructure.PickSegment;
import ml.cluster.to.PickLocation;

public class OpticsServiceImpl implements OpticsService {

	@Override
	public void getOptics(final List<PickLocation> pickLocations) {
		Validate.notEmpty(pickLocations, "Pick locations are not defined");

		final Map<String, List<PickLocation>> segmentGroups = groupByLine(pickLocations);
		final Map<PickSegment, List<PickLocation>> pickSegments = defineSegmentBoundaries(segmentGroups);

		generateSegmentMatrix(pickSegments);
		assignPickLocationsToMatrixCells(pickSegments);

	}

	private Map<String, List<PickLocation>> groupByLine(final List<PickLocation> pickLocations) {
		return Collections.unmodifiableMap(pickLocations.stream().collect(Collectors.groupingBy(PickLocation::getLine)));
	}

	private Map<PickSegment, List<PickLocation>> defineSegmentBoundaries(final Map<String, List<PickLocation>> segmentGroups) {
		final Map<PickSegment, List<PickLocation>> pickSegments = new HashMap<>();

		segmentGroups.forEach((line, pickLocations) -> {
			final Stream<PickLocation> stream = pickLocations.stream();
			final DoubleSummaryStatistics xStats = stream.mapToDouble(PickLocation::getX).summaryStatistics();
			final DoubleSummaryStatistics yStats = stream.mapToDouble(PickLocation::getY).summaryStatistics();

			final double minY = yStats.getMin();
			final double maxY = yStats.getMax();
			final double minX = xStats.getMin();
			final double maxX = xStats.getMax();

			pickSegments.put(new PickSegment(line, minY, maxY, minX, maxX), pickLocations);
		});

		return Collections.unmodifiableMap(pickSegments);
	}

	private void generateSegmentMatrix(final Map<PickSegment, List<PickLocation>> pickSegments) {
		pickSegments.forEach((segment, locations) -> {

			final FixedRadiusMatrix matrix = new FixedRadiusMatrix.MatrixBuilder().build();

			final double matrixHeight = segment.getMaxY() - segment.getMinY();
			final double matrixWidth = segment.getMaxX() - segment.getMinX();

			final double cellHeight = matrixHeight / matrix.getCellHeightRatio();
			final double cellWidth = matrixWidth / matrix.getCellWidthRatio();

			generateSegmentMatrixCells(cellHeight, matrixHeight, 0, 0, cellWidth, matrixWidth, 0, 0, matrix, segment);

			// LongStream.range(1, rows + 1).forEach(row -> LongStream.range(1,
			// columns + 1).forEach(column -> {
			// final MatrixCell cell = new MatrixCell(row, column);
			// matrix.addToSegmentPickLocations(cell);
			// }));

			segment.setMatrix(matrix);
		});
	}

	private void generateSegmentMatrixCells(final double cellHeight, final double maxMatrixHeight, final double currentMatrixHeight, final long currentRow,
			final double cellWidth, final double maxMatrixWidth, final double currentMatrixWidth, final long currentColumn, final FixedRadiusMatrix matrix,
			final PickSegment segment) {

		if (currentMatrixHeight < maxMatrixHeight) {

			createMatrixCell(currentRow, currentColumn, cellWidth, cellHeight, matrix, segment);
			generateSegmentMatrixCells(cellHeight, maxMatrixHeight, currentMatrixHeight, 0, cellWidth, maxMatrixWidth, currentMatrixWidth + cellWidth,
									   currentColumn + 1, matrix, segment);

		} else if (currentMatrixWidth < maxMatrixWidth) {

			createMatrixCell(currentRow, currentColumn, cellWidth, cellHeight, matrix, segment);
			generateSegmentMatrixCells(cellHeight, maxMatrixHeight, currentMatrixHeight, 0, cellWidth, maxMatrixWidth, currentMatrixWidth + cellWidth,
									   currentColumn + 1, matrix, segment);
		}
	}

	private void createMatrixCell(final long currentRow, final long currentColumn, final double cellWidth, final double cellHeight,
			final FixedRadiusMatrix matrix, final PickSegment segment) {
		final MatrixCell cell = new MatrixCell(currentRow, currentColumn);
		matrix.addToSegmentPickLocations(cell);

		final double cellMinX = segment.getMinX() + currentColumn * cellWidth;
		final double cellMaxX = cellMinX + cellWidth;
		final double cellMinY = segment.getMinY() + currentRow * cellHeight;
		final double cellMaxY = cellMinY + cellHeight;

		cell.setMinX(cellMinX);
		cell.setMaxX(cellMaxX);
		cell.setMinY(cellMinY);
		cell.setMaxY(cellMaxY);
	}

	private void assignPickLocationsToMatrixCells(final Map<PickSegment, List<PickLocation>> pickSegments) {
		pickSegments.forEach((segment, locations) -> {

			final double segmentMinX = segment.getMinX();
			final double segmentMaxY = segment.getMaxY();

			final FixedRadiusMatrix matrix = segment.getMatrix();
			final double cellHorizontalLen = matrix.getCellWidthRatio();
			final double cellVerticalLen = matrix.getCellHeightRatio();

			segment.getMatrix().getSegmentPickCells().forEach((number, cell) -> {

				final long columnNumber = cell.getColumnNumber();
				final long rowNumber = cell.getRowNumber();

				locations.forEach((location) -> {

					final double x = location.getX();
					final double y = location.getY();



				});

			});

		});
	}

	private boolean isLocationInCell(final PickSegment pickSegment, final PickLocation pickLocation, final long row, final long column) {

		return true;
	}

}
