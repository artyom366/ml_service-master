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
import ml.cluster.to.PickLocationViewDO;

public class OpticsServiceImpl implements OpticsService {

	@Override
	public void getOptics(final List<PickLocationViewDO> pickLocationViewDOs) {
		Validate.notEmpty(pickLocationViewDOs, "Pick locations are not defined");

		final Map<String, List<PickLocationViewDO>> segmentGroups = groupByLine(pickLocationViewDOs);
		final Map<PickSegment, List<PickLocationViewDO>> pickSegments = defineSegmentBoundaries(segmentGroups);

		generateSegmentMatrix(pickSegments);
		assignPickLocationsToMatrixCells(pickSegments);

	}

	protected Map<String, List<PickLocationViewDO>> groupByLine(final List<PickLocationViewDO> pickLocations) {
		return Collections.unmodifiableMap(pickLocations.stream().collect(Collectors.groupingBy(PickLocationViewDO::getLine)));
	}

	protected Map<PickSegment, List<PickLocationViewDO>> defineSegmentBoundaries(final Map<String, List<PickLocationViewDO>> segmentGroups) {
		final Map<PickSegment, List<PickLocationViewDO>> pickSegments = new HashMap<>();

		segmentGroups.forEach((line, pickLocations) -> {
			final Stream<PickLocationViewDO> stream = pickLocations.stream();
			final DoubleSummaryStatistics xStats = stream.mapToDouble(PickLocationViewDO::getX).summaryStatistics();
			final DoubleSummaryStatistics yStats = stream.mapToDouble(PickLocationViewDO::getY).summaryStatistics();

			final double minY = yStats.getMin();
			final double maxY = yStats.getMax();
			final double minX = xStats.getMin();
			final double maxX = xStats.getMax();

			pickSegments.put(new PickSegment(line, minY, maxY, minX, maxX), pickLocations);
		});

		return Collections.unmodifiableMap(pickSegments);
	}

	protected void generateSegmentMatrix(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
		pickSegments.forEach((segment, locations) -> {

			final FixedRadiusMatrix matrix = new FixedRadiusMatrix.MatrixBuilder().build();

			final double matrixHeight = segment.getMaxY() - segment.getMinY();
			final double matrixWidth = segment.getMaxX() - segment.getMinX();

			final double cellHeight = matrixHeight / matrix.getCellHeightRatio();
			final double cellWidth = matrixWidth / matrix.getCellWidthRatio();


			segment.setMatrix(matrix);
		});
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

	private void assignPickLocationsToMatrixCells(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
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

	private boolean isLocationInCell(final PickSegment pickSegment, final PickLocationViewDO pickLocationViewDO, final long row, final long column) {

		return true;
	}

}
