package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MatrixServiceImplTest {

	private final static int NUMBER_OF_LOCATIONS = 50;
	private final static int NUMBER_OF_LOCATIONS_IN_GROUP = 10;
	private final static int NUMBER_OF_LOCATIONS_IN_MATRIX = 200;

	@Spy
	private MatrixServiceImpl matrixService;

	@Test
	public void testGetSegmentedLocationPoints() throws Exception {
		final List<OpticsPoint> points = TestLocationPointsGenerator.generateLocationPoints(NUMBER_OF_LOCATIONS);

		final Map<String, List<OpticsPoint>> result = matrixService.groupLocationPointsInSegment(points);
		assertThat("Location point grouping result should not be null", result, is(notNullValue()));
		assertThat("Grouped location lines count should be the equal to total location lines count", result.size(), is(TestLocationPointsGenerator.getLocationLinesQuantity()));

		final int locationPointsCount = result.values().stream().mapToInt(List::size).sum();
		assertThat("Location points count should be equal to total location points count in all the segments", NUMBER_OF_LOCATIONS, is(locationPointsCount));
	}

	@Test
	public void testDefineSegmentBoundaries() throws Exception {
		final Map<String, List<OpticsPoint>> segmentGroups = TestLocationPointsGenerator.generateGroupedLocationPoints(NUMBER_OF_LOCATIONS_IN_GROUP);

		final Map<Segment, List<OpticsPoint>> result = matrixService.defineSegmentBoundaries(segmentGroups);
		assertThat("Segments should not be null", result, is(notNullValue()));
		assertThat("Segment count should not be 0", result.size() > 0, is(true));

		result.forEach((segment, locationPoints) -> {
			final DoubleSummaryStatistics xStats = locationPoints.stream().mapToDouble(OpticsPoint::getX).summaryStatistics();
			final DoubleSummaryStatistics yStats = locationPoints.stream().mapToDouble(OpticsPoint::getY).summaryStatistics();

			final double minY = yStats.getMin();
			final double maxY = yStats.getMax();
			final double minX = xStats.getMin();
			final double maxX = xStats.getMax();

			assertThat("Location points count should be equal to total number of location points", locationPoints.size(), is(NUMBER_OF_LOCATIONS_IN_GROUP));
			assertThat("Segment right border should be equal to location points maximum X axis value", segment.getMaxX(), is(maxX));
			assertThat("Segment left border should be equal to location points minimum X axis value", segment.getMinX(), is(minX));
			assertThat("Segment upper border should be equal to location points maximum Y axis value", segment.getMaxY(), is(maxY));
			assertThat("Segment lower border should be equal to location points minimum Y axis value", segment.getMinY(), is(minY));
			assertThat("Location line number should be equal to location point line number", segment.getLine(), is(locationPoints.get(0).getLine()));
		});
	}

	@Test
	public void testGenerateSegmentMatrix() throws Exception {
		final Map<String, List<OpticsPoint>> segmentGroups = TestLocationPointsGenerator.generateGroupedLocationPoints(NUMBER_OF_LOCATIONS_IN_MATRIX);
		final Map<Segment, List<OpticsPoint>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);

		matrixService.generateSegmentMatrix(pickSegments);
		assertThat("Segments should not be null", pickSegments, is(notNullValue()));
		assertThat("Segment count should not be 0", pickSegments.size() > 0, is(true));

		pickSegments.forEach((segment, locations) -> {
			final FixedRadiusMatrix matrix = segment.getMatrix();

			final double matrixWidth = matrix.getMatrixWidth();
			final double matrixHeight = matrix.getMatrixHeight();

			final long cellWidth = matrix.getCellWidth();
			final long cellHeight = matrix.getCellHeight();

			final long columns = matrix.getColumns();
			final long rows = matrix.getRows();

			assertThat("Total columns width should be greater then matrix width", matrixWidth < cellWidth * columns, is(true));
			assertThat("Total rows height should be greater then matrix height", matrixHeight < cellHeight * rows, is(true));

			final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getCells();

			assertThat("Matrix cells should not be null", cells, is(notNullValue()));
			assertThat("Matrix cells count should be equal to the product of rows and columns", (long)cells.size(), is(rows * columns));

			for (long row = 0, height = 0; row < matrix.getRows(); row++, height += cellHeight) {
				for (long column = 0, width = 0; column < matrix.getColumns(); column++, width += cellWidth) {
					final MatrixCell cell = cells.get(new ImmutablePair<>(row, column));
					assertThat("Cell left border is not correct", cell.getMinX(), is(width));
					assertThat("Cell right border is not correct", cell.getMaxX(), is(width + cellWidth));
					assertThat("Cell lower border is not correct", cell.getMinY(), is(height));
					assertThat("Cell lower upper border is not correct", cell.getMaxY(), is(height + cellHeight));
				}
			}
		});
	}

	@Test
	public void testAssignPickLocationsToMatrixCells() throws Exception {
		final Map<String, List<OpticsPoint>> segmentGroups = TestLocationPointsGenerator.generateGroupedLocationPoints(NUMBER_OF_LOCATIONS_IN_MATRIX);
		final Map<Segment, List<OpticsPoint>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
		matrixService.generateSegmentMatrix(pickSegments);
		matrixService.assignLocationPointsToMatrixCells(pickSegments);

		pickSegments.forEach((segment, locationPoints) -> {
			final Map<Pair<Long, Long>, MatrixCell> cells = segment.getMatrix().getCells();

			cells.forEach((position, cell) -> {
				final List<OpticsPoint> cellLocationPoints = cell.getLocationPoints();

				cellLocationPoints.forEach(cellLocationPoint -> {
					assertThat("Cell location point should be to the right from the left cell border", cellLocationPoint.getX() >= cell.getMinX(), is(true));
					assertThat("Cell location point should be to the left from the right cell border", cellLocationPoint.getX() <= cell.getMaxX(), is(true));
					assertThat("Cell location point should be higher then the lower cell border", cellLocationPoint.getY() >= cell.getMinY(), is(true));
					assertThat("Cell location point should be lower then the upper cell border", cellLocationPoint.getY() <= cell.getMaxY(), is(true));
					assertThat("Cell location point row is not correct", cellLocationPoint.getCell().getLeft(), is(position.getLeft()));
					assertThat("Cell location point column is not correct", cellLocationPoint.getCell().getRight(), is(position.getRight()));
				});
			});

			final int numberOfLocationInMatrix = cells.values().stream().collect(Collectors.toList()).stream().map(MatrixCell::getLocationPoints)
				.flatMap(Collection::stream).collect(Collectors.toList()).size();

			assertThat("Total number of location points in matrix should equal to the total number of location points", numberOfLocationInMatrix, is(NUMBER_OF_LOCATIONS_IN_MATRIX));
		});
	}

	@Test
	public void testAssignNeighboringMatrixCells() throws Exception {
		final int fixedMaxXAxisValue = 15;
		final int fixedMaxYAxisValue = 15;

		final Map<String, List<OpticsPoint>> segmentGroups =
			TestLocationPointsGenerator.generateGroupedLocationPoints(NUMBER_OF_LOCATIONS, fixedMaxXAxisValue, fixedMaxYAxisValue);
		final Map<Segment, List<OpticsPoint>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
		final Set<Segment> segmentsMatrices = matrixService.generateSegmentMatricesAndCells(pickSegments);

		matrixService.generateSegmentMatrix(pickSegments);
		matrixService.assignLocationPointsToMatrixCells(pickSegments);
		matrixService.assignNeighboringMatrixCells(segmentsMatrices);

		final Set<Segment> key = pickSegments.keySet();
		final Segment segment = key.iterator().next();
		final FixedRadiusMatrix matrix = segment.getMatrix();
		final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getCells();

		final MatrixCell cell0 = cells.get(new ImmutablePair<>(0L, 0L));
		final Set<Pair<Long, Long>> neighboringCellsSet0 = cell0.getNeighboringCells();
		if (neighboringCellsSet0.isEmpty()) {
			assertThat("Cell should be empty since it has no neighboring cells", cell0.getLocationPoints().isEmpty(), is(true));

		} else {
			assertThat("Bottom left cell should have the exact number of 3 neighboring cells", neighboringCellsSet0.size() == 3, is(true));

			List<Pair<Long, Long>> neighboringCells0 = new ArrayList<>(neighboringCellsSet0);
			final Pair<Long, Long> cell0Neighbor0 = neighboringCells0.get(0);
			final Pair<Long, Long> cell0Neighbor1 = neighboringCells0.get(1);
			final Pair<Long, Long> cell0Neighbor2 = neighboringCells0.get(2);
			assertThat("Neighboring cell row and column should equal to 1 and 1 accordingly", cell0Neighbor0.getLeft() == 1 && cell0Neighbor0.getRight() == 1, is(true));
			assertThat("Neighboring cell row and column should equal to 0 and 1 accordingly", cell0Neighbor1.getLeft() == 0 && cell0Neighbor1.getRight() == 1, is(true));
			assertThat("Neighboring cell row and column should equal to 1 and 0 accordingly", cell0Neighbor2.getLeft() == 1 && cell0Neighbor2.getRight() == 0, is(true));
		}

		final MatrixCell cell1 = cells.get(new ImmutablePair<>(0L, 1L));
		final Set<Pair<Long, Long>> neighboringCellsSet1 = cell1.getNeighboringCells();
		if (neighboringCellsSet1.isEmpty()) {
			assertThat("Cell should be empty since it has no neighboring cells", cell1.getLocationPoints().isEmpty(), is(true));

		} else {
			assertThat("Bottom middle cell should have the exact number of 5 neighboring cells", neighboringCellsSet1.size() == 5, is(true));

			List<Pair<Long, Long>> neighboringCells1 = new ArrayList<>(neighboringCellsSet1);
			final Pair<Long, Long> cell1Neighbor0 = neighboringCells1.get(0);
			final Pair<Long, Long> cell1Neighbor1 = neighboringCells1.get(1);
			final Pair<Long, Long> cell1Neighbor2 = neighboringCells1.get(2);
			final Pair<Long, Long> cell1Neighbor3 = neighboringCells1.get(3);
			final Pair<Long, Long> cell1Neighbor4 = neighboringCells1.get(4);
			assertThat("Neighboring cell row and column should equal to 0 and 0 accordingly", cell1Neighbor0.getLeft() == 0 && cell1Neighbor0.getRight() == 0, is(true));
			assertThat("Neighboring cell row and column should equal to 1 and 1 accordingly", cell1Neighbor1.getLeft() == 1 && cell1Neighbor1.getRight() == 1, is(true));
			assertThat("Neighboring cell row and column should equal to 1 and 0 accordingly", cell1Neighbor2.getLeft() == 1 && cell1Neighbor2.getRight() == 0, is(true));
			assertThat("Neighboring cell row and column should equal to 0 and 2 accordingly", cell1Neighbor3.getLeft() == 0 && cell1Neighbor3.getRight() == 2, is(true));
			assertThat("Neighboring cell row and column should equal to 1 and 2 accordingly", cell1Neighbor4.getLeft() == 1 && cell1Neighbor4.getRight() == 2, is(true));
		}

		final MatrixCell cell4 = cells.get(new ImmutablePair<>(1L, 1L));
		final Set<Pair<Long, Long>> neighboringCellsSet4 = cell4.getNeighboringCells();
		if (neighboringCellsSet4.isEmpty()) {
			assertThat("Cell should be empty since it has no neighboring cells", cell4.getLocationPoints().isEmpty(), is(true));

		} else {
			assertThat("Center cell should have the exact number of 8 neighboring cells", neighboringCellsSet4.size() == 8, is(true));

			List<Pair<Long, Long>> neighboringCells4 = new ArrayList<>(neighboringCellsSet4);
			final Pair<Long, Long> cell4Neighbor0 = neighboringCells4.get(0);
			final Pair<Long, Long> cell4Neighbor1 = neighboringCells4.get(1);
			final Pair<Long, Long> cell4Neighbor2 = neighboringCells4.get(2);
			final Pair<Long, Long> cell4Neighbor3 = neighboringCells4.get(3);
			final Pair<Long, Long> cell4Neighbor4 = neighboringCells4.get(4);
			final Pair<Long, Long> cell4Neighbor5 = neighboringCells4.get(5);
			final Pair<Long, Long> cell4Neighbor6 = neighboringCells4.get(6);
			final Pair<Long, Long> cell4Neighbor7 = neighboringCells4.get(7);
			assertThat("Neighboring cell row and column should equal to 0 and 0 accordingly", cell4Neighbor0.getLeft() == 0 && cell4Neighbor0.getRight() == 0, is(true));
			assertThat("Neighboring cell row and column should equal to 2 and 2 accordingly", cell4Neighbor1.getLeft() == 2 && cell4Neighbor1.getRight() == 2, is(true));
			assertThat("Neighboring cell row and column should equal to 0 and 1 accordingly", cell4Neighbor2.getLeft() == 0 && cell4Neighbor2.getRight() == 1, is(true));
			assertThat("Neighboring cell row and column should equal to 1 and 0 accordingly", cell4Neighbor3.getLeft() == 1 && cell4Neighbor3.getRight() == 0, is(true));
			assertThat("Neighboring cell row and column should equal to 0 and 2 accordingly", cell4Neighbor4.getLeft() == 0 && cell4Neighbor4.getRight() == 2, is(true));
			assertThat("Neighboring cell row and column should equal to 2 and 0 accordingly", cell4Neighbor5.getLeft() == 2 && cell4Neighbor5.getRight() == 0, is(true));
			assertThat("Neighboring cell row and column should equal to 1 and 2 accordingly", cell4Neighbor6.getLeft() == 1 && cell4Neighbor6.getRight() == 2, is(true));
			assertThat("Neighboring cell row and column should equal to 2 and 1 accordingly", cell4Neighbor7.getLeft() == 2 && cell4Neighbor7.getRight() == 1, is(true));
		}
	}
}